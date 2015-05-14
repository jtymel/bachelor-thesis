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

import org.jboss.ci.tracker.client.widgets.bridges.ResultListTestDetailBridge;
import com.google.gwt.cell.client.NumberCell;
import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.client.SafeHtmlTemplates.Template;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionModel;
import org.jboss.ci.tracker.common.objects.BuildDto;
import org.jboss.ci.tracker.common.objects.CategoryDto;
import org.jboss.ci.tracker.common.objects.JobDto;
import org.jboss.ci.tracker.common.objects.ParameterizedBuildDto;
import org.jboss.ci.tracker.common.objects.PossibleResultDto;
import org.jboss.ci.tracker.common.objects.ResultDto;
import org.jboss.ci.tracker.common.services.ResultService;
import org.jboss.ci.tracker.common.services.ResultServiceAsync;
import org.jboss.ci.tracker.common.objects.TestDto;
import org.jboss.ci.tracker.common.services.CategoryService;
import org.jboss.ci.tracker.common.services.CategoryServiceAsync;
import java.util.List;

/**
 *
 * @author jtymel
 */
public class TestDetail extends Composite {

    private static TestDetailUiBinder uiBinder = GWT.create(TestDetailUiBinder.class);

    private final ResultServiceAsync resultService = GWT.create(ResultService.class);
    private final CategoryServiceAsync categoryService = GWT.create(CategoryService.class);

    interface TestDetailUiBinder extends UiBinder<Widget, TestDetail> {
    }

    ResultListTestDetailBridge resultListTestDetailBridge;

    @UiField(provided = true)
    DataGrid<TestDto> dataGrid;

    @UiField(provided = true)
    SimplePager pager;

    @UiField
    Button cancelButton;

    @UiField
    ListBox possibleResultListBox;

    @UiField
    ListBox categoryListBox;

    private SelectionModel<TestDto> selectionModel;
    private ListDataProvider<TestDto> dataProvider;
    private List<CategoryDto> categoryList;
    private ResultDto result;
    private JobDto job;
    private BuildDto build;
    private ParameterizedBuildDto paramBuild;
    private List<PossibleResultDto> possibleResultList;

    public TestDetail() {
        dataGrid = new DataGrid<TestDto>(500);
        initDatagrid();
        initPager();
        initWidget(uiBinder.createAndBindUi(this));
        initPossibleResultListBox();
        initCategoryListBox();
    }

    public interface SimpleCellTemplates extends SafeHtmlTemplates {

        @Template("<a href=\"{0}\" target=\"_blank\">{1}</a>")
        SafeHtml anchor(SafeUri href, String name);
    }

    static final SimpleCellTemplates cell = GWT.create(SimpleCellTemplates.class);

    @UiHandler("cancelButton")
    void onCancelButtonClick(ClickEvent event) {
        resultListTestDetailBridge.cancelTestDetailAndDisplayResultList();
        refreshPossibleResultListBox(possibleResultList);
        loadCategories();

    }

    public void setResultListTestDetailBridge(ResultListTestDetailBridge bridge) {
        resultListTestDetailBridge = bridge;
    }

    private void initDatagrid() {
        dataGrid.setSelectionModel(selectionModel);

        TextColumn<TestDto> dateColumn = new TextColumn<TestDto>() {
            @Override
            public String getValue(TestDto object) {
                DateTimeFormat dateTimeFormat = DateTimeFormat.getFormat("d.M.yyyy HH:mm:ss");
                return dateTimeFormat.format(object.getDate());
            }
        };

        TextColumn<TestDto> resultColumn = new TextColumn<TestDto>() {
            @Override
            public String getValue(TestDto object) {
                return object.getResult();
            }
        };

        TextColumn<TestDto> machineColumn = new TextColumn<TestDto>() {
            @Override
            public String getValue(TestDto object) {
                return object.getMachine();
            }
        };

        Column<TestDto, Number> durationColumn = new Column<TestDto, Number>(new NumberCell()) {

            @Override
            public Number getValue(TestDto object) {
                return object.getDuration();
            }
        };

        Column urlColumn = new Column<TestDto, SafeHtml>(new SafeHtmlCell()) {
            @Override
            public SafeHtml getValue(TestDto object) {
                SafeUri href = UriUtils.fromSafeConstant(object.getUrl() + "testReport");
                return cell.anchor(href, object.getUrl());
            }
        };

        dataGrid.setColumnWidth(dateColumn, 15, Unit.PCT);
        dataGrid.setColumnWidth(resultColumn, 15, Unit.PCT);
        dataGrid.setColumnWidth(machineColumn, 15, Unit.PCT);
        dataGrid.setColumnWidth(durationColumn, 10, Unit.PCT);
        dataGrid.setColumnWidth(urlColumn, 45, Unit.PCT);

        dataGrid.addColumn(dateColumn, "Date");
        dataGrid.addColumn(resultColumn, "Result");
        dataGrid.addColumn(machineColumn, "Machine");
        dataGrid.addColumn(durationColumn, "Duration [sec]");
        dataGrid.addColumn(urlColumn, "URL");
    }

