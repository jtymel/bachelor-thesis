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

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
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
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;
import gwtEntity.client.BuildDto;
import gwtEntity.client.JobDto;
import gwtEntity.client.ParameterizedBuildDto;
import gwtEntity.client.PossibleResultDto;
import gwtEntity.client.ResultDto;
import gwtEntity.client.ResultService;
import gwtEntity.client.ResultServiceAsync;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jtymel
 */
public class ResultList extends Composite {

    private static ResultListUiBinder uiBinder = GWT.create(ResultListUiBinder.class);

    private final ResultServiceAsync resultService = GWT.create(ResultService.class);

    interface ResultListUiBinder extends UiBinder<Widget, ResultList> {
    }

    ParamBuildResultListBridge paramBuildResultListBridge;
    BuildListResultListBridge buildListResultListBridge;
    JobListResultListBridge jobListResultListBridge;
    ResultListTestDetailBridge resultListTestDetailBridge;

    @UiField(provided = true)
    DataGrid<ResultDto> dataGrid;

    @UiField(provided = true)
    SimplePager pager;

    @UiField
    ListBox possibleResultListBox;

    private SelectionModel<ResultDto> selectionModel;
    private ListDataProvider<ResultDto> dataProvider;
    private ParameterizedBuildDto paramBuild;
    private BuildDto build;
    private JobDto job;
    private List<PossibleResultDto> possibleResultsList;

    public ResultList() {
        dataGrid = new DataGrid<ResultDto>(500);
        initDatagrid();
        initPager();
        initWidget(uiBinder.createAndBindUi(this));
        initListBox();
    }

    @UiHandler("cancelButton")
    void onCancelButtonClick(ClickEvent event) {
        if (job != null) {
            jobListResultListBridge.cancelResultListAndDisplayJobList();
        }

        if (build != null) {
            buildListResultListBridge.cancelResultListAndDisplayBuildList();
        }

        if (paramBuild != null) {
            paramBuildResultListBridge.cancelResultListAndDisplayParamBuildList();
        }
    }

    public void setParamBuildResultListBridge(ParamBuildResultListBridge bridge) {
        paramBuildResultListBridge = bridge;
    }

    public void setBuildListResultListBridge(BuildListResultListBridge bridge) {
        buildListResultListBridge = bridge;
    }

    public void setJobListResultListBridge(JobListResultListBridge bridge) {
        jobListResultListBridge = bridge;
    }

    public void setResultListTestDetailBridge(ResultListTestDetailBridge bridge) {
        resultListTestDetailBridge = bridge;
    }

