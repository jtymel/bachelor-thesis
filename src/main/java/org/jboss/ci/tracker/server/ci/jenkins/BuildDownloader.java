/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jboss.ci.tracker.server.ci.jenkins;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import org.hibernate.Session;
import static org.jboss.ci.tracker.server.ci.jenkins.JenkinsDownloader.getEventReader;
import org.jboss.ci.tracker.server.entity.Build;
import org.jboss.ci.tracker.server.entity.Job;
import org.jboss.ci.tracker.server.entity.ParameterizedBuild;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.jboss.logging.Logger;

/**
 * This task downloads a single build including subsequent parameterized builds.
 * @author Hynek Mlnarik <hmlnarik@redhat.com>
 */
@Stateless
@Transactional(Transactional.TxType.REQUIRES_NEW)
@TransactionTimeout(value = 60, unit = TimeUnit.MINUTES)
public class BuildDownloader {

    private static final Logger LOGGER = Logger.getLogger(BuildDownloader.class);

    @EJB
    private ParamBuildDownloader paramBuildDownloader;

    @PersistenceContext(name = "MainPU")
    private EntityManager em;

    @Asynchronous
    public void download(Integer jobId, Build build) throws IOException, XMLStreamException {
        LOGGER.debugv("Downloading build {0} with URL {1}", build.getName(), build.getUrl());

        Session session = (Session) em.getDelegate();

        build.setJob(em.find(Job.class, jobId));
        session.saveOrUpdate(build);
        session.flush();

        for (ParameterizedBuild paramBuild : findParameterizedBuilds(build)) {
            paramBuildDownloader.download(paramBuild);
        }

        LOGGER.debugv("COMPLETED: Downloading build {0} with URL {1}", build.getName(), build.getUrl());
    }

    public List<ParameterizedBuild> findParameterizedBuilds(Build build) throws IOException, XMLStreamException {
        List<ParameterizedBuild> paramBuilds = new ArrayList<ParameterizedBuild>();
        XMLEventReader eventReader = getEventReader(build.getUrl() + "api/xml");

        while (eventReader.hasNext()) {
            XMLEvent event = eventReader.nextEvent();

            if (event.isStartElement()) {
                StartElement startElement = event.asStartElement();

                if (startElement.getName().getLocalPart().equals("run")) {
                    ParameterizedBuild paramBuild = getParamBuild(event, eventReader, build);

                    if (paramBuild != null) {
                        final String labelName = getLabelName(paramBuild);

                        getMachineAndDateTimeOfParamBuild(paramBuild);
                        paramBuild.setBuild(build);
                        paramBuild.setLabel(labelName);
                        paramBuilds.add(paramBuild);
                    }
                }
            }
        }

        Session session = (Session) em.getDelegate();
        session.flush();

        return paramBuilds;
    }

    private static ParameterizedBuild getParamBuild(XMLEvent event, XMLEventReader eventReader, Build build) throws XMLStreamException {
        ParameterizedBuild paramBuild = new ParameterizedBuild();

        while (eventReader.hasNext()) {
            event = eventReader.nextEvent();

            if (event.isStartElement()) {
                StartElement startElement = event.asStartElement();

                if (startElement.getName().getLocalPart().equals("number")) {
                    String paramBuildNumber = eventReader.getElementText();

                    if (! paramBuildNumber.equals(build.getName())) {
                        return null;
                    }

                    paramBuild.setName(paramBuildNumber);
                }

                if (startElement.getName().getLocalPart().equals("url")) {
                    paramBuild.setUrl(eventReader.getElementText());
                }
            } else if (event.isEndElement()) {
                EndElement endElement = event.asEndElement();

                if (endElement.getName().getLocalPart().equals("run")) {
                    break;
                }
            }
        }

        LOGGER.debugv("Found parameterized build {0}", paramBuild);

        return paramBuild;
    }

    public ParameterizedBuild getMachineAndDateTimeOfParamBuild(ParameterizedBuild paramBuild) {
        try {
            XMLEventReader eventReader = getEventReader(paramBuild.getUrl() + "api/xml");

            while (eventReader.hasNext()) {
                XMLEvent event = eventReader.nextEvent();

                if (event.isStartElement()) {
                    StartElement startElement = event.asStartElement();

                    if (startElement.getName().getLocalPart().equals("timestamp")) {
                        paramBuild.setDatetime(new Date(Long.parseLong(eventReader.getElementText())));
                    }

                    if (startElement.getName().getLocalPart().equals("builtOn")) {
                        paramBuild.setMachine(eventReader.getElementText());
                    }
                }
            }

        } catch (IOException | XMLStreamException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }

        return paramBuild;
    }

    private String getLabelName(ParameterizedBuild paramBuild) {
        String labelName = paramBuild.getUrl().substring(0, paramBuild.getUrl().lastIndexOf("/"));
        labelName = labelName.substring(0, labelName.lastIndexOf("/"));
        labelName = labelName.substring(labelName.lastIndexOf("/") + 1, labelName.length());
        try {
            return java.net.URLDecoder.decode(labelName, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            return labelName;
        }
    }
}
