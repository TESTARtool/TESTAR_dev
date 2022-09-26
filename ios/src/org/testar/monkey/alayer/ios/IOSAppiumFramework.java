/***************************************************************************************************
 *
 * Copyright (c) 2020 - 2022 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2020 - 2022 Open Universiteit - www.ou.nl
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

package org.testar.monkey.alayer.ios;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.testar.serialisation.ScreenshotSerialiser;
import io.appium.java_client.ios.IOSDriver;
import org.apache.commons.io.FileUtils;
import org.testar.monkey.Pair;
import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.exceptions.SystemStopException;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testar.monkey.alayer.ios.enums.IOSTags;
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

public class IOSAppiumFramework extends SUTBase {

	public static IOSAppiumFramework iosSUT = null;

	private static IOSDriver driver = null;

	public IOSAppiumFramework(DesiredCapabilities cap) {

		try {
			driver = new IOSDriver(new URL("http://0.0.0.0:4723/wd/hub"), cap);
		} catch (MalformedURLException e) {
			System.err.println("ERROR: Exception with IOS Driver URL: http://0.0.0.0:4723/wd/hub");
			e.printStackTrace();
		}
	}

	public static IOSAppiumFramework fromCapabilities(String capabilitesJsonFile) {
		if (iosSUT != null) {
			iosSUT.stop();
		}

		DesiredCapabilities cap = createCapabilitiesFromJsonFile(capabilitesJsonFile);

		return new IOSAppiumFramework(cap);
	}

	public static IOSDriver getDriver() {
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
			driver.findElement(new By.ById(id)).click();
		}
		else {
			String xpathString = w.get(IOSTags.iosXpath);
			driver.findElement(new By.ByXPath(xpathString)).click();
		}
	}

	/**
	 * Send Type Action. 
	 * Uses unique accessibility ID if present, otherwise uses xpath. 
	 * 
	 * @param id
	 * @param value
	 * @param w
	 */
	public static void sendKeysTextTextElementById(String id, String text, Widget w){
		if (!id.equals("")) {
			WebElement element = driver.findElement(new By.ById(id));
			element.clear();
			element.sendKeys(text);
		}
		else {
			String xpathString = w.get(IOSTags.iosXpath);
			WebElement element = driver.findElement(new By.ByXPath(xpathString));
			element.clear();
			element.sendKeys(text);
		}
	}

	public static void scrollElementById(String id, Widget w, int scrollDistance) {
		Duration NO_TIME = Duration.ofMillis(0);
		Duration STEP_DURATION = Duration.ofMillis(20);

		PointerInput.Origin VIEW = PointerInput.Origin.viewport();
		Rect bounds = w.get(IOSTags.iosBounds);

		int startCoorsX = (int) (bounds.x() + 0.5*bounds.width());
		int startCoorsY = (int) (bounds.y() + 0.5*bounds.height());

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
	}

	public static void shakeDevice() {
		driver.shake();
	}

	public static void clickBackButton() {
		driver.navigate().back();
	}

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

	// TODO: Complete for IOSDriver, KeyEvent seems android specific?
	/*public static void pressKeyEvent(KeyEvent keyEvent){
		driver.pressKey(keyEvent);
	}*/

	// Utility Interactions
	public static void hideKeyboard(){
		driver.hideKeyboard();
	}

	// TODO: Complete for IOSDriver, KeyEvent seems android specific?
	/*public static void wakeUpKeyCode(){
		driver.pressKey(new KeyEvent(AndroidKey.WAKEUP));
	}*/

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

	public static void resetApp(){
		driver.resetApp();
	}

	public static void runAppInBackground(Duration duration){
		driver.runAppInBackground(duration);
	}

	public static void pushFile(String remotePath, File file){
		try {
			driver.pushFile(remotePath, file);
		} catch (IOException e) {
			System.err.println("Exception: IOSDriver pushFile request was not properly executed");
		}
	}

	public static Pair<Integer, Integer> getScreenSize() {
		Dimension screenSize = driver.manage().window().getSize();
		Pair<Integer, Integer> widthHeight = new Pair(screenSize.width, screenSize.height);
		return widthHeight;
	}

	public static String getScreenshot(String stateID) throws IOException {
		String scrshotOutputFolder = "screenshot_folder";
		String testSequenceFolder = "IOSScreenshots";
		String statePath = scrshotOutputFolder + File.separator + testSequenceFolder + File.separator + stateID + ".png";
		File srcFile = driver.getScreenshotAs(OutputType.FILE);
		FileUtils.copyFile(srcFile, new File(statePath));

		return statePath;
	}

	public static String getScreenshotSpyMode(String stateID) throws IOException {
		String scrshotOutputFolder = "screenshot_folder";
		String testSequenceFolder = "IOSScreenshots";
		String statePath = scrshotOutputFolder + File.separator + testSequenceFolder + File.separator + stateID + ".png";
		File srcFile = driver.getScreenshotAs(OutputType.FILE);
		FileUtils.copyFile(srcFile, new File(statePath));

		return statePath;
	}

	public static String getScreenshotState(State state) throws IOException {
		byte[] byteImage = driver.getScreenshotAs(OutputType.BYTES);
		InputStream is = new ByteArrayInputStream(byteImage);
		AWTCanvas canvas = AWTCanvas.fromInputStream(is);
		return ScreenshotSerialiser.saveStateshot(state.get(Tags.ConcreteIDCustom, "NoConcreteIdAvailable"), canvas);
	}

	public static String getScreenshotAction(State state, Action action) throws IOException {
		byte[] byteImage = driver.getScreenshotAs(OutputType.BYTES);
		InputStream is = new ByteArrayInputStream(byteImage);

		// Highligh the action on the screenshot:
		BufferedImage newBi = ImageIO.read(is);

		int width = newBi.getWidth();
		int height = newBi.getHeight();

		// get the Graphics context for this single BufferedImage object
		Graphics2D g = (Graphics2D) newBi.getGraphics();

		Pair<Integer, Integer> screenSize = IOSAppiumFramework.getScreenSize();
		int screenWidth = screenSize.left();
		int screenHeight = screenSize.right();

		Widget widget = action.get(Tags.OriginWidget);

		Rect bounds = widget.get(IOSTags.iosBounds);
		int dotWidth = (int)(((((double)width)/screenWidth) * widget.get(IOSTags.iosWidth))/2.0);
		int dotHeight = (int)(((((double)height)/screenHeight) * widget.get(IOSTags.iosHeight))/2.0);
		int xLocationDot = (int)((((double)width)/screenWidth)*widget.get(IOSTags.iosX) + dotWidth);
		int yLocationDot = (int)((((double)height)/screenHeight)*widget.get(IOSTags.iosY) + dotHeight);

		//int xLocation = ((int)(widget.get(IOSTags.iosX) + (widget.get(IOSTags.iosWidth)/2.0))-1);
		//int yLocation = ((int)(widget.get(IOSTags.iosY) + (widget.get(IOSTags.iosHeight)/2.0))-1);

		int xLocationBox = (int)((((double)width)/screenWidth)*widget.get(IOSTags.iosX));
		int yLocationBox = (int)((((double)height)/screenHeight)*widget.get(IOSTags.iosY));
		int boxWidth = (int)(((double)width/screenWidth) * widget.get(IOSTags.iosWidth));
		int boxHeight = (int)(((double)height/screenHeight) * widget.get(IOSTags.iosHeight));

		g.setColor(new java.awt.Color(255, 0, 0, 130));
		g.drawOval(xLocationDot, yLocationDot, 20, 20);
		g.setColor(new java.awt.Color(255, 0, 0, 130));
		g.fillOval(xLocationDot, yLocationDot, 20, 20);
		g.setColor(new java.awt.Color(255, 0, 0, 130));
		g.setStroke(new BasicStroke(6));
		g.drawRect(xLocationBox, yLocationBox, boxWidth, boxHeight);
		g.dispose();  // get rid of the Graphics context to save resources

		ByteArrayOutputStream os = new ByteArrayOutputStream();
		ImageIO.write(newBi, "png", os);
		InputStream is2 = new ByteArrayInputStream(os.toByteArray());

		AWTCanvas canvas = AWTCanvas.fromInputStream(is2);
		return ScreenshotSerialiser.saveActionshot(state.get(Tags.ConcreteIDCustom, "NoConcreteIdAvailable"), action.get(Tags.ConcreteIDCustom, "NoConcreteIdAvailable"), canvas);
	}

	// Note that besides obtaining a screenshot of the SUT it also highlights which action was clicked!
	public static AWTCanvas getScreenshotBinary(State state, Widget widget) throws IOException {
		byte[] byteImage = driver.getScreenshotAs(OutputType.BYTES);
		InputStream is = new ByteArrayInputStream(byteImage);
		return AWTCanvas.fromInputStream(is);
	}

	public static String getAppName() {
		// getSessionDetails() does not exist anymore for Appium 8.X
		//TODO: Debug and validate the capability to replace session details

		//Map<String, Object> sessionDetails = driver.getSessionDetails();
		//String tempApp = (String) sessionDetails.get("app");
		//int indexSlash = tempApp.lastIndexOf("/");
		//tempApp = tempApp.substring(indexSlash + 1);
		//int indexDot = tempApp.indexOf(".");
		//tempApp = tempApp.substring(0 , indexDot);

		String tempApp = (String) driver.getCapabilities().getCapability("appPackage");
		int indexDot = tempApp.indexOf(".");
		tempApp = tempApp.substring(0 , indexDot);

		return tempApp;
	}

	public static void terminateApp(String bundleId){
		driver.terminateApp(bundleId);
	}

	/**
	 * Obtain a Document representation of the IOS loaded page DOM.
	 * 
	 * Information is about loaded page/application in the foreground,
	 * not about specific process or SUT.
	 * 
	 * @return Document with DOM representation
	 */
	public static Document getIOSPageSource() {
		try {
			String pageSource = driver.getPageSource();
			return loadXML(pageSource);
		} catch (WebDriverException wde) {
			System.err.println("ERROR: Exception trying to obtain driver.getPageSource()");
		} catch (ParserConfigurationException | SAXException | IOException doce) {
			System.err.println("ERROR: Exception parsing IOS Driver Page Source to XML Document");
		} catch (Exception e) {
			System.err.println("ERROR: Unknown Exception AppiumFramework getIOSPageSource()");
			e.printStackTrace();
		}
		return null;
	}

	public static boolean widgetVisible(Widget w) {
		Pair<Integer, Integer> widthHeight = getScreenSize();

		int height = widthHeight.right();
		int y = w.get(IOSTags.iosY);

		return y >= 0 && y <= height;
	}

	private static Document loadXML(String xml) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory fctr = DocumentBuilderFactory.newInstance();
		DocumentBuilder bldr = fctr.newDocumentBuilder();
		InputSource insrc = new InputSource(new StringReader(xml));
		return bldr.parse(insrc);
	}

	@Override
	public void stop() throws SystemStopException {
		driver.closeApp();
		driver = null;
	}

	@Override
	public boolean isRunning() {
		//TODO: Check and select proper method to verify if running
		try {
			driver.getPageSource();
		}
		catch (Exception e) {
			return false;
		}

		return true;
	}

	@Override
	public String getStatus() {
		//TODO: Check and select proper method to print the status
		return "IOS Page Source : " + driver.getPageSource();
	}

	@Override
	public AutomationCache getNativeAutomationCache() {
		return null;
	}

	@Override
	public void setNativeAutomationCache() {
	}

	public static List<SUT> fromAll() {
		if (iosSUT == null) {
			return new ArrayList<>();
		}

		return Collections.singletonList(iosSUT);
	}

	private static DesiredCapabilities createCapabilitiesFromJsonFile(String capabilitesJsonFile) {
		DesiredCapabilities cap = new DesiredCapabilities();

		try (FileReader reader = new FileReader(capabilitesJsonFile)) {

			JsonObject jsonObject = new JsonParser().parse(reader).getAsJsonObject();

			cap.setCapability("deviceName", jsonObject.get("deviceName").getAsString());
			cap.setCapability("platformName", jsonObject.get("platformName").getAsString());
			cap.setCapability("automationName", jsonObject.get("automationName").getAsString());

			String appPath = jsonObject.get("app").getAsString();

			//TODO modify this to a more generic app path!!!
			String appLocation = appPath;
			cap.setCapability("app", appLocation);

		} catch (IOException | NullPointerException e) {
			System.err.println("ERROR: Exception reading Appium Desired Capabilities from JSON file: " + capabilitesJsonFile);
			e.printStackTrace();
		}

		return cap;
	}

}
