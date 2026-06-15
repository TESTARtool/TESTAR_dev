<script>
    import { onDestroy, onMount } from "svelte";
    import TestConfigurationView from "./TestConfigurationView.svelte";
    import RunTestarView from "./RunTestarView.svelte";
    import TestResultsView from "./TestResultsView.svelte";

    let workspaces = [];
    let selectedWorkspaceName = "";
    let workspaceDocument = null;
    let selectedSourceName = "";
    let selectedSourceFile = null;
    let scriptlessStatus = null;
    let loading = false;
    let saving = false;
    let message = "";
    let messageTimeoutHandle = null;
    let scriptlessPollHandle = null;
    let currentPage = "configuration";
    let scriptlessResults = null;
    let selectedResultGroup = null;
    let selectedResultFile = null;
    let selectedEditor = "java-composition";
    let policySourceFiles = [];
    let compositionSourceFiles = [];
    let currentEditorDocument = null;
    let activePolicySourceFiles = [];
    let inactivePolicySourceFiles = [];
    let compositionFlowNodes = [];
    let selectedCompositionFlowNode = null;

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
        const [workspaceResponse, scriptlessResponse] = await Promise.all([
            loadJson("/api/workspaces"),
            loadJson("/api/execution/status/scriptless")
        ]);

        workspaces = workspaceResponse;
        scriptlessStatus = scriptlessResponse;
    }

    async function loadWorkspace(workspaceName) {
        if (!workspaceName) {
            workspaceDocument = null;
            selectedSourceName = "";
            selectedSourceFile = null;
            selectedEditor = "java-composition";
            selectedCompositionFlowNode = null;
            return;
        }

        loading = true;
        message = "";

        try {
            workspaceDocument = await loadJson(`/api/workspaces/${workspaceName}`);
            selectedWorkspaceName = workspaceName;
            selectedSourceName = "";
            selectedSourceFile = null;
            selectedEditor = "java-composition";
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

    function buildTestSettingsContent(currentContent, settingsGroups) {
        const settingsEntries = [];

        for (const settingsGroup of settingsGroups || []) {
            for (const setting of settingsGroup.settings || []) {
                settingsEntries.push([setting.key, setting.value ?? ""]);
            }
        }

        let nextContent = currentContent || "";
        const missingEntries = [];

        for (const [key, value] of settingsEntries) {
            const escapedKey = escapeRegExp(key);
            const propertyPattern = new RegExp(`^(\\s*${escapedKey}\\s*=\\s*).*$`, "m");

            if (propertyPattern.test(nextContent)) {
                nextContent = nextContent.replace(propertyPattern, `$1${value}`);
            } else {
                missingEntries.push(`${key} = ${value}`);
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
        if (!selectedWorkspaceName) {
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

    function startScriptlessPolling() {
        stopScriptlessPolling();
        scriptlessPollHandle = window.setInterval(async () => {
            try {
                await refreshScriptlessStatus();
            } catch (pollError) {
                reportClientError("Unable to refresh scriptless status", pollError);
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

    function navigateToResults() {
        currentPage = "results";
        loadResults();
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
            : (isCustom ? "custom" : "default");

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
        if (flowNode.sourceFile) {
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

    async function loadResults() {
        try {
            scriptlessResults = await loadJson("/api/execution/scriptless/results");
            const resultGroups = scriptlessResults.groups || [];
            if (resultGroups.length > 0) {
                await selectResultGroup(resultGroups[resultGroups.length - 1]);
            } else {
                selectedResultGroup = null;
                selectedResultFile = null;
            }
        } catch (resultsError) {
            reportClientError("Unable to load scriptless results", resultsError);
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
                `/api/execution/scriptless/results/${resultFile.name}?path=${resultPath}`
            );
        } catch (fileError) {
            reportClientError(`Unable to load result file ${resultFile.name}`, fileError);
        }
    }

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
        <button class:secondary={currentPage !== "configuration"} on:click={navigateToConfiguration}>
            ⚙️ Test Configuration
        </button>
        <button class:secondary={currentPage !== "run"} on:click={navigateToRun}>
            🔄 Run TESTAR tool
        </button>
        <button class:secondary={currentPage !== "results"} on:click={navigateToResults}>
            👁️ View Test Results
        </button>
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
            selectedEditor={selectedEditor}
            selectedCompositionFlowNode={selectedCompositionFlowNode}
            selectedSourceFile={selectedSourceFile}
            selectedWorkspaceName={selectedWorkspaceName}
            workspaceDocument={workspaceDocument}
            workspaces={workspaces}
            loadWorkspace={loadWorkspace}
        />
    {/if}

    {#if currentPage === "run"}
        <RunTestarView
            saving={saving}
            scriptlessStatus={scriptlessStatus}
            selectedWorkspaceName={selectedWorkspaceName}
            startGenerate={startGenerate}
            stopGenerate={stopGenerate}
        />
    {/if}

    {#if currentPage === "results"}
        <TestResultsView
            loadResultFile={loadResultFile}
            scriptlessResults={scriptlessResults}
            selectedResultFile={selectedResultFile}
            selectedResultGroup={selectedResultGroup}
            selectResultGroup={selectResultGroup}
        />
    {/if}

    {#if message}
        <div class="toast-message">
            {message}
        </div>
    {/if}
</div>
