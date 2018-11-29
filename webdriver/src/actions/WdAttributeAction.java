package actions;

import org.fruit.alayer.*;
import org.fruit.alayer.exceptions.ActionFailedException;
import org.fruit.alayer.webdriver.WdDriver;

public class WdAttributeAction extends TaggableBase implements Action {
  private String elementId;
  private String key;
  private String value;

  public WdAttributeAction (String elementId, String key, String value) {
    this.elementId = elementId;
    this.key = key;
    this.value = value;
  }

  @Override
  public void run(SUT system, State state, double duration)
      throws ActionFailedException {
    WdDriver.executeScript(String.format(
        "document.getElementById('%s').setAttribute('%s', '%s');",
        elementId, key, value));
  }

  @Override
  public String toShortString() {
    return "Set attribute on id '" + elementId +
           "' for '" + key +
           "' to '" + value + "'";
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
