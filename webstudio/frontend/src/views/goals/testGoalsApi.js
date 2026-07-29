// Implements WS-FUNC-TEST-GOALS-001: workspace-scoped Test Goal API requests.
import { testGoalApiPath } from "./testGoalsModel.js";

function testGoalPathQuery(path) {
    return `path=${encodeURIComponent(path)}`;
}

export async function loadTestGoalTreeRequest(loadJson, workspaceName) {
    return loadJson(testGoalApiPath(workspaceName));
}

export async function loadTestGoalFileRequest(loadJson, workspaceName, path) {
    return loadJson(`${testGoalApiPath(workspaceName, "/file")}?${testGoalPathQuery(path)}`);
}

export async function saveTestGoalFileRequest(loadJson, workspaceName, path, content) {
    return loadJson(`${testGoalApiPath(workspaceName, "/file")}?${testGoalPathQuery(path)}`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ content })
    });
}

export async function createTestGoalFileRequest(loadJson, workspaceName, path) {
    return loadJson(`${testGoalApiPath(workspaceName, "/file")}?${testGoalPathQuery(path)}`, {
        method: "POST"
    });
}

export async function createTestGoalFolderRequest(loadJson, workspaceName, path) {
    return loadJson(`${testGoalApiPath(workspaceName, "/folder")}?${testGoalPathQuery(path)}`, {
        method: "POST"
    });
}

export async function deleteTestGoalPathRequest(loadJson, workspaceName, path) {
    return loadJson(`${testGoalApiPath(workspaceName)}?${testGoalPathQuery(path)}`, {
        method: "DELETE"
    });
}
