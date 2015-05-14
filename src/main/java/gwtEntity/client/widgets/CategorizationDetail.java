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
package gwtEntity.client.widgets;

import gwtEntity.client.widgets.bridges.CategorizationListDetailBridge;
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
import gwtEntity.common.objects.CategorizationDto;
import gwtEntity.common.services.CategorizationService;
import gwtEntity.common.services.CategorizationServiceAsync;

/**
 *
 * @author jtymel
 */
public class CategorizationDetail extends Composite {

    private static CategorizationDetailUiBinder uiBinder = GWT.create(CategorizationDetailUiBinder.class);

    private final CategorizationServiceAsync categorizationService = GWT.create(CategorizationService.class);

    private CategorizationListDetailBridge categorizationListDetailBridge;

    void setCategorization(CategorizationDto categorizationDto) {
        if (categorizationDto != null) {
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

    @UiField
    Button cancelButton;

    CategorizationDto editedCategorization = null;

    @UiHandler("saveButton")
    void onSaveClick(ClickEvent event) {
        addCategorization();
    }

    @UiHandler("cancelButton")
    void onCancelClick(ClickEvent event) {
        categorizationListDetailBridge.cancelCategorizationDetailAndDisplayCategorizationList();
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

        if (editedCategorization == null) {
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
                categorizationListDetailBridge.cancelCategorizationDetailAndDisplayCategorizationList();
            }
        });

    }

}
