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
import org.jboss.ci.tracker.client.widgets.bridges.ParamBuildResultListBridge;
import org.jboss.ci.tracker.client.widgets.bridges.JobListResultListBridge;
import org.jboss.ci.tracker.client.widgets.bridges.BuildListResultListBridge;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;
import org.jboss.ci.tracker.common.objects.BuildDto;
import org.jboss.ci.tracker.common.objects.CategoryDto;
import org.jboss.ci.tracker.common.objects.JobDto;
import org.jboss.ci.tracker.common.objects.ParameterizedBuildDto;
import org.jboss.ci.tracker.common.objects.PossibleResultDto;
import org.jboss.ci.tracker.common.objects.ResultDto;
import org.jboss.ci.tracker.common.services.CategoryService;
import org.jboss.ci.tracker.common.services.CategoryServiceAsync;
import org.jboss.ci.tracker.common.services.ResultService;
import org.jboss.ci.tracker.common.services.ResultServiceAsync;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import org.jboss.ci.tracker.client.utils.ResultsColumnProperties;
import org.jboss.ci.tracker.common.objects.CategorizationDto;
import org.jboss.ci.tracker.common.objects.FilterDto;
import org.jboss.ci.tracker.common.services.CategorizationService;
import org.jboss.ci.tracker.common.services.CategorizationServiceAsync;

/**
 *
 * @author jtymel
 */
public class ResultList extends Composite {

    private static final ResultListUiBinder uiBinder = GWT.create(ResultListUiBinder.class);

    private static final ResultServiceAsync resultService = GWT.create(ResultService.class);
    private static final CategoryServiceAsync categoryService = GWT.create(CategoryService.class);
    private static final CategorizationServiceAsync categorizationService = GWT.create(CategorizationService.class);

    interface ResultListUiBinder extends UiBinder<Widget, ResultList> {
    }

    private ParamBuildResultListBridge paramBuildResultListBridge;
    private BuildListResultListBridge buildListResultListBridge;
    private JobListResultListBridge jobListResultListBridge;
    private ResultListTestDetailBridge resultListTestDetailBridge;

    @UiField(provided = true)
    DataGrid<ResultDto> dataGrid;

    @UiField(provided = true)
    SimplePager pager;

    @UiField
    Button cancelButton;

    @UiField
    Button filterButton;

    private SelectionModel<ResultDto> selectionModel;
    private ListDataProvider<ResultDto> dataProvider;
    private ParameterizedBuildDto paramBuild;
    private Collection<BuildDto> builds;
    private JobDto job;
    private List<PossibleResultDto> possibleResultList;
    private List<CategoryDto> categoryList;
    private List<CategorizationDto> categorizationList;
    private FilterDto filter = null;

    private TextColumn<ResultDto> testNameColumn;
    private TextColumn<ResultDto> testCaseNameColumn;
    private List<ResultsColumnProperties> resultColumns = new ArrayList<ResultsColumnProperties>();

    public ResultList() {
        dataGrid = new DataGrid<ResultDto>(500);
        initDatagrid();
        initPager();
        initWidget(uiBinder.createAndBindUi(this));
    }

    @UiHandler("cancelButton")
    void onCancelButtonClick(ClickEvent event) {
        if (job != null) {
            jobListResultListBridge.cancelResultListAndDisplayJobList();
        }

        if (builds != null) {
            buildListResultListBridge.cancelResultListAndDisplayBuildList();
        }

        if (paramBuild != null) {
            paramBuildResultListBridge.cancelResultListAndDisplayParamBuildList();
        }
    }

    @UiHandler("filterButton")
    void onFilterButtonClick(ClickEvent event) {
        DialogBox filterDialogBox = CustomWidgets.filterDialogBox(this, categorizationList, categoryList, possibleResultList, filter);
        filterDialogBox.setPopupPosition(filterButton.getAbsoluteLeft(), filterButton.getAbsoluteTop() + filterButton.getOffsetHeight());
        filterDialogBox.show();
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
        addSelectionModel();
        addDoubleClickHandler();
    }

    private void addSelectionModel() {
        selectionModel = new SingleSelectionModel<ResultDto>(keyProvider);
        dataGrid.setSelectionModel(selectionModel);
    }

