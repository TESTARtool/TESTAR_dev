package org.testar.action.priorization.llm;

import java.util.ArrayList;
import java.util.List;

/**
 * Response sent by the OpenAI compatible LLM API.
 * We can extract the generated message by retrieving choice 0.
 */
public class LlmResponse {
    private String id;

    private List<Choice> choices = new ArrayList<>();
    private Usage usage;

    public LlmResponse(String id, List<Choice> choices, Usage usage) {
        this.id = id;
        this.choices = choices;
        this.usage = usage;
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

    public Usage getUsage() {
        return usage;
    }

    public void setUsage(Usage usage) {
        this.usage = usage;
    }

    public class Usage {
        private int prompt_tokens;
        private int completion_tokens;
        private int total_tokens;

        public Usage(int prompt_tokens, int completion_tokens, int total_tokens) {
            this.prompt_tokens = prompt_tokens;
            this.completion_tokens = completion_tokens;
            this.total_tokens = total_tokens;
        }

        public int getPrompt_tokens() {
            return prompt_tokens;
        }

        public void setPrompt_tokens(int prompt_tokens) {
            this.prompt_tokens = prompt_tokens;
        }

        public int getCompletion_tokens() {
            return completion_tokens;
        }

        public void setCompletion_tokens(int completion_tokens) {
            this.completion_tokens = completion_tokens;
        }

        public int getTotal_tokens() {
            return total_tokens;
        }

        public void setTotal_tokens(int total_tokens) {
            this.total_tokens = total_tokens;
        }
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
