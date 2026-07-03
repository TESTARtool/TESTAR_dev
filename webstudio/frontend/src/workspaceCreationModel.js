const WORKSPACE_NAME_PATTERN = /^[A-Za-z0-9_-]+$/;

export function defaultWorkspaceCreationDraft(workspaces, selectedWorkspaceName) {
    return {
        name: "",
        baseWorkspace: selectedWorkspaceName || workspaces?.[0]?.name || "",
        copyTestGoals: true
    };
}

export function workspaceCreationValidation(draft, workspaces) {
    const workspaceName = (draft?.name || "").trim();
    const baseWorkspace = (draft?.baseWorkspace || "").trim();
    const existingNames = new Set((workspaces || []).map((workspace) => workspace.name));

    if (!workspaceName) {
        return {
            valid: false,
            message: "Enter a workspace name."
        };
    }

    if (!WORKSPACE_NAME_PATTERN.test(workspaceName)) {
        return {
            valid: false,
            message: "Use only letters, numbers, underscores, and hyphens."
        };
    }

    if (existingNames.has(workspaceName)) {
        return {
            valid: false,
            message: "A workspace with this name already exists."
        };
    }

    if (!baseWorkspace || !existingNames.has(baseWorkspace)) {
        return {
            valid: false,
            message: "Select an existing base workspace."
        };
    }

    return {
        valid: true,
        message: ""
    };
}

export function workspaceCreationRequest(draft) {
    return {
        name: (draft?.name || "").trim(),
        baseWorkspace: (draft?.baseWorkspace || "").trim(),
        copyTestGoals: draft?.copyTestGoals !== false
    };
}
