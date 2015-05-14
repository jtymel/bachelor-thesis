package gwtEntity.server.entity;

import gwtEntity.server.entity.Categorization;
import gwtEntity.common.objects.CategoryDto;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
    private Categorization categorization;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "categories")
    private List<Job> jobs;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "categories")
    private Set<ParameterizedBuild> parameterizedBuilds = new HashSet<ParameterizedBuild>();

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

    public Categorization getCategorization() {
        return categorization;
    }

    public void setCategorization(Categorization categorization) {
        this.categorization = categorization;
    }

    public List<Job> getJobs() {
        return jobs;
    }

    public void setJobs(List<Job> jobs) {
        this.jobs = jobs;
    }

    public Set<ParameterizedBuild> getParameterizedBuilds() {
        return parameterizedBuilds;
    }

    public void setParameterizedBuilds(Set<ParameterizedBuild> parameterizedBuilds) {
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

    public void addParamBuild(ParameterizedBuild pb) {
        parameterizedBuilds.add(pb);
    }
}
