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

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
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
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeDriverService;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.GeckoDriverService;
import org.openqa.selenium.remote.HttpCommandExecutor;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.SessionId;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.awt.*;
import java.awt.event.InputEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
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
    if(!driverPath.contains(".exe") && osName.contains("Windows")) {
    	driverPath = sutConnector.substring(0, sutConnector.indexOf(".exe")+4);
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
    String path = System.getProperty("user.dir");
    String extensionPath = path.substring(0, path.length() - 3) + "web-extension";

    if (driverPath.toLowerCase().contains("chrome")) {
      webDriver = startChromeDriver(driverPath, extensionPath);
    }
    else if (driverPath.toLowerCase().contains("gecko")) {
      webDriver = startGeckoDriver(driverPath, extensionPath);
    }
    else if (driverPath.toLowerCase().contains("microsoft")) {
      webDriver = startEdgeDriver(driverPath, extensionPath);
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

    CanvasDimensions.startThread();

    wdDriver = this;
  }

  private static RemoteWebDriver startChromeDriver(String chromeDriverPath,
                                                   String extensionPath) {
    ChromeDriverService service = new ChromeDriverService.Builder()
        .usingDriverExecutable(new File(chromeDriverPath))
        .usingAnyFreePort()
        .build();
    ChromeOptions options = new ChromeOptions();
    options.addArguments("load-extension=" + extensionPath);
    options.addArguments("disable-infobars");

    Map<String, Object> prefs = new HashMap<>();
    prefs.put("profile.default_content_setting_values.notifications", 1);
    options.setExperimentalOption("prefs", prefs);

    return new ChromeDriver(service, options);
  }

  private static RemoteWebDriver startGeckoDriver(String geckoDriverPath,
                                                  String extensionPath) {
    String nullPath = "/dev/null";
    if (System.getProperty("os.name").toLowerCase().contains("windows")) {
      nullPath = "NUL";
    }

    GeckoDriverService service = new GeckoDriverService.Builder()
        .usingDriverExecutable(new File(geckoDriverPath))
        .withLogFile(new File(nullPath))
        .usingAnyFreePort()
        .build();

    FirefoxProfile profile = new FirefoxProfile();
    profile.setPreference("dom.webnotifications.enabled", false);
    FirefoxOptions options = new FirefoxOptions();
    options.setProfile(profile);

    RemoteWebDriver webDriver = new FirefoxDriver(service, options);
    loadGeckoExtension(webDriver, extensionPath);

    return webDriver;
  }

  private static void loadGeckoExtension(RemoteWebDriver webDriver,
                                         String extensionPath) {
    // Create payload
    String payload = "{" +
                     "\"path\": \"" + extensionPath.replace("\\", "\\\\") + "\", " +
                     "\"temporary\": true }";
    StringEntity entity = new StringEntity(payload,
        ContentType.APPLICATION_FORM_URLENCODED);

    // Determine the endpoint
    HttpCommandExecutor ce = (HttpCommandExecutor) webDriver.getCommandExecutor();
    SessionId sessionId = webDriver.getSessionId();
    String urlString = String.format(
        "http://localhost:%s/session/%s/moz/addon/install",
        ce.getAddressOfRemoteServer().getPort(), sessionId);

    // POST payload to the endpoint
    HttpClient httpClient = HttpClientBuilder.create().build();
    HttpPost request = new HttpPost(urlString);
    request.setEntity(entity);
    try {
      httpClient.execute(request);
    }
    catch (IOException ioe) {
      throw new SystemStartException(ioe);
    }
  }

  private static RemoteWebDriver startEdgeDriver(
      String edgeDriverPath, String extensionPath) {
    // Edge can only load extensions from a system path
    String edgeSideLoadPath = copyExtension(extensionPath);

    EdgeDriverService service = new EdgeDriverService.Builder()
        .usingDriverExecutable(new File(edgeDriverPath))
        .usingAnyFreePort()
        .build();

    EdgeOptions options = new EdgeOptions();
    options.setCapability("extensionPaths", new String[]{edgeSideLoadPath});
    RemoteWebDriver webDriver = new EdgeDriver(service, options);

    closeDialog(webDriver);

    return webDriver;
  }

  private static String copyExtension(String extensionPath) {
    String appDataDir = System.getenv("LOCALAPPDATA");
    String extensionDir = new File(extensionPath).getName();
    String subDirs = "Packages\\Microsoft.MicrosoftEdge_8wekyb3d8bbwe\\LocalState";
    String edgeSideLoadPath = String.format("%s\\%s\\%s", appDataDir, subDirs, extensionDir);

    File sourceFolder = new File(extensionPath);
    File targetFolder = new File(edgeSideLoadPath);
    try {
      copyFolder(sourceFolder, targetFolder);
    }
    catch (IOException ioe) {
      throw new SystemStartException(ioe);
    }

    return edgeSideLoadPath;
  }

  private static void copyFolder(File sourceFolder, File targetFolder) throws IOException {
    if (sourceFolder.isDirectory()) {
      if (!targetFolder.exists()) {
        targetFolder.mkdir();
      }

      for (String file : sourceFolder.list()) {
        File sourceFile = new File(sourceFolder, file);
        File targetFile = new File(targetFolder, file);
        copyFolder(sourceFile, targetFile);
      }
    }
    else {
      Files.copy(sourceFolder.toPath(), targetFolder.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }
  }

  /**
   * Edge has an annoying bug during the loading of the extension
   * This method closes the dialog without user action
   */
  private static void closeDialog(RemoteWebDriver webDriver) {
    // Fix window size/position, so we don't have to look for the button
    Dimension screenDimensions = new Dimension(800, 400);
    webDriver.manage().window().setSize(screenDimensions);
    Point screenPosition = new Point(0, 0);
    webDriver.manage().window().setPosition(screenPosition);

    try {
      Robot robot = new Robot();
      robot.mouseMove(745, 365);
      robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
      robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }
    catch (AWTException awte) {
      awte.printStackTrace();
    }
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

    // Nothing to activate
    if (windowHandles.size() < 1) {
      return;
    }

    String handle = windowHandles.get(followLinks ? windowHandles.size() - 1 : 0);
    try {
      webDriver.switchTo().window(handle);
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
