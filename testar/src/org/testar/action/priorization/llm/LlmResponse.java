package org.testar.action.priorization.llm;

import java.util.ArrayList;
import java.util.List;

public class LlmResponse {
    private String id;

    private List<Choice> choices = new ArrayList<>();

    public LlmResponse(String id, List<Choice> choices) {
        this.id = id;
        this.choices = choices;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Choice> getChoices() {
        return choices;
    }

    public void setChoices(List<Choice> choices) {
        this.choices = choices;
    }

    public class Choice {
        private int index;
        private LlmConversation.Message message;
        private String finish_reason;

        public Choice(int index, LlmConversation.Message message, String finish_reason) {
            this.index = index;
            this.message = message;
            this.finish_reason = finish_reason;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public LlmConversation.Message getMessage() {
            return message;
        }

        public void setMessage(LlmConversation.Message message) {
            this.message = message;
        }

        public String getFinish_reason() {
            return finish_reason;
        }

        public void setFinish_reason(String finish_reason) {
            this.finish_reason = finish_reason;
        }
    }
}
