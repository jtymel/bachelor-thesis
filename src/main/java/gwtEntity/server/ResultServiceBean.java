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
package gwtEntity.server;

import gwtEntity.client.ParameterizedBuildDto;
import gwtEntity.client.PossibleResultDto;
import gwtEntity.client.ResultDto;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 *
 * @author jtymel
 */
@Stateless
@Transactional(Transactional.TxType.REQUIRED)
public class ResultServiceBean {

    @PersistenceContext(name = "MainPU")
    private EntityManager em;

    public List<PossibleResultDto> getPossibleResults() {
        Session session = (Session) em.getDelegate();
        Query query = session.createQuery("FROM PossibleResult");

        List<PossibleResult> possibleResults = new ArrayList<PossibleResult>(query.list());
        List<PossibleResultDto> possibleResultDtos = new ArrayList<PossibleResultDto>(possibleResults.size());

        for (PossibleResult possibleResult : possibleResults) {
            possibleResultDtos.add(createPossibleResultDto(possibleResult));
        }

        return possibleResultDtos;
    }

    private PossibleResultDto createPossibleResultDto(PossibleResult possibleResult) {
        PossibleResultDto possibleResultDto = new PossibleResultDto();
        possibleResultDto.setId(possibleResult.getId());
        possibleResultDto.setName(possibleResult.getName());
        return possibleResultDto;
    }

    public List<ResultDto> getResults(ParameterizedBuildDto paramBuildDto) {
        if(paramBuildDto == null)
            return null;
        
        Session session = (Session) em.getDelegate();
        Query query = session.createQuery("FROM Result WHERE id_parameterizedBuild_id = :paramBuildId")
                .setParameter("paramBuildId", paramBuildDto.getId());

        List<Result> results = new ArrayList<Result>(query.list());
        List<ResultDto> resultDtos = new ArrayList<ResultDto>(results.size());
        
        for (Result result : results) {
            resultDtos.add(createResultDto(result));
        }
        
        return resultDtos;
    }
    
    private ResultDto createResultDto(Result result) {
        ResultDto resultDto = new ResultDto();
        resultDto.setTest(result.getId_test().getName());
        resultDto.setTestCase(result.getId_test().getId_testCase().getName());
        resultDto.addResult(result.getId_possibleResult().getName(), 1);
        
        return resultDto;
    }
}
