package org.fruit.alayer.webdriver;

import es.upv.staq.testar.ProtocolUtil;
import es.upv.staq.testar.serialisation.ScreenshotSerialiser;
import org.fruit.alayer.*;
import org.fruit.alayer.Shape;
import org.fruit.alayer.webdriver.enums.WdTags;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.awt.*;
import java.util.List;

import static org.fruit.alayer.webdriver.Constants.scrollThick;


public class WdProtocolUtil extends ProtocolUtil {
  private RemoteWebDriver webDriver;

  public WdProtocolUtil(SUT sut) {
    webDriver = ((WdDriver) sut).getRemoteWebDriver();
  }

  @Override
  public String getStateshot(State state) {
    double width = CanvasDimensions.getCanvasWidth() + (
        state.get(WdTags.WebVerticallyScrollable) ? scrollThick : 0);
    double height = CanvasDimensions.getCanvasHeight() + (
        state.get(WdTags.WebHorizontallyScrollable) ? scrollThick : 0);
    Rect rect = Rect.from(0, 0, width, height);
    AWTCanvas screenshot = WdScreenshot.fromScreenshot(webDriver, rect);
    return ScreenshotSerialiser.saveStateshot(state.get(Tags.ConcreteID), screenshot);
  }

  @Override
  public String getActionshot(State state, Action action) {
    List<Finder> targets = action.get(Tags.Targets, null);
    if (targets == null) {
      return null;
    }

    Rectangle actionArea = new Rectangle(
        Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
    for (Finder f : targets) {
      Widget widget = f.apply(state);
      Shape shape = widget.get(Tags.Shape);
      Rectangle r = new Rectangle((int) shape.x(), (int) shape.y(), (int) shape.width(), (int) shape.height());
      actionArea = actionArea.union(r);
    }
    if (actionArea.isEmpty()) {
      return null;
    }

    // Actionarea is outside viewport
    if (actionArea.x < 0 || actionArea.y < 0 ||
        actionArea.x + actionArea.width > CanvasDimensions.getCanvasWidth() ||
        actionArea.y + actionArea.height > CanvasDimensions.getCanvasHeight()) {
      return null;
    }

    Rect rect = Rect.from(
        actionArea.x, actionArea.y, actionArea.width + 1, actionArea.height + 1);
    AWTCanvas scrshot = WdScreenshot.fromScreenshot(webDriver, rect);
    return ScreenshotSerialiser.saveActionshot(state.get(Tags.ConcreteID), action.get(Tags.ConcreteID), scrshot);
  }
}