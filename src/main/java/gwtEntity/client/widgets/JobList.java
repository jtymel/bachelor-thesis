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
package gwtEntity.client.widgets;

import com.google.gwt.cell.client.SafeHtmlCell;
import gwtEntity.client.widgets.bridges.JobListResultListBridge;
import gwtEntity.client.widgets.bridges.JobListDetailBridge;
import gwtEntity.client.widgets.bridges.JobListBuildListBridge;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.cellview.client.SafeHtmlHeader;
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
import gwtEntity.common.services.JenkinsService;
import gwtEntity.common.services.JenkinsServiceAsync;
import gwtEntity.common.objects.JobDto;
import gwtEntity.common.services.JobService;
import gwtEntity.common.services.JobServiceAsync;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jtymel
 */
public class JobList extends Composite {

    private static JobListUiBinder uiBinder = GWT.create(JobListUiBinder.class);

    private final JobServiceAsync jobService = GWT.create(JobService.class);
    private final JenkinsServiceAsync jenkinsService = GWT.create(JenkinsService.class);

    private JobListDetailBridge jobListDetailBridge;
    private JobListBuildListBridge jobListBuildListBridge;
    private JobListResultListBridge jobListResultListBridge;

    interface JobListUiBinder extends UiBinder<Widget, JobList> {
    }

    @UiField(provided = true)
    DataGrid<JobDto> dataGrid;

    /*
     SimplePager has strange behaviour when the last page is reached. See https://code.google.com/p/google-web-toolkit/issues/detail?id=6163
     Steps to reproduce: Hold "Enter" for a while, click on the 'last page icon', click on the 'previous icon' and click on the 'next icon'
     */
    @UiField(provided = true)
    SimplePager pager;

    @UiField
    Button deleteButton;

    @UiField
    Button addButton;

    @UiField
    Button showDetailButton;

    @UiField
    Button addCtgToParamBuildButton;

    @UiField
    Button downloadResultsButton;

    private SelectionModel<JobDto> selectionModel;
    private ListDataProvider<JobDto> dataProvider;

    public JobList() {
        dataGrid = new DataGrid<JobDto>(500);
        initDatagrid();
        initPager();
        initWidget(uiBinder.createAndBindUi(this));
    }

    public void setJobListDetailBridge(JobListDetailBridge bridge) {
        jobListDetailBridge = bridge;
    }

    public void setJobListBuildListBridge(JobListBuildListBridge bridge) {
        jobListBuildListBridge = bridge;
    }

    public void setJobListResultListBridge(JobListResultListBridge bridge) {
        jobListResultListBridge = bridge;
    }

    public interface SimpleCellTemplates extends SafeHtmlTemplates {

        @SafeHtmlTemplates.Template("<a href=\"{0}\" target=\"_blank\">{1}</a>")
        SafeHtml anchor(SafeUri href, String name);
    }

    static final SimpleCellTemplates cell = GWT.create(SimpleCellTemplates.class);

    @UiHandler("deleteButton")
    void onDeleteButtonClick(ClickEvent event) {
        List<JobDto> jobList = getSelectedJobs();

        for (JobDto jobDTO : jobList) {
            if (selectionModel.isSelected(jobDTO)) {
                deleteJob(jobDTO);
            }
        }

    }

    @UiHandler("showDetailButton")
    void onShowDetailButtonClick(ClickEvent event) {
        List<JobDto> jobList = getSelectedJobs();

        for (JobDto jobDto : jobList) {
            jobListDetailBridge.setJobAndDisplayDetail(jobDto);
        }

    }

    @UiHandler("addButton")
    void onAddButtonClick(ClickEvent event) {
        jobListDetailBridge.setJobAndDisplayDetail(null);
    }

    @UiHandler("showResultsButton")
    void onShowResultButtonClick(ClickEvent event) {
        List<JobDto> jobList = getSelectedJobs();

        for (JobDto jobDto : jobList) {
            if (selectionModel.isSelected(jobDto)) {
                jobListResultListBridge.setJobAndDisplayResults(jobDto);
            }
        }

    }

    @UiHandler("downloadResultsButton")
    void ondDownloadResultsButtonClick(ClickEvent event) {
        List<JobDto> jobList = getSelectedJobs();
        jenkinsService.downloadBuilds(jobList, new AsyncCallback<Void>() {

            @Override
            public void onFailure(Throwable caught) {
                CustomWidgets.alertWidget("Unexpected error",
                        "An error occured during downloading results. See server log for more details.").center();
            }

            @Override
            public void onSuccess(Void result) {
                CustomWidgets.alertWidget("Results successfully downloaded",
                        "Results have been correctly downloaded.").center();
            }
        });

        CustomWidgets.alertWidget("Results download",
                "Results are being downloaded.").center();

    }

