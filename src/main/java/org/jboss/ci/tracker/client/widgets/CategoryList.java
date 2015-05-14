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
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;
import org.jboss.ci.tracker.common.objects.CategoryDto;
import org.jboss.ci.tracker.common.services.CategoryService;
import org.jboss.ci.tracker.common.services.CategoryServiceAsync;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jtymel
 */
public class CategoryList extends Composite {

    private static CategoryListUiBinder uiBinder = GWT.create(CategoryListUiBinder.class);

    private final CategoryServiceAsync categoryService = GWT.create(CategoryService.class);

    private CategoryListDetailBridge categoryListDetailBridge;

    interface CategoryListUiBinder extends UiBinder<Widget, CategoryList> {
    }

    @UiField(provided = true)
    DataGrid<CategoryDto> dataGrid;

    @UiField(provided = true)
    SimplePager pager;

    @UiField
    Button deleteButton;

    @UiField
    Button addButton;

    @UiField
    Button cancelButton;

    private SelectionModel<CategoryDto> selectionModel;
    private ListDataProvider<CategoryDto> dataProvider;

    public CategoryList() {
        dataGrid = new DataGrid<CategoryDto>(500);
        initDatagrid();
        initPager();
        initWidget(uiBinder.createAndBindUi(this));
    }

    public void setCategoryListDetailBridge(CategoryListDetailBridge bridge) {
        categoryListDetailBridge = bridge;
    }

    @UiHandler("deleteButton")
    void onDeleteButtonClick(ClickEvent event) {
        List<CategoryDto> categoryList = getSelectedCategories();

        for (CategoryDto categorizationDto : categoryList) {
            if (selectionModel.isSelected(categorizationDto)) {
                deleteCategory(categorizationDto);
            }
        }

    }

    @UiHandler("addButton")
    void onAddButtonClick(ClickEvent event) {
        categoryListDetailBridge.setCategoryAndDisplayDetail(null);
    }

    @UiHandler("cancelButton")
    void onCancelButtonClick(ClickEvent event) {
        categoryListDetailBridge.cancelCategoryList();
    }

    private void deleteCategory(CategoryDto categoryDto) {
        categoryService.deleteCategory(categoryDto, new AsyncCallback<Void>() {

            @Override
            public void onFailure(Throwable caught) {
            }

            @Override
            public void onSuccess(Void result) {
                updateDataGrid();
            }
        });
    }

    public void onTabShow() {
        updateDataGrid();
    }

    private void initDatagrid() {

        TextColumn<CategoryDto> nameColumn = new TextColumn<CategoryDto>() {
            @Override
            public String getValue(CategoryDto object) {
                return object.getName();
            }
        };

        TextColumn<CategoryDto> categorizationColumn = new TextColumn<CategoryDto>() {
            @Override
            public String getValue(CategoryDto object) {
                return object.getCategorization();
            }
        };

        dataGrid.setColumnWidth(nameColumn, 40, Style.Unit.PX);
        dataGrid.addColumn(nameColumn, "Name");

        dataGrid.setColumnWidth(categorizationColumn, 40, Style.Unit.PX);
        dataGrid.addColumn(categorizationColumn, "Categorization");

        selectionModel = new SingleSelectionModel<CategoryDto>(keyProvider);

        dataGrid.addDomHandler(new DoubleClickHandler() {

            @Override
            public void onDoubleClick(DoubleClickEvent event) {
                List<CategoryDto> categoryList = getSelectedCategories();

                for (CategoryDto categoryDto : categoryList) {
                    categoryListDetailBridge.setCategoryAndDisplayDetail(categoryDto);
                }
            }
        }, DoubleClickEvent.getType());

        dataGrid.setSelectionModel(selectionModel);
        updateDataGrid();
    }

    private void initPager() {
        SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
        pager = new SimplePager(SimplePager.TextLocation.CENTER, pagerResources, false, 0, true);
        pager.setDisplay(dataGrid);
    }

    private void updateDataGrid() {
        categoryService.getCategories(new AsyncCallback<List<CategoryDto>>() {

            @Override
            public void onFailure(Throwable caught) {
            }

            @Override
            public void onSuccess(List<CategoryDto> result) {
                dataProvider = new ListDataProvider<CategoryDto>();
                dataProvider.setList(result);
                dataProvider.addDataDisplay(dataGrid);
                dataGrid.setRowCount(result.size());
            }
        });

    }

    ProvidesKey<CategoryDto> keyProvider = new ProvidesKey<CategoryDto>() {
        @Override
        public Object getKey(CategoryDto category) {
            return (category == null) ? null : category.getId();
        }
    };

    public List<CategoryDto> getSelectedCategories() {
        List<CategoryDto> categoryList = (List<CategoryDto>) dataProvider.getList();
        List<CategoryDto> selectedCategories = new ArrayList<CategoryDto>();

        Long i = 0L;

        for (CategoryDto categoryDto : categoryList) {
            if (selectionModel.isSelected(categoryDto)) {
                selectedCategories.add(categoryDto);
            }
            i++;
        }

        return selectedCategories;
    }
}
