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
