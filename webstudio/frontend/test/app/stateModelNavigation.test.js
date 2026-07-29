// Verifies WS-FUNC-STATE-MODEL-001 and WS-UX-STATE-MODEL-001:
// state model status, error, and dialog action decisions.
import test from "node:test";
import assert from "node:assert/strict";
import {
    STATE_MODEL_DEFAULT_URL,
    stateModelDialogFromStatus,
    stateModelDialogMessage,
    stateModelDialogTitle,
    stateModelExternalUrl,
    stateModelRequestedMessage,
    stateModelShouldOpenExternalTab,
    stateModelShouldPollStatus,
    stateModelStoppedMessage,
    stateModelWorkspaceDialog
} from "../../src/app/stateModelNavigation.js";

test("shows a dialog when opening state model without a selected workspace", () => {
    const dialog = stateModelWorkspaceDialog("");

    assert.equal(dialog.title, "Unable To Open State Model");
    assert.match(dialog.message, /select a TESTAR workspace/);
});

test("does not block state model navigation for a selected workspace name", () => {
    assert.equal(stateModelWorkspaceDialog("webdriver_generic"), null);
    assert.equal(stateModelWorkspaceDialog("workspace_copied_after_startup"), null);
});

test("maps state model status to dialog titles", () => {
    assert.equal(stateModelDialogTitle("RUNNING"), "State Model Analysis Running");
    assert.equal(stateModelDialogTitle("STARTING"), "State Model Analysis Starting");
    assert.equal(stateModelDialogTitle("FAILED"), "Unable To Open State Model");
    assert.equal(stateModelDialogTitle("STOPPED"), "State Model Analysis");
});

test("builds a dialog from state model status responses", () => {
    const dialog = stateModelDialogFromStatus({
        status: "RUNNING",
        message: "State model is available.",
        url: "http://localhost:8090/models"
    });

    assert.equal(dialog.title, "State Model Analysis Running");
    assert.equal(dialog.message, "State model is available.");
    assert.equal(dialog.status.status, "RUNNING");
});

test("uses a fallback message for unknown state model status responses", () => {
    const dialog = stateModelDialogFromStatus(null);

    assert.equal(dialog.title, "State Model Analysis");
    assert.equal(dialog.message, "State model analysis status is unknown.");
    assert.deepEqual(dialog.status, {});
});

test("maps missing datastore errors to the state model not available dialog", () => {
    const dialog = stateModelDialogMessage(new Error("No generated state model was found yet"));

    assert.equal(dialog.title, "State Model Not Available");
    assert.match(dialog.message, /must execute a Generate run/);
});

test("maps generic open errors to the unable dialog", () => {
    const dialog = stateModelDialogMessage(new Error("Unexpected failure"));

    assert.equal(dialog.title, "Unable To Open State Model");
    assert.match(dialog.message, /no generated state models available/);
});

test("detects status follow-up actions", () => {
    assert.equal(stateModelShouldOpenExternalTab({ status: "RUNNING" }), true);
    assert.equal(stateModelShouldOpenExternalTab({ status: "STARTING" }), false);
    assert.equal(stateModelShouldPollStatus({ status: "STARTING" }), true);
    assert.equal(stateModelShouldPollStatus({ status: "RUNNING" }), false);
});

test("uses state model default url and message fallbacks", () => {
    assert.equal(stateModelExternalUrl({}), STATE_MODEL_DEFAULT_URL);
    assert.equal(stateModelExternalUrl({ url: "http://localhost:8090/custom" }), "http://localhost:8090/custom");
    assert.equal(stateModelRequestedMessage({}), "State model analysis requested.");
    assert.equal(stateModelRequestedMessage({ message: "Requested." }), "Requested.");
    assert.equal(stateModelStoppedMessage({}), "State model analysis stopped.");
    assert.equal(stateModelStoppedMessage({ message: "Stopped." }), "Stopped.");
});
