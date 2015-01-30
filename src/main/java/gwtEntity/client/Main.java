package gwtEntity.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import gwtEntity.client.widgets.DesktopWidget;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Main implements EntryPoint {
  

  /**
   * This is the entry point method.
   */
  @Override
  public void onModuleLoad() {
      RootLayoutPanel.get().add(new DesktopWidget());

  }
}
          

