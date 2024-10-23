package org.testar.action.priorization.llm;

import org.testar.action.priorization.llm.gemini.LlmConversationGemini;
import org.testar.action.priorization.llm.gemini.LlmResponseGemini;
import org.testar.action.priorization.llm.openai.LlmConversationOpenAI;
import org.testar.action.priorization.llm.openai.LlmResponseOpenAI;

import com.google.gson.Gson;

public class LlmFactory {
    private static final Gson GSON = new Gson(); 

    public static LlmConversation createLlmConversation(String platform, float temperature) {
        switch (platform) {
        case "OpenAI":
            return new LlmConversationOpenAI(temperature);
        case "Gemini":
            return new LlmConversationGemini();
        default:
            throw new IllegalArgumentException("Unknown conversation platform: " + platform);
        }
    }

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
