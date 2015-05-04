package gwtEntity.client.widgets;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.ProvidesKey;
import gwtEntity.client.CategoryDto;
import gwtEntity.client.CategoryService;
import gwtEntity.client.CategoryServiceAsync;
import gwtEntity.client.JobDto;
import gwtEntity.client.JobService;
import gwtEntity.client.JobServiceAsync;
import gwtEntity.client.LabelDto;
import gwtEntity.client.LabelService;
import gwtEntity.client.LabelServiceAsync;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jtymel
 */
public class JobCategories extends Composite {

    private static JobCategories.JobCategoriesUiBinder uiBinder = GWT.create(JobCategories.JobCategoriesUiBinder.class);

    private final CategoryServiceAsync categoryService = GWT.create(CategoryService.class);
    private final JobServiceAsync jobService = GWT.create(JobService.class);

    private JobDetailCategoriesBridge jobDetailCategoriesBridge;

    interface JobCategoriesUiBinder extends UiBinder<Widget, JobCategories> {
    }

    @UiField(provided = true)
    DataGrid<CategoryDto> dataGrid;

    @UiField(provided = true)
    SimplePager pager;

    @UiField
    Button addButton;

    @UiField
    Button cancelButton;

    private JobDto job;

    private MultiSelectionModel<CategoryDto> selectionModel;
    private ListDataProvider<CategoryDto> dataProvider;

    public JobCategories() {
        dataGrid = new DataGrid<CategoryDto>(20);
        initDatagrid();
        initPager();
        initWidget(uiBinder.createAndBindUi(this));
    }

//    public void setLabelListDetailBridge(LabelListDetailBridge bridge) {       
//        categorizationListDetailBridge = bridge;
//    }
    @UiHandler("addButton")
    void onAddButtonClick(ClickEvent event) {
        final List<CategoryDto> categories = getSelectedCategories();

        jobService.addCategoriesToLabel(job, categories, new AsyncCallback<Void>() {

            @Override
            public void onFailure(Throwable caught) {
                Window.alert("Categories were not added. See system log for more details");
                jobDetailCategoriesBridge.cancelJobCategoriesAndDisplayJobDetail();
            }

            @Override
            public void onSuccess(Void result) {
                jobDetailCategoriesBridge.cancelJobCategoriesAndDisplayJobDetail();
            }
        });

    }

    @UiHandler("cancelButton")
    void onCancelButtonClick(ClickEvent event) {
        jobDetailCategoriesBridge.cancelJobCategoriesAndDisplayJobDetail();
    }

    public void onTabShow() {
        updateDataGrid();
    }

    private void initDatagrid() {

        Column<CategoryDto, Boolean> checkColumn = new Column<CategoryDto, Boolean>(new CheckboxCell(true, false)) {
            @Override
            public Boolean getValue(CategoryDto object) {
                // Get the value from the selection model.
                return selectionModel.isSelected(object);
            }
        };

        TextColumn<CategoryDto> categoryName = new TextColumn<CategoryDto>() {
            @Override
            public String getValue(CategoryDto object) {
                return object.getName();
            }
        };

        TextColumn<CategoryDto> categorizationName = new TextColumn<CategoryDto>() {
            @Override
            public String getValue(CategoryDto object) {
                return object.getCategorization();
            }
        };

        dataGrid.setColumnWidth(checkColumn, 5, Style.Unit.PX);
        dataGrid.addColumn(checkColumn, "");

        dataGrid.setColumnWidth(categorizationName, 40, Style.Unit.PX);
        dataGrid.addColumn(categorizationName, "Categorization");

        dataGrid.setColumnWidth(categoryName, 40, Style.Unit.PX);
        dataGrid.addColumn(categoryName, "Category");

        selectionModel = new MultiSelectionModel<CategoryDto>(keyProvider);

        dataGrid.setSelectionModel(selectionModel, DefaultSelectionEventManager.<CategoryDto>createCheckboxManager());
        updateDataGrid();
    }

    private void initPager() {
        SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
        pager = new SimplePager(SimplePager.TextLocation.CENTER, pagerResources, false, 0, true);
        pager.setDisplay(dataGrid);
    }

    public void updateDataGrid() {
        categoryService.getCategories(new AsyncCallback<List<CategoryDto>>() {

            @Override
            public void onFailure(Throwable caught) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void onSuccess(List<CategoryDto> result) {
                dataProvider = new ListDataProvider<CategoryDto>();
                dataProvider.setList(result);
                dataProvider.addDataDisplay(dataGrid);
                dataGrid.setRowCount(result.size());
            }
        });

    }

    ProvidesKey<CategoryDto> keyProvider = new ProvidesKey<CategoryDto>() {
        @Override
        public Object getKey(CategoryDto category) {
            return (category == null) ? null : category.getId();
        }
    };

    private List<CategoryDto> getSelectedCategories() {
        List<CategoryDto> categories = (List<CategoryDto>) dataProvider.getList();
        List<CategoryDto> selectedCategories = new ArrayList<CategoryDto>();

        Long i = 0L;

        for (CategoryDto categoryDto : categories) {
            if (selectionModel.isSelected(categoryDto)) {
                selectedCategories.add(categoryDto);
            }
            i++;
        }

        return selectedCategories;
    }

    public void setJobDetailCategoriesBridge(JobDetailCategoriesBridge bridge) {
        jobDetailCategoriesBridge = bridge;
    }

    public void setJob(JobDto jobDto) {
        job = jobDto;
    }
}
