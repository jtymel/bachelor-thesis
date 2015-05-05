package gwtEntity.client.widgets;

import gwtEntity.client.CategoryDto;

/**
 *
 * @author jtymel
 */
public interface CategoryListDetailBridge {

    public void setCategoryAndDisplayDetail(CategoryDto categoryDto);

    public void cancelCategoryDetailAndDisplayCategoryList();

    public void cancelCategoryList();
}
