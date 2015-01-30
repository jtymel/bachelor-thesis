package gwtEntity.server;

import gwtEntity.client.ParameterizedBuildDto;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Created by jtymel on 12/15/14.
 */
@Entity
public class ParameterizedBuild implements Serializable {
    @OneToMany(mappedBy = "id_parameterizedBuild")
    private List<Result> results;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinTable(name = "paramBuild_category", catalog = "testdb", joinColumns = { 
			@JoinColumn(name = "JOB_ID", nullable = false, updatable = false) }, 
			inverseJoinColumns = { @JoinColumn(name = "CATEGORY_ID", 
					nullable = false, updatable = false) })
    private List<Category> categories;

    @ManyToOne
    private Build id_build;

    private String machine;
    
    @Column()
    @Temporal(TemporalType.TIMESTAMP)
    private java.util.Date datetime;
    private String name;

    public Build getId_build() {
        return id_build;
    }

    public void setId_build(Build id_build) {
        this.id_build = id_build;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public Build getBuild() {
        return id_build;
    }

    public void setBuild(Build build) {
        this.id_build = build;
    }

    public String getMachine() {
        return machine;
    }

    public void setMachine(String machine) {
        this.machine = machine;
    }

    public Date getDatetime() {
        return datetime;
    }

    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }

    public ParameterizedBuild() {
    }

    public ParameterizedBuild(List<Result> results, Long id, List<Category> categories, Build id_build, String machine, Date datetime, String name) {
        this.results = results;
        this.id = id;
        this.categories = categories;
        this.id_build = id_build;
        this.machine = machine;
        this.datetime = datetime;
        this.name = name;
    }
    
    public ParameterizedBuild(ParameterizedBuildDto build) {        
        this.id = build.getId();
        this.datetime = build.getDatetime();
        this.name = build.getName();
        
        Build aux = new Build(build.getId_build());
        this.id_build = aux;
    }
       
}
