// Verifies WS-FUNC-TEST-SETTINGS-001 and WS-UX-TEST-SETTINGS-001:
// visual setting persistence, restore defaults, and setting feedback state.
import test from "node:test";
import assert from "node:assert/strict";
import {
    buildTestSettingsContent,
    canRestoreSettingDefault,
    restoredSettingDefaultValue,
    shouldPersistVisualSetting,
    updatedRegexValidationResults
} from "../../../src/views/settings/settingsEditorModel.js";

test("persists non-empty settings and empty string or list settings", () => {
    assert.equal(shouldPersistVisualSetting({ value: "abc", type: "boolean" }), true);
    assert.equal(shouldPersistVisualSetting({ value: "", type: "string" }), true);
    assert.equal(shouldPersistVisualSetting({ value: "", type: "list" }), true);
    assert.equal(shouldPersistVisualSetting({ value: "", type: "boolean" }), false);
});

test("builds test.settings content from visual settings", () => {
    const content = buildTestSettingsContent(
        "SUTConnector = webdriver\nBrowserFullScreen = true\n",
        [
            {
                settings: [
                    { key: "SUTConnector", value: "android", type: "string" },
                    { key: "BrowserFullScreen", value: "", type: "boolean" },
                    { key: "OutputDir", value: "./output", type: "string" }
                ]
            }
        ]
    );

    assert.equal(content, "SUTConnector = android\n\nOutputDir = ./output\n");
});

test("protects non-empty suspicious oracle settings from restore default", () => {
    assert.equal(canRestoreSettingDefault({ key: "SuspiciousTags", value: "error" }), false);
    assert.equal(canRestoreSettingDefault({ key: "SuspiciousTags", value: "" }), true);
    assert.equal(canRestoreSettingDefault({ key: "ReportInHTML", value: "false" }), true);
});

test("builds restored setting default value", () => {
    assert.equal(
        restoredSettingDefaultValue({ key: "ReportInHTML", value: "false", defaultValue: "true" }),
        "true"
    );
    assert.equal(
        restoredSettingDefaultValue({ key: "SuspiciousTags", value: "error", defaultValue: "warning" }),
        "error"
    );
});

test("updates regex validation results immutably", () => {
    assert.deepEqual(
        updatedRegexValidationResults({ Existing: { valid: true } }, "Pattern", { valid: false }),
        {
            Existing: { valid: true },
            Pattern: { valid: false }
        }
    );
});
