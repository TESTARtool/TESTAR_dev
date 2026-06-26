<script>
    import { onDestroy, onMount } from "svelte";
    import CliModeView from "./CliModeView.svelte";
    import TestConfigurationView from "./TestConfigurationView.svelte";
    import RunTestarView from "./RunTestarView.svelte";
    import SpyModeView from "./SpyModeView.svelte";
    import TestResultsView from "./TestResultsView.svelte";
    import InspectLogsView from "./InspectLogsView.svelte";
    import StateModelIcon from "./icons/StateModelIcon.svelte";
    import { shouldGuardConfigurationTransition } from "./configurationGuard.js";
    import { clearSelectedSourceState } from "./policyEditorState.js";
    import { stateModelWorkspaceDialog } from "./stateModelNavigation.js";

    const STATE_MODEL_URL = "http://localhost:8090/models";

    let workspaces = [];
    let selectedWorkspaceName = "";
    let selectedWorkspaceSummary = null;
    let selectedWorkspaceAvailableInTestar = false;
    let workspaceDocument = null;
    let selectedWorkspaceSutConnector = "";
    let selectedWorkspaceSutConnectorValue = "";
    let selectedWorkspaceObservationMode = "";
    let selectedSourceName = "";
    let selectedSourceFile = null;
    let cliStatus = null;
    let cliAgentSettings = {
        apiKeyEnvVarName: "OPENAI_API_KEY",
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
    let regexValidationResults = {};
    let javaCompileResult = null;
    let stateModelDialog = {
        open: false,
        title: "",
        message: ""
    };
    let visualSettingsDirty = false;
    let unsavedSettingsDialog = {
        open: false,
        title: "",
        message: "",
        saveLabel: "Save"
    };
    let savedTestSettingsContent = "";
    let savedCompositionPropertiesContent = "";
    let savedPoliciesPropertiesContent = "";
    let savedSourceContents = {};
    let pendingConfigurationAction = null;

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

    function openUnsavedSettingsDialog(title, dialogMessage, saveLabel = "Save") {
        unsavedSettingsDialog = {
            open: true,
            title,
            message: dialogMessage,
            saveLabel
        };
    }

    function closeUnsavedSettingsDialog() {
        unsavedSettingsDialog = {
            open: false,
            title: "",
            message: "",
            saveLabel: "Save"
        };
        pendingConfigurationAction = null;
    }

    function closeUnsavedSettingsDialogFromBackdrop(event) {
        if (event.currentTarget === event.target) {
            closeUnsavedSettingsDialog();
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
            selectedWorkspaceName = "";
            workspaceDocument = null;
            selectedWorkspaceSutConnector = "";
            selectedWorkspaceSutConnectorValue = "";
            selectedWorkspaceObservationMode = "";
            savedTestSettingsContent = "";
            visualSettingsDirty = false;
            selectedSourceName = "";
            selectedSourceFile = null;
            selectedEditor = "java-composition";
            selectedSettingsGroupId = "";
            selectedCompositionFlowNode = null;
            regexValidationResults = {};
            javaCompileResult = null;
            return;
        }

        loading = true;
        message = "";
        selectedWorkspaceName = workspaceName;
        selectedWorkspaceSummary = workspaces.find((workspace) => workspace.name === workspaceName) || null;

        try {
            workspaceDocument = await loadJson(`/api/workspaces/${workspaceName}`);
            savedTestSettingsContent = workspaceDocument?.testSettings?.content || "";
            savedCompositionPropertiesContent = workspaceDocument?.compositionProperties?.content || "";
            savedPoliciesPropertiesContent = workspaceDocument?.policiesProperties?.content || "";
            savedSourceContents = {};
            visualSettingsDirty = false;
            selectedWorkspaceSutConnector = normalizeSettingDisplayValue(
                parsePropertiesContent(workspaceDocument?.testSettings?.content || "").SUTConnector
                    || workspaceSettingValue("SUTConnector")
            );
            selectedWorkspaceSutConnectorValue = normalizeSettingDisplayValue(
                parsePropertiesContent(workspaceDocument?.testSettings?.content || "").SUTConnectorValue
                    || workspaceSettingValue("SUTConnectorValue")
            );
            selectedWorkspaceObservationMode = normalizeSettingDisplayValue(
                parsePropertiesContent(workspaceDocument?.testSettings?.content || "").StateObservationMode
                    || workspaceSettingValue("StateObservationMode")
            );
            selectedSourceName = "";
            selectedSourceFile = null;
            selectedEditor = "java-composition";
            selectedSettingsGroupId = workspaceDocument?.settingsGroups?.[0]?.id || "";
            selectedCompositionFlowNode = null;
            regexValidationResults = {};
            javaCompileResult = null;
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
        savedSourceContents = {
            ...savedSourceContents,
            [sourceName]: selectedSourceFile?.content || ""
        };
        selectedEditor = editorId || `source:${sourceName}`;
        javaCompileResult = null;
    }

    function clearSelectedSource() {
        const nextState = clearSelectedSourceState({
            selectedSourceName,
            selectedSourceFile,
            javaCompileResult
        });

        selectedSourceName = nextState.selectedSourceName;
        selectedSourceFile = nextState.selectedSourceFile;
        javaCompileResult = nextState.javaCompileResult;
    }

    async function closeCompositionSourceEditor() {
        await guardConfigurationTransition(async () => {
            selectedCompositionFlowNode = null;
            clearSelectedSource();
        }, "__close-composition-source__");
    }

    async function closePolicySourceEditor() {
        await guardConfigurationTransition(async () => {
            clearSelectedSource();
            await openJavaPolicies();
        }, "__close-policy-source__");
    }

    function openEditorImmediate(editorId) {
        selectedSourceName = "";
        selectedSourceFile = null;
        selectedEditor = editorId;
        javaCompileResult = null;
        if (editorId !== "java-composition") {
            selectedCompositionFlowNode = null;
        }
    }

    async function openEditor(editorId) {
        if (selectedEditor === editorId) {
            return;
        }

        await guardConfigurationTransition(async () => {
            openEditorImmediate(editorId);
        }, editorId);
    }

    async function openTestSettings() {
        await openEditor("test-settings");
    }

    async function openVisualSettings() {
        await openEditor("settings-form");
        if (!selectedSettingsGroupId && workspaceDocument?.settingsGroups?.length > 0) {
            selectedSettingsGroupId = workspaceDocument.settingsGroups[0].id;
        }
    }

    async function openPoliciesProperties() {
        await openEditor("policies-properties");
    }

    async function openCompositionProperties() {
        await openEditor("composition-properties");
    }

    async function openJavaPolicies() {
        await openEditor("java-policies");
    }

    async function openJavaComposition() {
        await openEditor("java-composition");
    }

    async function refreshWorkspaceDocument() {
        if (!selectedWorkspaceName) {
            return;
        }

        workspaceDocument = await loadJson(`/api/workspaces/${selectedWorkspaceName}`);
        savedTestSettingsContent = workspaceDocument?.testSettings?.content || "";
        savedCompositionPropertiesContent = workspaceDocument?.compositionProperties?.content || "";
        savedPoliciesPropertiesContent = workspaceDocument?.policiesProperties?.content || "";
        visualSettingsDirty = false;
        selectedWorkspaceSutConnector = normalizeSettingDisplayValue(
            parsePropertiesContent(workspaceDocument?.testSettings?.content || "").SUTConnector
                || workspaceSettingValue("SUTConnector")
        );
        selectedWorkspaceSutConnectorValue = normalizeSettingDisplayValue(
            parsePropertiesContent(workspaceDocument?.testSettings?.content || "").SUTConnectorValue
                || workspaceSettingValue("SUTConnectorValue")
        );
        selectedWorkspaceObservationMode = normalizeSettingDisplayValue(
            parsePropertiesContent(workspaceDocument?.testSettings?.content || "").StateObservationMode
                || workspaceSettingValue("StateObservationMode")
        );
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
            savedTestSettingsContent = workspaceDocument?.testSettings?.content || "";
            visualSettingsDirty = false;
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

    async function persistSelectedSourceForCompile() {
        if (!selectedWorkspaceName || !selectedSourceFile?.name) {
            return;
        }

        const activeEditorId = selectedEditor;
        const activeSourceName = selectedSourceName;

        await loadJson(
            `/api/workspaces/${selectedWorkspaceName}/sources/${selectedSourceFile.name}`,
            {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    content: selectedSourceFile.content
                })
            }
        );

        await refreshWorkspaceDocument();
        await restoreEditorState(activeEditorId, activeSourceName);
    }

    async function compileSelectedJavaSource() {
        if (!selectedWorkspaceName || !selectedSourceFile?.name) {
            return null;
        }

        saving = true;
        message = "";

        try {
            await persistSelectedSourceForCompile();
            javaCompileResult = await loadJson(
                `/api/workspaces/${selectedWorkspaceName}/sources/${encodeURIComponent(selectedSourceFile.name)}/compile`,
                {
                    method: "POST"
                }
            );
            return javaCompileResult;
        } catch (compileError) {
            reportClientError(`Unable to compile Java source ${selectedSourceFile.name}`, compileError);
            return null;
        } finally {
            saving = false;
        }
    }

    async function compileWorkspaceProfile() {
        if (!selectedWorkspaceName) {
            return null;
        }

        saving = true;
        message = "";

        try {
            await persistSelectedSourceForCompile();
            javaCompileResult = await loadJson(
                `/api/workspaces/${selectedWorkspaceName}/compile-profile`,
                {
                    method: "POST"
                }
            );
            return javaCompileResult;
        } catch (compileError) {
            reportClientError(`Unable to compile workspace profile ${selectedWorkspaceName}`, compileError);
            return null;
        } finally {
            saving = false;
        }
    }

    function escapeRegExp(text) {
        return text.replace(/[.*+?^${}()|[\]\\]/g, "\\$&");
    }

    function touchWorkspaceDocument() {
        workspaceDocument = workspaceDocument
            ? {
                ...workspaceDocument,
                settingsGroups: [...(workspaceDocument.settingsGroups || [])]
            }
            : workspaceDocument;
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

    async function validateRegexExpression(setting) {
        if (!setting?.key) {
            return;
        }

        touchWorkspaceDocument();

        try {
            const validationResult = await loadJson("/api/settings/regex/validate", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    value: setting.value || ""
                })
            });

            regexValidationResults = {
                ...regexValidationResults,
                [setting.key]: validationResult
            };
            setting.regexValidation = validationResult;
            touchWorkspaceDocument();
        } catch (validationError) {
            reportClientError(`Unable to validate regex for ${setting.key}`, validationError);
        }
    }

    function restoreSettingDefault(setting) {
        if (!setting?.key) {
            return;
        }

        if (setting.key === "SuspiciousTags" || setting.key === "SuspiciousProcessOutput") {
            if ((setting.value || "").trim() !== "") {
                return;
            }
        }

        setting.value = setting.defaultValue || "";
        setting.regexValidation = null;
        visualSettingsDirty = true;
        touchWorkspaceDocument();
        regexValidationResults = {
            ...regexValidationResults,
            [setting.key]: null
        };
    }

    async function startGenerate() {
        if (!selectedWorkspaceName || !selectedWorkspaceAvailableInTestar) {
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

    async function navigateToConfiguration() {
        if (currentPage === "configuration") {
            return;
        }

        await guardConfigurationTransition(async () => {
            currentPage = "configuration";
        });
    }

    async function navigateToRun() {
        if (currentPage === "run") {
            return;
        }

        await guardConfigurationTransition(async () => {
            currentPage = "run";
        });
    }

    async function navigateToCli() {
        if (currentPage === "cli") {
            return;
        }

        await guardConfigurationTransition(async () => {
            currentPage = "cli";
        });
    }

    async function navigateToSpy() {
        if (currentPage === "spy") {
            return;
        }

        await guardConfigurationTransition(async () => {
            currentPage = "spy";
            await refreshRemoteSpyStatus();
        });
    }

    async function navigateToResults() {
        if (currentPage === "results") {
            return;
        }

        await guardConfigurationTransition(async () => {
            currentPage = "results";
            loadResults();
        });
    }

    async function navigateToLogs() {
        if (currentPage === "logs") {
            return;
        }

        await guardConfigurationTransition(async () => {
            currentPage = "logs";
            await loadDebugFiles();
        });
    }

    function openStateModelExternalTab(url = STATE_MODEL_URL) {
        window.open(url, "_blank", "noopener,noreferrer");
    }

    function workspaceAvailableInTestar(workspaceSummary = selectedWorkspaceSummary) {
        return workspaceSummary?.availableInTestar === true;
    }

    $: selectedWorkspaceAvailableInTestar = workspaceAvailableInTestar();

    function workspaceSettingValue(settingKey) {
        for (const settingsGroup of workspaceDocument?.settingsGroups || []) {
            for (const setting of settingsGroup.settings || []) {
                if (setting.key === settingKey) {
                    return setting.value || "";
                }
            }
        }

        return "";
    }

    function normalizeSettingDisplayValue(value) {
        const text = String(value || "").trim();
        if (text.length >= 2) {
            const hasDoubleQuotes = text.startsWith("\"") && text.endsWith("\"");
            const hasSingleQuotes = text.startsWith("'") && text.endsWith("'");
            if (hasDoubleQuotes || hasSingleQuotes) {
                return text.slice(1, -1).trim();
            }
        }

        return text;
    }

    function settingsEditorId(editorId) {
        return editorId === "settings-form" || editorId === "test-settings";
    }

    function settingsEditorSelected() {
        return settingsEditorId(selectedEditor);
    }

    function hasSettingsChanges() {
        const persistedContent = savedTestSettingsContent || "";
        const currentContent = workspaceDocument?.testSettings?.content || "";
        if (currentContent !== persistedContent) {
            return true;
        }

        return visualSettingsDirty;
    }

    function hasCompositionPropertiesChanges() {
        return (workspaceDocument?.compositionProperties?.content || "") !== (savedCompositionPropertiesContent || "");
    }

    function hasPoliciesPropertiesChanges() {
        return (workspaceDocument?.policiesProperties?.content || "") !== (savedPoliciesPropertiesContent || "");
    }

    function hasSelectedSourceChanges(categoryNames) {
        if (!selectedSourceFile?.name || !categoryNames.includes(selectedSourceFile.category)) {
            return false;
        }

        return (selectedSourceFile.content || "") !== (savedSourceContents[selectedSourceFile.name] || "");
    }

    function configurationDirtyAreas() {
        return {
            settings: hasSettingsChanges(),
            "composition-file": hasCompositionPropertiesChanges(),
            "composition-flow": hasSelectedSourceChanges(["service", "capability"]),
            "policies-file": hasPoliciesPropertiesChanges(),
            "policies-flow": hasSelectedSourceChanges(["policy"])
        };
    }

    function activeGuardDialogDetails() {
        if (settingsEditorSelected()) {
            return {
                title: "Unsaved Settings Changes",
                message: "Detected unsaved settings changes. Save them before continuing, discard them, or cancel this navigation.",
                saveLabel: "Save"
            };
        }

        if (selectedEditor === "composition-properties") {
            return {
                title: "Unsaved Composition File Changes",
                message: "Detected unsaved composition.properties changes. Save them before continuing, discard them, or cancel this navigation.",
                saveLabel: "Save"
            };
        }

        if (selectedEditor === "policies-properties") {
            return {
                title: "Unsaved Policies File Changes",
                message: "Detected unsaved policies.properties changes. Save them before continuing, discard them, or cancel this navigation.",
                saveLabel: "Save"
            };
        }

        if (selectedEditor === "java-composition") {
            return {
                title: "Unsaved and Uncompiled Composition Changes",
                message: "Detected unsaved Java composition changes. Save and compile before continuing, discard them, or cancel this navigation.",
                saveLabel: "Save and Compile"
            };
        }

        if (selectedEditor === "java-policies") {
            return {
                title: "Unsaved and Uncompiled Policy Changes",
                message: "Detected unsaved Java policy changes. Save and compile before continuing, discard them, or cancel this navigation.",
                saveLabel: "Save and Compile"
            };
        }

        return {
            title: "Unsaved Changes",
            message: "Detected unsaved changes. Save them before continuing, discard them, or cancel this navigation.",
            saveLabel: "Save"
        };
    }

    async function saveCurrentSettingsEditor() {
        if (selectedEditor === "settings-form") {
            await saveVisualSettings();
            return;
        }

        if (selectedEditor === "test-settings" && workspaceDocument?.testSettings) {
            await saveWorkspaceFile(
                `/api/workspaces/${selectedWorkspaceName}/test-settings`,
                workspaceDocument.testSettings.content
            );
        }
    }

    async function saveCurrentGuardedEditor() {
        if (settingsEditorSelected()) {
            await saveCurrentSettingsEditor();
            return true;
        }

        if (selectedEditor === "composition-properties" && workspaceDocument?.compositionProperties) {
            await saveWorkspaceFile(
                `/api/workspaces/${selectedWorkspaceName}/composition-properties`,
                workspaceDocument.compositionProperties.content
            );
            return true;
        }

        if (selectedEditor === "policies-properties" && workspaceDocument?.policiesProperties) {
            await saveWorkspaceFile(
                `/api/workspaces/${selectedWorkspaceName}/policies-properties`,
                workspaceDocument.policiesProperties.content
            );
            return true;
        }

        if (selectedEditor === "java-composition" || selectedEditor === "java-policies") {
            const selectedSourceCategories = selectedEditor === "java-composition"
                ? ["service", "capability"]
                : ["policy"];
            const compileResult = selectedSourceFile?.name && selectedSourceCategories.includes(selectedSourceFile.category)
                ? await compileSelectedJavaSource()
                : await compileWorkspaceProfile();
            return compileResult?.success === true;
        }

        return true;
    }

    function hasCurrentGuardedChanges() {
        return shouldGuardConfigurationTransition({
            currentPage,
            currentEditor: selectedEditor,
            nextEditor: "__leave__",
            dirtyAreas: configurationDirtyAreas()
        });
    }

    async function guardConfigurationTransition(action, nextEditor = "__leave__") {
        if (!shouldGuardConfigurationTransition({
            currentPage,
            currentEditor: selectedEditor,
            nextEditor,
            dirtyAreas: configurationDirtyAreas()
        })) {
            await action();
            return;
        }

        pendingConfigurationAction = action;
        const dialogDetails = activeGuardDialogDetails();
        openUnsavedSettingsDialog(dialogDetails.title, dialogDetails.message, dialogDetails.saveLabel);
    }

    async function discardUnsavedConfigurationChanges() {
        const action = pendingConfigurationAction;
        await refreshWorkspaceDocument();
        closeUnsavedSettingsDialog();
        if (action) {
            await action();
        }
    }

    async function saveUnsavedConfigurationChanges() {
        const action = pendingConfigurationAction;
        const saved = await saveCurrentGuardedEditor();
        if (!saved) {
            closeUnsavedSettingsDialog();
            return;
        }

        if (hasCurrentGuardedChanges()) {
            return;
        }

        closeUnsavedSettingsDialog();
        if (action) {
            await action();
        }
    }

    async function navigateToStateModel() {
        const workspaceDialog = stateModelWorkspaceDialog(
            selectedWorkspaceName,
            selectedWorkspaceAvailableInTestar
        );
        if (workspaceDialog) {
            openStateModelDialog(workspaceDialog.title, workspaceDialog.message);
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

    function setSettingValue(setting, value) {
        if (!setting || setting.value === value) {
            return;
        }

        setting.value = value;
        if (setting.key === "SUTConnector") {
            selectedWorkspaceSutConnector = normalizeSettingDisplayValue(value);
        }
        if (setting.key === "SUTConnectorValue") {
            selectedWorkspaceSutConnectorValue = normalizeSettingDisplayValue(value);
        }
        if (setting.key === "StateObservationMode") {
            selectedWorkspaceObservationMode = normalizeSettingDisplayValue(value);
        }
        visualSettingsDirty = true;
        touchWorkspaceDocument();
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

    function toPropertiesContent(properties) {
        return Object.entries(properties)
            .map(([key, value]) => `${key}=${value}`)
            .join("\n")
            .concat("\n");
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

    function policyInterfaceSimpleName(propertyKey) {
        const interfaceByPropertyKey = {
            clickablePolicies: "ClickablePolicy",
            typeablePolicies: "TypeablePolicy",
            scrollablePolicies: "ScrollablePolicy",
            selectablePolicies: "SelectablePolicy",
            enabledPolicies: "EnabledPolicy",
            blockedPolicies: "BlockedPolicy",
            widgetFilterPolicies: "WidgetFilterPolicy",
            visiblePolicies: "VisiblePolicy",
            topLevelPolicies: "TopLevelPolicy"
        };

        return interfaceByPropertyKey[propertyKey] || "";
    }

    function inferPolicyPropertyKey(sourceFile) {
        const className = sourceClassName(sourceFile);
        for (const policyDefinition of workspaceDocument?.policyDefinitions || []) {
            if ((policyDefinition.configuredClassNames || []).includes(className)) {
                return policyDefinition.propertyKey;
            }
        }

        const content = sourceFile?.content || "";
        for (const policyDefinition of workspaceDocument?.policyDefinitions || []) {
            const interfaceName = policyInterfaceSimpleName(policyDefinition.propertyKey);
            if (interfaceName && content.includes(`implements ${interfaceName}`)) {
                return policyDefinition.propertyKey;
            }
        }

        return "";
    }

    async function togglePolicySourceActivation(sourceFile, enablePolicy) {
        if (!selectedWorkspaceName || !workspaceDocument?.policiesProperties?.content || !sourceFile) {
            return;
        }

        const propertyKey = inferPolicyPropertyKey(sourceFile);
        if (!propertyKey) {
            reportClientError(`Unable to infer policy seam for ${sourceFile.name}`, new Error("Unknown policy seam"));
            return;
        }

        const className = sourceClassName(sourceFile);
        const properties = parsePropertiesContent(workspaceDocument.policiesProperties.content);
        const existingValues = (properties[propertyKey] || "")
            .split(";")
            .map((value) => value.trim())
            .filter((value) => value !== "" && value !== className);

        if (enablePolicy) {
            existingValues.push(className);
        }

        properties[propertyKey] = existingValues.join("; ");

        await saveWorkspaceFile(
            `/api/workspaces/${selectedWorkspaceName}/policies-properties`,
            toPropertiesContent(properties)
        );
    }

    async function loadResults() {
        try {
            resultsData = await loadJson("/api/execution/scriptless/results");
            const resultGroups = resultsData.groups || [];
            if (resultGroups.length > 0) {
                await selectResultGroup(resultGroups[resultGroups.length - 1]);
            } else {
                selectedResultGroup = null;
                selectedResultFile = null;
            }
        } catch (resultsError) {
            reportClientError("Unable to load output results", resultsError);
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
            title: "Edit test.settings file",
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
        selectedResultFile = null;
    }

    function showResultSummary() {
        selectedResultFile = null;
    }

    async function loadResultFile(resultFile) {
        if (!resultFile?.name) {
            selectedResultFile = null;
            return;
        }

        try {
            const resultPath = encodeURIComponent(resultFile.path);
            selectedResultFile = await loadJson(`/api/execution/scriptless/results/${resultFile.name}?path=${resultPath}`);
        } catch (fileError) {
            reportClientError(`Unable to load result file ${resultFile.name}`, fileError);
        }
    }

    function applyRefreshedResults(refreshedResults, preferredGroupPath = "") {
        resultsData = refreshedResults;
        const resultGroups = resultsData?.groups || [];
        if (resultGroups.length > 0) {
            selectedResultGroup = resultGroups.find((resultGroup) => resultGroup.path === preferredGroupPath)
                || resultGroups[resultGroups.length - 1];
        } else {
            selectedResultGroup = null;
        }

        selectedResultFile = null;
    }

    async function deleteResultFile(resultFile) {
        if (!resultFile?.path) {
            return;
        }

        try {
            const resultPath = encodeURIComponent(resultFile.path);
            const currentGroupPath = selectedResultGroup?.path || "";
            const refreshedResults = await loadJson(`/api/execution/scriptless/results/${resultFile.name}?path=${resultPath}`, {
                method: "DELETE"
            });
            applyRefreshedResults(refreshedResults, currentGroupPath);
        } catch (deleteError) {
            reportClientError(`Unable to delete result file ${resultFile.name}`, deleteError);
        }
    }

    async function deleteResultGroup(resultGroup) {
        if (!resultGroup?.path) {
            return;
        }

        try {
            const resultPath = encodeURIComponent(resultGroup.path);
            const refreshedResults = await loadJson(`/api/execution/scriptless/result-groups?path=${resultPath}`, {
                method: "DELETE"
            });
            applyRefreshedResults(refreshedResults);
        } catch (deleteError) {
            reportClientError(`Unable to delete result output folder ${resultGroup.name}`, deleteError);
        }
    }

    async function startRemoteSpyMode() {
        if (!selectedWorkspaceName || !selectedWorkspaceAvailableInTestar) {
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

    async function createCompositionModuleSource(flowNode) {
        if (!selectedWorkspaceName || !flowNode?.propertyKey) {
            return;
        }

        saving = true;
        message = "";

        try {
            const sourceFile = await loadJson(
                `/api/workspaces/${selectedWorkspaceName}/composition/modules/${encodeURIComponent(flowNode.propertyKey)}/source`,
                {
                    method: "POST"
                }
            );

            await refreshWorkspaceDocument();
            selectedSourceName = sourceFile.name;
            selectedSourceFile = sourceFile;
            selectedEditor = "java-composition";
            showTemporaryMessage(`${sourceFile.name} ready for editing.`);
        } catch (sourceError) {
            reportClientError(`Unable to create or open source for ${flowNode.title}`, sourceError);
        } finally {
            saving = false;
        }
    }

    async function createPolicySource(policyDefinition) {
        if (!selectedWorkspaceName || !policyDefinition?.propertyKey) {
            return;
        }

        saving = true;
        message = "";

        try {
            const sourceFile = await loadJson(
                `/api/workspaces/${selectedWorkspaceName}/policies/${encodeURIComponent(policyDefinition.propertyKey)}/source`,
                {
                    method: "POST"
                }
            );

            await refreshWorkspaceDocument();
            selectedSourceName = sourceFile.name;
            selectedSourceFile = sourceFile;
            selectedEditor = "java-policies";
            showTemporaryMessage(`${sourceFile.name} ready for editing.`);
        } catch (sourceError) {
            reportClientError(`Unable to create or open policy source for ${policyDefinition.label}`, sourceError);
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
        if (!selectedWorkspaceName || !selectedWorkspaceAvailableInTestar) {
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

    async function startCliManualSession() {
        if (!selectedWorkspaceName) {
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
                }
            });
            showTemporaryMessage(cliStatus.message || "Manual CLI session started.");
        } catch (cliError) {
            reportClientError("Unable to start manual CLI session", cliError);
        } finally {
            saving = false;
        }
    }

    async function startCliAgentSession() {
        if (!selectedWorkspaceName) {
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
                }
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
                const defaultWorkspace = workspaces.find((workspace) => workspace.name === "webdriver_generic") || workspaces[0];
                await loadWorkspace(defaultWorkspace.name);
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
                on:change={(event) => {
                    const nextWorkspaceName = event.currentTarget.value;
                    guardConfigurationTransition(async () => {
                        await loadWorkspace(nextWorkspaceName);
                    });
                }}
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
            closeCompositionSourceEditor={closeCompositionSourceEditor}
            closePolicySourceEditor={closePolicySourceEditor}
            compileSelectedJavaSource={compileSelectedJavaSource}
            compileWorkspaceProfile={compileWorkspaceProfile}
            createCompositionModuleSource={createCompositionModuleSource}
            createPolicySource={createPolicySource}
            javaCompileResult={javaCompileResult}
            regexValidationResults={regexValidationResults}
            saving={saving}
            setSettingValue={setSettingValue}
            selectSource={selectSource}
            selectCompositionFlowNode={selectCompositionFlowNode}
            selectSettingsGroup={selectSettingsGroup}
            selectedEditor={selectedEditor}
            selectedCompositionFlowNode={selectedCompositionFlowNode}
            selectedSettingsGroupId={selectedSettingsGroupId}
            selectedSourceFile={selectedSourceFile}
            restoreSettingDefault={restoreSettingDefault}
            togglePolicySourceActivation={togglePolicySourceActivation}
            validateRegexExpression={validateRegexExpression}
            workspaceDocument={workspaceDocument}
        />
    {/if}

    {#if currentPage === "run"}
        <RunTestarView
            saving={saving}
            scriptlessStatus={scriptlessStatus}
            selectedWorkspaceName={selectedWorkspaceName}
            selectedWorkspaceAvailableInTestar={selectedWorkspaceAvailableInTestar}
            selectedWorkspaceSutConnectorValue={selectedWorkspaceSutConnectorValue}
            startGenerate={startGenerate}
            stopGenerate={stopGenerate}
        />
    {/if}

    {#if currentPage === "spy"}
        <SpyModeView
            scriptlessStatus={scriptlessStatus}
            saving={saving}
            selectedWorkspaceName={selectedWorkspaceName}
            selectedWorkspaceAvailableInTestar={selectedWorkspaceAvailableInTestar}
            selectedWorkspaceSutConnectorValue={selectedWorkspaceSutConnectorValue}
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
        {#key `cli:${selectedWorkspaceName}`}
            <CliModeView
                cliAgentSettings={cliAgentSettings}
                cliStatus={cliStatus}
                saving={saving}
                saveCliAgentSettings={saveCliAgentSettings}
                selectedWorkspaceSutConnector={selectedWorkspaceSutConnector}
                selectedWorkspaceSutConnectorValue={selectedWorkspaceSutConnectorValue}
                selectedWorkspaceObservationMode={selectedWorkspaceObservationMode}
                selectedWorkspaceAvailableInCli={Boolean(selectedWorkspaceName)}
                startCliAgentSession={startCliAgentSession}
                startCliManualSession={startCliManualSession}
                runCliManualCommand={runCliManualCommand}
                stopCliAgentSession={stopCliAgentSession}
                stopCliManualSession={stopCliManualSession}
            />
        {/key}
    {/if}

    {#if currentPage === "results"}
        <TestResultsView
            deleteResultFile={deleteResultFile}
            deleteResultGroup={deleteResultGroup}
            loadResultFile={loadResultFile}
            resultsData={resultsData}
            selectedResultFile={selectedResultFile}
            selectedResultGroup={selectedResultGroup}
            selectResultGroup={selectResultGroup}
            showResultSummary={showResultSummary}
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

    {#if unsavedSettingsDialog.open}
        <div class="composition-modal-backdrop" role="presentation" on:click={closeUnsavedSettingsDialogFromBackdrop}>
            <div
                class="composition-modal state-model-dialog"
                role="dialog"
                aria-modal="true"
                aria-labelledby="unsaved-settings-dialog-title"
            >
                <div class="composition-modal-header">
                    <div>
                        <h2 id="unsaved-settings-dialog-title">{unsavedSettingsDialog.title}</h2>
                        <p>{unsavedSettingsDialog.message}</p>
                    </div>
                </div>
                <div class="composition-modal-actions">
                    <button type="button" on:click={saveUnsavedConfigurationChanges} disabled={saving}>
                        {unsavedSettingsDialog.saveLabel}
                    </button>
                    <button type="button" class="secondary" on:click={discardUnsavedConfigurationChanges} disabled={saving}>
                        Discard
                    </button>
                    <button type="button" class="secondary" on:click={closeUnsavedSettingsDialog} disabled={saving}>
                        Cancel
                    </button>
                </div>
            </div>
        </div>
    {/if}
</div>
