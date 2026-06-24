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

    return "other";
}

export function shouldGuardConfigurationTransition({
    currentPage,
    currentEditor,
    nextEditor,
    dirtyAreas = {}
}) {
    if (currentPage !== "configuration") {
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
