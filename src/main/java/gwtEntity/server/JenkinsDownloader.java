package gwtEntity.server;

import gwtEntity.client.JobDto;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
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
    private static final Logger LOGGER = Logger.getLogger("gwtEntity");
    
    @EJB
    private BuildServiceBean buildServiceBean;
    
    @EJB
    private ParameterizedBuildServiceBean paramBuildServiceBean;
    
 
    public void downloadBuilds(final List<JobDto> jobs){
        for (JobDto job : jobs) {
            List<Build> builds = downloadBuilds(job);                    
        }
        
   
    }

    
    public List<Build> downloadBuilds(final JobDto jobDto) {        
        List<Build> builds = null;        
        try {
            builds = findBuilds(jobDto);    
            List<ParameterizedBuild> paramBuilds = downloadParameterizedBuilds(builds);
        } catch (IOException | XMLStreamException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        return builds;
    }
    
    public List<Build> findBuilds(JobDto jobDto) throws MalformedURLException, IOException, XMLStreamException {        
        XMLEventReader eventReader = getEventReader(jobDto.getUrl());
                
        List<Build> builds = new ArrayList<Build>();
        while (eventReader.hasNext()) {
            XMLEvent event = eventReader.nextEvent();

            if (event.isStartElement()) {
                StartElement startElement = event.asStartElement();                
                
                if (startElement.getName().getLocalPart().equals("build")) {
                    Build build = getBuild(event, eventReader);
                    Job aux = new Job(jobDto);
                    build.setJob(aux);
                    LOGGER.log(Level.SEVERE, "Build: " + build.getName() + build.getUrl());
                    buildServiceBean.saveBuild(build);
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
            } catch (IOException | XMLStreamException ex) {
                Logger.getLogger(JenkinsDownloader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
                        
        return paramBuilds;
    }               
  
    public List<ParameterizedBuild> findParameterizedBuilds(Build build) throws IOException, MalformedURLException, XMLStreamException {
        List<ParameterizedBuild> paramBuilds = new ArrayList<ParameterizedBuild>();
        XMLEventReader eventReader = getEventReader(build.getUrl()+"api/xml");
                
        while (eventReader.hasNext()) {
            XMLEvent event = eventReader.nextEvent();

            if (event.isStartElement()) {
                StartElement startElement = event.asStartElement();                
                
                if (startElement.getName().getLocalPart().equals("run")) {
                    ParameterizedBuild paramBuild = getParamBuild(event, eventReader);
                    
                    paramBuild.setBuild(build);
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
                LOGGER.log(Level.SEVERE, "URL of parameterized build " + paramBuild.getUrl());
                return paramBuild;
            }
        }
        return paramBuild;
    }
           
}
