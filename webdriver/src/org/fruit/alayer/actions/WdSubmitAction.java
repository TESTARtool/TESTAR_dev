package org.fruit.alayer.actions;

import org.fruit.alayer.*;
import org.fruit.alayer.exceptions.ActionFailedException;
import org.fruit.alayer.webdriver.WdDriver;

public class WdSubmitAction extends TaggableBase implements Action {
  private String formId;

  public WdSubmitAction(String formId) {
    this.formId = formId;
  }

  @Override
  public void run(SUT system, State state, double duration)
      throws ActionFailedException {
    String form = String.format("document.getElementById('%s')", formId);
    try {
      WdDriver.executeScript(String.format("%s.submit();", form));
    }
    catch (Exception wde) {
      // The form can not be found by id, let's try by name
      if (wde.getMessage().contains("Cannot read property 'submit' of null")) {
        form = String.format("document.getElementsByName('%s')[0]", formId);
        WdDriver.executeScript(String.format("%s.submit();", form));
      }
      // Let's try by clicking on the submit button
      else if (wde.getMessage().contains("submit is not a function")) {
        WdDriver.executeScript(String.format(
            "%s.querySelector('input[type=\"submit\"]').click();", form));
      }
    }
  }

  @Override
  public String toShortString() {
    return "Submit form : '" + formId + "'";
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
