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

    private static final DesktopWidgetUiBinder uiBinder = GWT.create(DesktopWidgetUiBinder.class);

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
