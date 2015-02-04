package gwtEntity.server;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import gwtEntity.client.BuildDto;
import gwtEntity.client.BuildService;
import gwtEntity.client.BuildServiceAsync;
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

    
    public static List<BuildDto> downloadResults(final JobDto jobDto) {        
        List<BuildDto> builds = null;        
        try {
            builds = parseDocument(jobDto);
        } catch (IOException | XMLStreamException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        return builds;
    }
    
    public static List<BuildDto> parseDocument(JobDto jobDto) throws MalformedURLException, IOException, XMLStreamException {
        List<BuildDto> builds = new ArrayList<BuildDto>();
        
        URL url = new URL("http", JENKINS_SERVER, jobDto.getUrl());

        BufferedReader in =  new BufferedReader(new InputStreamReader(url.openStream()));
        XMLInputFactory inputFactory = XMLInputFactory.newFactory();
        XMLEventReader eventReader = inputFactory.createXMLEventReader(in);

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
    public static void main(String[] args) {
        JobDto job = new JobDto();
        job.setUrl("");
//        downloadResults(job);
    }
}
