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
import org.jboss.ci.tracker.common.objects.BuildDto;
import org.jboss.ci.tracker.common.objects.ParameterizedBuildDto;
import java.util.List;

/**
 *
 * @author jtymel
 */
@RemoteServiceRelativePath("parambuildservice")
public interface ParameterizedBuildService extends RemoteService {

    /**
     * Returns all parameterized builds of specified build
     *
     * @param build Build that determines which parameterized builds will be gotten
     * @return List of parameterized builds
     */
    public List<ParameterizedBuildDto> getParamBuilds(BuildDto build);
}
