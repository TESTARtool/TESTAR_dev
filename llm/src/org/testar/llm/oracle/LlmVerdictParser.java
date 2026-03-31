/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.llm.oracle;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public final class LlmVerdictParser {

	private LlmVerdictParser() {}

	public static LlmVerdict parse(String llmResponse) {
		JsonObject object = JsonParser.parseString(llmResponse).getAsJsonObject();

		String info = getStringValue(object, "info");
		String status = getStringValue(object, "status");
		Boolean match = getBooleanValue(object, "match");

		return new LlmVerdict(match, status, info);
	}

	private static String getStringValue(JsonObject object, String property) {
		if (!object.has(property) || object.get(property).isJsonNull()) {
			return "";
		}
		JsonElement value = object.get(property);
		if (value.isJsonPrimitive()) {
			return value.getAsString();
		}
		return value.toString();
	}

	private static Boolean getBooleanValue(JsonObject object, String property) {
		if (!object.has(property) || object.get(property).isJsonNull()) {
			return null;
		}

		JsonElement value = object.get(property);
		try {
			return value.getAsBoolean();
		} catch (Exception ignored) {
			try {
				return Boolean.parseBoolean(value.getAsString());
			} catch (Exception ignoredAgain) {
				return null;
			}
		}
	}
}
