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

import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;
import java.util.Iterator;
import java.util.List;
import org.jboss.ci.tracker.common.objects.CategorizationDto;
import org.jboss.ci.tracker.common.objects.CategoryDto;
import org.jboss.ci.tracker.common.objects.FilterDto;
import org.jboss.ci.tracker.common.objects.PossibleResultDto;

/**
 *
 * @author jtymel
 */
public class CustomWidgets {

    private static final String CATEGORY_SEPARATOR_PREFIX = "ctg:";
    private static final String POSSIBLE_RESULT_SEPARATOR_PREFIX = "posRes:";

    public static DialogBox alertWidget(final String header, final String content) {
        final DialogBox box = new DialogBox();
        final VerticalPanel panel = new VerticalPanel();
        box.setText(header);

        final Button buttonClose = new Button("Close", new ClickHandler() {
            @Override
            public void onClick(final ClickEvent event) {
                box.hide();
            }
        });

        panel.add(new Label(content));

        final Label emptyLabel = new Label("");
        emptyLabel.setSize("auto", "80px");
        panel.add(emptyLabel);

        buttonClose.setWidth("90px");
        panel.add(buttonClose);
        panel.setCellHorizontalAlignment(buttonClose, HasAlignment.ALIGN_RIGHT);

        box.setAutoHideEnabled(true);
        box.add(panel);
        return box;
    }

    public static DialogBox filterDialogBox(final ResultList resultList, List<CategorizationDto> categorizations, List<CategoryDto> categories, List<PossibleResultDto> possibleResults, FilterDto oldFilter) {
        final DialogBox box = new DialogBox();
        final VerticalPanel panel = new VerticalPanel();

        panel.setSize("20em", "20em");

        box.setText("Filter results");

        // ----------------------- Possible results
        final Label resultsLabel = new Label("Results");
        panel.add(resultsLabel);

        for (PossibleResultDto possibleResult : possibleResults) {
            final CheckBox checkBox = new CheckBox(possibleResult.getName());
            checkBox.setName(POSSIBLE_RESULT_SEPARATOR_PREFIX + possibleResult.getId().toString());
            panel.add(checkBox);
        }

        // ----------------------- Categorizations and categories
        for (CategorizationDto categorization : categorizations) {
            final Label categorizationLabel = new Label(categorization.getName());
            panel.add(categorizationLabel);

            for (CategoryDto category : categories) {
                if (category.getCategorizationId().equals(categorization.getId())) {
                    final CheckBox checkBox = new CheckBox(category.getName());
                    checkBox.setName(CATEGORY_SEPARATOR_PREFIX + category.getId().toString());
                    panel.add(checkBox);
                }
            }

        }

        // ----------------------- Date from
        final DateTimeFormat dateTimeFormat = DateTimeFormat.getFormat("d.M.yyyy");
        final DateBox dateBoxFrom = new DateBox();
        dateBoxFrom.setFormat(new DateBox.DefaultFormat(dateTimeFormat));
        dateBoxFrom.getDatePicker().setYearArrowsVisible(true);

        panel.add(new Label("From"));
        dateBoxFrom.setTitle("Midnight of the day, i.e. time 00:00");
        panel.add(dateBoxFrom);

        // ----------------------- Date to
        final DateBox dateBoxTo = new DateBox();
        dateBoxTo.setFormat(new DateBox.DefaultFormat(dateTimeFormat));
        dateBoxTo.getDatePicker().setYearArrowsVisible(true);

        panel.add(new Label("To"));
        dateBoxTo.setTitle("Midnight of the day, i.e. time 00:00");
        panel.add(dateBoxTo);

        // ----------------------- Set widgets according to filter
        setWidgetValues(panel, oldFilter, dateBoxFrom, dateBoxTo);

        // ----------------------- Filter button
        final Button buttonFilter = new Button("OK", new ClickHandler() {
            @Override
            public void onClick(final ClickEvent event) {

                FilterDto filter = new FilterDto();

                Iterator<Widget> arrayOfWidgets = panel.iterator();
                while (arrayOfWidgets.hasNext()) {
                    Widget widget = arrayOfWidgets.next();

                    if (widget instanceof CheckBox) {
                        CheckBox checkBox = (CheckBox) widget;
                        if (checkBox.getValue()) {
                            if (checkBox.getName().startsWith(CATEGORY_SEPARATOR_PREFIX)) {
                                filter.addCategoryId(Long.parseLong(checkBox.getName().substring(CATEGORY_SEPARATOR_PREFIX.length())));
                            } else if (checkBox.getName().startsWith(POSSIBLE_RESULT_SEPARATOR_PREFIX)) {
                                filter.addPossibleResultId(Long.parseLong(checkBox.getName().substring(POSSIBLE_RESULT_SEPARATOR_PREFIX.length())));
                            }
                        }
                    }
                }

                filter.setDateFrom(dateBoxFrom.getValue());
                filter.setDateTo(dateBoxTo.getValue());

                resultList.filterResults(filter);
                box.hide();

            }
        });

        buttonFilter.setWidth("5em");
        panel.add(buttonFilter);
        panel.setCellHorizontalAlignment(buttonFilter, HasAlignment.ALIGN_RIGHT);

        // ----------------------- Show all results button
        final Button buttonShowAll = new Button("Clear", new ClickHandler() {
            @Override
            public void onClick(final ClickEvent event) {
                resultList.filterResults(null);
                box.hide();
            }
        });

        buttonShowAll.setWidth("5em");
        panel.add(buttonShowAll);
        panel.setCellHorizontalAlignment(buttonShowAll, HasAlignment.ALIGN_RIGHT);

        // ----------------------- Cancel button
        final Button buttonCancel = new Button("Cancel", new ClickHandler() {
            @Override
            public void onClick(final ClickEvent event) {
                box.hide();
            }
        });

        buttonCancel.setWidth("5em");
        panel.add(buttonCancel);
        panel.setCellHorizontalAlignment(buttonCancel, HasAlignment.ALIGN_RIGHT);

        box.add(panel);
        return box;
    }

