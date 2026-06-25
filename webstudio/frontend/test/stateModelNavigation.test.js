import test from "node:test";
import assert from "node:assert/strict";
import { stateModelRuntime, stateModelWorkspaceDialog } from "../src/stateModelNavigation.js";

test("shows a dialog when opening state model without a selected workspace", () => {
    const dialog = stateModelWorkspaceDialog("", false, false);

    assert.equal(dialog.title, "Unable To Open State Model");
    assert.match(dialog.message, /select a TESTAR workspace/);
});

test("shows a dialog when selected workspace is not available in TESTAR or CLI runtime", () => {
    const dialog = stateModelWorkspaceDialog("unknown_workspace", false, false);

    assert.equal(dialog.title, "Unable To Open State Model");
    assert.match(dialog.message, /unknown_workspace/);
    assert.match(dialog.message, /not available in the TESTAR or CLI runtime/);
});

test("does not show a dialog when selected workspace is available in TESTAR runtime", () => {
    assert.equal(stateModelWorkspaceDialog("webdriver_generic", true, false), null);
});

test("does not show a dialog when selected workspace is available in CLI runtime", () => {
    assert.equal(stateModelWorkspaceDialog("cli_generic", false, true), null);
});

test("uses CLI runtime for state model analysis when current page is CLI", () => {
    assert.equal(stateModelRuntime("cli", true, true), "cli");
});

test("uses TESTAR runtime for state model analysis outside CLI when both runtimes contain the workspace", () => {
    assert.equal(stateModelRuntime("run", true, true), "testar");
});

test("uses CLI runtime for state model analysis when workspace exists only in CLI runtime", () => {
    assert.equal(stateModelRuntime("configuration", false, true), "cli");
});
