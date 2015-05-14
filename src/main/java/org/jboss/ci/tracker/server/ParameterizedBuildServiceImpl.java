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

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.jboss.ci.tracker.common.objects.BuildDto;
import org.jboss.ci.tracker.common.objects.ParameterizedBuildDto;
import org.jboss.ci.tracker.common.services.ParameterizedBuildService;
import java.util.List;
import javax.ejb.EJB;

/**
 *
 * @author jtymel
 */
public class ParameterizedBuildServiceImpl extends RemoteServiceServlet implements ParameterizedBuildService {

    @EJB
    ParameterizedBuildServiceBean paramBuildServiceBean;

    @Override
    public List<ParameterizedBuildDto> getParamBuilds(BuildDto build) {
        return paramBuildServiceBean.getParamBuilds(build);
    }
}
