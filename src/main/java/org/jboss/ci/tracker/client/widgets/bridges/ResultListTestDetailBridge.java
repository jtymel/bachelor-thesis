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
package org.jboss.ci.tracker.client.widgets.bridges;

import java.util.Collection;
import org.jboss.ci.tracker.common.objects.BuildDto;
import org.jboss.ci.tracker.common.objects.JobDto;
import org.jboss.ci.tracker.common.objects.ParameterizedBuildDto;
import org.jboss.ci.tracker.common.objects.PossibleResultDto;
import org.jboss.ci.tracker.common.objects.ResultDto;
import java.util.List;

/**
 *
 * @author jtymel
 */
public interface ResultListTestDetailBridge {

    public void setTestAndDisplayHistory(ResultDto result, JobDto job, List<PossibleResultDto> possibleResults);

    public void setTestAndDisplayHistory(ResultDto result, Collection<BuildDto> build, List<PossibleResultDto> possibleResults);

    public void setTestAndDisplayHistory(ResultDto result, ParameterizedBuildDto paramBuild, List<PossibleResultDto> possibleResults);

    public void cancelTestDetailAndDisplayResultList();

}
