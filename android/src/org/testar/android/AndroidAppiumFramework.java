/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2020-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2020-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.android;

import com.google.common.collect.ImmutableMap;
import org.testar.core.serialisation.ScreenshotSerialiser;
import org.testar.core.state.SUT;
import org.testar.core.state.SUTBase;
import org.testar.core.state.State;
import org.testar.core.state.Widget;
import org.testar.core.tag.Tags;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.GsmCallActions;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import io.appium.java_client.appmanagement.ApplicationState;
import io.appium.java_client.touch.LongPressOptions;
import io.appium.java_client.touch.offset.ElementOption;

import org.testar.android.tag.AndroidTags;
import org.testar.core.action.Action;
import org.testar.core.alayer.*;
import org.testar.core.exceptions.SystemStopException;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.remote.DesiredCapabilities;
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
	private static volatile boolean driverUnresponsive = false;

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

		AndroidCapabilitiesFactory factory = new AndroidCapabilitiesFactory(androidAppiumURL);

		AndroidCapabilitiesFactory.Result result = factory.fromJsonFile(capabilitesJsonFile);

		androidAppiumURL = result.getAppiumServerUrl();

		return new AndroidAppiumFramework(result.getCapabilities());
	}

	public static AndroidDriver getDriver() {
		return driver;
	}

	public static void markDriverUnresponsive(Throwable t) {
		driverUnresponsive = true;
		if (t != null) {
			if(t.getMessage() != null) {
				System.err.println("AndroidAppiumFramework: Android driver is unresponsive: " + t.getClass().getSimpleName() + " - " + t.getMessage());
			} else {
				System.err.println("AndroidAppiumFramework: Android driver is unresponsive");
				t.printStackTrace();
			}
		}
	}

	public static boolean isDriverUnresponsive() {
		return driverUnresponsive;
	}

	public static void resetDriverUnresponsive() {
		driverUnresponsive = false;
	}

	public static List<WebElement> findElements(By by){
		return driver.findElements(by);
	}

	/**
	 * Obtain the widget associated with the (Android) web element.
	 * Uses unique accessibility ID if present and unique, otherwise uses xpath.
	 * 
	 * @param id
	 * @param w
	 * @return android web element
	 */
	public static WebElement resolveElementByIdOrXPath(String id, Widget w) {
		if (id != null && !id.isEmpty()) {
			// Try by accessibility id only if non-null and non-empty
			List<WebElement> elements = driver.findElements(new AppiumBy.ByAccessibilityId(id));

			// Use the ID only if exactly one element is found
			if (elements.size() == 1) {
				return elements.get(0);
			}
		}

		// Fallback using XPath: ID is empty or did not resolve to exactly one element
		return AndroidAppiumFramework.resolveElementByXPath(w);
	}

	/**
	 * Obtain the widget associated with the (Android) web element.
	 * Uses the xpath.
	 * 
	 * @param w
	 * @return android web element
	 */
	public static WebElement resolveElementByXPath(Widget w) {
		String xpathString = w.get(AndroidTags.AndroidXpath);
		return driver.findElement(new By.ByXPath(xpathString));
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
		try {
			return driver.getCurrentPackage();
		} catch (WebDriverException wde) {
			markDriverUnresponsive(wde);
			return "";
		}
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
		try {
			return driver.currentActivity();
		} catch (WebDriverException wde) {
			markDriverUnresponsive(wde);
			return "";
		}
	}

	public static String getScreenshotSpyMode(String stateID) throws IOException {
		String scrshotOutputFolder = "output" + File.separator + "android_spy_screenshots";
		String statePath = scrshotOutputFolder + File.separator + stateID + ".png";
		File srcFile = driver.getScreenshotAs(OutputType.FILE);
		FileUtils.copyFile(srcFile, new File(statePath));

		return statePath;
	}

	public static String getScreenshotState(State state) throws IOException {
		try {
			byte[] byteImage = driver.getScreenshotAs(OutputType.BYTES);
			InputStream is = new ByteArrayInputStream(byteImage);
			AWTCanvas canvas = AWTCanvas.fromInputStream(is);
			return ScreenshotSerialiser.saveStateshot(state.get(Tags.ConcreteID, "NoConcreteIdAvailable"), canvas);
		} catch (WebDriverException wde) {
			markDriverUnresponsive(wde);
			throw new IOException("Exception: AndroidDriver getScreenshotState failed", wde);
		}
	}

	public static String getScreenshotAction(State state, Action action) throws IOException {
		byte[] byteImage;
		InputStream is;
		try {
			byteImage = driver.getScreenshotAs(OutputType.BYTES);
			is = new ByteArrayInputStream(byteImage);
		} catch (WebDriverException wde) {
			markDriverUnresponsive(wde);
			throw new IOException("Exception: AndroidDriver getScreenshotAction failed", wde);
		}

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
		try {
			byte[] byteImage = driver.getScreenshotAs(OutputType.BYTES);
			InputStream is = new ByteArrayInputStream(byteImage);
			return AWTCanvas.fromInputStream(is);
		} catch (WebDriverException wde) {
			markDriverUnresponsive(wde);
			throw new IOException("Exception: AndroidDriver getScreenshotBinary failed", wde);
		}
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

	/**
	 * Clear all logcat content. 
	 */
	public static void clearLogcat() {
		try {
			mobileShell("logcat", List.of("-b", "all", "-c"), Duration.ofSeconds(10));
		} catch (Exception ignored) {}
	}

	/**
	 * Execute an Android shell command on the device via Appium ("mobile: shell"). 
	 */
	private static void mobileShell(String command, List<String> args, Duration timeout) {
		Map<String, Object> m = new HashMap<>();
		m.put("command", command);
		m.put("args", args);
		m.put("timeout", (int) timeout.toMillis());
		driver.executeScript("mobile: shell", m);
	}

	/**
	 * Dump logcat output (main + system + crash buffers). 
	 */
	public static String dumpLogcatThreadtimeForPackage(String pkg) {
		if (pkg == null || pkg.isBlank()) return "";

		// 1) get PID
		String pid = mobileShellStdout("pidof", List.of("-s", pkg), Duration.ofSeconds(5)).trim();
		if (pid.isEmpty()) return "";

		// 2) dump logcat for that PID
		return mobileShellStdout(
			"logcat",
			List.of("-d", "-v", "threadtime", "--pid", pid, "-b", "main", "-b", "system", "-b", "crash"),
			Duration.ofSeconds(10)
		);
	}

	/**
	 * Execute an Android shell command on the device via Appium ("mobile: shell") and return stdout. 
	 */
	private static String mobileShellStdout(String command, List<String> args, Duration timeout) {
		Map<String, Object> m = new HashMap<>();
		m.put("command", command);
		m.put("args", args);
		m.put("timeout", (int) timeout.toMillis());

		Object raw;
		try {
			raw = driver.executeScript("mobile: shell", m);
		} catch (WebDriverException wde) {
			markDriverUnresponsive(wde);
			return "";
		}
		if (raw == null) return "";
		if (raw instanceof String) return (String)raw;
		if (raw instanceof Map<?, ?>) {
			Object out = ((Map<?, ?>)raw).get("stdout");
			return out == null ? "" : out.toString();
		}
		return raw.toString();
	}

	public static String getAppPackageFromCapabilitiesOrCurrent() {
		if (driver == null) {
			return "";
		}

		try {
			Object appPackage = driver.getCapabilities().getCapability("appium:appPackage");
			if (appPackage == null) {
				appPackage = driver.getCapabilities().getCapability("appPackage");
			}
			if (appPackage != null) {
				String pkg = appPackage.toString().trim();
				if (!pkg.isEmpty()) {
					return pkg;
				}
			}
		} catch (Exception ignored) {
		}

		try {
			return driver.getCurrentPackage();
		} catch (WebDriverException wde) {
			markDriverUnresponsive(wde);
			return "";
		} catch (Exception ignored) {
			return "";
		}
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
		catch (WebDriverException wde) {
			markDriverUnresponsive(wde);
			return false;
		}
		catch (Exception e) {
			return false;
		}

		return true;
	}

	@Override
	public String getStatus() {
		//TODO: Check and select proper method to print the status
		try {
			return "Android current package : " + driver.getCurrentPackage();
		} catch (WebDriverException wde) {
			markDriverUnresponsive(wde);
			return "Android current package : <unavailable>";
		}
	}

	public static ApplicationState getStatus(String appId) {
		try {
			return driver.queryAppState(appId);
		} catch (WebDriverException wde) {
			markDriverUnresponsive(wde);
			return ApplicationState.NOT_RUNNING;
		}
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

}
