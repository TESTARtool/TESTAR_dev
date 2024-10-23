package org.testar.action.priorization.llm;

import java.util.ArrayList;
import java.util.List;

/**
 * Conversation with the LLM.
 * This gets converted to JSON and sent to the LLM.
 */
public class LlmConversation {
    private List<Message> messages;
    private float temperature = 0.3f;
    private int max_tokens = -1;
    private boolean stream = false;

    public LlmConversation(float temperature) {
        this.temperature = temperature;
        messages = new ArrayList<>();
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

    public float getMax_tokens() {
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

    public void addMessage(String role, String content) {
        messages.add(new Message(role, content));
    }

    public void addMessage(Message message) {
        messages.add(message);
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
