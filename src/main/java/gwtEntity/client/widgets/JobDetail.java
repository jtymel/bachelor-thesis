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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import gwtEntity.client.JobDto;
import gwtEntity.client.JobService;
import gwtEntity.client.JobServiceAsync;

/**
 *
 * @author jtymel
 */
public class JobDetail extends Composite {

    private static JobDetailUiBinder uiBinder = GWT.create(JobDetailUiBinder.class);
    
    private final JobServiceAsync jobService = GWT.create(JobService.class);
    
    private JobListDetailBridge jobListDetailBridge;

    void setJob(JobDto jobDTO) {
        if(jobDTO != null) {
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
        initWidget(uiBinder.createAndBindUi(this));
    }
    
    @UiField
    TextBox jobNameField;

    @UiField
    TextBox jobUrlField;
    
    @UiField
    Button saveButton;
    
    JobDto editedJob = null;
    
    @UiHandler("saveButton")
    void onSaveClick(ClickEvent event) {
        addJob();        
    }

    @UiHandler("jobNameField")
    void onKeyUp(KeyUpEvent event) {
        if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
            addJob();
        }
    }
    
    public void setJobListDetailBridge(JobListDetailBridge bridge) {       
        jobListDetailBridge = bridge; // I was stolen of 45 minutes of my life by this line! I want this time back! :-)
    }
        
    private void addJob() {
        JobDto jobDTO;
        
        if(editedJob == null) {
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
                jobListDetailBridge.displayList();
            }
        });

    }
    
//    public void displayJobDetail(JobDto jobDTO) {
//        jobNameField.setText(jobDTO.getName());
//        jobUrlField.setText(jobDTO.getUrl());
//        Window.alert("displayJobDetail: JobDetail got: " + jobDTO.getId() + " | " + jobDTO.getName() + ", " + jobDTO.getUrl());
//    }
            
}
