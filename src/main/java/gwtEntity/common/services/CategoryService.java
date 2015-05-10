package gwtEntity.common.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import gwtEntity.common.objects.CategorizationDto;
import gwtEntity.common.objects.CategoryDto;
import java.util.List;


/**
 *
 * @author jtymel
 */

@RemoteServiceRelativePath("categoryservice")
public interface CategoryService extends RemoteService {
    public List<CategoryDto> getCategories();
    public Long saveCategory(CategoryDto category);
    public Long saveCategory(CategoryDto category, CategorizationDto categorizationDto);
    public void deleteCategory(CategoryDto category);
}
