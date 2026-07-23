export function stateModelWorkspaceDialog(selectedWorkspaceName) {
    if (!selectedWorkspaceName) {
        return {
            title: "Unable To Open State Model",
            message: "Dear user, select a TESTAR workspace before opening the state model analysis."
        };
    }

    return null;
}
