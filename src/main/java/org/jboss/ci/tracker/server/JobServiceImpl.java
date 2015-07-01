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
package org.jboss.ci.tracker.server;

import org.jboss.ci.tracker.common.services.JobService;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.jboss.ci.tracker.common.objects.CategoryDto;
import org.jboss.ci.tracker.common.objects.JobDto;
import java.util.List;
import javax.ejb.EJB;

/**
 *
 * @author jtymel
 */
public class JobServiceImpl extends RemoteServiceServlet implements JobService {

    @EJB
    private JobServiceBean jobServiceBean;

    @Override
    public List<JobDto> getJobs() {
        return jobServiceBean.getJobs();
    }

    @Override
    public Integer saveJob(JobDto jobDTO) {
        return jobServiceBean.saveJob(jobDTO);
    }

    @Override
    public void deleteJob(JobDto jobDTO) {
        jobServiceBean.deleteJob(jobDTO);
    }

    @Override
    public void addCategoriesToLabel(JobDto job, List<CategoryDto> categories) {
        jobServiceBean.addCategoriesToLabel(job, categories);
    }

    @Override
    public void addCategoriesToParamBuild(JobDto job) {
        jobServiceBean.addCategoriesToParamBuild(job);
    }
}
