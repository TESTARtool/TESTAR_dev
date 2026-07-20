export const TEST_ORACLE_PANEL_IDS = {
    ACTIVE: "active-oracles",
    GUI_REGEX: "gui-regex-oracles",
    WINDOWS_PROCESS: "windows-process-oracles",
    WEBDRIVER_CONSOLE: "webdriver-console-oracles",
    LOG_REGEX: "log-regex-oracles",
    EXTENDED_ENABLEMENT: "extended-oracle-enablement",
    JAVA_FILES: "java-oracle-files",
    DSL_FILES: "dsl-oracle-files"
};

export const TEST_ORACLE_PANELS = [
    {
        id: TEST_ORACLE_PANEL_IDS.ACTIVE,
        label: "Active Oracles",
        description: "Read-only summary of configured oracle mechanisms."
    },
    {
        id: TEST_ORACLE_PANEL_IDS.GUI_REGEX,
        label: "GUI Regex Oracles",
        description: "Configure suspicious GUI text matching."
    },
    {
        id: TEST_ORACLE_PANEL_IDS.WINDOWS_PROCESS,
        label: "Windows Process Oracles",
        description: "Configure Windows process oracles."
    },
    {
        id: TEST_ORACLE_PANEL_IDS.WEBDRIVER_CONSOLE,
        label: "WebDriver Console Oracles",
        description: "Configure browser console matching."
    },
    {
        id: TEST_ORACLE_PANEL_IDS.LOG_REGEX,
        label: "Log Regex Oracles",
        description: "Configure log file and command output matching."
    },
    {
        id: TEST_ORACLE_PANEL_IDS.EXTENDED_ENABLEMENT,
        label: "Enable Extended Oracles",
        description: "Enable or disable built-in, workspace Java, and generated DSL Java oracles."
    },
    {
        id: TEST_ORACLE_PANEL_IDS.JAVA_FILES,
        label: "Java Oracle Files",
        description: "Manage workspace Java oracle source files."
    },
    {
        id: TEST_ORACLE_PANEL_IDS.DSL_FILES,
        label: "DSL Oracle Files",
        description: "Manage workspace DSL oracle source files."
    }
];

export const TEST_ORACLE_SETTING_KEYS = {
    [TEST_ORACLE_PANEL_IDS.GUI_REGEX]: [
        "SuspiciousTags",
        "TagsForSuspiciousOracle"
    ],
    [TEST_ORACLE_PANEL_IDS.WINDOWS_PROCESS]: [
        "ProcessListener",
        "SuspiciousProcessOutput",
        "TimeToFreeze"
    ],
    [TEST_ORACLE_PANEL_IDS.WEBDRIVER_CONSOLE]: [
        "WebConsoleErrorOracle",
        "WebConsoleErrorPattern",
        "WebConsoleWarningOracle",
        "WebConsoleWarningPattern"
    ],
    [TEST_ORACLE_PANEL_IDS.LOG_REGEX]: [
        "LogOracle",
        "LogOracleRegex",
        "LogOracleCommands",
        "LogOracleFiles"
    ],
    [TEST_ORACLE_PANEL_IDS.EXTENDED_ENABLEMENT]: [
        "ExtendedOracles"
    ]
};

export function normalizedOraclePanelId(panelId) {
    return TEST_ORACLE_PANELS.some((panel) => panel.id === panelId)
        ? panelId
        : TEST_ORACLE_PANEL_IDS.ACTIVE;
}

export function oracleSettingsGroupId(panelId) {
    if (panelId === TEST_ORACLE_PANEL_IDS.WEBDRIVER_CONSOLE) {
        return "webdriver";
    }

    return "oracles";
}

export function testOracleRootPanels() {
    return TEST_ORACLE_PANELS.filter((panel) =>
        panel.id !== TEST_ORACLE_PANEL_IDS.JAVA_FILES
        && panel.id !== TEST_ORACLE_PANEL_IDS.DSL_FILES
    );
}

export function testOracleFilePanels() {
    return TEST_ORACLE_PANELS.filter((panel) =>
        panel.id === TEST_ORACLE_PANEL_IDS.JAVA_FILES
        || panel.id === TEST_ORACLE_PANEL_IDS.DSL_FILES
    );
}

export function settingValueByKey(workspaceDocument, settingKey) {
    for (const settingsGroup of workspaceDocument?.settingsGroups || []) {
        const setting = (settingsGroup.settings || []).find((item) => item.key === settingKey);
        if (setting) {
            return setting.value || "";
        }
    }

    return "";
}

export function settingBooleanValue(workspaceDocument, settingKey) {
    return settingValueByKey(workspaceDocument, settingKey).toLowerCase() === "true";
}

export function activeExtendedOracleNames(workspaceDocument) {
    return settingValueByKey(workspaceDocument, "ExtendedOracles")
        .split(",")
        .map((item) => item.trim())
        .filter(Boolean);
}

