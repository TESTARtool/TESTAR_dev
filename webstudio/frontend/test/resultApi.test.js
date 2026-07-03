import test from "node:test";
import assert from "node:assert/strict";
import {
    resultFileUrl,
    resultGroupDeleteUrl,
    resultListUrl
} from "../src/resultApi.js";

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
