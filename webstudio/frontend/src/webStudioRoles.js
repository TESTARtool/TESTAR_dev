export const WEB_STUDIO_ROLES = {
    BASIC: "basic",
    ADVANCED: "advanced"
};

export const BASIC_ROLE_SETTINGS_GROUP_IDS = [
    "sut-connection",
    "execution",
    "filters",
    "agent-cli",
    "webdriver",
    "appium"
];

export const TEST_SETTINGS_GROUP_IDS = [
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
];

export const TEST_SETTINGS_EXCLUDED_SETTING_KEYS = [
    "WebConsoleErrorOracle",
    "WebConsoleErrorPattern",
    "WebConsoleWarningOracle",
    "WebConsoleWarningPattern"
];

const BASIC_PAGES = new Set([
    "basic-settings",
    "oracles",
    "test-goals",
    "spy",
    "run",
    "cli",
    "results"
]);

const ADVANCED_PAGES = new Set([
    "settings",
    "oracles",
    "composition",
    "policies",
    "test-goals",
    "spy",
    "run",
    "cli",
    "results",
    "logs"
]);

export function normalizeWebStudioRole(role) {
    return role === WEB_STUDIO_ROLES.BASIC
        ? WEB_STUDIO_ROLES.BASIC
        : WEB_STUDIO_ROLES.ADVANCED;
}

export function pageForRole(role, currentPage) {
    const normalizedRole = normalizeWebStudioRole(role);

    if (normalizedRole === WEB_STUDIO_ROLES.BASIC) {
        return BASIC_PAGES.has(currentPage) ? currentPage : "basic-settings";
    }

    return ADVANCED_PAGES.has(currentPage) ? currentPage : "settings";
}

export function pageAvailableForRole(role, page) {
    return normalizeWebStudioRole(role) === WEB_STUDIO_ROLES.BASIC
        ? BASIC_PAGES.has(page)
        : ADVANCED_PAGES.has(page);
}
