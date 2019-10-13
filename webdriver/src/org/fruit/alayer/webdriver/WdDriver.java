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

import org.fruit.alayer.*;
import org.fruit.alayer.devices.AWTKeyboard;
import org.fruit.alayer.devices.Keyboard;
import org.fruit.alayer.devices.Mouse;
import org.fruit.alayer.exceptions.SystemStartException;
import org.fruit.alayer.exceptions.SystemStopException;
import org.fruit.alayer.windows.WinProcess;
import org.fruit.alayer.windows.Windows;
import org.openqa.selenium.Point;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.*;


public class WdDriver extends SUTBase {
  private static WdDriver wdDriver = null;
  private static RemoteWebDriver webDriver = null;
  private static List<String> windowHandles = new ArrayList<>();
  public static boolean followLinks = true;

  private final Keyboard kbd = AWTKeyboard.build();
  private final Mouse mouse = WdMouse.build();

  private WdDriver(String sutConnector) {
    String[] parts = sutConnector.split(" ");

    String driverPath = parts[0].replace("\"", "");

    String osName = System.getProperty("os.name");
    if (!driverPath.contains(".exe") && osName.contains("Windows")) {
      driverPath = sutConnector.substring(0, sutConnector.indexOf(".exe") + 4);
      driverPath = driverPath.replace("\"", "");
      parts = sutConnector.substring(sutConnector.indexOf(".exe")).split(" ");
      parts[0] = driverPath;
    }

    String url = parts[parts.length - 1].replace("\"", "");
    Dimension screenDimensions = null;
    Point screenPosition = null;
    if (parts.length == 3) {
      String tmp = parts[1].replace("\"", "").toLowerCase();
      String[] dims = tmp.split("\\+")[0].split("x");
      screenDimensions =
          new Dimension(Integer.parseInt(dims[0]), Integer.parseInt(dims[1]));
      try {
        screenPosition = new Point(Integer.parseInt(tmp.split("\\+")[1]),
            Integer.parseInt(tmp.split("\\+")[2]));
      }
      catch (ArrayIndexOutOfBoundsException aioobe) {

      }
    }

    if (driverPath.toLowerCase().contains("chrome")) {
      webDriver = WdRemoteDriver.startChromeDriver(driverPath);
    }
    else if (driverPath.toLowerCase().contains("gecko")) {
      webDriver = WdRemoteDriver.startGeckoDriver(driverPath);
    }
    else if (driverPath.toLowerCase().contains("microsoft")) {
      webDriver = WdRemoteDriver.startEdgeDriver(driverPath);
    }
    else {
      String msg = " \n ******** Not a valid webdriver Exception ********"
                   + "\n Something looks wrong with the webdriver path: \n "
                   + sutConnector
                   + "\n Allowed webdrivers: chromedriver.exe"
                   + "\n Readed path: " + driverPath
                   + "\n Verify if it exists or if the path is a correct definition. "
                   + "\n Please follow this structure (spaces, quotes, characters...):"
                   + "\n \"C:\\Windows\\chromedriver.exe\" \"1920x900+0+0\" \"https://www.testar.org\" \n";
      System.out.println(msg);

      throw new SystemStartException("Not a valid webdriver");
    }

    if (screenDimensions != null) {
      webDriver.manage().window().setSize(screenDimensions);
    }
    if (screenPosition != null) {
      webDriver.manage().window().setPosition(screenPosition);
    }

    webDriver.get(url);

    if (System.getProperty("os.name").contains("Windows") &&
        this.get(Tags.HWND, (long) -1) == (long) -1) {
        this.set(Tags.HWND, Windows.GetForegroundWindow());
    }

    CanvasDimensions.startThread();

    wdDriver = this;
  }

  @Override
  public void stop() throws SystemStopException {
    if (webDriver != null) {
      webDriver.quit();
      webDriver = null;
    }

    CanvasDimensions.stopThread();
  }

  @Override
  public boolean isRunning() {
    try {
      webDriver.getCurrentUrl();
    }
    catch (NullPointerException | WebDriverException ignored) {
      return false;
    }

    return true;
  }

  @Override
  public String getStatus() {
    return "WebDriver : " + WdDriver.getCurrentUrl();
  }

  @Override
  public AutomationCache getNativeAutomationCache() {
    return null;
  }

