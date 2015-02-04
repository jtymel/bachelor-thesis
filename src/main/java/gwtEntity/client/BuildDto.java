package gwtEntity.client;

import gwtEntity.server.Job;
import gwtEntity.server.ParameterizedBuild;
import java.io.Serializable;
import java.util.List;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 *
 * @author jtymel
 */
public class BuildDto implements Serializable{
    private Long id;        
    private List<ParameterizedBuildDto> parameterizedBuilds;            
    private JobDto job;
    private String name;
    private String url;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<ParameterizedBuildDto> getParameterizedBuilds() {
        return parameterizedBuilds;
    }

    public void setParameterizedBuilds(List<ParameterizedBuildDto> parameterizedBuilds) {
        this.parameterizedBuilds = parameterizedBuilds;
    }

    public JobDto getJob() {
        return job;
    }

    public void setJob(JobDto job) {
        this.job = job;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    
    
}
