import test from "node:test";
import assert from "node:assert/strict";
import {
    BASIC_ROLE_SETTINGS_GROUP_IDS,
    WEB_STUDIO_ROLES,
    normalizeWebStudioRole,
    pageAvailableForRole,
    pageForRole
} from "../src/webStudioRoles.js";

test("normalizes unknown roles to advanced", () => {
    assert.equal(normalizeWebStudioRole("unknown"), WEB_STUDIO_ROLES.ADVANCED);
    assert.equal(normalizeWebStudioRole(WEB_STUDIO_ROLES.BASIC), WEB_STUDIO_ROLES.BASIC);
});

test("maps unavailable pages when switching to basic role", () => {
    assert.equal(pageForRole(WEB_STUDIO_ROLES.BASIC, "configuration"), "basic-settings");
    assert.equal(pageForRole(WEB_STUDIO_ROLES.BASIC, "logs"), "basic-settings");
    assert.equal(pageForRole(WEB_STUDIO_ROLES.BASIC, "test-goals"), "test-goals");
});

test("maps basic-only settings page back to advanced configuration", () => {
    assert.equal(pageForRole(WEB_STUDIO_ROLES.ADVANCED, "basic-settings"), "configuration");
    assert.equal(pageForRole(WEB_STUDIO_ROLES.ADVANCED, "results"), "results");
});

test("exposes the initial basic settings group set", () => {
    assert.deepEqual(
        BASIC_ROLE_SETTINGS_GROUP_IDS,
        [
            "sut-connection",
            "execution",
            "filters",
            "oracles",
            "agent-cli",
            "webdriver",
            "appium"
        ]
    );
});

test("checks page availability by role", () => {
    assert.equal(pageAvailableForRole(WEB_STUDIO_ROLES.BASIC, "basic-settings"), true);
    assert.equal(pageAvailableForRole(WEB_STUDIO_ROLES.BASIC, "logs"), false);
    assert.equal(pageAvailableForRole(WEB_STUDIO_ROLES.ADVANCED, "configuration"), true);
    assert.equal(pageAvailableForRole(WEB_STUDIO_ROLES.ADVANCED, "basic-settings"), false);
});
