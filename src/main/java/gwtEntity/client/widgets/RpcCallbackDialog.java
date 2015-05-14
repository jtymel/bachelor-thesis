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
