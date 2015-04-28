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

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.cellview.client.SafeHtmlHeader;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;
import gwtEntity.client.JobDto;
import gwtEntity.client.JobService;
import gwtEntity.client.JobServiceAsync;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jtymel
 */
public class JobList extends Composite {

    private static JobListUiBinder uiBinder = GWT.create(JobListUiBinder.class);

    private final JobServiceAsync jobService = GWT.create(JobService.class);
    
    private JobListDetailBridge jobListDetailBridge;
    private JobListBuildListBridge jobListBuildListBridge;

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

    private SelectionModel<JobDto> selectionModel;
    private ListDataProvider<JobDto> dataProvider;

    public JobList() {
        dataGrid = new DataGrid<JobDto>(20);
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
                Window.alert("An error occured during addition categories to param build");
            }

            @Override
            public void onSuccess(Void result) {
                Window.alert("Categories were correctly added");
            }
        });
        }
        
    }   

    private void initDatagrid() {
        Column<JobDto, String> urlColumn = new Column<JobDto, String>(new TextCell()) {

            @Override
            public String getValue(JobDto object) {
                return object.getUrl();
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

        Header<String> nameFooter = new Header<String>(new TextCell()) {
            @Override
            public String getValue() {
                List<JobDto> items = dataGrid.getVisibleItems();
                return "Number of jobs: " + items.size();
            }
        };

        dataGrid.addColumn(nameColumn, new SafeHtmlHeader(new SafeHtml() {

            @Override
            public String asString() {
                return "Name";
            }
        }), nameFooter);

        dataGrid.setColumnWidth(nameColumn, 40, Style.Unit.PX);
        dataGrid.addColumn(urlColumn, "URL");
        dataGrid.setColumnWidth(urlColumn, 40, Style.Unit.PX);

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
        updateDataGrid();
    }

    private void initPager() {
        SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
        pager = new SimplePager(SimplePager.TextLocation.CENTER, pagerResources, false, 0, true);
        pager.setDisplay(dataGrid);
    }

    public void updateDataGrid() {
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
