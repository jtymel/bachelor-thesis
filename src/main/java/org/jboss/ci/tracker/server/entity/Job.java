/*
 * Copyright (C) 2015 Jan Tymel
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.jboss.ci.tracker.server.entity;

import org.jboss.ci.tracker.common.objects.JobDto;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 *
 * @author jtymel
 */
@Entity
public class Job implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "serial")
    private Integer id;

    @OneToMany(cascade = { javax.persistence.CascadeType.REFRESH, javax.persistence.CascadeType.DETACH, javax.persistence.CascadeType.MERGE, javax.persistence.CascadeType.REFRESH, javax.persistence.CascadeType.PERSIST }, mappedBy = "job")
    @Cascade({CascadeType.REFRESH})
    @OnDelete(action=OnDeleteAction.CASCADE)
    private List<Build> builds = new ArrayList<Build>();

    @OneToMany(cascade = { javax.persistence.CascadeType.REFRESH, javax.persistence.CascadeType.DETACH, javax.persistence.CascadeType.MERGE, javax.persistence.CascadeType.REFRESH, javax.persistence.CascadeType.PERSIST }, mappedBy = "job")
    @Cascade({CascadeType.REFRESH})
    @OnDelete(action=OnDeleteAction.CASCADE)
    private List<Label> labels = new ArrayList<Label>();

    @OneToMany(cascade = { javax.persistence.CascadeType.REFRESH, javax.persistence.CascadeType.DETACH, javax.persistence.CascadeType.MERGE, javax.persistence.CascadeType.REFRESH, javax.persistence.CascadeType.PERSIST }, mappedBy = "job")
    @Cascade({CascadeType.REFRESH})
    @OnDelete(action=OnDeleteAction.CASCADE)
    private List<JobCategory> jobCategories = new ArrayList<JobCategory>();

    private String name;
    private String url;

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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Job(JobDto jobDTO) {
        id = jobDTO.getId();
        name = jobDTO.getName();
        url = jobDTO.getUrl();
    }

    public Job(Integer id, String name, String url) {
        this.id = id;
        this.name = name;
        this.url = url;
    }

    public Job() {
    }

    public Job(Integer id) {
        this.id = id;
    }

    public List<Build> getBuilds() {
        return builds;
    }

    public void setBuilds(List<Build> builds) {
        this.builds = builds;
    }

    public void addCategory(Category category) {
        JobCategory jc = new JobCategory();
        jc.setCategory(category);
        jc.setJob(this);
        jobCategories.add(jc);
    }

    public List<JobCategory> getJobCategories() {
        return jobCategories;
    }

    public void setJobCategories(List<JobCategory> jobCategories) {
        this.jobCategories = jobCategories;
    }

    public List<Label> getLabels() {
        return labels;
    }

    public void setLabels(List<Label> labels) {
        this.labels = labels;
    }

}
