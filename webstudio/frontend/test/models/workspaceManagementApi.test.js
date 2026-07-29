// Verifies WS-FUNC-WORKSPACE-MANAGEMENT-001: workspace management API request construction.
import test from "node:test";
import assert from "node:assert/strict";
import {
    createWorkspaceRequest,
    renameWorkspaceRequest
} from "../../src/models/workspaceManagementApi.js";

function recordingLoadJson(result = {}) {
    const calls = [];
    const loadJson = async (path, options) => {
        calls.push({ path, options });
        return result;
    };

    return { calls, loadJson };
}

test("creates workspaces with normalized JSON request body", async () => {
    const { calls, loadJson } = recordingLoadJson({ name: "webdriver_parabank" });

    const result = await createWorkspaceRequest(loadJson, {
        name: " webdriver_parabank ",
        baseWorkspace: " webdriver_generic ",
        copyTestGoals: false,
        copyOracles: true
    });

    assert.deepEqual(result, {
        request: {
            name: "webdriver_parabank",
            baseWorkspace: "webdriver_generic",
            copyTestGoals: false,
            copyOracles: true
        },
        workspace: {
            name: "webdriver_parabank"
        }
    });
    assert.deepEqual(calls, [
        {
            path: "/api/workspaces",
            options: {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    name: "webdriver_parabank",
                    baseWorkspace: "webdriver_generic",
                    copyTestGoals: false,
                    copyOracles: true
                })
            }
        }
    ]);
});

test("renames workspaces with encoded workspace name and normalized JSON request body", async () => {
    const { calls, loadJson } = recordingLoadJson({ name: "webdriver_parabank" });

    const result = await renameWorkspaceRequest(loadJson, "webdriver generic", {
        name: " webdriver_parabank "
    });

    assert.deepEqual(result, {
        request: {
            name: "webdriver_parabank"
        },
        workspace: {
            name: "webdriver_parabank"
        }
    });
    assert.deepEqual(calls, [
        {
            path: "/api/workspaces/webdriver%20generic/rename",
            options: {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    name: "webdriver_parabank"
                })
            }
        }
    ]);
});
