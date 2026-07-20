import test from "node:test";
import assert from "node:assert/strict";
import {
    BASIC_ROLE_SETTINGS_GROUP_IDS,
    TEST_SETTINGS_EXCLUDED_SETTING_KEYS,
    TEST_SETTINGS_GROUP_IDS,
    WEB_STUDIO_ROLES,
    normalizeWebStudioRole,
    pageAvailableForRole,
    pageForRole,
    workspaceManagementLandingPageForRole
} from "../src/webStudioRoles.js";

test("normalizes unknown roles to advanced", () => {
    assert.equal(normalizeWebStudioRole("unknown"), WEB_STUDIO_ROLES.ADVANCED);
    assert.equal(normalizeWebStudioRole(WEB_STUDIO_ROLES.BASIC), WEB_STUDIO_ROLES.BASIC);
});

test("maps unavailable pages when switching to basic role", () => {
    assert.equal(pageForRole(WEB_STUDIO_ROLES.BASIC, "composition"), "basic-settings");
    assert.equal(pageForRole(WEB_STUDIO_ROLES.BASIC, "logs"), "basic-settings");
    assert.equal(pageForRole(WEB_STUDIO_ROLES.BASIC, "oracles"), "oracles");
    assert.equal(pageForRole(WEB_STUDIO_ROLES.BASIC, "test-goals"), "test-goals");
});

test("maps basic-only settings page back to advanced settings page", () => {
    assert.equal(pageForRole(WEB_STUDIO_ROLES.ADVANCED, "basic-settings"), "settings");
    assert.equal(pageForRole(WEB_STUDIO_ROLES.ADVANCED, "results"), "results");
});

test("workspace management returns to the role-specific settings page", () => {
    assert.equal(workspaceManagementLandingPageForRole(WEB_STUDIO_ROLES.BASIC), "basic-settings");
    assert.equal(workspaceManagementLandingPageForRole(WEB_STUDIO_ROLES.ADVANCED), "settings");
});

test("exposes the initial basic settings group set", () => {
    assert.deepEqual(
        BASIC_ROLE_SETTINGS_GROUP_IDS,
        [
            "sut-connection",
            "execution",
            "filters",
            "agent-cli",
            "webdriver",
            "appium"
        ]
    );
});

test("exposes visual test settings without oracle settings", () => {
    assert.deepEqual(
        TEST_SETTINGS_GROUP_IDS,
        [
            "sut-connection",
            "execution",
            "reporting",
            "filters",
            "state-model",
            "state-identification",
            "agent-cli",
            "webdriver",
            "appium",
            "llm",
            "coverage"
        ]
    );
    assert.equal(TEST_SETTINGS_GROUP_IDS.includes("oracles"), false);
    assert.deepEqual(
        TEST_SETTINGS_EXCLUDED_SETTING_KEYS,
        [
            "WebConsoleErrorOracle",
            "WebConsoleErrorPattern",
            "WebConsoleWarningOracle",
            "WebConsoleWarningPattern"
        ]
    );
});

test("checks page availability by role", () => {
    assert.equal(pageAvailableForRole(WEB_STUDIO_ROLES.BASIC, "basic-settings"), true);
    assert.equal(pageAvailableForRole(WEB_STUDIO_ROLES.BASIC, "oracles"), true);
    assert.equal(pageAvailableForRole(WEB_STUDIO_ROLES.BASIC, "logs"), false);
    assert.equal(pageAvailableForRole(WEB_STUDIO_ROLES.ADVANCED, "composition"), true);
    assert.equal(pageAvailableForRole(WEB_STUDIO_ROLES.ADVANCED, "oracles"), true);
    assert.equal(pageAvailableForRole(WEB_STUDIO_ROLES.ADVANCED, "basic-settings"), false);
});
