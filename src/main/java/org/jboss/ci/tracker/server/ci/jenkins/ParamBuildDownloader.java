/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jboss.ci.tracker.server.ci.jenkins;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.jboss.ci.tracker.server.LabelBean;
import org.jboss.ci.tracker.server.PossibleResultBean;
import org.jboss.ci.tracker.server.StoreParamBuildCategoriesBean;
import org.jboss.ci.tracker.server.StoreResultBean;
import org.jboss.ci.tracker.server.TestCaseBean;
import org.jboss.ci.tracker.server.TestResult;
import org.jboss.ci.tracker.server.entity.Label;
import org.jboss.ci.tracker.server.entity.ParameterizedBuild;
import org.jboss.ci.tracker.server.entity.PossibleResult;
import org.jboss.ci.tracker.server.entity.Result;
import org.jboss.ci.tracker.server.entity.Test;
import org.jboss.ci.tracker.server.entity.TestCase;
import org.jboss.logging.Logger;

/**
 * This task downloads a single parameterized build.
 * @author Hynek Mlnarik <hmlnarik@redhat.com>
 */
@Stateless
public class ParamBuildDownloader {

    private static final Logger LOGGER = Logger.getLogger(ParamBuildDownloader.class);

    private static final String ELEMENT_TEST_CASE = "case";

    @EJB
    private StoreResultBean storeResultBean;

    @EJB
    private StoreParamBuildCategoriesBean storeParamBuildCategoriesBean;

    @EJB
    private LabelBean labelBean;

    @EJB
    private PossibleResultBean possibleResultBean;

    @EJB
    private TestCaseBean testCaseBean;

    @PersistenceContext(name = "MainPU")
    private EntityManager em;

    public void download(ParameterizedBuild paramBuild) {
        LOGGER.debugv("Downloading parameterized build {0} with URL {1}", paramBuild.getName(), paramBuild.getUrl());

        createOrFindLabel(paramBuild);
        storeParamBuild(paramBuild);

        Collection<TestResult> testResults = getTestResults(paramBuild);

        if (testResults != null) {
            Collection<Result> results = new HashSet<Result>();

            Map<String, PossibleResult> possibleResults = new HashMap<String, PossibleResult>();
            for (TestResult testResult : testResults) {
                final String possibleResult = testResult.getResult();
                final PossibleResult pr;
                if (possibleResults.containsKey(possibleResult)) {
                    pr = possibleResults.get(possibleResult);
                } else {
                    pr = getPossibleResult(possibleResult);
                    possibleResults.put(possibleResult, pr);
                }

                final String testCaseName = testResult.getTestCase();

                final String testName = testResult.getTest();
                final Test t = getTest(testCaseName, testName);

                final Result r = new Result();
                r.setDuration(testResult.getDuration());
                r.setParameterizedBuild(paramBuild);
                r.setPossibleResult(pr);
                r.setTest(t);
                results.add(r);
            }

            for (Result result : results) {
                storeResultBean.saveTestResult(result.getDuration(), result.getTest(), paramBuild, result.getPossibleResult());
            }
        }

        LOGGER.debugv("COMPLETED: Downloading parameterized build {0} with URL {1}", paramBuild.getName(), paramBuild.getUrl());
    }

    private Label createOrFindLabel(ParameterizedBuild paramBuild) {
        return labelBean.createOrFindLabel(paramBuild.getLabel(), paramBuild.getBuild().getJob().getId());
    }

    private Test getTest(final String testCase, final String testName) {
        Test res = null;
        int retryCount = 0;
        while (res == null && retryCount < 10) {
            res = testCaseBean.ensureTestExists(testCase, testName);
        }
        return (res == null) // created in concurrent transaction, reload
          ? testCaseBean.getTest(testCase, testName)
          : res;
    }

    private TestCase getTestCase(final String testCaseName) {
        final TestCase res = testCaseBean.ensureTestCaseExists(testCaseName);
        return (res == null) // created in concurrent transaction, reload
          ? testCaseBean.getTestCase(testCaseName)
          : res;
    }

    private PossibleResult getPossibleResult(final String possibleResult) {
        final PossibleResult res = possibleResultBean.ensurePossibleResultExists(possibleResult);
        return (res == null) // created in concurrent transaction, reload
          ? possibleResultBean.getPossibleResult(possibleResult)
          : res;
    }

    private void storeParamBuild(ParameterizedBuild paramBuild) throws HibernateException {
        Session session = (Session) em.getDelegate();
        session.saveOrUpdate(paramBuild);
        session.flush();

        storeParamBuildCategoriesBean.addCategoriesToParamBuild(paramBuild);
    }

    public Collection<TestResult> getTestResults(ParameterizedBuild paramBuild) {
        Set<TestResult> testResults = new HashSet<TestResult>();
        try {
            XMLEventReader eventReader = JenkinsDownloader.getEventReader(paramBuild.getUrl() + "testReport/api/xml");

            while (eventReader.hasNext()) {
                XMLEvent event = eventReader.nextEvent();

                if (event.isStartElement()) {
                    StartElement startElement = event.asStartElement();

                    if (startElement.getName().getLocalPart().equals(ELEMENT_TEST_CASE)) {
                        TestResult tesResult = getTestResult(event, eventReader);
                        testResults.add(tesResult);
                    }

                }
            }

            return testResults;
        } catch (IOException | XMLStreamException ex) {
            LOGGER.warnv("Results of parameterized build {0} were not found", paramBuild.getUrl());
        }

        return null;
    }

    private TestResult getTestResult(XMLEvent event, XMLEventReader eventReader) throws XMLStreamException {
        TestResult testResult = new TestResult();

        while (eventReader.hasNext()) {
            event = eventReader.nextEvent();
            if (event.isStartElement()) {
                if (event.asStartElement().getName().getLocalPart().equals("className")) {
                    testResult.setTestCase(eventReader.getElementText());
                }

                if (event.asStartElement().getName().getLocalPart().equals("duration")) {
                    testResult.setDuration(Float.parseFloat(eventReader.getElementText()));
                }

                if (event.asStartElement().getName().getLocalPart().equals("name")) {
                    testResult.setTest(eventReader.getElementText());
                }

                if (event.asStartElement().getName().getLocalPart().equals("status")) {
                    testResult.setResult(eventReader.getElementText());

                }
            } else if (event.isEndElement()) {
                EndElement endElement = event.asEndElement();

                if (endElement.getName().getLocalPart().equals(ELEMENT_TEST_CASE)) {
//                    LOGGER.debugv("Test result: {0} [{1}], {2} from {3}",
//                      testResult.getResult(),
//                      testResult.getDuration(),
//                      testResult.getTest(),
//                      testResult.getTestCase()
//                    );
                    return testResult;
                }
            }
        }

        return null;
    }
}
