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

import gwtEntity.client.widgets.bridges.JobListBuildListBridge;
import gwtEntity.client.widgets.bridges.BuildListResultListBridge;
import gwtEntity.client.widgets.bridges.BuildListParamBuildListBridge;
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
import gwtEntity.common.objects.BuildDto;
import gwtEntity.common.services.BuildService;
import gwtEntity.common.objects.JobDto;
import gwtEntity.common.services.ResultService;
import gwtEntity.common.services.BuildServiceAsync;
import gwtEntity.common.services.ResultServiceAsync;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jtymel
 */
public class BuildList extends Composite {

    private static BuildListUiBinder uiBinder = GWT.create(BuildListUiBinder.class);

    private final BuildServiceAsync buildService = GWT.create(BuildService.class);
    private final ResultServiceAsync resultService = GWT.create(ResultService.class);

    private JobListBuildListBridge jobListBuildListBridge;
    private BuildListParamBuildListBridge buildListParamBuildListBridge;
    private BuildListResultListBridge buildListResultListBridge;

    interface BuildListUiBinder extends UiBinder<Widget, BuildList> {
    }

    @UiField(provided = true)
    DataGrid<BuildDto> dataGrid;

    @UiField(provided = true)
    SimplePager pager;

    @UiField
    Button showResultsButton;

    @UiField
    Button cancelButton;

    private JobDto job;

    private SelectionModel<BuildDto> selectionModel;
    private ListDataProvider<BuildDto> dataProvider;

    public BuildList() {
        dataGrid = new DataGrid<BuildDto>(500);
        initDatagrid();
        initPager();
        initWidget(uiBinder.createAndBindUi(this));
    }

    public void setJobListBuildListBridge(JobListBuildListBridge bridge) {
        jobListBuildListBridge = bridge;
    }

    public void setBuildListParamBuildListBridge(BuildListParamBuildListBridge bridge) {
        buildListParamBuildListBridge = bridge;
    }

    public void setBuildListResultListBridge(BuildListResultListBridge bridge) {
        buildListResultListBridge = bridge;
    }

    @UiHandler("showResultsButton")
    public void onShowResultButtonClick(ClickEvent event) {
        List<BuildDto> buildList = getSelectedBuilds();

        for (BuildDto buildDto : buildList) {
            if (selectionModel.isSelected(buildDto)) {
                buildListResultListBridge.setBuildAndDisplayResults(buildDto);
            }
        }

    }

    @UiHandler("cancelButton")
    public void onCancelButtonClick(ClickEvent event) {
        jobListBuildListBridge.cancelBuildListAndDisplayJobList();
    }

    void setJob(JobDto jobDto) {
        if (jobDto == null) {
            throw new RuntimeException("Job must not be null");
        } else {
            job = jobDto;
        }
    }

    private void initDatagrid() {

        TextColumn<BuildDto> nameColumn = new TextColumn<BuildDto>() {
            @Override
            public String getValue(BuildDto object) {
                return object.getName();
            }
        };

        TextColumn<BuildDto> urlColumn = new TextColumn<BuildDto>() {
            @Override
            public String getValue(BuildDto object) {
                return object.getUrl();
            }
        };

        dataGrid.setColumnWidth(nameColumn, 40, Style.Unit.PX);
        dataGrid.addColumn(nameColumn, "Name");

        dataGrid.setColumnWidth(urlColumn, 40, Style.Unit.PX);
        dataGrid.addColumn(urlColumn, "URL");

        selectionModel = new SingleSelectionModel<BuildDto>(keyProvider);

        dataGrid.addDomHandler(new DoubleClickHandler() {

            @Override
            public void onDoubleClick(DoubleClickEvent event) {
                List<BuildDto> buildList = getSelectedBuilds();

                for (BuildDto buildDto : buildList) {
                    buildListParamBuildListBridge.setBuildAndDisplayParamBuilds(buildDto);
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

    public void onTabShow() {
        updateDataGrid();
    }

    public void updateDataGrid() {
        buildService.getBuilds(job, new AsyncCallback<List<BuildDto>>() {

            @Override
            public void onFailure(Throwable caught) {
            }

            @Override
            public void onSuccess(List<BuildDto> result) {
                dataProvider = new ListDataProvider<BuildDto>();
                dataProvider.setList(result);
                dataProvider.addDataDisplay(dataGrid);
                dataGrid.setRowCount(result.size());
            }
        });

    }

    ProvidesKey<BuildDto> keyProvider = new ProvidesKey<BuildDto>() {
        @Override
        public Object getKey(BuildDto category) {
            return (category == null) ? null : category.getId();
        }
    };

    public List<BuildDto> getSelectedBuilds() {
        List<BuildDto> buildList = (List<BuildDto>) dataProvider.getList();
        List<BuildDto> selectedBuilds = new ArrayList<BuildDto>();

        Long i = 0L;

        for (BuildDto buildDto : buildList) {
            if (selectionModel.isSelected(buildDto)) {
                selectedBuilds.add(buildDto);
            }
            i++;
        }

        return selectedBuilds;
    }
}
