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
package gwtEntity.server;

import gwtEntity.server.entity.Job;
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
