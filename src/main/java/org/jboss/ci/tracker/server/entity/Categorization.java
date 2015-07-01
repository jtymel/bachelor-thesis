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

import org.jboss.ci.tracker.common.objects.CategorizationDto;
import java.io.Serializable;
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
 * Created by jtymel on 12/15/14.
 */
@Entity
public class Categorization implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "serial")
    private Integer id;

    @OneToMany(cascade = { javax.persistence.CascadeType.REFRESH, javax.persistence.CascadeType.DETACH, javax.persistence.CascadeType.MERGE, javax.persistence.CascadeType.REFRESH, javax.persistence.CascadeType.PERSIST }, mappedBy = "categorization")
    @Cascade({CascadeType.REFRESH})
    @OnDelete(action=OnDeleteAction.CASCADE)
    private List<Category> categories;

    private String name;

    public Categorization() {
    }

    public Categorization(CategorizationDto categorizationDto) {
        this.id = categorizationDto.getId();
        this.name = categorizationDto.getName();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
