/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2015, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package gwtEntity.client.widgets;

import gwtEntity.client.widgets.bridges.ResultListTestDetailBridge;
import com.google.gwt.cell.client.NumberCell;
import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.core.client.GWT;
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
import gwtEntity.client.BuildDto;
import gwtEntity.client.JobDto;
import gwtEntity.client.ParameterizedBuildDto;
import gwtEntity.client.PossibleResultDto;
import gwtEntity.client.ResultDto;
import gwtEntity.common.service.ResultService;
import gwtEntity.common.service.ResultServiceAsync;
import gwtEntity.client.TestDto;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jtymel
 */
public class TestDetail extends Composite {

    private static TestDetailUiBinder uiBinder = GWT.create(TestDetailUiBinder.class);

    private final ResultServiceAsync resultService = GWT.create(ResultService.class);

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

    private SelectionModel<TestDto> selectionModel;
    private ListDataProvider<TestDto> dataProvider;

    public TestDetail() {
        dataGrid = new DataGrid<TestDto>(500);
        initDatagrid();
        initPager();
        initWidget(uiBinder.createAndBindUi(this));
        initListBox();
    }

    public interface SimpleCellTemplates extends SafeHtmlTemplates {

        @Template("<a href=\"{0}\" target=\"_blank\">{1}</a>")
        SafeHtml anchor(SafeUri href, String name);
    }

    static final SimpleCellTemplates cell = GWT.create(SimpleCellTemplates.class);

    @UiHandler("cancelButton")
    void onCancelButtonClick(ClickEvent event) {
        resultListTestDetailBridge.cancelTestDetailAndDisplayResultList();
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

    private void initListBox() {
        possibleResultListBox.addItem("All");

        possibleResultListBox.addChangeHandler(new ChangeHandler() {

            @Override
            public void onChange(ChangeEvent event) {
                filterSelectedResults(possibleResultListBox.getSelectedItemText());

            }
        });
    }

    public void showTestHistory(ResultDto result, ParameterizedBuildDto paramBuild, List<PossibleResultDto> possibleResults) {
        getTestHistory(result, paramBuild);
        prepareResultsFilter(possibleResults);
    }

    public void showTestHistory(ResultDto result, BuildDto build, List<PossibleResultDto> possibleResults) {
        getTestHistory(result, build);
        prepareResultsFilter(possibleResults);
    }

    public void showTestHistory(ResultDto result, JobDto job, List<PossibleResultDto> possibleResults) {
        getTestHistory(result, job);
        prepareResultsFilter(possibleResults);
    }

    private void fillDataGrid(List<TestDto> result) {
        dataProvider = new ListDataProvider<TestDto>();
        dataProvider.setList(result);
        dataProvider.addDataDisplay(dataGrid);
        dataGrid.setRowCount(result.size());
    }

    private void getTestHistory(ResultDto result, JobDto job) {
        resultService.getTestResults(result, job, new AsyncCallback<List<TestDto>>() {

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
        resultService.getTestResults(result, build, new AsyncCallback<List<TestDto>>() {

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
        resultService.getTestResults(result, paramBuild, new AsyncCallback<List<TestDto>>() {

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

        possibleResultListBox.addItem("All");

        for (PossibleResultDto possibleResult : possibleResults) {
            possibleResultListBox.addItem(possibleResult.getName());
        }
    }

    private void filterSelectedResults(String selectedResults) {
        if (selectedResults.equals("All")) {
            fillDataGrid(dataProvider.getList());
        } else {

            List<TestDto> auxResultList = new ArrayList<TestDto>();

            for (TestDto auxResult : dataProvider.getList()) {
                if (auxResult.getResult().equals(selectedResults)) {
                    auxResultList.add(auxResult);
                }
            }
            ListDataProvider<TestDto> auxListProvider = new ListDataProvider<TestDto>();

            auxListProvider.setList(auxResultList);
            auxListProvider.addDataDisplay(dataGrid);
            dataGrid.setRowCount(auxResultList.size());
        }
    }

    private void prepareResultsFilter(List<PossibleResultDto> possibleResults) {
        refreshPossibleResultListBox(possibleResults);
    }
}
