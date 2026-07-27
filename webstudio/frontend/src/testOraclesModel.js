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
            description: "Enable or disable workspace Java and generated DSL Java oracles."
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

export function normalizedDslOracleMetadata(metadata = null) {
    return {
        keywords: arrayValue(metadata?.keywords),
        widgetTypes: arrayValue(metadata?.widgetTypes),
        fieldNames: arrayValue(metadata?.fieldNames),
        rootKeywords: arrayValue(metadata?.rootKeywords),
        conditionOperators: arrayValue(metadata?.conditionOperators),
        connectorKeywords: arrayValue(metadata?.connectorKeywords),
        locales: arrayValue(metadata?.locales),
        fieldsByWidgetType: objectValue(metadata?.fieldsByWidgetType)
    };
}

export function dslCompletionGroupsForContext(source = "", lineNumber = 1, column = 1, metadata = null) {
    const normalizedMetadata = normalizedDslOracleMetadata(metadata);
    if (!hasDslOracleMetadata(normalizedMetadata)) {
        return emptyDslCompletionGroups();
    }

    const linePrefix = dslLinePrefix(source, lineNumber, column);
    const trimmedPrefix = linePrefix.trimStart();
    if (trimmedPrefix === "") {
        return emptyDslCompletionGroups();
    }

    if (/\bit\.[A-Za-z_]*$/.test(linePrefix)) {
        return {
            ...emptyDslCompletionGroups(),
            fieldNames: normalizedMetadata.fieldNames
        };
    }

    if (/\bspell\s+checks\s+in\s+[A-Za-z_]*$/.test(linePrefix)) {
        return {
            ...emptyDslCompletionGroups(),
            locales: normalizedMetadata.locales
        };
    }

    if (/\b(?:is|are)\s+[A-Za-z_]*$/.test(linePrefix)) {
        return {
            ...emptyDslCompletionGroups(),
            fieldNames: normalizedMetadata.fieldNames
        };
    }

    if (/\bassert\s+for\s+all(?:\s+|$)(?:[A-Za-z_]\w*\s*,\s*)*[A-Za-z_]*$/.test(linePrefix)
        || /\bassert\s+[A-Za-z_]*$/.test(linePrefix)) {
        return {
            ...emptyDslCompletionGroups(),
            widgetTypes: normalizedMetadata.widgetTypes
        };
    }

    if (/^[A-Za-z_]*$/.test(trimmedPrefix) && isRootKeywordPrefix(trimmedPrefix, normalizedMetadata.rootKeywords)) {
        return {
            ...emptyDslCompletionGroups(),
            keywords: normalizedMetadata.rootKeywords,
            snippets: true
        };
    }

    return emptyDslCompletionGroups();
}

export function hasDslOracleMetadata(metadata = null) {
    const normalizedMetadata = normalizedDslOracleMetadata(metadata);

    return normalizedMetadata.keywords.length > 0
        && normalizedMetadata.widgetTypes.length > 0
        && normalizedMetadata.fieldNames.length > 0
        && normalizedMetadata.rootKeywords.length > 0
        && normalizedMetadata.conditionOperators.length > 0
        && normalizedMetadata.connectorKeywords.length > 0;
}

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