    private void addDoubleClickHandler() {
        dataGrid.addDomHandler(new DoubleClickHandler() {

            @Override
            public void onDoubleClick(DoubleClickEvent event) {
                List<ResultDto> resultList = getSelectedResults();
                if (job != null) {
                    for (ResultDto result : resultList) {
                        resultListTestDetailBridge.setTestAndDisplayHistory(result, job, possibleResultList);
                    }
                }

                if (builds != null) {
                    for (ResultDto result : resultList) {
                        resultListTestDetailBridge.setTestAndDisplayHistory(result, builds, possibleResultList);
                    }
                }

                if (paramBuild != null) {
                    for (ResultDto result : resultList) {
                        resultListTestDetailBridge.setTestAndDisplayHistory(result, paramBuild, possibleResultList);
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

    public void showParamBuildResults(ParameterizedBuildDto paramBuildDto) {
        setEnvironmentForParamBuildResults(paramBuildDto);
        addColumnsAndCallForResults();
    }

    private void setEnvironmentForParamBuildResults(ParameterizedBuildDto paramBuildDto) {
        paramBuild = paramBuildDto;
        builds = null;
        job = null;
    }

    public void showBuildResults(Collection<BuildDto> builds) {
        setEnvironmentForBuildResults(builds);
        addColumnsAndCallForResults();
    }

    private void setEnvironmentForBuildResults(Collection<BuildDto> builds) {
        paramBuild = null;
        this.builds = builds;
        job = null;
    }

    public void showJobResults(JobDto jobDto) {
        setEnvironmentForJobResults(jobDto);
        addColumnsAndCallForResults();
    }

    private void setEnvironmentForJobResults(JobDto jobDto) {
        paramBuild = null;
        builds = null;
        job = jobDto;
    }

    private void addColumnsAndCallForResults() {
        removeDataGridColumns();
        addTestNameColumn();
        addTestCaseNameColumn();
        addResultColumns();
    }

    private void removeDataGridColumns() {
        for (int i = dataGrid.getColumnCount() - 1; i >= 0; i--) {
            dataGrid.removeColumn(i);
        }
        resultColumns.clear();
    }

    private void addTestNameColumn() {
        testNameColumn = new TextColumn<ResultDto>() {
            @Override
            public String getValue(ResultDto object) {
                return object.getTest();
            }
        };

        testNameColumn.setSortable(true);
        dataGrid.addColumn(testNameColumn, "Test name");
    }

    private void addTestCaseNameColumn() {
        testCaseNameColumn = new TextColumn<ResultDto>() {
            @Override
            public String getValue(ResultDto object) {
                return object.getTestCase();
            }
        };

        testCaseNameColumn.setSortable(true);
        dataGrid.addColumn(testCaseNameColumn, "TestCase name");
    }

    private void addResultColumns() {
        resultService.getPossibleResults(new AsyncCallback<List<PossibleResultDto>>() {

            @Override
            public void onFailure(Throwable caught) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void onSuccess(List<PossibleResultDto> possibleResults) {
                for (final PossibleResultDto possibleResult : possibleResults) {

                    TextColumn<ResultDto> resultColumn = createAndAddResultColumn(possibleResult);

                    storeResultColumnProperties(resultColumn, possibleResult);

                }

                storePossibleResults(possibleResults);
                loadCategorizationsAndCategories();

                filter = null;
                getResults();
            }

        });
    }

    private TextColumn<ResultDto> createAndAddResultColumn(final PossibleResultDto possibleResult) {
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

        resultColumn.setSortable(true);
        dataGrid.setColumnWidth(resultColumn, 8, Style.Unit.EM);
        dataGrid.addColumn(resultColumn, possibleResult.getName());

        return resultColumn;
    }

    private void storeResultColumnProperties(TextColumn<ResultDto> resultColumn, final PossibleResultDto possibleResult) {
        ResultsColumnProperties columnProperties = new ResultsColumnProperties();
        columnProperties.setColumn(resultColumn);
        columnProperties.setPossibleResultId(possibleResult.getId());

        resultColumns.add(columnProperties);
    }

    private void fillDataGrid(List<ResultDto> result) {
        prepareDataProvider(result);
        ListHandler<ResultDto> sortHandler = createSortHandler();

        dataGrid.addColumnSortHandler(sortHandler);
        dataGrid.setRowCount(result.size());
    }

    private ListHandler<ResultDto> createSortHandler() {
        ListHandler<ResultDto> sortHandler = new ListHandler<ResultDto>(dataProvider.getList());

        setTestColumnComparator(sortHandler);
        setTestCaseColumnComparator(sortHandler);
        setResultColumnComparators(sortHandler);

        return sortHandler;
    }

    private void prepareDataProvider(List<ResultDto> result) {
        dataProvider = new ListDataProvider<ResultDto>();
        dataProvider.addDataDisplay(dataGrid);

        dataProvider.getList().clear();
        dataProvider.getList().addAll(result);
    }

    private void setTestColumnComparator(ListHandler<ResultDto> sortHandler) {
        sortHandler.setComparator(testNameColumn, new Comparator<ResultDto>() {

            @Override
            public int compare(ResultDto o1, ResultDto o2) {
                return o1.getTest().compareTo(o2.getTest());
            }
        });
    }

    private void setTestCaseColumnComparator(ListHandler<ResultDto> sortHandler) {
        sortHandler.setComparator(testCaseNameColumn, new Comparator<ResultDto>() {

            @Override
            public int compare(ResultDto o1, ResultDto o2) {
                return o1.getTestCase().compareTo(o2.getTestCase());
            }
        });
    }

    private void setResultColumnComparators(ListHandler<ResultDto> sortHandler) {
        for (final ResultsColumnProperties resultColumn : resultColumns) {
            sortHandler.setComparator(resultColumn.getColumn(), new Comparator<ResultDto>() {

                @Override
                public int compare(ResultDto o1, ResultDto o2) {
                    if (o1 == o2) {
                        return 0;
                    }
                    if (o1 == null || o1.getResults().get(resultColumn.getPossibleResultId()) == null) {
                        return -1;
                    }
                    if (o2 == null || o2.getResults().get(resultColumn.getPossibleResultId()) == null) {
                        return 1;
                    }

                    return o1.getResults().get(resultColumn.getPossibleResultId()) - o2.getResults().get(resultColumn.getPossibleResultId());
                }
            });
        }
    }

    private void storePossibleResults(List<PossibleResultDto> possibleResults) {
        possibleResultList = possibleResults;
    }

    private void getResults() {
        if (paramBuild != null) {
            getResults(paramBuild);
        }

        if (builds != null) {
            getResults(builds);
        }

        if (job != null) {
            getResults(job);
        }
    }

    public void getResults(ParameterizedBuildDto paramBuild) {
        resultService.getResults(paramBuild, filter, new AsyncCallback<List<ResultDto>>() {
            @Override
            public void onFailure(Throwable caught) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void onSuccess(List<ResultDto> result) {
                fillDataGrid(result);
            }
        });

    }

    public void getResults(Collection<BuildDto> builds) {
        resultService.getResults(builds, filter, new AsyncCallback<List<ResultDto>>() {
            @Override
            public void onFailure(Throwable caught) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void onSuccess(List<ResultDto> result) {
                fillDataGrid(result);
            }
        });

    }

    public void getResults(JobDto job) {
        resultService.getResults(job, filter, new AsyncCallback<List<ResultDto>>() {
            @Override
            public void onFailure(Throwable caught) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void onSuccess(List<ResultDto> result) {
                fillDataGrid(result);
            }
        });

    }

    private void loadCategorizationsAndCategories() {
        loadCategories();
        loadCategorizations();
    }

    private void loadCategorizations() {
        categorizationService.getCategorizations(new AsyncCallback<List<CategorizationDto>>() {

            @Override
            public void onFailure(Throwable caught) {
                throw new RuntimeException("Could not get the list of categorizations", caught);
            }

            @Override
            public void onSuccess(List<CategorizationDto> categorizations) {
                categorizationList = categorizations;
            }
        });
    }

    private void loadCategories() {
        categoryService.getCategories(new AsyncCallback<List<CategoryDto>>() {

            @Override
            public void onFailure(Throwable caught) {
                throw new RuntimeException("Could not get the list of categories", caught);
            }

            @Override
            public void onSuccess(List<CategoryDto> categories) {
                categoryList = categories;
            }
        });
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

        for (ResultDto buildDto : buildList) {
            if (selectionModel.isSelected(buildDto)) {
                selectedBuilds.add(buildDto);
            }
        }

        return selectedBuilds;
    }

    public void filterResults(FilterDto filterDto) {
        filter = filterDto;
        getResults();
    }
}
