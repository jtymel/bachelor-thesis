package gwtEntity.server;

import gwtEntity.server.entity.Category;
import gwtEntity.server.entity.Categorization;
import gwtEntity.client.CategorizationDto;
import gwtEntity.client.CategoryDto;
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

        List<Category> categories = new ArrayList<Category>(session.createQuery("from Category").list());
        List<CategoryDto> categoryDtos = new ArrayList<CategoryDto>(categories != null ? categories.size() : 0);

        for (Category category : categories) {
            categoryDtos.add(createCategoryDto(category));
        }

        return categoryDtos;
    }
    
    private CategoryDto createCategoryDto(Category category) {
        CategoryDto categoryDto = new CategoryDto(category.getId(), category.getName());
        categoryDto.setCategorization(category.getCategorization().getName());
        return categoryDto;
    }
    
    public Long saveCategory(CategoryDto categoryDto) {
        Session session = (Session) em.getDelegate();
        Category category = new Category(categoryDto);
        
        session.saveOrUpdate(category);

        return category.getId();
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
        Session session = (Session) em.getDelegate();
        
        Category category = new Category(categoryDto);
        em.remove(em.contains(category) ? category : em.merge(category));
    }
}
