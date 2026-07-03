import test from "node:test";
import assert from "node:assert/strict";
import {
    defaultWorkspaceCreationDraft,
    workspaceCreationRequest,
    workspaceCreationValidation
} from "../src/workspaceCreationModel.js";

const WORKSPACES = [
    { name: "webdriver_generic" },
    { name: "windows_generic" }
];

test("defaults workspace creation to the selected workspace and copies test goals", () => {
    assert.deepEqual(
        defaultWorkspaceCreationDraft(WORKSPACES, "windows_generic"),
        {
            name: "",
            baseWorkspace: "windows_generic",
            copyTestGoals: true
        }
    );
});

test("validates safe unique workspace names and existing base workspace", () => {
    assert.deepEqual(
        workspaceCreationValidation(
            {
                name: "webdriver_parabank",
                baseWorkspace: "webdriver_generic",
                copyTestGoals: true
            },
            WORKSPACES
        ),
        {
            valid: true,
            message: ""
        }
    );
});

test("rejects empty invalid duplicate or unknown-base workspace creation drafts", () => {
    assert.equal(workspaceCreationValidation({ name: "", baseWorkspace: "webdriver_generic" }, WORKSPACES).valid, false);
    assert.equal(workspaceCreationValidation({ name: "../bad", baseWorkspace: "webdriver_generic" }, WORKSPACES).valid, false);
    assert.equal(workspaceCreationValidation({ name: "webdriver_generic", baseWorkspace: "windows_generic" }, WORKSPACES).valid, false);
    assert.equal(workspaceCreationValidation({ name: "new_workspace", baseWorkspace: "missing" }, WORKSPACES).valid, false);
});

test("normalizes workspace creation request values", () => {
    assert.deepEqual(
        workspaceCreationRequest({
            name: "  webdriver_parabank  ",
            baseWorkspace: "  webdriver_generic  ",
            copyTestGoals: false
        }),
        {
            name: "webdriver_parabank",
            baseWorkspace: "webdriver_generic",
            copyTestGoals: false
        }
    );
});
