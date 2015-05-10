package gwtEntity.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import gwtEntity.client.CategorizationDto;
import gwtEntity.client.CategoryDto;
import gwtEntity.common.service.CategoryService;
import java.util.List;
import javax.ejb.EJB;

/**
 *
 * @author jtymel
 */
public class CategoryServiceImpl extends RemoteServiceServlet implements CategoryService {
    @EJB
    private CategoryServiceBean categoryServiceBean;

    @Override
    public List<CategoryDto> getCategories() {
        return categoryServiceBean.getCategories();
    }

    @Override
    public Long saveCategory(CategoryDto category) {
        return categoryServiceBean.saveCategory(category);
    }

    @Override
    public void deleteCategory(CategoryDto category) {
        categoryServiceBean.deleteCategory(category);
    }

    @Override
    public Long saveCategory(CategoryDto category, CategorizationDto categorizationDto) {
        return categoryServiceBean.saveCategory(category, categorizationDto);
    }

}
