package org.jboss.ci.tracker.server.ci.jenkins;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import static org.jboss.ci.tracker.server.ci.jenkins.JenkinsDownloader.getEventReader;
import org.jboss.ci.tracker.server.entity.Build;
import org.jboss.ci.tracker.server.entity.Job;
import org.jboss.logging.Logger;

/**
 * This EJB downloads a single job including subsequent builds (asynchronously).
 * @author Hynek Mlnarik <hmlnarik@redhat.com>
 */
@Stateless
public class JobDownloader {

    private static final Logger LOGGER = Logger.getLogger(JobDownloader.class);

    @EJB
    private BuildDownloader buildDownloader;

    public void download(Job job) throws IOException, XMLStreamException {
        for (Build build : findBuilds(job)) {
            buildDownloader.download(job.getId(), build);
        }
    }

    public List<Build> findBuilds(Job job) throws MalformedURLException, IOException, XMLStreamException {
        XMLEventReader eventReader = getEventReader(job.getUrl() + "/api/xml");

        List<Build> builds = new ArrayList<Build>();
        while (eventReader.hasNext()) {
            XMLEvent event = eventReader.nextEvent();

            if (event.isStartElement()) {
                StartElement startElement = event.asStartElement();

                if (startElement.getName().getLocalPart().equals("build")) {
                    Build build = getBuild(event, eventReader);

                    if (!job.getBuilds().contains(build)) {
                        builds.add(build);
                    }
                }

            }
        }

        return builds;
    }

    private static Build getBuild(XMLEvent event, XMLEventReader eventReader) throws XMLStreamException {
        Build build = new Build();

        while (eventReader.hasNext()) {
            event = eventReader.nextEvent();

            if (event.asStartElement().getName().getLocalPart().equals("number")) {
                build.setName(eventReader.getElementText());
            }

            if (event.asStartElement().getName().getLocalPart().equals("url")) {
                build.setUrl(eventReader.getElementText());
            }

            if (build.getName() != null && build.getUrl() != null) {
                break;
            }
        }

        return build;
    }

}
