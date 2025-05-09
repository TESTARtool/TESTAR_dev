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

/**
 * Conversation with the OpenAI LLM.
 * This gets converted to JSON and sent to the LLM.
 */
public class LlmConversationOpenAI implements LlmConversation {
    protected static final Logger logger = LogManager.getLogger();

    private String model;
    private String format;
    private List<Message> messages;
    private float temperature = 0.2f;
    private Integer max_tokens = null;
    private boolean stream = false;

    public LlmConversationOpenAI(String model, float temperature) {
        this.model = model;
        this.format = "json";
        this.messages = new ArrayList<>();
        this.temperature = temperature;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getFormat() {
    	return format;
    }

    public void setFormat(String format) {
    	this.format = format;
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
    public void initConversation(String fewshotFile) {
        try {
            String initPromptJson = getTextResource(fewshotFile);
            LlmConversationOpenAI.Message[] initMessages = new Gson().fromJson(initPromptJson, LlmConversationOpenAI.Message[].class);
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
    public void addMessage(String role, String content) {
        messages.add(new Message(role, content));
    }

    /**
     * A message inside a conversation with the LLM.
     */
    public class Message {
        private String role;
        private String content;

        /**
         * Creates a new Message.
         * @param role Role of the message. Can be "system", "user", or "assistant".
         * @param content Content of the message in plaintext.
         */
        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
