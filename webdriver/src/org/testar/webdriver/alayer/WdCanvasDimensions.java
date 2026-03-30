/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2018-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.webdriver.alayer;

import org.testar.core.util.Util;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testar.webdriver.state.WdDriver;

import java.util.List;

/*
 * Periodically poll position of the browser window (viewport)
 * Browsers don't have event listeners for window movement
 */
public class WdCanvasDimensions extends Thread {

  private static int canvasX = 0;
  private static int canvasY = 0;
  private static int canvasWidth = 0;
  private static int canvasHeight = 0;
  private static int innerWidth = 0;
  private static int innerHeight = 0;
  private static int scrollY = 0;
  private static int scrollX = 0;

  private static boolean running = false;

  private WdCanvasDimensions() {
  }

  public static void startThread() {
    if (running) {
      return;
    }

    running = true;
    new Thread(new WdCanvasDimensions()).start();
  }

  public static void stopThread() {
    running = false;
  }

  public static int getCanvasX() {
    return canvasX;
  }

  public static int getCanvasY() {
    return canvasY;
  }

  public static int getCanvasWidth() {
    return canvasWidth;
  }

  public static int getCanvasHeight() {
    return canvasHeight;
  }

  public static int getInnerWidth() {
    return innerWidth;
  }

  public static int getInnerHeight() {
    return innerHeight;
  }

  public static int getScrollY() {
    return scrollY;
  }

  public static int getScrollX() {
    return scrollX;
  }

  @Override
  public void run() {
    while (running) {
      updateDimensions();
      Util.pause(0.5);
    }
  }

  @SuppressWarnings("unchecked")
  private void updateDimensions() {
    // This assumes no status bars on the left or on the bottom
    try {
      RemoteWebDriver remoteWebDriver = WdDriver.getRemoteWebDriver();

      if(remoteWebDriver == null) return;

      List<Long> screen = (List<Long>)remoteWebDriver.executeScript("return canvasDimensionsTestar()");

      if (screen == null) return;

      canvasX = Math.toIntExact(screen.get(0));
      canvasY = Math.toIntExact(screen.get(1));
      canvasWidth = Math.toIntExact(screen.get(2));
      canvasHeight = Math.toIntExact(screen.get(3));
      innerWidth = Math.toIntExact(screen.get(4));
      innerHeight = Math.toIntExact(screen.get(5));
      scrollY = Math.toIntExact(screen.get(6));
      scrollX = Math.toIntExact(screen.get(7));
    }
    catch (WebDriverException ignored) {

    }
  }
}
