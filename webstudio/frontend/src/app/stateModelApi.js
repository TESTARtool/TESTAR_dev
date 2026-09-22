export function loadStateModelStatusRequest(loadJson) {
    return loadJson("/api/statemodel/status");
}

export function openStateModelRequest(loadJson, workspaceName) {
    return loadJson(`/api/statemodel/open/${encodeURIComponent(workspaceName)}`, {
        method: "POST"
    });
}

export function stopStateModelRequest(loadJson) {
    return loadJson("/api/statemodel/stop", {
        method: "POST"
    });
}