export function extendedOracleCheckboxItems(testOracleInventory = null) {
    const items = Array.isArray(testOracleInventory?.items) ? testOracleInventory.items : [];

    return items
        .filter((item) => item.origin === "BUILT_IN" || item.origin === "WORKSPACE_JAVA")
        .map((item) => ({
            ...item,
            sourceLabel: item.origin === "BUILT_IN" ? "Built-in" : "Workspace Java"
        }))
        .sort((left, right) => {
            if (left.origin !== right.origin) {
                return left.origin === "BUILT_IN" ? -1 : 1;
            }

            return left.name.localeCompare(right.name);
        });
}

export function oracleFilesByOrigin(testOracleInventory = null, origin) {
    const items = Array.isArray(testOracleInventory?.items) ? testOracleInventory.items : [];

    return items
        .filter((item) => item.origin === origin)
        .sort((left, right) => (left.path || left.name).localeCompare(right.path || right.name));
}

export function javaOracleSourceFiles(testOracleInventory = null) {
    const filesByPath = new Map();
    for (const item of oracleFilesByOrigin(testOracleInventory, "WORKSPACE_JAVA")) {
        const path = item.path || item.name;
        if (!filesByPath.has(path)) {
            filesByPath.set(path, {
                ...item,
                name: path.split("/").pop() || item.name,
                oracleCount: 0,
                oracleNames: []
            });
        }

        const file = filesByPath.get(path);
        file.oracleCount += 1;
        if (item.name) {
            file.oracleNames.push(item.name);
        }
    }

    return Array.from(filesByPath.values());
}

export function extendedOracleSettingValue(oracleItems = []) {
    return oracleItems
        .filter((item) => item.active)
        .map((item) => item.name)
        .filter(Boolean)
        .join(",");
}

export function extendedOracleItemsWithEnablement(oracleItems = [], enabled) {
    return oracleItems.map((item) => ({
        ...item,
        active: enabled
    }));
}

export function dslDiagnosticText(diagnostic = {}) {
    const severity = diagnostic.severity || "INFO";
    const message = diagnostic.message || "";
    if (diagnostic.line > 0) {
        return `${severity} at ${diagnostic.line}:${diagnostic.column || 1} - ${message}`;
    }

    return `${severity} - ${message}`;
}

export function dslFileNameForCreation(value) {
    const trimmedValue = (value || "").trim().replace(/\\/g, "/");
    if (!trimmedValue) {
        return "";
    }

    const fileName = trimmedValue.split("/").filter(Boolean).pop() || "";
    return fileName.toLowerCase().endsWith(".testar") ? fileName : `${fileName}.testar`;
}

export function closedOracleDeleteDialog() {
    return {
        open: false,
        type: "",
        path: "",
        title: "",
        message: ""
    };
}

export function oracleDeleteDialogForPath(path, type) {
    if (!path) {
        return closedOracleDeleteDialog();
    }

    const isJava = type === "java";
    return {
        open: true,
        type,
        path,
        title: isJava ? "Delete Java Oracle File" : "Delete DSL Oracle File",
        message: isJava
            ? "This deletes the selected workspace Java oracle file."
            : "This deletes the selected workspace DSL oracle file."
    };
}

export function activeOracleSummaries(workspaceDocument) {
    const extendedOracles = activeExtendedOracleNames(workspaceDocument);

    return [
        {
            label: "GUI Regex Oracles",
            active: settingValueByKey(workspaceDocument, "SuspiciousTags").trim() !== "",
            detail: settingValueByKey(workspaceDocument, "SuspiciousTags") || "No suspicious GUI regex configured."
        },
        {
            label: "Windows Process Oracles",
            active: settingBooleanValue(workspaceDocument, "ProcessListener"),
            detail: settingBooleanValue(workspaceDocument, "ProcessListener")
                ? settingValueByKey(workspaceDocument, "SuspiciousProcessOutput")
                : "Process listener is disabled."
        },
        {
            label: "WebDriver Console Oracles",
            active: settingBooleanValue(workspaceDocument, "WebConsoleErrorOracle")
                || settingBooleanValue(workspaceDocument, "WebConsoleWarningOracle"),
            detail: [
                settingBooleanValue(workspaceDocument, "WebConsoleErrorOracle") ? "error console" : "",
                settingBooleanValue(workspaceDocument, "WebConsoleWarningOracle") ? "warning console" : ""
            ].filter(Boolean).join(", ") || "Browser console oracles are disabled."
        },
        {
            label: "Log Regex Oracles",
            active: settingBooleanValue(workspaceDocument, "LogOracle"),
            detail: settingBooleanValue(workspaceDocument, "LogOracle")
                ? settingValueByKey(workspaceDocument, "LogOracleRegex")
                : "LogOracle is disabled."
        },
        {
            label: "Extended Oracles",
            active: extendedOracles.length > 0,
            detail: extendedOracles.length > 0
                ? `${extendedOracles.length} enabled`
                : "No extended oracles enabled."
        }
    ];
}