  @Override
  public void setNativeAutomationCache() {
  }

  public static List<SUT> fromAll() {
    if (wdDriver == null) {
      return new ArrayList<>();
    }

    return Collections.singletonList(wdDriver);
  }

  public static WdDriver fromExecutable(String sutConnector)
      throws SystemStartException {
    if (webDriver != null) {
      webDriver.quit();
    }

    return new WdDriver(sutConnector);
  }

  @SuppressWarnings("unchecked")
  protected <T> T fetch(Tag<T> tag) {
    if (tag.equals(Tags.StandardKeyboard)) {
      return (T) kbd;
    }
    else if (tag.equals(Tags.StandardMouse)) {
      return (T) mouse;
    }
    else if (tag.equals(Tags.PID)) {
      long pid = -1;
      return (T) (Long) pid;
    }
    else if (tag.equals(Tags.HANDLE)) {
      return null;
    }
    else if (tag.equals(Tags.ProcessHandles)) {
      return null;
    }
    else if (tag.equals(Tags.SystemActivator)) {
      return (T) new WdProcessActivator();
    }
    return null;
  }

  public static RemoteWebDriver getRemoteWebDriver() {
    return webDriver;
  }

  /*
   * Update the list of handles with added handles (new tabs)
   * Remove handles from closed tabs
   */
  private static void updateHandlesList() {
    Set<String> currentHandles = webDriver.getWindowHandles();

    // Remove handles not present anymore (closed tabs)
    windowHandles.removeIf(handle -> !currentHandles.contains(handle));
    // Add new handles (new tabs)
    for (String handle : currentHandles) {
      if (!windowHandles.contains(handle)) {
        windowHandles.add(handle);
      }
    }
  }

  /*
   * Make sure the browser window and correct tab has focus
   */
  public static void activate() {
    updateHandlesList();

    // Nothing to activate
    if (windowHandles.size() < 1) {
      return;
    }

    try {
      if (System.getProperty("os.name").contains("Windows")) {
        // Detect if the browser has focus
        long hwnd = wdDriver.get(Tags.HWND, (long) -1);
        if (hwnd != -1 && hwnd != Windows.GetForegroundWindow()) {
          WinProcess.toForeground(Windows.GetWindowProcessId(hwnd));
        }
      }

      // Check if document not in foreground
      boolean hasFocus = (Boolean)
          webDriver.executeScript("return document.hasFocus();");
      if (!hasFocus) {
        // On OSX and Linux this also forces the browser to the foreground
        String handle = windowHandles.get(followLinks ? windowHandles.size() - 1 : 0);
        webDriver.switchTo().window(handle);
      }
    }
    catch (NullPointerException | WebDriverException ignored) {
      webDriver = null;
    }
  }

  public static Set<String> getWindowHandles() {
    try {
      return webDriver.getWindowHandles();
    }
    catch (NullPointerException | WebDriverException ignored) {
      return new HashSet<>();
    }
  }

  public static String getCurrentUrl() {
    try {
      return webDriver.getCurrentUrl();
    }
    catch (NullPointerException | WebDriverException ignored) {
      return "";
    }
  }

  public static Object executeScript(String script, Object... args) {
    try {
      // Choose first or last tab, depending on user prefs
      activate();

      // Wait until document is ready for script
      waitDocumentReady();

      return webDriver.executeScript(script, args);
    }
    catch (NullPointerException | WebDriverException ignored) {
      // We need this for WdSubmitAction
      if (ignored instanceof WebDriverException &&
          script.contains("getElementById")) {
        throw ignored;
      }
      return null;
    }
  }

  public static void waitDocumentReady() {
    WebDriverWait wait = new WebDriverWait(webDriver, 60);
    ExpectedCondition<Boolean> documentReady = (WebDriver driver) -> {
      Object result = webDriver.executeScript("return document.readyState");
      return result != null && result.equals("complete");
    };
    wait.until(documentReady);
  }

  public static void executeCanvasScript(String script, Object... args) {
    try {
      // Choose first or last tab, depending on user prefs
      activate();

      // Add the canvas if the page doesn't have one
      webDriver.executeScript("addCanvasTestar()");

      webDriver.executeScript(script, args);
    }
    catch (NullPointerException | WebDriverException ignored) {

    }
  }
}
