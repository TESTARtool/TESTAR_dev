// Verifies WS-FUNC-WORKSPACE-SOURCE-EDITOR-001, WS-UX-SOURCE-EDITOR-001, WS-UX-COMPOSITION-FLOW-001, and WS-UX-POLICIES-001:
// selected composition node and source editor state transitions.
import test from "node:test";
import assert from "node:assert/strict";
import {
    clearedSourceSelectionState,
    currentEditorDocumentDescriptor,
    openedEditorSelectionState,
    selectedAllowedSettingsGroupId,
    selectedSettingsGroupForEditor
} from "../../src/models/editorSelectionModel.js";

test("clears selected source state", () => {
    const nextState = clearedSourceSelectionState({
        selectedSourceName: "CustomService.java",
        selectedSourceFile: { name: "CustomService.java" },
        javaCompileResult: { success: true },
        selectedEditor: "java-composition"
    });

    assert.equal(nextState.selectedSourceName, "");
    assert.equal(nextState.selectedSourceFile, null);
    assert.equal(nextState.javaCompileResult, null);
    assert.equal(nextState.selectedEditor, "java-composition");
});

test("opening a non-composition editor clears the selected composition flow node", () => {
    const nextState = openedEditorSelectionState({
        selectedSourceName: "CustomService.java",
        selectedSourceFile: { name: "CustomService.java" },
        selectedCompositionFlowNode: { id: "system" },
        javaCompileResult: { success: true }
    }, "settings-form");

    assert.equal(nextState.selectedEditor, "settings-form");
    assert.equal(nextState.selectedSourceName, "");
    assert.equal(nextState.selectedSourceFile, null);
    assert.equal(nextState.selectedCompositionFlowNode, null);
    assert.equal(nextState.javaCompileResult, null);
});

test("opening the Java composition editor keeps the selected composition flow node", () => {
    const nextState = openedEditorSelectionState({
        selectedCompositionFlowNode: { id: "system" }
    }, "java-composition");

    assert.equal(nextState.selectedEditor, "java-composition");
    assert.deepEqual(nextState.selectedCompositionFlowNode, { id: "system" });
});

test("selects requested settings group when available", () => {
    const workspaceDocument = {
        settingsGroups: [
            { id: "sut" },
            { id: "webdriver" }
        ]
    };

    assert.equal(selectedSettingsGroupForEditor(workspaceDocument, "webdriver", "sut"), "webdriver");
    assert.equal(selectedSettingsGroupForEditor(workspaceDocument, "", "webdriver"), "webdriver");
    assert.equal(selectedSettingsGroupForEditor(workspaceDocument, "missing", ""), "sut");
    assert.equal(selectedSettingsGroupForEditor({ settingsGroups: [] }, "missing", ""), "");
});

test("selects allowed settings groups using current group before fallback", () => {
    const workspaceDocument = {
        settingsGroups: [
            { id: "sut" },
            { id: "execution" },
            { id: "webdriver" }
        ]
    };

    assert.equal(
        selectedAllowedSettingsGroupId(workspaceDocument, ["sut", "execution"], "execution"),
        "execution"
    );
    assert.equal(
        selectedAllowedSettingsGroupId(workspaceDocument, ["webdriver"], "execution"),
        "webdriver"
    );
    assert.equal(
        selectedAllowedSettingsGroupId(workspaceDocument, ["missing"], "execution"),
        "sut"
    );
    assert.equal(
        selectedAllowedSettingsGroupId({ settingsGroups: [] }, ["missing"], ""),
        ""
    );
});

test("does not build an editor document descriptor without a workspace document", () => {
    assert.equal(currentEditorDocumentDescriptor({
        workspaceDocument: null,
        selectedEditor: "test-settings"
    }), null);
});

test("builds settings editor document descriptors", () => {
    const workspaceDocument = {};

    assert.deepEqual(currentEditorDocumentDescriptor({
        workspaceDocument,
        selectedEditor: "test-settings"
    }), {
        kind: "test-settings",
        title: "Edit Settings",
        saveLabel: "Save Settings"
    });
    assert.deepEqual(currentEditorDocumentDescriptor({
        workspaceDocument,
        selectedEditor: "settings-form"
    }), {
        kind: "settings-form",
        title: "Edit Settings",
        saveLabel: "Save Settings"
    });
});

test("builds properties editor document descriptors", () => {
    const workspaceDocument = {};

    assert.deepEqual(currentEditorDocumentDescriptor({
        workspaceDocument,
        selectedEditor: "policies-properties"
    }), {
        kind: "policies-properties",
        title: "Edit policies.properties",
        saveLabel: "Save policies.properties"
    });
    assert.deepEqual(currentEditorDocumentDescriptor({
        workspaceDocument,
        selectedEditor: "composition-properties"
    }), {
        kind: "composition-properties",
        title: "Edit composition.properties",
        saveLabel: "Save composition.properties"
    });
});

test("builds generic source editor document descriptors", () => {
    assert.deepEqual(currentEditorDocumentDescriptor({
        workspaceDocument: {},
        selectedEditor: "source:CustomPolicy.java",
        selectedSourceFile: {
            name: "CustomPolicy.java",
            category: "policy"
        }
    }), {
        kind: "source",
        title: "CustomPolicy.java",
        saveLabel: "Save source",
        sourceCategory: "policy"
    });
});

test("does not build generic source descriptor for Java composition and policy landing editors", () => {
    assert.equal(currentEditorDocumentDescriptor({
        workspaceDocument: {},
        selectedEditor: "java-policies",
        selectedSourceFile: {
            name: "CustomPolicy.java",
            category: "policy"
        }
    }), null);
    assert.equal(currentEditorDocumentDescriptor({
        workspaceDocument: {},
        selectedEditor: "java-composition",
        selectedSourceFile: {
            name: "CustomService.java",
            category: "service"
        }
    }), null);
});
