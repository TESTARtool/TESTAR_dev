/***************************************************************************************************
 *
 * Copyright (c) 2024 - 2025 Open Universiteit - www.ou.nl
 * Copyright (c) 2024 - 2025 Universitat Politecnica de Valencia - www.upv.es
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

import org.testar.llm.gemini.LlmConversationGemini;
import org.testar.llm.gemini.LlmResponseGemini;
import org.testar.llm.openai.LlmConversationOpenAI;
import org.testar.llm.openai.LlmResponseOpenAI;

import com.google.gson.Gson;

/**
 * Factory class for creating LlmConversation objects.
 */
public class LlmFactory {
    private static final Gson GSON = new Gson();

    /**
     * Creates a new LLmConversation object used to store messages sent/received from the llm.
     * @param platform Supported: OpenAI, Gemini.
     * @param model The model to use, can be empty when using LMStudio with a single model loaded.
     * @param temperature Lower values result in more predictable output, usually between 0-1f.
     * @return LlmConversation for the chosen platform.
     */
    public static LlmConversation createLlmConversation(String platform, String model, float temperature) {
        switch (platform) {
        case "OpenAI":
            return new LlmConversationOpenAI(model, temperature);
        case "Gemini":
            return new LlmConversationGemini();
        default:
            throw new IllegalArgumentException("Unknown conversation platform: " + platform);
        }
    }

    /**
     * Creates an LlmResponse object based on the JSON body of the received output of the llm's API.
     * @param platform Supported: OpenAI, Gemini.
     * @param response Llm response in JSON.
     * @return LlmResponse object.
     */
    public static LlmResponse createResponse(String platform, StringBuilder response) {
        switch (platform) {
        case "OpenAI":
            return GSON.fromJson(response.toString(), LlmResponseOpenAI.class);
        case "Gemini":
            return GSON.fromJson(response.toString(), LlmResponseGemini.class);
        default:
            throw new IllegalArgumentException("Unknown response platform: " + platform);
        }
    }
}
