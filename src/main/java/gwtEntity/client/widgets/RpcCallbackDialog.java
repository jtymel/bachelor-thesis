/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gwtEntity.client.widgets;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 *
 * @author Hynek Mlnarik <hmlnarik@redhat.com>
 */
public class RpcCallbackDialog extends DialogBox {

    private final Label textToServerLabel = new Label();
    private final HTML serverResponseLabel = new HTML();
    private final Button closeButton = new Button("Close");

    public RpcCallbackDialog() {
        initWidgets();
    }

    private void initWidgets() {
        setText("Remote Procedure Call");
        setAnimationEnabled(true);
        // We can set the id of a widget by accessing its Element
        closeButton.getElement().setId("closeButton");
        VerticalPanel dialogVPanel = new VerticalPanel();
        dialogVPanel.addStyleName("dialogVPanel");
        dialogVPanel.add(new HTML("<b>Sending name to the server:</b>"));
        dialogVPanel.add(textToServerLabel);
        dialogVPanel.add(new HTML("<br><b>Server replies:</b>"));
        dialogVPanel.add(serverResponseLabel);
        dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
        dialogVPanel.add(closeButton);

        setWidget(dialogVPanel);

        // Add a handler to close the DialogBox
        closeButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                hide();
            }
        });
    }

    public void setTextToServer(String string) {
        textToServerLabel.setText(string);
    }

    public void setServerResponseLabel(String string) {
        serverResponseLabel.setText(string);
    }

    public Button getCloseButton() {
        return closeButton;
    }

    public void setErrorStyle(boolean isError) {
        if (isError) {
            serverResponseLabel.addStyleName("serverResponseLabelError");
        } else {
            serverResponseLabel.removeStyleName("serverResponseLabelError");
        }
    }

}
