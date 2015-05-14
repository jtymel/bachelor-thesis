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

import org.jboss.ci.tracker.client.widgets.bridges.CategorizationListDetailBridge;
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
import org.jboss.ci.tracker.common.objects.CategorizationDto;
import org.jboss.ci.tracker.common.services.CategorizationService;
import org.jboss.ci.tracker.common.services.CategorizationServiceAsync;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jtymel
 */
public class CategorizationList extends Composite {

    private static CategorizationListUiBinder uiBinder = GWT.create(CategorizationListUiBinder.class);

    private final CategorizationServiceAsync categorizationService = GWT.create(CategorizationService.class);

    private CategorizationListDetailBridge categorizationListDetailBridge;

    interface CategorizationListUiBinder extends UiBinder<Widget, CategorizationList> {
    }

    @UiField(provided = true)
    DataGrid<CategorizationDto> dataGrid;

    /*
     SimplePager has strange behaviour when the last page is reached. See https://code.google.com/p/google-web-toolkit/issues/detail?id=6163     
     */
    @UiField(provided = true)
    SimplePager pager;

    @UiField
    Button deleteButton;

    @UiField
    Button addButton;

    @UiField
    Button cancelButton;

    private SelectionModel<CategorizationDto> selectionModel;
    private ListDataProvider<CategorizationDto> dataProvider;

    public CategorizationList() {
        dataGrid = new DataGrid<CategorizationDto>(500);
        initDatagrid();
        initPager();
        initWidget(uiBinder.createAndBindUi(this));
    }

    public void setCategorizationListDetailBridge(CategorizationListDetailBridge bridge) {
        categorizationListDetailBridge = bridge;
    }

    @UiHandler("deleteButton")
    void onDeleteButtonClick(ClickEvent event) {
        List<CategorizationDto> categorizationLost = getSelectedCategorizations();

        for (CategorizationDto categorizationDto : categorizationLost) {
            if (selectionModel.isSelected(categorizationDto)) {
                deleteCategorization(categorizationDto);
            }
        }

    }

    @UiHandler("addButton")
    void onAddButtonClick(ClickEvent event) {
        categorizationListDetailBridge.setCategorizationAndDisplayDetail(null);
    }

    @UiHandler("cancelButton")
    void onCancelButtonClick(ClickEvent event) {
        categorizationListDetailBridge.cancelCategorizationList();
    }

    public void onTabShow() {
        updateDataGrid();
    }

    private void deleteCategorization(CategorizationDto categorizationDto) {
        categorizationService.deleteCategorization(categorizationDto, new AsyncCallback<Void>() {

            @Override
            public void onFailure(Throwable caught) {
            }

            @Override
            public void onSuccess(Void result) {
                updateDataGrid();
            }
        });
    }

    private void initDatagrid() {

        TextColumn<CategorizationDto> nameColumn = new TextColumn<CategorizationDto>() {
            @Override
            public String getValue(CategorizationDto object) {
                return object.getName();
            }
        };

        dataGrid.setColumnWidth(nameColumn, 40, Style.Unit.PX);
        dataGrid.addColumn(nameColumn, "Name");

        selectionModel = new SingleSelectionModel<CategorizationDto>(keyProvider);

        dataGrid.addDomHandler(new DoubleClickHandler() {

            @Override
            public void onDoubleClick(DoubleClickEvent event) {
                List<CategorizationDto> categorizationList = getSelectedCategorizations();

                for (CategorizationDto categorizationDto : categorizationList) {
                    categorizationListDetailBridge.setCategorizationAndDisplayDetail(categorizationDto);
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

    private void updateDataGrid() {
        categorizationService.getCategorizations(new AsyncCallback<List<CategorizationDto>>() {

            @Override
            public void onFailure(Throwable caught) {
            }

            @Override
            public void onSuccess(List<CategorizationDto> result) {
                dataProvider = new ListDataProvider<CategorizationDto>();
                dataProvider.setList(result);
                dataProvider.addDataDisplay(dataGrid);
                dataGrid.setRowCount(result.size());
            }
        });

    }

    ProvidesKey<CategorizationDto> keyProvider = new ProvidesKey<CategorizationDto>() {
        @Override
        public Object getKey(CategorizationDto categorization) {
            return (categorization == null) ? null : categorization.getId();
        }
    };

    public List<CategorizationDto> getSelectedCategorizations() {
        List<CategorizationDto> categorizationList = (List<CategorizationDto>) dataProvider.getList();
        List<CategorizationDto> selectedCategorizations = new ArrayList<CategorizationDto>();

        for (CategorizationDto categorizationDto : categorizationList) {
            if (selectionModel.isSelected(categorizationDto)) {
                selectedCategorizations.add(categorizationDto);
            }
        }

        return selectedCategorizations;
    }

}
