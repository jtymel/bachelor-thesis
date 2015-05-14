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

import org.jboss.ci.tracker.common.objects.BuildDto;
import org.jboss.ci.tracker.server.entity.Build;
import org.jboss.ci.tracker.server.entity.Category;
import org.jboss.ci.tracker.server.entity.Result;
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
