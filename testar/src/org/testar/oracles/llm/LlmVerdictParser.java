/***************************************************************************************************
 *
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.e
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
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

package org.testar.oracles.llm;

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
