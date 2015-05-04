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

import com.google.gwt.cell.client.NumberCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
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
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;
import gwtEntity.client.BuildDto;
import gwtEntity.client.JobDto;
import gwtEntity.client.ParameterizedBuildDto;
import gwtEntity.client.ResultDto;
import gwtEntity.client.ResultService;
import gwtEntity.client.ResultServiceAsync;
import gwtEntity.client.TestDto;
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

    private SelectionModel<TestDto> selectionModel;
    private ListDataProvider<TestDto> dataProvider;

    public TestDetail() {
        dataGrid = new DataGrid<TestDto>(500);
        initDatagrid();
        initPager();
        initWidget(uiBinder.createAndBindUi(this));
    }

    @UiHandler("cancelButton")
    void onCancelButtonClick(ClickEvent event) {
        resultListTestDetailBridge.cancelTestDetailAndDisplayResultList();
    }

    public void setResultListTestDetailBridge(ResultListTestDetailBridge bridge) {
        resultListTestDetailBridge = bridge;
    }

    private void initDatagrid() {
        selectionModel = new SingleSelectionModel<TestDto>(keyProvider);
        dataGrid.setSelectionModel(selectionModel);

        TextColumn<TestDto> dateColumn = new TextColumn<TestDto>() {
            @Override
            public String getValue(TestDto object) {
                return object.getDate().toString();
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

        dataGrid.addColumn(dateColumn, "Date");
        dataGrid.addColumn(resultColumn, "Result");
        dataGrid.addColumn(machineColumn, "Machine");
        dataGrid.addColumn(durationColumn, "Duration [sec]");
    }

    private void initPager() {
        SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
        pager = new SimplePager(SimplePager.TextLocation.CENTER, pagerResources, false, 0, true);
        pager.setDisplay(dataGrid);
    }

    public void showTestHistory(ResultDto result, ParameterizedBuildDto paramBuild) {
        getTestHistory(result, paramBuild);
    }

    public void showTestHistory(ResultDto result, BuildDto build) {
        getTestHistory(result, build);
    }

    public void showTestHistory(ResultDto result, JobDto job) {
        getTestHistory(result, job);
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

    ProvidesKey<TestDto> keyProvider = new ProvidesKey<TestDto>() {
        @Override
        public Object getKey(TestDto test) {
            return (test == null) ? null : test.hashCode();
        }
    };

}
