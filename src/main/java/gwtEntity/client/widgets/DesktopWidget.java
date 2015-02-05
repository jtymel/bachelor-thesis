/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gwtEntity.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.Widget;
import gwtEntity.client.BuildDto;
import gwtEntity.client.BuildService;
import gwtEntity.client.BuildServiceAsync;
import gwtEntity.client.JenkinsService;
import gwtEntity.client.JenkinsServiceAsync;
import gwtEntity.client.JobDto;
import gwtEntity.client.JobService;
import gwtEntity.client.JobServiceAsync;
import gwtEntity.client.ParameterizedBuildDto;
import gwtEntity.client.ParameterizedBuildService;
import gwtEntity.client.ParameterizedBuildServiceAsync;
import gwtEntity.server.JenkinsDownloader;
import java.util.List;

/**
 *
 * @author jtymel
 */
public class DesktopWidget extends Composite {

    private static DesktopWidgetUiBinder uiBinder = GWT.create(DesktopWidgetUiBinder.class);
    
    private final JenkinsServiceAsync jenkinsService = GWT.create(JenkinsService.class);
    private final BuildServiceAsync buildService = GWT.create(BuildService.class);
    private final ParameterizedBuildServiceAsync paramBuildService = GWT.create(ParameterizedBuildService.class);

    interface DesktopWidgetUiBinder extends UiBinder<Widget, DesktopWidget> {
    }

    @UiField
    MenuItem menuDownloadAll;
    
    @UiField
    MainPanel mainPanel;

    private class DownloadAllCommand implements Scheduler.ScheduledCommand {
        @Override
        public void execute() {
            List<JobDto> jobList = mainPanel.getSelectedJobs();
            for (JobDto job : jobList) {
                Window.alert(job.getUrl());
                jenkinsService.downloadBuilds(job, new AsyncCallback<List<BuildDto>>() {

                    @Override
                    public void onFailure(Throwable caught) {
                        Window.alert("Error during parsing builds " + caught.toString());
                    }

                    @Override
                    public void onSuccess(List<BuildDto> result) {
                        for (final BuildDto build : result) {
                            buildService.saveBuild(build, new AsyncCallback<Long>() {

                                @Override
                                public void onFailure(Throwable caught) {
                                    Window.alert("Error during saving builds " + caught.toString());
                                }

                                @Override
                                public void onSuccess(Long result) {
                                    Window.alert("New Build: " + result);
                                    jenkinsService.downloadParameterizedBuilds(build, new AsyncCallback<List<ParameterizedBuildDto>>() {

                                        @Override
                                        public void onFailure(Throwable caught) {
                                        }

                                        @Override
                                        public void onSuccess(List<ParameterizedBuildDto> result) {
                                            for (ParameterizedBuildDto paramBuild : result) {
                                                paramBuildService.saveParamBuild(paramBuild, new AsyncCallback<Long>() {

                                                    @Override
                                                    public void onFailure(Throwable caught) {
                                                    }

                                                    @Override
                                                    public void onSuccess(Long result) {
                                                    }
                                                });
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    }
                });
            }                        
        }
    }

  
    public DesktopWidget() {
        initWidget(uiBinder.createAndBindUi(this));

        menuDownloadAll.setScheduledCommand(new DownloadAllCommand());
    }
}
