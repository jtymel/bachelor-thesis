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
package gwtEntity.common.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import gwtEntity.common.objects.CategoryDto;
import gwtEntity.common.objects.JobDto;
import java.util.List;

/**
 *
 * @author jtymel
 */
@RemoteServiceRelativePath("jobservice")
public interface JobService extends RemoteService {

    public List<JobDto> getJobs();

    public Long saveJob(JobDto jobDTO);

    public void deleteJob(JobDto jobDTO);

    public void addCategoriesToLabel(JobDto job, List<CategoryDto> categories);

    public void addCategoriesToParamBuild(JobDto job);
}
