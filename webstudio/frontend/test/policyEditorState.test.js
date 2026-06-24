import test from "node:test";
import assert from "node:assert/strict";
import {
    clearSelectedSourceState,
    policySourceModalVisible
} from "../src/policyEditorState.js";

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

    const nextState = clearSelectedSourceState(currentState);

    assert.equal(nextState.selectedSourceName, "");
    assert.equal(nextState.selectedSourceFile, null);
    assert.equal(nextState.javaCompileResult, null);
    assert.equal(policySourceModalVisible(nextState.selectedSourceFile), false);
});
