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
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.Widget;

/**
 *
 * @author jtymel
 */
public class DesktopWidget extends Composite {

    private static DesktopWidgetUiBinder uiBinder = GWT.create(DesktopWidgetUiBinder.class);

    interface DesktopWidgetUiBinder extends UiBinder<Widget, DesktopWidget> {
    }

    @UiField
    MenuItem menuCategorizations;

    @UiField
    MenuItem menuCategories;

    @UiField
    MainPanel mainPanel;

    private class showCategorizationsCommand implements Scheduler.ScheduledCommand {

        @Override
        public void execute() {
            mainPanel.showCategorizationList();
        }

    }

    private class showCategoriesCommand implements Scheduler.ScheduledCommand {

        @Override
        public void execute() {
            mainPanel.showCategoryList();
        }
    }

    public DesktopWidget() {
        initWidget(uiBinder.createAndBindUi(this));
        menuCategorizations.setScheduledCommand(new showCategorizationsCommand());
        menuCategories.setScheduledCommand(new showCategoriesCommand());
    }
}
