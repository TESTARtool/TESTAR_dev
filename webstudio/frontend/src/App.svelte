<script>
    import { onDestroy, onMount } from "svelte";
    import CliModeView from "./CliModeView.svelte";
    import TestConfigurationView from "./TestConfigurationView.svelte";
    import RunTestarView from "./RunTestarView.svelte";
    import SpyModeView from "./SpyModeView.svelte";
    import TestResultsView from "./TestResultsView.svelte";
    import InspectLogsView from "./InspectLogsView.svelte";
    import StateModelIcon from "./icons/StateModelIcon.svelte";

    const STATE_MODEL_URL = "http://localhost:8090/models";

    let workspaces = [];
    let selectedWorkspaceName = "";
    let selectedWorkspaceSummary = null;
    let workspaceDocument = null;
    let selectedSourceName = "";
    let selectedSourceFile = null;
    let cliStatus = null;
    let cliAgentSettings = {
        apiKeyEnvVarName: "OPENAI_API",
        baseUrl: "",
        model: "gpt-5.4-mini",
        reasoningEffort: "medium",
        sandboxMode: "danger-full-access",
        approvalPolicy: "never",
        networkAccessEnabled: false,
        skipGitRepoCheck: true,
        promptTitle: "Test Parabank Login",
        promptText: "As a test agent verify that you can log in with the credentials john/demo. Then the Welcome John Smith message is shown."
    };
    let scriptlessStatus = null;
    let loading = false;
    let saving = false;
    let message = "";
    let messageTimeoutHandle = null;
    let scriptlessPollHandle = null;
    let currentPage = "configuration";
    let resultsData = null;
    let resultSource = "generate";
    let selectedResultGroup = null;
    let selectedResultFile = null;
    let selectedEditor = "java-composition";
    let selectedSettingsGroupId = "";
    let debugFiles = [];
    let selectedDebugFile = null;
    let spyState = null;
    let policySourceFiles = [];
    let compositionSourceFiles = [];
    let currentEditorDocument = null;
    let activePolicySourceFiles = [];
    let inactivePolicySourceFiles = [];
    let compositionFlowNodes = [];
    let selectedCompositionFlowNode = null;
    let stateModelDialog = {
        open: false,
        title: "",
        message: ""
    };

    function reportClientError(context, clientError) {
        console.error(`[WebStudio] ${context}`, clientError);
    }

    function showTemporaryMessage(text) {
        message = text;
        if (messageTimeoutHandle !== null) {
            window.clearTimeout(messageTimeoutHandle);
        }
        messageTimeoutHandle = window.setTimeout(() => {
            message = "";
            messageTimeoutHandle = null;
        }, 2000);
    }

    function openStateModelDialog(title, dialogMessage) {
        stateModelDialog = {
            open: true,
            title,
            message: dialogMessage
        };
    }

    function closeStateModelDialog() {
        stateModelDialog = {
            open: false,
            title: "",
            message: ""
        };
    }

    function closeStateModelDialogFromBackdrop(event) {
        if (event.currentTarget === event.target) {
            closeStateModelDialog();
        }
    }

    function stateModelDialogMessage(openError) {
        const errorMessage = openError?.message || "";

        if (errorMessage.includes("No generated state model was found yet")
            || errorMessage.includes("Cannot open the storage")
            || errorMessage.includes("because it does not exist")) {
            return {
                title: "State Model Not Available",
                message: "Dear user, before opening the analysis mode, TESTAR must execute a Generate run with the state model enabled. Currently there are no generated state models available."
            };
        }

        return {
            title: "Unable To Open State Model",
            message: "Dear user, before opening the analysis mode, TESTAR must execute a Generate run with the state model enabled. Currently there are no generated state models available."
        };
    }

    async function loadJson(path, options = {}) {
        const response = await fetch(path, options);
        if (!response.ok) {
            const responseText = await response.text();
            let responseMessage = responseText;

            try {
                const responseJson = JSON.parse(responseText);
                responseMessage = responseJson.message || responseJson.error || responseText;
            } catch (ignored) {
                // Keep the raw response text when it is not JSON.
            }

            throw new Error(`Request failed for ${path}: ${response.status}${responseMessage ? ` - ${responseMessage}` : ""}`);
        }

        return response.json();
    }

    async function refreshInitialData() {
        const [workspaceResponse, cliStatusResponse, cliAgentSettingsResponse, scriptlessResponse, spyResponse] = await Promise.all([
            loadJson("/api/workspaces"),
            loadJson("/api/execution/status/cli"),
            loadJson("/api/execution/cli/agent-settings"),
            loadJson("/api/execution/status/scriptless"),
            loadJson("/api/spy/status")
        ]);

        workspaces = workspaceResponse;
        cliStatus = cliStatusResponse;
        cliAgentSettings = cliAgentSettingsResponse;
        scriptlessStatus = scriptlessResponse;
        spyState = spyResponse;
    }

    async function loadWorkspace(workspaceName) {
        if (!workspaceName) {
            selectedWorkspaceSummary = null;
            workspaceDocument = null;
            selectedSourceName = "";
            selectedSourceFile = null;
            selectedEditor = "java-composition";
            selectedSettingsGroupId = "";
            selectedCompositionFlowNode = null;
            return;
        }

        loading = true;
        message = "";

        try {
            workspaceDocument = await loadJson(`/api/workspaces/${workspaceName}`);
            selectedWorkspaceName = workspaceName;
            selectedWorkspaceSummary = workspaces.find((workspace) => workspace.name === workspaceName) || null;
            selectedSourceName = "";
            selectedSourceFile = null;
            selectedEditor = "java-composition";
            selectedSettingsGroupId = workspaceDocument?.settingsGroups?.[0]?.id || "";
            selectedCompositionFlowNode = null;
        } catch (loadError) {
            reportClientError(`Unable to load workspace ${workspaceName}`, loadError);
        } finally {
            loading = false;
        }
    }

    async function selectSource(sourceName, editorId = null) {
        if (!selectedWorkspaceName || !sourceName) {
            selectedSourceName = "";
            selectedSourceFile = null;
            return;
        }

        selectedSourceName = sourceName;
        selectedSourceFile = await loadJson(`/api/workspaces/${selectedWorkspaceName}/sources/${sourceName}`);
        selectedEditor = editorId || `source:${sourceName}`;
    }

    function openEditor(editorId) {
        selectedSourceName = "";
        selectedSourceFile = null;
        selectedEditor = editorId;
        if (editorId !== "java-composition") {
            selectedCompositionFlowNode = null;
        }
    }

    function openTestSettings() {
        openEditor("test-settings");
    }

    function openVisualSettings() {
        openEditor("settings-form");
        if (!selectedSettingsGroupId && workspaceDocument?.settingsGroups?.length > 0) {
            selectedSettingsGroupId = workspaceDocument.settingsGroups[0].id;
        }
    }

    function openPoliciesProperties() {
        openEditor("policies-properties");
    }

    function openCompositionProperties() {
        openEditor("composition-properties");
    }

    function openJavaPolicies() {
        openEditor("java-policies");
    }

    function openJavaComposition() {
        openEditor("java-composition");
    }

    async function refreshWorkspaceDocument() {
        if (!selectedWorkspaceName) {
            return;
        }

        workspaceDocument = await loadJson(`/api/workspaces/${selectedWorkspaceName}`);
    }

    async function restoreEditorState(editorId, sourceName) {
        selectedEditor = editorId;
        if (selectedCompositionFlowNode) {
            selectedCompositionFlowNode = compositionFlowNodes.find((flowNode) => flowNode.id === selectedCompositionFlowNode.id) || selectedCompositionFlowNode;
        }
        if (workspaceDocument?.settingsGroups?.length > 0) {
            const matchingSettingsGroup = workspaceDocument.settingsGroups.find((settingsGroup) => settingsGroup.id === selectedSettingsGroupId);
            if (!matchingSettingsGroup) {
                selectedSettingsGroupId = workspaceDocument.settingsGroups[0].id;
            }
        } else {
            selectedSettingsGroupId = "";
        }
        if (sourceName) {
            await selectSource(sourceName, editorId);
        }
    }

    async function saveWorkspaceFile(endpoint, content) {
        saving = true;
        message = "";
        const activeEditorId = selectedEditor;
        const activeSourceName = selectedSourceName;

        try {
            await loadJson(endpoint, {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({ content })
            });

            await refreshWorkspaceDocument();
            await restoreEditorState(activeEditorId, activeSourceName);
            await refreshScriptlessStatus();
            showTemporaryMessage("Workspace file saved.");
        } catch (saveError) {
            reportClientError(`Unable to save ${endpoint}`, saveError);
        } finally {
            saving = false;
        }
    }

    async function saveSelectedSource() {
        if (!selectedWorkspaceName || !selectedSourceFile) {
            return;
        }

        await saveWorkspaceFile(
            `/api/workspaces/${selectedWorkspaceName}/sources/${selectedSourceFile.name}`,
            selectedSourceFile.content
        );
    }

    function escapeRegExp(text) {
        return text.replace(/[.*+?^${}()|[\]\\]/g, "\\$&");
    }

    function shouldPersistVisualSetting(setting) {
        const settingValue = (setting?.value ?? "").trim();
        const settingType = setting?.type || "string";

        if (settingValue !== "") {
            return true;
        }

        return settingType === "string" || settingType === "list";
    }

    function buildTestSettingsContent(currentContent, settingsGroups) {
        const settingsEntries = [];

        for (const settingsGroup of settingsGroups || []) {
            for (const setting of settingsGroup.settings || []) {
                settingsEntries.push({
                    key: setting.key,
                    value: setting.value ?? "",
                    persist: shouldPersistVisualSetting(setting)
                });
            }
        }

        let nextContent = currentContent || "";
        const missingEntries = [];

        for (const settingEntry of settingsEntries) {
            const key = settingEntry.key;
            const value = settingEntry.value;
            const escapedKey = escapeRegExp(key);
            const propertyPattern = new RegExp(`^\\s*${escapedKey}\\s*=.*(?:\\r?\\n|$)`, "gm");

            if (settingEntry.persist) {
                if (propertyPattern.test(nextContent)) {
                    nextContent = nextContent.replace(propertyPattern, `${key} = ${value}\n`);
                } else {
                    missingEntries.push(`${key} = ${value}`);
                }
            } else {
                nextContent = nextContent.replace(propertyPattern, "");
            }
        }

        if (missingEntries.length > 0) {
            if (nextContent.length === 0) {
                nextContent = `${missingEntries.join("\n")}\n`;
            } else {
                const separator = nextContent.endsWith("\n") ? "" : "\n";
                nextContent = `${nextContent}${separator}\n${missingEntries.join("\n")}\n`;
            }
        }

        return nextContent;
    }

    async function saveVisualSettings() {
        if (!workspaceDocument?.settingsGroups) {
            return;
        }

        const nextContent = buildTestSettingsContent(
            workspaceDocument.testSettings.content,
            workspaceDocument.settingsGroups
        );

        workspaceDocument.testSettings.content = nextContent;

        await saveWorkspaceFile(
            `/api/workspaces/${selectedWorkspaceName}/test-settings`,
            nextContent
        );
    }

    async function startGenerate() {
        if (!selectedWorkspaceName || !workspaceAvailableInTestar()) {
            return;
        }

        saving = true;
        message = "";
        currentPage = "run";

        try {
            scriptlessStatus = await loadJson(`/api/execution/scriptless/generate/${selectedWorkspaceName}`, {
                method: "POST"
            });
            if (scriptlessStatus.status === "error") {
                reportClientError("Generate mode returned an error status", scriptlessStatus.message);
            } else {
                showTemporaryMessage(scriptlessStatus.message || "Generate mode started.");
            }
        } catch (executionError) {
            reportClientError("Unable to start Generate mode", executionError);
        } finally {
            saving = false;
        }
    }

    async function stopGenerate() {
        saving = true;
        message = "";

        try {
            scriptlessStatus = await loadJson("/api/execution/scriptless/stop", {
                method: "POST"
            });
            showTemporaryMessage(scriptlessStatus.message || "Generate mode stopped.");
        } catch (executionError) {
            reportClientError("Unable to stop Generate mode", executionError);
        } finally {
            saving = false;
        }
    }

    async function refreshScriptlessStatus() {
        scriptlessStatus = await loadJson("/api/execution/status/scriptless");
    }

    async function refreshCliStatus() {
        cliStatus = await loadJson("/api/execution/status/cli");
    }

    async function refreshRemoteSpyStatus() {
        spyState = await loadJson("/api/spy/status");
    }

    function startScriptlessPolling() {
        stopScriptlessPolling();
        scriptlessPollHandle = window.setInterval(async () => {
            try {
                await refreshCliStatus();
                await refreshScriptlessStatus();
            } catch (pollError) {
                reportClientError("Unable to refresh execution status", pollError);
            }
        }, 1000);
    }

    function stopScriptlessPolling() {
        if (scriptlessPollHandle !== null) {
            window.clearInterval(scriptlessPollHandle);
            scriptlessPollHandle = null;
        }
    }

    function navigateToConfiguration() {
        currentPage = "configuration";
    }

    function navigateToRun() {
        currentPage = "run";
    }

    function navigateToCli() {
        currentPage = "cli";
    }

    async function navigateToSpy() {
        currentPage = "spy";
        await refreshRemoteSpyStatus();
    }

    function navigateToResults() {
        currentPage = "results";
        loadResults(resultSource);
    }

    async function navigateToLogs() {
        currentPage = "logs";
        await loadDebugFiles();
    }

    function openStateModelExternalTab(url = STATE_MODEL_URL) {
        window.open(url, "_blank", "noopener,noreferrer");
    }

    function workspaceAvailableInTestar(workspaceSummary = selectedWorkspaceSummary) {
        return workspaceSummary?.availableInTestar === true;
    }

    function workspaceAvailableInCli(workspaceSummary = selectedWorkspaceSummary) {
        return workspaceSummary?.availableInCli === true;
    }

    function workspaceSelectionInvalidForCurrentPage() {
        if (currentPage === "cli") {
            return !workspaceAvailableInCli();
        }

        if (currentPage === "run" || currentPage === "spy") {
            return !workspaceAvailableInTestar();
        }

        return false;
    }

    async function navigateToStateModel() {
        if (!selectedWorkspaceName || !workspaceAvailableInTestar()) {
            return;
        }

        saving = true;
        message = "";

        try {
            const response = await loadJson(`/api/statemodel/open/${selectedWorkspaceName}`, {
                method: "POST"
            });
            openStateModelExternalTab(response.url || STATE_MODEL_URL);
            showTemporaryMessage(response.message || "State model analysis opened.");
        } catch (openError) {
            reportClientError("Unable to open state model analysis", openError);
            const dialogContent = stateModelDialogMessage(openError);
            openStateModelDialog(dialogContent.title, dialogContent.message);
        } finally {
            saving = false;
        }
    }

    function isSelectedEditor(editorId) {
        return selectedEditor === editorId;
    }

    function sourceClassName(sourceFile) {
        if (!sourceFile?.name) {
            return "";
        }

        return sourceFile.name.endsWith(".java")
            ? sourceFile.name.slice(0, -".java".length)
            : sourceFile.name;
    }

    function referencedClasses(referenceGroup) {
        if (!workspaceDocument?.references?.[referenceGroup]) {
            return [];
        }

        return workspaceDocument.references[referenceGroup];
    }

    function sourceFilesForReferencedClasses(sourceFiles, referenceGroup) {
        const configuredClassNames = new Set(referencedClasses(referenceGroup));
        return sourceFiles.filter((sourceFile) => configuredClassNames.has(sourceClassName(sourceFile)));
    }

    function sourceFilesNotInReferenceSet(sourceFiles, referenceGroup) {
        const configuredClassNames = new Set(referencedClasses(referenceGroup));
        return sourceFiles.filter((sourceFile) => !configuredClassNames.has(sourceClassName(sourceFile)));
    }

    function parsePropertiesContent(content) {
        const properties = {};
        if (!content) {
            return properties;
        }

        for (const rawLine of content.split(/\r?\n/)) {
            const line = rawLine.trim();
            if (!line || line.startsWith("#") || !line.includes("=")) {
                continue;
            }

            const separatorIndex = line.indexOf("=");
            const key = line.slice(0, separatorIndex).trim();
            const value = line.slice(separatorIndex + 1).trim();
            properties[key] = value;
        }

        return properties;
    }

    function sourceFileByClassName(className) {
        if (!className) {
            return null;
        }

        return compositionSourceFiles.find((sourceFile) => sourceClassName(sourceFile) === className)
            || policySourceFiles.find((sourceFile) => sourceClassName(sourceFile) === className)
            || null;
    }

    function createCompositionFlowNode(nodeDefinition, properties) {
        const configuredClassName = nodeDefinition.propertyKey ? (properties[nodeDefinition.propertyKey] || "") : "";
        const sourceFile = sourceFileByClassName(configuredClassName);
        const isCustom = configuredClassName !== "";
        const color = nodeDefinition.kind === "oracle"
            ? "oracle"
            : (isCustom ? (sourceFile ? "custom" : "invalid") : "default");

        return {
            id: nodeDefinition.id,
            title: nodeDefinition.title,
            propertyKey: nodeDefinition.propertyKey,
            kind: nodeDefinition.kind,
            configuredClassName,
            sourceFile,
            color,
            description: isCustom
                ? configuredClassName
                : `Default ${nodeDefinition.kind} implementation`
        };
    }

    function selectCompositionFlowNode(flowNode) {
        if (!flowNode) {
            selectedCompositionFlowNode = null;
            selectedSourceName = "";
            selectedSourceFile = null;
            selectedEditor = "java-composition";
            return;
        }

        selectedCompositionFlowNode = flowNode;
        if (flowNode.sourceFile?.name) {
            selectSource(flowNode.sourceFile.name, "java-composition");
            return;
        }

        selectedSourceName = "";
        selectedSourceFile = null;
        selectedEditor = "java-composition";
    }

    function isPolicySourceSelected(sourceFile) {
        return selectedEditor === "java-policies" && selectedSourceName === sourceFile.name;
    }

    async function loadResults(source = resultSource) {
        try {
            resultSource = source;
            resultsData = await loadJson(
                source === "cli"
                    ? "/api/execution/cli/results"
                    : "/api/execution/scriptless/results"
            );
            const resultGroups = resultsData.groups || [];
            if (resultGroups.length > 0) {
                await selectResultGroup(resultGroups[resultGroups.length - 1]);
            } else {
                selectedResultGroup = null;
                selectedResultFile = null;
            }
        } catch (resultsError) {
            reportClientError(`Unable to load ${source} results`, resultsError);
        }
    }

    async function loadDebugFiles() {
        try {
            debugFiles = await loadJson("/api/debug-files");
            if (debugFiles.length > 0) {
                await loadDebugFile(debugFiles[0]);
            } else {
                selectedDebugFile = null;
            }
        } catch (debugFilesError) {
            reportClientError("Unable to load debug files", debugFilesError);
            debugFiles = [];
            selectedDebugFile = null;
        }
    }

    async function loadDebugFile(debugFile) {
        if (!debugFile?.name) {
            selectedDebugFile = null;
            return;
        }

        try {
            const debugFilePath = encodeURIComponent(debugFile.path);
            selectedDebugFile = await loadJson(`/api/debug-files/${debugFile.name}?path=${debugFilePath}`);
        } catch (debugFileError) {
            selectedDebugFile = {
                name: debugFile.name,
                path: debugFile.path,
                content: debugFileError?.message || "Unable to load the selected debug file."
            };
            reportClientError(`Unable to load debug file ${debugFile.name}`, debugFileError);
        }
    }

    $: if (workspaceDocument?.sourceFiles) {
        policySourceFiles = workspaceDocument.sourceFiles.filter((sourceFile) => sourceFile.category === "policy");
        compositionSourceFiles = workspaceDocument.sourceFiles.filter((sourceFile) =>
            sourceFile.category === "service" || sourceFile.category === "capability"
        );
        activePolicySourceFiles = sourceFilesForReferencedClasses(policySourceFiles, "policies");
        inactivePolicySourceFiles = sourceFilesNotInReferenceSet(policySourceFiles, "policies");
        const compositionProperties = parsePropertiesContent(workspaceDocument.compositionProperties.content);
        compositionFlowNodes = [
            createCompositionFlowNode({ id: "settings", title: "SettingsCapability", propertyKey: "settingsCapabilityClass", kind: "capability" }, compositionProperties),
            createCompositionFlowNode({ id: "test-session", title: "TestSessionCapability", propertyKey: "testSessionCapabilityClass", kind: "capability" }, compositionProperties),
            createCompositionFlowNode({ id: "test-sequence", title: "TestSequenceCapability", propertyKey: "testSequenceCapabilityClass", kind: "capability" }, compositionProperties),
            createCompositionFlowNode({ id: "system", title: "SystemService", propertyKey: "systemServiceClass", kind: "service" }, compositionProperties),
            createCompositionFlowNode({ id: "stop-criteria", title: "StopCriteriaCapability", propertyKey: "stopCriteriaCapabilityClass", kind: "capability" }, compositionProperties),
            createCompositionFlowNode({ id: "state", title: "StateService", propertyKey: "stateServiceClass", kind: "service" }, compositionProperties),
            createCompositionFlowNode({ id: "state-identifier", title: "StateIdentifierService", propertyKey: "stateIdentifierServiceClass", kind: "service" }, compositionProperties),
            createCompositionFlowNode({ id: "oracle-evaluation", title: "OracleEvaluationService", propertyKey: "", kind: "service" }, compositionProperties),
            createCompositionFlowNode({ id: "oracle-services", title: "Custom Oracle Services", propertyKey: "oracleComposerClass", kind: "oracle" }, compositionProperties),
            createCompositionFlowNode({ id: "action-derivation", title: "ActionDerivationService", propertyKey: "actionDerivationServiceClass", kind: "service" }, compositionProperties),
            createCompositionFlowNode({ id: "action-identifier", title: "ActionIdentifierService", propertyKey: "actionIdentifierServiceClass", kind: "service" }, compositionProperties),
            createCompositionFlowNode({ id: "action-selector", title: "ActionSelectorService", propertyKey: "actionSelectorServiceClass", kind: "service" }, compositionProperties),
            createCompositionFlowNode({ id: "action-execution", title: "ActionExecutionService", propertyKey: "actionExecutionServiceClass", kind: "service" }, compositionProperties)
        ];
        if (selectedCompositionFlowNode) {
            selectedCompositionFlowNode = compositionFlowNodes.find((flowNode) => flowNode.id === selectedCompositionFlowNode.id) || selectedCompositionFlowNode;
        }
    } else {
        policySourceFiles = [];
        compositionSourceFiles = [];
        activePolicySourceFiles = [];
        inactivePolicySourceFiles = [];
        compositionFlowNodes = [];
    }

    $: if (!workspaceDocument) {
        currentEditorDocument = null;
    } else if (selectedEditor === "test-settings") {
        currentEditorDocument = {
            title: "Edit test.settings",
            saveLabel: "Save test.settings",
            save: () => saveWorkspaceFile(
                `/api/workspaces/${selectedWorkspaceName}/test-settings`,
                workspaceDocument.testSettings.content
            )
        };
    } else if (selectedEditor === "settings-form") {
        currentEditorDocument = {
            title: "Edit Settings",
            saveLabel: "Save settings",
            save: saveVisualSettings
        };
    } else if (selectedEditor === "policies-properties") {
        currentEditorDocument = {
            title: "Edit policies.properties",
            saveLabel: "Save policies.properties",
            save: () => saveWorkspaceFile(
                `/api/workspaces/${selectedWorkspaceName}/policies-properties`,
                workspaceDocument.policiesProperties.content
            )
        };
    } else if (selectedEditor === "composition-properties") {
        currentEditorDocument = {
            title: "Edit composition.properties",
            saveLabel: "Save composition.properties",
            save: () => saveWorkspaceFile(
                `/api/workspaces/${selectedWorkspaceName}/composition-properties`,
                workspaceDocument.compositionProperties.content
            )
        };
    } else if (selectedEditor !== "java-policies" && selectedEditor !== "java-composition" && selectedSourceFile) {
        currentEditorDocument = {
            title: selectedSourceFile.name,
            saveLabel: "Save source",
            save: saveSelectedSource
        };
    } else {
        currentEditorDocument = null;
    }

    function selectSettingsGroup(groupId) {
        selectedSettingsGroupId = groupId || "";
    }

    $: if (selectedEditor === "settings-form" && !selectedSettingsGroupId && workspaceDocument?.settingsGroups?.length > 0) {
        selectedSettingsGroupId = workspaceDocument.settingsGroups[0].id;
    }

    async function selectResultGroup(resultGroup) {
        selectedResultGroup = resultGroup;
        if (resultGroup?.files?.length > 0) {
            await loadResultFile(resultGroup.files[0]);
        } else {
            selectedResultFile = null;
        }
    }

    async function loadResultFile(resultFile) {
        if (!resultFile?.name) {
            selectedResultFile = null;
            return;
        }

        try {
            const resultPath = encodeURIComponent(resultFile.path);
            selectedResultFile = await loadJson(
                resultSource === "cli"
                    ? `/api/execution/cli/results/${resultFile.name}?path=${resultPath}`
                    : `/api/execution/scriptless/results/${resultFile.name}?path=${resultPath}`
            );
        } catch (fileError) {
            reportClientError(`Unable to load result file ${resultFile.name}`, fileError);
        }
    }

    async function selectResultSource(source) {
        if (resultSource === source && currentPage === "results") {
            return;
        }

        await loadResults(source);
    }

    async function startRemoteSpyMode() {
        if (!selectedWorkspaceName || !workspaceAvailableInTestar()) {
            return;
        }

        saving = true;
        message = "";
        currentPage = "spy";

        try {
            spyState = await loadJson(`/api/spy/start/${selectedWorkspaceName}`, {
                method: "POST"
            });
            showTemporaryMessage(spyState.message || "Remote Spy Mode started.");
        } catch (spyError) {
            reportClientError("Unable to start remote Spy Mode", spyError);
        } finally {
            saving = false;
        }
    }

    async function refreshRemoteSpyMode() {
        saving = true;
        message = "";

        try {
            spyState = await loadJson("/api/spy/refresh", {
                method: "POST"
            });
        } catch (spyError) {
            reportClientError("Unable to refresh remote Spy Mode", spyError);
        } finally {
            saving = false;
        }
    }

    async function stopRemoteSpyMode() {
        saving = true;
        message = "";

        try {
            spyState = await loadJson("/api/spy/stop", {
                method: "POST"
            });
            showTemporaryMessage(spyState.message || "Remote Spy Mode stopped.");
        } catch (spyError) {
            reportClientError("Unable to stop remote Spy Mode", spyError);
        } finally {
            saving = false;
        }
    }

    async function executeSpyAction(actionId) {
        if (!actionId) {
            return;
        }

        saving = true;
        message = "";

        try {
            spyState = await loadJson(`/api/spy/actions/${encodeURIComponent(actionId)}`, {
                method: "POST"
            });
        } catch (spyError) {
            reportClientError(`Unable to execute Spy Mode action ${actionId}`, spyError);
        } finally {
            saving = false;
        }
    }

    async function executeSpyWidgetDefaultAction(widgetId) {
        if (!widgetId) {
            return;
        }

        saving = true;
        message = "";

        try {
            spyState = await loadJson(`/api/spy/widgets/${encodeURIComponent(widgetId)}/default-action`, {
                method: "POST"
            });
        } catch (spyError) {
            reportClientError(`Unable to execute default Spy Mode action for ${widgetId}`, spyError);
        } finally {
            saving = false;
        }
    }

    async function startLocalSpyMode() {
        if (!selectedWorkspaceName || !workspaceAvailableInTestar()) {
            return;
        }

        saving = true;
        message = "";
        currentPage = "spy";

        try {
            scriptlessStatus = await loadJson(`/api/execution/scriptless/local-spy/${selectedWorkspaceName}`, {
                method: "POST"
            });
            showTemporaryMessage(scriptlessStatus.message || "Local Spy Mode started.");
        } catch (spyError) {
            reportClientError("Unable to start local Spy Mode", spyError);
        } finally {
            saving = false;
        }
    }

    async function stopLocalSpyMode() {
        saving = true;
        message = "";

        try {
            scriptlessStatus = await loadJson("/api/execution/scriptless/stop", {
                method: "POST"
            });
            showTemporaryMessage(scriptlessStatus.message || "Local Spy Mode stopped.");
        } catch (spyError) {
            reportClientError("Unable to stop local Spy Mode", spyError);
        } finally {
            saving = false;
        }
    }

    async function executeSpyWidgetDirectType(widgetId, text) {
        if (!widgetId) {
            return;
        }

        saving = true;
        message = "";

        try {
            spyState = await loadJson(`/api/spy/widgets/${encodeURIComponent(widgetId)}/direct-type`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({ text })
            });
        } catch (spyError) {
            reportClientError(`Unable to type into Spy Mode widget ${widgetId}`, spyError);
        } finally {
            saving = false;
        }
    }

    async function startCliManualSession(platform, target) {
        if (!selectedWorkspaceName || !workspaceAvailableInCli()) {
            return;
        }

        saving = true;
        message = "";
        currentPage = "cli";

        try {
            cliStatus = await loadJson(`/api/execution/cli/manual/start/${selectedWorkspaceName}`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({ platform, target })
            });
            showTemporaryMessage(cliStatus.message || "Manual CLI session started.");
        } catch (cliError) {
            reportClientError("Unable to start manual CLI session", cliError);
        } finally {
            saving = false;
        }
    }

    async function startCliAgentSession(platform, target) {
        if (!selectedWorkspaceName || !workspaceAvailableInCli()) {
            return;
        }

        saving = true;
        message = "";
        currentPage = "cli";

        try {
            cliStatus = await loadJson(`/api/execution/cli/agent/start/${selectedWorkspaceName}`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({ platform, target })
            });
            showTemporaryMessage(cliStatus.message || "Agent CLI execution started.");
        } catch (cliError) {
            reportClientError("Unable to start agent CLI execution", cliError);
        } finally {
            saving = false;
        }
    }

    async function runCliManualCommand(commandLine) {
        saving = true;
        message = "";

        try {
            cliStatus = await loadJson("/api/execution/cli/manual/command", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({ commandLine })
            });
        } catch (cliError) {
            reportClientError(`Unable to execute CLI command ${commandLine}`, cliError);
        } finally {
            saving = false;
        }
    }

    async function stopCliManualSession() {
        saving = true;
        message = "";

        try {
            cliStatus = await loadJson("/api/execution/cli/manual/stop", {
                method: "POST"
            });
            showTemporaryMessage(cliStatus.message || "Manual CLI session stopped.");
        } catch (cliError) {
            reportClientError("Unable to stop manual CLI session", cliError);
        } finally {
            saving = false;
        }
    }

    async function stopCliAgentSession() {
        saving = true;
        message = "";

        try {
            cliStatus = await loadJson("/api/execution/cli/agent/stop", {
                method: "POST"
            });
            showTemporaryMessage(cliStatus.message || "Agent CLI execution stopped.");
        } catch (cliError) {
            reportClientError("Unable to stop agent CLI execution", cliError);
        } finally {
            saving = false;
        }
    }

    async function saveCliAgentSettings() {
        saving = true;
        message = "";

        try {
            cliAgentSettings = await loadJson("/api/execution/cli/agent-settings", {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(cliAgentSettings || {})
            });
            showTemporaryMessage("Agent CLI settings saved.");
        } catch (cliError) {
            reportClientError("Unable to save Agent CLI settings", cliError);
        } finally {
            saving = false;
        }
    }

    $: selectedWorkspaceSummary = workspaces.find((workspace) => workspace.name === selectedWorkspaceName) || null;

    onMount(async () => {
        startScriptlessPolling();
        try {
            await refreshInitialData();
            if (workspaces.length > 0) {
                await loadWorkspace(workspaces[0].name);
            }
        } catch (loadError) {
            reportClientError("Unable to initialize Web Studio", loadError);
        }
    });

    onDestroy(() => {
        stopScriptlessPolling();
    });
