/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2018-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.webdriver.alayer;

import org.testar.core.devices.Mouse;
import org.testar.core.devices.MouseButtons;
import org.testar.core.exceptions.FruitException;

import java.awt.*;

/*
 * This is a clone of the AWTMouse class for the WebDriver layer.
 * AWTMouse is final, so it's not possible to extend.
 *
 * AbstractProtocol creates its own Mouse object, separate from the native layer.
 * The end Protocol should replace this with the static WdMouse from WdDriver.
 *
 * Positions are relative to the viewport.
 */
public class WdMouse implements Mouse {
  private static String INFO_MESSAGE =
      "MouseInfo.getPointerInfo() returned null! " + System.lineSeparator() +
      "This seeems to be undocumented Java library behavior... " + System.lineSeparator() +
      "Consider using a platform specific Mouse Implementation instead of WdMouse!";

  private final Robot robot;
  
  private double displayScale;

  public static WdMouse build() throws FruitException {
    return new WdMouse();
  }

  private WdMouse() throws FruitException {
    try {
      robot = new Robot();
      this.displayScale = 1.0;
    }
    catch (AWTException awte) {
      throw new FruitException(awte);
    }
  }
  
  public void setCursorDisplayScale(double displayScale) {
	  this.displayScale = displayScale;
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

  public void setCursor(double x, double y) {
    double canvasX = Math.min(Math.max(0, x), WdCanvasDimensions.getInnerWidth());
    double canvasY = Math.min(Math.max(0, y), WdCanvasDimensions.getInnerHeight());
    
    canvasX += WdCanvasDimensions.getCanvasX();
    canvasY += WdCanvasDimensions.getCanvasY();
    
    canvasX = canvasX * displayScale;
    canvasY = canvasY * displayScale;

    robot.mouseMove((int)canvasX, (int)canvasY);
  }

  public org.testar.core.alayer.Point cursor() {
    PointerInfo info = MouseInfo.getPointerInfo();
    if (info == null) {
      throw new RuntimeException(INFO_MESSAGE);
    }
    java.awt.Point p = info.getLocation();

    int viewportX = (int) ((p.x/ displayScale) - WdCanvasDimensions.getCanvasX());
    int viewportY = (int) ((p.y/ displayScale) - WdCanvasDimensions.getCanvasY());
    return org.testar.core.alayer.Point.from(viewportX, viewportY);
  }
}