export function balancedOracleNameColumns(oracleNames = []) {
    const columnCount = 3;
    const columnSize = Math.ceil(oracleNames.length / columnCount);
    return [
        oracleNames.slice(0, columnSize),
        oracleNames.slice(columnSize, columnSize * 2),
        oracleNames.slice(columnSize * 2)
    ];
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

export function dslLocalDiagnostics(source = "", metadata = null) {
    const dslMetadata = normalizedDslOracleMetadata(metadata);
    const hasMetadata = hasDslOracleMetadata(dslMetadata);
    const diagnostics = [];
    const bracketStack = [];
    const lines = String(source || "").split(/\r?\n/);
    let inString = false;
    let stringStartLine = 1;
    let stringStartColumn = 1;
    let openAssertLine = 0;
    let activeForAllWidgetType = "";

    for (let lineIndex = 0; lineIndex < lines.length; lineIndex++) {
        const line = lines[lineIndex];
        const lineNumber = lineIndex + 1;
        const trimmedLine = line.trim();
        if (!isDslLineAllowed(trimmedLine)) {
            const invalidToken = firstTokenRange(line);
            diagnostics.push({
                severity: "ERROR",
                line: lineNumber,
                column: invalidToken.column,
                endColumn: invalidToken.endColumn,
                message: unexpectedDslStatementMessage(invalidToken.token)
            });
        }

        if (hasMetadata) {
            const lineForAllWidgetType = inferredForAllWidgetType(maskQuotedText(line), dslMetadata);
            if (lineForAllWidgetType) {
                activeForAllWidgetType = lineForAllWidgetType;
            }

            diagnostics.push(...dslLineTokenDiagnostics(line, lineNumber, dslMetadata, activeForAllWidgetType));
        }

        if (startsDslAssert(trimmedLine) && !endsDslStatement(trimmedLine)) {
            openAssertLine = lineNumber;
        } else if (openAssertLine > 0 && endsDslStatement(trimmedLine)) {
            openAssertLine = 0;
            activeForAllWidgetType = "";
        }

        for (let columnIndex = 0; columnIndex < line.length; columnIndex++) {
            const character = line[columnIndex];
            const columnNumber = columnIndex + 1;

            if (inString) {
                if (character === "\"" && !isEscaped(line, columnIndex)) {
                    inString = false;
                }
                continue;
            }

            if (character === "/" && line[columnIndex + 1] === "/") {
                break;
            }

            if (character === "\"") {
                inString = true;
                stringStartLine = lineNumber;
                stringStartColumn = columnNumber;
                continue;
            }

            if (character === "(" || character === "[" || character === "{") {
                bracketStack.push({
                    character,
                    line: lineNumber,
                    column: columnNumber
                });
                continue;
            }

            if (character === ")" || character === "]" || character === "}") {
                const lastBracket = bracketStack.pop();
                if (!lastBracket || !bracketsMatch(lastBracket.character, character)) {
                    diagnostics.push({
                        severity: "ERROR",
                        line: lineNumber,
                        column: columnNumber,
                        message: `Unmatched closing bracket '${character}'`
                    });
                }
                continue;
            }

            if (character === "`" || character === "@") {
                diagnostics.push({
                    severity: "ERROR",
                    line: lineNumber,
                    column: columnNumber,
                    message: `Unexpected character '${character}'`
                });
            }
        }
    }

    if (inString) {
        diagnostics.push({
            severity: "ERROR",
            line: stringStartLine,
            column: stringStartColumn,
            message: "Unclosed string literal"
        });
    }

    if (openAssertLine > 0) {
        diagnostics.push({
            severity: "ERROR",
            line: openAssertLine,
            column: Math.max(lines[openAssertLine - 1].length, 1),
            endColumn: Math.max(lines[openAssertLine - 1].length + 1, 2),
            message: "Unterminated assert statement. End the assertion message with a period."
        });
    }

    for (const bracket of bracketStack) {
        diagnostics.push({
            severity: "ERROR",
            line: bracket.line,
            column: bracket.column,
            message: `Unclosed bracket '${bracket.character}'`
        });
    }

    return diagnostics;
}

export function dslMonacoMarkerData(diagnostics = []) {
    return diagnostics.map((diagnostic) => {
        const line = diagnostic.line > 0 ? diagnostic.line : 1;
        const column = diagnostic.column > 0 ? diagnostic.column : 1;
        const endLine = diagnostic.endLine > 0 ? diagnostic.endLine : line;
        return {
            severity: diagnostic.severity || "INFO",
            message: diagnostic.message || "",
            startLineNumber: line,
            startColumn: column,
            endLineNumber: endLine,
            endColumn: diagnostic.endColumn > column ? diagnostic.endColumn : column + 1
        };
    });
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

function isEscaped(line, columnIndex) {
    let slashCount = 0;
    for (let index = columnIndex - 1; index >= 0 && line[index] === "\\"; index--) {
        slashCount += 1;
    }

    return slashCount % 2 === 1;
}

function bracketsMatch(openingBracket, closingBracket) {
    return (openingBracket === "(" && closingBracket === ")")
        || (openingBracket === "[" && closingBracket === "]")
        || (openingBracket === "{" && closingBracket === "}");
}

function isDslLineAllowed(trimmedLine) {
    return trimmedLine === ""
        || trimmedLine === "}"
        || trimmedLine === "{"
        || trimmedLine.startsWith("//")
        || /^package\s+[A-Za-z_][\w.]*\s*;?$/.test(trimmedLine)
        || /^pattern\s+[A-Za-z_]\w*\s*=/.test(trimmedLine)
        || /^context\s+"/.test(trimmedLine)
        || startsDslAssert(trimmedLine)
        || /^it(?:\.|\s+)/.test(trimmedLine)
        || /^is\s+/.test(trimmedLine)
        || /^are\s+/.test(trimmedLine)
        || /^has\s+/.test(trimmedLine)
        || /^not\s+/.test(trimmedLine)
        || /^when\s+/.test(trimmedLine)
        || /^unless\s+/.test(trimmedLine)
        || /^and\s+/.test(trimmedLine)
        || /^or\s+/.test(trimmedLine)
        || /^"([^"\\]|\\.)*"\s*\.?$/.test(trimmedLine);
}

