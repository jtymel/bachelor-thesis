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

import org.jboss.ci.tracker.server.entity.Category;
import org.jboss.ci.tracker.server.entity.Categorization;
import org.jboss.ci.tracker.common.objects.CategorizationDto;
import org.jboss.ci.tracker.common.objects.CategoryDto;
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
public class CategoryServiceBean {

    @PersistenceContext(name = "MainPU")
    private EntityManager em;

    public List<CategoryDto> getCategories() {
        Session session = (Session) em.getDelegate();

        List<Category> categories = new ArrayList<Category>(session.createQuery("FROM Category ORDER BY categorization.name, name").list());
        List<CategoryDto> categoryDtos = new ArrayList<CategoryDto>(categories != null ? categories.size() : 0);

        for (Category category : categories) {
            categoryDtos.add(createCategoryDto(category));
        }

        return categoryDtos;
    }

    private CategoryDto createCategoryDto(Category category) {
        CategoryDto categoryDto = new CategoryDto(category.getId(), category.getName());
        categoryDto.setCategorization(category.getCategorization().getName());
        categoryDto.setCategorizationId(category.getCategorization().getId());
        return categoryDto;
    }

    public Long saveCategory(CategoryDto categoryDto, CategorizationDto categorizationDto) {
        Session session = (Session) em.getDelegate();
        Category category = new Category(categoryDto);
        Categorization categorization = new Categorization(categorizationDto);
        category.setCategorization(categorization);

        session.saveOrUpdate(category);

        return category.getId();
    }

    public void deleteCategory(CategoryDto categoryDto) {
        Category category = new Category(categoryDto);
        em.remove(em.contains(category) ? category : em.merge(category));
    }

    public Category getCategoryById(Long id) {
        Session session = (Session) em.getDelegate();
        Query query = session.createQuery("FROM Category WHERE id = :categoryId")
                .setParameter("categoryId", id);
        return (Category) query.uniqueResult();
    }
}
