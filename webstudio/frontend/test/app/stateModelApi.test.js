// Verifies WS-FUNC-STATE-MODEL-001: state model endpoint request construction.
import test from "node:test";
import assert from "node:assert/strict";
import {
    loadStateModelStatusRequest,
    openStateModelRequest,
    stopStateModelRequest
} from "../../src/app/stateModelApi.js";

function recordingLoadJson(calls) {
    return (path, options = {}) => {
        calls.push({ path, options });
        return Promise.resolve({ ok: true });
    };
}

test("loads state model status from the status endpoint", async () => {
    const calls = [];

    await loadStateModelStatusRequest(recordingLoadJson(calls));

    assert.deepEqual(calls, [
        { path: "/api/statemodel/status", options: {} }
    ]);
});

test("opens state model analysis for the encoded workspace name", async () => {
    const calls = [];

    await openStateModelRequest(recordingLoadJson(calls), "webdriver custom");

    assert.deepEqual(calls, [
        {
            path: "/api/statemodel/open/webdriver%20custom",
            options: { method: "POST" }
        }
    ]);
});

test("stops state model analysis with a POST request", async () => {
    const calls = [];

    await stopStateModelRequest(recordingLoadJson(calls));

    assert.deepEqual(calls, [
        {
            path: "/api/statemodel/stop",
            options: { method: "POST" }
        }
    ]);
});
