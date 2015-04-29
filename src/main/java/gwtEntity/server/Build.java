package gwtEntity.server;

import gwtEntity.client.BuildDto;
import java.io.Serializable;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 * Created by jtymel on 12/15/14.
 */

@Entity
public class Build implements Serializable {    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @OneToMany(mappedBy = "build")
    private List<ParameterizedBuild> parameterizedBuilds;    
    
    @ManyToOne
    private Job job;

    private String name;
    private String url;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<ParameterizedBuild> getParameterizedBuilds() {
        return parameterizedBuilds;
    }

    public void setParameterizedBuilds(List<ParameterizedBuild> parameterizedBuilds) {
        this.parameterizedBuilds = parameterizedBuilds;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
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

    public Build() {
    }

    public Build(Long id, List<ParameterizedBuild> parameterizedBuilds, Job job, String name, String url) {
        this.id = id;
        this.parameterizedBuilds = parameterizedBuilds;
        this.job = job;
        this.name = name;
        this.url = url;
    }
    
    public Build(BuildDto build) {
        this.id = build.getId();
//        this.parameterizedBuilds = build.getParameterizedBuilds();        
        Job aux = new Job(build.getJob());
        this.job = aux;
//        this.job = build.getJob();
        this.name = build.getName();
        this.url = build.getUrl();
    }
   
}
