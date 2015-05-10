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

import gwtEntity.client.widgets.bridges.ParamBuildResultListBridge;
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
import gwtEntity.client.BuildDto;
import gwtEntity.client.ParameterizedBuildDto;
import gwtEntity.common.services.ParameterizedBuildService;
import gwtEntity.common.services.ParameterizedBuildServiceAsync;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jtymel
 */
public class ParamBuildList extends Composite {

    private static ParamBuildListUiBinder uiBinder = GWT.create(ParamBuildListUiBinder.class);

    private final ParameterizedBuildServiceAsync paramBuildService = GWT.create(ParameterizedBuildService.class);

    private BuildListParamBuildListBridge buildListParamBuildListBridge;
    private ParamBuildResultListBridge paramBuildResultListBridge;

    interface ParamBuildListUiBinder extends UiBinder<Widget, ParamBuildList> {
    }

    @UiField(provided = true)
    DataGrid<ParameterizedBuildDto> dataGrid;

    @UiField(provided = true)
    SimplePager pager;

    @UiField
    Button cancelButton;

    private BuildDto build;

    private SelectionModel<ParameterizedBuildDto> selectionModel;
    private ListDataProvider<ParameterizedBuildDto> dataProvider;

    public ParamBuildList() {
        dataGrid = new DataGrid<ParameterizedBuildDto>(20);
        initDatagrid();
        initPager();
        initWidget(uiBinder.createAndBindUi(this));
    }

    public void setBuildListParamBuildListBridge(BuildListParamBuildListBridge bridge) {
        buildListParamBuildListBridge = bridge;
    }

    public void setParamBuildResultListBridge(ParamBuildResultListBridge bridge) {
        paramBuildResultListBridge = bridge;
    }

    @UiHandler("cancelButton")
    void onCancelButtonClick(ClickEvent event) {
        buildListParamBuildListBridge.cancelParamBuildListAndDisplazBuildList();
    }

    void setBuild(BuildDto buildDto) {
        if (buildDto == null) {
            throw new RuntimeException("Build must not be null");
        } else {
            build = buildDto;
        }
    }

    private void initDatagrid() {

        TextColumn<ParameterizedBuildDto> nameColumn = new TextColumn<ParameterizedBuildDto>() {
            @Override
            public String getValue(ParameterizedBuildDto object) {
                return object.getName();

            }
        };

        TextColumn<ParameterizedBuildDto> labelColumn = new TextColumn<ParameterizedBuildDto>() {
            @Override
            public String getValue(ParameterizedBuildDto object) {
                String label = object.getUrl().substring(0, object.getUrl().lastIndexOf("/"));
                label = label.substring(0, label.lastIndexOf("/"));
                label = label.substring(label.lastIndexOf("/") + 1, label.length());
                return label;
            }
        };

        TextColumn<ParameterizedBuildDto> urlColumn = new TextColumn<ParameterizedBuildDto>() {
            @Override
            public String getValue(ParameterizedBuildDto object) {
                return object.getUrl();
            }
        };

        dataGrid.setColumnWidth(nameColumn, 15, Style.Unit.PX);
        dataGrid.addColumn(nameColumn, "Name");

        dataGrid.setColumnWidth(labelColumn, 25, Style.Unit.PX);
        dataGrid.addColumn(labelColumn, "Parameterization");

        dataGrid.setColumnWidth(urlColumn, 40, Style.Unit.PX);
        dataGrid.addColumn(urlColumn, "URL");

        selectionModel = new SingleSelectionModel<ParameterizedBuildDto>(keyProvider);

        dataGrid.addDomHandler(new DoubleClickHandler() {

            @Override
            public void onDoubleClick(DoubleClickEvent event) {
                List<ParameterizedBuildDto> paramBuilds = getSelectedParamBuilds();

                for (ParameterizedBuildDto paramBuild : paramBuilds) {
                    paramBuildResultListBridge.setParamBuildAndDisplayResults(paramBuild);
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
        paramBuildService.getParamBuilds(build, new AsyncCallback<List<ParameterizedBuildDto>>() {

            @Override
            public void onFailure(Throwable caught) {
            }

            @Override
            public void onSuccess(List<ParameterizedBuildDto> result) {
                dataProvider = new ListDataProvider<ParameterizedBuildDto>();
                dataProvider.setList(result);
                dataProvider.addDataDisplay(dataGrid);
                dataGrid.setRowCount(result.size());
            }
        });

    }

    ProvidesKey<ParameterizedBuildDto> keyProvider = new ProvidesKey<ParameterizedBuildDto>() {
        @Override
        public Object getKey(ParameterizedBuildDto paramBuild) {
            return (paramBuild == null) ? null : paramBuild.getId();
        }
    };

    public List<ParameterizedBuildDto> getSelectedParamBuilds() {
        List<ParameterizedBuildDto> paramBuildList = (List<ParameterizedBuildDto>) dataProvider.getList();
        List<ParameterizedBuildDto> selectedParamBuilds = new ArrayList<ParameterizedBuildDto>();

        Long i = 0L;

        for (ParameterizedBuildDto paramBuildDto : paramBuildList) {
            if (selectionModel.isSelected(paramBuildDto)) {
                selectedParamBuilds.add(paramBuildDto);
            }
            i++;
        }

        return selectedParamBuilds;
    }
}
