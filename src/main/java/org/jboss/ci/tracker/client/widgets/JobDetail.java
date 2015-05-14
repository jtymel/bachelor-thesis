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

import org.jboss.ci.tracker.client.widgets.bridges.JobListDetailBridge;
import org.jboss.ci.tracker.client.widgets.bridges.JobDetailLabelDetailBridge;
import org.jboss.ci.tracker.client.widgets.bridges.JobDetailCategoriesBridge;
import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
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
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;
import org.jboss.ci.tracker.common.objects.JobDto;
import org.jboss.ci.tracker.common.services.JobService;
import org.jboss.ci.tracker.common.services.JobServiceAsync;
import org.jboss.ci.tracker.common.objects.LabelDto;
import org.jboss.ci.tracker.common.services.LabelService;
import org.jboss.ci.tracker.common.services.LabelServiceAsync;
import java.util.List;

/**
 *
 * @author jtymel
 */
public class JobDetail extends Composite {

    private static JobDetailUiBinder uiBinder = GWT.create(JobDetailUiBinder.class);

    private final JobServiceAsync jobService = GWT.create(JobService.class);

    private final LabelServiceAsync labelService = GWT.create(LabelService.class);

    private JobListDetailBridge jobListDetailBridge;
    private JobDetailLabelDetailBridge jobDetailLabelDetailBridge;
    private JobDetailCategoriesBridge jobDetailCategoriesBridge;

    void setJob(JobDto jobDTO) {
        if (jobDTO != null) {
            jobNameField.setText(jobDTO.getName());
            jobUrlField.setText(jobDTO.getUrl());
        } else {
            jobNameField.setText("");
            jobUrlField.setText("");
        }

        editedJob = jobDTO;
    }

    interface JobDetailUiBinder extends UiBinder<Widget, JobDetail> {
    }

    public JobDetail() {
        dataGrid = new DataGrid<LabelDto>(500);
        initDatagrid();
        initPager();
        initWidget(uiBinder.createAndBindUi(this));
    }

    @UiField
    TextBox jobNameField;

    @UiField
    TextBox jobUrlField;

    @UiField
    Button saveButton;

    @UiField
    Button setCategories;

    @UiField(provided = true)
    DataGrid<LabelDto> dataGrid;

    @UiField(provided = true)
    SimplePager pager;

    private SelectionModel<LabelDto> selectionModel;
    private ListDataProvider<LabelDto> dataProvider;

    JobDto editedJob = null;

    @UiHandler("saveButton")
    void onSaveClick(ClickEvent event) {
        addJob();
    }

    @UiHandler("setCategories")
    void onSetCategoriesClick(ClickEvent event) {
        jobDetailCategoriesBridge.setJobAndDisplayCategories(editedJob);
    }

    @UiHandler("jobNameField")
    void onKeyUp(KeyUpEvent event) {
        if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
            addJob();
        }
    }

    @UiHandler("cancelButton")
    void onCancelButtonClick(ClickEvent event) {
        jobListDetailBridge.cancelJobDetailAndDisplayJobList();
    }

    public void onTabShow() {
        getParameterizations();
    }

    public void setJobListDetailBridge(JobListDetailBridge bridge) {
        jobListDetailBridge = bridge; // I was stolen of 45 minutes of my life by this line! I want this time back! :-)
    }

    private void addJob() {
        JobDto jobDTO;

        if (editedJob == null) {
            jobDTO = new JobDto(jobNameField.getText(), jobUrlField.getText());
        } else {
            jobDTO = editedJob;
            jobDTO.setName(jobNameField.getText());
            jobDTO.setUrl(jobUrlField.getText());

            editedJob = null;
        }

//        Window.alert("JobDetail got: " + jobDTO.getId() + " | " + jobDTO.getName() + ", " + jobDTO.getUrl());                        
        jobService.saveJob(jobDTO, new AsyncCallback<Long>() {

            @Override
            public void onFailure(Throwable caught) {
            }

            @Override
            public void onSuccess(Long result) {
                jobListDetailBridge.cancelJobDetailAndDisplayJobList();
            }
        });

    }

    private void initDatagrid() {

        TextColumn<LabelDto> nameColumn = new TextColumn<LabelDto>() {
            @Override
            public String getValue(LabelDto object) {
                return object.getName();
            }
        };

        ButtonCell buttonCell = new ButtonCell();
        Column buttonColumn = new Column<LabelDto, String>(buttonCell) {
            @Override
            public String getValue(LabelDto object) {
                // The value to display in the button.
                return "Edit categories";
            }
        };

        buttonColumn.setFieldUpdater(new FieldUpdater<LabelDto, String>() {
            public void update(int index, LabelDto object, String value) {
                jobDetailLabelDetailBridge.setLabelAndDisplayDetail(object, editedJob);
            }
        });

        dataGrid.setColumnWidth(nameColumn, 40, Style.Unit.PX);
        dataGrid.addColumn(nameColumn, "Parameterization");

        dataGrid.setColumnWidth(buttonColumn, 40, Style.Unit.PX);
        dataGrid.addColumn(buttonColumn, "Edit label");

        selectionModel = new SingleSelectionModel<LabelDto>(keyProvider);

        dataGrid.setSelectionModel(selectionModel);
    }

    ProvidesKey<LabelDto> keyProvider = new ProvidesKey<LabelDto>() {
        @Override
        public Object getKey(LabelDto label) {
            return (label == null) ? null : label.getId();
        }
    };

    private void initPager() {
        SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
        pager = new SimplePager(SimplePager.TextLocation.CENTER, pagerResources, false, 0, true);
        pager.setDisplay(dataGrid);
    }

    private void getParameterizations() {
        if (editedJob == null) {
            dataProvider = new ListDataProvider<LabelDto>();
            dataProvider.addDataDisplay(dataGrid);
        } else {
            labelService.getLabels(editedJob, new AsyncCallback<List<LabelDto>>() {

                @Override
                public void onFailure(Throwable caught) {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public void onSuccess(List<LabelDto> result) {
                    dataProvider = new ListDataProvider<LabelDto>();
                    dataProvider.setList(result);
                    dataProvider.addDataDisplay(dataGrid);
                    dataGrid.setRowCount(result.size());
                }
            });
        }
    }

    public void setJobDetailLabelDetailBridge(JobDetailLabelDetailBridge bridge) {
        jobDetailLabelDetailBridge = bridge;
    }

    public void setJobDetailCategoriesBridge(JobDetailCategoriesBridge bridge) {
        jobDetailCategoriesBridge = bridge;
    }
}
