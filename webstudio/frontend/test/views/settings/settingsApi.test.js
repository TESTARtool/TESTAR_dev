// Verifies WS-FUNC-TEST-SETTINGS-001: settings save and regex validation request contracts.
import test from "node:test";
import assert from "node:assert/strict";
import {
    regexValidationUrl,
    saveTestSettingsRequest,
    testSettingsUrl,
    validateRegexRequest
} from "../../../src/views/settings/settingsApi.js";

function recordingLoadJson(result = {}) {
    const calls = [];
    const loadJson = async (path, options) => {
        calls.push({ path, options });
        return result;
    };

    return { calls, loadJson };
}

test("builds settings API URLs", () => {
    assert.equal(testSettingsUrl("webdriver_generic"), "/api/workspaces/webdriver_generic/test-settings");
    assert.equal(regexValidationUrl(), "/api/settings/regex/validate");
});

test("saves test.settings through provided loader", async () => {
    const { calls, loadJson } = recordingLoadJson();

    await saveTestSettingsRequest(loadJson, "webdriver_generic", "SUTConnector = webdriver\n");

    assert.deepEqual(calls, [
        {
            path: "/api/workspaces/webdriver_generic/test-settings",
            options: {
                method: "PUT",
                headers: {
                    "Content-Type": "text/plain"
                },
                body: "SUTConnector = webdriver\n"
            }
        }
    ]);
});

test("validates regex through provided loader", async () => {
    const { calls, loadJson } = recordingLoadJson({ valid: true });

    await validateRegexRequest(loadJson, ".*error.*");

    assert.deepEqual(calls, [
        {
            path: "/api/settings/regex/validate",
            options: {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    value: ".*error.*"
                })
            }
        }
    ]);
});
