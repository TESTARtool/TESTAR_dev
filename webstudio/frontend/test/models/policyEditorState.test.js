// Verifies WS-UX-POLICIES-001: Java policy source modal visibility follows selected source state.
import test from "node:test";
import assert from "node:assert/strict";
import { policySourceModalVisible } from "../../src/models/policyEditorState.js";
import { clearedSourceSelectionState } from "../../src/models/editorSelectionModel.js";

test("clearing the selected policy source hides the policy source modal", () => {
    const currentState = {
        selectedSourceName: "WebdriverCanvasVisiblePolicy.java",
        selectedSourceFile: {
            name: "WebdriverCanvasVisiblePolicy.java",
            category: "policy"
        },
        javaCompileResult: {
            scope: "source",
            success: true
        }
    };

    assert.equal(policySourceModalVisible(currentState.selectedSourceFile), true);

    const nextState = clearedSourceSelectionState(currentState);

    assert.equal(nextState.selectedSourceName, "");
    assert.equal(nextState.selectedSourceFile, null);
    assert.equal(nextState.javaCompileResult, null);
    assert.equal(policySourceModalVisible(nextState.selectedSourceFile), false);
});
