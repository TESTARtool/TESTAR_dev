import {
    workspaceCreateRequest,
    workspaceRenameRequest
} from "./workspaceManagementModel.js";

// Implements WS-FUNC-WORKSPACE-MANAGEMENT-001: request builders for workspace create/rename endpoints.
function jsonRequestOptions(method, body) {
    return {
        method,
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(body)
    };
}

export async function createWorkspaceRequest(loadJson, draft) {
    const request = workspaceCreateRequest(draft);
    const workspace = await loadJson("/api/workspaces", jsonRequestOptions("POST", request));
    return {
        request,
        workspace
    };
}

export async function renameWorkspaceRequest(loadJson, currentWorkspaceName, draft) {
    const request = workspaceRenameRequest(draft);
    const encodedWorkspaceName = encodeURIComponent(currentWorkspaceName || "");
    const workspace = await loadJson(
        `/api/workspaces/${encodedWorkspaceName}/rename`,
        jsonRequestOptions("PUT", request)
    );
    return {
        request,
        workspace
    };
}
