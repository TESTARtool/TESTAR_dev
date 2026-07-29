// Implements WS-FUNC-WORKSPACE-SOURCE-EDITOR-001, WS-UX-SOURCE-EDITOR-001, WS-FUNC-COMPOSITION-FLOW-001, WS-UX-COMPOSITION-FLOW-001, and WS-UX-POLICIES-001:
// keeps selected composition/policy source editors consistent while switching editors.
export function clearedSourceSelectionState(state = {}) {
    return {
        ...state,
        selectedSourceName: "",
        selectedSourceFile: null,
        javaCompileResult: null
    };
}

export function openedEditorSelectionState(state = {}, editorId) {
    const nextState = {
        ...clearedSourceSelectionState(state),
        selectedEditor: editorId
    };

    if (editorId !== "java-composition") {
        nextState.selectedCompositionFlowNode = null;
    }

    return nextState;
}

export function selectedSettingsGroupForEditor(workspaceDocument, requestedGroupId, currentGroupId) {
    const settingsGroups = workspaceDocument?.settingsGroups || [];
    if (settingsGroups.length === 0) {
        return "";
    }

    const preferredGroupId = requestedGroupId || currentGroupId;
    const matchingSettingsGroup = settingsGroups.find((settingsGroup) => settingsGroup.id === preferredGroupId);
    if (matchingSettingsGroup) {
        return preferredGroupId;
    }

    return settingsGroups[0].id;
}

export function selectedAllowedSettingsGroupId(workspaceDocument, allowedSettingsGroupIds, currentGroupId) {
    const allowedIds = allowedSettingsGroupIds || [];
    if (allowedIds.includes(currentGroupId)) {
        return currentGroupId;
    }

    const availableGroupIds = new Set((workspaceDocument?.settingsGroups || []).map((settingsGroup) => settingsGroup.id));
    return allowedIds.find((groupId) => availableGroupIds.has(groupId))
        || workspaceDocument?.settingsGroups?.[0]?.id
        || "";
}

export function currentEditorDocumentDescriptor(state = {}) {
    if (!state.workspaceDocument) {
        return null;
    }

    if (state.selectedEditor === "test-settings") {
        return {
            kind: "test-settings",
            title: "Edit Settings",
            saveLabel: "Save Settings"
        };
    }

    if (state.selectedEditor === "settings-form") {
        return {
            kind: "settings-form",
            title: "Edit Settings",
            saveLabel: "Save Settings"
        };
    }

    if (state.selectedEditor === "policies-properties") {
        return {
            kind: "policies-properties",
            title: "Edit policies.properties",
            saveLabel: "Save policies.properties"
        };
    }

    if (state.selectedEditor === "composition-properties") {
        return {
            kind: "composition-properties",
            title: "Edit composition.properties",
            saveLabel: "Save composition.properties"
        };
    }

    if (state.selectedEditor !== "java-policies"
        && state.selectedEditor !== "java-composition"
        && state.selectedSourceFile) {
        return {
            kind: "source",
            title: state.selectedSourceFile.name,
            saveLabel: "Save source",
            sourceCategory: state.selectedSourceFile.category
        };
    }

    return null;
}
