/**
 * Copyright (c) 2018 - 2025 Open Universiteit - www.ou.nl
 * Copyright (c) 2019 - 2025 Universitat Politecnica de Valencia - www.upv.es
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

package org.testar.monkey.alayer.webdriver;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testar.monkey.alayer.devices.AWTKeyboard;
import org.testar.monkey.alayer.devices.Keyboard;
import org.testar.monkey.alayer.devices.Mouse;
import org.testar.monkey.alayer.exceptions.SystemStartException;
import org.testar.monkey.alayer.exceptions.SystemStopException;
import org.testar.webdriver.manager.WdChromeManager;
import org.testar.webdriver.manager.WdEdgeManager;
import org.testar.webdriver.manager.WdFirefoxManager;
import org.testar.webdriver.SutConnectorParser;
import org.testar.webdriver.manager.WdBrowserManager;

import org.openqa.selenium.*;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testar.monkey.Util;
import org.testar.monkey.alayer.*;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.*;

public class WdDriver extends SUTBase {
  protected static final Logger logger = LogManager.getLogger();

  private static WdDriver wdDriver = null;
  private static RemoteWebDriver remoteWebDriver = null;
  private static CopyOnWriteArrayList<String> windowHandles = new CopyOnWriteArrayList<>();
  public static boolean followLinks = true;
  public static boolean fullScreen = false;
  public static boolean forceActivateTab = true;
  public static boolean disableSecurity = false;
  public static boolean remoteDebugging = false;
  public static boolean disableGPU = true;

  private final Keyboard kbd = AWTKeyboard.build();
  private final Mouse mouse = WdMouse.build();

  public WdDriver(String sutConnector) {
	  SutConnectorParser parser = new SutConnectorParser(sutConnector);

	  String url = parser.getUrl();
	  if (url.isEmpty()) {
		  throw new IllegalArgumentException("No valid URL provided in sutConnector.");
	  }

	  Dimension screenDimensions = !fullScreen ? parser.getScreenDimensions() : null;
	  Point screenPosition = !fullScreen ? parser.getScreenPosition() : null;

	  String browserPath = parser.getBrowserPath();
	  String path = System.getProperty("user.dir");
	  String extensionPath = path.substring(0, path.length() - 3) + "web-extension";

	  WdBrowserManager wdManager;
	  if (browserPath.toLowerCase().contains("firefox")) {
		  wdManager = new WdFirefoxManager();
	  } else if (browserPath.toLowerCase().contains("msedge")) {
		  wdManager = new WdEdgeManager();
	  } else {
		  wdManager = new WdChromeManager();
	  }

	  String browserBinary = wdManager.resolveBinary(browserPath);
	  remoteWebDriver = wdManager.createWebDriver(browserBinary, extensionPath);

	  if (screenDimensions != null) {
		  remoteWebDriver.manage().window().setSize(screenDimensions);
	  }
	  if (screenPosition != null) {
		  remoteWebDriver.manage().window().setPosition(screenPosition);
	  }

	  loadUrlWithRetry(url);

	  CanvasDimensions.startThread();

	  wdDriver = this;
  }

  private void loadUrlWithRetry(String url) {
	  try {
		  remoteWebDriver.get(url);
	  } catch (WebDriverException wex) {
		  if (wex instanceof TimeoutException) {
			  System.err.println("WebDriver TimeoutException for URL: " + url);
		  } else {
			  System.err.println("WebDriverException on first attempt for URL: " + url);
		  }
		  wex.printStackTrace();

		  try {
			  System.out.println("Waiting and retrying URL load...");
			  Util.pause(10);
			  remoteWebDriver.get(url);
		  } catch (WebDriverException retryWex) {
			  if (retryWex instanceof TimeoutException) {
				  System.err.println("Retry WebDriver TimeoutException for URL: " + url);
			  } else {
				  System.err.println("Retry WebDriverException for URL: " + url);
			  }
			  retryWex.printStackTrace();
		  } catch (Exception retryOther) {
			  System.err.println("Unexpected exception during retry for URL: " + url);
			  retryOther.printStackTrace();
		  }
	  } catch (Exception e) {
		  System.err.println("Unexpected exception for URL: " + url);
		  e.printStackTrace();
	  }
  }

  public static LogEntries getBrowserLogs() {
	  try {
		  return remoteWebDriver.manage().logs().get(LogType.BROWSER);
	  } catch (Exception e) {
		  // Firefox does not support retrieving browser logs
		  return new LogEntries(new ArrayList<>());
	  }
  }

  @Override
  public void stop() throws SystemStopException {
    if (remoteWebDriver != null) {
    	remoteWebDriver.quit();
    	remoteWebDriver = null;
    }

    CanvasDimensions.stopThread();
  }

  @Override
  public boolean isRunning() {
    try {
    	remoteWebDriver.getCurrentUrl();
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
    if (remoteWebDriver != null) {
    	remoteWebDriver.quit();
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
    return remoteWebDriver;
  }

  /*
   * Update the list of handles with added handles (new tabs)
   * Remove handles from closed tabs
   */
  private static void updateHandlesList() {
    Set<String> currentHandles = remoteWebDriver.getWindowHandles();

    // Remove handles not present anymore (closed tabs)
    for (String handle : new ArrayList<>(windowHandles)) {
      if (!currentHandles.contains(handle)) {
        windowHandles.remove(handle);
      }
    }
    // Add new handles (new tabs), shouldn't be more than one
    for (String handle : currentHandles) {
      if (!windowHandles.contains(handle)) {
        windowHandles.add(handle);
      }
    }
  }

  /*
   * Make sure the last tab has focus
   */
  public static void activate() {
    updateHandlesList();

    // Nothing to activate, or user doesn't want to use this activate feature
    if (windowHandles.size() < 1 || !forceActivateTab) {
      return;
    }

    String handle = windowHandles.get(followLinks ? windowHandles.size() - 1 : 0);
    try {
    	remoteWebDriver.switchTo().window(handle);
    }
    catch (NullPointerException | WebDriverException ignored) {
    	remoteWebDriver = null;
    }
  }

  public static Set<String> getWindowHandles() {
    try {
      return remoteWebDriver.getWindowHandles();
    }
    catch (NullPointerException | WebDriverException ignored) {
      return new HashSet<>();
    }
  }

  public static String getCurrentUrl() {
    try {
      return remoteWebDriver.getCurrentUrl();
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

      return remoteWebDriver.executeScript(script, args);
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
    WebDriverWait wait = new WebDriverWait((WebDriver)remoteWebDriver, Duration.ofSeconds(60));
    ExpectedCondition<Boolean> documentReady = (WebDriver driver) -> {
      Object result = remoteWebDriver.executeScript("return document.readyState");
      return result != null && result.equals("complete");
    };
    wait.until(documentReady);
  }

  public static void executeCanvasScript(String script, Object... args) {
    try {
      // Choose first or last tab, depending on user prefs
      activate();

      // Add the canvas if the page doesn't have one
      remoteWebDriver.executeScript("addCanvasTestar()");

      remoteWebDriver.executeScript(script, args);
    }
    catch (NullPointerException | WebDriverException ignored) {

    }
  }
}
