package gwtEntity.server;

import gwtEntity.client.JobDto;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import org.hibernate.Session;

/**
 *
 * @author jtymel
 */
@Stateless
public class JenkinsDownloader {

    private static final Logger LOGGER = Logger.getLogger("gwtEntity");

    @EJB
    private BuildServiceBean buildServiceBean;

    @EJB
    private JobServiceBean jobServiceBean;

    @EJB
    private ParameterizedBuildServiceBean paramBuildServiceBean;

    @EJB
    private StoreResultBean storeResultBean;

    @EJB
    private LabelServiceBean labelServiceBean;

    @PersistenceContext(name = "MainPU")
    private EntityManager em;

    public void downloadBuilds(final List<JobDto> jobs) {
        for (JobDto jobDto : jobs) {
            Job job = jobServiceBean.getPlainJob(jobDto);
            List<Build> builds = downloadBuilds(job);
            jobServiceBean.addCategoriesToParamBuild(jobDto);
        }

    }

    public List<Build> downloadBuilds(final Job job) {
        List<Build> builds = null;
        try {
            builds = findBuilds(job);
            List<ParameterizedBuild> paramBuilds = downloadParameterizedBuilds(builds);
        } catch (IOException | XMLStreamException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }
        return builds;
    }

    public List<Build> findBuilds(Job job) throws MalformedURLException, IOException, XMLStreamException {
        XMLEventReader eventReader = getEventReader(job.getUrl() + "/api/xml");

        List<Build> builds = new ArrayList<Build>();
        while (eventReader.hasNext()) {
            XMLEvent event = eventReader.nextEvent();

            if (event.isStartElement()) {
                StartElement startElement = event.asStartElement();

                if (startElement.getName().getLocalPart().equals("build")) {
                    Build build = getBuild(event, eventReader);
//                    Job aux = jobServiceBean.getPlainJob(jobDto);

                    if (!job.getBuilds().contains(build)) {
                        build.setJob(job);
                        LOGGER.log(Level.SEVERE, "Build: " + build.getName() + build.getUrl());
                        buildServiceBean.saveBuild(build);
                        builds.add(build);
                    }
                }

            }
        }

        return builds;
    }

    private static XMLEventReader getEventReader(String link) throws MalformedURLException, IOException, XMLStreamException {
        URL aux = new URL(link);
        URL url = new URL("http", aux.getHost(), aux.getPath());

        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
        XMLInputFactory inputFactory = XMLInputFactory.newFactory();
        return inputFactory.createXMLEventReader(in);

    }

    private static Build getBuild(XMLEvent event, XMLEventReader eventReader) throws XMLStreamException {
        Build build = null;

        while (eventReader.hasNext()) {
            event = eventReader.nextEvent();

            if (event.asStartElement().getName().getLocalPart().equals("number")) {
                build = new Build();
                build.setName(eventReader.getElementText());
            }

            if (event.asStartElement().getName().getLocalPart().equals("url")) {
//                event.asCharacters().getData()
                build.setUrl(eventReader.getElementText());
                return build;
            }
        }
        return build;
    }

