package gwtEntity.client.widgets;

import gwtEntity.client.widgets.bridges.ResultListTestDetailBridge;
import gwtEntity.client.widgets.bridges.ParamBuildResultListBridge;
import gwtEntity.client.widgets.bridges.JobListResultListBridge;
import gwtEntity.client.widgets.bridges.JobListDetailBridge;
import gwtEntity.client.widgets.bridges.JobListBuildListBridge;
import gwtEntity.client.widgets.bridges.JobDetailLabelDetailBridge;
import gwtEntity.client.widgets.bridges.JobDetailCategoriesBridge;
import gwtEntity.client.widgets.bridges.CategoryListDetailBridge;
import gwtEntity.client.widgets.bridges.CategorizationListDetailBridge;
import gwtEntity.client.widgets.bridges.BuildListResultListBridge;
import gwtEntity.client.widgets.bridges.BuildListParamBuildListBridge;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import gwtEntity.client.BuildDto;
import gwtEntity.client.CategorizationDto;
import gwtEntity.client.CategoryDto;
import gwtEntity.client.JobDto;
import gwtEntity.client.LabelDto;
import gwtEntity.client.ParameterizedBuildDto;
import gwtEntity.client.PossibleResultDto;
import gwtEntity.client.ResultDto;
import java.util.List;
import java.util.logging.Logger;

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
    public void setBuildAndDisplayResults(BuildDto build) {
        resultList.showBuildResults(build);
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
    public void setTestAndDisplayHistory(ResultDto result, BuildDto build, List<PossibleResultDto> possibleResults) {
        testDetail.showTestHistory(result, build, possibleResults);
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

    private static final Logger LOGGER = Logger.getLogger("gwtEntity");

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

    public MainPanel() {
        initWidget(uiBinder.createAndBindUi(this));
        setBridges();
        removeUnnecessaryTabs();
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

    @UiHandler("tabPanel")
    void onTabSelection(SelectionEvent<Integer> event) {
        int selectedTab = event.getSelectedItem();

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
    }

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

}
