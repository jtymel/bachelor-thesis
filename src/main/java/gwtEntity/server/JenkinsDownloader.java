package gwtEntity.server;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import gwtEntity.client.BuildDto;
import gwtEntity.client.BuildService;
import gwtEntity.client.BuildServiceAsync;
import gwtEntity.client.JobDto;
import gwtEntity.client.ParameterizedBuildDto;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

/**
 *
 * @author jtymel
 */
@Stateless
public class JenkinsDownloader {
    private static final String JENKINS_SERVER = "";
    
    private static final Logger LOGGER = Logger.getLogger("gwtEntity");

    
    public static List<BuildDto> downloadBuilds(final JobDto jobDto) {        
        List<BuildDto> builds = null;        
        try {
            builds = findBuilds(jobDto);
        } catch (IOException | XMLStreamException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        return builds;
    }
    
    public static List<BuildDto> findBuilds(JobDto jobDto) throws MalformedURLException, IOException, XMLStreamException {        
        XMLEventReader eventReader = getEventReader(jobDto.getUrl());
                
        List<BuildDto> builds = new ArrayList<BuildDto>();
        while (eventReader.hasNext()) {
            XMLEvent event = eventReader.nextEvent();

            if (event.isStartElement()) {
                StartElement startElement = event.asStartElement();                
                
                if (startElement.getName().getLocalPart().equals("build")) {
                    BuildDto build = getBuild(event, eventReader);
                    build.setJob(jobDto);
                                                                       
                    builds.add(build);
                }

            }
        }

        return builds;
    }
    
    private static XMLEventReader getEventReader(String link) throws MalformedURLException, IOException, XMLStreamException {
        URL aux = new URL(link);
        URL url = new URL("http", aux.getHost(), aux.getPath());

        BufferedReader in =  new BufferedReader(new InputStreamReader(url.openStream()));
        XMLInputFactory inputFactory = XMLInputFactory.newFactory();
        return inputFactory.createXMLEventReader(in);
    
    }
    
    private static BuildDto getBuild(XMLEvent event, XMLEventReader eventReader) throws XMLStreamException {
        BuildDto build = null;
        
        while (eventReader.hasNext()) {            
            event = eventReader.nextEvent();
            
            if (event.asStartElement().getName().getLocalPart().equals("number")) {
                build = new BuildDto();               
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
    
    public List<ParameterizedBuildDto> downloadParameterizedBuilds(BuildDto buildDto) {
        List<ParameterizedBuildDto> paramBuilds = null;
        
        try {
            paramBuilds = findParameterizedBuilds(buildDto);
        } catch (IOException | XMLStreamException ex) {
            Logger.getLogger(JenkinsDownloader.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return paramBuilds;
    }
    
    public List<ParameterizedBuildDto> findParameterizedBuilds(BuildDto buildDto) throws IOException, MalformedURLException, XMLStreamException {
        List<ParameterizedBuildDto> paramBuilds = new ArrayList<ParameterizedBuildDto>();
        XMLEventReader eventReader = getEventReader(buildDto.getUrl()+"api/xml");
                
        while (eventReader.hasNext()) {
            XMLEvent event = eventReader.nextEvent();

            if (event.isStartElement()) {
                StartElement startElement = event.asStartElement();                
                
                if (startElement.getName().getLocalPart().equals("run")) {
                    ParameterizedBuildDto paramBuild = getParamBuild(event, eventReader);
                    paramBuild.setId_build(buildDto);
                                                                       
                    paramBuilds.add(paramBuild);
                }

            }
        }

        return paramBuilds;

    }
    
    private static ParameterizedBuildDto getParamBuild(XMLEvent event, XMLEventReader eventReader) throws XMLStreamException {
        ParameterizedBuildDto paramBuild = null;
        
        while (eventReader.hasNext()) {            
            event = eventReader.nextEvent();
            
            if (event.asStartElement().getName().getLocalPart().equals("number")) {
                paramBuild = new ParameterizedBuildDto();               
                paramBuild.setName(eventReader.getElementText());                
            }
            
            if (event.asStartElement().getName().getLocalPart().equals("url")) {
                paramBuild.setUrl(eventReader.getElementText());
                return paramBuild;
            }
        }
        return paramBuild;
    }
    
    
    
    public static void main(String[] args) {
        JobDto job = new JobDto();
        job.setUrl("");
//        downloadBuilds(job);
    }
}
