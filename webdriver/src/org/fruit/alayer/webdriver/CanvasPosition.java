package org.fruit.alayer.webdriver;

import org.fruit.Util;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.util.List;

/*
 * Periodically poll position of the browser window (viewport ackchyually)
 * Browsers don't have event listeners for window movement
 */
public class CanvasPosition extends Thread {
  private static RemoteWebDriver webDriver;

  private static int canvasX = 0;
  private static int canvasY = 0;
  private static int canvasWidth = 0;
  private static int canvasHeight = 0;

  private static boolean running = false;

  private CanvasPosition() {
  }

  public static void startThread(RemoteWebDriver driver) {
    if (running || driver == null) {
      return;
    }

    running = true;
    webDriver = driver;
    new Thread(new CanvasPosition()).start();
  }

  public static void stopThread() {
    running = false;
  }

  @Override
  public void run() {
    while (running) {
      updateDimensions();
      Util.pause(1);
    }
  }

  @SuppressWarnings("unchecked")
  private void updateDimensions () {
    // This assumes no status bars on the left or on the bottom
    if (webDriver != null) {
      List<Long> screen = (List<Long>)
          webDriver.executeScript("return canvasDimensionsTestar()");
      canvasX = (int) (long) screen.get(0);
      canvasY = (int) (long) screen.get(1);
      canvasWidth = (int) (long) screen.get(2);
      canvasHeight = (int) (long) screen.get(3);
    }
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
}
