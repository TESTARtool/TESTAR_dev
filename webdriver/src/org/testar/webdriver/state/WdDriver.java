/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2018-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.webdriver.state;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testar.core.devices.AWTKeyboard;
import org.testar.core.devices.Keyboard;
import org.testar.core.devices.Mouse;
import org.testar.core.exceptions.SystemStartException;
import org.testar.core.exceptions.SystemStopException;
import org.testar.webdriver.manager.WdChromeManager;
import org.testar.webdriver.manager.WdEdgeManager;
import org.testar.webdriver.manager.WdFirefoxManager;
import org.testar.webdriver.SutConnectorParser;
import org.testar.webdriver.WebdriverPathResolver;
import org.testar.webdriver.manager.WdBrowserManager;
import org.testar.webdriver.alayer.WdCanvasDimensions;
import org.testar.webdriver.alayer.WdMouse;
import org.testar.webdriver.alayer.WdProcessActivator;

import org.openqa.selenium.*;
import org.openqa.selenium.Point;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testar.core.util.Util;
import org.testar.core.alayer.*;
import org.testar.core.state.*;
import org.testar.core.tag.*;

import java.time.Duration;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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
	  String extensionPath = resolveExtensionPath();

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

	  WdCanvasDimensions.startThread();

	  wdDriver = this;
  }

  private static String resolveExtensionPath() {
      String extensionPath = WebdriverPathResolver.resolveExtensionDir().toString();
      logger.log(Level.INFO, "Using WebDriver extension path: " + extensionPath);
      return extensionPath;
  }

  private void loadUrlWithRetry(String url) {
      try {
          remoteWebDriver.get(url);
      } catch (WebDriverException wex) {
          if (wex instanceof TimeoutException) {
              logger.log(Level.WARN, "WebDriver TimeoutException for URL: " + url);
          } else {
              logger.log(Level.WARN, "WebDriverException on first attempt for URL: " + url);
          }

          try {
              logger.log(Level.INFO, "Waiting and retrying URL load...");
              Util.pause(10);
              remoteWebDriver.get(url);
          } catch (WebDriverException retryWex) {
              if (retryWex instanceof TimeoutException) {
                  logger.log(Level.WARN, "Retry WebDriver TimeoutException for URL: " + url);
              } else {
                  logger.log(Level.WARN, "Retry WebDriverException for URL: " + url);
              }
          } catch (Exception retryOther) {
              logger.log(Level.ERROR, "Unexpected exception during retry for URL: " + url);
          }
      } catch (Exception e) {
          logger.log(Level.ERROR, "Unexpected exception for URL: " + url);
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

    WdCanvasDimensions.stopThread();
    
    if(this.get(Tags.PID, -1L) != -1L) {
      stopProcessTree(this.get(Tags.PID));
    }
  }

  private void stopProcessTree(long pid) {
    Optional<ProcessHandle> opt = ProcessHandle.of(pid);
    if (!opt.isPresent()) return;

    ProcessHandle ph = opt.get();
    String cmd = ph.info().command().orElse("").toLowerCase();

    if (!cmd.contains("chrome")) return;

    List<ProcessHandle> procs = Stream
      .concat(ph.descendants(), Stream.of(ph))
      .collect(Collectors.toList());

    procs.forEach(ProcessHandle::destroy);
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

  public static WdDriver fromExecutable(String sutConnector) throws SystemStartException {
    if (remoteWebDriver != null) remoteWebDriver.quit();

    try {
      return new WdDriver(sutConnector);
    } catch (WebDriverException wde) {
      logger.log(Level.WARN, "WebDriverException trying to initialize the web SUT", wde);
      if (remoteWebDriver != null) remoteWebDriver.quit();
      throw new SystemStartException(wde);
    }
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
      return (remoteWebDriver != null) ? remoteWebDriver.getWindowHandles() : new HashSet<>();
    }
    catch (WebDriverException wde) {
      logger.log(Level.WARN, "WebDriverException trying to obtain the window handles", wde);
      return new HashSet<>();
    }
  }

  public static String getCurrentUrl() {
    try {
      return (remoteWebDriver != null) ? remoteWebDriver.getCurrentUrl() : "";
    }
    catch (WebDriverException wde) {
      logger.log(Level.WARN, "WebDriverException trying to obtain the current url", wde);
      return "";
    }
  }

  public static String getTitle() {
    try {
      return (remoteWebDriver != null) ? remoteWebDriver.getTitle() : "";
    }
    catch (WebDriverException wde) {
      logger.log(Level.WARN, "WebDriverException trying to obtain the page title", wde);
      return "";
    }
  }

  public static Object executeScript(String script, Object... args) {
    try {
      // Choose first or last tab, depending on user prefs
      activate();

      // Wait until document is ready; if not, return null (lost session or timeout)
      if(!waitDocumentReady()) {
        logger.log(Level.WARN, "WebDriver document is not ready for interaction");
        logger.log(Level.WARN, "Skipping script execution: " + script);
        return null;
      }

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

  public static boolean waitDocumentReady() {
    if (remoteWebDriver == null) {
      return false;
    }

    try {
      WebDriverWait wait = new WebDriverWait(remoteWebDriver, Duration.ofSeconds(60));
      ExpectedCondition<Boolean> documentReady = driver -> {
      try {
        Object result = remoteWebDriver.executeScript("return document.readyState");
        return result != null && "complete".equals(result.toString());
      } catch (WebDriverException wde) {
        logger.log(Level.WARN, "WebDriverException waiting for the document to be ready", wde);
        return false;
      }
    };
      return wait.until(documentReady);
    } catch (WebDriverException wde) {
      logger.log(Level.WARN, "WebDriverException waiting for the document to be ready", wde);
      return false;
    }
  }

}