    private void initPager() {
        SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
        pager = new SimplePager(SimplePager.TextLocation.CENTER, pagerResources, false, 0, true);
        pager.setDisplay(dataGrid);
    }

    private void initPossibleResultListBox() {
        possibleResultListBox.addItem("Don't filter");

        possibleResultListBox.addChangeHandler(new ChangeHandler() {

            @Override
            public void onChange(ChangeEvent event) {
                getTestHistory();

            }
        });
    }

    private void initCategoryListBox() {
        categoryListBox.addItem("Don't filter");

        loadCategories();

        categoryListBox.addChangeHandler(new ChangeHandler() {

            @Override
            public void onChange(ChangeEvent event) {
                getTestHistory();
            }
        });
    }

    public void showTestHistory(ResultDto result, ParameterizedBuildDto paramBuild, List<PossibleResultDto> possibleResults) {
        this.result = result;
        this.possibleResultList = possibleResults;
        this.paramBuild = paramBuild;
        this.build = null;
        this.job = null;

        getTestHistory();
        prepareResultsFilter(possibleResults);
    }

    public void showTestHistory(ResultDto result, BuildDto build, List<PossibleResultDto> possibleResults) {
        this.result = result;
        this.possibleResultList = possibleResults;
        this.paramBuild = null;
        this.build = build;
        this.job = null;

        getTestHistory();
        prepareResultsFilter(possibleResults);
    }

    public void showTestHistory(ResultDto result, JobDto job, List<PossibleResultDto> possibleResults) {
        this.result = result;
        this.possibleResultList = possibleResults;
        this.paramBuild = null;
        this.build = null;
        this.job = job;

        getTestHistory();
        prepareResultsFilter(possibleResults);
    }

    private void fillDataGrid(List<TestDto> result) {
        dataProvider = new ListDataProvider<TestDto>();
        dataProvider.setList(result);
        dataProvider.addDataDisplay(dataGrid);
        dataGrid.setRowCount(result.size());
    }

    private void getTestHistory() {
        if (paramBuild != null) {
            getTestHistory(result, paramBuild);
        }

        if (build != null) {
            getTestHistory(result, build);
        }

        if (job != null) {
            getTestHistory(result, job);
        }
    }

    private void getTestHistory(ResultDto result, JobDto job) {
        resultService.getTestResults(result, job, getPossibleResultId(), getCategoryId(), new AsyncCallback<List<TestDto>>() {

            @Override
            public void onFailure(Throwable caught) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void onSuccess(List<TestDto> result) {
                fillDataGrid(result);
            }
        });
    }

    private void getTestHistory(ResultDto result, BuildDto build) {
        resultService.getTestResults(result, build, getPossibleResultId(), getCategoryId(), new AsyncCallback<List<TestDto>>() {

            @Override
            public void onFailure(Throwable caught) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void onSuccess(List<TestDto> result) {
                fillDataGrid(result);
            }
        });
    }

    private void getTestHistory(ResultDto result, ParameterizedBuildDto paramBuild) {
        resultService.getTestResults(result, paramBuild, getPossibleResultId(), getCategoryId(), new AsyncCallback<List<TestDto>>() {

            @Override
            public void onFailure(Throwable caught) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void onSuccess(List<TestDto> result) {
                fillDataGrid(result);
            }
        });
    }

    private void refreshPossibleResultListBox(List<PossibleResultDto> possibleResults) {
        possibleResultListBox.clear();

        possibleResultListBox.addItem("Don't filter");

        for (PossibleResultDto possibleResult : possibleResults) {
            possibleResultListBox.addItem(possibleResult.getName());
        }
    }

    private void prepareResultsFilter(List<PossibleResultDto> possibleResults) {
        refreshPossibleResultListBox(possibleResults);
    }

    private void loadCategories() {
        categoryService.getCategories(new AsyncCallback<List<CategoryDto>>() {

            @Override
            public void onFailure(Throwable caught) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void onSuccess(List<CategoryDto> categories) {
                categoryList = categories;
                categoryListBox.clear();

                categoryListBox.addItem("Don't filter");

                for (CategoryDto category : categories) {
                    categoryListBox.addItem(category.getCategorization() + " : " + category.getName());
                }
            }
        });
    }

    private Long getCategoryId() {
        if (categoryListBox.getSelectedIndex() == 0) {
            return null;
        }

        return categoryList.get(categoryListBox.getSelectedIndex() - 1).getId();
    }

    private Long getPossibleResultId() {
        Long possibleResultId = null;

        for (PossibleResultDto possibleResult : possibleResultList) {
            if (possibleResult.getName().equals(possibleResultListBox.getSelectedItemText())) {
                possibleResultId = possibleResult.getId();
                break;
            }
        }
        return possibleResultId;
    }

}
