package org.fruit.alayer.actions;

import org.fruit.alayer.*;
import org.fruit.alayer.exceptions.ActionFailedException;
import org.fruit.alayer.webdriver.WdDriver;

public class WdCloseTabAction extends TaggableBase implements Action {
  public WdCloseTabAction() {

  }

  @Override
  public void run(SUT system, State state, double duration)
      throws ActionFailedException {
    WdDriver.executeScript("window.close();");
  }

  @Override
  public String toShortString() {
    return "Close tab";
  }

  @Override
  public String toParametersString() {
    return toShortString();
  }

  @Override
  public String toString(Role... discardParameters) {
    return toShortString();
  }
}
