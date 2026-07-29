// Implements WS-FUNC-TEST-SETTINGS-001: parses workspace settings and derives runtime-facing values.
export const CLI_AGENT_SETTING_KEYS = {
    apiKeyEnvVarName: "AgentCLIApiKeyEnvVar",
    baseUrl: "AgentCLIBaseUrl",
    model: "AgentCLIModel",
    reasoningEffort: "AgentCLIReasoningEffort",
    sandboxMode: "AgentCLISandboxMode",
    approvalPolicy: "AgentCLIApprovalPolicy",
    allowNetworkAccess: "AgentCLIAllowNetworkAccess",
    skipGitRepoCheck: "AgentCLISkipGitRepoCheck",
    promptTitle: "AgentCLIPromptTitle",
    promptText: "AgentCLIPromptText"
};

export const DEFAULT_CLI_AGENT_SETTINGS = {
    apiKeyEnvVarName: "OPENAI_API_KEY",
    baseUrl: "",
    model: "gpt-5.4-mini",
    reasoningEffort: "medium",
    sandboxMode: "danger-full-access",
    approvalPolicy: "never",
    allowNetworkAccess: false,
    skipGitRepoCheck: true,
    promptTitle: "Test Parabank Login",
    promptText: "As a test agent verify that you can log in with the credentials john/demo. Then the Welcome John Smith message is shown."
};

export function parsePropertiesContent(content) {
    const properties = {};
    if (!content) {
        return properties;
    }

    for (const rawLine of content.split(/\r?\n/)) {
        const line = rawLine.trim();
        if (!line || line.startsWith("#") || !line.includes("=")) {
            continue;
        }

        const separatorIndex = line.indexOf("=");
        const key = line.slice(0, separatorIndex).trim();
        const value = line.slice(separatorIndex + 1).trim();
        properties[key] = value;
    }

    return properties;
}

export function findWorkspaceSetting(workspaceDocument, settingKey) {
    for (const settingsGroup of workspaceDocument?.settingsGroups || []) {
        const setting = (settingsGroup.settings || []).find((candidate) => candidate.key === settingKey);
        if (setting) {
            return setting;
        }
    }

    return null;
}

export function workspaceSettingValue(workspaceDocument, settingKey) {
    return findWorkspaceSetting(workspaceDocument, settingKey)?.value || "";
}

export function workspaceSettingBoolean(workspaceDocument, settingKey, defaultValue = false) {
    const value = workspaceSettingValue(workspaceDocument, settingKey);
    if (value === "") {
        return defaultValue;
    }

    return value === true || `${value}`.toLowerCase() === "true";
}

export function workspaceSettingDefaultValue(workspaceDocument, settingKey) {
    return findWorkspaceSetting(workspaceDocument, settingKey)?.defaultValue || "";
}

export function normalizeSettingDisplayValue(value) {
    const text = String(value || "").trim();
    if (text.length >= 2) {
        const hasDoubleQuotes = text.startsWith("\"") && text.endsWith("\"");
        const hasSingleQuotes = text.startsWith("'") && text.endsWith("'");
        if (hasDoubleQuotes || hasSingleQuotes) {
            return text.slice(1, -1).trim();
        }
    }

    return text;
}

export function normalizedCliAgentSettings(settings) {
    const source = settings || {};
    const valueOrDefault = (value, defaultValue) => {
        if (value === null || value === undefined || value === "") {
            return defaultValue;
        }

        return value;
    };

    return {
        apiKeyEnvVarName: valueOrDefault(source.apiKeyEnvVarName, DEFAULT_CLI_AGENT_SETTINGS.apiKeyEnvVarName),
        baseUrl: source.baseUrl ?? DEFAULT_CLI_AGENT_SETTINGS.baseUrl,
        model: valueOrDefault(source.model, DEFAULT_CLI_AGENT_SETTINGS.model),
        reasoningEffort: valueOrDefault(source.reasoningEffort, DEFAULT_CLI_AGENT_SETTINGS.reasoningEffort),
        sandboxMode: valueOrDefault(source.sandboxMode, DEFAULT_CLI_AGENT_SETTINGS.sandboxMode),
        approvalPolicy: valueOrDefault(source.approvalPolicy, DEFAULT_CLI_AGENT_SETTINGS.approvalPolicy),
        allowNetworkAccess: Boolean(source.allowNetworkAccess),
        skipGitRepoCheck: source.skipGitRepoCheck !== false,
        promptTitle: valueOrDefault(source.promptTitle, DEFAULT_CLI_AGENT_SETTINGS.promptTitle),
        promptText: valueOrDefault(source.promptText, DEFAULT_CLI_AGENT_SETTINGS.promptText)
    };
}

