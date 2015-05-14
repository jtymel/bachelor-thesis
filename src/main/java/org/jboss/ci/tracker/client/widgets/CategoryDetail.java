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
package org.jboss.ci.tracker.client.widgets;

import org.jboss.ci.tracker.client.widgets.bridges.CategoryListDetailBridge;
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
import org.jboss.ci.tracker.common.objects.CategorizationDto;
import org.jboss.ci.tracker.common.services.CategorizationService;
import org.jboss.ci.tracker.common.objects.CategoryDto;
import org.jboss.ci.tracker.common.services.CategoryService;
import org.jboss.ci.tracker.common.services.CategorizationServiceAsync;
import org.jboss.ci.tracker.common.services.CategoryServiceAsync;
import java.util.List;

/**
 *
 * @author jtymel
 */
public class CategoryDetail extends Composite {

    private static final CategoryDetailUiBinder uiBinder = GWT.create(CategoryDetailUiBinder.class);

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

                categorizationListField.clear();

                categorizations = result;
                for (CategorizationDto categorization : categorizations) {
                    categorizationListField.addItem(categorization.getName());
                }
            }
        });

    }

}
