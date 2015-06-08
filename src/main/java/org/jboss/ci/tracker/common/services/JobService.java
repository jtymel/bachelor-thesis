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
package org.jboss.ci.tracker.common.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import org.jboss.ci.tracker.common.objects.CategoryDto;
import org.jboss.ci.tracker.common.objects.JobDto;
import java.util.List;

/**
 *
 * @author jtymel
 */
@RemoteServiceRelativePath("jobservice")
public interface JobService extends RemoteService {

    /**
     * Returns all Jobs
     *
     * @return List of jobs
     */
    public List<JobDto> getJobs();

    /**
     * Saves (or updates) specified job
     *
     * @param jobDTO Job that is going to be saved
     * @return Id of saved job
     */
    public Long saveJob(JobDto jobDTO);

    /**
     * Deletes specified job
     *
     * @param jobDTO Job that is going to be deleted
     */
    public void deleteJob(JobDto jobDTO);

    /**
     * Sets categories to specified job
     *
     * @param job Job
     * @param categories Categories (= attributes) of specified job
     */
    public void addCategoriesToLabel(JobDto job, List<CategoryDto> categories);

    /**
     * Sets the job's categories (= attributes) to all its parameterized builds
     *
     * @param job Job
     */
    public void addCategoriesToParamBuild(JobDto job);
}
