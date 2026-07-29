// Implements WS-FUNC-CONFIG-GUARD-001 and WS-UX-CONFIG-GUARD-001:
// centralizes unsaved-change guard decisions and dialog copy for configuration editors.
export function configurationEditorArea(editorId) {
    if (editorId === "test-settings" || editorId === "settings-form") {
        return "settings";
    }

    if (editorId === "composition-properties") {
        return "composition-file";
    }

    if (editorId === "java-composition") {
        return "composition-flow";
    }

    if (editorId === "policies-properties") {
        return "policies-file";
    }

    if (editorId === "java-policies") {
        return "policies-flow";
    }

    if (editorId === "oracle-source") {
        return "oracle-source";
    }

    return "other";
}

export function shouldGuardConfigurationTransition({
    currentPage,
    currentEditor,
    nextEditor,
    dirtyAreas = {}
}) {
    const configurationPages = new Set([
        "configuration",
        "basic-settings",
        "oracles",
        "settings",
        "composition",
        "policies"
    ]);

    if (!configurationPages.has(currentPage)) {
        return false;
    }

    const currentArea = configurationEditorArea(currentEditor);
    if (currentArea === "other") {
        return false;
    }

    if (!dirtyAreas[currentArea]) {
        return false;
    }

    if (currentEditor === nextEditor) {
        return false;
    }

    return true;
}

export function configurationDirtyAreas({
    settingsDirty = false,
    compositionPropertiesDirty = false,
    compositionFlowDirty = false,
    policiesPropertiesDirty = false,
    policiesFlowDirty = false,
    oracleSourceDirty = false
} = {}) {
    return {
        settings: settingsDirty,
        "composition-file": compositionPropertiesDirty,
        "composition-flow": compositionFlowDirty,
        "policies-file": policiesPropertiesDirty,
        "policies-flow": policiesFlowDirty,
        "oracle-source": oracleSourceDirty
    };
}

export function settingsEditorSelected(editorId) {
    return editorId === "settings-form" || editorId === "test-settings";
}

export function guardDialogDetails(editorId, selectedOracleSourceFile = null) {
    if (settingsEditorSelected(editorId)) {
        return {
            title: "Unsaved Settings Changes",
            message: "Detected unsaved settings changes. Save them before continuing, discard them, or cancel this navigation.",
            saveLabel: "Save"
        };
    }

    if (editorId === "composition-properties") {
        return {
            title: "Unsaved Composition File Changes",
            message: "Detected unsaved composition.properties changes. Save them before continuing, discard them, or cancel this navigation.",
            saveLabel: "Save"
        };
    }

    if (editorId === "policies-properties") {
        return {
            title: "Unsaved Policies File Changes",
            message: "Detected unsaved policies.properties changes. Save them before continuing, discard them, or cancel this navigation.",
            saveLabel: "Save"
        };
    }

    if (editorId === "java-composition") {
        return {
            title: "Unsaved and Uncompiled Composition Changes",
            message: "Detected unsaved Java composition changes. Save and compile before continuing, discard them, or cancel this navigation.",
            saveLabel: "Save and Compile"
        };
    }

    if (editorId === "java-policies") {
        return {
            title: "Unsaved and Uncompiled Policy Changes",
            message: "Detected unsaved Java policy changes. Save and compile before continuing, discard them, or cancel this navigation.",
            saveLabel: "Save and Compile"
        };
    }

    if (editorId === "oracle-source") {
        return {
            title: "Unsaved Oracle Source Changes",
            message: "Detected unsaved oracle source changes. Save them before continuing, discard them, or cancel this navigation.",
            saveLabel: selectedOracleSourceFile?.category === "dsl-oracle"
                ? "Save and Generate Java-DSL"
                : "Save"
        };
    }

    return {
        title: "Unsaved Changes",
        message: "Detected unsaved changes. Save them before continuing, discard them, or cancel this navigation.",
        saveLabel: "Save"
    };
}

export function cliAgentSettingsGuardDetails() {
    return {
        kind: "cli-agent",
        title: "Unsaved Agent CLI Settings",
        message: "Detected unsaved Agent CLI settings. Save them before continuing, discard them, or cancel this navigation.",
        saveLabel: "Save"
    };
}

export function testGoalGuardDetails() {
    return {
        kind: "test-goal",
        title: "Unsaved Test Goal Changes",
        message: "Detected unsaved Test Goal changes. Save them before continuing, discard them, or cancel this navigation.",
        saveLabel: "Save"
    };
}

export function initialPendingGuardState() {
    return {
        action: null,
        kind: "configuration"
    };
}

export function pendingGuardState(action, kind = "configuration") {
    return {
        action,
        kind
    };
}

export function guardSaveTarget(pendingGuardKind) {
    if (pendingGuardKind === "cli-agent") {
        return "cli-agent";
    }

    if (pendingGuardKind === "test-goal") {
        return "test-goal";
    }

    return "configuration";
}
