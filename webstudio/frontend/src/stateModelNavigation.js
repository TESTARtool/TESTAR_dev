export function stateModelWorkspaceDialog(selectedWorkspaceName, workspaceAvailable) {
    if (!selectedWorkspaceName) {
        return {
            title: "Unable To Open State Model",
            message: "Dear user, select a TESTAR workspace before opening the state model analysis."
        };
    }

    if (!workspaceAvailable) {
        return {
            title: "Unable To Open State Model",
            message: `Dear user, the selected workspace "${selectedWorkspaceName}" is not available in the shared TESTAR runtime. Select a workspace with a generated state model before opening the analysis mode.`
        };
    }

    return null;
}
