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

import org.jboss.ci.tracker.common.objects.CategoryDto;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * Created by jtymel on 12/15/14.
 */
@Entity
public class Category implements Serializable {

    private static final Logger LOG = Logger.getLogger(Category.class.getName());

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "serial")
    private Integer id;

    private String name;

    /**
     * Regular expression that identifies given category in the parameterized build label.
     */
    private String regex;

    @ManyToOne(cascade = { CascadeType.REFRESH, CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST })
    private Categorization categorization;

    @OneToMany(cascade = { CascadeType.REFRESH, CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST }, fetch = FetchType.LAZY, mappedBy = "category")
    @OnDelete(action=OnDeleteAction.CASCADE)
    private List<JobCategory> jobCategories;

    @OneToMany(cascade = { CascadeType.REFRESH, CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST }, fetch = FetchType.LAZY, mappedBy = "category")
    @OnDelete(action=OnDeleteAction.CASCADE)
    private Set<ParameterizedBuildCategory> parameterizedBuildCategories = new HashSet<ParameterizedBuildCategory>();

    @OneToMany(cascade = { CascadeType.REFRESH, CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST }, fetch = FetchType.LAZY, mappedBy = "category")
    @OnDelete(action=OnDeleteAction.CASCADE)
    private List<LabelCategory> labelCategories = new ArrayList<LabelCategory>();

    public Category() {
    }

    public Category(CategoryDto categoryDto) {
        this.id = categoryDto.getId();
        this.name = categoryDto.getName();
        this.regex = categoryDto.getRegex();
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

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public Categorization getCategorization() {
        return categorization;
    }

    public void setCategorization(Categorization categorization) {
        this.categorization = categorization;
    }

    public List<JobCategory> getJobCategories() {
        return jobCategories;
    }

    public void setJobCategories(List<JobCategory> jobCategories) {
        this.jobCategories = jobCategories;
    }

    public Set<ParameterizedBuildCategory> getParameterizedBuildCategories() {
        return parameterizedBuildCategories;
    }

    public void setParameterizedBuildCategories(Set<ParameterizedBuildCategory> parameterizedBuildCategories) {
        this.parameterizedBuildCategories = parameterizedBuildCategories;
    }

    public List<LabelCategory> getLabelCategories() {
        return labelCategories;
    }

    public void setLabelCategories(List<LabelCategory> labelCategories) {
        this.labelCategories = labelCategories;
    }

    public void addLabel(Label label) {
        LabelCategory lc = new LabelCategory();
        lc.setCategory(this);
        lc.setLabel(label);
        labelCategories.add(lc);
    }

    public void addJob(Job job) {
        JobCategory jc = new JobCategory();
        jc.setCategory(this);
        jc.setJob(job);
        jobCategories.add(jc);
    }

    public void addParamBuild(ParameterizedBuild pb) {
        ParameterizedBuildCategory pbc = new ParameterizedBuildCategory();
        pbc.setCategory(this);
        pbc.setParameterizedBuild(pb);
        parameterizedBuildCategories.add(pbc);
    }

    /**
     * Returns {@code true} if the label matches the regular expression
     * @return
     */
    public boolean isInLabel(String label) {
        if (label == null || this.regex == null) {
            return false;
        }

        try {
            final Pattern regexPattern = Pattern.compile(regex);
            LOG.log(Level.INFO, "Checking category '{0}' regex: {1}, res: {2}", new Object[] { getName(), regex, regexPattern.matcher(label).find() });
            return regexPattern.matcher(label).find();
        } catch (PatternSyntaxException ex) {
            LOG.log(Level.WARNING, "Invalid category '{0}' regex: {1}", new Object[] { getName(), regex });
        }

        return false;
    }
}
