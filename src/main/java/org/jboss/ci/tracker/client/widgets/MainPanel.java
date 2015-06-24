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

import org.jboss.ci.tracker.client.widgets.bridges.ResultListTestDetailBridge;
import org.jboss.ci.tracker.client.widgets.bridges.ParamBuildResultListBridge;
import org.jboss.ci.tracker.client.widgets.bridges.JobListResultListBridge;
import org.jboss.ci.tracker.client.widgets.bridges.JobListDetailBridge;
import org.jboss.ci.tracker.client.widgets.bridges.JobListBuildListBridge;
import org.jboss.ci.tracker.client.widgets.bridges.JobDetailLabelDetailBridge;
import org.jboss.ci.tracker.client.widgets.bridges.JobDetailCategoriesBridge;
import org.jboss.ci.tracker.client.widgets.bridges.CategoryListDetailBridge;
import org.jboss.ci.tracker.client.widgets.bridges.CategorizationListDetailBridge;
import org.jboss.ci.tracker.client.widgets.bridges.BuildListResultListBridge;
import org.jboss.ci.tracker.client.widgets.bridges.BuildListParamBuildListBridge;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import java.util.Collection;
import org.jboss.ci.tracker.common.objects.BuildDto;
import org.jboss.ci.tracker.common.objects.CategorizationDto;
import org.jboss.ci.tracker.common.objects.CategoryDto;
import org.jboss.ci.tracker.common.objects.JobDto;
import org.jboss.ci.tracker.common.objects.LabelDto;
import org.jboss.ci.tracker.common.objects.ParameterizedBuildDto;
import org.jboss.ci.tracker.common.objects.PossibleResultDto;
import org.jboss.ci.tracker.common.objects.ResultDto;
import java.util.List;

/**
 *
 * @author jtymel
 */
public class MainPanel extends Composite implements JobListDetailBridge, CategorizationListDetailBridge, CategoryListDetailBridge, JobDetailLabelDetailBridge, JobDetailCategoriesBridge, JobListBuildListBridge, BuildListParamBuildListBridge, ParamBuildResultListBridge, BuildListResultListBridge, JobListResultListBridge, ResultListTestDetailBridge {

    private static MainPanelUiBinder uiBinder = GWT.create(MainPanelUiBinder.class);

    interface MainPanelUiBinder extends UiBinder<Widget, MainPanel> {
    }

    @Override
    public void setJobAndDisplayDetail(JobDto jobDTO) {
        jobDetail.setJob(jobDTO);
        tabPanel.add(jobDetail, "Job detail");
        tabPanel.selectTab(jobDetail);
    }

    @Override
    public void cancelJobDetailAndDisplayJobList() {
        tabPanel.remove(jobDetail);
        tabPanel.selectTab(jobList);
    }

    @Override
    public void setCategorizationAndDisplayDetail(CategorizationDto categorizationDto) {
        categorizationDetail.setCategorization(categorizationDto);
        tabPanel.add(categorizationDetail, "Categorization detail");
        tabPanel.selectTab(categorizationDetail);
    }

    @Override
    public void cancelCategorizationDetailAndDisplayCategorizationList() {
        categorizationList.onTabShow();
        tabPanel.selectTab(categorizationList);
        tabPanel.remove(categorizationDetail);
    }

    @Override
    public void cancelCategorizationList() {
        tabPanel.remove(categorizationList);
    }

    @Override
    public void cancelCategoryList() {
        tabPanel.remove(categoryList);
    }

    @Override
    public void setCategoryAndDisplayDetail(CategoryDto categoryDto) {
        categoryDetail.setCategory(categoryDto);
        tabPanel.add(categoryDetail, "Category detail");
        tabPanel.selectTab(categoryDetail);
    }

    @Override
    public void cancelCategoryDetailAndDisplayCategoryList() {
        categoryList.onTabShow();
        tabPanel.selectTab(categoryList);
        tabPanel.remove(categoryDetail);
    }

    @Override
    public void setLabelAndDisplayDetail(LabelDto label, JobDto job) {
        labelDetail.setLabel(label, job);
        tabPanel.add(labelDetail, "Label detail");
        tabPanel.selectTab(labelDetail);
    }

    @Override
    public void cancelLabelDetailAndDisplayJobDetail() {
        tabPanel.remove(labelDetail);
        tabPanel.selectTab(jobDetail);
    }

