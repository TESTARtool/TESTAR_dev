/***************************************************************************************************
 *
 * Copyright (c) 2020 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2020 Open Universiteit - www.ou.nl
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

package org.testar.android;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.fruit.alayer.AutomationCache;
import org.fruit.alayer.SUT;
import org.fruit.alayer.SUTBase;
import org.fruit.alayer.exceptions.SystemStopException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;

public class AppiumFramework extends SUTBase {

	public static AppiumFramework androidSUT = null;

	private static AndroidDriver<MobileElement> driver = null;

	public AppiumFramework(DesiredCapabilities cap) {

		try {
			driver = new AndroidDriver<>(new URL("http://127.0.0.1:4723/wd/hub"), cap);
		} catch (MalformedURLException e) {
			System.out.println("ERROR: Exception with Android Driver URL: http://127.0.0.1:4723/wd/hub");
			e.printStackTrace();
		}
	}

	public static AppiumFramework fromCapabilities(String capabilitesJsonFile) {
		if (androidSUT != null) {
			androidSUT.stop();
		}

		DesiredCapabilities cap = createCapabilitiesFromJsonFile(capabilitesJsonFile);

		return new AppiumFramework(cap);
	}

	public static List<MobileElement> findElements(By by){
		return driver.findElements(by);
	}
	
	public static void clickElementById(String id){
		driver.findElementById(id).click();
	}

	public static Document getAndroidPageSource() {
		try {
			return loadXML(driver.getPageSource());
		} catch (WebDriverException wde) {
			System.out.println("ERROR: Exception trying to obtain driver.getPageSource()");
		} catch (ParserConfigurationException | SAXException | IOException doce) {
			System.out.println("ERROR: Exception parsing Android Driver Page Source to XML Document");
		} catch (Exception e) {
			System.out.println("ERROR: Unknown Exception AppiumFramework getAndroidPageSource()");
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

	@Override
	public void stop() throws SystemStopException {
		driver.closeApp();
		driver = null;
	}

	@Override
	public boolean isRunning() {
		//TODO: Check and select proper method to verify if running
		try {
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

			JsonObject jsonObject = new JsonParser().parse(reader).getAsJsonObject();

			cap.setCapability("deviceName", jsonObject.get("deviceName").getAsString());
			cap.setCapability("platformName", jsonObject.get("platformName").getAsString());

			String appPath = jsonObject.get("app").getAsString();

			cap.setCapability("app", new File(appPath).getCanonicalPath());

		} catch (IOException | NullPointerException e) {
			System.out.println("ERROR: Exception reading Appium Desired Capabilities from JSON file: " + capabilitesJsonFile);
			e.printStackTrace();
		}

		return cap;
	}

}
