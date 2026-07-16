import test from "node:test";
import assert from "node:assert/strict";

import {
    ORACLE_COMPOSITION_NODE_ID,
    TEST_ORACLE_PANEL_IDS,
    TEST_ORACLE_SETTING_KEYS,
    activeOracleSummaries,
    normalizedOraclePanelId,
    oracleCompositionNodes,
    oracleSettingsGroupId,
    settingValueByKey
} from "../src/testOraclesModel.js";

function workspaceWithSettings(settingsByKey) {
    return {
        settingsGroups: [
            {
                id: "oracles",
                settings: Object.entries(settingsByKey).map(([key, value]) => ({ key, value }))
            }
        ]
    };
}

test("normalizes unknown oracle panel ids to Active Oracles", () => {
    assert.equal(normalizedOraclePanelId("missing"), TEST_ORACLE_PANEL_IDS.ACTIVE);
    assert.equal(normalizedOraclePanelId(TEST_ORACLE_PANEL_IDS.LOG_REGEX), TEST_ORACLE_PANEL_IDS.LOG_REGEX);
});

test("maps oracle panels to their settings groups", () => {
    assert.equal(oracleSettingsGroupId(TEST_ORACLE_PANEL_IDS.WEBDRIVER_CONSOLE), "webdriver");
    assert.equal(oracleSettingsGroupId(TEST_ORACLE_PANEL_IDS.GUI_REGEX), "oracles");
});

test("defines setting keys for oracle configuration panels", () => {
    assert.deepEqual(TEST_ORACLE_SETTING_KEYS[TEST_ORACLE_PANEL_IDS.GUI_REGEX], [
        "SuspiciousTags",
        "TagsForSuspiciousOracle"
    ]);
    assert.deepEqual(TEST_ORACLE_SETTING_KEYS[TEST_ORACLE_PANEL_IDS.LOG_REGEX], [
        "LogOracle",
        "LogOracleRegex",
        "LogOracleCommands",
        "LogOracleFiles"
    ]);
});

test("keeps only the Custom Oracle Services composition node", () => {
    const nodes = [
        { id: "state", title: "State Service" },
        { id: ORACLE_COMPOSITION_NODE_ID, title: "Custom Oracle Services" },
        { id: "actions", title: "Action Derivation" }
    ];

    assert.deepEqual(oracleCompositionNodes(nodes), [
        { id: ORACLE_COMPOSITION_NODE_ID, title: "Custom Oracle Services" }
    ]);
});

test("reads setting values from any settings group", () => {
    const workspaceDocument = workspaceWithSettings({
        LogOracle: "true"
    });

    assert.equal(settingValueByKey(workspaceDocument, "LogOracle"), "true");
    assert.equal(settingValueByKey(workspaceDocument, "Missing"), "");
});

test("summarizes active oracle mechanisms without policies", () => {
    const summaries = activeOracleSummaries(
        workspaceWithSettings({
            SuspiciousTags: ".*error.*",
            ProcessListener: "false",
            WebConsoleErrorOracle: "true",
            WebConsoleWarningOracle: "false",
            LogOracle: "true",
            LogOracleRegex: ".*Exception.*",
            ExtendedOracles: "A,B"
        }),
        { configuredClassName: "CustomOracleComposer" }
    );

    assert.equal(summaries.find((item) => item.label === "GUI Regex Oracles").active, true);
    assert.equal(summaries.find((item) => item.label === "Windows Process Oracles").active, false);
    assert.equal(summaries.find((item) => item.label === "WebDriver Console Oracles").detail, "error console");
    assert.equal(summaries.find((item) => item.label === "Log Regex Oracles").detail, ".*Exception.*");
    assert.equal(summaries.find((item) => item.label === "Extended Oracles").detail, "2 configured");
    assert.equal(summaries.find((item) => item.label === "Java Oracle Composition").detail, "CustomOracleComposer");
});
