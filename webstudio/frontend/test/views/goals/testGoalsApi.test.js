// Verifies WS-FUNC-TEST-GOALS-001: workspace-scoped Test Goal request construction.
import test from "node:test";
import assert from "node:assert/strict";
import {
    createTestGoalFileRequest,
    createTestGoalFolderRequest,
    deleteTestGoalPathRequest,
    loadTestGoalFileRequest,
    loadTestGoalTreeRequest,
    saveTestGoalFileRequest
} from "../../../src/views/goals/testGoalsApi.js";

function recordingLoadJson(result = {}) {
    const calls = [];
    const loadJson = async (path, options) => {
        calls.push({ path, options });
        return result;
    };

    return { calls, loadJson };
}

test("loads workspace test goal tree", async () => {
    const { calls, loadJson } = recordingLoadJson({ name: "test_goals" });

    const result = await loadTestGoalTreeRequest(loadJson, "webdriver_generic");

    assert.deepEqual(result, { name: "test_goals" });
    assert.deepEqual(calls, [
        {
            path: "/api/workspaces/webdriver_generic/test-goals",
            options: undefined
        }
    ]);
});

test("loads a workspace test goal file", async () => {
    const { calls, loadJson } = recordingLoadJson({ path: "web/login.yaml" });

    await loadTestGoalFileRequest(loadJson, "webdriver generic", "web/login.yaml");

    assert.deepEqual(calls, [
        {
            path: "/api/workspaces/webdriver%20generic/test-goals/file?path=web%2Flogin.yaml",
            options: undefined
        }
    ]);
});

test("saves a workspace test goal file", async () => {
    const { calls, loadJson } = recordingLoadJson();

    await saveTestGoalFileRequest(loadJson, "webdriver_generic", "login.yaml", "goal: login");

    assert.deepEqual(calls, [
        {
            path: "/api/workspaces/webdriver_generic/test-goals/file?path=login.yaml",
            options: {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({ content: "goal: login" })
            }
        }
    ]);
});

test("creates test goal files and folders", async () => {
    const { calls, loadJson } = recordingLoadJson();

    await createTestGoalFileRequest(loadJson, "webdriver_generic", "login.yaml");
    await createTestGoalFolderRequest(loadJson, "webdriver_generic", "web");

    assert.deepEqual(calls, [
        {
            path: "/api/workspaces/webdriver_generic/test-goals/file?path=login.yaml",
            options: {
                method: "POST"
            }
        },
        {
            path: "/api/workspaces/webdriver_generic/test-goals/folder?path=web",
            options: {
                method: "POST"
            }
        }
    ]);
});

test("deletes test goal paths", async () => {
    const { calls, loadJson } = recordingLoadJson();

    await deleteTestGoalPathRequest(loadJson, "webdriver_generic", "web/login.yaml");

    assert.deepEqual(calls, [
        {
            path: "/api/workspaces/webdriver_generic/test-goals?path=web%2Flogin.yaml",
            options: {
                method: "DELETE"
            }
        }
    ]);
});
