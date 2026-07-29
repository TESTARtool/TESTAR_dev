// Verifies WS-FUNC-RUNTIME-EXECUTION-001 and WS-UX-RUNTIME-EXECUTION-001:
// runtime page routing, error detection, and user-facing feedback mapping.
import test from "node:test";
import assert from "node:assert/strict";
import {
    RUNTIME_ACTIONS,
    RUNTIME_FEEDBACK_ACTIONS,
    RUNTIME_PAGES,
    runtimeActionFeedbackMessage,
    runtimeFeedbackMessage,
    runtimeFallbackMessageForAction,
    runtimePageForAction,
    runtimeStatusReturnedError
} from "../../../src/views/runtime/runtimeModel.js";

test("maps runtime start actions to their page", () => {
    assert.equal(runtimePageForAction(RUNTIME_ACTIONS.GENERATE), RUNTIME_PAGES.GENERATE);
    assert.equal(runtimePageForAction(RUNTIME_ACTIONS.REMOTE_SPY), RUNTIME_PAGES.SPY);
    assert.equal(runtimePageForAction(RUNTIME_ACTIONS.LOCAL_SPY), RUNTIME_PAGES.SPY);
    assert.equal(runtimePageForAction(RUNTIME_ACTIONS.CLI_MANUAL), RUNTIME_PAGES.CLI);
    assert.equal(runtimePageForAction(RUNTIME_ACTIONS.CLI_AGENT), RUNTIME_PAGES.CLI);
    assert.equal(runtimePageForAction("unknown"), "");
});

test("detects backend runtime error statuses", () => {
    assert.equal(runtimeStatusReturnedError({ status: "error" }), true);
    assert.equal(runtimeStatusReturnedError({ status: "running" }), false);
    assert.equal(runtimeStatusReturnedError(null), false);
});

test("uses backend runtime messages before fallback messages", () => {
    assert.equal(
        runtimeFeedbackMessage({ message: "Started by backend." }, "Started."),
        "Started by backend."
    );
    assert.equal(runtimeFeedbackMessage({ message: "" }, "Started."), "Started.");
    assert.equal(runtimeFeedbackMessage(null, "Started."), "Started.");
});

test("maps runtime feedback actions to fallback messages", () => {
    assert.equal(
        runtimeFallbackMessageForAction(RUNTIME_FEEDBACK_ACTIONS.GENERATE_STARTED),
        "Generate mode started."
    );
    assert.equal(
        runtimeFallbackMessageForAction(RUNTIME_FEEDBACK_ACTIONS.REMOTE_SPY_STOPPED),
        "Remote Spy Mode stopped."
    );
    assert.equal(
        runtimeFallbackMessageForAction(RUNTIME_FEEDBACK_ACTIONS.CLI_AGENT_STOPPED),
        "Agent CLI execution stopped."
    );
    assert.equal(runtimeFallbackMessageForAction("unknown"), "");
});

test("builds runtime action feedback messages", () => {
    assert.equal(
        runtimeActionFeedbackMessage(RUNTIME_FEEDBACK_ACTIONS.CLI_MANUAL_STARTED, { message: "Backend started." }),
        "Backend started."
    );
    assert.equal(
        runtimeActionFeedbackMessage(RUNTIME_FEEDBACK_ACTIONS.CLI_MANUAL_STARTED, null),
        "Manual CLI session started."
    );
});
