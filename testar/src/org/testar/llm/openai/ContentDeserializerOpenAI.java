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

