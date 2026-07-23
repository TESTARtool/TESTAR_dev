import test from "node:test";
import assert from "node:assert/strict";
import { stateModelWorkspaceDialog } from "../src/stateModelNavigation.js";

test("shows a dialog when opening state model without a selected workspace", () => {
    const dialog = stateModelWorkspaceDialog("");

    assert.equal(dialog.title, "Unable To Open State Model");
    assert.match(dialog.message, /select a TESTAR workspace/);
});

test("does not block state model navigation for a selected workspace name", () => {
    assert.equal(stateModelWorkspaceDialog("webdriver_generic"), null);
    assert.equal(stateModelWorkspaceDialog("workspace_copied_after_startup"), null);
});
