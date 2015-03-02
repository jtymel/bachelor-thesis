package gwtEntity.server;

import gwtEntity.client.CategoryDto;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 * Created by jtymel on 12/15/14.
 */
@Entity
public class Category implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    private String name;

    @ManyToOne
    private Categorization id_categorization;
      
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "categories")
    private List<Job> jobs;
    
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "categories")
    private List<ParameterizedBuild> parameterizedBuilds;
    
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "categories")
    private List<Label> labels = new ArrayList<Label>();

    public Category() {
    }
    
    public Category(CategoryDto categoryDto) {
        this.id = categoryDto.getId();
        this.name = categoryDto.getName();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Categorization getId_categorization() {
        return id_categorization;
    }

    public void setId_categorization(Categorization id_categorization) {
        this.id_categorization = id_categorization;
    }

    public List<Job> getJobs() {
        return jobs;
    }

    public void setJobs(List<Job> jobs) {
        this.jobs = jobs;
    }

    public List<ParameterizedBuild> getParameterizedBuilds() {
        return parameterizedBuilds;
    }

    public void setParameterizedBuilds(List<ParameterizedBuild> parameterizedBuilds) {
        this.parameterizedBuilds = parameterizedBuilds;
    }

    public List<Label> getLabels() {
        return labels;
    }

    public void setLabels(List<Label> labels) {
        this.labels = labels;
    }

    public void addLabel(Label label) {
        labels.add(label);
    }

    public void addJob(Job job) {
        jobs.add(job);
    }
}