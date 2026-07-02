export function flattenTestGoalTree(node, depth = 0) {
    if (!node) {
        return [];
    }

    const children = Array.isArray(node.children) ? node.children : [];
    const current = [{ ...node, depth }];

    return [
        ...current,
        ...children.flatMap((child) => flattenTestGoalTree(child, depth + 1))
    ];
}

export function isExecutableTestGoal(node) {
    return node?.type === "file" && node.executable === true;
}

export function testGoalKindLabel(node) {
    if (node?.type === "folder") {
        return "Folder";
    }

    return isExecutableTestGoal(node) ? "YAML Goal" : "Reference";
}

export function testGoalWorkspaceRootLabel(workspaceName) {
    return workspaceName ? `${workspaceName}/test_goals` : "workspace/test_goals";
}

export function closedTestGoalDeleteDialog() {
    return {
        open: false,
        path: "",
        title: "",
        message: ""
    };
}

export function testGoalDeleteDialogForPath(path, type = "file") {
    if (!path) {
        return closedTestGoalDeleteDialog();
    }

    if (type === "folder") {
        return {
            open: true,
            path,
            title: "Delete Test Goals Folder",
            message: "This deletes the selected folder and all Test Goals inside it. This action cannot be undone."
        };
    }

    return {
        open: true,
        path,
        title: "Delete Test Goal File",
        message: "This deletes the selected Test Goal file. This action cannot be undone."
    };
}

export function testGoalFolderSelectionState(folderPath) {
    return {
        selectedTestGoalFolderPath: folderPath || "",
        selectedTestGoalFile: null,
        testGoalDraftContent: "",
        savedTestGoalContent: ""
    };
}