    public List<ParameterizedBuild> downloadParameterizedBuilds(List<Build> builds) {
        List<ParameterizedBuild> paramBuilds = null;

        for (Build build : builds) {
            try {
                paramBuilds = findParameterizedBuilds(build);
                Session session = (Session) em.getDelegate();
                session.flush();

                for (ParameterizedBuild paramBuild : paramBuilds) {

                    List<TestResult> testResults = getTestResults(paramBuild);
                    if (testResults != null) {
                        for (TestResult testResult : testResults) {
                            storeResultBean.saveTestResult(testResult, paramBuild);
                        }

                    }
                }

            } catch (IOException | XMLStreamException ex) {
                Logger.getLogger(JenkinsDownloader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return paramBuilds;
    }

    public List<ParameterizedBuild> findParameterizedBuilds(Build build) throws IOException, MalformedURLException, XMLStreamException {
        List<ParameterizedBuild> paramBuilds = new ArrayList<ParameterizedBuild>();
        XMLEventReader eventReader = getEventReader(build.getUrl() + "api/xml");

        while (eventReader.hasNext()) {
            XMLEvent event = eventReader.nextEvent();

            if (event.isStartElement()) {
                StartElement startElement = event.asStartElement();

                if (startElement.getName().getLocalPart().equals("run")) {
                    ParameterizedBuild paramBuild = getParamBuild(event, eventReader);
                    paramBuild = getMachineAndDateTimeOfParamBuild(paramBuild);
                    paramBuild.setBuild(build);
                    paramBuild.setLabel(getLabelName(paramBuild));
                    saveLabel(paramBuild);

                    paramBuildServiceBean.saveParamBuild(paramBuild);
                    paramBuilds.add(paramBuild);
                }

            }
        }

        return paramBuilds;
    }

    private static ParameterizedBuild getParamBuild(XMLEvent event, XMLEventReader eventReader) throws XMLStreamException {
        ParameterizedBuild paramBuild = null;

        while (eventReader.hasNext()) {
            event = eventReader.nextEvent();

            if (event.asStartElement().getName().getLocalPart().equals("number")) {
                paramBuild = new ParameterizedBuild();
                paramBuild.setName(eventReader.getElementText());
            }

            if (event.asStartElement().getName().getLocalPart().equals("url")) {
                paramBuild.setUrl(eventReader.getElementText());
                LOGGER.log(Level.INFO, "Downloaded build with url: " + paramBuild.getUrl());
                return paramBuild;
            }
        }
        return paramBuild;
    }

    public List<TestResult> getTestResults(ParameterizedBuild paramBuild) {
        List<TestResult> testResults = new ArrayList<TestResult>();
        try {
            XMLEventReader eventReader = getEventReader(paramBuild.getUrl() + "testReport/api/xml");

            while (eventReader.hasNext()) {
                XMLEvent event = eventReader.nextEvent();

                if (event.isStartElement()) {
                    StartElement startElement = event.asStartElement();

                    if (startElement.getName().getLocalPart().equals("case")) {
                        TestResult tesResult = getTestResult(event, eventReader);

                        testResults.add(tesResult);
                    }

                }
            }

            return testResults;

        } catch (IOException | XMLStreamException ex) {
            Logger.getLogger(JenkinsDownloader.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    private TestResult getTestResult(XMLEvent event, XMLEventReader eventReader) throws XMLStreamException {
        TestResult testResult = null;

        while (eventReader.hasNext()) {
            event = eventReader.nextEvent();
            if (event.isStartElement()) {
                if (event.asStartElement().getName().getLocalPart().equals("className")) {
                    testResult = new TestResult();
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

                    LOGGER.log(Level.FINEST, "Test result: " + testResult.getResult() + " [" + testResult.getDuration() + "],  " + testResult.getTest() + " from " + testResult.getTestCase());

                    return testResult;
                }
            }
        }

        return null;
    }

    public ParameterizedBuild getMachineAndDateTimeOfParamBuild(ParameterizedBuild paramBuild) {
        try {
            XMLEventReader eventReader = getEventReader(paramBuild.getUrl() + "api/xml");

            while (eventReader.hasNext()) {
                XMLEvent event = eventReader.nextEvent();

                if (event.isStartElement()) {
                    StartElement startElement = event.asStartElement();

                    if (startElement.getName().getLocalPart().equals("timestamp")) {
                        paramBuild.setDatetime(new Date(Long.parseLong(eventReader.getElementText())));
                    }

                    if (startElement.getName().getLocalPart().equals("builtOn")) {
                        paramBuild.setMachine(eventReader.getElementText());
                    }
                }
            }

        } catch (IOException | XMLStreamException ex) {
            Logger.getLogger(JenkinsDownloader.class.getName()).log(Level.SEVERE, null, ex);
        }

        return paramBuild;
    }

    private void saveLabel(ParameterizedBuild paramBuild) {
        String labelName = getLabelName(paramBuild);

        Label label = new Label();
        label.setName(labelName);
        label.setJob(paramBuild.getBuild().getJob());

        labelServiceBean.saveLabel(label);
    }

    private String getLabelName(ParameterizedBuild paramBuild) {
        String labelName = paramBuild.getUrl().substring(0, paramBuild.getUrl().lastIndexOf("/"));
        labelName = labelName.substring(0, labelName.lastIndexOf("/"));
        labelName = labelName.substring(labelName.lastIndexOf("/") + 1, labelName.length());
        return labelName;
    }
}
