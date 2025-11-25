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

package org.testar.llm.openai;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testar.llm.LlmConversation;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.reflect.TypeToken;

/**
 * Conversation with the OpenAI LLM.
 * This gets converted to JSON and sent to the LLM.
 */
public class LlmConversationOpenAI implements LlmConversation {
    protected static final Logger logger = LogManager.getLogger();

    private String model;
    private String reasoning_effort;
    private ResponseFormat response_format;
    private List<Message> messages;
    private float temperature = 0.2f;
    private Integer max_tokens = null;
    private boolean stream = false;

    public LlmConversationOpenAI(String model, String reasoning_effort, float temperature) {
        this.model = model;
        this.reasoning_effort = reasoning_effort;
        this.response_format = new ResponseFormat("json_object");
        this.messages = new ArrayList<>();
        this.temperature = temperature;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getReasoningEffort() {
        return reasoning_effort;
    }

    public void setReasoningEffort(String reasoning_effort) {
        this.reasoning_effort = reasoning_effort;
    }

    public ResponseFormat getResponseFormat() {
        return response_format;
    }

    public void setResponseFormat(ResponseFormat response_format) {
        this.response_format = response_format;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public Integer getMax_tokens() {
        return max_tokens;
    }

    public void setMax_tokens(int max_tokens) {
        this.max_tokens = max_tokens;
    }

    public boolean isStream() {
        return stream;
    }

    public void setStream(boolean stream) {
        this.stream = stream;
    }

    @Override
    public String buildRequestBody() {
        // Serialization for OpenAI
        return new GsonBuilder().create().toJson(toRequestJson());
    }

    @Override
    public void initConversation(String fewshotFile) {
        try {
            String initPromptJson = getTextResource(fewshotFile);

            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(new TypeToken<List<ContentPart>>() {}.getType(), new ContentDeserializerOpenAI())
                    .create();

            LlmConversationOpenAI.Message[] initMessages = gson.fromJson(initPromptJson, LlmConversationOpenAI.Message[].class);
            for(LlmConversationOpenAI.Message message : initMessages) {
                addMessage(message);
            }
        } catch(Exception e) {
            logger.log(Level.ERROR, "Failed to initialize conversation, LLM quality may be degraded.");
        }
    }

    public void addMessage(Message message) {
        messages.add(message);
    }

    @Override
    public void addMessage(String role, String textContent) {
        List<ContentPart> content = new ArrayList<>();
        content.add(new ContentPart("text", textContent));
        messages.add(new Message(role, content));
    }

    @Override
    public void addMessage(String role, String textContent, String base64ImageData) {
        List<ContentPart> content = new ArrayList<>();
        if (base64ImageData != null) {
            content.add(new ContentPart("image_url", new ImageUrl("data:image/png;base64," + base64ImageData)));
        }
        if (textContent != null && !textContent.isEmpty()) {
            content.add(new ContentPart("text", textContent));
        }
        messages.add(new Message(role, content));
    }

    /**
     * A message inside a conversation with the LLM.
     */
    public class Message {
        private String role;

        @JsonAdapter(ContentDeserializerOpenAI.class)
        private List<ContentPart> content;

        /**
         * Creates a new Message.
         * @param role Role of the message. Can be "system", "user", or "assistant".
         * @param content Content of the message in plaintext.
         */
        public Message(String role, List<ContentPart> content) {
            this.role = role;
            this.content = content;
        }

        public String getRole() {
            return role;
        }

        public List<ContentPart> getContent() {
            return content;
        }
    }

    public static class ContentPart {
        private String type;
        private String text;
        private ImageUrl image_url;

        public ContentPart() {}

        public ContentPart(String type, String text) {
            this.type = type;
            this.text = text;
        }

        public ContentPart(String type, ImageUrl image_url) {
            this.type = type;
            this.image_url = image_url;
        }

        public String getType() {
            return type;
        }

        public String getText() {
            return text;
        }

        public ImageUrl getImage_url() {
            return image_url;
        }
    }

    public static class ImageUrl {
        private String url;

        public ImageUrl(String url) {
            this.url = url;
        }

        public String getUrl() {
            return url;
        }
    }

    public static class ResponseFormat {
        private String type;
        public ResponseFormat(String type) { this.type = type; }
        public String getType() { return type; }
    }

    public JsonObject toRequestJson() {
        JsonObject root = new JsonObject();

        root.addProperty("model", model);

        if (supportsReasoningEffort(model)
                && reasoning_effort != null
                && !reasoning_effort.isEmpty()
                && !"default".equalsIgnoreCase(reasoning_effort)) {
            root.addProperty("reasoning_effort", reasoning_effort);
        }

        if (response_format != null && response_format.getType() != null) {
            JsonObject rf = new JsonObject();
            rf.addProperty("type", response_format.getType());
            root.add("response_format", rf);
        }

        if (!hasFixedTemperature(model)) {
            root.addProperty("temperature", temperature);
        }

        if (max_tokens != null) {
            root.addProperty("max_tokens", max_tokens);
        }

        root.addProperty("stream", stream);

        JsonArray msgs = new JsonArray();
        for (Message m : messages) {
            JsonObject jm = new JsonObject();
            jm.addProperty("role", m.getRole());
            JsonArray content = new JsonArray();
            for (ContentPart p : m.getContent()) {
                if (p == null || p.getType() == null) continue;
                JsonObject part = new JsonObject();
                part.addProperty("type", p.getType());
                if ("text".equals(p.getType())) {
                    part.addProperty("text", p.getText() == null ? "" : p.getText());
                } else if ("image_url".equals(p.getType()) && p.getImage_url() != null) {
                    JsonObject iu = new JsonObject();
                    iu.addProperty("url", p.getImage_url().getUrl());
                    part.add("image_url", iu);
                }
                content.add(part);
            }
            jm.add("content", content);
            msgs.add(jm);
        }
        root.add("messages", msgs);

        return root;
    }

    private static boolean supportsReasoningEffort(String model) {
        if (model == null) return false;
        String m = model.toLowerCase();
        return m.startsWith("gpt-5") || m.startsWith("o-") || m.contains("reasoning");
    }

    private static boolean hasFixedTemperature(String model) {
        if (model == null) return false;
        String m = model.toLowerCase();
        return m.startsWith("gpt-5") || m.startsWith("o-") || m.contains("reasoning");
    }

}
