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
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testar.webdriver.state.WdDriver;

import io.github.bonigarcia.wdm.WebDriverManager;

public class WdEdgeManager implements WdBrowserManager {

	protected static final Logger logger = LogManager.getLogger();

	@Override
	public String resolveBinary(String edgePathCandidate) {
		File file = new File(edgePathCandidate);
		if (file.exists() && file.getName().toLowerCase().contains("msedge")) {
			logger.log(Level.INFO, String.format("User indicated the Edge browser path: %s", edgePathCandidate));
			return file.getAbsolutePath();
		}
		throw new RuntimeException("Invalid Edge browser path: " + edgePathCandidate);
	}

	@Override
	public RemoteWebDriver createWebDriver(String edgeBinaryPath, String extensionPath) {
		String edgeVersion = getEdgeFullVersion(edgeBinaryPath);
		if(!edgeVersion.isEmpty()) {
			logger.log(Level.INFO, String.format("Detected Edge Version: %s", edgeVersion));
			WebDriverManager.edgedriver().driverVersion(edgeVersion).setup();
		}
		else {
			logger.log(Level.WARN, "Edge version not detected, the default WebDriverManager driver version will be used");
			WebDriverManager.edgedriver().setup();
		}

		EdgeOptions options = new EdgeOptions();
		options.setBinary(edgeBinaryPath);
		options.addArguments("--load-extension=" + extensionPath);
		options.addArguments("disable-infobars");

		options.addArguments("--remote-allow-origins=*");

		options.addArguments("--disable-search-engine-choice-screen");

		if (WdDriver.fullScreen) {
			options.addArguments("--start-maximized");
		}
		if (WdDriver.disableSecurity) {
			options.addArguments("ignore-certificate-errors");
			options.addArguments("--disable-web-security");
			options.addArguments("--allow-running-insecure-content");
		}

		Map<String, Object> prefs = new HashMap<>();
		prefs.put("profile.default_content_setting_values.notifications", 1);
		options.setExperimentalOption("prefs", prefs);

		return new EdgeDriver(options);
	}

	private String getEdgeFullVersion(String edgePath) {
		File edgeDir = new File(edgePath).getParentFile();
		File[] files = edgeDir.listFiles();

		if (files != null) {
			for (File file : files) {
				String name = file.getName();
				if (file.isDirectory() && name.matches("\\d+\\.\\d+\\.\\d+\\.\\d+")) {
					// Example: "136.0.3240.92" directory name
					return name;
				}
			}
		}

		// If no directory version found, fall back to use a WMIC command
		try {
			String escapedPath = edgePath.replace("\\", "\\\\");
			String wmicQuery = "name='" + escapedPath + "'";

			ProcessBuilder builder = new ProcessBuilder(
					"wmic", "datafile", "where",
					"\"" + wmicQuery + "\"",
					"get", "Version", "/value"
					);

			Process process = builder.start();

			try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
				String line;
				while ((line = reader.readLine()) != null) {
					line = line.trim();
					if (line.startsWith("Version=")) {
						return line.replace("Version=", "").trim();
					}
				}
			}
		} catch (IOException e) {
			logger.log(Level.WARN, "Failed to get Edge version from WMIC", e);
		}

		return "";
	}

}
