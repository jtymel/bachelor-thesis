/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2015, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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

    public List<TestDto> getTestResults(ResultDto resultDto, ParameterizedBuildDto paramBuildDto);

    public List<TestDto> getTestResults(ResultDto resultDto, BuildDto buildDto);

    public List<TestDto> getTestResults(ResultDto resultDto, JobDto jobDto);
}
