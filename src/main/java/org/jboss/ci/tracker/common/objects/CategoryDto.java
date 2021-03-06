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
package org.jboss.ci.tracker.common.objects;

import java.io.Serializable;

/**
 *
 * @author jtymel
 */
public class CategoryDto implements Serializable {

    private Integer id;
    private String name;
    private String regex;
    private String categorization;
    private Integer categorizationId;

    public Integer getCategorizationId() {
        return categorizationId;
    }

    public void setCategorizationId(Integer categorizationId) {
        this.categorizationId = categorizationId;
    }

    public CategoryDto() {
    }

    public CategoryDto(Integer id, String name, String regex) {
        this.id = id;
        this.name = name;
        this.regex = regex;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategorization() {
        return categorization;
    }

    public void setCategorization(String categorization) {
        this.categorization = categorization;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    @Override
    public String toString() {
        return "[" + id + "] " + name + "(regex = " + regex + ", group of parameterization = " + categorization + ")";
    }

}
