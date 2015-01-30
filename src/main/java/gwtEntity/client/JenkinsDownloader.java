package gwtEntity.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

/**
 *
 * @author jtymel
 */
public class JenkinsDownloader {
    private static final String JENKINS_SERVER = "";
    private static final String PATH_TO_LOGIN_CONFIG = "";
    private static final String PATH_TO_KERB_CONFIG = "";

    
    public static void downloadResults(final String jobUrl) throws LoginException, PrivilegedActionException, MalformedURLException, IOException {        
        System.setProperty("java.security.auth.login.config", PATH_TO_LOGIN_CONFIG);
        System.setProperty("sun.security.krb5.debug", "true");
        System.setProperty("javax.security.auth.useSubjectCredsOnly", "false");
        System.setProperty("java.security.krb5.conf", PATH_TO_KERB_CONFIG);                  

        LoginContext lc = new LoginContext("spnegoClient");
        lc.login();       
        
        Subject.doAs(lc.getSubject(), new PrivilegedExceptionAction<Object>() {

            @Override
            public Object run() throws Exception {
                parseDocument(jobUrl);
                return null;
            }
        });
   
    }
    
    public static void parseDocument(String jobUrl) throws MalformedURLException, IOException, XMLStreamException {
        URL url = new URL("https", JENKINS_SERVER, jobUrl);

        BufferedReader in =  new BufferedReader(new InputStreamReader(url.openStream()));
        XMLInputFactory inputFactory = XMLInputFactory.newFactory();
        XMLEventReader eventReader = inputFactory.createXMLEventReader(in);

        while (eventReader.hasNext()) {
            XMLEvent event = eventReader.nextEvent();

            if (event.isStartElement()) {
                StartElement startElement = event.asStartElement();                
                
                if (startElement.getName().getLocalPart().equals("build")) {
                    BuildDto build = getBuild(event, eventReader);
                }

//                if (event.asStartElement().getName().getLocalPart().equals("url")) {
//                    JenkinsJob tempjenkinsJob = new JenkinsJob(eventReader.getElementText());
//                    jenkinsJobs.add(tempjenkinsJob);
//
//                    event = eventReader.nextEvent();
////                    jenkinsJob = new JenkinsJob(event.asCharacters().getData());
////                    items.add(jenkinsJob);
//                    continue;
//                }

            }
            // If we reach the end of an testStatus element, we add it to the list
            if (event.isEndElement()) {
                EndElement endElement = event.asEndElement();
                if (endElement.getName().getLocalPart().equals("url")) {
//                    jenkinsJobs.add(tempjenkinsJob);
                }
            }

        }
    }
    
    private static BuildDto getBuild(XMLEvent event, XMLEventReader eventReader) throws XMLStreamException {
        BuildDto build = null;
        
        while (eventReader.hasNext()) {            
            event = eventReader.nextEvent();
            
            if (event.asStartElement().getName().getLocalPart().equals("number")) {
                build = new BuildDto();
                build.setName(event.asCharacters().getData());                
            }
            
            if (event.asStartElement().getName().getLocalPart().equals("url")) {
                build.setUrl(event.asCharacters().getData());
            }
        }
        return build;
    }
    public static void main(String[] args) throws LoginException, PrivilegedActionException, IOException {
        downloadResults("");
    }
}
