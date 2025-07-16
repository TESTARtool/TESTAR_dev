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

import org.testar.llm.LlmResponse;
import org.testar.llm.openai.LlmConversationOpenAI.ContentPart;
import org.testar.llm.openai.LlmConversationOpenAI.Message;

/**
 * Response sent by the OpenAI compatible LLM API.
 * We can extract the generated message by retrieving choice 0.
 */
public class LlmResponseOpenAI implements LlmResponse {
    private String id;

    private List<Choice> choices = new ArrayList<>();
    private Usage usage;

    public LlmResponseOpenAI(String id, List<Choice> choices, Usage usage) {
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

    @Override
    public String getResponse() {
        Message message = this.getChoices().get(0).getMessage();
        StringBuilder result = new StringBuilder();

        for (ContentPart part : message.getContent()) {
            if ("text".equals(part.getType()) && part.getText() instanceof String) {
                result.append((String) part.getText());
            }
        }

        return result.toString().trim();
    }

    @Override
    public int getUsageTokens() {
        return this.getUsage().getTotal_tokens();
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
        private LlmConversationOpenAI.Message message;
        private String finish_reason;

        public Choice(int index, LlmConversationOpenAI.Message message, String finish_reason) {
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

        public LlmConversationOpenAI.Message getMessage() {
            return message;
        }

        public void setMessage(LlmConversationOpenAI.Message message) {
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
