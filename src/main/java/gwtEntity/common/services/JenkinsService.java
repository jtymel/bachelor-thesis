package gwtEntity.common.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import gwtEntity.common.objects.BuildDto;
import gwtEntity.common.objects.JobDto;
import gwtEntity.common.objects.ParameterizedBuildDto;
import java.util.List;

/**
 *
 * @author jtymel
 */
@RemoteServiceRelativePath("jenkinsservice")
public interface JenkinsService extends RemoteService {
    public void downloadBuilds(List<JobDto> jobs);
    public List<BuildDto> downloadBuilds(JobDto jobDto);
    public List<ParameterizedBuildDto> downloadParameterizedBuilds(BuildDto buildDto);
}
