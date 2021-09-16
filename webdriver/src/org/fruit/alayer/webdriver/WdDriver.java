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
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogType;
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
  public static boolean fullScreen = false;
  public static boolean forceActivateTab = true;
  public static boolean disableSecurity = false;
  public static boolean headlessMode = false;
  
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
    if (parts.length == 3 && !fullScreen) {
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
    //options.addArguments("load-extension=" + extensionPath);
    options.addArguments("disable-infobars");
  
    if(fullScreen) {
    	options.addArguments("--start-maximized");
    }
    if(disableSecurity) {
    	options.addArguments("--disable-web-security");
    	options.addArguments("--allow-running-insecure-content");
    }
    if (headlessMode) {
      options.addArguments("--headless");
    }
    
    Map<String, Object> prefs = new HashMap<>();
    prefs.put("profile.default_content_setting_values.notifications", 1);
    options.setExperimentalOption("prefs", prefs);
    
    ChromeDriver driver = new ChromeDriver(service, options);
    
    Map<String, Object> map = new HashMap<>();

    map.put("source", testar_event_handlers);
    driver.executeCdpCommand("Page.addScriptToEvaluateOnNewDocument", map);
    
    map.put("source", testar_canvas);
    driver.executeCdpCommand("Page.addScriptToEvaluateOnNewDocument", map);
  
    map.put("source", testar_state);
    driver.executeCdpCommand("Page.addScriptToEvaluateOnNewDocument", map);
    
    return driver;
  }

  static public LogEntries getLog() {
    LogEntries logs = webDriver.manage().logs().get(LogType.BROWSER);
    return logs;
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

    // Nothing to activate, or user doesn't want to use this activate feature
    if (windowHandles.size() < 1 || !forceActivateTab) {
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
  
  final static String testar_event_handlers = "" +
      "    EventTarget.prototype._addEventListener = EventTarget.prototype.addEventListener;\n" +
      "    EventTarget.prototype.addEventListener = function (a, b, c) {\n" +
      "        if (c === undefined)\n" +
      "            c = false;\n" +
      "        this._addEventListener(a, b, c);\n" +
      "        if (!this.eventListenerList)\n" +
      "            this.eventListenerList = {};\n" +
      "        if (!this.eventListenerList[a])\n" +
      "            this.eventListenerList[a] = [];\n" +
      "        this.eventListenerList[a].push({listener: b, useCapture: c});\n" +
      "    };\n" +
      "\n" +
      "    EventTarget.prototype.getEventListeners = function (a) {\n" +
      "        if (!this.eventListenerList)\n" +
      "            this.eventListenerList = {};\n" +
      "        if (a === undefined)\n" +
      "            return this.eventListenerList;\n" +
      "        return this.eventListenerList[a];\n" +
      "    };\n" +
      "\n" +
      "    EventTarget.prototype.clearEventListeners = function (a) {\n" +
      "        if (!this.eventListenerList)\n" +
      "            this.eventListenerList = {};\n" +
      "        if (a === undefined) {\n" +
      "            for (var x in (this.getEventListeners())) this.clearEventListeners(x);\n" +
      "            return;\n" +
      "        }\n" +
      "        var el = this.getEventListeners(a);\n" +
      "        if (el === undefined)\n" +
      "            return;\n" +
      "        for (var i = el.length - 1; i >= 0; --i) {\n" +
      "            var ev = el[i];\n" +
      "            this.removeEventListener(a, ev.listener, ev.useCapture);\n" +
      "        }\n" +
      "    };\n" +
      "\n" +
      "    EventTarget.prototype._removeEventListener = EventTarget.prototype.removeEventListener;\n" +
      "    EventTarget.prototype.removeEventListener = function (a, b, c) {\n" +
      "        if (c === undefined)\n" +
      "            c = false;\n" +
      "        this._removeEventListener(a, b, c);\n" +
      "        if (!this.eventListenerList)\n" +
      "            this.eventListenerList = {};\n" +
      "        if (!this.eventListenerList[a])\n" +
      "            this.eventListenerList[a] = [];\n" +
      "\n" +
      "        for (var i = 0; i < this.eventListenerList[a].length; i++) {\n" +
      "            if (this.eventListenerList[a][i].listener === b &&\n" +
      "                this.eventListenerList[a][i].useCapture === c) {\n" +
      "                this.eventListenerList[a].splice(i, 1);\n" +
      "                break;\n" +
      "            }\n" +
      "        }\n" +
      "        if (this.eventListenerList[a].length === 0)\n" +
      "            delete this.eventListenerList[a];\n" +
      "    };\n" +
      "\n" +
      "    window.alert = function () {};\n" +
      "    window.confirm = function () {};\n" +
      "    window.print = function () {};\n" +
      "    navigator.geolocation.getCurrentPosition = function () {};";
  
  public final static String testar_canvas = "" +
      "function addCanvasTestar() {\n" +
      "    if (typeof testar_canvas === 'object') {\n" +
      "        ensureCanvasOnTop();\n" +
      "        return typeof testar_canvas;\n" +
      "    }\n" +
      "\n" +
      "    testar_canvas = document.createElement('canvas');\n" +
      "    testar_canvas.setAttribute(\"style\", \"pointer-events: none; position: absolute; top: 0; left: 0;\");\n" +
      "    testar_canvas.id = 'testar_canvas';\n" +
      "    document.body.appendChild(testar_canvas);\n" +
      "    testarCtx = testar_canvas.getContext('2d');\n" +
      "\n" +
      "    resizeCanvasTestar();\n" +
      "\n" +
      "    window.addEventListener('resize', resizeCanvasTestar, true);\n" +
      "    window.addEventListener('scroll', resizeCanvasTestar, true);\n" +
      "\n" +
      "    ensureCanvasOnTop();\n" +
      "    return typeof testar_canvas;\n" +
      "}\n" +
      "\n" +
      "function ensureCanvasOnTop() {\n" +
      "    var lengths = Array.from(document.querySelectorAll('body *'))\n" +
      "        .map(a => parseFloat(window.getComputedStyle(a).zIndex))\n" +
      "        .filter(a => !isNaN(a));\n" +
      "    var maxIndex = Math.max.apply(null, lengths);\n" +
      "    if (testar_canvas.style.zIndex < maxIndex) {\n" +
      "        testar_canvas.style.zIndex = maxIndex + 1;\n" +
      "    }\n" +
      "}\n" +
      "\n" +
      "function canvasDimensionsTestar() {\n" +
      "    return [window.screenX,\n" +
      "        window.screenY + window.outerHeight - window.innerHeight,\n" +
      "        document.documentElement.clientWidth,\n" +
      "        document.documentElement.clientHeight,\n" +
      "        window.innerWidth,\n" +
      "        window.innerHeight\n" +
      "    ];\n" +
      "}\n" +
      "\n" +
      "function resizeCanvasTestar() {\n" +
      "    testar_canvas.width = document.documentElement.clientWidth;\n" +
      "    testar_canvas.height = document.documentElement.clientHeight;\n" +
      "    var scrollLeft = Math.max(document.documentElement.scrollLeft, document.body.scrollLeft);\n" +
      "    testar_canvas.style.left = scrollLeft + \"px\";\n" +
      "    var scrollTop = Math.max(document.documentElement.scrollTop, document.body.scrollTop);\n" +
      "    testar_canvas.style.top = scrollTop + \"px\";\n" +
      "}\n" +
      "\n" +
      "function clearCanvasTestar(args) {\n" +
      "    testarCtx.clearRect(args[0], args[1], args[2], args[3]);\n" +
      "}\n" +
      "\n" +
      "function drawTextTestar(args) {\n" +
      "    testarCtx.fillStyle = args[0];\n" +
      "    testarCtx.font = args[1] + 'px ' + args[2];\n" +
      "    testarCtx.fillText(args[3], args[4], args[5]);\n" +
      "}\n" +
      "\n" +
      "function drawLineTestar(args) {\n" +
      "    testarCtx.fillStyle = args[0];\n" +
      "    testarCtx.lineWidth = args[1];\n" +
      "    testarCtx.beginPath();\n" +
      "    testarCtx.moveTo(args[2], args[3]);\n" +
      "    testarCtx.lineTo(args[4], args[5]);\n" +
      "    testarCtx.stroke();\n" +
      "}\n" +
      "\n" +
      "function drawTriangleTestar(args) {\n" +
      "    testarCtx.fillStyle = args[0];\n" +
      "    testarCtx.strokeStyle = args[1];\n" +
      "    testarCtx.lineWidth = args[1];\n" +
      "    testarCtx.beginPath();\n" +
      "    testarCtx.moveTo(args[3], args[4]);\n" +
      "    testarCtx.lineTo(args[5], args[6]);\n" +
      "    testarCtx.lineTo(args[7], args[8]);\n" +
      "    testarCtx.lineTo(args[3], args[4]);\n" +
      "    testarCtx.lineTo(args[5], args[6]);\n" +
      "\n" +
      "    if (args[2] === \"fill\") {\n" +
      "        testarCtx.fill();\n" +
      "    } else if (args[2] === \"stroke\") {\n" +
      "        testarCtx.stroke();\n" +
      "    }\n" +
      "}\n" +
      "\n" +
      "function drawEllipseTestar(args) {\n" +
      "    testarCtx.fillStyle = args[0];\n" +
      "    testarCtx.strokeStyle = args[0];\n" +
      "    testarCtx.lineWidth = args[1];\n" +
      "    testarCtx.beginPath();\n" +
      "    testarCtx.ellipse(args[3], args[4], args[5], args[6], 0, 0, 2 * Math.PI);\n" +
      "\n" +
      "    if (args[2] === \"fill\") {\n" +
      "        testarCtx.fill();\n" +
      "    } else if (args[2] === \"stroke\") {\n" +
      "        testarCtx.stroke();\n" +
      "    }\n" +
      "}\n" +
      "\n" +
      "function drawRectTestar(args) {\n" +
      "    testarCtx.fillStyle = args[0];\n" +
      "    testarCtx.lineWidth = args[1];\n" +
      "    testarCtx.strokeStyle = args[0];\n" +
      "\n" +
      "    if (args[2] === 'fillRect') {\n" +
      "        testarCtx.fillRect(args[3], args[4], args[5], args[6]);\n" +
      "    } else if (args[2] === 'strokeRect') {\n" +
      "        testarCtx.strokeRect(args[3], args[4], args[5], args[6]);\n" +
      "    }\n" +
      "}\n";
  
  public final static String testar_state = "var labelMap;\n" +
      "\n" +
      "var getStateTreeTestar = function (ignoredTags) {\n" +
      "    var body = document.body;\n" +
      "    var bodyWrapped = wrapElementTestar(body, 0, 0);\n" +
      "    bodyWrapped['documentHasFocus'] = document.hasFocus();\n" +
      "    bodyWrapped['documentTitle'] = document.title;\n" +
      "\n" +
      "    getLabelMapTestar();\n" +
      "\n" +
      "    if (window.navigator.userAgent.indexOf(\"Edge\") > -1) {\n" +
      "        var treeArray = [];\n" +
      "        traverseElementArrayTestar(treeArray, bodyWrapped, body, -1, ignoredTags);\n" +
      "        return treeArray;\n" +
      "    }\n" +
      "    else {\n" +
      "        traverseElementTestar(bodyWrapped, body, ignoredTags);\n" +
      "        return bodyWrapped;\n" +
      "    }\n" +
      "};\n" +
      "\n" +
      "function traverseElementTestar(parentWrapped, rootElement, ignoredTags) {\n" +
      "    var childNodes = getChildNodesTestar(parentWrapped);\n" +
      "    for (var i = 0; i < childNodes.length; i++) {\n" +
      "        var childElement = childNodes[i];\n" +
      "\n" +
      "        if (childElement.nodeType === 3) {\n" +
      "            parentWrapped.textContent += childElement.textContent;\n" +
      "            parentWrapped.textContent = parentWrapped.textContent.trim();\n" +
      "            continue;\n" +
      "        }\n" +
      "        if (childElement.nodeType !== 1 ||\n" +
      "            ignoredTags.includes(childElement.nodeName.toLowerCase())) {\n" +
      "            continue;\n" +
      "        }\n" +
      "\n" +
      "        var childWrapped = wrapElementTestar(childElement, parentWrapped[\"xOffset\"], parentWrapped[\"yOffset\"]);\n" +
      "        traverseElementTestar(childWrapped, rootElement, ignoredTags);\n" +
      "        parentWrapped.wrappedChildren.push(childWrapped);\n" +
      "    }\n" +
      "\t\n" +
      "\tif(parentWrapped.element.shadowRoot !== null){\n" +
      "\t\tvar shadowNodes = parentWrapped.element.shadowRoot.childNodes;\n" +
      "\t\tfor (var i = 0; i < shadowNodes.length; i++) {\n" +
      "\t\t\tvar childShadowElement = shadowNodes[i];\n" +
      "\t\t\t\n" +
      "\t\t\tif (childShadowElement.nodeType === 3) {\n" +
      "\t\t\t\tparentWrapped.textContent += childShadowElement.textContent;\n" +
      "\t\t\t\tparentWrapped.textContent = parentWrapped.textContent.trim();\n" +
      "\t\t\t\tcontinue;\n" +
      "\t\t\t}\n" +
      "\t\t\tif (childShadowElement.nodeType !== 1 ||\n" +
      "\t\t\t\tignoredTags.includes(childShadowElement.nodeName.toLowerCase())) {\n" +
      "\t\t\t\tcontinue;\n" +
      "\t\t\t}\n" +
      "\t\t\t\n" +
      "\t\t\tvar childShadowWrapped = wrapElementTestar(childShadowElement, parentWrapped[\"xOffset\"], parentWrapped[\"yOffset\"]);\n" +
      "\t\t\ttraverseElementTestar(childShadowWrapped, rootElement, ignoredTags);\n" +
      "\t\t\tparentWrapped.wrappedChildren.push(childShadowWrapped);\n" +
      "\t\t}\n" +
      "\t}\n" +
      "}\n" +
      "\n" +
      "function traverseElementArrayTestar(treeArray, parentWrapped, rootElement, parentId, ignoredTags) {\n" +
      "    parentWrapped['parentId'] = parentId;\n" +
      "    treeArray.push(parentWrapped);\n" +
      "\n" +
      "    parentId = treeArray.length - 1;\n" +
      "\n" +
      "    var childNodes = getChildNodesTestar(parentWrapped);\n" +
      "    for (var i = 0; i < childNodes.length; i++) {\n" +
      "        var childElement = childNodes[i];\n" +
      "\n" +
      "        if (childElement.nodeType === 3) {\n" +
      "            parentWrapped.textContent += childElement.textContent;\n" +
      "            parentWrapped.textContent = parentWrapped.textContent.trim();\n" +
      "            continue;\n" +
      "        }\n" +
      "        if (childElement.nodeType !== 1 ||\n" +
      "            ignoredTags.includes(childElement.nodeName.toLowerCase())) {\n" +
      "            continue\n" +
      "        }\n" +
      "\n" +
      "        var childWrapped = wrapElementTestar(childElement, parentWrapped[\"xOffset\"], parentWrapped[\"yOffset\"]);\n" +
      "        traverseElementArrayTestar(treeArray, childWrapped, rootElement, parentId, ignoredTags);\n" +
      "    }\n" +
      "}\n" +
      "\n" +
      "function getChildNodesTestar(parentWrapped) {\n" +
      "    var childNodes = parentWrapped.element.childNodes;\n" +
      "\n" +
      "    if (childNodes.length === 0 &&\n" +
      "        parentWrapped.element.contentDocument !== undefined &&\n" +
      "        parentWrapped.element.contentDocument !== null) {\n" +
      "        childNodes = parentWrapped.element.contentDocument.childNodes;\n" +
      "\n" +
      "        var style = getComputedStyle(parentWrapped.element);\n" +
      "        var left = parseInt(style.getPropertyValue('border-left-width'));\n" +
      "        left += parseInt(style.getPropertyValue('padding-left'));\n" +
      "        parentWrapped[\"xOffset\"] = parentWrapped['rect'][0] + left;\n" +
      "        var top = parseInt(style.getPropertyValue('border-top-width'));\n" +
      "        top += parseInt(style.getPropertyValue('padding-top'));\n" +
      "        parentWrapped[\"yOffset\"] = parentWrapped['rect'][1] + top;\n" +
      "    }\n" +
      "\n" +
      "    return childNodes;\n" +
      "}\n" +
      "\n" +
      "function wrapElementTestar(element, xOffset, yOffset) {\n" +
      "    var computedStyle = getComputedStyle(element);\n" +
      "\t\n" +
      "\tvar shadowElement = false;\n" +
      "\tif(element.shadowRoot !== null){\n" +
      "\t\tshadowElement = true;\n" +
      "\t}\n" +
      "\n" +
      "    return {\n" +
      "        element: element,\n" +
      "\n" +
      "        attributeMap: getAttributeMapTestar(element),\n" +
      "\n" +
      "        name: getNameTestar(element),\n" +
      "        tagName: element.tagName.toLowerCase(),\n" +
      "        textContent: \"\",\n" +
      "        value: element.value,\n" +
      "        checked: element.checked,\n" +
      "        selected: element.selected,\n" +
      "        display: computedStyle.getPropertyValue('display'),\n" +
      "\n" +
      "        zIndex: getZIndexTestar(element),\n" +
      "        rect: getRectTestar(element, xOffset, yOffset),\n" +
      "        dimensions: getDimensionsTestar(element),\n" +
      "        isBlocked: getIsBlockedTestar(element, xOffset, yOffset),\n" +
      "        isClickable: isClickableTestar(element, xOffset, yOffset),\n" +
      "\t\tisShadowElement: shadowElement,\n" +
      "        hasKeyboardFocus: document.activeElement === element,\n" +
      "\n" +
      "        wrappedChildren: [],\n" +
      "        xOffset: xOffset,\n" +
      "        yOffset: yOffset\n" +
      "    };\n" +
      "}\n" +
      "\n" +
      "function getNameTestar(element) {\n" +
      "    var name = element.getAttribute(\"name\");\n" +
      "    var id = element.getAttribute(\"id\");\n" +
      "\n" +
      "    try {\n" +
      "        if (id && labelMap[id]) {\n" +
      "            return labelMap[id];\n" +
      "        }\n" +
      "    }\n" +
      "    catch (err) {\n" +
      "    }\n" +
      "\n" +
      "    if (name) {\n" +
      "        return name;\n" +
      "    }\n" +
      "    if (id) {\n" +
      "        return id;\n" +
      "    }\n" +
      "    return \"\";\n" +
      "}\n" +
      "\n" +
      "function getZIndexTestar(element) {\n" +
      "    if (element === document.body) {\n" +
      "        return 0;\n" +
      "    }\n" +
      "\n" +
      "    if (element === null || element === undefined) {\n" +
      "        return 0;\n" +
      "    } else if (element.nodeType !== 1) {\n" +
      "        return getZIndexTestar(element.parentNode) + 1;\n" +
      "    }\n" +
      "\n" +
      "    var zIndex = getComputedStyle(element).getPropertyValue('z-index');\n" +
      "    if (isNaN(zIndex)) {\n" +
      "        return getZIndexTestar(element.parentNode) + 1;\n" +
      "    }\n" +
      "    return zIndex * 1;\n" +
      "}\n" +
      "\n" +
      "function getRectTestar(element, xOffset, yOffset) {\n" +
      "    var rect = element.getBoundingClientRect();\n" +
      "    if (element === document.body) {\n" +
      "        rect = document.documentElement.getBoundingClientRect();\n" +
      "    }\n" +
      "\n" +
      "    return [\n" +
      "        parseInt(rect.left) + xOffset,\n" +
      "        parseInt(rect.top) + yOffset,\n" +
      "        parseInt(element === document.body ? window.innerWidth : rect.width),\n" +
      "        parseInt(element === document.body ? window.innerHeight : rect.height)\n" +
      "    ];\n" +
      "}\n" +
      "\n" +
      "function getDimensionsTestar(element) {\n" +
      "    if (element === document.body) {\n" +
      "        scrollLeft = Math.max(document.documentElement.scrollLeft, document.body.scrollLeft);\n" +
      "        scrollTop = Math.max(document.documentElement.scrollTop, document.body.scrollTop);\n" +
      "    } else {\n" +
      "        scrollLeft = element.scrollLeft;\n" +
      "        scrollTop = element.scrollTop;\n" +
      "    }\n" +
      "\n" +
      "    var style = window.getComputedStyle(element);\n" +
      "\n" +
      "    return {\n" +
      "        overflowX: style.getPropertyValue('overflow-x'),\n" +
      "        overflowY: style.getPropertyValue('overflow-y'),\n" +
      "        clientWidth: element.clientWidth,\n" +
      "        clientHeight: element.clientHeight,\n" +
      "        offsetWidth: element.offsetWidth || 0,\n" +
      "        offsetHeight: element.offsetHeight || 0,\n" +
      "        scrollWidth: element.scrollWidth,\n" +
      "        scrollHeight: element.scrollHeight,\n" +
      "        scrollLeft: scrollLeft,\n" +
      "        scrollTop: scrollTop,\n" +
      "        borderWidth: parseInt(style.borderLeftWidth, 10) + parseInt(style.borderRightWidth, 10),\n" +
      "        borderHeight: parseInt(style.borderTopWidth, 10) + parseInt(style.borderBottomWidth, 10)\n" +
      "    };\n" +
      "}\n" +
      "\n" +
      "function isPageVerticalScrollable(){\n" +
      "\treturn (document.body.clientHeight > window.innerHeight);\n" +
      "}\n" +
      "\n" +
      "function isPageHorizontalScrollable(){\n" +
      "\treturn (document.body.clientWidth > window.innerWidth);\n" +
      "}\n" +
      "\n" +
      "function getIsBlockedTestar(element, xOffset, yOffset) {\n" +
      "    var rect = element.getBoundingClientRect();\n" +
      "    var x = rect.left + rect.width / 2 + xOffset;\n" +
      "    var y = rect.top + rect.height / 2 + yOffset;\n" +
      "    var elem = document.elementFromPoint(x, y);\n" +
      "\n" +
      "    var outer = undefined;\n" +
      "    while (elem instanceof HTMLIFrameElement ||\n" +
      "           elem instanceof HTMLFrameElement ||\n" +
      "           (outer !== undefined && outer.contentWindow !== undefined &&\n" +
      "            elem instanceof outer.contentWindow.HTMLIFrameElement)) {\n" +
      "        outer = elem;\n" +
      "\n" +
      "        var tmp = elem.getBoundingClientRect();\n" +
      "        x -= tmp.x;\n" +
      "        y -= tmp.y;\n" +
      "\n" +
      "        try {\n" +
      "            elem = elem.contentWindow.document.elementFromPoint(x, y);\n" +
      "        }\n" +
      "        catch(exception) {\n" +
      "            return true\n" +
      "        }\n" +
      "    }\n" +
      "\n" +
      "    if (elem === null) {\n" +
      "        return false;\n" +
      "    }\n" +
      "\n" +
      "    if (element.tagName === \"A\" && element.contains(elem)) {\n" +
      "        return false;\n" +
      "    }\n" +
      "\n" +
      "    if (elem.tagName === \"LABEL\") {\n" +
      "        return false;\n" +
      "    }\n" +
      "\n" +
      "    return elem.parentNode !== element.parentNode;\n" +
      "}\n" +
      "\n" +
      "function isClickableTestar(element) {\n" +
      "    if (element.getAttribute(\"onclick\") !== null) {\n" +
      "        return true;\n" +
      "    }\n" +
      "    if (element.getEventListeners === undefined) {\n" +
      "        return false;\n" +
      "    }\n" +
      "    var arr = element.getEventListeners('click');\n" +
      "    return arr !== undefined && arr.length > 0;\n" +
      "}\n" +
      "\n" +
      "function getLabelMapTestar() {\n" +
      "    labelMap = {};\n" +
      "    var labelList = document.getElementsByTagName(\"LABEL\");\n" +
      "    for (var i = 0; i < labelList.length; i++) {\n" +
      "        var item = labelList[i];\n" +
      "        if (item.getAttribute(\"for\")) {\n" +
      "            labelMap[item.getAttribute(\"for\")] = item.textContent;\n" +
      "        }\n" +
      "    }\n" +
      "}\n" +
      "\n" +
      "function getAttributeMapTestar(element) {\n" +
      "    return Array.from(element.attributes).reduce(function (map, att) {\n" +
      "        map[att.name] = att.nodeValue;\n" +
      "        return map;\n" +
      "    }, {});\n" +
      "}\n";
}
