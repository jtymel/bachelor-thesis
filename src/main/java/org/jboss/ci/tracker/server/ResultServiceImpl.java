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
import java.util.Collection;
import org.jboss.ci.tracker.common.objects.BuildDto;
import org.jboss.ci.tracker.common.objects.JobDto;
import org.jboss.ci.tracker.common.objects.ParameterizedBuildDto;
import org.jboss.ci.tracker.common.objects.PossibleResultDto;
import org.jboss.ci.tracker.common.objects.ResultDto;
import org.jboss.ci.tracker.common.services.ResultService;
import org.jboss.ci.tracker.common.objects.TestDto;
import java.util.List;
import javax.ejb.EJB;
import org.jboss.ci.tracker.common.objects.FilterDto;

/**
 *
 * @author jtymel
 */
public class ResultServiceImpl extends RemoteServiceServlet implements ResultService {

    @EJB
    ResultServiceBean resultServiceBean;

    @Override
    public List<PossibleResultDto> getPossibleResults() {
        return resultServiceBean.getPossibleResults();
    }

    @Override
    public List<ResultDto> getResults(ParameterizedBuildDto paramBuildDto, FilterDto filter) {
        return resultServiceBean.getResults(paramBuildDto, filter);
    }

    @Override
    public List<ResultDto> getResults(Collection<BuildDto> builds, FilterDto filter) {
        return resultServiceBean.getResults(builds, filter);
    }

    @Override
    public List<ResultDto> getResults(JobDto jobDto, FilterDto filter) {
        return resultServiceBean.getResults(jobDto, filter);
    }

    @Override
    public List<TestDto> getTestResults(ResultDto resultDto, ParameterizedBuildDto paramBuildDto, Long resultId, Long categoryId) {
        return resultServiceBean.getTestResults(resultDto, paramBuildDto, resultId, categoryId);
    }

    @Override
    public List<TestDto> getTestResults(ResultDto resultDto, BuildDto buildDto, Long resultId, Long categoryId) {
        return resultServiceBean.getTestResults(resultDto, buildDto, resultId, categoryId);
    }

    @Override
    public List<TestDto> getTestResults(ResultDto resultDto, JobDto jobDto, Long resultId, Long categoryId) {
        return resultServiceBean.getTestResults(resultDto, jobDto, resultId, categoryId);
    }

}
