/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.agent.codex;

import java.nio.file.Path;

public final class CodexRunResult {

    private final boolean success;
    private final String status;
    private final String threadId;
    private final String finalResponse;
    private final String errorMessage;
    private final Path runDirectory;
    private final Usage usage;

    public CodexRunResult(boolean success,
                          String status,
                          String threadId,
                          String finalResponse,
                          String errorMessage,
                          Path runDirectory,
                          Usage usage) {
        this.success = success;
        this.status = status == null ? "" : status;
        this.threadId = threadId == null ? "" : threadId;
        this.finalResponse = finalResponse == null ? "" : finalResponse;
        this.errorMessage = errorMessage == null ? "" : errorMessage;
        this.runDirectory = runDirectory;
        this.usage = usage;
    }

    public boolean success() {
        return success;
    }

    public String status() {
        return status;
    }

    public String threadId() {
        return threadId;
    }

    public String finalResponse() {
        return finalResponse;
    }

    public String errorMessage() {
        return errorMessage;
    }

    public Path runDirectory() {
        return runDirectory;
    }

    public Usage usage() {
        return usage;
    }

    public String toNotificationMessage() {
        if (!success) {
            return errorMessage.isBlank() ? "Codex agent execution failed." : errorMessage;
        }

        StringBuilder message = new StringBuilder("Codex agent execution completed.");
        if (usage != null) {
            message.append(" Tokens: in=").append(usage.inputTokens())
                    .append(", out=").append(usage.outputTokens())
                    .append(", reasoning=").append(usage.reasoningOutputTokens());
        }
        return message.toString();
    }

    public static final class Usage {

        private final int inputTokens;
        private final int cachedInputTokens;
        private final int outputTokens;
        private final int reasoningOutputTokens;

        public Usage(int inputTokens, int cachedInputTokens, int outputTokens, int reasoningOutputTokens) {
            this.inputTokens = inputTokens;
            this.cachedInputTokens = cachedInputTokens;
            this.outputTokens = outputTokens;
            this.reasoningOutputTokens = reasoningOutputTokens;
        }

        public int inputTokens() {
            return inputTokens;
        }

        public int cachedInputTokens() {
            return cachedInputTokens;
        }

        public int outputTokens() {
            return outputTokens;
        }

        public int reasoningOutputTokens() {
            return reasoningOutputTokens;
        }
    }
}
