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
import gwtEntity.client.CategorizationDto;
import gwtEntity.client.CategoryDto;
import gwtEntity.client.JobDto;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author jtymel
 */
public class MainPanel extends Composite implements JobListDetailBridge, CategorizationListDetailBridge, CategoryListDetailBridge {

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
    TabLayoutPanel tabPanel;
       
    public MainPanel() {     
        initWidget(uiBinder.createAndBindUi(this));
        jobList.setJobListDetailBridge(MainPanel.this);
        jobDetail.setJobListDetailBridge(MainPanel.this);
        categorizationList.setCategorizationListDetailBridge(MainPanel.this);
        categorizationDetail.setCategorizationListDetailBridge(MainPanel.this);
        categoryList.setCategoryListDetailBridge(MainPanel.this);
        categoryDetail.setCategoryListDetailBridge(MainPanel.this);
    }
    
    public List<JobDto> getSelectedJobs() {
        return jobList.getSelectedJobs();    
    }

    @UiHandler("tabPanel")
    void onTabSelection(SelectionEvent<Integer> event) {
        int selectedTab = event.getSelectedItem();

        if(tabPanel.getWidget(selectedTab).equals(labelDetail)) {
            labelDetail.updateDataGrid();
        }
        
        if(tabPanel.getWidget(selectedTab).equals(categoryDetail)) {
            categoryDetail.getCategorizations();            
        }
    }

}
