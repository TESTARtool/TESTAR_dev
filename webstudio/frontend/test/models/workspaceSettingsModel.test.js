// Verifies WS-FUNC-TEST-SETTINGS-001: workspace settings parsing and runtime setting derivation.
import test from "node:test";
import assert from "node:assert/strict";
import {
    CLI_AGENT_SETTING_KEYS,
    DEFAULT_CLI_AGENT_SETTINGS,
    cliAgentSettingsFromWorkspace,
    clearedWorkspaceDocumentState,
    emptyWorkspaceState,
    findWorkspaceSetting,
    normalizeSettingDisplayValue,
    normalizedCliAgentSettings,
    parsePropertiesContent,
    preferredDefaultWorkspace,
    updatedSavedSourceContents,
    validSettingsGroupId,
    workspaceDocumentBaseline,
    workspaceDocumentWithSettingsContentValues,
    workspaceRuntimeSettings,
    workspaceSummaryForName,
    workspaceSettingBoolean,
    workspaceSettingDefaultValue,
    workspaceSettingValue
} from "../../src/models/workspaceSettingsModel.js";

function workspaceDocument(settings = [], testSettingsContent = "") {
    return {
        testSettings: {
            content: testSettingsContent
        },
        settingsGroups: [
            {
                id: "main",
                settings
            }
        ]
    };
}

test("parses properties content while ignoring comments and blank lines", () => {
    assert.deepEqual(
        parsePropertiesContent("SUTConnector = webdriver\n# comment\n\nA=B=C\n"),
        {
            SUTConnector: "webdriver",
            A: "B=C"
        }
    );
});

test("reads workspace setting values defaults and booleans", () => {
    const document = workspaceDocument([
        { key: "SUTConnector", value: "webdriver" },
        { key: "BrowserFullScreen", value: "true" },
        { key: "CliStateProjectionMode", value: "", defaultValue: "INTERACTIVE_SEMANTIC_WIDGETS" }
    ]);

    assert.equal(findWorkspaceSetting(document, "SUTConnector").value, "webdriver");
    assert.equal(workspaceSettingValue(document, "SUTConnector"), "webdriver");
    assert.equal(workspaceSettingValue(document, "Missing"), "");
    assert.equal(workspaceSettingBoolean(document, "BrowserFullScreen"), true);
    assert.equal(workspaceSettingBoolean(document, "Missing", true), true);
    assert.equal(workspaceSettingDefaultValue(document, "CliStateProjectionMode"), "INTERACTIVE_SEMANTIC_WIDGETS");
});

test("normalizes quoted setting display values", () => {
    assert.equal(normalizeSettingDisplayValue("\"webdriver\""), "webdriver");
    assert.equal(normalizeSettingDisplayValue("'webdriver'"), "webdriver");
    assert.equal(normalizeSettingDisplayValue(" webdriver "), "webdriver");
});

test("normalizes CLI agent settings with defaults", () => {
    assert.deepEqual(normalizedCliAgentSettings({}), DEFAULT_CLI_AGENT_SETTINGS);
    assert.equal(normalizedCliAgentSettings({ skipGitRepoCheck: false }).skipGitRepoCheck, false);
    assert.equal(normalizedCliAgentSettings({ allowNetworkAccess: "false" }).allowNetworkAccess, true);
});

test("reads CLI agent settings from workspace settings", () => {
    const document = workspaceDocument([
        { key: CLI_AGENT_SETTING_KEYS.apiKeyEnvVarName, value: "OPENAI_API_KEY" },
        { key: CLI_AGENT_SETTING_KEYS.model, value: "gpt-5.4" },
        { key: CLI_AGENT_SETTING_KEYS.allowNetworkAccess, value: "true" },
        { key: CLI_AGENT_SETTING_KEYS.skipGitRepoCheck, value: "false" }
    ]);

    const settings = cliAgentSettingsFromWorkspace(document);

    assert.equal(settings.apiKeyEnvVarName, "OPENAI_API_KEY");
    assert.equal(settings.model, "gpt-5.4");
    assert.equal(settings.allowNetworkAccess, true);
    assert.equal(settings.skipGitRepoCheck, false);
});

test("derives runtime settings from raw test.settings before visual settings", () => {
    const document = workspaceDocument(
        [
            { key: "SUTConnector", value: "android" },
            { key: "SUTConnectorValue", value: "visual-value" },
            { key: "CliStateProjectionMode", value: "", defaultValue: "INTERACTIVE_SEMANTIC_WIDGETS" }
        ],
        "SUTConnector = webdriver\nSUTConnectorValue = \"https://example.org\"\n"
    );

    assert.deepEqual(workspaceRuntimeSettings(document), {
        sutConnector: "webdriver",
        sutConnectorValue: "https://example.org",
        cliStateProjectionMode: "INTERACTIVE_SEMANTIC_WIDGETS",
        cliAgentSettings: DEFAULT_CLI_AGENT_SETTINGS
    });
});

test("syncs visual settings values from raw test.settings content", () => {
    const document = workspaceDocument(
        [
            { key: "SUTConnector", value: "android" },
            { key: "BrowserFullScreen", value: "false" },
            { key: "Unchanged", value: "keep" }
        ],
        "SUTConnector = webdriver\nBrowserFullScreen = true\n"
    );

    const syncedDocument = workspaceDocumentWithSettingsContentValues(document);

    assert.equal(workspaceSettingValue(syncedDocument, "SUTConnector"), "webdriver");
    assert.equal(workspaceSettingValue(syncedDocument, "BrowserFullScreen"), "true");
    assert.equal(workspaceSettingValue(syncedDocument, "Unchanged"), "keep");
    assert.equal(workspaceSettingValue(document, "SUTConnector"), "android");
});

