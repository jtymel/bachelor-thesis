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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;
import gwtEntity.client.BuildDto;
import gwtEntity.client.BuildService;
import gwtEntity.client.BuildServiceAsync;
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

    @UiField(provided = true)
    DataGrid<ResultDto> dataGrid;

    @UiField(provided = true)
    SimplePager pager;

    private SelectionModel<ResultDto> selectionModel;
    private ListDataProvider<ResultDto> dataProvider;
    private ParameterizedBuildDto paramBuild;

    public ResultList() {
        dataGrid = new DataGrid<ResultDto>(20);
        initDatagrid();
        initPager();
        initWidget(uiBinder.createAndBindUi(this));
    }

    public void setParamBuildResultListBridge(ParamBuildResultListBridge bridge) {
        paramBuildResultListBridge = bridge;
    }

    private void initDatagrid() {

        TextColumn<ResultDto> nameColumn = new TextColumn<ResultDto>() {
            @Override
            public String getValue(ResultDto object) {
                return object.getTest();
            }
        };

        dataGrid.setColumnWidth(nameColumn, 40, Style.Unit.PX);
        dataGrid.addColumn(nameColumn, "Name");

        selectionModel = new SingleSelectionModel<ResultDto>(keyProvider);

        dataGrid.addDomHandler(new DoubleClickHandler() {

            @Override
            public void onDoubleClick(DoubleClickEvent event) {
//                List<BuildDto> buildList = getSelectedBuilds();
//
//                for (BuildDto buildDto : buildList) {
//                    buildListParamBuildListBridge.setBuildAndDisplayParamBuilds(buildDto);
//                }                
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

    public void setParamBuild(ParameterizedBuildDto paramBuildDto) {
        paramBuild = paramBuildDto;
    }

    public void updateDataGrid() {
        if (dataGrid.getColumnCount() == 1) {
            resultService.getPossibleResults(new AsyncCallback<List<PossibleResultDto>>() {

                @Override
                public void onFailure(Throwable caught) {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public void onSuccess(List<PossibleResultDto> possibleResults) {
                    for (final PossibleResultDto possibleResult : possibleResults) {
                        TextColumn<ResultDto> resultColumn = new TextColumn<ResultDto>() {
                            @Override
                            public String getValue(ResultDto object) {
                                if (object.getResults().containsKey(possibleResult.getName())) {
                                    return object.getResults().get(possibleResult.getName()).toString();
                                } else {
                                    return "0";
                                }
                            }
                        };

                        dataGrid.setColumnWidth(resultColumn, 8, Style.Unit.PX);
                        dataGrid.addColumn(resultColumn, possibleResult.getName());
                    }
                    getResults();
                }
            });
        }

    }

    public void getResults() {
        resultService.getResults(paramBuild, new AsyncCallback<List<ResultDto>>() {

            @Override
            public void onFailure(Throwable caught) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void onSuccess(List<ResultDto> result) {
                dataProvider = new ListDataProvider<ResultDto>();
                dataProvider.setList(result);
                dataProvider.addDataDisplay(dataGrid);
                dataGrid.setRowCount(result.size());
            }
        });
    }

    ProvidesKey<ResultDto> keyProvider = new ProvidesKey<ResultDto>() {
        @Override
        public Object getKey(ResultDto category) {
            return (category == null) ? null : category.getTest();
        }
    };

    public List<ResultDto> getSelectedBuilds() {
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