    private void deleteJob(JobDto jobDTO) {
        jobService.deleteJob(jobDTO, new AsyncCallback<Void>() {

            @Override
            public void onFailure(Throwable caught) {
            }

            @Override
            public void onSuccess(Void result) {
                updateDataGrid();
            }
        });
    }

    @UiHandler("addCtgToParamBuildButton")
    void onAddCtgToParamBuildButtonClick(ClickEvent event) {
        List<JobDto> jobs = getSelectedJobs();

        for (JobDto jobDto : jobs) {
            jobService.addCategoriesToParamBuild(jobDto, new AsyncCallback<Void>() {

                @Override
                public void onFailure(Throwable caught) {
                    CustomWidgets.alertWidget("Unexpected error",
                            "An error occured during addition categories to param build. See server log for more details.").center();
                }

                @Override
                public void onSuccess(Void result) {
                    CustomWidgets.alertWidget("Categories correctly added",
                            "Categories were correctly added.").center();
                }
            });
        }

    }

    public void onTabShow() {
        updateDataGrid();
    }

    private void initDatagrid() {
        Column urlColumn = new Column<JobDto, SafeHtml>(new SafeHtmlCell()) {

            @Override
            public SafeHtml getValue(JobDto object) {
                SafeUri href = UriUtils.fromSafeConstant(object.getUrl());
                return cell.anchor(href, object.getUrl());
            }
        };

        TextColumn<JobDto> nameColumn = new TextColumn<JobDto>() {
            @Override
            public String getValue(JobDto object) {
                return object.getName();
            }
        };

        Header<String> nameHeader = new Header<String>(new TextCell()) {
            @Override
            public String getValue() {
                List<JobDto> items = dataGrid.getVisibleItems();
                return "Name";
            }
        };

        dataGrid.addColumn(nameColumn, new SafeHtmlHeader(new SafeHtml() {

            @Override
            public String asString() {
                return "Name";
            }
        }));

        dataGrid.setColumnWidth(nameColumn, 40, Style.Unit.PCT);
        dataGrid.addColumn(urlColumn, "URL");
        dataGrid.setColumnWidth(urlColumn, 60, Style.Unit.PCT);

        selectionModel = new SingleSelectionModel<JobDto>(keyProvider);

        dataGrid.addDomHandler(new DoubleClickHandler() {

            @Override
            public void onDoubleClick(DoubleClickEvent event) {

                List<JobDto> jobList = getSelectedJobs();

                for (JobDto jobDto : jobList) {
                    jobListBuildListBridge.setJobAndDisplayBuilds(jobDto);
                }
            }
        }, DoubleClickEvent.getType());

        dataGrid.setSelectionModel(selectionModel);

        /* Not running yet */
//        nameColumn.setSortable(true);
//        ListHandler<JobDto> sortHandler = new ListHandler<JobDto>(dataProvider.getList());
//
//        sortHandler.setComparator(nameColumn, new Comparator<JobDto>() {
//
//            @Override
//            public int compare(JobDto o1, JobDto o2) {
////                return o1.getName().compareTo(o2.getName());
//                if (o1 == o2) {
//                    return 0;
//                }
//
//                // Compare the name columns.
//                if (o1 != null) {
//                    return (o2 != null) ? o1.getName().compareTo(o2.getName()) : 1;
//                }
//                return -1;
//            }
//        });
//
//        dataGrid.addColumnSortHandler(sortHandler);
//        dataGrid.getColumnSortList().push(nameColumn);
        updateDataGrid();
    }

    private void initPager() {
        SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
        pager = new SimplePager(SimplePager.TextLocation.CENTER, pagerResources, false, 0, true);
        pager.setDisplay(dataGrid);
    }

    private void updateDataGrid() {
        jobService.getJobs(new AsyncCallback<List<JobDto>>() {

            @Override
            public void onFailure(Throwable caught) {
            }

            @Override
            public void onSuccess(List<JobDto> result) {
                dataProvider = new ListDataProvider<JobDto>();
                dataProvider.setList(result);
                dataProvider.addDataDisplay(dataGrid);
                dataGrid.setRowCount(result.size());
            }
        });

    }

    ProvidesKey<JobDto> keyProvider = new ProvidesKey<JobDto>() {
        public Object getKey(JobDto job) {
            return (job == null) ? null : job.getId();
        }
    };

    public List<JobDto> getSelectedJobs() {
        List<JobDto> jobList = (List<JobDto>) dataProvider.getList();
        List<JobDto> selectedJobs = new ArrayList<JobDto>();

        Long i = 0L;

        for (JobDto jobDTO : jobList) {
            if (selectionModel.isSelected(jobDTO)) {
                selectedJobs.add(jobDTO);
            }
            i++;
        }

        return selectedJobs;
    }

}
