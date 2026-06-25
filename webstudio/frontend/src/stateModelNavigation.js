export function stateModelWorkspaceDialog(selectedWorkspaceName, workspaceAvailableInTestar, workspaceAvailableInCli) {
    if (!selectedWorkspaceName) {
        return {
            title: "Unable To Open State Model",
            message: "Dear user, select a TESTAR workspace before opening the state model analysis."
        };
    }

    if (!workspaceAvailableInTestar && !workspaceAvailableInCli) {
        return {
            title: "Unable To Open State Model",
            message: `Dear user, the selected workspace "${selectedWorkspaceName}" is not available in the TESTAR or CLI runtime. Select a workspace with a generated state model before opening the analysis mode.`
        };
    }

    return null;
}

export function stateModelRuntime(currentPage, workspaceAvailableInTestar, workspaceAvailableInCli) {
    if (currentPage === "cli" && workspaceAvailableInCli) {
        return "cli";
    }

    if (workspaceAvailableInTestar) {
        return "testar";
    }

    if (workspaceAvailableInCli) {
        return "cli";
    }

    return "";
}
