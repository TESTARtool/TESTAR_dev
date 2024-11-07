package org.testar.action.priorization.llm.openai;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testar.action.priorization.llm.LlmConversation;

import com.google.gson.Gson;

/**
 * Conversation with the OpenAI LLM.
 * This gets converted to JSON and sent to the LLM.
 */
public class LlmConversationOpenAI implements LlmConversation {
    protected static final Logger logger = LogManager.getLogger();

    private String model;
    private List<Message> messages;
    private float temperature = 0.2f;
    private Integer max_tokens = null;
    private boolean stream = false;

    public LlmConversationOpenAI(String model, float temperature) {
        this.model = model;
        this.messages = new ArrayList<>();
        this.temperature = temperature;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
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
