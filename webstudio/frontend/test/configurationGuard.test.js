import test from "node:test";
import assert from "node:assert/strict";
import { shouldGuardConfigurationTransition } from "../src/configurationGuard.js";

test("guards switching from Edit test.settings to Edit Settings when settings are dirty", () => {
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

test("does not guard switching from Edit test.settings to Edit Settings when settings are clean", () => {
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

test("guards switching from Edit Settings to Edit test.settings when settings are dirty", () => {
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