function startsDslAssert(trimmedLine) {
    return /^assert(?:\s|$)/.test(trimmedLine);
}

function endsDslStatement(trimmedLine) {
    return /\.\s*$/.test(trimmedLine);
}

function firstTokenRange(line) {
    const match = /\S+/.exec(line);
    if (!match) {
        return {
            token: "",
            column: 1,
            endColumn: 2
        };
    }

    return {
        token: match[0],
        column: match.index + 1,
        endColumn: match.index + match[0].length + 1
    };
}

function unexpectedDslStatementMessage(token) {
    if (token && isCloseToAssert(token)) {
        return "Unexpected DSL statement. Did you mean 'assert'?";
    }

    return "Unexpected DSL statement";
}

function isCloseToAssert(token) {
    const normalizedToken = String(token || "").toLowerCase();
    return normalizedToken.startsWith("ass") || normalizedToken === "asser";
}

function dslLineTokenDiagnostics(line, lineNumber, metadata, activeForAllWidgetType = "") {
    const diagnostics = [];
    const lineWithoutStrings = maskQuotedText(line);
    const tokens = dslWordTokens(lineWithoutStrings);
    const conditionMatches = lineWithoutStrings.matchAll(/\b(?:is|are)\s+([A-Za-z_]\w*)\b/g);
    const allowedStatusNames = new Set([
        ...metadata.fieldNames,
        "equal",
        "one"
    ]);

    for (const match of conditionMatches) {
        const statusName = match[1];
        if (!allowedStatusNames.has(statusName)) {
            diagnostics.push({
                severity: "ERROR",
                line: lineNumber,
                column: match.index + match[0].lastIndexOf(statusName) + 1,
                endColumn: match.index + match[0].lastIndexOf(statusName) + statusName.length + 1,
                message: `Unknown DSL status or field '${statusName}'`
            });
        }
    }

    diagnostics.push(...assertLineDiagnostics(tokens, lineNumber, metadata));
    diagnostics.push(...widgetFieldDiagnostics(lineWithoutStrings, lineNumber, metadata, activeForAllWidgetType));
    diagnostics.push(...conditionKeywordDiagnostics(tokens, lineNumber, metadata));

    return diagnostics;
}

function maskQuotedText(line) {
    return line.replace(/"([^"\\]|\\.)*"/g, (match) => " ".repeat(match.length));
}

function dslWordTokens(line) {
    const tokens = [];
    for (const match of line.matchAll(/[A-Za-z_]\w*/g)) {
        tokens.push({
            value: match[0],
            column: match.index + 1,
            endColumn: match.index + match[0].length + 1
        });
    }

    return tokens;
}

function assertLineDiagnostics(tokens, lineNumber, metadata) {
    if (tokens.length === 0 || tokens[0].value !== "assert") {
        return [];
    }

    if (tokens[1]?.value === "for" && tokens[2]?.value === "all") {
        return forAllAssertDiagnostics(tokens, lineNumber, metadata);
    }

    return directAssertDiagnostics(tokens, lineNumber, metadata);
}

function directAssertDiagnostics(tokens, lineNumber, metadata) {
    const diagnostics = [];
    const widgetType = tokens[1];
    if (widgetType && !metadata.widgetTypes.includes(widgetType.value)) {
        diagnostics.push({
            severity: "ERROR",
            line: lineNumber,
            column: widgetType.column,
            endColumn: widgetType.endColumn,
            message: `Unknown DSL widget type '${widgetType.value}'`
        });
        return diagnostics;
    }

    const conditionToken = tokens[2];
    if (conditionToken && !isDslConditionKeyword(conditionToken.value, metadata)) {
        diagnostics.push(unknownConditionDiagnostic(conditionToken, lineNumber));
    }

    return diagnostics;
}

function forAllAssertDiagnostics(tokens, lineNumber, metadata) {
    const diagnostics = [];
    const conditionIndex = tokens.findIndex((token, index) => index > 2 && isConditionStartToken(token.value, metadata));
    const elementTokens = conditionIndex > 0 ? tokens.slice(3, conditionIndex) : tokens.slice(3);
    for (const elementToken of elementTokens) {
        if (!metadata.widgetTypes.includes(elementToken.value)) {
            diagnostics.push({
                severity: "ERROR",
                line: lineNumber,
                column: elementToken.column,
                endColumn: elementToken.endColumn,
                message: `Unknown DSL widget type '${elementToken.value}'`
            });
        }
    }

    if (conditionIndex > 0) {
        const conditionToken = tokens[conditionIndex];
        if (!isDslConditionKeyword(conditionToken.value, metadata) && conditionToken.value !== "it") {
            diagnostics.push(unknownConditionDiagnostic(conditionToken, lineNumber));
        }
    }

    return diagnostics;
}

