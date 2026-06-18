/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.agent.codex;

public final class CodexAgentSettings {

    private final String apiKeyEnvVarName;
    private final String baseUrl;
    private final String model;
    private final String reasoningEffort;
    private final String sandboxMode;
    private final String approvalPolicy;
    private final boolean networkAccessEnabled;
    private final boolean skipGitRepoCheck;
    private final String promptTitle;
    private final String promptText;

    public CodexAgentSettings(String apiKeyEnvVarName,
                              String baseUrl,
                              String model,
                              String reasoningEffort,
                              String sandboxMode,
                              String approvalPolicy,
                              boolean networkAccessEnabled,
                              boolean skipGitRepoCheck,
                              String promptTitle,
                              String promptText) {
        this.apiKeyEnvVarName = apiKeyEnvVarName == null ? "" : apiKeyEnvVarName.trim();
        this.baseUrl = baseUrl == null ? "" : baseUrl.trim();
        this.model = model == null ? "" : model.trim();
        this.reasoningEffort = reasoningEffort == null ? "" : reasoningEffort.trim();
        this.sandboxMode = sandboxMode == null ? "" : sandboxMode.trim();
        this.approvalPolicy = approvalPolicy == null ? "" : approvalPolicy.trim();
        this.networkAccessEnabled = networkAccessEnabled;
        this.skipGitRepoCheck = skipGitRepoCheck;
        this.promptTitle = promptTitle == null ? "" : promptTitle.trim();
        this.promptText = promptText == null ? "" : promptText.trim();
    }

    public String apiKeyEnvVarName() {
        return apiKeyEnvVarName;
    }

    public String baseUrl() {
        return baseUrl;
    }

    public String model() {
        return model;
    }

    public String reasoningEffort() {
        return reasoningEffort;
    }

    public String sandboxMode() {
        return sandboxMode;
    }

    public String approvalPolicy() {
        return approvalPolicy;
    }

    public boolean networkAccessEnabled() {
        return networkAccessEnabled;
    }

    public boolean skipGitRepoCheck() {
        return skipGitRepoCheck;
    }

    public String promptTitle() {
        return promptTitle;
    }

    public String promptText() {
        return promptText;
    }
}
