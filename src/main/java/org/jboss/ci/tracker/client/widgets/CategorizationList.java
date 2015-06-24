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

import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import org.jboss.ci.tracker.client.widgets.bridges.CategorizationListDetailBridge;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;
import org.jboss.ci.tracker.common.objects.CategorizationDto;
import org.jboss.ci.tracker.common.services.CategorizationService;
import org.jboss.ci.tracker.common.services.CategorizationServiceAsync;
import java.util.ArrayList;
import java.util.List;
import org.jboss.ci.tracker.common.objects.CategoryDto;
import org.jboss.ci.tracker.common.services.CategoryService;
import org.jboss.ci.tracker.common.services.CategoryServiceAsync;

/**
 *
 * @author jtymel
 */

public class CategorizationList extends Composite {

    private static final String LABEL_OF_NEW_ROW = "...";

    private static final CategorizationListUiBinder uiBinder = GWT.create(CategorizationListUiBinder.class);

    private final CategorizationServiceAsync categorizationService = GWT.create(CategorizationService.class);

    private final CategoryServiceAsync categoryService = GWT.create(CategoryService.class);

    private CategorizationListDetailBridge categorizationListDetailBridge;

    interface CategorizationListUiBinder extends UiBinder<Widget, CategorizationList> {
    }

    @UiField(provided = true)
    DataGrid<CategorizationDto> categorizationDataGrid;

    @UiField(provided = true)
    DataGrid<CategoryDto> categoryDataGrid;

    @UiField
    Button deleteCategorizationButton;

    @UiField
    Button deleteCategoryButton;

    @UiField
    Button cancelButton;

    private SelectionModel<CategorizationDto> selectionModel;
    private SelectionModel<CategoryDto> categorySelectionModel;
    private ListDataProvider<CategorizationDto> categorizationDataProvider;
    private ListDataProvider<CategoryDto> categoryDataProvider;

    public CategorizationList() {
        categorizationDataGrid = new DataGrid<CategorizationDto>(500);
        categoryDataGrid = new DataGrid<CategoryDto>(500);
        initCategorizationDatagrid();
        initCategoryDataGrid();
        initWidget(uiBinder.createAndBindUi(this));
    }

    public void setCategorizationListDetailBridge(CategorizationListDetailBridge bridge) {
        categorizationListDetailBridge = bridge;
    }

    // TODO: Deletion when the Del key is pressed
    @UiHandler("deleteCategorizationButton")
    void ondeleteCategorizationButtonClick(ClickEvent event) {
        CategorizationDto categorization = getSelectedCategorization();
        if (categorization != null) {
            deleteCategorization(categorization);
        }
    }

    // TODO: Deletion when the Del key is pressed
    @UiHandler("deleteCategoryButton")
    void ondeleteCategoryButtonClick(ClickEvent event) {
        CategoryDto category = getSelectedCategory();
        if (category != null) {
            deleteCategory(category);
        }
    }

    @UiHandler("cancelButton")
    void onCancelButtonClick(ClickEvent event) {
        categorizationListDetailBridge.cancelCategorizationList();
    }

    public void onTabShow() {
        updateCategorizationDataGrid();
    }

//  ----------------- Categorizations --------------------
    /**
     * Deletes categorization.
     *
     * @param categorizationDto categorization to be deleted
     */
    private void deleteCategorization(final CategorizationDto categorizationDto) {
        categorizationService.deleteCategorization(categorizationDto, new AsyncCallback<Void>() {

            @Override
            public void onFailure(Throwable caught) {
                throw new RuntimeException("Can't delete group of parameterization " + categorizationDto, caught);
            }

            @Override
            public void onSuccess(Void result) {
                selectionModel.setSelected(categorizationDto, false);
                updateCategorizationDataGrid();
                updateCategoryDataGrid();
            }
        });
    }

