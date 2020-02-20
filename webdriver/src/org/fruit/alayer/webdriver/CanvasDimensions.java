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

import org.fruit.Util;
import org.openqa.selenium.WebDriverException;

import java.util.List;


/*
 * Periodically poll position of the browser window (viewport)
 * Browsers don't have event listeners for window movement
 */
public class CanvasDimensions extends Thread {

  private static int canvasX = 0;
  private static int canvasY = 0;
  private static int canvasWidth = 0;
  private static int canvasHeight = 0;
  private static int innerWidth = 0;
  private static int innerHeight = 0;

  private static boolean running = false;

  private CanvasDimensions() {
  }

  public static void startThread() {
    if (running) {
      return;
    }

    running = true;
    new Thread(new CanvasDimensions()).start();
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
      List<Long> screen = (List<Long>)
          WdDriver.executeScript("return canvasDimensionsTestar()");
      if (screen == null) {
        return;
      }
      canvasX = Math.toIntExact(screen.get(0));
      canvasY = Math.toIntExact(screen.get(1));
      canvasWidth = Math.toIntExact(screen.get(2));
      canvasHeight = Math.toIntExact(screen.get(3));
      innerWidth = Math.toIntExact(screen.get(4));
      innerHeight = Math.toIntExact(screen.get(5));
    }
    catch (WebDriverException ignored) {

    }
  }
}
