/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2025-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2025-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.webdriver.manager;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testar.webdriver.state.WdDriver;

import io.github.bonigarcia.wdm.WebDriverManager;

public class WdChromeManager implements WdBrowserManager {

	protected static final Logger logger = LogManager.getLogger();

	@Override
	public String resolveBinary(String chromePathCandidate) {
		File file = new File(chromePathCandidate);
		if (file.exists() && file.getName().endsWith(".exe")) {
			logger.log(Level.INFO, String.format("User indicated Chrome for testing path: %s", chromePathCandidate));
			return file.getAbsolutePath();
		} else {
			if(!chromePathCandidate.isEmpty()) logger.log(Level.WARN, String.format("Invalid Chrome for testing path: %s", chromePathCandidate));
			logger.log(Level.INFO, "Downloading Chrome for testing...");
			return ChromeDownloader.downloadChromeForTesting();
		}
	}

	@Override
	public RemoteWebDriver createWebDriver(String chromeForTestingPath, String extensionPath) {
		String chromeVersion = getChromeMajorVersion(chromeForTestingPath);
		if(!chromeVersion.isEmpty()) {
			logger.log(Level.INFO, String.format("Detected Chrome Version: %s", chromeVersion));
			WebDriverManager.chromedriver().driverVersion(chromeVersion).setup();
		}
		else {
			logger.log(Level.WARN, "Chrome version not detected, the default WebDriverManager driver version will be used");
			WebDriverManager.chromedriver().setup();
		}

		ChromeOptions options = new ChromeOptions();
		options.setBinary(chromeForTestingPath);

		options.addArguments("--load-extension=" + extensionPath);

		options.addArguments("disable-infobars");

		// Workaround to fix https://github.com/SeleniumHQ/selenium/issues/11750
		options.addArguments("--remote-allow-origins=*");

		// Disable search engine selector
		options.addArguments("--disable-search-engine-choice-screen");  

		if(WdDriver.fullScreen) {
			options.addArguments("--start-maximized");
		}
		if(WdDriver.disableSecurity) {
			options.addArguments("ignore-certificate-errors");
			options.addArguments("--disable-web-security");
			options.addArguments("--allow-running-insecure-content");
		}
		if(WdDriver.remoteDebugging) {
			options.addArguments("--remote-debugging-port=9222");
		}
		if(WdDriver.disableGPU) {
			options.addArguments("--disable-gpu");
		}

		Map<String, Object> prefs = new HashMap<>();
		prefs.put("profile.default_content_setting_values.notifications", 1);
		prefs.put("profile.password_manager_leak_detection", false);
		options.setExperimentalOption("prefs", prefs);

		return new ChromeDriver(options);
	}

	private String getChromeMajorVersion(String chromePath) {
		File chromeDir = new File(chromePath).getParentFile();
		File[] files = chromeDir.listFiles();

		if (files != null) {
			for (File file : files) {
				String name = file.getName();
				if (name.matches("\\d+\\.\\d+\\.\\d+\\.\\d+\\.manifest")) {
					// Example: "137.0.7151.40.manifest" to "137"
					return name.split("\\.")[0];
				}
			}
		}

		// If no .manifest file found, fall back to using --version
		try {
			Process process = new ProcessBuilder(chromePath, "--version").start();
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
				// Example: "Google Chrome 137.0.7151.40.manifest" to "137"
				String line = reader.readLine();
				if (line != null && line.matches(".*\\d+\\.\\d+\\.\\d+\\.\\d+.*")) {
					String version = line.replaceAll("[^\\d.]", "");
					return version.split("\\.")[0];
				}
			}
		} catch (IOException e) {
			logger.log(Level.WARN, "Failed to get Chrome version from binary", e);
		}

		return "";
	}

}
