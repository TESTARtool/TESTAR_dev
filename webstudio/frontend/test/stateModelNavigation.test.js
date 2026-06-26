import test from "node:test";
import assert from "node:assert/strict";
import { stateModelWorkspaceDialog } from "../src/stateModelNavigation.js";

test("shows a dialog when opening state model without a selected workspace", () => {
    const dialog = stateModelWorkspaceDialog("", false);

    assert.equal(dialog.title, "Unable To Open State Model");
    assert.match(dialog.message, /select a TESTAR workspace/);
});

test("shows a dialog when selected workspace is not available in shared runtime", () => {
    const dialog = stateModelWorkspaceDialog("unknown_workspace", false);

    assert.equal(dialog.title, "Unable To Open State Model");
    assert.match(dialog.message, /unknown_workspace/);
    assert.match(dialog.message, /not available in the shared TESTAR runtime/);
});

test("does not show a dialog when selected workspace is available", () => {
    assert.equal(stateModelWorkspaceDialog("webdriver_generic", true), null);
});
