function postOptions(body = null) {
    if (body === null) {
        return {
            method: "POST"
        };
    }

    return {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(body)
    };
}

function encodedRuntimePathSegment(value) {
    return encodeURIComponent(value || "");
}

export async function loadScriptlessStatusRequest(loadJson) {
    return loadJson("/api/execution/status/scriptless");
}

export async function loadCliStatusRequest(loadJson) {
    return loadJson("/api/execution/status/cli");
}

export async function loadRemoteSpyStatusRequest(loadJson) {
    return loadJson("/api/spy/status");
}

export async function startGenerateRequest(loadJson, workspaceName) {
    return loadJson(
        `/api/execution/scriptless/generate/${encodedRuntimePathSegment(workspaceName)}`,
        postOptions()
    );
}

export async function stopScriptlessRequest(loadJson) {
    return loadJson("/api/execution/scriptless/stop", postOptions());
}

export async function startRemoteSpyRequest(loadJson, workspaceName) {
    return loadJson(
        `/api/spy/start/${encodedRuntimePathSegment(workspaceName)}`,
        postOptions()
    );
}

export async function refreshRemoteSpyRequest(loadJson) {
    return loadJson("/api/spy/refresh", postOptions());
}

export async function stopRemoteSpyRequest(loadJson) {
    return loadJson("/api/spy/stop", postOptions());
}

export async function executeSpyActionRequest(loadJson, actionId) {
    return loadJson(
        `/api/spy/actions/${encodedRuntimePathSegment(actionId)}`,
        postOptions()
    );
}

export async function executeSpyWidgetDefaultActionRequest(loadJson, widgetId) {
    return loadJson(
        `/api/spy/widgets/${encodedRuntimePathSegment(widgetId)}/default-action`,
        postOptions()
    );
}

export async function startLocalSpyRequest(loadJson, workspaceName) {
    return loadJson(
        `/api/execution/scriptless/local-spy/${encodedRuntimePathSegment(workspaceName)}`,
        postOptions()
    );
}

export async function executeSpyWidgetDirectTypeRequest(loadJson, widgetId, text) {
    return loadJson(
        `/api/spy/widgets/${encodedRuntimePathSegment(widgetId)}/direct-type`,
        postOptions({ text })
    );
}

export async function startCliManualSessionRequest(loadJson, workspaceName) {
    return loadJson(
        `/api/execution/cli/manual/start/${encodedRuntimePathSegment(workspaceName)}`,
        postOptions({})
    );
}

export async function startCliAgentSessionRequest(loadJson, workspaceName) {
    return loadJson(
        `/api/execution/cli/agent/start/${encodedRuntimePathSegment(workspaceName)}`,
        postOptions({})
    );
}

export async function runCliManualCommandRequest(loadJson, commandLine) {
    return loadJson("/api/execution/cli/manual/command", postOptions({ commandLine }));
}

export async function stopCliManualSessionRequest(loadJson) {
    return loadJson("/api/execution/cli/manual/stop", postOptions());
}

export async function stopCliAgentSessionRequest(loadJson) {
    return loadJson("/api/execution/cli/agent/stop", postOptions());
}
