import test from "node:test";
import assert from "node:assert/strict";

// Verifies WS-UX-TOP-NAV-ROLES-001: grouped top navigation menus and menu action mapping.
import {
    NAVIGATION_ACTIONS,
    menuHasActivePage,
    navigationActionForMenuItem,
    resultMenuItems,
    runModeMenuItems,
    testConfigurationMenuItems,
    toggledNavMenu
} from "../../src/app/webStudioNavigation.js";
import { WEB_STUDIO_ROLES } from "../../src/app/webStudioRoles.js";

test("test configuration menu exposes Basic settings entry", () => {
    assert.deepEqual(testConfigurationMenuItems(WEB_STUDIO_ROLES.BASIC), [
        { id: "basic-settings", label: "Test Settings" }
    ]);
});

test("test configuration menu exposes Advanced configuration entries", () => {
    assert.deepEqual(testConfigurationMenuItems(WEB_STUDIO_ROLES.ADVANCED), [
        { id: "settings", label: "Test Settings" },
        { id: "composition", label: "Composition Flow" },
        { id: "policies", label: "Policies" }
    ]);
});

test("run mode menu contains current modes and disabled MCP placeholder", () => {
    assert.deepEqual(runModeMenuItems(), [
        { id: "run", label: "Generate Mode" },
        { id: "cli", label: "CLI Mode" },
        { id: "mcp", label: "MCP Mode", disabled: true }
    ]);
});

test("result menu hides advanced result tools in Basic role", () => {
    assert.deepEqual(resultMenuItems(WEB_STUDIO_ROLES.BASIC), [
        { id: "results", label: "Test Reports" }
    ]);
    assert.deepEqual(resultMenuItems(WEB_STUDIO_ROLES.ADVANCED), [
        { id: "results", label: "Test Reports" },
        { id: "state-model", label: "State Model" },
        { id: "logs", label: "Debug Files" }
    ]);
});

test("menu active state detects direct page matches", () => {
    assert.equal(menuHasActivePage(runModeMenuItems(), "cli"), true);
    assert.equal(menuHasActivePage(runModeMenuItems(), "spy"), false);
});

test("navigation menu toggle closes the active menu or opens a new one", () => {
    assert.equal(toggledNavMenu("", "run"), "run");
    assert.equal(toggledNavMenu("run", "run"), "");
    assert.equal(toggledNavMenu("run", "results"), "results");
});

test("navigation menu items resolve to navigation actions", () => {
    assert.equal(navigationActionForMenuItem({ id: "settings" }), NAVIGATION_ACTIONS.SETTINGS);
    assert.equal(navigationActionForMenuItem({ id: "composition" }), NAVIGATION_ACTIONS.COMPOSITION_FLOW);
    assert.equal(navigationActionForMenuItem({ id: "state-model" }), NAVIGATION_ACTIONS.STATE_MODEL);
    assert.equal(navigationActionForMenuItem({ id: "mcp", disabled: true }), NAVIGATION_ACTIONS.NONE);
    assert.equal(navigationActionForMenuItem({ id: "unknown" }), NAVIGATION_ACTIONS.NONE);
    assert.equal(navigationActionForMenuItem(null), NAVIGATION_ACTIONS.NONE);
});
