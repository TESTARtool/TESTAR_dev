// Keeps selected composition/policy source editors consistent while switching editors.
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

export function currentEditorDocumentState({
    descriptor,
    settingsDirty = false,
    policiesPropertiesDirty = false,
    compositionPropertiesDirty = false,
    selectedSourceDirty = false,
    saveCurrentSettingsEditor = null,
    saveVisualSettings = null,
    savePoliciesProperties = null,
    saveCompositionProperties = null,
    saveSelectedSource = null
} = {}) {
    if (!descriptor) {
        return null;
    }

    if (descriptor.kind === "test-settings") {
        return {
            ...descriptor,
            dirty: settingsDirty,
            save: saveCurrentSettingsEditor
        };
    }

    if (descriptor.kind === "settings-form") {
        return {
            ...descriptor,
            dirty: settingsDirty,
            save: saveVisualSettings
        };
    }

    if (descriptor.kind === "policies-properties") {
        return {
            ...descriptor,
            dirty: policiesPropertiesDirty,
            save: savePoliciesProperties
        };
    }

    if (descriptor.kind === "composition-properties") {
        return {
            ...descriptor,
            dirty: compositionPropertiesDirty,
            save: saveCompositionProperties
        };
    }

    if (descriptor.kind === "source") {
        return {
            ...descriptor,
            dirty: selectedSourceDirty,
            save: saveSelectedSource
        };
    }

    return null;
}
