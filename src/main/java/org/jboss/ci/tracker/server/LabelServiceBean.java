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
import org.jboss.ci.tracker.server.entity.Category;
import org.jboss.ci.tracker.common.objects.CategoryDto;
import org.jboss.ci.tracker.common.objects.JobDto;
import org.jboss.ci.tracker.common.objects.LabelDto;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import org.hibernate.Session;
import org.jboss.ci.tracker.server.entity.LabelCategory;

/**
 *
 * @author jtymel
 */
@Stateless
@Transactional(Transactional.TxType.REQUIRED)
public class LabelServiceBean {

    @PersistenceContext(name = "MainPU")
    private EntityManager em;

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
        CategoryDto categoryDto = new CategoryDto(category.getId(), category.getName(), category.getRegex());
        categoryDto.setCategorization(category.getCategorization().getName());
        return categoryDto;
    }

    public void addCategoriesToLabel(LabelDto labelDto, List<CategoryDto> categoriesDto) {
        Label label = em.find(Label.class, labelDto.getId());

        List<LabelCategory> labelCategories = new ArrayList<LabelCategory>(categoriesDto.size());

        for (CategoryDto categoryDto : categoriesDto) {
            Category category = em.find(Category.class, categoryDto.getId());
            addLabelToCategory(label, category);
            final LabelCategory lc = new LabelCategory();
            lc.setCategory(category);
            lc.setLabel(label);
            labelCategories.add(lc);
        }

        label.setLabelCategories(labelCategories);

        em.persist(label);
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

        Label label = em.find(Label.class, labelDto.getId());

        List<LabelCategory> categories = label.getLabelCategories();
        List<CategoryDto> categoryDtos = new ArrayList<CategoryDto>(categories.size());

        for (LabelCategory lc : categories) {
            categoryDtos.add(createCategoryDto(lc.getCategory()));
        }

        return categoryDtos;
    }

}
