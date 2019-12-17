/**
 * Copyright (c) 2018, 2019 Open Universiteit - www.ou.nl
 * Copyright (c) 2019 Universitat Politecnica de Valencia - www.upv.es
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */

package org.fruit.alayer.webdriver;

import org.fruit.FruitException;
import org.fruit.alayer.devices.Mouse;
import org.fruit.alayer.devices.MouseButtons;

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
    int canvasX = (int) Math.min(Math.max(0, x), CanvasDimensions.getInnerWidth());
    int canvasY = (int) Math.min(Math.max(0, y), CanvasDimensions.getInnerHeight());

    canvasX += CanvasDimensions.getCanvasX();
    canvasY += CanvasDimensions.getCanvasY();

    robot.mouseMove(canvasX, canvasY);
  }

  public org.fruit.alayer.Point cursor() {
    PointerInfo info = MouseInfo.getPointerInfo();
    if (info == null) {
      throw new RuntimeException(INFO_MESSAGE);
    }
    java.awt.Point p = info.getLocation();

    int viewportX = p.x - CanvasDimensions.getCanvasX();
    int viewportY = p.y - CanvasDimensions.getCanvasY();
    return org.fruit.alayer.Point.from(viewportX, viewportY);
  }
}
