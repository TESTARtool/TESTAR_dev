import test from "node:test";
import assert from "node:assert/strict";

import {
    TEST_ORACLE_PANEL_IDS,
    TEST_ORACLE_PANELS,
    TEST_ORACLE_SETTING_KEYS,
    activeOracleSummaries,
    balancedOracleNameColumns,
    closedOracleDeleteDialog,
    dslCompletionGroupsForContext,
    dslDiagnosticText,
    dslFileNameForCreation,
    dslLocalDiagnostics,
    dslMonacoMarkerData,
    extendedOracleCheckboxItems,
    extendedOracleItemsWithEnablement,
    extendedOracleSettingValue,
    hasDslOracleMetadata,
    javaOracleSourceFiles,
    normalizedDslOracleMetadata,
    normalizedOraclePanelId,
    oracleDeleteDialogForPath,
    oracleFilesByOrigin,
    oracleSettingsGroupId,
    settingValueByKey,
    testOracleFilePanels,
    testOracleRootPanels
} from "../src/testOraclesModel.js";

const BACKEND_DSL_METADATA = {
    keywords: [
        "all",
        "and",
        "are",
        "assert",
        "contains",
        "ends",
        "for",
        "has",
        "import",
        "in",
        "is",
        "matches",
        "nonempty",
        "not",
        "of",
        "one",
        "or",
        "package",
        "spell",
        "starts",
        "unless",
        "when",
        "with"
    ],
    widgetTypes: [
        "button",
        "checkbox",
        "image",
        "static_text",
        "table",
        "table_data"
    ],
    fieldNames: [
        "alttext",
        "backgroundColor",
        "checked",
        "clickable",
        "enabled",
        "focused",
        "text",
        "visible"
    ],
    rootKeywords: [
        "assert",
        "context",
        "import",
        "package",
        "pattern"
    ],
    conditionOperators: [
        "is",
        "are",
        "has nonempty",
        "contains",
        "spell checks",
        "spell checks in",
        "starts with",
        "ends with",
        "matches",
        "is one of",
        "is equal to",
        "not"
    ],
    connectorKeywords: [
        "and",
        "or",
        "unless",
        "when"
    ],
    locales: [
        "en_US"
    ],
    fieldsByWidgetType: {
        static_text: [
            { name: "enabled", type: "bool" },
            { name: "focused", type: "bool" },
            { name: "text", type: "str" },
            { name: "visible", type: "bool" }
        ],
        table: [
            { name: "enabled", type: "bool" },
            { name: "text", type: "str" },
            { name: "visible", type: "bool" }
        ],
        table_data: [
            { name: "backgroundColor", type: "str" },
            { name: "text", type: "str" },
            { name: "visible", type: "bool" }
        ]
    }
};

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

test("normalizes DSL oracle metadata from backend values", () => {
    const metadata = normalizedDslOracleMetadata({
        keywords: ["assert"],
        widgetTypes: ["custom_widget"],
        fieldNames: ["customField"],
        rootKeywords: ["assert"],
        conditionOperators: ["is"],
        connectorKeywords: ["when"],
        locales: ["en_US"],
        fieldsByWidgetType: {
            custom_widget: [
                { name: "customField", type: "str" }
            ]
        }
    });

    assert.deepEqual(metadata.keywords, ["assert"]);
    assert.deepEqual(metadata.widgetTypes, ["custom_widget"]);
    assert.deepEqual(metadata.fieldNames, ["customField"]);
    assert.deepEqual(metadata.rootKeywords, ["assert"]);
    assert.deepEqual(metadata.conditionOperators, ["is"]);
    assert.deepEqual(metadata.connectorKeywords, ["when"]);
    assert.deepEqual(metadata.locales, ["en_US"]);
    assert.deepEqual(metadata.fieldsByWidgetType.custom_widget, [
        { name: "customField", type: "str" }
    ]);
});

test("requires backend DSL metadata for DSL editor assistance", () => {
    const metadata = normalizedDslOracleMetadata(null);

    assert.deepEqual(metadata.keywords, []);
    assert.deepEqual(metadata.widgetTypes, []);
    assert.deepEqual(metadata.fieldNames, []);
    assert.deepEqual(metadata.rootKeywords, []);
    assert.deepEqual(metadata.conditionOperators, []);
    assert.deepEqual(metadata.connectorKeywords, []);
    assert.deepEqual(metadata.locales, []);
    assert.deepEqual(metadata.fieldsByWidgetType, {});
    assert.equal(hasDslOracleMetadata(metadata), false);
});

test("skips metadata-dependent DSL diagnostics without backend metadata", () => {
    const diagnostics = dslLocalDiagnostics("assert static_te \"Welcome John Demo\" is visible.");

    assert.deepEqual(diagnostics, []);
});

