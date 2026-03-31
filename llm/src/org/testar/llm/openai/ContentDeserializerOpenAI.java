/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2025-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2025-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.llm.openai;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.testar.llm.openai.LlmConversationOpenAI.ContentPart;
import org.testar.llm.openai.LlmConversationOpenAI.ImageUrl;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class ContentDeserializerOpenAI implements JsonDeserializer<List<ContentPart>> {

	@Override
	public List<ContentPart> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		List<ContentPart> parts = new ArrayList<>();

		// { "role": "user", "content": [ { "type": "text", "text": "this is a text message" }, { "type": "image_url", "image_url": { "url": f"data:image/jpeg;base64,{base64_image}", }, }, ], }
		if (json.isJsonArray()) {
			for (JsonElement element : json.getAsJsonArray()) {
				JsonObject obj = element.getAsJsonObject();
				String type = obj.get("type").getAsString();

				if ("text".equals(type)) {
					parts.add(new ContentPart("text", obj.get("text").getAsString()));
				} else if ("image_url".equals(type)) {
					JsonObject img = obj.getAsJsonObject("image_url");
					parts.add(new ContentPart("image_url", new ImageUrl(img.get("url").getAsString())));
				}
			}
		} else if (json.isJsonPrimitive()) {
			// { "role": "user", "content": "content" }
			parts.add(new ContentPart("text", json.getAsString()));
		} else {
			throw new JsonParseException("Unsupported content format: " + json.toString());
		}

		return parts;
	}
}

