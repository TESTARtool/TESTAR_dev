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
import java.nio.file.Paths;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testar.webdriver.state.WdDriver;

import io.github.bonigarcia.wdm.WebDriverManager;

public class WdFirefoxManager implements WdBrowserManager {

	protected static final Logger logger = LogManager.getLogger();

	@Override
	public String resolveBinary(String firefoxPathCandidate) {
		File file = new File(firefoxPathCandidate);
		if (file.exists() && file.getName().toLowerCase().contains("firefox")) {
			logger.log(Level.INFO, String.format("User indicated the Firefox browser path: %s", firefoxPathCandidate));
			return file.getAbsolutePath();
		}
		throw new RuntimeException("Invalid Firefox browser path: " + firefoxPathCandidate);
	}

	@Override
	public RemoteWebDriver createWebDriver(String firefoxBinaryPath, String extensionPath) {
		String firefoxVersion = getFirefoxMajorVersion(firefoxBinaryPath);
		if(!firefoxVersion.isEmpty()) {
			logger.log(Level.INFO, String.format("Detected Firefox Version: %s", firefoxVersion));
			WebDriverManager.firefoxdriver().driverVersion(firefoxVersion).setup();
		}
		else {
			logger.log(Level.WARN, "Firefox version not detected, the default WebDriverManager driver version will be used");
			WebDriverManager.firefoxdriver().setup();
		}

		FirefoxProfile profile = new FirefoxProfile();
		profile.setPreference("xpinstall.signatures.required", false);
		profile.setPreference("dom.webnotifications.enabled", true);

		FirefoxOptions options = new FirefoxOptions();
		options.setProfile(profile);

		if (firefoxBinaryPath != null && !firefoxBinaryPath.isEmpty()) {
			options.setBinary(firefoxBinaryPath);
		}

		if (WdDriver.fullScreen) {
			options.addArguments("--start-maximized");
		}

		if (WdDriver.disableSecurity) {
			options.addPreference("webdriver_accept_untrusted_certs", true);
			options.addPreference("security.insecure_field_warning.contextual.enabled", false);
		}

		FirefoxDriver firefoxDriver = new FirefoxDriver(options);

		// Firefox extension must be .xpi file
		String firefoxExtensionPath = extensionPath + ".xpi";
		if (firefoxExtensionPath != null && !firefoxExtensionPath.isEmpty()) {
			File extensionFile = new File(firefoxExtensionPath);
			if (extensionFile.exists() && extensionFile.getName().endsWith(".xpi")) {
				firefoxDriver.installExtension(Paths.get(extensionFile.getAbsolutePath()), true);
			} else {
				logger.log(Level.WARN, "Extension file not found or not an .xpi file");
			}
		}

		return firefoxDriver;
	}

	private String getFirefoxMajorVersion(String firefoxBrowserPath) {
		try {
			String output = extractVersionFromCommand(new ProcessBuilder(firefoxBrowserPath, "-v"));

			if (output == null || output.isBlank()) {
				// Version fallback using cmd shell (for Windows OS)
				output = extractVersionFromCommand(new ProcessBuilder("cmd.exe", "/c", firefoxBrowserPath + " -v | more"));
			}

			// Example: "Mozilla Firefox 139.0" to "139"
			if (output != null && output.toLowerCase().contains("firefox")) {
				String version = output.replaceAll("[^\\d.]", "");
				return version.split("\\.")[0];
			}

		} catch (IOException e) {
			logger.log(Level.WARN, "Failed to get Firefox version from binary", e);
		}

		return "";
	}

	private String extractVersionFromCommand(ProcessBuilder builder) throws IOException {
		Process process = builder.start();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
			return reader.readLine();
		}
	}

}
