package gwtEntity.client.widgets;

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
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author jtymel
 */
public class MainPanel extends Composite implements JobListDetailBridge, CategorizationListDetailBridge, CategoryListDetailBridge, JobDetailLabelDetailBridge, JobDetailCategoriesBridge, JobListBuildListBridge, BuildListParamBuildListBridge, ParamBuildResultListBridge {

    private static MainPanelUiBinder uiBinder = GWT.create(MainPanelUiBinder.class);

    interface MainPanelUiBinder extends UiBinder<Widget, MainPanel> {
    }

    @Override
    public void setJobAndDisplayDetail(JobDto jobDTO) {
        jobDetail.setJob(jobDTO);
        tabPanel.selectTab(jobDetail);
    }

    @Override
    public void displayList() {
        jobList.updateDataGrid();
        tabPanel.selectTab(jobList);
    }

    @Override
    public void setCategorizationAndDisplayDetail(CategorizationDto categorizationDto) {
        categorizationDetail.setCategorization(categorizationDto);
        tabPanel.selectTab(categorizationDetail);
    }

    @Override
    public void displayCategorizationList() {
        categorizationList.updateDataGrid();
        tabPanel.selectTab(categorizationList);
    }

    @Override
    public void setCategoryAndDisplayDetail(CategoryDto categoryDto) {
        categoryDetail.setCategory(categoryDto);
        tabPanel.selectTab(categoryDetail);
    }

    @Override
    public void displayCategoryList() {
        categoryList.updateDataGrid();
        tabPanel.selectTab(categoryList);
    }

    @Override
    public void setLabelAndDisplayDetail(LabelDto label, JobDto job) {
        labelDetail.setLabel(label, job);
        tabPanel.selectTab(labelDetail);
    }

    @Override
    public void displayLabelList() {
        tabPanel.selectTab(jobDetail);
    }

    @Override
    public void setJobAndDisplayCategories(JobDto job) {
        jobCategories.setJob(job);
        tabPanel.selectTab(jobCategories);
    }

    @Override
    public void displayJobDetail() {
        tabPanel.selectTab(jobDetail);
    }

    @Override
    public void setJobAndDisplayBuilds(JobDto job) {
        buildList.setJob(job);
        tabPanel.selectTab(buildList);
    }

    @Override
    public void setBuildAndDisplayParamBuilds(BuildDto build) {
        paramBuildList.setBuild(build);
        tabPanel.selectTab(paramBuildList);
    }

    @Override
    public void setParamBuildAndDisplayResults(ParameterizedBuildDto paramBuild) {
        resultList.setParamBuild(paramBuild);
        tabPanel.selectTab(resultList);
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

    public MainPanel() {
        initWidget(uiBinder.createAndBindUi(this));
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
    }

    public List<JobDto> getSelectedJobs() {
        return jobList.getSelectedJobs();
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

        if (tabPanel.getWidget(selectedTab).equals(resultList)) {
            resultList.onTabShow();
        }
        
        if (tabPanel.getWidget(selectedTab).equals(jobDetail)) {
            jobDetail.onTabShow();
        }
    }

}
