import test from "node:test";
import assert from "node:assert/strict";
import {
    contentChanged,
    objectChanged,
    objectSnapshot
} from "../src/editorDirtyState.js";

test("detects changed text content for raw editor save buttons", () => {
    assert.equal(contentChanged("A = 1\n", "A = 1\n"), false);
    assert.equal(contentChanged("A = 2\n", "A = 1\n"), true);
});

test("treats missing text content as empty text", () => {
    assert.equal(contentChanged(null, ""), false);
    assert.equal(contentChanged(undefined, ""), false);
    assert.equal(contentChanged("value", null), true);
});

test("detects changed object content for Agent CLI save button", () => {
    assert.equal(
        objectChanged({ model: "gpt-5.4-mini" }, { model: "gpt-5.4-mini" }),
        false
    );
    assert.equal(
        objectChanged({ model: "gpt-5.4-mini" }, { model: "gpt-5.4" }),
        true
    );
});

test("snapshots object content so later edits do not mutate the saved baseline", () => {
    const currentSettings = { model: "gpt-5.4-mini" };
    const savedSettings = objectSnapshot(currentSettings);

    currentSettings.model = "gpt-5.4";

    assert.equal(savedSettings.model, "gpt-5.4-mini");
    assert.equal(objectChanged(currentSettings, savedSettings), true);
});
