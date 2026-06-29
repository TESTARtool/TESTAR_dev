export function canStartRuntimeMode({
    selectedWorkspaceName = "",
    saving = false,
    running = false
} = {}) {
    return Boolean(selectedWorkspaceName) && !saving && !running;
}
