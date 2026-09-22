// Verifies WS-FUNC-RUNTIME-EXECUTION-001 and WS-UX-RUNTIME-EXECUTION-001:
// runtime page routing, error detection, and user-facing feedback mapping.
import test from "node:test";
import assert from "node:assert/strict";
import {
    RUNTIME_ACTIONS,
    RUNTIME_FEEDBACK_ACTIONS,
    RUNTIME_PAGES,
    sequenceVerdicts,
    runtimeActionFeedbackMessage,
    runtimeFeedbackMessage,
    runtimeFallbackMessageForAction,
    runtimePageForAction,
    runtimeStatusReturnedError
} from "../../../src/views/runtime/runtimeModel.js";

test("preserves every verdict reported for a Generate sequence", () => {
    const verdicts = sequenceVerdicts({
        sequenceNumber: 1,
        status: "failed",
        verdicts: [
            { label: "sequence_1_V001_WARNING_ACCESSIBILITY_FAULT", status: "failed" },
            { label: "sequence_1_V002_SUSPICIOUS_TAG", status: "failed" },
            { label: "sequence_1_V003_WARNING_UI_VISUAL_OR_RENDERING_FAULT", status: "failed" }
        ]
    });

    assert.deepEqual(verdicts.map((verdict) => verdict.label), [
        "sequence_1_V001_WARNING_ACCESSIBILITY_FAULT",
        "sequence_1_V002_SUSPICIOUS_TAG",
        "sequence_1_V003_WARNING_UI_VISUAL_OR_RENDERING_FAULT"
    ]);
    assert.deepEqual(verdicts.map((verdict) => verdict.status), ["failed", "failed", "failed"]);
});

test("falls back to the sequence outcome for older status payloads", () => {
    assert.deepEqual(
        sequenceVerdicts({
            sequenceNumber: 2,
            status: "failed",
            label: "sequence_2_V001_SUSPICIOUS_TAG"
        }),
        [{ label: "sequence_2_V001_SUSPICIOUS_TAG", status: "failed", outputPath: null }]
    );
});

test("does not render an aggregate sequence item beside detailed verdicts", () => {
    const verdicts = sequenceVerdicts({
        sequenceNumber: 1,
        verdicts: [
            { label: "sequence_1", status: "ok" },
            { label: "sequence_1_V001_WARNING_ACCESSIBILITY_FAULT", status: "failed" }
        ]
    });

    assert.deepEqual(verdicts.map((verdict) => verdict.label), [
        "sequence_1_V001_WARNING_ACCESSIBILITY_FAULT"
    ]);
});

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
