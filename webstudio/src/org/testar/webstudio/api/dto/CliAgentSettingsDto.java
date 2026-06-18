/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.webstudio.api.dto;

public final class CliAgentSettingsDto {

    private String apiKeyEnvVarName;
    private String baseUrl;
    private String model;
    private String reasoningEffort;
    private String sandboxMode;
    private String approvalPolicy;
    private Boolean networkAccessEnabled;
    private Boolean skipGitRepoCheck;
    private String promptTitle;
    private String promptText;

    public CliAgentSettingsDto() {
    }

    public CliAgentSettingsDto(
        String apiKeyEnvVarName,
        String baseUrl,
        String model,
        String reasoningEffort,
        String sandboxMode,
        String approvalPolicy,
        Boolean networkAccessEnabled,
        Boolean skipGitRepoCheck,
        String promptTitle,
        String promptText
    ) {
        this.apiKeyEnvVarName = apiKeyEnvVarName;
        this.baseUrl = baseUrl;
        this.model = model;
        this.reasoningEffort = reasoningEffort;
        this.sandboxMode = sandboxMode;
        this.approvalPolicy = approvalPolicy;
        this.networkAccessEnabled = networkAccessEnabled;
        this.skipGitRepoCheck = skipGitRepoCheck;
        this.promptTitle = promptTitle;
        this.promptText = promptText;
    }

    public String apiKeyEnvVarName() {
        return valueOrDefault(apiKeyEnvVarName, "OPENAI_API_KEY");
    }

    public String baseUrl() {
        return valueOrDefault(baseUrl, "");
    }

    public String model() {
        return valueOrDefault(model, "gpt-5.4");
    }

    public String reasoningEffort() {
        return valueOrDefault(reasoningEffort, "medium");
    }

    public String sandboxMode() {
        return valueOrDefault(sandboxMode, "workspace-write");
    }

    public String approvalPolicy() {
        return valueOrDefault(approvalPolicy, "never");
    }

    public Boolean networkAccessEnabled() {
        return networkAccessEnabled != null ? networkAccessEnabled : Boolean.FALSE;
    }

    public Boolean skipGitRepoCheck() {
        return skipGitRepoCheck != null ? skipGitRepoCheck : Boolean.TRUE;
    }

    public String promptTitle() {
        return valueOrDefault(promptTitle, "CLI Agent Goal");
    }

    public String promptText() {
        return valueOrDefault(promptText, "");
    }

    private String valueOrDefault(String value, String defaultValue) {
        if (value == null) {
            return defaultValue;
        }

        return value;
    }
}
