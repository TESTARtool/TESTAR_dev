// Verifies WS-FUNC-TEST-RESULTS-001: workspace-scoped Test Results request construction.
import test from "node:test";
import assert from "node:assert/strict";
import {
    deleteResultFileRequest,
    deleteResultGroupRequest,
    loadResultFileRequest,
    loadResultListRequest,
    resultFileUrl,
    resultGroupDeleteUrl,
    resultListUrl
} from "../../src/api/resultApi.js";

function recordingLoadJson(result = {}) {
    const calls = [];
    const loadJson = async (path, options) => {
        calls.push({ path, options });
        return result;
    };

    return { calls, loadJson };
}

test("builds workspace-scoped result list URL", () => {
    assert.equal(
        resultListUrl("webdriver_generic"),
        "/api/execution/scriptless/results?workspace=webdriver_generic"
    );
});

test("builds workspace-scoped result file URL", () => {
    assert.equal(
        resultFileUrl("webdriver_generic", {
            name: "sequence_1_V001_OK.html",
            path: "C:\\output\\webdriver_generic\\run\\reports\\sequence_1_V001_OK.html"
        }),
        "/api/execution/scriptless/results/sequence_1_V001_OK.html?workspace=webdriver_generic&path=C%3A%5Coutput%5Cwebdriver_generic%5Crun%5Creports%5Csequence_1_V001_OK.html"
    );
});

test("builds workspace-scoped result group delete URL", () => {
    assert.equal(
        resultGroupDeleteUrl("android_generic", {
            path: "C:\\output\\android_generic\\run"
        }),
        "/api/execution/scriptless/result-groups?workspace=android_generic&path=C%3A%5Coutput%5Candroid_generic%5Crun"
    );
});

test("loads workspace-scoped result list through provided loader", async () => {
    const { calls, loadJson } = recordingLoadJson({ groups: [] });

    await loadResultListRequest(loadJson, "webdriver_generic");

    assert.deepEqual(calls, [
        {
            path: "/api/execution/scriptless/results?workspace=webdriver_generic",
            options: undefined
        }
    ]);
});

test("loads and deletes workspace-scoped result files through provided loader", async () => {
    const resultFile = {
        name: "sequence_1_V001_OK.html",
        path: "C:\\output\\sequence_1_V001_OK.html"
    };
    const { calls, loadJson } = recordingLoadJson();

    await loadResultFileRequest(loadJson, "webdriver_generic", resultFile);
    await deleteResultFileRequest(loadJson, "webdriver_generic", resultFile);

    assert.deepEqual(calls, [
        {
            path: "/api/execution/scriptless/results/sequence_1_V001_OK.html?workspace=webdriver_generic&path=C%3A%5Coutput%5Csequence_1_V001_OK.html",
            options: undefined
        },
        {
            path: "/api/execution/scriptless/results/sequence_1_V001_OK.html?workspace=webdriver_generic&path=C%3A%5Coutput%5Csequence_1_V001_OK.html",
            options: {
                method: "DELETE"
            }
        }
    ]);
});

test("deletes workspace-scoped result groups through provided loader", async () => {
    const { calls, loadJson } = recordingLoadJson();

    await deleteResultGroupRequest(loadJson, "webdriver_generic", {
        path: "C:\\output\\webdriver_generic\\run"
    });

    assert.deepEqual(calls, [
        {
            path: "/api/execution/scriptless/result-groups?workspace=webdriver_generic&path=C%3A%5Coutput%5Cwebdriver_generic%5Crun",
            options: {
                method: "DELETE"
            }
        }
    ]);
});