test("syncs visual settings values while preserving setting metadata", () => {
    const document = workspaceDocument(
        [
            {
                key: "SUTConnector",
                value: "android",
                type: "string",
                description: "Connector"
            }
        ],
        "SUTConnector = webdriver\n"
    );

    const syncedSetting = workspaceDocumentWithSettingsContentValues(document)
        .settingsGroups[0]
        .settings[0];

    assert.deepEqual(syncedSetting, {
        key: "SUTConnector",
        value: "webdriver",
        type: "string",
        description: "Connector"
    });
});

test("finds workspace summaries by name", () => {
    const workspaces = [
        { name: "webdriver_generic" },
        { name: "android_generic" }
    ];

    assert.deepEqual(workspaceSummaryForName(workspaces, "android_generic"), { name: "android_generic" });
    assert.equal(workspaceSummaryForName(workspaces, "missing"), null);
    assert.equal(workspaceSummaryForName(null, "missing"), null);
});

test("selects preferred default workspace when available", () => {
    const workspaces = [
        { name: "android_generic" },
        { name: "webdriver_generic" }
    ];

    assert.deepEqual(preferredDefaultWorkspace(workspaces), { name: "webdriver_generic" });
    assert.deepEqual(preferredDefaultWorkspace([{ name: "android_generic" }]), { name: "android_generic" });
    assert.equal(preferredDefaultWorkspace([]), null);
});

test("builds empty workspace state defaults", () => {
    const state = emptyWorkspaceState();

    assert.equal(state.selectedWorkspaceName, "");
    assert.equal(state.workspaceDocument, null);
    assert.equal(state.selectedWorkspaceSutConnector, "");
    assert.deepEqual(state.cliAgentSettings, DEFAULT_CLI_AGENT_SETTINGS);
    assert.deepEqual(state.savedCliAgentSettings, DEFAULT_CLI_AGENT_SETTINGS);
    assert.deepEqual(state.savedSourceContents, {});
});

test("builds cleared workspace document state defaults without clearing selected workspace name", () => {
    const state = clearedWorkspaceDocumentState();

    assert.equal(state.workspaceDocument, null);
    assert.equal(state.selectedWorkspaceSutConnector, "");
    assert.equal(state.selectedWorkspaceSutConnectorValue, "");
    assert.equal(state.selectedWorkspaceCliStateProjectionMode, "");
    assert.deepEqual(state.cliAgentSettings, DEFAULT_CLI_AGENT_SETTINGS);
    assert.deepEqual(state.savedCliAgentSettings, DEFAULT_CLI_AGENT_SETTINGS);
    assert.equal(state.savedTestSettingsContent, "");
    assert.equal(state.savedCompositionPropertiesContent, "");
    assert.equal(state.savedPoliciesPropertiesContent, "");
    assert.deepEqual(state.savedSourceContents, {});
    assert.equal(state.selectedSettingsGroupId, "");
    assert.equal(Object.hasOwn(state, "selectedWorkspaceName"), false);
});

test("builds a workspace document baseline from loaded workspace data", () => {
    const document = {
        testSettings: {
            content: "SUTConnector = webdriver\nSUTConnectorValue = \"https://example.org\"\n"
        },
        compositionProperties: {
            content: "composition=true"
        },
        policiesProperties: {
            content: "policies=true"
        },
        settingsGroups: [
            {
                id: "sut",
                settings: [
                    { key: CLI_AGENT_SETTING_KEYS.model, value: "gpt-5.4" },
                    { key: "CliStateProjectionMode", value: "", defaultValue: "INTERACTIVE_SEMANTIC_WIDGETS" }
                ]
            }
        ]
    };

    const baseline = workspaceDocumentBaseline(document);

    assert.equal(baseline.savedTestSettingsContent, document.testSettings.content);
    assert.equal(baseline.savedCompositionPropertiesContent, "composition=true");
    assert.equal(baseline.savedPoliciesPropertiesContent, "policies=true");
    assert.equal(baseline.selectedSettingsGroupId, "sut");
    assert.equal(baseline.selectedWorkspaceSutConnector, "webdriver");
    assert.equal(baseline.selectedWorkspaceSutConnectorValue, "https://example.org");
    assert.equal(baseline.selectedWorkspaceCliStateProjectionMode, "INTERACTIVE_SEMANTIC_WIDGETS");
    assert.equal(baseline.cliAgentSettings.model, "gpt-5.4");
    assert.deepEqual(baseline.savedCliAgentSettings, baseline.cliAgentSettings);
});

test("updates saved source contents immutably", () => {
    const previousContents = {
        "Existing.java": "old"
    };

    const nextContents = updatedSavedSourceContents(previousContents, "New.java", {
        content: "new"
    });

    assert.deepEqual(nextContents, {
        "Existing.java": "old",
        "New.java": "new"
    });
    assert.deepEqual(previousContents, {
        "Existing.java": "old"
    });
});

test("keeps selected settings group when valid and falls back when missing", () => {
    const document = {
        settingsGroups: [
            { id: "sut" },
            { id: "runtime" }
        ]
    };

    assert.equal(validSettingsGroupId(document, "runtime"), "runtime");
    assert.equal(validSettingsGroupId(document, "missing"), "sut");
    assert.equal(validSettingsGroupId({ settingsGroups: [] }, "missing"), "");
});