    private static void setWidgetValues(VerticalPanel panel, FilterDto filter, DateBox dateBoxFrom, DateBox dateBoxTo) {
        if (filter == null) {
            return;
        }

        Iterator<Widget> arrayOfWidgets = panel.iterator();
        while (arrayOfWidgets.hasNext()) {
            Widget widget = arrayOfWidgets.next();

            if (widget instanceof CheckBox) {
                CheckBox checkBox = (CheckBox) widget;

                if (checkBox.getName().startsWith(CATEGORY_SEPARATOR_PREFIX)) {
                    checkBox.setValue(filter.getCategoryIds().contains(Long.parseLong(checkBox.getName().substring(CATEGORY_SEPARATOR_PREFIX.length()))));
                } else if (checkBox.getName().startsWith(POSSIBLE_RESULT_SEPARATOR_PREFIX)) {
                    checkBox.setValue(filter.getPossibleResultIds().contains(Long.parseLong(checkBox.getName().substring(POSSIBLE_RESULT_SEPARATOR_PREFIX.length()))));
                }

            }
        }

        dateBoxFrom.setValue(filter.getDateFrom());
        dateBoxTo.setValue(filter.getDateTo());

    }

    // Keep here only in case it's used in {@link org.jboss.ci.tracker.client.widgets.CategorizationList}
    // Otherwise use superclass (EditTextCell) instead
    public static class CustomCell extends EditTextCell {

        @Override
        public void render(com.google.gwt.cell.client.Cell.Context context,
                String value, SafeHtmlBuilder sb) {
            CategorizationDto data = (CategorizationDto) context.getKey();

            if (data.isReadOnly()) {
                sb.appendHtmlConstant(
                        "<div contentEditable=\'false\' unselectable ='false'>" + value + "</div >");
            } else {
                super.render(context, value, sb);
            }

        }
    }

}