test("uses provided DSL metadata for widget diagnostics", () => {
    const metadata = normalizedDslOracleMetadata({
        widgetTypes: ["custom_widget"],
        fieldNames: ["visible"],
        keywords: ["assert"],
        conditionOperators: ["is"]
    });
    const diagnostics = dslLocalDiagnostics(
        "assert custom_widget \"Demo\" is visible \"DSL: custom widget is visible\".",
        metadata
    );

    assert.deepEqual(diagnostics, []);
});

test("suggests only model fields after it dot", () => {
    const completions = dslCompletionGroupsForContext(
        "assert for all table_data\n  it.",
        2,
        6,
        BACKEND_DSL_METADATA
    );

    assert.deepEqual(completions.fieldNames, BACKEND_DSL_METADATA.fieldNames);
    assert.deepEqual(completions.keywords, []);
    assert.deepEqual(completions.widgetTypes, []);
    assert.deepEqual(completions.locales, []);
    assert.equal(completions.snippets, false);
});

test("does not suggest DSL completions on empty whitespace", () => {
    const completions = dslCompletionGroupsForContext("  ", 1, 3, BACKEND_DSL_METADATA);

    assert.deepEqual(completions, {
        keywords: [],
        widgetTypes: [],
        fieldNames: [],
        locales: [],
        snippets: false
    });
});

test("suggests locales only after spell checks in", () => {
    const completions = dslCompletionGroupsForContext(
        "it.text spell checks in ",
        1,
        25,
        BACKEND_DSL_METADATA
    );

    assert.deepEqual(completions.locales, BACKEND_DSL_METADATA.locales);
    assert.deepEqual(completions.keywords, []);
    assert.deepEqual(completions.widgetTypes, []);
    assert.deepEqual(completions.fieldNames, []);
});

test("suggests widget types after assert for all", () => {
    const completions = dslCompletionGroupsForContext("assert for all ", 1, 16, BACKEND_DSL_METADATA);

    assert.deepEqual(completions.widgetTypes, BACKEND_DSL_METADATA.widgetTypes);
    assert.deepEqual(completions.keywords, []);
    assert.deepEqual(completions.fieldNames, []);
});

test("suggests model fields after is condition", () => {
    const completions = dslCompletionGroupsForContext(
        "assert static_text \"Welcome John Demo\" is fo",
        1,
        43,
        BACKEND_DSL_METADATA
    );

    assert.ok(completions.fieldNames.includes("focused"));
    assert.ok(completions.fieldNames.includes("enabled"));
    assert.deepEqual(completions.keywords, []);
    assert.deepEqual(completions.locales, []);
});

test("suggests model fields after an empty is condition", () => {
    const completions = dslCompletionGroupsForContext(
        "assert static_text \"Welcome John Demo\" is ",
        1,
        43,
        BACKEND_DSL_METADATA
    );

    assert.ok(completions.fieldNames.includes("focused"));
    assert.ok(completions.fieldNames.includes("enabled"));
    assert.deepEqual(completions.keywords, []);
    assert.deepEqual(completions.widgetTypes, []);
    assert.deepEqual(completions.locales, []);
});

test("suggests root keywords from backend metadata while typing a statement", () => {
    const completions = dslCompletionGroupsForContext("asse", 1, 5, BACKEND_DSL_METADATA);

    assert.deepEqual(completions.keywords, BACKEND_DSL_METADATA.rootKeywords);
    assert.deepEqual(completions.widgetTypes, []);
    assert.equal(completions.snippets, true);
});

test("detects lightweight DSL diagnostics for obvious local syntax issues", () => {
    const diagnostics = dslLocalDiagnostics("`\n]\n\"missing");
    const messages = diagnostics.map((diagnostic) => diagnostic.message);

    assert.ok(messages.includes("Unexpected character '`'"));
    assert.ok(messages.includes("Unmatched closing bracket ']'"));
    assert.ok(messages.includes("Unclosed string literal"));
    assert.ok(diagnostics.every((diagnostic) => diagnostic.severity === "ERROR"));
});

test("accepts common DSL statement lines without local syntax diagnostics", () => {
    const diagnostics = dslLocalDiagnostics(`package dsl_generated.web_invariants;
pattern twoDecimals = "\\d+\\.\\d{2}$"
context "Parabank | .*" "Account Services"
assert for all table_data
  it.text matches twoDecimals when it.text contains "$"
  "DSL: Dollar values must have two decimals".
context "Parabank | .*" "Customer Care" {
assert button "Send to Customer Care" is clickable
  "DSL: Send to Customer Care button must be clickable".
}`, BACKEND_DSL_METADATA);

    assert.deepEqual(diagnostics, []);
});

