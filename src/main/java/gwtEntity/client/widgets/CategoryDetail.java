package gwtEntity.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import gwtEntity.client.CategorizationDto;
import gwtEntity.common.service.CategorizationService;
import gwtEntity.common.service.CategorizationServiceAsync;
import gwtEntity.client.CategoryDto;
import gwtEntity.common.service.CategoryService;
import gwtEntity.common.service.CategoryServiceAsync;
import gwtEntity.common.service.CategorizationServiceAsync;
import gwtEntity.common.service.CategoryServiceAsync;
import java.util.List;

/**
 *
 * @author jtymel
 */
public class CategoryDetail extends Composite {

    private static CategoryDetailUiBinder uiBinder = GWT.create(CategoryDetailUiBinder.class);

    private final CategoryServiceAsync categoryService = GWT.create(CategoryService.class);
    private final CategorizationServiceAsync categorizationService = GWT.create(CategorizationService.class);

    private CategoryListDetailBridge categoryListDetailBridge;

    interface CategoryDetailUiBinder extends UiBinder<Widget, CategoryDetail> {
    }

    @UiField
    TextBox categoryNameField;

    @UiField
    ListBox categorizationListField;

    @UiField
    Button saveButton;

    @UiField
    Button cancelButton;

    CategoryDto editedCategory = null;
    List<CategorizationDto> categorizations;

    void setCategory(CategoryDto categoryDto) {
        if (categoryDto != null) {
            categoryNameField.setText(categoryDto.getName());
        } else {
            categoryNameField.setText("");
        }

        editedCategory = categoryDto;
//        getCategorizations();
    }

    public CategoryDetail() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @UiHandler("saveButton")
    void onSaveButtonClick(ClickEvent event) {
        addCategory();
    }

    @UiHandler("cancelButton")
    void onCancelButtonClick(ClickEvent event) {
        categoryListDetailBridge.cancelCategoryDetailAndDisplayCategoryList();
    }

    @UiHandler("categoryNameField")
    void onKeyUp(KeyUpEvent event) {
        if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
            addCategory();
        }
    }

    public void setCategoryListDetailBridge(CategoryListDetailBridge bridge) {
        categoryListDetailBridge = bridge;
    }

    private void addCategory() {
        CategoryDto categoryDto;

        if (editedCategory == null) {
            categoryDto = new CategoryDto();
        } else {
            categoryDto = editedCategory;
            editedCategory = null;
        }

        categoryDto.setName(categoryNameField.getText());

        categoryService.saveCategory(categoryDto, categorizations.get(categorizationListField.getSelectedIndex()), new AsyncCallback<Long>() {

            @Override
            public void onFailure(Throwable caught) {
            }

            @Override
            public void onSuccess(Long result) {
                categoryListDetailBridge.cancelCategoryDetailAndDisplayCategoryList();
            }
        });

    }

    public void getCategorizations() {
        categorizationService.getCategorizations(new AsyncCallback<List<CategorizationDto>>() {

            @Override
            public void onFailure(Throwable caught) {
            }

            @Override
            public void onSuccess(List<CategorizationDto> result) {
                //        This needs to be reviewed and done better!
//                if (categorizations != null) categorizations.clear();

                categorizationListField.clear();

                categorizations = result;
                for (CategorizationDto categorization : categorizations) {
                    categorizationListField.addItem(categorization.getName());
                }
            }
        });

//        categorizationListField.clear();
//        for (CategorizationDto categorization : categorizations) {
//            categorizationListField.addItem(categorization.getName());
//        }
    }

}
