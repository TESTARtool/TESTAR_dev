import test from "node:test";
import assert from "node:assert/strict";
import {
    clearIgnoredVerdicts,
    ignoredVerdictsUrl,
    loadIgnoredVerdicts,
    removeIgnoredVerdicts
} from "../../../src/views/settings/ignoredVerdictsApi.js";

test("uses the selected workspace for ignored verdicts", async () => {
    assert.equal(ignoredVerdictsUrl("web driver"), "/api/workspaces/web%20driver/ignored-verdicts");
    const calls = [];
    const loadJson = async (...args) => {
        calls.push(args);
        return [];
    };

    await loadIgnoredVerdicts(loadJson, "first");
    await removeIgnoredVerdicts(loadJson, "first", ["issue"]);
    await clearIgnoredVerdicts(loadJson, "second");

    assert.equal(calls[0][0], "/api/workspaces/first/ignored-verdicts");
    assert.equal(calls[1][0], "/api/workspaces/first/ignored-verdicts/remove");
    assert.equal(calls[1][1].method, "POST");
    assert.equal(calls[1][1].body, '["issue"]');
    assert.equal(calls[2][0], "/api/workspaces/second/ignored-verdicts");
    assert.equal(calls[2][1].method, "DELETE");
});
