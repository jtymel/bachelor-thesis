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
import gwtEntity.common.objects.BuildDto;
import gwtEntity.common.objects.JobDto;
import gwtEntity.common.objects.ParameterizedBuildDto;
import gwtEntity.common.objects.PossibleResultDto;
import gwtEntity.common.objects.ResultDto;
import gwtEntity.common.objects.TestDto;
import java.util.List;

/**
 *
 * @author jtymel
 */
@RemoteServiceRelativePath("resultService")
public interface ResultService extends RemoteService {

    public List<PossibleResultDto> getPossibleResults();

    public List<ResultDto> getResults(ParameterizedBuildDto paramBuildDto, Long possibleResultId, Long categoryId);

    public List<ResultDto> getResults(BuildDto buildDto, Long possibleResultId, Long categoryId);

    public List<ResultDto> getResults(JobDto jobDto, Long possibleResultId, Long categoryId);

    public List<TestDto> getTestResults(ResultDto resultDto, ParameterizedBuildDto paramBuildDto, Long resultId, Long categoryId);

    public List<TestDto> getTestResults(ResultDto resultDto, BuildDto buildDto, Long resultId, Long categoryId);

    public List<TestDto> getTestResults(ResultDto resultDto, JobDto jobDto, Long resultId, Long categoryId);
}
