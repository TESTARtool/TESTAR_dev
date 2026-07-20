const WORKSPACE_NAME_PATTERN = /^[A-Za-z0-9_-]+$/;

export function defaultWorkspaceCreateDraft(workspaces, selectedWorkspaceName) {
    return {
        name: "",
        baseWorkspace: selectedWorkspaceName || workspaces?.[0]?.name || "",
        copyTestGoals: true,
        copyOracles: true
    };
}

export function defaultWorkspaceRenameDraft(selectedWorkspaceName) {
    return {
        name: selectedWorkspaceName || ""
    };
}

export function workspaceCreateValidation(draft, workspaces) {
    const workspaceName = (draft?.name || "").trim();
    const baseWorkspace = (draft?.baseWorkspace || "").trim();
    const existingNames = workspaceNameSet(workspaces);

    const nameValidation = validateNewWorkspaceName(workspaceName, existingNames);
    if (!nameValidation.valid) {
        return nameValidation;
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

export function workspaceRenameValidation(draft, currentWorkspaceName, workspaces) {
    const workspaceName = (draft?.name || "").trim();
    const existingNames = workspaceNameSet(workspaces);

    if (workspaceName === currentWorkspaceName) {
        return {
            valid: false,
            message: "Enter a different workspace name."
        };
    }

    return validateNewWorkspaceName(workspaceName, existingNames);
}

export function workspaceCreateRequest(draft) {
    return {
        name: (draft?.name || "").trim(),
        baseWorkspace: (draft?.baseWorkspace || "").trim(),
        copyTestGoals: draft?.copyTestGoals !== false,
        copyOracles: draft?.copyOracles !== false
    };
}

export function workspaceRenameRequest(draft) {
    return {
        name: (draft?.name || "").trim()
    };
}

function validateNewWorkspaceName(workspaceName, existingNames) {
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

    return {
        valid: true,
        message: ""
    };
}

function workspaceNameSet(workspaces) {
    return new Set((workspaces || []).map((workspace) => workspace.name));
}