</script>

<svelte:head>
    <title>TESTAR Web Studio</title>
</svelte:head>

<div class="page">
    <nav class="panel panel-wide page-nav">
        <div class="page-nav-workspace">
            <select
                id="page-workspace-select"
                value={selectedWorkspaceName}
                class:workspace-select-invalid={workspaceSelectionInvalidForCurrentPage()}
                on:change={(event) => loadWorkspace(event.currentTarget.value)}
            >
                {#each workspaces as workspace}
                    <option value={workspace.name}>{workspace.name}</option>
                {/each}
            </select>
        </div>
        <button class:secondary={currentPage !== "configuration"} on:click={navigateToConfiguration}>
            ⚙️ Test Configuration
        </button>
        <button class:secondary={currentPage !== "spy"} on:click={navigateToSpy}>
            🔍 Spy Mode
        </button>
        <button class:secondary={currentPage !== "run"} on:click={navigateToRun}>
            🔄 Generate Mode
        </button>
        <button class:secondary={currentPage !== "cli"} on:click={navigateToCli}>
            >_ CLI Mode
        </button>
        <div class="page-nav-right-group">
        <button class:secondary={currentPage !== "results"} type="button" on:click={navigateToResults}>
            👁️ View Test Results
        </button>
        <button class="secondary" type="button" on:click={navigateToStateModel}>
            <StateModelIcon size={18} title="State model" />
            <span>View State Model</span>
        </button>
        <button class:secondary={currentPage !== "logs"} type="button" on:click={navigateToLogs}>
            🧾 Inspect Debug Files
        </button>
        </div>
    </nav>

    {#if currentPage === "configuration"}
        <TestConfigurationView
            activePolicySourceFiles={activePolicySourceFiles}
            compositionFlowNodes={compositionFlowNodes}
            currentEditorDocument={currentEditorDocument}
            inactivePolicySourceFiles={inactivePolicySourceFiles}
            isPolicySourceSelected={isPolicySourceSelected}
            isSelectedEditor={isSelectedEditor}
            loading={loading}
            openCompositionProperties={openCompositionProperties}
            openJavaComposition={openJavaComposition}
            openJavaPolicies={openJavaPolicies}
            openPoliciesProperties={openPoliciesProperties}
            openTestSettings={openTestSettings}
            openVisualSettings={openVisualSettings}
            policySourceFiles={policySourceFiles}
            saveSelectedSource={saveSelectedSource}
            saving={saving}
            selectSource={selectSource}
            selectCompositionFlowNode={selectCompositionFlowNode}
            selectSettingsGroup={selectSettingsGroup}
            selectedEditor={selectedEditor}
            selectedCompositionFlowNode={selectedCompositionFlowNode}
            selectedSettingsGroupId={selectedSettingsGroupId}
            selectedSourceFile={selectedSourceFile}
            workspaceDocument={workspaceDocument}
        />
    {/if}

    {#if currentPage === "run"}
        <RunTestarView
            saving={saving}
            scriptlessStatus={scriptlessStatus}
            selectedWorkspaceName={selectedWorkspaceName}
            selectedWorkspaceAvailableInTestar={workspaceAvailableInTestar()}
            startGenerate={startGenerate}
            stopGenerate={stopGenerate}
        />
    {/if}

    {#if currentPage === "spy"}
        <SpyModeView
            scriptlessStatus={scriptlessStatus}
            saving={saving}
            selectedWorkspaceName={selectedWorkspaceName}
            selectedWorkspaceAvailableInTestar={workspaceAvailableInTestar()}
            spyState={spyState}
            startRemoteSpyMode={startRemoteSpyMode}
            refreshRemoteSpyMode={refreshRemoteSpyMode}
            stopRemoteSpyMode={stopRemoteSpyMode}
            startLocalSpyMode={startLocalSpyMode}
            stopLocalSpyMode={stopLocalSpyMode}
            executeSpyAction={executeSpyAction}
            executeSpyWidgetDefaultAction={executeSpyWidgetDefaultAction}
            executeSpyWidgetDirectType={executeSpyWidgetDirectType}
        />
    {/if}

    {#if currentPage === "cli"}
        <CliModeView
            cliAgentSettings={cliAgentSettings}
            cliStatus={cliStatus}
            saving={saving}
            saveCliAgentSettings={saveCliAgentSettings}
            selectedWorkspaceAvailableInCli={workspaceAvailableInCli()}
            startCliAgentSession={startCliAgentSession}
            startCliManualSession={startCliManualSession}
            runCliManualCommand={runCliManualCommand}
            stopCliAgentSession={stopCliAgentSession}
            stopCliManualSession={stopCliManualSession}
        />
    {/if}

    {#if currentPage === "results"}
        <TestResultsView
            loadResultFile={loadResultFile}
            resultsData={resultsData}
            resultSource={resultSource}
            selectedResultFile={selectedResultFile}
            selectedResultGroup={selectedResultGroup}
            selectResultSource={selectResultSource}
            selectResultGroup={selectResultGroup}
        />
    {/if}

    {#if currentPage === "logs"}
        <InspectLogsView
            debugFiles={debugFiles}
            loadDebugFile={loadDebugFile}
            selectedDebugFile={selectedDebugFile}
        />
    {/if}

    {#if message}
        <div class="toast-message">
            {message}
        </div>
    {/if}

    {#if stateModelDialog.open}
        <div class="composition-modal-backdrop" role="presentation" on:click={closeStateModelDialogFromBackdrop}>
            <div
                class="composition-modal state-model-dialog"
                role="dialog"
                aria-modal="true"
                aria-labelledby="state-model-dialog-title"
            >
                <div class="composition-modal-header">
                    <div>
                        <h2 id="state-model-dialog-title">{stateModelDialog.title}</h2>
                        <p>{stateModelDialog.message}</p>
                    </div>
                </div>
                <div class="composition-modal-actions">
                    <button type="button" class="secondary" on:click={closeStateModelDialog}>
                        Close
                    </button>
                </div>
            </div>
        </div>
    {/if}
</div>
