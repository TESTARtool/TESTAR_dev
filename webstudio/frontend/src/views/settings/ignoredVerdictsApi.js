export function ignoredVerdictsUrl(workspaceName) {
    return `/api/workspaces/${encodeURIComponent(workspaceName)}/ignored-verdicts`;
}

export function loadIgnoredVerdicts(loadJson, workspaceName) {
    return loadJson(ignoredVerdictsUrl(workspaceName));
}

export function removeIgnoredVerdicts(loadJson, workspaceName, selected) {
    return loadJson(`${ignoredVerdictsUrl(workspaceName)}/remove`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(selected)
    });
}

export function clearIgnoredVerdicts(loadJson, workspaceName) {
    return loadJson(ignoredVerdictsUrl(workspaceName), { method: "DELETE" });
}
