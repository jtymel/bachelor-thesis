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
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.Widget;
import gwtEntity.client.JobDto;
import gwtEntity.server.JenkinsDownloader;
import java.util.List;

/**
 *
 * @author jtymel
 */
public class DesktopWidget extends Composite {

    private static DesktopWidgetUiBinder uiBinder = GWT.create(DesktopWidgetUiBinder.class);

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
                JenkinsDownloader.downloadResults(job);
            }                        
//            Window.alert("Tady by se v budoucnu mely stahovat vysledky (napr. volanim staticke metody)");
        }
    }

  
    public DesktopWidget() {
        initWidget(uiBinder.createAndBindUi(this));

        menuDownloadAll.setScheduledCommand(new DownloadAllCommand());
    }
}
