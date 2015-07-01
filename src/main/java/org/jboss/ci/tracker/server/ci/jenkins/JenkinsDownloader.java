/*
 * Copyright (C) 2015 Jan Tymel
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.jboss.ci.tracker.server.ci.jenkins;

import org.jboss.ci.tracker.server.entity.Job;
import org.jboss.ci.tracker.common.objects.JobDto;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import org.jboss.ci.tracker.server.JobServiceBean;
import org.jboss.logging.Logger;

/**
 *
 * @author jtymel
 */
@Stateless
public class JenkinsDownloader {

    private static final Logger LOGGER = Logger.getLogger(JenkinsDownloader.class);

    @EJB
    private JobServiceBean jobServiceBean;

    @EJB
    private JobDownloader jobDownloader;

    public void downloadBuilds(final List<JobDto> jobs) {

        for (JobDto jobDto : jobs) {
            Job job = jobServiceBean.getPlainJob(jobDto);
            downloadBuilds(job);
        }
    }

    public void downloadBuilds(final Job job) {
        try {
            jobDownloader.download(job);
        } catch (IOException | XMLStreamException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    public static XMLEventReader getEventReader(String link) throws MalformedURLException, IOException, XMLStreamException {
        URL aux = new URL(link);
        URL url = new URL("http", aux.getHost(), aux.getPath());

        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
        XMLInputFactory inputFactory = XMLInputFactory.newFactory();
        return inputFactory.createXMLEventReader(in);
    }

}
