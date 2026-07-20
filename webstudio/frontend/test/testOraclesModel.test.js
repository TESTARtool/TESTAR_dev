import test from "node:test";
import assert from "node:assert/strict";

import {
    TEST_ORACLE_PANEL_IDS,
    TEST_ORACLE_PANELS,
    TEST_ORACLE_SETTING_KEYS,
    activeOracleSummaries,
    balancedOracleNameColumns,
    closedOracleDeleteDialog,
    dslDiagnosticText,
    dslFileNameForCreation,
    extendedOracleCheckboxItems,
    extendedOracleItemsWithEnablement,
    extendedOracleSettingValue,
    javaOracleSourceFiles,
    normalizedOraclePanelId,
    oracleDeleteDialogForPath,
    oracleFilesByOrigin,
    oracleSettingsGroupId,
    settingValueByKey,
    testOracleFilePanels,
    testOracleRootPanels
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

test("reads setting values from any settings group", () => {
    const workspaceDocument = workspaceWithSettings({
        LogOracle: "true"
    });

    assert.equal(settingValueByKey(workspaceDocument, "LogOracle"), "true");
    assert.equal(settingValueByKey(workspaceDocument, "Missing"), "");
});

test("summarizes active oracle mechanisms without policies", () => {
    const summaries = activeOracleSummaries(workspaceWithSettings({
        SuspiciousTags: ".*error.*",
        ProcessListener: "false",
        WebConsoleErrorOracle: "true",
        WebConsoleWarningOracle: "false",
        LogOracle: "true",
        LogOracleRegex: ".*Exception.*",
        ExtendedOracles: "A,B"
    }));

    assert.equal(summaries.find((item) => item.label === "GUI Regex Oracles").active, true);
    assert.equal(summaries.find((item) => item.label === "Windows Process Oracles").active, false);
    assert.equal(summaries.find((item) => item.label === "WebDriver Console Oracles").detail, "error console");
    assert.equal(summaries.find((item) => item.label === "Log Regex Oracles").detail, ".*Exception.*");
    assert.equal(summaries.find((item) => item.label === "Extended Oracles").detail, "2 enabled");
    assert.deepEqual(summaries.find((item) => item.label === "Extended Oracles").classNames, ["A", "B"]);
    assert.deepEqual(summaries.find((item) => item.label === "Extended Oracles").classNameColumns, [["A"], ["B"], []]);
    assert.equal(summaries.length, 5);
});

test("splits enabled extended oracle names into three balanced vertical columns", () => {
    assert.deepEqual(balancedOracleNameColumns(["A", "B", "C", "D", "E"]), [
        ["A", "B"],
        ["C", "D"],
        ["E"]
    ]);
    assert.deepEqual(balancedOracleNameColumns([]), [[], [], []]);
});

test("uses dedicated Extended Java DSL oracle panels", () => {
    const panelLabels = TEST_ORACLE_PANELS.map((panel) => panel.label);

    assert.ok(panelLabels.includes("Enable Extended Oracles"));
    assert.ok(panelLabels.includes("Java Oracle Files"));
    assert.ok(panelLabels.includes("DSL Oracle Files"));
    assert.equal(TEST_ORACLE_PANEL_IDS.EXTENDED_ENABLEMENT, "extended-oracle-enablement");
    assert.equal(Object.hasOwn(TEST_ORACLE_PANEL_IDS, "EXTENDED"), false);
    assert.equal(Object.hasOwn(TEST_ORACLE_PANEL_IDS, "JAVA_COMPOSITION"), false);
});

test("groups Java and DSL oracle files under Enable Extended Oracles navigation", () => {
    assert.deepEqual(testOracleFilePanels().map((panel) => panel.label), [
        "Java Oracle Files",
        "DSL Oracle Files"
    ]);
    assert.ok(testOracleRootPanels().some((panel) => panel.label === "Enable Extended Oracles"));
    assert.equal(testOracleRootPanels().some((panel) => panel.label === "Java Oracle Files"), false);
    assert.equal(testOracleRootPanels().some((panel) => panel.label === "DSL Oracle Files"), false);
});

test("builds checkbox items for built-in and workspace Java oracles", () => {
    const items = extendedOracleCheckboxItems({
        items: [
            { name: "LoginDsl", origin: "DSL_SOURCE", path: "login.testar", active: false },
            { name: "WorkspaceOracle", origin: "WORKSPACE_JAVA", path: "WorkspaceOracle.java", active: true },
            { name: "BuiltInOracle", origin: "BUILT_IN", path: "", active: false }
        ]
    });

    assert.deepEqual(items.map((item) => item.name), ["BuiltInOracle", "WorkspaceOracle"]);
    assert.deepEqual(items.map((item) => item.sourceLabel), ["Built-in", "Workspace Java"]);
});

test("filters oracle inventory files by origin", () => {
    const inventory = {
        items: [
            { name: "B.testar", origin: "DSL_SOURCE", path: "b/B.testar" },
            { name: "A.java", origin: "WORKSPACE_JAVA", path: "A.java" },
            { name: "A.testar", origin: "DSL_SOURCE", path: "a/A.testar" }
        ]
    };

    assert.deepEqual(
        oracleFilesByOrigin(inventory, "DSL_SOURCE").map((item) => item.path),
        ["a/A.testar", "b/B.testar"]
    );
});

test("deduplicates Java oracle source files for editor list", () => {
    const inventory = {
        items: [
            { name: "FirstInvariant", origin: "WORKSPACE_JAVA", path: "parabank/invariants.java" },
            { name: "SecondInvariant", origin: "WORKSPACE_JAVA", path: "parabank/invariants.java" },
            { name: "OtherOracle", origin: "WORKSPACE_JAVA", path: "other/OtherOracle.java" }
        ]
    };

    const files = javaOracleSourceFiles(inventory);

    assert.deepEqual(files.map((item) => item.path), [
        "other/OtherOracle.java",
        "parabank/invariants.java"
    ]);
    assert.equal(files.find((item) => item.path === "parabank/invariants.java").oracleCount, 2);
    assert.deepEqual(files.find((item) => item.path === "parabank/invariants.java").oracleNames, [
        "FirstInvariant",
        "SecondInvariant"
    ]);
});

test("serializes enabled extended oracle names", () => {
    assert.equal(extendedOracleSettingValue([
        { name: "FirstOracle", active: true },
        { name: "SecondOracle", active: false },
        { name: "ThirdOracle", active: true }
    ]), "FirstOracle,ThirdOracle");
});

test("updates all extended oracle items with the requested enablement", () => {
    const items = extendedOracleItemsWithEnablement([
        { name: "FirstOracle", active: true },
        { name: "SecondOracle", active: false }
    ], true);

    assert.deepEqual(items.map((item) => item.active), [true, true]);
    assert.deepEqual(items.map((item) => item.name), ["FirstOracle", "SecondOracle"]);
});

test("formats DSL diagnostics with and without source locations", () => {
    assert.equal(
        dslDiagnosticText({ severity: "ERROR", line: 4, column: 9, message: "Unexpected token" }),
        "ERROR at 4:9 - Unexpected token"
    );
    assert.equal(
        dslDiagnosticText({ severity: "WARNING", line: -1, column: -1, message: "No source location" }),
        "WARNING - No source location"
    );
});

test("normalizes DSL creation input to a root-level testar file name", () => {
    assert.equal(dslFileNameForCreation("login"), "login.testar");
    assert.equal(dslFileNameForCreation("login.testar"), "login.testar");
    assert.equal(dslFileNameForCreation("parabank/login"), "login.testar");
    assert.equal(dslFileNameForCreation("parabank\\login.testar"), "login.testar");
    assert.equal(dslFileNameForCreation("   "), "");
});

test("builds oracle source delete confirmation dialogs", () => {
    assert.deepEqual(closedOracleDeleteDialog(), {
        open: false,
        type: "",
        path: "",
        title: "",
        message: ""
    });

    const javaDialog = oracleDeleteDialogForPath("parabank/invariants.java", "java");
    assert.equal(javaDialog.open, true);
    assert.equal(javaDialog.title, "Delete Java Oracle File");
    assert.equal(javaDialog.path, "parabank/invariants.java");

    const dslDialog = oracleDeleteDialogForPath("parabank/invariants.testar", "dsl");
    assert.equal(dslDialog.open, true);
    assert.equal(dslDialog.title, "Delete DSL Oracle File");
    assert.equal(dslDialog.path, "parabank/invariants.testar");
});
