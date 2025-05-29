/**
 * Copyright (c) 2025 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2025 Open Universiteit - www.ou.nl
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
import org.testar.monkey.alayer.webdriver.WdDriver;

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