    @Override
    public void setJobAndDisplayCategories(JobDto job) {
        jobCategories.setJob(job);
        tabPanel.add(jobCategories, "Job categories");
        tabPanel.selectTab(jobCategories);
    }

    @Override
    public void cancelJobCategoriesAndDisplayJobDetail() {
        tabPanel.remove(jobCategories);
        tabPanel.selectTab(jobDetail);
    }

    @Override
    public void setJobAndDisplayBuilds(JobDto job) {
        buildList.setJob(job);
        tabPanel.add(buildList, "List of builds");
        tabPanel.selectTab(buildList);
    }

    @Override
    public void setBuildAndDisplayParamBuilds(BuildDto build) {
        paramBuildList.setBuild(build);
        tabPanel.add(paramBuildList, "List of parameterized builds");
        tabPanel.selectTab(paramBuildList);
    }

    @Override
    public void setParamBuildAndDisplayResults(ParameterizedBuildDto paramBuild) {
        resultList.showParamBuildResults(paramBuild);
        tabPanel.add(resultList, "List of results");
        tabPanel.selectTab(resultList);
    }

    @Override
    public void setBuildAndDisplayResults(Collection<BuildDto> builds) {
        resultList.showBuildResults(builds);
        tabPanel.add(resultList, "List of results");
        tabPanel.selectTab(resultList);
    }

    @Override
    public void setJobAndDisplayResults(JobDto job) {
        resultList.showJobResults(job);
        tabPanel.add(resultList, "List of results");
        tabPanel.selectTab(resultList);
    }

    @Override
    public void setTestAndDisplayHistory(ResultDto result, JobDto job, List<PossibleResultDto> possibleResults) {
        testDetail.showTestHistory(result, job, possibleResults);
        tabPanel.add(testDetail, "History of test");
        tabPanel.selectTab(testDetail);
    }

    @Override
    public void setTestAndDisplayHistory(ResultDto result, Collection<BuildDto> builds, List<PossibleResultDto> possibleResults) {
        testDetail.showTestHistory(result, builds, possibleResults);
        tabPanel.add(testDetail, "History of test");
        tabPanel.selectTab(testDetail);
    }

    @Override
    public void setTestAndDisplayHistory(ResultDto result, ParameterizedBuildDto paramBuild, List<PossibleResultDto> possibleResults) {
        testDetail.showTestHistory(result, paramBuild, possibleResults);
        tabPanel.add(testDetail, "History of test");
        tabPanel.selectTab(testDetail);
    }

    @Override
    public void cancelTestDetailAndDisplayResultList() {
        tabPanel.remove(testDetail);
        tabPanel.selectTab(resultList);
    }

    @Override
    public void cancelResultListAndDisplayJobList() {
        tabPanel.remove(resultList);
        tabPanel.selectTab(jobList);
    }

    @Override
    public void cancelResultListAndDisplayParamBuildList() {
        tabPanel.remove(resultList);
        tabPanel.selectTab(paramBuildList);
    }

    @Override
    public void cancelResultListAndDisplayBuildList() {
        tabPanel.remove(resultList);
        tabPanel.selectTab(buildList);
    }

    @Override
    public void cancelParamBuildListAndDisplazBuildList() {
        tabPanel.remove(paramBuildList);
        tabPanel.selectTab(buildList);
    }

    @Override
    public void cancelBuildListAndDisplayJobList() {
        tabPanel.remove(buildList);
        tabPanel.selectTab(jobList);
    }

    @UiField
    JobDetail jobDetail;

    @UiField
    JobList jobList;

    @UiField
    CategorizationList categorizationList;

    @UiField
    CategorizationDetail categorizationDetail;

    @UiField
    CategoryList categoryList;

    @UiField
    CategoryDetail categoryDetail;

    @UiField
    LabelDetail labelDetail;

    @UiField
    JobCategories jobCategories;

    @UiField
    BuildList buildList;

    @UiField
    ParamBuildList paramBuildList;

    @UiField
    ResultList resultList;

    @UiField
    TabLayoutPanel tabPanel;

    @UiField
    TestDetail testDetail;

    private int selectedTab;

    public MainPanel() {
        initWidget(uiBinder.createAndBindUi(this));
        setBridges();
        removeUnnecessaryTabs();
        addKeyHandler();
    }

    public List<JobDto> getSelectedJobs() {
        return jobList.getSelectedJobs();
    }

