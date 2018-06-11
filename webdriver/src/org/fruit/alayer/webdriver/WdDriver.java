package org.fruit.alayer.webdriver;

import org.fruit.alayer.*;
import org.fruit.alayer.devices.AWTKeyboard;
import org.fruit.alayer.devices.Keyboard;
import org.fruit.alayer.devices.Mouse;
import org.fruit.alayer.exceptions.SystemStartException;
import org.fruit.alayer.exceptions.SystemStopException;
import org.openqa.selenium.*;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.fruit.Util.pause;


public class WdDriver extends SUTBase {
  private static RemoteWebDriver webDriver = null;

  private final Keyboard kbd = AWTKeyboard.build();
  private final Mouse mouse = WdMouse.build();

  private WdDriver(String sutConnector) {
    String[] parts = sutConnector.split(" ");
    String driverPath = parts[0].replace("\"", "");
    String url = parts[parts.length - 1].replace("\"", "");

    ChromeDriverService service = new ChromeDriverService.Builder()
        .usingDriverExecutable(new File(driverPath))
        .usingAnyFreePort()
        .build();
    ChromeOptions options = new ChromeOptions();
    String path = System.getProperty("user.dir");
    path = path.substring(0, path.length() - 3) + "chrome-extension";
    options.addArguments("load-extension=" + path);
    options.addArguments("disable-infobars");
    webDriver = new ChromeDriver(service, options);
    webDriver.get(url);

    CanvasDimensions.startThread();
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
    return webDriver != null;
  }

  @Override
  public String getStatus() {
    return "WebDriver : " + webDriver.getCurrentUrl();
  }

  @Override
  public AutomationCache getNativeAutomationCache() {
    return null;
  }

  @Override
  public void setNativeAutomationCache() {
  }

  public static List<SUT> fromAll() {
    List<SUT> suts = new ArrayList<>();

    // TODO GB

    return suts;
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
      Utils.logAndEnd();
      return null;
      // return (T) (Long) hProcess;
    }
    else if (tag.equals(Tags.ProcessHandles)) {
      Utils.logAndEnd();
      return null;
      // return (T) runningProcesses().iterator();
    }
    else if (tag.equals(Tags.SystemActivator)) {
      return (T) new WdProcessActivator();
    }
    return null;
  }

  public RemoteWebDriver getRemoteWebDriver() {
    return webDriver;
  }

  /*
   * Make sure the last tab has focus
   */
  public static void activate() {
    // TODO Remember window handles for each element / widget?

    for (String handle : webDriver.getWindowHandles()) {
      webDriver.switchTo().window(handle);
    }
  }

  public static String getCurrentUrl () {
    return webDriver.getCurrentUrl();
  }

  public static Set<String> getWindowHandles() {
    return webDriver.getWindowHandles();
  }

  public static Object executeScript(String script, Object... args) {
    if (webDriver == null) {
      return null;
    }

      try {
        return webDriver.executeScript(script, args);
      }
    catch (NoSuchWindowException nswe) {
      // Make sure we have the last tab
      activate();
      return webDriver.executeScript(script, args);
    }
  }
}
