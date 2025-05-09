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
import java.util.List;

import org.testar.llm.LlmResponse;

/**
 * Response sent by the Gemini LLM.
 * We can extract the generated message by retrieving choice 0.
 */
public class LlmResponseGemini implements LlmResponse {
    private List<Candidate> candidates = new ArrayList<>();
    private UsageMetadata usageMetadata;

    public LlmResponseGemini(List<Candidate> candidates, UsageMetadata usageMetadata) {
        this.candidates = candidates;
        this.usageMetadata = usageMetadata;
    }

    public List<Candidate> getCandidates() {
        return candidates;
    }

    public void setCandidates(List<Candidate> candidates) {
        this.candidates = candidates;
    }

    public UsageMetadata getUsageMetadata() {
        return usageMetadata;
    }

    public void setUsage(UsageMetadata usageMetadata) {
        this.usageMetadata = usageMetadata;
    }

    @Override
    public String getResponse() {
        return this.getCandidates().get(0).getContent().getParts().get(0).getText();
    }

    @Override
    public int getUsageTokens() {
        return this.getUsageMetadata().getTokenCount();
    }

    public class Candidate {
        private Content content;

        public Candidate(Content content) {
            this.content = content;
        }

        public Content getContent() {
            return content;
        }

        public void setContent(Content content) {
            this.content = content;
        }
    }

    public class Content {
        private List<Part> parts;
        private String role;

        public Content(List<Part> parts, String role) {
            this.parts = parts;
            this.role = role;
        }

        public List<Part> getParts() {
            return parts;
        }

        public void setParts(List<Part> parts) {
            this.parts = parts;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
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

    public class UsageMetadata {
        private int promptTokenCount;
        private int candidatesTokenCount;
        private int totalTokenCount;

        public UsageMetadata(int promptTokenCount, int candidatesTokenCount, int totalTokenCount) {
            this.promptTokenCount = promptTokenCount;
            this.candidatesTokenCount = candidatesTokenCount;
            this.totalTokenCount = totalTokenCount;
        }

        public int getPromptTokenCount() {
            return promptTokenCount;
        }

        public void setPromptTokenCount(int promptTokenCount) {
            this.promptTokenCount = promptTokenCount;
        }

        public int getCandidatesTokenCount() {
            return candidatesTokenCount;
        }

        public void setCandidatesTokenCount(int candidatesTokenCount) {
            this.candidatesTokenCount = candidatesTokenCount;
        }

        public int getTokenCount() {
            return totalTokenCount;
        }

        public void setTokenCount(int totalTokenCount) {
            this.totalTokenCount = totalTokenCount;
        }
    }
}
