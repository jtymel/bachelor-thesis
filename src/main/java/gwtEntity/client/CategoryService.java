package gwtEntity.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
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
