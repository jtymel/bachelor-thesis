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
package org.jboss.ci.tracker.server;

import org.jboss.ci.tracker.server.entity.Label;
import org.jboss.ci.tracker.server.entity.Job;
import org.jboss.ci.tracker.server.entity.Category;
import org.jboss.ci.tracker.common.objects.CategorizationDto;
import org.jboss.ci.tracker.common.objects.CategoryDto;
import org.jboss.ci.tracker.common.objects.JobDto;
import org.jboss.ci.tracker.common.objects.LabelDto;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 *
 * @author jtymel
 */
@Stateless
@Transactional(Transactional.TxType.REQUIRED)
public class LabelServiceBean {

    @PersistenceContext(name = "MainPU")
    private EntityManager em;

    public List<LabelDto> getLabels() {
        Session session = (Session) em.getDelegate();

        List<Label> labels = new ArrayList<Label>(session.createQuery("from Label").list());
        List<LabelDto> labelDtos = new ArrayList<LabelDto>(labels != null ? labels.size() : 0);

        for (Label label : labels) {
            labelDtos.add(createLabelDto(label));
        }

        return labelDtos;
    }

    public List<LabelDto> getLabels(JobDto job) {
        if (job == null) {
            return null;
        }

        Session session = (Session) em.getDelegate();

        List<Label> labels = new ArrayList<Label>(session.createQuery("FROM Label WHERE job_id = :jobID").setParameter("jobID", job.getId()).list());
        List<LabelDto> labelDtos = new ArrayList<LabelDto>(labels != null ? labels.size() : 0);

        for (Label label : labels) {
            labelDtos.add(createLabelDto(label));
        }

        return labelDtos;
    }

    private LabelDto createLabelDto(Label label) {
        return new LabelDto(label.getId(), label.getName());
    }

    private CategoryDto createCategoryDto(Category category) {
        CategoryDto categoryDto = new CategoryDto(category.getId(), category.getName());
        categoryDto.setCategorization(category.getCategorization().getName());
        return categoryDto;
    }

    public Long saveLabel(LabelDto labelDto, JobDto jobDto) {
        Session session = (Session) em.getDelegate();
        Label label = new Label(labelDto);

        Query query = session.createQuery("FROM Job WHERE url = :jobUrl")
                .setParameter("jobUrl", jobDto.getUrl());
// !!!!!!! NPE !!!!!!!!!!
        Job job = (Job) query.uniqueResult();
        label.setJob(job);

        session.saveOrUpdate(label);

        return label.getId();
    }

    public Long saveLabel(Label label) {
        Session session = (Session) em.getDelegate();

        Query query = session.createQuery("from Label WHERE name = :labelName")
                .setParameter("labelName", label.getName());

        Label storedLabel = (Label) query.uniqueResult();

        if (storedLabel == null) {
            session.saveOrUpdate(label);
            return label.getId();
        }

        return storedLabel.getId();
    }

    public void deleteLabel(LabelDto labelDto) {
        Session session = (Session) em.getDelegate();

        Label label = new Label(labelDto);
        em.remove(em.contains(label) ? label : em.merge(label));
    }

    public void addCategoriesToLabel(LabelDto labelDto, List<CategoryDto> categoriesDto) {
        Session session = (Session) em.getDelegate();
// No label is in the table
        Query query = session.createQuery("from Label WHERE id = :labelid")
                .setParameter("labelid", labelDto.getId());
        Label label = (Label) query.uniqueResult();

        List<Category> categories = new ArrayList<Category>(categoriesDto.size());

        for (CategoryDto categoryDto : categoriesDto) {
            Query query2 = session.createQuery("from Category WHERE id = :categoryid")
                    .setParameter("categoryid", categoryDto.getId());
            Category category = (Category) query2.uniqueResult();
            addLabelToCategory(label, category);
            categories.add(category);
        }

        label.setCategories(categories);

        session.saveOrUpdate(label);
    }

    private void addLabelToCategory(Label label, Category category) {
        Session session = (Session) em.getDelegate();

        category.addLabel(label);
        session.saveOrUpdate(category);
    }

    public List<CategoryDto> getCategoriesOfLabel(LabelDto labelDto) {
        if (labelDto == null) {
            return null;
        }

        Session session = (Session) em.getDelegate();

        Query query = session.createQuery("from Label WHERE id = :labelid")
                .setParameter("labelid", labelDto.getId());
        Label label = (Label) query.uniqueResult();

        List<Category> categories = label.getCategories();
        List<CategoryDto> categoryDtos = new ArrayList<CategoryDto>(categories.size());

        for (Category category : categories) {
            categoryDtos.add(createCategoryDto(category));
        }

        return categoryDtos;
    }

}