import test from "node:test";
import assert from "node:assert/strict";

import {
    menuHasActivePage,
    resultMenuItems,
    runModeMenuItems,
    testConfigurationMenuItems
} from "../src/webStudioNavigation.js";
import { WEB_STUDIO_ROLES } from "../src/webStudioRoles.js";

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
