export const WEB_STUDIO_ROLES = {
    BASIC: "basic",
    ADVANCED: "advanced"
};

export const BASIC_ROLE_SETTINGS_GROUP_IDS = [
    "sut-connection",
    "execution",
    "filters",
    "oracles",
    "agent-cli",
    "webdriver",
    "appium"
];

const BASIC_PAGES = new Set([
    "basic-settings",
    "test-goals",
    "spy",
    "run",
    "cli",
    "results"
]);

const ADVANCED_PAGES = new Set([
    "configuration",
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

    return ADVANCED_PAGES.has(currentPage) ? currentPage : "configuration";
}

export function pageAvailableForRole(role, page) {
    return normalizeWebStudioRole(role) === WEB_STUDIO_ROLES.BASIC
        ? BASIC_PAGES.has(page)
        : ADVANCED_PAGES.has(page);
}
