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

import org.jboss.ci.tracker.common.objects.LabelDto;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * Created by jtymel on 12/15/14.
 */
@Entity
@Table(uniqueConstraints={@UniqueConstraint(columnNames = {"name" , "job_id"})})
@OnDelete(action = OnDeleteAction.CASCADE)
public class Label implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "serial")
    private Integer id;

    private String name;

    @ManyToOne(cascade = { CascadeType.REFRESH, CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST })
    private Job job;

    @OneToMany(cascade = { CascadeType.REFRESH, CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST }, fetch = FetchType.LAZY, mappedBy = "label")
    @OnDelete(action=OnDeleteAction.CASCADE)
    private List<LabelCategory> labelCategories = new ArrayList<LabelCategory>();

    public Label() {
    }

    public Label(LabelDto labelDto) {
        this.id = labelDto.getId();
        this.name = labelDto.getName();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public List<LabelCategory> getLabelCategories() {
        return labelCategories;
    }

    public void setLabelCategories(List<LabelCategory> labelCategories) {
        this.labelCategories = labelCategories;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
