package org.fruit.alayer.actions;

import org.fruit.alayer.*;
import org.fruit.alayer.exceptions.ActionFailedException;
import org.fruit.alayer.webdriver.WdDriver;

public class WdHistoryBackAction extends TaggableBase implements Action {
  public WdHistoryBackAction() {

  }

  @Override
  public void run(SUT system, State state, double duration)
      throws ActionFailedException {
    WdDriver.executeScript("window.history.back();");
  }

  @Override
  public String toShortString() {
    return "History back";
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
