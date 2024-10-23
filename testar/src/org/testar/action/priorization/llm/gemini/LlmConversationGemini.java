package org.testar.action.priorization.llm.gemini;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testar.action.priorization.llm.LlmConversation;

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

    public LlmConversationGemini() {
        contents = new ArrayList<>();
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
    public void addMessage(String role, String text) {
        Content content = new Content(role);
        content.addPart(new Part(text));
        contents.add(content);
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
