// Verifies WS-FUNC-CONFIG-GUARD-001 and WS-UX-CONFIG-GUARD-001:
// guard trigger rules, dialog details, pending navigation state, and save targets.
import test from "node:test";
import assert from "node:assert/strict";
import {
    cliAgentSettingsGuardDetails,
    configurationDirtyAreas,
    guardDialogDetails,
    guardSaveTarget,
    initialPendingGuardState,
    pendingGuardState,
    settingsEditorSelected,
    shouldGuardConfigurationTransition,
    testGoalGuardDetails
} from "../../src/app/configurationGuard.js";

test("guards toggling from raw test.settings to visual settings when settings are dirty", () => {
    assert.equal(
        shouldGuardConfigurationTransition({
            currentPage: "configuration",
            currentEditor: "test-settings",
            nextEditor: "settings-form",
            dirtyAreas: {
                settings: true
            }
        }),
        true
    );
});

test("does not guard toggling from raw test.settings to visual settings when settings are clean", () => {
    assert.equal(
        shouldGuardConfigurationTransition({
            currentPage: "configuration",
            currentEditor: "test-settings",
            nextEditor: "settings-form",
            dirtyAreas: {
                settings: false
            }
        }),
        false
    );
});

test("guards toggling from visual settings to raw test.settings when settings are dirty", () => {
    assert.equal(
        shouldGuardConfigurationTransition({
            currentPage: "configuration",
            currentEditor: "settings-form",
            nextEditor: "test-settings",
            dirtyAreas: {
                settings: true
            }
        }),
        true
    );
});

test("guards leaving dirty basic settings", () => {
    assert.equal(
        shouldGuardConfigurationTransition({
            currentPage: "basic-settings",
            currentEditor: "settings-form",
            nextEditor: "__leave__",
            dirtyAreas: {
                settings: true
            }
        }),
        true
    );
});

test("guards leaving dirty split settings page", () => {
    assert.equal(
        shouldGuardConfigurationTransition({
            currentPage: "settings",
            currentEditor: "settings-form",
            nextEditor: "__leave__",
            dirtyAreas: {
                settings: true
            }
        }),
        true
    );
});

test("guards leaving dirty test oracles settings panel", () => {
    assert.equal(
        shouldGuardConfigurationTransition({
            currentPage: "oracles",
            currentEditor: "settings-form",
            nextEditor: "oracle-panel:active-oracles",
            dirtyAreas: {
                settings: true
            }
        }),
        true
    );
});

test("does not guard navigating into settings after already leaving the settings area", () => {
    assert.equal(
        shouldGuardConfigurationTransition({
            currentPage: "configuration",
            currentEditor: "java-composition",
            nextEditor: "test-settings",
            dirtyAreas: {
                settings: true
            }
        }),
        false
    );
});

test("guards leaving dirty composition properties for Java composition flow", () => {
    assert.equal(
        shouldGuardConfigurationTransition({
            currentPage: "configuration",
            currentEditor: "composition-properties",
            nextEditor: "java-composition",
            dirtyAreas: {
                "composition-file": true
            }
        }),
        true
    );
});

test("guards leaving dirty split composition page", () => {
    assert.equal(
        shouldGuardConfigurationTransition({
            currentPage: "composition",
            currentEditor: "java-composition",
            nextEditor: "__leave__",
            dirtyAreas: {
                "composition-flow": true
            }
        }),
        true
    );
});

test("guards leaving dirty Java policies for policies properties", () => {
    assert.equal(
        shouldGuardConfigurationTransition({
            currentPage: "configuration",
            currentEditor: "java-policies",
            nextEditor: "policies-properties",
            dirtyAreas: {
                "policies-flow": true
            }
        }),
        true
    );
});

test("guards leaving dirty split policies page", () => {
    assert.equal(
        shouldGuardConfigurationTransition({
            currentPage: "policies",
            currentEditor: "java-policies",
            nextEditor: "__leave__",
            dirtyAreas: {
                "policies-flow": true
            }
        }),
        true
    );
});

