export const TEST_ORACLE_PANEL_IDS = {
    ACTIVE: "active-oracles",
    GUI_REGEX: "gui-regex-oracles",
    WINDOWS_PROCESS: "windows-process-oracles",
    WEBDRIVER_CONSOLE: "webdriver-console-oracles",
    LOG_REGEX: "log-regex-oracles",
    EXTENDED: "extended-oracles",
    JAVA_COMPOSITION: "java-oracle-composition"
};

export const ORACLE_COMPOSITION_NODE_ID = "oracle-services";

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
        id: TEST_ORACLE_PANEL_IDS.EXTENDED,
        label: "Extended Oracles",
        description: "Configure extended oracle checks."
    },
    {
        id: TEST_ORACLE_PANEL_IDS.JAVA_COMPOSITION,
        label: "Java Oracle Composition",
        description: "Edit the Custom Oracle Services composition node."
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
    [TEST_ORACLE_PANEL_IDS.EXTENDED]: [
        "ExtendedOracles"
    ]
};

export function normalizedOraclePanelId(panelId) {
    return TEST_ORACLE_PANELS.some((panel) => panel.id === panelId)
        ? panelId
        : TEST_ORACLE_PANEL_IDS.ACTIVE;
}

export function oracleCompositionNodes(compositionFlowNodes = []) {
    return compositionFlowNodes.filter((flowNode) => flowNode?.id === ORACLE_COMPOSITION_NODE_ID);
}

export function oracleSettingsGroupId(panelId) {
    if (panelId === TEST_ORACLE_PANEL_IDS.WEBDRIVER_CONSOLE) {
        return "webdriver";
    }

    return "oracles";
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

export function activeOracleSummaries(workspaceDocument, oracleCompositionNode = null) {
    const extendedOracles = settingValueByKey(workspaceDocument, "ExtendedOracles")
        .split(",")
        .map((item) => item.trim())
        .filter(Boolean);

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
                ? `${extendedOracles.length} configured`
                : "No extended oracles configured."
        },
        {
            label: "Java Oracle Composition",
            active: Boolean(oracleCompositionNode?.configuredClassName),
            detail: oracleCompositionNode?.configuredClassName || "Using default oracle composition."
        }
    ];
}
