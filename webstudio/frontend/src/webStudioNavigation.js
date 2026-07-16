import { WEB_STUDIO_ROLES } from "./webStudioRoles.js";

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
