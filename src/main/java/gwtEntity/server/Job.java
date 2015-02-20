/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2014, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package gwtEntity.server;

import gwtEntity.client.JobDto;
import java.io.Serializable;
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
import javax.persistence.OneToMany;



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
    private List<Build> builds;
  
//    org.hibernate.exception.SQLGrammarException: could not extract ResultSet
//    causing 'org.postgresql.util.PSQLException: ERROR: relation "testdb.job_category" does not exist', hence temporarily commented 
    
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinTable(name = "job_category", joinColumns = { 
			@JoinColumn(name = "JOB_ID", nullable = false, updatable = false) }, 
			inverseJoinColumns = { @JoinColumn(name = "CATEGORY_ID", 
					nullable = false, updatable = false) })
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

}
