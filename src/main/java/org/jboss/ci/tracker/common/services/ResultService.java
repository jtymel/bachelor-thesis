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
import org.jboss.ci.tracker.common.objects.JobDto;
import org.jboss.ci.tracker.common.objects.ParameterizedBuildDto;
import org.jboss.ci.tracker.common.objects.PossibleResultDto;
import org.jboss.ci.tracker.common.objects.ResultDto;
import org.jboss.ci.tracker.common.objects.TestDto;
import java.util.List;
import org.jboss.ci.tracker.common.objects.FilterDto;

/**
 *
 * @author jtymel
 */
@RemoteServiceRelativePath("resultService")
public interface ResultService extends RemoteService {

    /**
     * Returns all possible (= reached) results
     *
     * @return List of possible results
     */
    public List<PossibleResultDto> getPossibleResults();

    /**
     * Returns results of tests in specified parameterized build
     *
     * @param paramBuildDto Parameterized build
     * @param filter Filter that determines which results will be gotten
     * @return List of results
     */
    public List<ResultDto> getResults(ParameterizedBuildDto paramBuildDto, FilterDto filter);

    /**
     * Returns results of tests in specified build
     *
     * @param buildDto Build
     * @param filter Filter that determines which results will be gotten
     * @return List of results
     */
    public List<ResultDto> getResults(BuildDto buildDto, FilterDto filter);

    /**
     * Returns results of tests in specified job
     *
     * @param jobDto Job
     * @param filter Filter that determines which results will be gotten
     * @return List of results
     */
    public List<ResultDto> getResults(JobDto jobDto, FilterDto filter);

    /**
     * Returns history of results of specified test in the whole job. This method is called when the results of specified
     * parameterized build are shown
     *
     * @param resultDto Determines the test which history will be gotten
     * @param paramBuildDto Parameterized build
     * @param resultId Id of result
     * @param categoryId Id of category
     * @return List of results of particular test
     */
    public List<TestDto> getTestResults(ResultDto resultDto, ParameterizedBuildDto paramBuildDto, Long resultId, Long categoryId); //TODO: Use instance of FilterDto instead of resultId and categoryId

    /**
     * Returns history of results of specified test in the whole job. This method is called when the results of specified build
     * are shown
     *
     * @param resultDto Determines the test which history will be gotten
     * @param buildDto Build
     * @param resultId Id of result
     * @param categoryId Id of category
     * @return List of results of particular test
     */
    public List<TestDto> getTestResults(ResultDto resultDto, BuildDto buildDto, Long resultId, Long categoryId); //TODO: Use instance of FilterDto instead of resultId and categoryId

    /**
     * Returns history of results of specified test in the whole job. This method is called when the results of specified job
     * are shown
     *
     * @param resultDto Determines the test which history will be gotten
     * @param jobDto job
     * @param resultId Id of result
     * @param categoryId Id of category
     * @return List of results of particular test
     */
    public List<TestDto> getTestResults(ResultDto resultDto, JobDto jobDto, Long resultId, Long categoryId); //TODO: Use instance of FilterDto instead of resultId and categoryId
}
