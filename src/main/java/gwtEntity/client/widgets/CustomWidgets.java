package gwtEntity.client.widgets;

import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 *
 * @author jtymel
 */
public class CustomWidgets {

    public static DialogBox alertWidget(final String header, final String content) {
        final DialogBox box = new DialogBox();
        final VerticalPanel panel = new VerticalPanel();
        box.setText(header);

        panel.add(new Label(content));

        final Label emptyLabel = new Label("");
        emptyLabel.setSize("auto", "80px");
        panel.add(emptyLabel);

        box.setAutoHideEnabled(true);
        box.add(panel);
        return box;
    }
}
