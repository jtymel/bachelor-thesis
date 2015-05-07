/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2015, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package gwtEntity.server;

import java.util.List;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timer;

/**
 *
 * @author jtymel
 */
@Singleton
@Startup
public class TimerServiceBean {

    private static final Logger LOGGER = Logger.getLogger("gwtEntity");

    @EJB
    private JobServiceBean jobServiceBean;

    @EJB
    private JenkinsDownloader jenkinsDownloader;

    public TimerServiceBean() {

    }

    @Schedule(dayOfWeek = "*")
    public void downloadResults(Timer timer) {
        List<Job> jobs = jobServiceBean.getPlainJobs();

        LOGGER.info("System started automatized download of results");

        for (Job job : jobs) {
            jenkinsDownloader.downloadBuilds(job);
        }

        LOGGER.info("System finished automatized download of results");
    }

}
