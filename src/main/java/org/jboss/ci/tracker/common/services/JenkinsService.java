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
import org.jboss.ci.tracker.common.objects.JobDto;
import java.util.List;

/**
 *
 * @author jtymel
 */
@RemoteServiceRelativePath("jenkinsservice")
public interface JenkinsService extends RemoteService {

    /**
     * Downloads results and another pieces of information about specified jobs. This method saves all the information about
     * test results, i.e. builds, parameterized builds (also with their labels), tests, test cases, possible results and
     * results. It also sets categories of parameterized builds with known labels (= that have set categories).
     *
     * @param jobs Jobs which results are going to be downloaded
     */
    public void downloadBuilds(List<JobDto> jobs);
}
