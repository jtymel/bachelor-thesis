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
package gwtEntity.server.entity;

import gwtEntity.server.entity.Category;
import gwtEntity.server.entity.Build;
import gwtEntity.common.objects.JobDto;
import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

/**
 *
 * @author jtymel
 */
@Entity
public class Job implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "serial")
    private Long id;

    @OneToMany(mappedBy = "job")
    @Cascade({CascadeType.SAVE_UPDATE, CascadeType.DELETE})
    private List<Build> builds;

    @OneToMany(mappedBy = "job")
    @Cascade({CascadeType.SAVE_UPDATE, CascadeType.DELETE})
    private List<Label> labels;

//    org.hibernate.exception.SQLGrammarException: could not extract ResultSet
//    causing 'org.postgresql.util.PSQLException: ERROR: relation "testdb.job_category" does not exist', hence temporarily commented 
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "job_category", joinColumns = {
        @JoinColumn(name = "JOB_ID", nullable = false, updatable = false)},
            inverseJoinColumns = {
                @JoinColumn(name = "CATEGORY_ID",
                        nullable = false, updatable = false)})
    @Cascade({CascadeType.SAVE_UPDATE, CascadeType.DELETE})
    private List<Category> categories;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Job(JobDto jobDTO) {
        id = jobDTO.getId();
        name = jobDTO.getName();
        url = jobDTO.getUrl();
    }

    public Job(Long id, String name, String url) {
        this.id = id;
        this.name = name;
        this.url = url;
    }

    public Job() {
    }

    public Job(Long id) {
        this.id = id;
    }

    public List<Build> getBuilds() {
        return builds;
    }

    public void setBuilds(List<Build> builds) {
        this.builds = builds;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

}
