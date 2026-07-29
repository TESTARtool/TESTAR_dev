// Verifies WS-FUNC-RUNTIME-EXECUTION-001: Generate, Spy, and CLI runtime API request contracts.
import test from "node:test";
import assert from "node:assert/strict";
import {
    executeSpyActionRequest,
    executeSpyWidgetDefaultActionRequest,
    executeSpyWidgetDirectTypeRequest,
    loadCliStatusRequest,
    loadRemoteSpyStatusRequest,
    loadScriptlessStatusRequest,
    refreshRemoteSpyRequest,
    runCliManualCommandRequest,
    startCliAgentSessionRequest,
    startCliManualSessionRequest,
    startGenerateRequest,
    startLocalSpyRequest,
    startRemoteSpyRequest,
    stopCliAgentSessionRequest,
    stopCliManualSessionRequest,
    stopRemoteSpyRequest,
    stopScriptlessRequest
} from "../../../src/views/runtime/runtimeApi.js";

function requestRecorder() {
    const calls = [];
    return {
        calls,
        loadJson: async (path, options = null) => {
            calls.push({ path, options });
            return { path, options };
        }
    };
}

test("loads runtime status endpoints", async () => {
    const recorder = requestRecorder();

    await loadScriptlessStatusRequest(recorder.loadJson);
    await loadCliStatusRequest(recorder.loadJson);
    await loadRemoteSpyStatusRequest(recorder.loadJson);

    assert.deepEqual(recorder.calls, [
        { path: "/api/execution/status/scriptless", options: null },
        { path: "/api/execution/status/cli", options: null },
        { path: "/api/spy/status", options: null }
    ]);
});

test("builds scriptless generate and stop requests", async () => {
    const recorder = requestRecorder();

    await startGenerateRequest(recorder.loadJson, "webdriver generic");
    await stopScriptlessRequest(recorder.loadJson);

    assert.deepEqual(recorder.calls, [
        {
            path: "/api/execution/scriptless/generate/webdriver%20generic",
            options: { method: "POST" }
        },
        {
            path: "/api/execution/scriptless/stop",
            options: { method: "POST" }
        }
    ]);
});

test("builds remote and local spy requests", async () => {
    const recorder = requestRecorder();

    await startRemoteSpyRequest(recorder.loadJson, "webdriver_generic");
    await refreshRemoteSpyRequest(recorder.loadJson);
    await stopRemoteSpyRequest(recorder.loadJson);
    await startLocalSpyRequest(recorder.loadJson, "webdriver generic");

    assert.deepEqual(recorder.calls, [
        {
            path: "/api/spy/start/webdriver_generic",
            options: { method: "POST" }
        },
        {
            path: "/api/spy/refresh",
            options: { method: "POST" }
        },
        {
            path: "/api/spy/stop",
            options: { method: "POST" }
        },
        {
            path: "/api/execution/scriptless/local-spy/webdriver%20generic",
            options: { method: "POST" }
        }
    ]);
});

test("builds spy action requests with encoded identifiers", async () => {
    const recorder = requestRecorder();

    await executeSpyActionRequest(recorder.loadJson, "click action");
    await executeSpyWidgetDefaultActionRequest(recorder.loadJson, "widget/path");
    await executeSpyWidgetDirectTypeRequest(recorder.loadJson, "input username", "john");

    assert.deepEqual(recorder.calls, [
        {
            path: "/api/spy/actions/click%20action",
            options: { method: "POST" }
        },
        {
            path: "/api/spy/widgets/widget%2Fpath/default-action",
            options: { method: "POST" }
        },
        {
            path: "/api/spy/widgets/input%20username/direct-type",
            options: {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({ text: "john" })
            }
        }
    ]);
});

test("builds CLI session and command requests", async () => {
    const recorder = requestRecorder();

    await startCliManualSessionRequest(recorder.loadJson, "webdriver generic");
    await startCliAgentSessionRequest(recorder.loadJson, "webdriver_generic");
    await runCliManualCommandRequest(recorder.loadJson, "getState");
    await stopCliManualSessionRequest(recorder.loadJson);
    await stopCliAgentSessionRequest(recorder.loadJson);

    assert.deepEqual(recorder.calls, [
        {
            path: "/api/execution/cli/manual/start/webdriver%20generic",
            options: {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: "{}"
            }
        },
        {
            path: "/api/execution/cli/agent/start/webdriver_generic",
            options: {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: "{}"
            }
        },
        {
            path: "/api/execution/cli/manual/command",
            options: {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({ commandLine: "getState" })
            }
        },
        {
            path: "/api/execution/cli/manual/stop",
            options: { method: "POST" }
        },
        {
            path: "/api/execution/cli/agent/stop",
            options: { method: "POST" }
        }
    ]);
});