export function cliAgentSettingsFromWorkspace(workspaceDocument) {
    return normalizedCliAgentSettings({
        apiKeyEnvVarName: workspaceSettingValue(workspaceDocument, CLI_AGENT_SETTING_KEYS.apiKeyEnvVarName),
        baseUrl: workspaceSettingValue(workspaceDocument, CLI_AGENT_SETTING_KEYS.baseUrl),
        model: workspaceSettingValue(workspaceDocument, CLI_AGENT_SETTING_KEYS.model),
        reasoningEffort: workspaceSettingValue(workspaceDocument, CLI_AGENT_SETTING_KEYS.reasoningEffort),
        sandboxMode: workspaceSettingValue(workspaceDocument, CLI_AGENT_SETTING_KEYS.sandboxMode),
        approvalPolicy: workspaceSettingValue(workspaceDocument, CLI_AGENT_SETTING_KEYS.approvalPolicy),
        allowNetworkAccess: workspaceSettingBoolean(
            workspaceDocument,
            CLI_AGENT_SETTING_KEYS.allowNetworkAccess,
            DEFAULT_CLI_AGENT_SETTINGS.allowNetworkAccess
        ),
        skipGitRepoCheck: workspaceSettingBoolean(
            workspaceDocument,
            CLI_AGENT_SETTING_KEYS.skipGitRepoCheck,
            DEFAULT_CLI_AGENT_SETTINGS.skipGitRepoCheck
        ),
        promptTitle: workspaceSettingValue(workspaceDocument, CLI_AGENT_SETTING_KEYS.promptTitle),
        promptText: workspaceSettingValue(workspaceDocument, CLI_AGENT_SETTING_KEYS.promptText)
    });
}

export function workspaceRuntimeSettings(workspaceDocument) {
    const testSettingsProperties = parsePropertiesContent(workspaceDocument?.testSettings?.content || "");
    return {
        sutConnector: normalizeSettingDisplayValue(
            testSettingsProperties.SUTConnector
                || workspaceSettingValue(workspaceDocument, "SUTConnector")
        ),
        sutConnectorValue: normalizeSettingDisplayValue(
            testSettingsProperties.SUTConnectorValue
                || workspaceSettingValue(workspaceDocument, "SUTConnectorValue")
        ),
        cliStateProjectionMode: normalizeSettingDisplayValue(
            testSettingsProperties.CliStateProjectionMode
                || workspaceSettingValue(workspaceDocument, "CliStateProjectionMode")
                || workspaceSettingDefaultValue(workspaceDocument, "CliStateProjectionMode")
        ),
        cliAgentSettings: cliAgentSettingsFromWorkspace(workspaceDocument)
    };
}

export function workspaceSummaryForName(workspaces, workspaceName) {
    return (workspaces || []).find((workspace) => workspace.name === workspaceName) || null;
}

export function preferredDefaultWorkspace(workspaces, preferredWorkspaceName = "webdriver_generic") {
    const workspaceList = workspaces || [];
    return workspaceSummaryForName(workspaceList, preferredWorkspaceName) || workspaceList[0] || null;
}

export function emptyWorkspaceState() {
    return {
        selectedWorkspaceSummary: null,
        selectedWorkspaceName: "",
        workspaceDocument: null,
        selectedWorkspaceSutConnector: "",
        selectedWorkspaceSutConnectorValue: "",
        selectedWorkspaceCliStateProjectionMode: "",
        cliAgentSettings: { ...DEFAULT_CLI_AGENT_SETTINGS },
        savedCliAgentSettings: { ...DEFAULT_CLI_AGENT_SETTINGS },
        savedTestSettingsContent: "",
        savedCompositionPropertiesContent: "",
        savedPoliciesPropertiesContent: "",
        savedSourceContents: {},
        selectedSettingsGroupId: ""
    };
}

export function clearedWorkspaceDocumentState() {
    return {
        workspaceDocument: null,
        selectedWorkspaceSutConnector: "",
        selectedWorkspaceSutConnectorValue: "",
        selectedWorkspaceCliStateProjectionMode: "",
        cliAgentSettings: { ...DEFAULT_CLI_AGENT_SETTINGS },
        savedCliAgentSettings: { ...DEFAULT_CLI_AGENT_SETTINGS },
        savedTestSettingsContent: "",
        savedCompositionPropertiesContent: "",
        savedPoliciesPropertiesContent: "",
        savedSourceContents: {},
        selectedSettingsGroupId: ""
    };
}

export function workspaceDocumentBaseline(workspaceDocument) {
    const runtimeSettings = workspaceRuntimeSettings(workspaceDocument);
    const cliAgentSettings = normalizedCliAgentSettings(runtimeSettings.cliAgentSettings);

    return {
        savedTestSettingsContent: workspaceDocument?.testSettings?.content || "",
        savedCompositionPropertiesContent: workspaceDocument?.compositionProperties?.content || "",
        savedPoliciesPropertiesContent: workspaceDocument?.policiesProperties?.content || "",
        selectedSettingsGroupId: workspaceDocument?.settingsGroups?.[0]?.id || "",
        selectedWorkspaceSutConnector: runtimeSettings.sutConnector,
        selectedWorkspaceSutConnectorValue: runtimeSettings.sutConnectorValue,
        selectedWorkspaceCliStateProjectionMode: runtimeSettings.cliStateProjectionMode,
        cliAgentSettings,
        savedCliAgentSettings: { ...cliAgentSettings }
    };
}

export function updatedSavedSourceContents(savedSourceContents, sourceName, sourceFile) {
    return {
        ...(savedSourceContents || {}),
        [sourceName]: sourceFile?.content || ""
    };
}

export function validSettingsGroupId(workspaceDocument, selectedSettingsGroupId) {
    const settingsGroups = workspaceDocument?.settingsGroups || [];
    if (settingsGroups.length === 0) {
        return "";
    }

    const matchingSettingsGroup = settingsGroups.find((settingsGroup) => settingsGroup.id === selectedSettingsGroupId);
    if (matchingSettingsGroup) {
        return selectedSettingsGroupId;
    }

    return settingsGroups[0].id;
}
