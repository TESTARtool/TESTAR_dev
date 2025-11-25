/***************************************************************************************************
 *
 * Copyright (c) 2020 - 2025 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2020 - 2025 Open Universiteit - www.ou.nl
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
 *******************************************************************************************************/

package org.testar.monkey.alayer.android;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.testar.serialisation.ScreenshotSerialiser;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.GsmCallActions;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import io.appium.java_client.appmanagement.ApplicationState;
import io.appium.java_client.touch.LongPressOptions;
import io.appium.java_client.touch.offset.ElementOption;

import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.exceptions.SystemStopException;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testar.monkey.alayer.android.enums.AndroidTags;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class AndroidAppiumFramework extends SUTBase {

	public static AndroidAppiumFramework androidSUT = null;

	private static AndroidDriver driver = null;

	// Appium v2 do not use /wd/hub suffix anymore
	// It can be enabled using the "--base-path /wd/hub" command when launching the Appium server
	public static String androidAppiumURL = "http://127.0.0.1:4723/wd/hub";

	public AndroidAppiumFramework(DesiredCapabilities cap) {
		try {
			driver = new AndroidDriver(new URL(androidAppiumURL), cap);
			// Next few lines of code enable the show touches in Android.
			// command to be executed: adb shell content insert --uri content://settings/system --bind name:s:show_touches --bind value:i:1
			List<String> showTouchesArgs = Arrays.asList(
					"insert",
					"--uri",
					"content://settings/system",
					"--bind",
					"name:s:show_touches",
					"--bind",
					"value:i:1"
					);
			Map<String, Object> showTouchesCmd = ImmutableMap.of(
					"command", "content",
					"args", showTouchesArgs
					);
			driver.executeScript("mobile: shell", showTouchesCmd);

			// command2 to be executed: adb shell settings put system pointer_location 1
			/*
			List<String> showPointerArgs = Arrays.asList(
					"put",
					"system",
					"pointer_location",
					"1"
			);
			Map<String, Object> showPointerCmd = ImmutableMap.of(
					"command", "settings",
					"args", showPointerArgs
			);
			driver.executeScript("mobile: shell", showPointerCmd);
			 */
		} catch (MalformedURLException e) {
			System.err.println("ERROR: Exception with Android Driver URL: http://0.0.0.0:4723/wd/hub");
			e.printStackTrace();
		}
	}

	public static AndroidAppiumFramework fromCapabilities(String capabilitesJsonFile) {
		if (androidSUT != null) {
			androidSUT.stop();
		}

		DesiredCapabilities cap = createCapabilitiesFromJsonFile(capabilitesJsonFile);

		return new AndroidAppiumFramework(cap);
	}

	public static AndroidDriver getDriver() {
		return driver;
	}

	public static List<WebElement> findElements(By by){
		return driver.findElements(by);
	}

	/**
	 * Send Click Action. 
	 * Uses unique accessibility ID if present, otherwise uses xpath. 
	 * 
	 * @param id
	 * @param w
	 */
	public static void clickElementById(String id, Widget w){
		if (!id.equals("")) {
			driver.findElement(new AppiumBy.ByAccessibilityId(id)).click();
		}
		else {
			String xpathString = w.get(AndroidTags.AndroidXpath);
			driver.findElement(new By.ByXPath(xpathString)).click();
		}
	}

	/**
	 * Send Type Action. 
	 * Uses unique accessibility ID if present, otherwise uses xpath. 
	 * 
	 * @param id
	 * @param text
	 * @param w
	 */
	public static void sendKeysTextTextElementById(String id, String text, Widget w){
		// Try by accessibility id only if it's non-null and non-empty
		if (id != null && !id.isEmpty()) {
			List<WebElement> elements = driver.findElements(new AppiumBy.ByAccessibilityId(id));

			// Use the ID only if exactly one element is found
			if (elements.size() == 1) {
				WebElement element = elements.get(0);
				element.clear();
				element.sendKeys(text);
				return;
			}
		}

		// Fallback using XPath: ID is empty or did not resolve to exactly one element
		AndroidAppiumFramework.sendKeysTextTextElementByXPath(text, w);
	}

	/**
	 * Send Type Action. 
	 * Uses unique xpath to identify the element. 
	 * 
	 * @param text
	 * @param w
	 */
	public static void sendKeysTextTextElementByXPath(String text, Widget w){
		String xpathString = w.get(AndroidTags.AndroidXpath);
		WebElement element = driver.findElement(new By.ByXPath(xpathString));
		element.clear();
		element.sendKeys(text);
	}

	public static void scrollElementById(String id, Widget w, int scrollDistance) {
		Duration NO_TIME = Duration.ofMillis(0);
		Duration STEP_DURATION = Duration.ofMillis(20);

		PointerInput.Origin VIEW = PointerInput.Origin.viewport();
		Rect bounds = w.get(AndroidTags.AndroidBounds);

		int startCoorsX = (int) (bounds.x() + 0.5*bounds.width());
		int startCoorsY = (int) (bounds.y() + 0.5*bounds.height());

		//TODO: THIS SECTION NOW DISPLAYS DRAG AND DROP BEHAVIOR!
		//TouchAction touchAction = new TouchAction(driver);
		//touchAction.press(PointOption.point(594, 723)).moveTo(PointOption.point(580, 1236)).perform();

		PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
		Sequence scroll = new Sequence(finger, 0);
		scroll.addAction(finger.createPointerMove(Duration.ofMillis(10), VIEW, startCoorsX, startCoorsY));
		scroll.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
		scroll.addAction(finger.createPointerMove(STEP_DURATION, VIEW, startCoorsX, (int)(startCoorsY-(0.2*scrollDistance))));
		scroll.addAction(finger.createPointerMove(STEP_DURATION, VIEW, startCoorsX, (int)(startCoorsY-(0.4*scrollDistance))));
		scroll.addAction(finger.createPointerMove(STEP_DURATION, VIEW, startCoorsX, (int)(startCoorsY-(0.6*scrollDistance))));
		scroll.addAction(finger.createPointerMove(STEP_DURATION, VIEW, startCoorsX, (int)(startCoorsY-(0.8*scrollDistance))));
		scroll.addAction(finger.createPointerMove(STEP_DURATION, VIEW, startCoorsX, (int)(startCoorsY-(1.0*scrollDistance))));

		scroll.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
		driver.perform(Arrays.asList(scroll));

		try {
			TimeUnit.MILLISECONDS.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void longClickElementById(String id, Widget w) {
		WebElement el;
		if (!id.equals("")) {
			el = driver.findElement(new AppiumBy.ByAccessibilityId(id));
		}
		else {
			String xpathString = w.get(AndroidTags.AndroidXpath);
			el = driver.findElement(new By.ByXPath(xpathString));
		}

		LongPressOptions longPressOptions = new LongPressOptions();
		longPressOptions.withDuration(Duration.ofMillis(2500)).withElement(ElementOption.element(el));
		new TouchAction(driver).longPress(longPressOptions).perform();
	}

	public static void clickBackButton() {
		driver.navigate().back();
	}

	/** Zooming functions.
	public static void zoomIn(Point center, int distance) {
		Pair<Sequence, Sequence> zoomInPair = zoom(center, 200, 200 + distance, 45, Duration.ofMillis(25));
		Sequence leftSide = zoomInPair.left();
		Sequence rightSide = zoomInPair.right();
		driver.perform(Arrays.asList(leftSide));
		driver.perform(Arrays.asList(rightSide));
	}

	public static void zoomOut(Point center, int distance) {
		Pair<Sequence, Sequence> zoomOutPair = zoom(center, 200 + distance, 200, 45, Duration.ofMillis(25));
		driver.perform(Arrays.asList(zoomOutPair.left()));
		driver.perform(Arrays.asList(zoomOutPair.right()));
	}

	private static Pair<Sequence, Sequence> zoom(Point center, int startRadius, int endRadius, int pinchAngle, Duration duration) {
		// convert degree angle into radians. 0/360 is top (12 O'clock).
		double angle = Math.PI / 2 - (2 * Math.PI / 360 * pinchAngle);

		// create the gesture for one finger
		Sequence fingerAPath = zoomSinglefinger("fingerA", center, startRadius, endRadius, angle, duration);

		// flip the angle around to the other side of the locus and get the gesture for the second finger
		angle = angle + Math.PI;
		Sequence fingerBPath = zoomSinglefinger("fingerB", center, startRadius, endRadius, angle, duration);

		return (new Pair<>(fingerAPath,fingerBPath));
	}

	private static Sequence zoomSinglefinger(String fingerName, Point locus, int startRadius, int endRadius, double angle, Duration duration) {
		PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, fingerName);
		Sequence fingerPath = new Sequence(finger, 0);

		double midpointRadius = startRadius + (endRadius > startRadius ? 1 : -1) * 20;

		// find coordinates for starting point of action (converting from polar coordinates to cartesian)
		int fingerStartx = (int)Math.floor(locus.x + startRadius * Math.cos(angle));
		int fingerStarty = (int)Math.floor(locus.y - startRadius * Math.sin(angle));

		// find coordinates for first point that pingers move quickly to
		int fingerMidx = (int)Math.floor(locus.x + (midpointRadius * Math.cos(angle)));
		int fingerMidy = (int)Math.floor(locus.y - (midpointRadius * Math.sin(angle)));

		// find coordinates for ending point of action (converting from polar coordinates to cartesian)
		int fingerEndx = (int)Math.floor(locus.x + endRadius * Math.cos(angle));
		int fingerEndy = (int)Math.floor(locus.y - endRadius * Math.sin(angle));

		// move finger into start position
		fingerPath.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), fingerStartx, fingerStarty));
		// finger comes down into contact with screen
		fingerPath.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
		// finger moves a small amount very quickly
		fingerPath.addAction(finger.createPointerMove(Duration.ofMillis(1), PointerInput.Origin.viewport(), fingerMidx, fingerMidy));
		// pause for a little bit
		fingerPath.addAction(new Pause(finger, Duration.ofMillis(100)));
		// finger moves to end position
		fingerPath.addAction(finger.createPointerMove(duration, PointerInput.Origin.viewport(), fingerEndx, fingerEndy));
		// finger lets up, off the screen
		fingerPath.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

		return fingerPath;
	}
	 */

	//System actions:
	public static void changeOrientation() {
		ScreenOrientation orientation = driver.getOrientation();

		if (orientation.value().equals("portrait")) {
			System.out.println("CHANGING TO LANDSCAPE");
			driver.rotate(ScreenOrientation.LANDSCAPE);
		} else {
			System.out.println("CHANGING TO PORTRAIT");
			driver.rotate(ScreenOrientation.PORTRAIT);
		}
	}

	public static void generatePhoneCall() {
		String phoneNumber = "1234567890";
		driver.makeGsmCall(phoneNumber, GsmCallActions.CALL);

		try {
			TimeUnit.MILLISECONDS.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		driver.makeGsmCall(phoneNumber, GsmCallActions.CANCEL);
	}

	public static void generateText() {
		String phoneNumber = "1234567890";
		String textMessage = "Hallo Tester, Testar says hi!";
		driver.sendSMS(phoneNumber, textMessage);

		try {
			TimeUnit.MILLISECONDS.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static String getCurrentPackage() {
		String currentPackage = driver.getCurrentPackage();
		return currentPackage;
	}

	public static void pressKeyEvent(KeyEvent keyEvent){
		driver.pressKey(keyEvent);
	}

	// Utility Interactions
	public static void hideKeyboard(){
		driver.hideKeyboard();
	}

	public static void wakeUpKeyCode(){
		driver.pressKey(new KeyEvent(AndroidKey.WAKEUP));
	}

	public static void activateAppByBundleId(String bundleId){
		driver.activateApp(bundleId);
	}

	//TODO: Update from Appium 7.3.0 to 8.2.0
	/*public static List<Map<String, Object>> getAllSessionDetails(){
		return driver.getAllSessionDetails();
	}*/

	public static Set<String> getWindowHandles(){
		return driver.getWindowHandles();
	}

	public static String getTitleOfCurrentPage(){
		return driver.getTitle();
	}

	public static void runAppInBackground(Duration duration){
		driver.runAppInBackground(duration);
	}

	public static void pushFile(String remotePath, File file){
		try {
			driver.pushFile(remotePath, file);
		} catch (IOException e) {
			System.err.println("Exception: AndroidDriver pushFile request was not properly executed");
		}
	}

	public static String getActivity() {
		return driver.currentActivity();
	}

	public static String getScreenshotSpyMode(String stateID) throws IOException {
		String scrshotOutputFolder = "output" + File.separator + "android_spy_screenshots";
		String statePath = scrshotOutputFolder + File.separator + stateID + ".png";
		File srcFile = driver.getScreenshotAs(OutputType.FILE);
		FileUtils.copyFile(srcFile, new File(statePath));

		return statePath;
	}

	public static String getScreenshotState(State state) throws IOException {
		byte[] byteImage = driver.getScreenshotAs(OutputType.BYTES);
		InputStream is = new ByteArrayInputStream(byteImage);
		AWTCanvas canvas = AWTCanvas.fromInputStream(is);
		return ScreenshotSerialiser.saveStateshot(state.get(Tags.ConcreteID, "NoConcreteIdAvailable"), canvas);
	}

	public static String getScreenshotAction(State state, Action action) throws IOException {
		byte[] byteImage = driver.getScreenshotAs(OutputType.BYTES);
		InputStream is = new ByteArrayInputStream(byteImage);

		// Highlight the action on the screenshot:
		BufferedImage newBi = ImageIO.read(is);
		// get the Graphics context for this single BufferedImage object
		Graphics2D g = (Graphics2D) newBi.getGraphics();

		Widget widget = action.get(Tags.OriginWidget);

		Rect bounds = widget.get(AndroidTags.AndroidBounds);
		int xLocation = ((int)(bounds.x() + (bounds.width()/2.0))-1);
		int yLocation = ((int)(bounds.y() + (bounds.height()/2.0))-1);

		g.setColor(new java.awt.Color(255, 0, 0, 130));
		g.drawOval(xLocation, yLocation, 20, 20);
		g.setColor(new java.awt.Color(255, 0, 0, 130));
		g.fillOval(xLocation, yLocation, 20, 20);
		g.setColor(new java.awt.Color(255, 0, 0, 130));
		g.setStroke(new BasicStroke(6));
		g.drawRect((int)bounds.x(), (int)bounds.y(), (int)bounds.width(), (int)bounds.height());
		g.dispose();  // get rid of the Graphics context to save resources

		ByteArrayOutputStream os = new ByteArrayOutputStream();
		ImageIO.write(newBi, "png", os);
		InputStream is2 = new ByteArrayInputStream(os.toByteArray());

		AWTCanvas canvas = AWTCanvas.fromInputStream(is2);
		return ScreenshotSerialiser.saveActionshot(state.get(Tags.ConcreteID, "NoConcreteIdAvailable"), action.get(Tags.ConcreteID, "NoConcreteIdAvailable"), canvas);
	}

	public static AWTCanvas getScreenshotBinary(State state) throws IOException {
		byte[] byteImage = driver.getScreenshotAs(OutputType.BYTES);
		InputStream is = new ByteArrayInputStream(byteImage);
		return AWTCanvas.fromInputStream(is);
	}

	public static void terminateApp(String bundleId){
		driver.terminateApp(bundleId);
	}

	/**
	 * Obtain a Document representation of the Android loaded page DOM.
	 * 
	 * Information is about loaded page/application in the foreground,
	 * not about specific process or SUT.
	 * 
	 * @return Document with DOM representation
	 */
	public static Document getAndroidPageSource() {
		try {
			String appiumState = driver.getPageSource();
			return loadXML(appiumState);
		} catch (WebDriverException wde) {
			System.err.println("ERROR: Exception trying to obtain driver.getPageSource()");
		} catch (ParserConfigurationException | SAXException | IOException doce) {
			System.err.println("ERROR: Exception parsing Android Driver Page Source to XML Document");
		} catch (Exception e) {
			System.err.println("ERROR: Unknown Exception AppiumFramework getAndroidPageSource()");
			e.printStackTrace();
		}
		return null;
	}

	private static Document loadXML(String xml) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory fctr = DocumentBuilderFactory.newInstance();
		DocumentBuilder bldr = fctr.newDocumentBuilder();
		InputSource insrc = new InputSource(new StringReader(xml));
		return bldr.parse(insrc);
	}

	public static void uninstallApp(String appName) {
		System.out.println("Uninstalling app: " + appName);
		driver.removeApp(appName);

		try {
			TimeUnit.MILLISECONDS.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static LogEntries getAppiumLogs() {
		return driver.manage().logs().get("driver");
	}

	@Override
	public void stop() throws SystemStopException {
		driver.quit();
		driver = null;
	}

	@Override
	public boolean isRunning() {
		//TODO: Check and select proper method to verify if running
		try {
			// Need to know appId to use this.
			//driver.queryAppState(appId), equalTo(ApplicationState.RUNNING_IN_FOREGROUND)
			driver.getCurrentPackage();
		}
		catch (Exception e) {
			return false;
		}

		return true;
	}

	@Override
	public String getStatus() {
		//TODO: Check and select proper method to print the status
		return "Android current package : " + driver.getCurrentPackage();
	}

	public static ApplicationState getStatus(String appId) {
		return driver.queryAppState(appId);
	}

	@Override
	public AutomationCache getNativeAutomationCache() {
		return null;
	}

	@Override
	public void setNativeAutomationCache() {
	}

	public static List<SUT> fromAll() {
		if (androidSUT == null) {
			return new ArrayList<>();
		}

		return Collections.singletonList(androidSUT);
	}

	private static DesiredCapabilities createCapabilitiesFromJsonFile(String capabilitesJsonFile) {
		DesiredCapabilities cap = new DesiredCapabilities();

		try (FileReader reader = new FileReader(capabilitesJsonFile)) {
			JsonObject json = new JsonParser().parse(reader).getAsJsonObject();

			// https://appium.io/docs/en/2.0/guides/caps/
			cap.setCapability("platformName", getString(json, "platformName", "Android"));

			cap.setCapability("appium:deviceName", getString(json, "deviceName", "Android Emulator"));
			cap.setCapability("appium:automationName", getString(json, "automationName", "UiAutomator2"));
			cap.setCapability("appium:newCommandTimeout", getInt(json, "newCommandTimeout", 600));
			cap.setCapability("appium:autoGrantPermissions", getBool(json, "autoGrantPermissions", false));

			// If the APK is already installed we use appPackage identifier
			if (getBool(json, "isApkInstalled", false)) {
				String appPackage = getString(json, "appPackage", null);
				String appActivity = getString(json, "appActivity", null);

				if (appPackage == null || appPackage.isEmpty()) {
					throw new IllegalArgumentException("When isApkInstalled=true, 'appPackage' is required.");
				}
				if (appActivity == null || appActivity.isEmpty()) {
					throw new IllegalArgumentException(String.join("\n",
							"When isApkInstalled=true, 'appActivity' is required (multiple launcher activities can exist).",
							"",
							"How to find it on Windows:",
							"1) Manually open the app on the emulator.",
							"2) Run:",
							"   adb shell dumpsys activity activities | findstr /R /C:\"ResumedActivity\" /C:\"topResumedActivity\"",
							"3) From the output, take the activity after the package name."
							));
				}

				cap.setCapability("appium:appPackage", appPackage);
				cap.setCapability("appium:appActivity", appActivity);
			} 
			// Else we need to install the APK
			else {
				String appPath = getString(json, "app", null);
				if (appPath == null || appPath.isEmpty()) {
					throw new IllegalArgumentException("When isApkInstalled=false, 'app' (APK path or URL) must be provided.");
				}

				boolean isEmulatorDocker = getBool(json, "isEmulatorDocker", false);
				String ipAddressAppium = getString(json, "ipAddressAppium", null);

				// If emulator is running inside a docker use the APK raw URL
				if (isEmulatorDocker && ipAddressAppium != null && !ipAddressAppium.isEmpty()) {
					// Docker container (budtmo/docker-android) + Appium v2 do not use /wd/hub suffix anymore
					// It can be enabled using the APPIUM_ADDITIONAL_ARGS "--base-path /wd/hub" command
					cap.setCapability("appium:app", appPath);
					androidAppiumURL = "http://" + ipAddressAppium + ":4723/wd/hub";
				} 
				// Else, obtain the local directory that contains the APK file
				else {
					cap.setCapability("appium:app", new File(appPath).getCanonicalPath());
				}
			}

		} catch (IOException | NullPointerException e) {
			System.err.println("ERROR: Exception reading Appium Desired Capabilities from JSON file: " + capabilitesJsonFile);
			e.printStackTrace();
		}

		return cap;
	}

	private static String getString(JsonObject json, String key, String def) {
		return json.has(key) && !json.get(key).isJsonNull() ? json.get(key).getAsString() : def;
	}
	private static boolean getBool(JsonObject json, String key, boolean def) {
		return json.has(key) && !json.get(key).isJsonNull() ? json.get(key).getAsBoolean() : def;
	}
	private static int getInt(JsonObject json, String key, int def) {
		return json.has(key) && !json.get(key).isJsonNull() ? json.get(key).getAsInt() : def;
	}

}
