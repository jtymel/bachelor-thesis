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

import gwtEntity.client.BuildDto;
import gwtEntity.client.JobDto;
import gwtEntity.client.ParameterizedBuildDto;
import gwtEntity.client.PossibleResultDto;
import gwtEntity.client.ResultDto;
import gwtEntity.client.TestDto;
import java.util.Date;
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
        Query query = session.createQuery("FROM PossibleResult ORDER BY name");
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

        String queryString = "SELECT t.name AS testName, tc.name AS testCaseName, r.possibleresult_id, COUNT(*) AS res"
                + " FROM Result r, PossibleResult pr, Test t, TestCase tc"
                + " WHERE"
                + "	r.possibleresult_id = pr.id"
                + "	AND r.test_id = t.id"
                + "	AND t.testcase_id = tc.id"
                + "	AND r.parameterizedbuild_id = :paramBuildId"
                + " GROUP BY t.name, tc.name, r.possibleresult_id";

        Session session = (Session) em.getDelegate();
        Query query = session.createSQLQuery(queryString).setParameter("paramBuildId", paramBuildDto.getId());

        List<Object[]> testResults = query.list();
        List<ResultDto> resultDtos = new ArrayList<ResultDto>(testResults.size());

        addOrEditResults(testResults, resultDtos);

        return resultDtos;
    }

    public List<ResultDto> getResults(BuildDto buildDto) {
        if (buildDto == null) {
            return null;
        }

        String queryString = "SELECT t.name AS testName, tc.name AS testCaseName, r.possibleresult_id, COUNT(*) AS res"
                + " FROM Result r, PossibleResult pr, Test t, TestCase tc, ParameterizedBuild pb"
                + " WHERE"
                + "	r.possibleresult_id = pr.id"
                + "	AND r.test_id = t.id"
                + "	AND t.testcase_id = tc.id"
                + "	AND r.parameterizedbuild_id = pb.id"
                + "     AND pb.build_id = :buildId"
                + " GROUP BY t.name, tc.name, r.possibleresult_id";

        Session session = (Session) em.getDelegate();
        Query query = session.createSQLQuery(queryString).setParameter("buildId", buildDto.getId());

        List<Object[]> testResults = query.list();
        List<ResultDto> resultDtos = new ArrayList<ResultDto>(testResults.size());

        addOrEditResults(testResults, resultDtos);

        return resultDtos;
    }

    public List<ResultDto> getResults(JobDto jobDto) {
        if (jobDto == null) {
            return null;
        }

        String queryString = "SELECT t.name AS testName, tc.name AS testCaseName, r.possibleresult_id, COUNT(*) AS res"
                + " FROM Result r, PossibleResult pr, Test t, TestCase tc, ParameterizedBuild pb, Build b"
                + " WHERE"
                + "	r.possibleresult_id = pr.id"
                + "	AND r.test_id = t.id"
                + "	AND t.testcase_id = tc.id"
                + "	AND r.parameterizedbuild_id = pb.id"
                + "     AND pb.build_id = b.id"
                + "     AND b.job_id = :jobId"
                + " GROUP BY t.name, tc.name, r.possibleresult_id";

        Session session = (Session) em.getDelegate();
        Query query = session.createSQLQuery(queryString).setParameter("jobId", jobDto.getId());

        List<Object[]> testResults = query.list();
        List<ResultDto> resultDtos = new ArrayList<ResultDto>();

        addOrEditResults(testResults, resultDtos);

        return resultDtos;
    }

    private void addOrEditResults(List<Object[]> testResults, List<ResultDto> resultDtos) {
        for (Object[] testResult : testResults) {
            ResultDto aux = createResultDto(testResult);

            if (resultDtos.contains(aux)) {
                for (int i = resultDtos.size() - 1; i >= 0; i--) {

                    if (resultDtos.get(i).equals(aux)) {
                        resultDtos.get(i).getResults().putAll(aux.getResults());
                        break;
                    }

                }
            } else {
                resultDtos.add(aux);
            }
        }
    }

    private ResultDto createResultDto(Object[] result) {
        ResultDto resultDto = new ResultDto();
        resultDto.setTest(result[0].toString());
        resultDto.setTestCase(result[1].toString());

        Map<Long, Integer> testResults = new HashMap<Long, Integer>();
        testResults.put(Long.parseLong(result[2].toString()), Integer.parseInt(result[3].toString()));

        resultDto.setResults(testResults);
        return resultDto;
    }

    private String getTestHistoryQuery() {
        return "SELECT pb.dateTime, pr.name AS posResName, pb.machine, r.duration"
                + " FROM Result r, PossibleResult pr, ParameterizedBuild pb, Build b, Test t, TestCase tc"
                + " WHERE"
                + "	r.possibleresult_id = pr.id"
                + "	AND r.test_id = t.id"
                + "	AND t.testcase_id = tc.id"
                + "	AND r.parameterizedbuild_id = pb.id"
                + "     AND pb.build_id = b.id"
                + "     AND b.job_id = :jobId"
                + "     AND t.name = :testName"
                + "     AND tc.name = :testCaseName"
                + " ORDER BY pb.dateTime DESC";
    }

    public List<TestDto> getTestResults(ResultDto resultDto, ParameterizedBuildDto paramBuildDto) {
        Session session = (Session) em.getDelegate();

        Query query = session.createQuery("FROM ParameterizedBuild WHERE id = :paramBuildId")
                .setParameter("paramBuildId", paramBuildDto.getId());
        ParameterizedBuild paramBuild = (ParameterizedBuild) query.uniqueResult();

        query = session.createSQLQuery(getTestHistoryQuery())
                .setParameter("jobId", paramBuild.getBuild().getJob().getId())
                .setParameter("testName", resultDto.getTest())
                .setParameter("testCaseName", resultDto.getTestCase());

        List<Object[]> testResults = query.list();

        return getTestHistory(testResults);
    }

    public List<TestDto> getTestResults(ResultDto resultDto, BuildDto buildDto) {
        Session session = (Session) em.getDelegate();

        Query query = session.createQuery("FROM Build WHERE id = :buildId")
                .setParameter("buildId", buildDto.getId());
        Build build = (Build) query.uniqueResult();

        query = session.createSQLQuery(getTestHistoryQuery())
                .setParameter("jobId", build.getJob().getId())
                .setParameter("testName", resultDto.getTest())
                .setParameter("testCaseName", resultDto.getTestCase());

        List<Object[]> testResults = query.list();

        return getTestHistory(testResults);
    }

    public List<TestDto> getTestResults(ResultDto resultDto, JobDto jobDto) {
        Session session = (Session) em.getDelegate();

        Query query = session.createQuery("FROM Job WHERE id = :jobId")
                .setParameter("jobId", jobDto.getId());
        Job job = (Job) query.uniqueResult();

        query = session.createSQLQuery(getTestHistoryQuery())
                .setParameter("jobId", job.getId())
                .setParameter("testName", resultDto.getTest())
                .setParameter("testCaseName", resultDto.getTestCase());

        List<Object[]> testResults = query.list();

        return getTestHistory(testResults);
    }

    private List<TestDto> getTestHistory(List<Object[]> testResults) {
        List<TestDto> testHistory = new ArrayList<TestDto>(testResults.size());

        for (Object[] testResult : testResults) {
            testHistory.add(createTestDto(testResult));
        }

        return testHistory;
    }

    private TestDto createTestDto(Object[] test) {
        TestDto testDto = new TestDto();
        testDto.setDate((Date) test[0]);
        testDto.setResult(test[1].toString());
        testDto.setMachine(test[2].toString());
        testDto.setDuration(Float.parseFloat(test[3].toString()));

        return testDto;
    }

}