function widgetFieldDiagnostics(lineWithoutStrings, lineNumber, metadata, activeForAllWidgetType = "") {
    const diagnostics = [];
    const widgetType = inferredForAllWidgetType(lineWithoutStrings, metadata) || activeForAllWidgetType;
    if (!widgetType) {
        return diagnostics;
    }

    const fields = fieldNamesForWidgetType(metadata, widgetType);
    if (fields.length === 0) {
        return diagnostics;
    }

    for (const match of lineWithoutStrings.matchAll(/\bit\.([A-Za-z_]\w*)/g)) {
        const fieldName = match[1];
        if (!fields.includes(fieldName)) {
            diagnostics.push({
                severity: "ERROR",
                line: lineNumber,
                column: match.index + 4,
                endColumn: match.index + 4 + fieldName.length,
                message: `Field '${fieldName}' is not available for widget type '${widgetType}'`
            });
        }
    }

    return diagnostics;
}

function inferredForAllWidgetType(lineWithoutStrings, metadata) {
    const match = /\bassert\s+for\s+all\s+([A-Za-z_]\w*)\b/.exec(lineWithoutStrings);
    if (!match || !metadata.widgetTypes.includes(match[1])) {
        return "";
    }

    return match[1];
}

function fieldNamesForWidgetType(metadata, widgetType) {
    const fields = metadata.fieldsByWidgetType[widgetType];
    if (!Array.isArray(fields)) {
        return [];
    }

    return fields
        .map((field) => field?.name)
        .filter(Boolean);
}

function conditionKeywordDiagnostics(tokens, lineNumber, metadata) {
    const diagnostics = [];
    for (const token of tokens) {
        if (shouldSuggestConditionKeyword(token.value, metadata)) {
            diagnostics.push(unknownConditionDiagnostic(token, lineNumber));
        }

        if (shouldSuggestConnectorKeyword(token.value, metadata)) {
            diagnostics.push({
                severity: "ERROR",
                line: lineNumber,
                column: token.column,
                endColumn: token.endColumn,
                message: `Unknown DSL connector '${token.value}'. Did you mean 'when'?`
            });
        }
    }

    return diagnostics;
}

function isConditionStartToken(value, metadata) {
    return value === "it" || isDslConditionKeyword(value, metadata);
}

function isDslConditionKeyword(value, metadata) {
    return conditionStartKeywords(metadata).includes(value);
}

function shouldSuggestConditionKeyword(value, metadata) {
    return likelyIncompleteToken(value, conditionStartKeywords(metadata));
}

function shouldSuggestConnectorKeyword(value, metadata) {
    return likelyIncompleteToken(value, metadata.connectorKeywords);
}

function unknownConditionDiagnostic(token, lineNumber) {
    return {
        severity: "ERROR",
        line: lineNumber,
        column: token.column,
        endColumn: token.endColumn,
        message: `Unknown DSL condition '${token.value}'`
    };
}

function arrayValue(value) {
    return Array.isArray(value) ? value : [];
}

function objectValue(value) {
    return value && typeof value === "object" && !Array.isArray(value) ? value : {};
}

function emptyDslCompletionGroups() {
    return {
        keywords: [],
        widgetTypes: [],
        fieldNames: [],
        locales: [],
        snippets: false
    };
}

function dslLinePrefix(source, lineNumber, column) {
    const lines = String(source || "").split(/\r?\n/);
    const line = lines[Math.max(lineNumber - 1, 0)] || "";
    return line.slice(0, Math.max(column - 1, 0));
}

function conditionStartKeywords(metadata) {
    return [
        ...new Set(metadata.conditionOperators.map((operator) => operator.split(" ")[0]))
    ];
}

function isRootKeywordPrefix(value, rootKeywords) {
    return rootKeywords.some((keyword) => keyword.startsWith(value));
}

function likelyIncompleteToken(value, candidates) {
    if (!value || value.length < 1) {
        return false;
    }

    return [...new Set(candidates)]
        .filter((candidate) => candidate !== value)
        .some((candidate) => candidate.startsWith(value) && value.length >= Math.min(3, candidate.length));
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
                : "No extended oracles enabled.",
            classNames: extendedOracles,
            classNameColumns: balancedOracleNameColumns(extendedOracles)
        }
    ];
}
