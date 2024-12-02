package org.testar.action.priorization.llm;

import org.testar.action.priorization.llm.gemini.LlmConversationGemini;
import org.testar.action.priorization.llm.gemini.LlmResponseGemini;
import org.testar.action.priorization.llm.openai.LlmConversationOpenAI;
import org.testar.action.priorization.llm.openai.LlmResponseOpenAI;

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
