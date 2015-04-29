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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        List<PossibleResult> possibleResults = getPlainPossibleResults();
        List<PossibleResultDto> possibleResultDtos = new ArrayList<PossibleResultDto>(possibleResults.size());

        for (PossibleResult possibleResult : possibleResults) {
            possibleResultDtos.add(createPossibleResultDto(possibleResult));
        }

        return possibleResultDtos;
    }

    private List<PossibleResult> getPlainPossibleResults() {
        Session session = (Session) em.getDelegate();
        Query query = session.createQuery("FROM PossibleResult");
        return new ArrayList<PossibleResult>(query.list());
    }

    private PossibleResultDto createPossibleResultDto(PossibleResult possibleResult) {
        PossibleResultDto possibleResultDto = new PossibleResultDto();
        possibleResultDto.setId(possibleResult.getId());
        possibleResultDto.setName(possibleResult.getName());
        return possibleResultDto;
    }

    public List<ResultDto> getResults(ParameterizedBuildDto paramBuildDto) {
        if (paramBuildDto == null) {
            return null;
        }

        List<PossibleResult> possibleResults = getPlainPossibleResults();

        String queryString = "WITH ";

        if (possibleResults != null && !possibleResults.isEmpty()) {
            for (PossibleResult possibleResult : possibleResults) {
                queryString += "t_" + possibleResult.getId() + " AS (SELECT test_id, parameterizedbuild_id FROM Result WHERE possibleresult_id =" + possibleResult.getId() + "),";
            }
            queryString = queryString.substring(0, queryString.length() - 1);
        }

        queryString += " SELECT t.name as testName, tc.name as testCaseName, ";
        if (possibleResults != null && !possibleResults.isEmpty()) {
            for (PossibleResult possibleResult : possibleResults) {
                queryString += " COUNT(t_" + possibleResult.getId() + ".test_id) AS res" + possibleResult.getId() + ",";
            }
            queryString = queryString.substring(0, queryString.length() - 1);
        }

        queryString += " FROM Result r ";
        if (possibleResults != null && !possibleResults.isEmpty()) {
            for (PossibleResult possibleResult : possibleResults) {
                queryString += " LEFT JOIN t_" + possibleResult.getId() + " ON t_" + possibleResult.getId() + ".test_id = r.test_id "
                        + " AND t_" + possibleResult.getId() + ".parameterizedbuild_id = r.parameterizedbuild_id";
            }
            queryString += ", ";
        }

        queryString += "PossibleResult pr, Test t, TestCase tc, ParameterizedBuild pb"
                + " WHERE"
                + "	r.possibleresult_id = pr.id"
                + "	AND r.test_id = t.id"
                + "	AND r.parameterizedbuild_id = pb.id"
                + "	AND t.testcase_id = tc.id"
                + "	AND r.parameterizedbuild_id = " + paramBuildDto.getId()
                + " GROUP BY t.name, tc.name";

        System.out.println("## ### ####### ###### " + queryString);

        Session session = (Session) em.getDelegate();
        Query query = session.createSQLQuery(queryString);

        List<Object[]> testResults = query.list();
        List<ResultDto> resultDtos = new ArrayList<ResultDto>(testResults.size());
        for (Object[] testResult : testResults) {
            resultDtos.add(createResultDto(testResult, possibleResults));

        }

        return resultDtos;
    }

    private ResultDto createResultDto(Object[] result, List<PossibleResult> possibleResults) {
        ResultDto resultDto = new ResultDto();
        resultDto.setTest(result[0].toString());
        resultDto.setTestCase(result[1].toString());

        Map<Long, Integer> testResults = new HashMap<Long, Integer>();

        int i = 2;
        for (PossibleResult possibleResult : possibleResults) {
            testResults.put(possibleResult.getId(), Integer.parseInt(result[i].toString()));
            i++;
        }

        resultDto.setResults(testResults);
        return resultDto;
    }
}
