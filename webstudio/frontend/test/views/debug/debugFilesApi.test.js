// Verifies WS-FUNC-DEBUG-FILES-001: debug file list and content request contracts.
import test from "node:test";
import assert from "node:assert/strict";
import {
    debugFileUrl,
    debugFilesListUrl,
    loadDebugFileRequest,
    loadDebugFilesRequest
} from "../../../src/views/debug/debugFilesApi.js";

function recordingLoadJson(result = {}) {
    const calls = [];
    const loadJson = async (path, options) => {
        calls.push({ path, options });
        return result;
    };

    return { calls, loadJson };
}

test("builds debug files list URL", () => {
    assert.equal(debugFilesListUrl(), "/api/debug-files");
});

test("builds debug file URL with encoded path", () => {
    assert.equal(
        debugFileUrl({
            name: "testar.log",
            path: "C:\\output\\debug files\\testar.log"
        }),
        "/api/debug-files/testar.log?path=C%3A%5Coutput%5Cdebug%20files%5Ctestar.log"
    );
});

test("loads debug files through provided loader", async () => {
    const { calls, loadJson } = recordingLoadJson([]);

    await loadDebugFilesRequest(loadJson);

    assert.deepEqual(calls, [
        {
            path: "/api/debug-files",
            options: undefined
        }
    ]);
});

test("loads selected debug file through provided loader", async () => {
    const { calls, loadJson } = recordingLoadJson({ name: "testar.log" });

    await loadDebugFileRequest(loadJson, {
        name: "testar.log",
        path: "C:\\output\\testar.log"
    });

    assert.deepEqual(calls, [
        {
            path: "/api/debug-files/testar.log?path=C%3A%5Coutput%5Ctestar.log",
            options: undefined
        }
    ]);
});
