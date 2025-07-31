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

package org.testar.llm.gemini;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testar.llm.LlmConversation;

import com.google.gson.Gson;

/**
 * Conversation with the Gemini LLM.
 * This gets converted to JSON and sent to the LLM.
 * 
 * { "contents": [{ "role": "user", "parts": [{ "text": "TEXT" }] }] }
 */
public class LlmConversationGemini implements LlmConversation {
    protected static final Logger logger = LogManager.getLogger();

    private List<Content> contents;
    private Map<String, String> generationConfig;

    public LlmConversationGemini() {
        contents = new ArrayList<>();
        // TESTAR expects the LLM response to be formatted as JSON
        generationConfig = new HashMap<>();
        generationConfig.put("response_mime_type", "application/json");
    }

    public List<Content> getContents() {
        return contents;
    }

    @Override
    public void initConversation(String fewshotFile) {
        try {
            String initPromptJson = getTextResource(fewshotFile);

            LlmConversationGemini.Content[] initContents = new Gson().fromJson(initPromptJson, LlmConversationGemini.Content[].class);

            for(LlmConversationGemini.Content content : initContents) {
                for(LlmConversationGemini.Part part : content.getParts()) {
                    addMessage(content.getRole(), part.getText());
                }
            }
        } catch(Exception e) {
            logger.log(Level.ERROR, "Failed to initialize conversation, LLM quality may be degraded.", e);
        }
    }

    @Override
    public void addMessage(String role, String textContent) {
        Content content = new Content(role);
        content.addPart(new Part(textContent));
        contents.add(content);
    }

    @Override
    public void addMessage(String role, String textContent, String base64ImageData) {
        // TODO: Implement image base64 communication for Gemini LLMs
        addMessage(role, textContent);
    }

    public class Content {
        private String role;
        private List<Part> parts;

        public Content(String role) {
            this.role = role;
            this.parts = new ArrayList<>();
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public List<Part> getParts() {
            return parts;
        }

        public void addPart(Part part) {
            parts.add(part);
        }
    }

    public class Part {
        private String text;

        public Part(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}