    public void showCategorizationList() {
        tabPanel.add(categorizationList, "List of categorizations");
        tabPanel.selectTab(categorizationList);
    }

    public void showCategoryList() {
        tabPanel.add(categoryList, "List of categories");
        tabPanel.selectTab(categoryList);
    }

    /**
     * Calls methods of tabs when the tab is selected (and when needed).
     *
     * @param event Selection event
     */
    @UiHandler("tabPanel")
    void onTabSelection(SelectionEvent<Integer> event) {
        selectedTab = event.getSelectedItem();

        if (tabPanel.getWidget(selectedTab).equals(labelDetail)) {
            labelDetail.onTabShow();
        }

        if (tabPanel.getWidget(selectedTab).equals(categoryDetail)) {
            categoryDetail.getCategorizations();
        }

        if (tabPanel.getWidget(selectedTab).equals(jobCategories)) {
            jobCategories.onTabShow();
        }

        if (tabPanel.getWidget(selectedTab).equals(buildList)) {
            buildList.onTabShow();
        }

        if (tabPanel.getWidget(selectedTab).equals(paramBuildList)) {
            paramBuildList.onTabShow();
        }

        if (tabPanel.getWidget(selectedTab).equals(jobDetail)) {
            jobDetail.onTabShow();
        }

        if (tabPanel.getWidget(selectedTab).equals(jobList)) {
            jobList.onTabShow();
        }

        if (tabPanel.getWidget(selectedTab).equals(categorizationList)) {
            categorizationList.onTabShow();
        }
    }

    /**
     * Sets bridges which permits collaboration between tabs.
     */
    private void setBridges() {
        jobList.setJobListDetailBridge(MainPanel.this);
        jobDetail.setJobListDetailBridge(MainPanel.this);
        categorizationList.setCategorizationListDetailBridge(MainPanel.this);
        categorizationDetail.setCategorizationListDetailBridge(MainPanel.this);
        categoryList.setCategoryListDetailBridge(MainPanel.this);
        categoryDetail.setCategoryListDetailBridge(MainPanel.this);
        jobDetail.setJobDetailLabelDetailBridge(MainPanel.this);
        labelDetail.setJobDetailLabelDetailBridge(MainPanel.this);
        jobDetail.setJobDetailCategoriesBridge(MainPanel.this);
        jobCategories.setJobDetailCategoriesBridge(MainPanel.this);
        jobList.setJobListBuildListBridge(MainPanel.this);
        buildList.setJobListBuildListBridge(MainPanel.this);
        buildList.setBuildListParamBuildListBridge(MainPanel.this);
        paramBuildList.setBuildListParamBuildListBridge(MainPanel.this);
        paramBuildList.setParamBuildResultListBridge(MainPanel.this);
        resultList.setParamBuildResultListBridge(MainPanel.this);
        buildList.setBuildListResultListBridge(MainPanel.this);
        resultList.setBuildListResultListBridge(MainPanel.this);
        jobList.setJobListResultListBridge(MainPanel.this);
        resultList.setJobListResultListBridge(MainPanel.this);
        resultList.setResultListTestDetailBridge(MainPanel.this);
        testDetail.setResultListTestDetailBridge(MainPanel.this);
    }

    /**
     * Removes unnecessary tabs when the application is starting.
     */
    private void removeUnnecessaryTabs() {
        tabPanel.remove(testDetail);
        tabPanel.remove(jobCategories);
        tabPanel.remove(resultList);
        tabPanel.remove(labelDetail);
        tabPanel.remove(paramBuildList);
        tabPanel.remove(buildList);
        tabPanel.remove(jobDetail);
        tabPanel.remove(categorizationList);
        tabPanel.remove(categorizationDetail);
        tabPanel.remove(categoryList);
        tabPanel.remove(categoryDetail);
    }

    /**
     * Adds key handler for closing tabs once the escape key is pressed with exception of JobList tab
     * {@link org.jboss.ci.tracker.client.widgets.JobList}.
     */
    private void addKeyHandler() {
        RootPanel.get().addDomHandler(new KeyDownHandler() {

            @Override
            public void onKeyDown(KeyDownEvent event) {
                if (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE && !tabPanel.getWidget(selectedTab).equals(jobList)) {
                    final int removedTabIndex = selectedTab;

                    tabPanel.remove(removedTabIndex);
                    tabPanel.selectTab(removedTabIndex - 1);
                }
            }
        }, KeyDownEvent.getType());
    }
}
