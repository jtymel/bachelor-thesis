package gwtEntity.common.objects;

import gwtEntity.common.objects.BuildDto;
import gwtEntity.server.entity.Build;
import gwtEntity.server.entity.Category;
import gwtEntity.server.entity.Result;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 *
 * @author jtymel
 */
public class ParameterizedBuildDto implements Serializable {
    private Long id;    
    private BuildDto build;
    private java.util.Date datetime;
    private String name;
    private String url;
    private String machine;    

    public String getMachine() {
        return machine;
    }

    public void setMachine(String machine) {
        this.machine = machine;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public BuildDto getBuild() {
        return build;
    }

    public void setBuild(BuildDto build) {
        this.build = build;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public Date getDatetime() {
        return datetime;
    }

    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }
    
    
    
}