    /**
     * Initializes categorization data grid.
     */
    private void initCategorizationDatagrid() {
        setCategorizationNameColumn();

        selectionModel = new SingleSelectionModel<CategorizationDto>(keyProvider);
        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {

            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                updateCategoryDataGrid();
            }
        });

        categorizationDataGrid.setSelectionModel(selectionModel);

        updateCategorizationDataGrid();
    }

    /**
     * Sets properties of name column of categorization data grid.
     */
    private void setCategorizationNameColumn() {
        Column<CategorizationDto, String> column = new Column<CategorizationDto, String>(new CustomWidgets.CustomCell()) {
            @Override
            public String getValue(CategorizationDto object) {
                return object.getName();
            }

            // TODO: Mark better the possibility to enter a new row
            // I think this could be the right direction but currently it's just a waste of time - not able to get it running right now
//            @Override
//            public void onBrowserEvent(Context context, com.google.gwt.dom.client.Element elem, CategorizationDto object, NativeEvent event) {
//                if (event.getType().equals(DoubleClickEvent.getType().getName())) {
//                    Window.alert(event.getString() + " | " + event.getType());
//                }
//            }
        };

        categorizationDataGrid.addColumn(column, "Group of parameterizations");

        column.setFieldUpdater(new FieldUpdater<CategorizationDto, String>() {
            @Override
            public void update(int index, final CategorizationDto categorization, String value) {
                if (!value.equals(categorization.getName())) {
                    if (categorization.getId() == null
                            && (categorization.getName().equals(LABEL_OF_NEW_ROW))) {
                        addNewCategorizationRow(categorizationDataProvider.getList());
                    }

                    categorization.setName(value);

                    categorizationService.saveCategorization(categorization, new AsyncCallback<Long>() {

                        @Override
                        public void onFailure(Throwable caught) {
                            throw new RuntimeException("Can't update name of " + categorization, caught);
                        }

                        @Override
                        public void onSuccess(Long result) {
                            categorization.setId(result);
                            updateCategorizationDataGrid();
                            updateCategoryDataGrid();
                        }
                    });
                }
            }
        });
    }

    /**
     * Refreshes the content of categorization data grid.
     */
    private void updateCategorizationDataGrid() {
        categorizationService.getCategorizations(new AsyncCallback<List<CategorizationDto>>() {

            @Override
            public void onFailure(Throwable caught) {
            }

            @Override
            public void onSuccess(List<CategorizationDto> result) {
                categorizationDataProvider = new ListDataProvider<CategorizationDto>();
                if (result == null) {
                    result = new ArrayList<CategorizationDto>();
                } else {
                    addNewCategorizationRow(result);
                }

                categorizationDataProvider.setList(result);
                categorizationDataProvider.addDataDisplay(categorizationDataGrid);
                categorizationDataGrid.setRowCount(result.size());
            }
        });

    }

    /**
     * Provides unique key of categorization.
     */
    ProvidesKey<CategorizationDto> keyProvider = new ProvidesKey<CategorizationDto>() {
        @Override
        public Object getKey(CategorizationDto categorization) {
            return (categorization == null) ? null : categorization.getId();
        }
    };

    /**
     * Returns selected categorization.
     *
     * @return selected categorization
     */
    public CategorizationDto getSelectedCategorization() {

        if (categorizationDataProvider == null || categorizationDataProvider.getList() == null) {
            return null;
        }

        List<CategorizationDto> categorizationList = (List<CategorizationDto>) categorizationDataProvider.getList();
        for (CategorizationDto categorizationDto : categorizationList) {
            if (selectionModel.isSelected(categorizationDto)) {
                return categorizationDto;
            }
        }

        return null;
    }

    /**
     * Adds new blank row to categorization data grid.
     *
     * @param categorizations currently displayed categorizations
     */
    private void addNewCategorizationRow(List<CategorizationDto> categorizations) {
        CategorizationDto addRow = new CategorizationDto();
        addRow.setName(LABEL_OF_NEW_ROW);
//        addRow.setReadOnly(true);
        categorizations.add(addRow);
    }

