// Workspace source, configuration property file, and compile API requests.
export function workspaceSourceUrl(workspaceName, sourceName) {
    return `/api/workspaces/${workspaceName}/sources/${encodeURIComponent(sourceName)}`;
}

export function workspaceDocumentUrl(workspaceName) {
    return `/api/workspaces/${workspaceName}`;
}

export function workspaceCompositionPropertiesUrl(workspaceName) {
    return `/api/workspaces/${workspaceName}/composition-properties`;
}

export function workspacePoliciesPropertiesUrl(workspaceName) {
    return `/api/workspaces/${workspaceName}/policies-properties`;
}

export function workspaceSourceCompileUrl(workspaceName, sourceName) {
    return `/api/workspaces/${workspaceName}/sources/${encodeURIComponent(sourceName)}/compile`;
}

export function compileWorkspaceProfileUrl(workspaceName) {
    return `/api/workspaces/${workspaceName}/compile-profile`;
}

export function workspaceCompositionModuleSourceUrl(workspaceName, propertyKey) {
    return `/api/workspaces/${workspaceName}/composition/modules/${encodeURIComponent(propertyKey)}/source`;
}

export function workspacePolicySourceUrl(workspaceName, propertyKey) {
    return `/api/workspaces/${workspaceName}/policies/${encodeURIComponent(propertyKey)}/source`;
}

export async function loadWorkspaceDocumentRequest(loadJson, workspaceName) {
    return loadJson(workspaceDocumentUrl(workspaceName));
}

export async function saveWorkspaceCompositionPropertiesRequest(loadJson, workspaceName, content) {
    return saveWorkspaceFileContentRequest(loadJson, workspaceCompositionPropertiesUrl(workspaceName), content);
}

export async function saveWorkspacePoliciesPropertiesRequest(loadJson, workspaceName, content) {
    return saveWorkspaceFileContentRequest(loadJson, workspacePoliciesPropertiesUrl(workspaceName), content);
}

export async function loadWorkspaceSourceRequest(loadJson, workspaceName, sourceName) {
    return loadJson(workspaceSourceUrl(workspaceName, sourceName));
}

export async function saveWorkspaceSourceRequest(loadJson, workspaceName, sourceName, content) {
    return loadJson(workspaceSourceUrl(workspaceName, sourceName), {
        method: "PUT",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ content })
    });
}

export async function compileWorkspaceSourceRequest(loadJson, workspaceName, sourceName) {
    return loadJson(workspaceSourceCompileUrl(workspaceName, sourceName), {
        method: "POST"
    });
}

export async function compileWorkspaceProfileRequest(loadJson, workspaceName) {
    return loadJson(compileWorkspaceProfileUrl(workspaceName), {
        method: "POST"
    });
}

export async function createCompositionModuleSourceRequest(loadJson, workspaceName, propertyKey) {
    return loadJson(workspaceCompositionModuleSourceUrl(workspaceName, propertyKey), {
        method: "POST"
    });
}

export async function createPolicySourceRequest(loadJson, workspaceName, propertyKey) {
    return loadJson(workspacePolicySourceUrl(workspaceName, propertyKey), {
        method: "POST"
    });
}

function saveWorkspaceFileContentRequest(loadJson, url, content) {
    return loadJson(url, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ content })
    });
}
