package gwtEntity.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import gwtEntity.common.objects.CategorizationDto;
import gwtEntity.common.objects.CategoryDto;
import gwtEntity.common.services.CategoryService;
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
