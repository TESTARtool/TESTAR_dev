// Verifies WS-FUNC-CONFIG-GUARD-001: dirty-state checks for text and object-backed editors.
import test from "node:test";
import assert from "node:assert/strict";
import {
    contentChanged,
    objectChanged,
    objectSnapshot,
    settingsChanged
} from "../../src/models/editorDirtyState.js";

test("detects changed text content for raw editor save buttons", () => {
    assert.equal(contentChanged("A = 1\n", "A = 1\n"), false);
    assert.equal(contentChanged("A = 2\n", "A = 1\n"), true);
});

test("treats missing text content as empty text", () => {
    assert.equal(contentChanged(null, ""), false);
    assert.equal(contentChanged(undefined, ""), false);
    assert.equal(contentChanged("value", null), true);
});

test("detects visual settings changes even when raw content is unchanged", () => {
    assert.equal(settingsChanged("A = 1\n", "A = 1\n", false), false);
    assert.equal(settingsChanged("A = 1\n", "A = 1\n", true), true);
    assert.equal(settingsChanged("A = 2\n", "A = 1\n", false), true);
});

test("detects changed object content for Agent CLI save button", () => {
    assert.equal(
        objectChanged({ model: "gpt-6-luna" }, { model: "gpt-6-luna" }),
        false
    );
    assert.equal(
        objectChanged({ model: "gpt-6-luna" }, { model: "gpt-6" }),
        true
    );
});

test("snapshots object content so later edits do not mutate the saved baseline", () => {
    const currentSettings = { model: "gpt-6-luna" };
    const savedSettings = objectSnapshot(currentSettings);

    currentSettings.model = "gpt-6";

    assert.equal(savedSettings.model, "gpt-6-luna");
    assert.equal(objectChanged(currentSettings, savedSettings), true);
});
