package gwtEntity.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import gwtEntity.client.CategorizationDto;
import gwtEntity.client.CategorizationService;
import gwtEntity.client.CategorizationServiceAsync;

/**
 *
 * @author jtymel
 */
public class CategorizationDetail extends Composite {
    
    private static CategorizationDetailUiBinder uiBinder = GWT.create(CategorizationDetailUiBinder.class);

    private final CategorizationServiceAsync categorizationService = GWT.create(CategorizationService.class);      
    
    private CategorizationListDetailBridge categorizationListDetailBridge;

    void setCategorization(CategorizationDto categorizationDto) {
        if(categorizationDto != null) {
            categorizationNameField.setText(categorizationDto.getName());
        } else {
            categorizationNameField.setText("");
        }

        editedCategorization = categorizationDto;
    }

    interface CategorizationDetailUiBinder extends UiBinder<Widget, CategorizationDetail> {
    }

    public CategorizationDetail() {
        initWidget(uiBinder.createAndBindUi(this));
    }
    
    @UiField
    TextBox categorizationNameField;
    
    @UiField
    Button saveButton;
    
    CategorizationDto editedCategorization = null;
    
    @UiHandler("saveButton")
    void onSaveClick(ClickEvent event) {
        addCategorization();        
    }

    @UiHandler("categorizationNameField")
    void onKeyUp(KeyUpEvent event) {
        if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
            addCategorization();
        }
    }

    public void setCategorizationListDetailBridge(CategorizationListDetailBridge bridge) {       
        categorizationListDetailBridge = bridge;
    }

    private void addCategorization() {
        CategorizationDto categorizationDto;

        if(editedCategorization == null) {
            categorizationDto = new CategorizationDto();
            categorizationDto.setName(categorizationNameField.getText());
        } else {
            categorizationDto = editedCategorization;
            categorizationDto.setName(categorizationNameField.getText());

            editedCategorization = null;
        }

        categorizationService.saveCategorization(categorizationDto, new AsyncCallback<Long>() {

            @Override
            public void onFailure(Throwable caught) {
            }

            @Override
            public void onSuccess(Long result) {
                categorizationListDetailBridge.displayCategorizationList();
            }
        });

    }
    
    
}