test("guards closing a dirty Java composition source editor", () => {
    assert.equal(
        shouldGuardConfigurationTransition({
            currentPage: "configuration",
            currentEditor: "java-composition",
            nextEditor: "__close-composition-source__",
            dirtyAreas: {
                "composition-flow": true
            }
        }),
        true
    );
});

test("guards closing a dirty Java policy source editor", () => {
    assert.equal(
        shouldGuardConfigurationTransition({
            currentPage: "configuration",
            currentEditor: "java-policies",
            nextEditor: "__close-policy-source__",
            dirtyAreas: {
                "policies-flow": true
            }
        }),
        true
    );
});

test("guards leaving dirty oracle source editor", () => {
    assert.equal(
        shouldGuardConfigurationTransition({
            currentPage: "oracles",
            currentEditor: "oracle-source",
            nextEditor: "__leave__",
            dirtyAreas: {
                "oracle-source": true
            }
        }),
        true
    );
});

test("builds configuration dirty areas consistently", () => {
    assert.deepEqual(
        configurationDirtyAreas({
            settingsDirty: true,
            compositionFlowDirty: true,
            oracleSourceDirty: true
        }),
        {
            settings: true,
            "composition-file": false,
            "composition-flow": true,
            "policies-file": false,
            "policies-flow": false,
            "oracle-source": true
        }
    );
});

test("detects settings editors", () => {
    assert.equal(settingsEditorSelected("settings-form"), true);
    assert.equal(settingsEditorSelected("test-settings"), true);
    assert.equal(settingsEditorSelected("java-composition"), false);
});

test("builds guard dialog details for configuration editors", () => {
    assert.deepEqual(guardDialogDetails("settings-form"), {
        title: "Unsaved Settings Changes",
        message: "Detected unsaved settings changes. Save them before continuing, discard them, or cancel this navigation.",
        saveLabel: "Save"
    });
    assert.deepEqual(guardDialogDetails("java-composition"), {
        title: "Unsaved and Uncompiled Composition Changes",
        message: "Detected unsaved Java composition changes. Save and compile before continuing, discard them, or cancel this navigation.",
        saveLabel: "Save and Compile"
    });
    assert.deepEqual(guardDialogDetails("oracle-source", { category: "dsl-oracle" }), {
        title: "Unsaved Oracle Source Changes",
        message: "Detected unsaved oracle source changes. Save them before continuing, discard them, or cancel this navigation.",
        saveLabel: "Save and Generate Java-DSL"
    });
});

test("builds guard dialog details for CLI agent and Test Goal changes", () => {
    assert.deepEqual(cliAgentSettingsGuardDetails(), {
        kind: "cli-agent",
        title: "Unsaved Agent CLI Settings",
        message: "Detected unsaved Agent CLI settings. Save them before continuing, discard them, or cancel this navigation.",
        saveLabel: "Save"
    });
    assert.deepEqual(testGoalGuardDetails(), {
        kind: "test-goal",
        title: "Unsaved Test Goal Changes",
        message: "Detected unsaved Test Goal changes. Save them before continuing, discard them, or cancel this navigation.",
        saveLabel: "Save"
    });
});

test("builds pending guard state", () => {
    const action = () => "saved";

    assert.deepEqual(initialPendingGuardState(), {
        action: null,
        kind: "configuration"
    });
    assert.deepEqual(pendingGuardState(action, "test-goal"), {
        action,
        kind: "test-goal"
    });
});

test("resolves guard save target", () => {
    assert.equal(guardSaveTarget("cli-agent"), "cli-agent");
    assert.equal(guardSaveTarget("test-goal"), "test-goal");
    assert.equal(guardSaveTarget("configuration"), "configuration");
    assert.equal(guardSaveTarget("unknown"), "configuration");
});
