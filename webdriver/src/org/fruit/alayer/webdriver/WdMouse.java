package org.fruit.alayer.webdriver;

import org.fruit.FruitException;
import org.fruit.alayer.devices.Mouse;
import org.fruit.alayer.devices.MouseButtons;

import java.awt.*;

/*
 * This is a clone of the AWTMouse class for the WebDriver layer.
 * AWTMouse is final, so not possible to extend.
 *
 * AbstractProtocol creates its own Mouse object, separate from the native layer.
 * The end Protocol should replace this with the static WdMouse from WdDriver.
 *
 * Positions are relative to the viewport.
 */
public class WdMouse implements Mouse {
  private static String UNSUPPORTED_MESSAGE =
      "WD Mouse cannot poll the mouse's state!";
  private static String INFO_MESSAGE =
      "MouseInfo.getPointerInfo() returned null! " + System.lineSeparator() +
      "This seeems to be undocumented Java library behavior... " + System.lineSeparator() +
      "Consider using a platform specific Mouse Implementation instead of WdMouse!";

  private final Robot robot;

  public static WdMouse build() throws FruitException {
    return new WdMouse();
  }

  private WdMouse() throws FruitException {
    try {
      robot = new Robot();
    }
    catch (AWTException awte) {
      throw new FruitException(awte);
    }
  }

  public String toString() {
    return "WD Mouse";
  }

  public void press(MouseButtons k) {
    robot.mousePress(k.code());
  }

  public void release(MouseButtons k) {
    robot.mouseRelease(k.code());
  }

  public void isPressed(MouseButtons k) {
    throw new UnsupportedOperationException(UNSUPPORTED_MESSAGE);
  }

  public void setCursor(double x, double y) {
    robot.mouseMove((int) x, (int) y);
  }

  public org.fruit.alayer.Point cursor() {
    PointerInfo info = MouseInfo.getPointerInfo();
    if (info == null) {
      throw new RuntimeException(INFO_MESSAGE);
    }
    java.awt.Point p = info.getLocation();

    int viewportX = p.x - CanvasPosition.getCanvasX();
    int viewportY = p.y - CanvasPosition.getCanvasY();
    return org.fruit.alayer.Point.from(viewportX, viewportY);
  }
}
