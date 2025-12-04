/***************************************************************************************************
 *
 * Copyright (c) 2025 Open Universiteit - www.ou.nl
 * Copyright (c) 2025 Universitat Politecnica de Valencia - www.upv.es
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

package org.testar.llm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.testar.monkey.Main;

public final class LlmUtils {

	private LlmUtils() {}

	public static String replaceApiKeyPlaceholder(String url) {
		String apiKeyPlaceholder = extractPlaceholder(url);

		if (apiKeyPlaceholder.isEmpty()) {
			// Return the original URL if no placeholder is found
			return url;
		}

		String apiKey = System.getenv(apiKeyPlaceholder);
		if (apiKey == null) {
			// Return the original URL if the API key is not found in the system
			return url;
		}

		// Return the final URL with the placeholder replaced by the actual API key
		return url.replace("%" + apiKeyPlaceholder + "%", apiKey);
	}

	private static String extractPlaceholder(String str) {
		int start = str.indexOf('%') + 1;
		int end = str.indexOf('%', start);

		if (start == 0 || end == -1) {
			// No valid placeholder found, return empty string
			return "";
		}

		// Return the placeholder between the '%' symbols
		return str.substring(start, end);
	}

	public static List<String> readTestGoalsFromFile(List<String> testGoalsList) {
		// Only allow when the user indicates one test.goal file
		if (testGoalsList == null || testGoalsList.size() != 1) {
			return testGoalsList;
		}

		String testGoalFileName = testGoalsList.get(0);
		if (testGoalFileName == null) {
			return testGoalsList;
		}

		// Only allow files with .goal extension
		if (!testGoalFileName.toLowerCase().endsWith(".goal")) {
			return testGoalsList;
		}

		File file = new File(Main.settingsDir, testGoalFileName);

		// Check if the test goal file exists
		if (!file.exists() || !file.isFile()) {
			System.out.println("The test.goal file does not exists in the settings folder: " + testGoalFileName);
			return testGoalsList;
		}

		StringBuilder content = new StringBuilder();

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {

			String line;
			boolean firstLine = true;

			while ((line = reader.readLine()) != null) {
				if (line.trim().isEmpty()) {
					continue;
				}
				if (!firstLine) {
					content.append("\\n");
				}
				content.append(line);
				firstLine = false;
			}

		} catch (IOException e) {
			System.out.println("Error reading test goal file: " + e.getMessage());
			return testGoalsList;
		}

		// Split by ";" to get individual test goals
		String full = content.toString();
		String[] parts = full.split(";");
		List<String> goalsFromFile = new ArrayList<>();

		for (String part : parts) {
			String trimmed = part.trim();
			if (!trimmed.isEmpty()) {
				goalsFromFile.add(trimmed);
			}
		}

		// If nothing could be parsed, fall back to the original list
		if (goalsFromFile.isEmpty()) {
			return testGoalsList;
		}

		String msg = String.format("Loaded test goal '%s' to content: %s", testGoalFileName, full);
		System.out.println(msg);

		return goalsFromFile;
	}

}
