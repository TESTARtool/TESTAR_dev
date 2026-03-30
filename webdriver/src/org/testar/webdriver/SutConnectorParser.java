/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2025-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2025-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.webdriver;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;

public class SutConnectorParser {
	private String browserPath = "";
	private String url = "";
	private String screenGeometryRaw = "";

	protected static final Logger logger = LogManager.getLogger();

	public SutConnectorParser(String sutConnector) {
		List<String> parts = parseParts(sutConnector);
		classifyParts(parts);
	}

	private List<String> parseParts(String sutConnector) {
		List<String> parts = new ArrayList<>();
		Matcher matcher = Pattern.compile("\"([^\"]*)\"|(\\S+)").matcher(sutConnector);
		while (matcher.find()) {
			if (matcher.group(1) != null) {
				parts.add(matcher.group(1));
			} else {
				parts.add(matcher.group(2));
			}
		}
		return parts;
	}

	private void classifyParts(List<String> parts) {
		// Identify and classify the WebDriver SUT Connector parts
		for (String raw : parts) {
			String part = raw.replace("\"", "");

			// Explicit URL (http, https, file, etc.)
			if (part.matches("^(https?|file|ftp)://.*")) {
				this.url = part;
			} 
			// Local HTML file treated as URL
			else if (new File(part).exists() && part.toLowerCase().endsWith(".html")) {
				this.url = new File(part).toURI().toString();
			} 
			// Browser executable (local file path)
			else if (part.endsWith(".exe") || new File(part).exists()) {
				this.browserPath = part;
			} 
			// Screen size
			else if (part.matches("^\\d+x\\d+\\+\\d+\\+\\d+$")) {
				this.screenGeometryRaw = part;
			} 
			// Invalid part
			else {
				logger.log(Level.WARN, "Invalid WebDriver SUT Connector part: " + part);
			}
		}
	}

	public String getUrl() {
		return url;
	}

	public String getBrowserPath() {
		return browserPath;
	}

	public String getScreenGeometryRaw() {
		return screenGeometryRaw;
	}

	public Dimension getScreenDimensions() {
		if (screenGeometryRaw.isEmpty()) return null;
		try {
			String[] dims = screenGeometryRaw.split("\\+")[0].split("x");
			return new Dimension(Integer.parseInt(dims[0]), Integer.parseInt(dims[1]));
		} catch (Exception e) {
			logger.log(Level.WARN, "Invalid screen geometry size", e);
			return null;
		}
	}

	public Point getScreenPosition() {
		if (screenGeometryRaw.isEmpty()) return null;
		try {
			String[] pos = screenGeometryRaw.split("\\+");
			return new Point(Integer.parseInt(pos[1]), Integer.parseInt(pos[2]));
		} catch (Exception e) {
			logger.log(Level.WARN, "Invalid screen geometry position", e);
			return null;
		}
	}
}