//  ----------------- Categories --------------------
    /**
     * Deletes category.
     *
     * @param category Category to be deleted
     */
    private void deleteCategory(CategoryDto category) {
        categoryService.deleteCategory(category, new AsyncCallback<Void>() {

            @Override
            public void onFailure(Throwable caught) {
            }

            @Override
            public void onSuccess(Void result) {
                updateCategoryDataGrid();
            }
        });
    }

    /**
     * Initializes category data grid.
     */
    private void initCategoryDataGrid() {
        setCategoryNameColumn();
        setCategoryRegexColumn();

        categorySelectionModel = new SingleSelectionModel<CategoryDto>(categoryKeyProvider);

        categoryDataGrid.setSelectionModel(categorySelectionModel);

        setCategoryDataProvider(null);
    }

    /**
     * Sets properties of column name of category data grid.
     */
    private void setCategoryNameColumn() {
        Column<CategoryDto, String> column = new Column<CategoryDto, String>(new EditTextCell()) {
            @Override
            public String getValue(CategoryDto object) {
                return object.getName();
            }
        };

        categoryDataGrid.addColumn(column, "Parameterization");

        column.setFieldUpdater(new FieldUpdater<CategoryDto, String>() {
            @Override
            public void update(int index, final CategoryDto category, String value) {
                if (!value.equals(category.getName())) {
                    if (category.getId() == null
                            && (category.getName().equals(LABEL_OF_NEW_ROW) || category.getRegex().equals(LABEL_OF_NEW_ROW))) {
                        addNewCategoryRow(categoryDataProvider.getList());
                    }

                    category.setName(value);

                    categoryService.saveCategory(category, getSelectedCategorization(), new AsyncCallback<Long>() {

                        @Override
                        public void onFailure(Throwable caught) {
                            throw new RuntimeException("Can't update name of " + category, caught);
                        }

                        @Override
                        public void onSuccess(Long result) {
                            category.setId(result);
                            updateCategoryDataGrid();
                        }
                    });
                }
            }
        });
    }

    /**
     * Sets properties of column regular expression of category data grid.
     */
    private void setCategoryRegexColumn() {
        Column<CategoryDto, String> column = new Column<CategoryDto, String>(new EditTextCell()) {
            @Override
            public String getValue(CategoryDto object) {
                return object.getRegex();
            }
        };

        categoryDataGrid.addColumn(column, "Regular expression");

        column.setFieldUpdater(new FieldUpdater<CategoryDto, String>() {
            @Override
            public void update(int index, final CategoryDto category, String value) {
                if (!value.equals(category.getRegex())) {
                    category.setRegex(value);

                    categoryService.saveCategory(category, getSelectedCategorization(), new AsyncCallback<Long>() {

                        @Override
                        public void onFailure(Throwable caught) {
                            throw new RuntimeException("Can't regex name of " + category, caught);
                        }

                        @Override
                        public void onSuccess(Long result) {
                            category.setId(result);
                            updateCategoryDataGrid();
                        }
                    });
                }
            }
        });
    }

    /**
     * Refreshes the content of category data grid.
     */
    private void updateCategoryDataGrid() {
        categoryService.getCategories(getSelectedCategorization(), new AsyncCallback<List<CategoryDto>>() {

            @Override
            public void onFailure(Throwable caught) {
                throw new RuntimeException("Can't get categories for categorization " + getSelectedCategorization(), caught);
            }

            @Override
            public void onSuccess(List<CategoryDto> result) {
                setCategoryDataProvider(result);
            }
        });
    }

    /**
     * Provides unique key for category.
     */
    ProvidesKey<CategoryDto> categoryKeyProvider = new ProvidesKey<CategoryDto>() {
        @Override
        public Object getKey(CategoryDto category) {
            return (category == null) ? null : category.getId();
        }
    };

    /**
     * Returns selected category.
     *
     * @return selected category
     */
    public CategoryDto getSelectedCategory() {

        List<CategoryDto> categoryList = (List<CategoryDto>) categoryDataProvider.getList();

        for (CategoryDto category : categoryList) {
            if (categorySelectionModel.isSelected(category)) {
                return category;
            }
        }

        return null;
    }

    /**
     * Auxiliary method for refreshing the content of category data grid. Sets data provider for data grid.
     *
     * @param categories
     */
    private void setCategoryDataProvider(List<CategoryDto> categories) {
        categoryDataProvider = new ListDataProvider<CategoryDto>();
        if (categories == null) {
            categories = new ArrayList<CategoryDto>();
        }

        if (getSelectedCategorization() != null) {
            addNewCategoryRow(categories);
        }

        categoryDataProvider.setList(categories);
        categoryDataProvider.addDataDisplay(categoryDataGrid);
        categoryDataGrid.setRowCount(categories.size());
    }

    /**
     * Adds new blank row to category data grid.
     *
     * @param categories currently displayed categories
     */
    private void addNewCategoryRow(List<CategoryDto> categories) {
        CategoryDto addRow = new CategoryDto();
        addRow.setName(LABEL_OF_NEW_ROW);
        addRow.setRegex(LABEL_OF_NEW_ROW);
        categories.add(addRow);
    }

}
