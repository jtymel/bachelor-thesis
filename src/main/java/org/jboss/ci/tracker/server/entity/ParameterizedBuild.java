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

import org.jboss.ci.tracker.common.objects.ParameterizedBuildDto;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * Created by jtymel on 12/15/14.
 */
@NamedNativeQueries({
    @NamedNativeQuery(
            name = "addCategoriesToParamBuild",
            query = "SELECT count (*) FROM addCategoriesToParamBuild(:id_paramBuild)"
    )
})
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "build_id", "cachedLabel" } ) )
public class ParameterizedBuild implements Serializable {

    @OneToMany(cascade = { CascadeType.REFRESH, CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST }, mappedBy = "parameterizedBuild")
    @Cascade({org.hibernate.annotations.CascadeType.REFRESH})
    @OnDelete(action=OnDeleteAction.CASCADE)
    private List<Result> results;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "serial")
    private Integer id;

    @OneToMany(cascade = { CascadeType.REFRESH, CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST }, mappedBy = "parameterizedBuild")
    @Cascade({org.hibernate.annotations.CascadeType.REFRESH})
    @OnDelete(action=OnDeleteAction.CASCADE)
    private Set<ParameterizedBuildCategory> parameterizedBuildCategories;

    @ManyToOne(cascade = { CascadeType.REFRESH, CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST })
    private Build build;

    private String machine;

    @Column()
    @Temporal(TemporalType.TIMESTAMP)
    private java.util.Date datetime;
    private String name;
    private String url;
    private String cachedLabel;

    public String getLabel() {
        return cachedLabel;
    }

    public void setLabel(String label) {
        this.cachedLabel = label;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Build getBuild() {
        return build;
    }

    public void setBuild(Build build) {
        this.build = build;
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Set<ParameterizedBuildCategory> getParameterizedBuildCategories() {
        return parameterizedBuildCategories;
    }

    public void setParameterizedBuildCategories(Set<ParameterizedBuildCategory> parameterizedBuildCategories) {
        this.parameterizedBuildCategories = parameterizedBuildCategories;
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

    public ParameterizedBuild(ParameterizedBuildDto build) {
        this.id = build.getId();
        this.datetime = build.getDatetime();
        this.name = build.getName();
        this.url = build.getUrl();
    }

    @Override
    public String toString() {
        return "ParameterizedBuild{" + "name=" + name + ", url=" + url + ", cachedLabel=" + cachedLabel + '}';
    }

}
