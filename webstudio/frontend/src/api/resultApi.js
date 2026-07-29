// Implements WS-FUNC-TEST-RESULTS-001: workspace-scoped Test Results API request paths.
export function resultWorkspaceQuery(workspaceName) {
    return `workspace=${encodeURIComponent(workspaceName || "")}`;
}

export function resultListUrl(workspaceName) {
    return `/api/execution/scriptless/results?${resultWorkspaceQuery(workspaceName)}`;
}

export function resultFileUrl(workspaceName, resultFile) {
    return `/api/execution/scriptless/results/${resultFile.name}?${resultWorkspaceQuery(workspaceName)}&path=${encodeURIComponent(resultFile.path)}`;
}

export function resultGroupDeleteUrl(workspaceName, resultGroup) {
    return `/api/execution/scriptless/result-groups?${resultWorkspaceQuery(workspaceName)}&path=${encodeURIComponent(resultGroup.path)}`;
}

export async function loadResultListRequest(loadJson, workspaceName) {
    return loadJson(resultListUrl(workspaceName));
}

export async function loadResultFileRequest(loadJson, workspaceName, resultFile) {
    return loadJson(resultFileUrl(workspaceName, resultFile));
}

export async function deleteResultFileRequest(loadJson, workspaceName, resultFile) {
    return loadJson(resultFileUrl(workspaceName, resultFile), {
        method: "DELETE"
    });
}

export async function deleteResultGroupRequest(loadJson, workspaceName, resultGroup) {
    return loadJson(resultGroupDeleteUrl(workspaceName, resultGroup), {
        method: "DELETE"
    });
}
