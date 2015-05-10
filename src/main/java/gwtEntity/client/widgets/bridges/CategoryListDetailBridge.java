package gwtEntity.client.widgets.bridges;

import gwtEntity.common.objects.CategoryDto;

/**
 *
 * @author jtymel
 */
public interface CategoryListDetailBridge {

    public void setCategoryAndDisplayDetail(CategoryDto categoryDto);

    public void cancelCategoryDetailAndDisplayCategoryList();

    public void cancelCategoryList();
}
