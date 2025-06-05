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