test("detects unexpected DSL statement lines and unterminated assertions", () => {
    const diagnostics = dslLocalDiagnostics(`package dsl_generated.demo;
assret for all button
assert for all image
  it has nonempty alttext
  "DSL: Images must have alternative text"`, BACKEND_DSL_METADATA);

    assert.deepEqual(diagnostics.map((diagnostic) => diagnostic.message), [
        "Unexpected DSL statement. Did you mean 'assert'?",
        "Unterminated assert statement. End the assertion message with a period."
    ]);
    assert.equal(diagnostics[0].line, 2);
    assert.equal(diagnostics[0].column, 1);
    assert.equal(diagnostics[0].endColumn, 7);
});

test("marks only the invalid DSL statement keyword", () => {
    const diagnostics = dslLocalDiagnostics(
        "asse static_text \"Welcome John Demo\" is visible",
        BACKEND_DSL_METADATA
    );

    assert.equal(diagnostics[0].message, "Unexpected DSL statement. Did you mean 'assert'?");
    assert.equal(diagnostics[0].column, 1);
    assert.equal(diagnostics[0].endColumn, 5);
});

test("marks unknown DSL status tokens inside assert statements", () => {
    const diagnostics = dslLocalDiagnostics(
        "assert static_text \"Welcome John Demo\" is visib",
        BACKEND_DSL_METADATA
    );

    assert.equal(diagnostics[0].message, "Unknown DSL status or field 'visib'");
    assert.equal(diagnostics[0].column, 43);
    assert.equal(diagnostics[0].endColumn, 48);
});

test("marks invalid condition keyword in direct assert statements", () => {
    const diagnostics = dslLocalDiagnostics(
        "assert static_text \"Welcome John Demo\" i visible",
        BACKEND_DSL_METADATA
    );
    const invalidCondition = diagnostics.find((diagnostic) => diagnostic.message === "Unknown DSL condition 'i'");

    assert.equal(invalidCondition.line, 1);
    assert.equal(invalidCondition.column, 40);
    assert.equal(invalidCondition.endColumn, 41);
});

test("marks invalid connector keyword in predicate continuation lines", () => {
    const diagnostics = dslLocalDiagnostics(
        "it.text matches twoDecimals whe it.text contains \"$\"",
        BACKEND_DSL_METADATA
    );
    const invalidConnector = diagnostics.find((diagnostic) => diagnostic.message === "Unknown DSL connector 'whe'. Did you mean 'when'?");

    assert.equal(invalidConnector.line, 1);
    assert.equal(invalidConnector.column, 29);
    assert.equal(invalidConnector.endColumn, 32);
});

test("marks invalid widget type in direct assert statements", () => {
    const diagnostics = dslLocalDiagnostics(
        "assert static_te \"Welcome John Demo\" is visible",
        BACKEND_DSL_METADATA
    );
    const invalidWidgetType = diagnostics.find((diagnostic) => diagnostic.message === "Unknown DSL widget type 'static_te'");

    assert.equal(invalidWidgetType.line, 1);
    assert.equal(invalidWidgetType.column, 8);
    assert.equal(invalidWidgetType.endColumn, 17);
});

test("marks fields unavailable for the current for all widget type", () => {
    const diagnostics = dslLocalDiagnostics(`assert for all table
  it.backgroundColor spell checks in en_GB
  "DSL: Spell checking for English table headers".`, BACKEND_DSL_METADATA);
    const invalidField = diagnostics.find((diagnostic) =>
        diagnostic.message === "Field 'backgroundColor' is not available for widget type 'table'"
    );

    assert.equal(invalidField.line, 2);
    assert.equal(invalidField.column, 6);
    assert.equal(invalidField.endColumn, 21);
});

test("accepts fields available for the current for all widget type", () => {
    const diagnostics = dslLocalDiagnostics(`assert for all table_data
  it.backgroundColor matches "red"
  "DSL: Table data background color can be checked".`, BACKEND_DSL_METADATA);

    assert.deepEqual(diagnostics, []);
});

test("maps DSL diagnostics to Monaco marker data", () => {
    assert.deepEqual(dslMonacoMarkerData([
        { severity: "ERROR", line: 3, column: 5, endLine: 4, endColumn: 20, message: "Unexpected token" },
        { severity: "WARNING", line: -1, column: -1, message: "No source location" }
    ]), [
        {
            severity: "ERROR",
            message: "Unexpected token",
            startLineNumber: 3,
            startColumn: 5,
            endLineNumber: 4,
            endColumn: 20
        },
        {
            severity: "WARNING",
            message: "No source location",
            startLineNumber: 1,
            startColumn: 1,
            endLineNumber: 1,
            endColumn: 2
        }
    ]);
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
