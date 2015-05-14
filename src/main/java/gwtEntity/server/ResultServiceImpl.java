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

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import gwtEntity.common.objects.BuildDto;
import gwtEntity.common.objects.JobDto;
import gwtEntity.common.objects.ParameterizedBuildDto;
import gwtEntity.common.objects.PossibleResultDto;
import gwtEntity.common.objects.ResultDto;
import gwtEntity.common.services.ResultService;
import gwtEntity.common.objects.TestDto;
import java.util.List;
import javax.ejb.EJB;

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
    public List<ResultDto> getResults(ParameterizedBuildDto paramBuildDto, Long possibleResultId, Long categoryId) {
        return resultServiceBean.getResults(paramBuildDto, possibleResultId, categoryId);
    }

    @Override
    public List<ResultDto> getResults(BuildDto buildDto, Long possibleResultId, Long categoryId) {
        return resultServiceBean.getResults(buildDto, possibleResultId, categoryId);
    }

    @Override
    public List<ResultDto> getResults(JobDto jobDto, Long possibleResultId, Long categoryId) {
        return resultServiceBean.getResults(jobDto, possibleResultId, categoryId);
    }

    @Override
    public List<TestDto> getTestResults(ResultDto resultDto, ParameterizedBuildDto paramBuildDto) {
        return resultServiceBean.getTestResults(resultDto, paramBuildDto);
    }

    @Override
    public List<TestDto> getTestResults(ResultDto resultDto, BuildDto buildDto) {
        return resultServiceBean.getTestResults(resultDto, buildDto);
    }

    @Override
    public List<TestDto> getTestResults(ResultDto resultDto, JobDto jobDto) {
        return resultServiceBean.getTestResults(resultDto, jobDto);
    }

}
