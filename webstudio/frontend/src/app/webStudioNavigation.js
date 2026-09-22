import { WEB_STUDIO_ROLES } from "./webStudioRoles.js";

// Top navigation menu groups and menu action mapping.
export const NAVIGATION_ACTIONS = {
    NONE: "none",
    BASIC_SETTINGS: "basic-settings",
    SETTINGS: "settings",
    TEST_ORACLES: "test-oracles",
    TEST_GOALS: "test-goals",
    COMPOSITION_FLOW: "composition-flow",
    POLICIES: "policies",
    GENERATE_MODE: "generate-mode",
    CLI_MODE: "cli-mode",
    TEST_RESULTS: "test-results",
    STATE_MODEL: "state-model",
    DEBUG_LOGS: "debug-logs"
};

export function testConfigurationMenuItems(role) {
    if (role === WEB_STUDIO_ROLES.BASIC) {
        return [
            { id: "basic-settings", label: "Test Settings" }
        ];
    }

    return [
        { id: "settings", label: "Test Settings" },
        { id: "composition", label: "Composition Flow" },
        { id: "policies", label: "Policies" }
    ];
}

export function runModeMenuItems() {
    return [
        { id: "run", label: "Generate Mode" },
        { id: "cli", label: "CLI Mode" },
        { id: "mcp", label: "MCP Mode", disabled: true }
    ];
}

export function resultMenuItems(role) {
    const items = [
        { id: "results", label: "Test Reports" }
    ];

    if (role === WEB_STUDIO_ROLES.ADVANCED) {
        items.push(
            { id: "state-model", label: "State Model" },
            { id: "logs", label: "Debug Files" }
        );
    }

    return items;
}

export function menuHasActivePage(items, currentPage) {
    return items.some((item) => item.id === currentPage || item.page === currentPage);
}

export function toggledNavMenu(activeNavMenu, menuId) {
    return activeNavMenu === menuId ? "" : menuId;
}

export function navigationActionForMenuItem(item) {
    if (!item || item.disabled) {
        return NAVIGATION_ACTIONS.NONE;
    }

    const actionsByItemId = {
        "basic-settings": NAVIGATION_ACTIONS.BASIC_SETTINGS,
        settings: NAVIGATION_ACTIONS.SETTINGS,
        "basic-oracles": NAVIGATION_ACTIONS.TEST_ORACLES,
        "advanced-oracles": NAVIGATION_ACTIONS.TEST_ORACLES,
        "test-goals": NAVIGATION_ACTIONS.TEST_GOALS,
        composition: NAVIGATION_ACTIONS.COMPOSITION_FLOW,
        policies: NAVIGATION_ACTIONS.POLICIES,
        run: NAVIGATION_ACTIONS.GENERATE_MODE,
        cli: NAVIGATION_ACTIONS.CLI_MODE,
        results: NAVIGATION_ACTIONS.TEST_RESULTS,
        "state-model": NAVIGATION_ACTIONS.STATE_MODEL,
        logs: NAVIGATION_ACTIONS.DEBUG_LOGS
    };

    return actionsByItemId[item.id] || NAVIGATION_ACTIONS.NONE;
}
