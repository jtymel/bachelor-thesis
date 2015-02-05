package gwtEntity.client;

import gwtEntity.server.Build;
import gwtEntity.server.Category;
import gwtEntity.server.Result;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 *
 * @author jtymel
 */
public class ParameterizedBuildDto implements Serializable {
//    private List<Result> results;
    private Long id;    
//    private List<Category> categories;
    private BuildDto id_build;
    private java.util.Date datetime;
    private String name;
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public BuildDto getId_build() {
        return id_build;
    }

    public void setId_build(BuildDto id_build) {
        this.id_build = id_build;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

//    public List<Result> getResults() {
//        return results;
//    }
//
//    public void setResults(List<Result> results) {
//        this.results = results;
//    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

//    public List<Category> getCategories() {
//        return categories;
//    }
//
//    public void setCategories(List<Category> categories) {
//        this.categories = categories;
//    }
//
//    public Build getBuild() {
//        return id_build;
//    }
//
//    public void setBuild(Build build) {
//        this.id_build = build;
//    }

    public Date getDatetime() {
        return datetime;
    }

    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }
    
    
    
}
