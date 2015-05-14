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

import gwtEntity.server.entity.ParameterizedBuild;
import gwtEntity.server.entity.PossibleResult;
import gwtEntity.server.entity.Job;
import gwtEntity.server.entity.Build;
import gwtEntity.common.objects.BuildDto;
import gwtEntity.common.objects.JobDto;
import gwtEntity.common.objects.ParameterizedBuildDto;
import gwtEntity.common.objects.PossibleResultDto;
import gwtEntity.common.objects.ResultDto;
import gwtEntity.common.objects.TestDto;
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

    private static final int QUERY_TEST_DTO_POSITION_DATE = 0;
    private static final int QUERY_TEST_DTO_POSITION_RESULT = 1;
    private static final int QUERY_TEST_DTO_POSITION_MACHINE = 2;
    private static final int QUERY_TEST_DTO_POSITION_DURATION = 3;
    private static final int QUERY_TEST_DTO_POSITION_URL = 4;

    private static final int QUERY_RESULT_DTO_POSITION_TEST_ID = 0;
    private static final int QUERY_RESULT_DTO_POSITION_TEST_NAME = 1;
    private static final int QUERY_RESULT_DTO_POSITION_TEST_CASE_NAME = 2;
    private static final int QUERY_RESULT_DTO_POSITION_POSSIBLE_RESULT = 3;
    private static final int QUERY_RESULT_DTO_POSITION_COUNT_OF_RESULTS = 4;

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

    public List<ResultDto> getResults(JobDto jobDto, Long possibleResultId, Long categoryId) {
        if (jobDto == null) {
            return null;
        }

        Query query = getJobResultQuery(jobDto, possibleResultId, categoryId);

        List<Object[]> testResults = query.list();
        List<ResultDto> resultDtos = new ArrayList<ResultDto>();

        addOrEditResults(testResults, resultDtos);

        return resultDtos;
    }

    public List<ResultDto> getResults(BuildDto buildDto, Long possibleResultId, Long categoryId) {
        if (buildDto == null) {
            return null;
        }

        Query query = getBuildResultQuery(buildDto, possibleResultId, categoryId);

        List<Object[]> testResults = query.list();
        List<ResultDto> resultDtos = new ArrayList<ResultDto>(testResults.size());

        addOrEditResults(testResults, resultDtos);

        return resultDtos;
    }

    public List<ResultDto> getResults(ParameterizedBuildDto paramBuildDto, Long possibleResultId, Long categoryId) {
        if (paramBuildDto == null) {
            return null;
        }

        Query query = getParamBuildResultQuery(paramBuildDto, possibleResultId, categoryId);

        List<Object[]> testResults = query.list();
        List<ResultDto> resultDtos = new ArrayList<ResultDto>(testResults.size());

        addOrEditResults(testResults, resultDtos);

        return resultDtos;
    }

    private Query getParamBuildResultQuery(ParameterizedBuildDto paramBuildDto, Long possibleResultId, Long categoryId) {
        Session session = (Session) em.getDelegate();

        if (possibleResultId != null && categoryId != null) {
            final String queryString = "SELECT t.id, t.name AS testName, tc.name AS testCaseName, r.possibleresult_id, COUNT(*) AS res"
                    + " FROM Result r, Test t, TestCase tc, ParameterizedBuild pb, ParamBuild_Category pbc"
                    + " WHERE"
                    + "     r.possibleresult_id = :possibleResultId"
                    + "     AND r.test_id = t.id"
                    + "     AND t.testcase_id = tc.id"
                    + "     AND r.parameterizedbuild_id = :paramBuildId"
                    + "     AND pbc.parambuild_id = r.parameterizedbuild_id"
                    + "     AND pbc.category_id = :categoryId"
                    + " GROUP BY t.id, t.name, tc.name, r.possibleresult_id"
                    + " ORDER BY tc.name, t.name";

            return session.createSQLQuery(queryString)
                    .setParameter("paramBuildId", paramBuildDto.getId())
                    .setParameter("possibleResultId", possibleResultId)
                    .setParameter("categoryId", categoryId);

        } else if (possibleResultId == null && categoryId == null) {
            String queryString = "SELECT t.id, t.name AS testName, tc.name AS testCaseName, r.possibleresult_id, COUNT(*) AS res"
                    + " FROM Result r, PossibleResult pr, Test t, TestCase tc"
                    + " WHERE"
                    + "     r.possibleresult_id = pr.id"
                    + "     AND r.test_id = t.id"
                    + "     AND t.testcase_id = tc.id"
                    + "     AND r.parameterizedbuild_id = :paramBuildId"
                    + " GROUP BY t.id, t.name, tc.name, r.possibleresult_id"
                    + " ORDER BY tc.name, t.name";

            return session.createSQLQuery(queryString).setParameter("paramBuildId", paramBuildDto.getId());

        } else if (possibleResultId != null && categoryId == null) {
            String queryString = "SELECT t.id, t.name AS testName, tc.name AS testCaseName, r.possibleresult_id, COUNT(*) AS res"
                    + " FROM Result r, Test t, TestCase tc"
                    + " WHERE"
                    + "     r.possibleresult_id = :possibleResultId"
                    + "     AND r.test_id = t.id"
                    + "     AND t.testcase_id = tc.id"
                    + "     AND r.parameterizedbuild_id = :paramBuildId"
                    + " GROUP BY t.id, t.name, tc.name, r.possibleresult_id"
                    + " ORDER BY tc.name, t.name";

            return session.createSQLQuery(queryString)
                    .setParameter("paramBuildId", paramBuildDto.getId())
                    .setParameter("possibleResultId", possibleResultId);

        } else {
            String queryString = "SELECT t.id, t.name AS testName, tc.name AS testCaseName, r.possibleresult_id, COUNT(*) AS res"
                    + " FROM Result r, Test t, TestCase tc, ParamBuild_Category pbc"
                    + " WHERE"
                    + "     r.test_id = t.id"
                    + "     AND t.testcase_id = tc.id"
                    + "     AND r.parameterizedbuild_id = :paramBuildId"
                    + "     AND pbc.parambuild_id = r.parameterizedbuild_id"
                    + "     AND pbc.category_id = :categoryId"
                    + " GROUP BY t.id, t.name, tc.name, r.possibleresult_id"
                    + " ORDER BY tc.name, t.name";

            return session.createSQLQuery(queryString)
                    .setParameter("paramBuildId", paramBuildDto.getId())
                    .setParameter("categoryId", categoryId);
        }
    }

    private Query getBuildResultQuery(BuildDto buildDto, Long possibleResultId, Long categoryId) {
        Session session = (Session) em.getDelegate();

        if (possibleResultId != null && categoryId != null) {
            String queryString = "SELECT t.id, t.name AS testName, tc.name AS testCaseName, r.possibleresult_id, COUNT(*) AS res"
                    + " FROM Result r, Test t, TestCase tc, ParameterizedBuild pb, ParamBuild_Category pbc"
                    + " WHERE"
                    + "     r.possibleresult_id = :possibleResultId"
                    + "     AND r.test_id = t.id"
                    + "     AND t.testcase_id = tc.id"
                    + "     AND r.parameterizedbuild_id = pb.id"
                    + "     AND pb.build_id = :buildId"
                    + "     AND pbc.category_id = :categoryId"
                    + "     AND pbc.parambuild_id = pb.id"
                    + " GROUP BY t.id, t.name, tc.name, r.possibleresult_id"
                    + " ORDER BY tc.name, t.name";

            return session.createSQLQuery(queryString)
                    .setParameter("buildId", buildDto.getId())
                    .setParameter("possibleResultId", possibleResultId)
                    .setParameter("categoryId", categoryId);

        } else if (possibleResultId == null && categoryId == null) {
            String queryString = "SELECT t.id, t.name AS testName, tc.name AS testCaseName, r.possibleresult_id, COUNT(*) AS res"
                    + " FROM Result r, PossibleResult pr, Test t, TestCase tc, ParameterizedBuild pb"
                    + " WHERE"
                    + "     r.possibleresult_id = pr.id"
                    + "     AND r.test_id = t.id"
                    + "     AND t.testcase_id = tc.id"
                    + "     AND r.parameterizedbuild_id = pb.id"
                    + "     AND pb.build_id = :buildId"
                    + " GROUP BY t.id, t.name, tc.name, r.possibleresult_id"
                    + " ORDER BY tc.name, t.name";

            return session.createSQLQuery(queryString).setParameter("buildId", buildDto.getId());

        } else if (possibleResultId != null && categoryId == null) {
            String queryString = "SELECT t.id, t.name AS testName, tc.name AS testCaseName, r.possibleresult_id, COUNT(*) AS res"
                    + " FROM Result r, Test t, TestCase tc, ParameterizedBuild pb"
                    + " WHERE"
                    + "     r.possibleresult_id = :possibleResultId"
                    + "     AND r.test_id = t.id"
                    + "     AND t.testcase_id = tc.id"
                    + "     AND r.parameterizedbuild_id = pb.id"
                    + "     AND pb.build_id = :buildId"
                    + " GROUP BY t.id, t.name, tc.name, r.possibleresult_id"
                    + " ORDER BY tc.name, t.name";

            return session.createSQLQuery(queryString)
                    .setParameter("buildId", buildDto.getId())
                    .setParameter("possibleResultId", possibleResultId);

        } else {
            String queryString = "SELECT t.id, t.name AS testName, tc.name AS testCaseName, r.possibleresult_id, COUNT(*) AS res"
                    + " FROM Result r, Test t, TestCase tc, ParameterizedBuild pb, ParamBuild_Category pbc"
                    + " WHERE"
                    + "     r.test_id = t.id"
                    + "     AND t.testcase_id = tc.id"
                    + "     AND r.parameterizedbuild_id = pb.id"
                    + "     AND pb.build_id = :buildId"
                    + "     AND pbc.category_id = :categoryId"
                    + "     AND pbc.parambuild_id = pb.id"
                    + " GROUP BY t.id, t.name, tc.name, r.possibleresult_id"
                    + " ORDER BY tc.name, t.name";

            return session.createSQLQuery(queryString)
                    .setParameter("buildId", buildDto.getId())
                    .setParameter("categoryId", categoryId);
        }
    }

    private Query getJobResultQuery(JobDto jobDto, Long possibleResultId, Long categoryId) {
        Session session = (Session) em.getDelegate();

        if (possibleResultId != null && categoryId != null) {
            String queryString = "SELECT t.id, t.name AS testName, tc.name AS testCaseName, r.possibleresult_id, COUNT(*) AS res"
                    + " FROM Result r, Test t, TestCase tc, ParameterizedBuild pb, Build b, ParamBuild_Category pbc"
                    + " WHERE"
                    + "     r.possibleresult_id = :possibleResultId"
                    + "     AND r.test_id = t.id"
                    + "     AND t.testcase_id = tc.id"
                    + "     AND r.parameterizedbuild_id = pb.id"
                    + "     AND pb.build_id = b.id"
                    + "     AND b.job_id = :jobId"
                    + "     AND pbc.parambuild_id = pb.id"
                    + "     AND pbc.category_id = :categoryId"
                    + " GROUP BY t.id, t.name, tc.name, r.possibleresult_id"
                    + " ORDER BY tc.name, t.name";

            return session.createSQLQuery(queryString)
                    .setParameter("jobId", jobDto.getId())
                    .setParameter("possibleResultId", possibleResultId)
                    .setParameter("categoryId", categoryId);

        } else if (possibleResultId == null && categoryId == null) {
            String queryString = "SELECT t.id, t.name AS testName, tc.name AS testCaseName, r.possibleresult_id, COUNT(*) AS res"
                    + " FROM Result r, Test t, TestCase tc, ParameterizedBuild pb, Build b"
                    + " WHERE"
                    + "     r.test_id = t.id"
                    + "     AND t.testcase_id = tc.id"
                    + "     AND r.parameterizedbuild_id = pb.id"
                    + "     AND pb.build_id = b.id"
                    + "     AND b.job_id = :jobId"
                    + " GROUP BY t.id, t.name, tc.name, r.possibleresult_id"
                    + " ORDER BY tc.name, t.name";

            return session.createSQLQuery(queryString)
                    .setParameter("jobId", jobDto.getId());

        } else if (possibleResultId != null && categoryId == null) {
            String queryString = "SELECT t.id, t.name AS testName, tc.name AS testCaseName, r.possibleresult_id, COUNT(*) AS res"
                    + " FROM Result r, Test t, TestCase tc, ParameterizedBuild pb, Build b"
                    + " WHERE"
                    + "     r.possibleresult_id = :possibleResultId"
                    + "     AND r.test_id = t.id"
                    + "     AND t.testcase_id = tc.id"
                    + "     AND r.parameterizedbuild_id = pb.id"
                    + "     AND pb.build_id = b.id"
                    + "     AND b.job_id = :jobId"
                    + " GROUP BY t.id, t.name, tc.name, r.possibleresult_id"
                    + " ORDER BY tc.name, t.name";

            return session.createSQLQuery(queryString)
                    .setParameter("jobId", jobDto.getId())
                    .setParameter("possibleResultId", possibleResultId);

        } else {
            String queryString = "SELECT t.id, t.name AS testName, tc.name AS testCaseName, r.possibleresult_id, COUNT(*) AS res"
                    + " FROM Result r, Test t, TestCase tc, ParameterizedBuild pb, Build b, ParamBuild_Category pbc"
                    + " WHERE"
                    + "     r.test_id = t.id"
                    + "     AND t.testcase_id = tc.id"
                    + "     AND r.parameterizedbuild_id = pb.id"
                    + "     AND pb.build_id = b.id"
                    + "     AND b.job_id = :jobId"
                    + "     AND pbc.parambuild_id = pb.id"
                    + "     AND pbc.category_id = :categoryId"
                    + " GROUP BY t.id, t.name, tc.name, r.possibleresult_id"
                    + " ORDER BY tc.name, t.name";

            return session.createSQLQuery(queryString)
                    .setParameter("jobId", jobDto.getId())
                    .setParameter("categoryId", categoryId);
        }

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
        resultDto.setTestId(Long.parseLong(result[QUERY_RESULT_DTO_POSITION_TEST_ID].toString()));
        resultDto.setTest(result[QUERY_RESULT_DTO_POSITION_TEST_NAME].toString());
        resultDto.setTestCase(result[QUERY_RESULT_DTO_POSITION_TEST_CASE_NAME].toString());

        Map<Long, Integer> testResults = new HashMap<Long, Integer>();
        testResults.put(Long.parseLong(result[QUERY_RESULT_DTO_POSITION_POSSIBLE_RESULT].toString()), Integer.parseInt(result[QUERY_RESULT_DTO_POSITION_COUNT_OF_RESULTS].toString()));

        resultDto.setResults(testResults);
        return resultDto;
    }

    private final String TEST_HISTORY_QUERY_ALL = "SELECT pb.dateTime, pr.name AS posResName, pb.machine, r.duration, pb.url"
            + " FROM Result r, PossibleResult pr, ParameterizedBuild pb, Build b, Test t, TestCase tc"
            + " WHERE"
            + "     r.possibleresult_id = pr.id"
            + "     AND r.test_id = t.id"
            + "     AND t.testcase_id = tc.id"
            + "     AND r.parameterizedbuild_id = pb.id"
            + "     AND pb.build_id = b.id"
            + "     AND b.job_id = :jobId"
            + "     AND t.id = :testId"
            + " ORDER BY pb.dateTime DESC";

    private final String TEST_HISTORY_QUERY_FILTER_RESULTS
            = "SELECT pb.dateTime, pr.name AS posResName, pb.machine, r.duration, pb.url"
            + " FROM Result r, PossibleResult pr, ParameterizedBuild pb, Build b, Test t, TestCase tc"
            + " WHERE"
            + "     r.possibleresult_id = pr.id"
            + "     AND r.test_id = t.id"
            + "     AND t.testcase_id = tc.id"
            + "     AND r.parameterizedbuild_id = pb.id"
            + "     AND pb.build_id = b.id"
            + "     AND r.possibleresult_id = :possibleResultId"
            + "     AND b.job_id = :jobId"
            + "     AND t.id = :testId"
            + " ORDER BY pb.dateTime DESC";

    private final String TEST_HISTORY_QUERY_FILTER_CATEGORIES
            = "SELECT pb.dateTime, pr.name AS posResName, pb.machine, r.duration, pb.url"
            + " FROM Result r, PossibleResult pr, ParameterizedBuild pb, Build b, Test t, TestCase tc, ParamBuild_category pbc"
            + " WHERE"
            + "     r.possibleresult_id = pr.id"
            + "     AND r.test_id = t.id"
            + "     AND t.testcase_id = tc.id"
            + "     AND r.parameterizedbuild_id = pb.id"
            + "     AND pb.build_id = b.id"
            + "     AND pbc.parambuild_id = pb.id"
            + "     AND pbc.category_id = :categoryId"
            + "     AND b.job_id = :jobId"
            + "     AND t.id = :testId"
            + " ORDER BY pb.dateTime DESC";

    private final String TEST_HISTORY_QUERY_FILTER_CATEGORIES_RESULTS
            = "SELECT pb.dateTime, pr.name AS posResName, pb.machine, r.duration, pb.url"
            + " FROM Result r, PossibleResult pr, ParameterizedBuild pb, Build b, Test t, TestCase tc, ParamBuild_category pbc"
            + " WHERE"
            + "     r.possibleresult_id = pr.id"
            + "     AND r.possibleresult_id = :possibleResultId"
            + "     AND r.test_id = t.id"
            + "     AND t.testcase_id = tc.id"
            + "     AND r.parameterizedbuild_id = pb.id"
            + "     AND pb.build_id = b.id"
            + "     AND pbc.parambuild_id = pb.id"
            + "     AND pbc.category_id = :categoryId"
            + "     AND b.job_id = :jobId"
            + "     AND t.id = :testId"
            + " ORDER BY pb.dateTime DESC";

    public List<TestDto> getTestResults(ResultDto resultDto, ParameterizedBuildDto paramBuildDto, Long resultId, Long categoryId) {
        Session session = (Session) em.getDelegate();

        Query query = session.createQuery("FROM ParameterizedBuild WHERE id = :paramBuildId")
                .setParameter("paramBuildId", paramBuildDto.getId());
        ParameterizedBuild paramBuild = (ParameterizedBuild) query.uniqueResult();

        query = getTestHistoryQuery(resultDto.getTestId(), paramBuild.getBuild().getJob().getId(), resultId, categoryId);

        List<Object[]> testResults = query.list();

        return getTestHistory(testResults);
    }

    public List<TestDto> getTestResults(ResultDto resultDto, BuildDto buildDto, Long resultId, Long categoryId) {
        Session session = (Session) em.getDelegate();

        Query query = session.createQuery("FROM Build WHERE id = :buildId")
                .setParameter("buildId", buildDto.getId());
        Build build = (Build) query.uniqueResult();

        query = getTestHistoryQuery(resultDto.getTestId(), build.getJob().getId(), resultId, categoryId);

        List<Object[]> testResults = query.list();

        return getTestHistory(testResults);
    }

    public List<TestDto> getTestResults(ResultDto resultDto, JobDto jobDto, Long resultId, Long categoryId) {
        Session session = (Session) em.getDelegate();

        Query query = getTestHistoryQuery(resultDto.getTestId(), jobDto.getId(), resultId, categoryId);

        List<Object[]> testResults = query.list();

        return getTestHistory(testResults);
    }

    private Query getTestHistoryQuery(Long testId, Long jobId, Long resultId, Long categoryId) {
        Session session = (Session) em.getDelegate();

        if (resultId == null && categoryId == null) {
            return session.createSQLQuery(TEST_HISTORY_QUERY_ALL)
                    .setParameter("jobId", jobId)
                    .setParameter("testId", testId);

        } else if (resultId != null && categoryId != null) {
            Query query = session.createQuery("FROM PossibleResult WHERE id = :result")
                    .setParameter("result", resultId);
            PossibleResult possibleResult = (PossibleResult) query.uniqueResult();

            return session.createSQLQuery(TEST_HISTORY_QUERY_FILTER_CATEGORIES_RESULTS)
                    .setParameter("jobId", jobId)
                    .setParameter("testId", testId)
                    .setParameter("categoryId", categoryId)
                    .setParameter("possibleResultId", possibleResult.getId());

        } else if (resultId == null && categoryId != null) {
            return session.createSQLQuery(TEST_HISTORY_QUERY_FILTER_CATEGORIES)
                    .setParameter("jobId", jobId)
                    .setParameter("testId", testId)
                    .setParameter("categoryId", categoryId);

        } else {
            Query query = session.createQuery("FROM PossibleResult WHERE id = :result")
                    .setParameter("result", resultId);

            PossibleResult possibleResult = (PossibleResult) query.uniqueResult();

            return session.createSQLQuery(TEST_HISTORY_QUERY_FILTER_RESULTS)
                    .setParameter("jobId", jobId)
                    .setParameter("testId", testId)
                    .setParameter("possibleResultId", possibleResult.getId());
        }
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
        testDto.setDate((Date) test[QUERY_TEST_DTO_POSITION_DATE]);
        testDto.setResult(test[QUERY_TEST_DTO_POSITION_RESULT].toString());
        testDto.setMachine(test[QUERY_TEST_DTO_POSITION_MACHINE].toString());
        testDto.setDuration(Float.parseFloat(test[QUERY_TEST_DTO_POSITION_DURATION].toString()));
        testDto.setUrl(test[QUERY_TEST_DTO_POSITION_URL].toString());

        return testDto;
    }

}