    private void initDatagrid() {
        selectionModel = new SingleSelectionModel<ResultDto>(keyProvider);
        dataGrid.setSelectionModel(selectionModel);

        dataGrid.addDomHandler(new DoubleClickHandler() {

            @Override
            public void onDoubleClick(DoubleClickEvent event) {
                List<ResultDto> resultList = getSelectedResults();
                if (job != null) {
                    for (ResultDto result : resultList) {
                        resultListTestDetailBridge.setTestAndDisplayHistory(result, job, possibleResultsList);
                    }
                }

                if (build != null) {
                    for (ResultDto result : resultList) {
                        resultListTestDetailBridge.setTestAndDisplayHistory(result, build, possibleResultsList);
                    }
                }

                if (paramBuild != null) {
                    for (ResultDto result : resultList) {
                        resultListTestDetailBridge.setTestAndDisplayHistory(result, paramBuild, possibleResultsList);
                    }
                }

            }
        }, DoubleClickEvent.getType());
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

    public void showParamBuildResults(ParameterizedBuildDto paramBuildDto) {
        addColumnsAndCallForResults(paramBuildDto);
    }

    public void showBuildResults(BuildDto buildDto) {
        addColumnsAndCallForResults(buildDto);
    }

    public void showJobResults(JobDto jobDto) {
        addColumnsAndCallForResults(jobDto);
    }

    private void addColumnsAndCallForResults(final Object entity) {
        for (int i = dataGrid.getColumnCount() - 1; i >= 0; i--) {
            dataGrid.removeColumn(i);
        }

        TextColumn<ResultDto> nameColumn = new TextColumn<ResultDto>() {
            @Override
            public String getValue(ResultDto object) {
                return object.getTest();
            }
        };

        TextColumn<ResultDto> testCaseColumn = new TextColumn<ResultDto>() {
            @Override
            public String getValue(ResultDto object) {
                return object.getTestCase();
            }
        };

        dataGrid.addColumn(nameColumn, "Test name");
        dataGrid.addColumn(testCaseColumn, "TestCase name");

        resultService.getPossibleResults(new AsyncCallback<List<PossibleResultDto>>() {

            @Override
            public void onFailure(Throwable caught) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void onSuccess(List<PossibleResultDto> possibleResults) {
                for (final PossibleResultDto possibleResult : possibleResults) {
                    possibleResultListBox.addItem(possibleResult.getName());

                    TextColumn<ResultDto> resultColumn = new TextColumn<ResultDto>() {
                        @Override
                        public String getValue(ResultDto object) {
                            if (object.getResults().containsKey(possibleResult.getId())) {
                                return object.getResults().get(possibleResult.getId()).toString();
                            } else {
                                return "";
                            }
                        }
                    };
                    dataGrid.setColumnWidth(resultColumn, 8, Style.Unit.EM);
                    dataGrid.addColumn(resultColumn, possibleResult.getName());
                }

                storePossibleResults(possibleResults);
                refreshPossibleResultListBox(possibleResults);

                if (entity instanceof ParameterizedBuildDto) {
                    getResults((ParameterizedBuildDto) entity);
                }

                if (entity instanceof BuildDto) {
                    getResults((BuildDto) entity);
                }

                if (entity instanceof JobDto) {
                    getResults((JobDto) entity);
                }
            }

        });
    }

    private void fillDataGrid(List<ResultDto> result) {
        dataProvider = new ListDataProvider<ResultDto>();
        dataProvider.setList(result);
        dataProvider.addDataDisplay(dataGrid);
        dataGrid.setRowCount(result.size());
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
            Long possibleResultId = null;

            for (PossibleResultDto possibleResult : possibleResultsList) {
                if (possibleResult.getName().equals(selectedResults)) {
                    possibleResultId = possibleResult.getId();
                    break;
                }
            }

            List<ResultDto> auxResultList = new ArrayList<ResultDto>();

            for (ResultDto auxResult : dataProvider.getList()) {
                if (auxResult.getResults().containsKey(possibleResultId)) {
                    auxResultList.add(auxResult);
                }
            }
            ListDataProvider<ResultDto> auxListProvider = new ListDataProvider<ResultDto>();

            auxListProvider.setList(auxResultList);
            auxListProvider.addDataDisplay(dataGrid);
            dataGrid.setRowCount(auxResultList.size());
        }
    }

    private void storePossibleResults(List<PossibleResultDto> possibleResults) {
        possibleResultsList = possibleResults;
    }

    public void getResults(ParameterizedBuildDto paramBuild) {
        resultService.getResults(paramBuild, new AsyncCallback<List<ResultDto>>() {

            @Override
            public void onFailure(Throwable caught) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void onSuccess(List<ResultDto> result) {
                fillDataGrid(result);
            }
        });

        this.paramBuild = paramBuild;
        this.build = null;
        this.job = null;
    }

    public void getResults(BuildDto build) {
        resultService.getResults(build, new AsyncCallback<List<ResultDto>>() {

            @Override
            public void onFailure(Throwable caught) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void onSuccess(List<ResultDto> result) {
                fillDataGrid(result);
            }
        });

        this.paramBuild = null;
        this.build = build;
        this.job = null;
    }

    public void getResults(JobDto job) {
        resultService.getResults(job, new AsyncCallback<List<ResultDto>>() {

            @Override
            public void onFailure(Throwable caught) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void onSuccess(List<ResultDto> result) {
                fillDataGrid(result);
            }
        });

        this.paramBuild = null;
        this.build = null;
        this.job = job;
    }

    ProvidesKey<ResultDto> keyProvider = new ProvidesKey<ResultDto>() {
        @Override
        public Object getKey(ResultDto result) {
            return (result == null) ? null : result.getTest() + result.getTestCase();
        }
    };

    public List<ResultDto> getSelectedResults() {
        List<ResultDto> buildList = (List<ResultDto>) dataProvider.getList();
        List<ResultDto> selectedBuilds = new ArrayList<ResultDto>();

        Long i = 0L;

        for (ResultDto buildDto : buildList) {
            if (selectionModel.isSelected(buildDto)) {
                selectedBuilds.add(buildDto);
            }
            i++;
        }

        return selectedBuilds;
    }
}
