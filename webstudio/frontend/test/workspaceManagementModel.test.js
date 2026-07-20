import test from "node:test";
import assert from "node:assert/strict";
import {
    defaultWorkspaceCreateDraft,
    defaultWorkspaceRenameDraft,
    workspaceCreateRequest,
    workspaceCreateValidation,
    workspaceRenameRequest,
    workspaceRenameValidation
} from "../src/workspaceManagementModel.js";

const WORKSPACES = [
    { name: "webdriver_generic" },
    { name: "windows_generic" }
];

test("defaults workspace creation to the selected workspace and copies optional workspace assets", () => {
    assert.deepEqual(
        defaultWorkspaceCreateDraft(WORKSPACES, "windows_generic"),
        {
            name: "",
            baseWorkspace: "windows_generic",
            copyTestGoals: true,
            copyOracles: true
        }
    );
});

test("defaults workspace rename to the selected workspace name", () => {
    assert.deepEqual(
        defaultWorkspaceRenameDraft("webdriver_generic"),
        {
            name: "webdriver_generic"
        }
    );
});

test("validates safe unique workspace names and existing base workspace", () => {
    assert.deepEqual(
        workspaceCreateValidation(
            {
                name: "webdriver_parabank",
                baseWorkspace: "webdriver_generic",
                copyTestGoals: true,
                copyOracles: true
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
    assert.equal(workspaceCreateValidation({ name: "", baseWorkspace: "webdriver_generic" }, WORKSPACES).valid, false);
    assert.equal(workspaceCreateValidation({ name: "../bad", baseWorkspace: "webdriver_generic" }, WORKSPACES).valid, false);
    assert.equal(workspaceCreateValidation({ name: "webdriver_generic", baseWorkspace: "windows_generic" }, WORKSPACES).valid, false);
    assert.equal(workspaceCreateValidation({ name: "new_workspace", baseWorkspace: "missing" }, WORKSPACES).valid, false);
});

test("validates workspace rename drafts", () => {
    assert.equal(
        workspaceRenameValidation({ name: "webdriver_parabank" }, "webdriver_generic", WORKSPACES).valid,
        true
    );
    assert.equal(
        workspaceRenameValidation({ name: "webdriver_generic" }, "webdriver_generic", WORKSPACES).valid,
        false
    );
    assert.equal(
        workspaceRenameValidation({ name: "windows_generic" }, "webdriver_generic", WORKSPACES).valid,
        false
    );
    assert.equal(
        workspaceRenameValidation({ name: "bad/name" }, "webdriver_generic", WORKSPACES).valid,
        false
    );
});

test("normalizes workspace management request values", () => {
    assert.deepEqual(
        workspaceCreateRequest({
            name: "  webdriver_parabank  ",
            baseWorkspace: "  webdriver_generic  ",
            copyTestGoals: false,
            copyOracles: false
        }),
        {
            name: "webdriver_parabank",
            baseWorkspace: "webdriver_generic",
            copyTestGoals: false,
            copyOracles: false
        }
    );
    assert.deepEqual(
        workspaceRenameRequest({
            name: "  webdriver_parabank  "
        }),
        {
            name: "webdriver_parabank"
        }
    );
});
