/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2025-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2025-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.llm;

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
}
