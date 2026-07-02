import test from "node:test";
import assert from "node:assert/strict";
import {
    flattenTestGoalTree,
    isExecutableTestGoal,
    closedTestGoalDeleteDialog,
    testGoalDeleteDialogForPath,
    testGoalFolderSelectionState,
    testGoalKindLabel,
    testGoalWorkspaceRootLabel
} from "../src/testGoalsModel.js";

test("flattens test goal tree including root node", () => {
    const flattened = flattenTestGoalTree({
        name: "test_goals",
        path: "",
        type: "folder",
        executable: false,
        children: [
            {
                name: "web",
                path: "web",
                type: "folder",
                executable: false,
                children: [
                    {
                        name: "login.yaml",
                        path: "web/login.yaml",
                        type: "file",
                        executable: true,
                        children: []
                    }
                ]
            }
        ]
    });

    assert.deepEqual(
        flattened.map((item) => ({ name: item.name, depth: item.depth })),
        [
            { name: "test_goals", depth: 0 },
            { name: "web", depth: 1 },
            { name: "login.yaml", depth: 2 }
        ]
    );
});

test("classifies executable YAML goals and reference files", () => {
    const yamlGoal = {
        type: "file",
        executable: true
    };
    const referenceFile = {
        type: "file",
        executable: false
    };
    const folder = {
        type: "folder",
        executable: false
    };

    assert.equal(isExecutableTestGoal(yamlGoal), true);
    assert.equal(isExecutableTestGoal(referenceFile), false);
    assert.equal(testGoalKindLabel(yamlGoal), "YAML Goal");
    assert.equal(testGoalKindLabel(referenceFile), "Reference");
    assert.equal(testGoalKindLabel(folder), "Folder");
});

test("selecting a test goal folder clears the selected YAML editor state", () => {
    assert.deepEqual(
        testGoalFolderSelectionState("web/parabank/functional"),
        {
            selectedTestGoalFolderPath: "web/parabank/functional",
            selectedTestGoalFile: null,
            testGoalDraftContent: "",
            savedTestGoalContent: ""
        }
    );
});

test("selecting the test goal root clears the selected YAML editor state", () => {
    assert.deepEqual(
        testGoalFolderSelectionState(""),
        {
            selectedTestGoalFolderPath: "",
            selectedTestGoalFile: null,
            testGoalDraftContent: "",
            savedTestGoalContent: ""
        }
    );
});

test("builds workspace-scoped test goal root labels", () => {
    assert.equal(testGoalWorkspaceRootLabel("webdriver_generic"), "webdriver_generic/test_goals");
    assert.equal(testGoalWorkspaceRootLabel(""), "workspace/test_goals");
});

test("builds closed test goal delete dialog state", () => {
    assert.deepEqual(
        closedTestGoalDeleteDialog(),
        {
            open: false,
            path: "",
            title: "",
            message: ""
        }
    );
});

test("builds custom delete dialog state for YAML goal files", () => {
    assert.deepEqual(
        testGoalDeleteDialogForPath("web/parabank/login.yaml", "file"),
        {
            open: true,
            path: "web/parabank/login.yaml",
            title: "Delete Test Goal File",
            message: "This deletes the selected Test Goal file. This action cannot be undone."
        }
    );
});

test("builds custom delete dialog state for test goal folders", () => {
    assert.deepEqual(
        testGoalDeleteDialogForPath("web/parabank", "folder"),
        {
            open: true,
            path: "web/parabank",
            title: "Delete Test Goals Folder",
            message: "This deletes the selected folder and all Test Goals inside it. This action cannot be undone."
        }
    );
});

test("does not open custom delete dialog without a path", () => {
    assert.deepEqual(testGoalDeleteDialogForPath("", "folder"), closedTestGoalDeleteDialog());
});
