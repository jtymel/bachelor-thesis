package gwtEntity.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import java.util.List;

/**
 *
 * @author jtymel
 */
@RemoteServiceRelativePath("jenkinsservice")
public interface JenkinsService extends RemoteService {
    public List<BuildDto> downloadBuilds(JobDto jobDto);    
}
