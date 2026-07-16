<script>
    import { onDestroy, onMount } from "svelte";
    import BasicSettingsView from "./BasicSettingsView.svelte";
    import CliModeView from "./CliModeView.svelte";
    import TestCompositionPageView from "./TestCompositionPageView.svelte";
    import TestPoliciesPageView from "./TestPoliciesPageView.svelte";
    import TestSettingsPageView from "./TestSettingsPageView.svelte";
    import RunTestarView from "./RunTestarView.svelte";
    import SpyModeView from "./SpyModeView.svelte";
    import TestResultsView from "./TestResultsView.svelte";
    import InspectLogsView from "./InspectLogsView.svelte";
    import TestGoalsView from "./TestGoalsView.svelte";
    import TestOraclesView from "./TestOraclesView.svelte";
    import { testGoalFolderSelectionState } from "./testGoalsModel.js";
    import { ORACLE_COMPOSITION_NODE_ID, TEST_ORACLE_PANEL_IDS, oracleSettingsGroupId } from "./testOraclesModel.js";
    import { shouldGuardConfigurationTransition } from "./configurationGuard.js";
    import { clearSelectedSourceState } from "./policyEditorState.js";
    import { stateModelWorkspaceDialog } from "./stateModelNavigation.js";
    import { objectSnapshot } from "./editorDirtyState.js";
    import { resultFileUrl, resultGroupDeleteUrl, resultListUrl } from "./resultApi.js";
    import {
        defaultWorkspaceCreateDraft,
        defaultWorkspaceRenameDraft,
        workspaceCreateRequest,
        workspaceCreateValidation,
        workspaceRenameRequest,
        workspaceRenameValidation
    } from "./workspaceManagementModel.js";
    import {
        BASIC_ROLE_SETTINGS_GROUP_IDS,
        TEST_SETTINGS_GROUP_IDS,
        WEB_STUDIO_ROLES,
        normalizeWebStudioRole,
        pageForRole
    } from "./webStudioRoles.js";
    import {
        menuHasActivePage,
        resultMenuItems,
        runModeMenuItems,
        testConfigurationMenuItems
    } from "./webStudioNavigation.js";

    const STATE_MODEL_URL = "http://localhost:8090/models";
    const ROLE_STORAGE_KEY = "testar-webstudio-role";
    const CLI_AGENT_SETTING_KEYS = {
        apiKeyEnvVarName: "AgentCLIApiKeyEnvVar",
        baseUrl: "AgentCLIBaseUrl",
        model: "AgentCLIModel",
        reasoningEffort: "AgentCLIReasoningEffort",
        sandboxMode: "AgentCLISandboxMode",
        approvalPolicy: "AgentCLIApprovalPolicy",
        allowNetworkAccess: "AgentCLIAllowNetworkAccess",
        skipGitRepoCheck: "AgentCLISkipGitRepoCheck",
        promptTitle: "AgentCLIPromptTitle",
        promptText: "AgentCLIPromptText"
    };
    const DEFAULT_CLI_AGENT_SETTINGS = {
        apiKeyEnvVarName: "OPENAI_API_KEY",
        baseUrl: "",
        model: "gpt-5.4-mini",
        reasoningEffort: "medium",
        sandboxMode: "danger-full-access",
        approvalPolicy: "never",
        allowNetworkAccess: false,
        skipGitRepoCheck: true,
        promptTitle: "Test Parabank Login",
        promptText: "As a test agent verify that you can log in with the credentials john/demo. Then the Welcome John Smith message is shown."
    };

    let workspaces = [];
    let selectedWorkspaceName = "";
    let selectedWorkspaceSummary = null;
    let selectedWorkspaceAvailableInSharedRuntime = false;
    let workspaceDocument = null;
    let selectedWorkspaceSutConnector = "";
    let selectedWorkspaceSutConnectorValue = "";
    let selectedWorkspaceCliStateProjectionMode = "";
    let selectedSourceName = "";
    let selectedSourceFile = null;
    let cliStatus = null;
    let cliAgentSettings = { ...DEFAULT_CLI_AGENT_SETTINGS };
    let savedCliAgentSettings = { ...DEFAULT_CLI_AGENT_SETTINGS };
    let scriptlessStatus = null;
    let loading = false;
    let saving = false;
    let message = "";
    let messageTimeoutHandle = null;
    let scriptlessPollHandle = null;
    let currentPage = "settings";
    let currentRole = WEB_STUDIO_ROLES.ADVANCED;
    let activeNavMenu = "";
    let selectedOraclePanelId = TEST_ORACLE_PANEL_IDS.ACTIVE;
    let resultsData = null;
    let selectedResultGroup = null;
    let selectedResultFile = null;
    let selectedEditor = "java-composition";
    let selectedSettingsGroupId = "";
    let debugFiles = [];
    let selectedDebugFile = null;
    let testGoalTree = null;
    let selectedTestGoalFile = null;
    let selectedTestGoalFolderPath = "";
    let testGoalDraftContent = "";
    let savedTestGoalContent = "";
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
    let workspaceManagementDialogOpen = false;
    let workspaceManagementTab = "create";
    let workspaceCreateDraft = defaultWorkspaceCreateDraft([], "");
    let workspaceRenameDraft = defaultWorkspaceRenameDraft("");
    let workspaceManagementError = "";
    let savedTestSettingsContent = "";
    let savedCompositionPropertiesContent = "";
    let savedPoliciesPropertiesContent = "";
    let savedSourceContents = {};
    let pendingConfigurationAction = null;
    let pendingGuardKind = "configuration";

    $: testGoalDirty = selectedTestGoalFile !== null
        && testGoalDraftContent !== savedTestGoalContent;

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
        pendingGuardKind = "configuration";
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
        const [workspaceResponse, cliStatusResponse, scriptlessResponse, spyResponse] = await Promise.all([
            loadJson("/api/workspaces"),
            loadJson("/api/execution/status/cli"),
            loadJson("/api/execution/status/scriptless"),
            loadJson("/api/spy/status")
        ]);

        workspaces = workspaceResponse;
        cliStatus = cliStatusResponse;
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
            selectedWorkspaceCliStateProjectionMode = "";
            cliAgentSettings = { ...DEFAULT_CLI_AGENT_SETTINGS };
            savedCliAgentSettings = { ...DEFAULT_CLI_AGENT_SETTINGS };
            savedTestSettingsContent = "";
            visualSettingsDirty = false;
            selectedSourceName = "";
            selectedSourceFile = null;
            selectedEditor = "java-composition";
            selectedSettingsGroupId = "";
            selectedCompositionFlowNode = null;
            regexValidationResults = {};
            javaCompileResult = null;
            resetTestGoalSelection();
            testGoalTree = null;
            resultsData = null;
            selectedResultGroup = null;
            selectedResultFile = null;
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
            syncWorkspaceRuntimeSettings();
            savedCliAgentSettings = normalizedCliAgentSettings(cliAgentSettings);
            selectedSourceName = "";
            selectedSourceFile = null;
            selectedEditor = "java-composition";
            selectedSettingsGroupId = workspaceDocument?.settingsGroups?.[0]?.id || "";
            selectedCompositionFlowNode = null;
            regexValidationResults = {};
            javaCompileResult = null;
            resetTestGoalSelection();
            if (currentPage === "test-goals") {
                await loadTestGoalTree(workspaceName);
            } else if (currentPage === "results") {
                await loadResults(workspaceName);
            } else if (currentPage === "basic-settings") {
                openBasicSettingsImmediate();
            } else if (currentPage === "settings") {
                await openVisualSettings();
            } else if (currentPage === "composition") {
                await openJavaComposition();
            } else if (currentPage === "policies") {
                await openJavaPolicies();
            }
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

    async function openVisualSettingsGroup(groupId) {
        await guardConfigurationTransition(async () => {
            openEditorImmediate("settings-form");
            selectedSettingsGroupId = groupId || selectedSettingsGroupId;
            if (!selectedSettingsGroupId && workspaceDocument?.settingsGroups?.length > 0) {
                selectedSettingsGroupId = workspaceDocument.settingsGroups[0].id;
            }
        }, "settings-form");
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
        syncWorkspaceRuntimeSettings();
        savedCliAgentSettings = normalizedCliAgentSettings(cliAgentSettings);
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

    function storedWebStudioRole() {
        if (typeof window === "undefined") {
            return WEB_STUDIO_ROLES.ADVANCED;
        }

        return normalizeWebStudioRole(window.localStorage.getItem(ROLE_STORAGE_KEY));
    }

    function storeWebStudioRole(role) {
        if (typeof window !== "undefined") {
            window.localStorage.setItem(ROLE_STORAGE_KEY, normalizeWebStudioRole(role));
        }
    }

    function firstSettingsGroupId(allowedSettingsGroupIds) {
        const availableGroupIds = new Set((workspaceDocument?.settingsGroups || []).map((settingsGroup) => settingsGroup.id));
        return allowedSettingsGroupIds.find((groupId) => availableGroupIds.has(groupId))
            || workspaceDocument?.settingsGroups?.[0]?.id
            || "";
    }

    function openAllowedSettingsImmediate(allowedSettingsGroupIds) {
        openEditorImmediate("settings-form");
        if (!allowedSettingsGroupIds.includes(selectedSettingsGroupId)) {
            selectedSettingsGroupId = firstSettingsGroupId(allowedSettingsGroupIds);
        }
    }

    function openBasicSettingsImmediate() {
        openAllowedSettingsImmediate(BASIC_ROLE_SETTINGS_GROUP_IDS);
    }

    function openTestSettingsImmediate() {
        openAllowedSettingsImmediate(TEST_SETTINGS_GROUP_IDS);
    }

    function openOracleCompositionImmediate() {
        openEditorImmediate("java-composition");
        const oracleNode = compositionFlowNodes.find((flowNode) => flowNode.id === ORACLE_COMPOSITION_NODE_ID);
        if (oracleNode) {
            selectCompositionFlowNode(oracleNode);
        }
    }

    async function openOraclePanel(panelId) {
        await guardConfigurationTransition(async () => {
            selectedOraclePanelId = panelId;
            if (panelId === TEST_ORACLE_PANEL_IDS.ACTIVE) {
                return;
            }

            if (panelId === TEST_ORACLE_PANEL_IDS.JAVA_COMPOSITION) {
                openOracleCompositionImmediate();
                return;
            }

            openEditorImmediate("settings-form");
            selectedSettingsGroupId = oracleSettingsGroupId(panelId);
        }, `oracle-panel:${panelId}`);
    }

    async function activatePageImmediate(page) {
        currentPage = page;
        if (page === "basic-settings") {
            openBasicSettingsImmediate();
        } else if (page === "oracles") {
            await openOraclePanel(selectedOraclePanelId || TEST_ORACLE_PANEL_IDS.ACTIVE);
        } else if (page === "settings") {
            openTestSettingsImmediate();
        } else if (page === "composition") {
            await openJavaComposition();
        } else if (page === "policies") {
            await openJavaPolicies();
        } else if (page === "test-goals") {
            await loadTestGoalTree();
        } else if (page === "spy") {
            await refreshRemoteSpyStatus();
        } else if (page === "results") {
            loadResults();
        }
    }

    async function changeWebStudioRole(nextRole) {
        const normalizedRole = normalizeWebStudioRole(nextRole);
        if (normalizedRole === currentRole) {
            return;
        }

        await guardApplicationTransition(async () => {
            currentRole = normalizedRole;
            storeWebStudioRole(currentRole);
            await activatePageImmediate(pageForRole(currentRole, currentPage));
        });
    }

    async function navigateToBasicSettings() {
        if (currentPage === "basic-settings") {
            return;
        }

        await guardApplicationTransition(async () => {
            await activatePageImmediate("basic-settings");
        }, "settings-form");
    }

    async function navigateToSettings() {
        if (currentPage === "settings") {
            return;
        }

        await guardApplicationTransition(async () => {
            await activatePageImmediate("settings");
        }, "settings-form");
    }

    async function navigateToAdvancedCompositionFlow() {
        if (currentPage === "composition") {
            await openJavaComposition();
            return;
        }

        await guardApplicationTransition(async () => {
            await activatePageImmediate("composition");
        }, "java-composition");
    }

    async function navigateToAdvancedPolicies() {
        if (currentPage === "policies") {
            await openJavaPolicies();
            return;
        }

        await guardApplicationTransition(async () => {
            await activatePageImmediate("policies");
        }, "java-policies");
    }

    async function navigateToTestOracles() {
        activeNavMenu = "";
        if (currentPage === "oracles") {
            return;
        }

        await guardApplicationTransition(async () => {
            selectedOraclePanelId = TEST_ORACLE_PANEL_IDS.ACTIVE;
            await activatePageImmediate("oracles");
        });
    }

    async function navigateToRun() {
        if (currentPage === "run") {
            return;
        }

        await guardApplicationTransition(async () => {
            await activatePageImmediate("run");
        });
    }

    async function navigateToCli() {
        if (currentPage === "cli") {
            return;
        }

        await guardApplicationTransition(async () => {
            await activatePageImmediate("cli");
        });
    }

    async function navigateToTestGoals() {
        if (currentPage === "test-goals") {
            return;
        }

        activeNavMenu = "";
        await guardApplicationTransition(async () => {
            await activatePageImmediate("test-goals");
        });
    }

    async function navigateToSpy() {
        if (currentPage === "spy") {
            return;
        }

        activeNavMenu = "";
        await guardApplicationTransition(async () => {
            await activatePageImmediate("spy");
        });
    }

    async function navigateToResults() {
        if (currentPage === "results") {
            return;
        }

        await guardApplicationTransition(async () => {
            await activatePageImmediate("results");
        });
    }

    async function navigateToLogs() {
        if (currentPage === "logs") {
            return;
        }

        await guardApplicationTransition(async () => {
            await activatePageImmediate("logs");
            await loadDebugFiles();
        });
    }

    function toggleNavMenu(menuId) {
        activeNavMenu = activeNavMenu === menuId ? "" : menuId;
    }

    async function navigateFromMenu(item) {
        if (item.disabled) {
            return;
        }

        activeNavMenu = "";

        if (item.id === "basic-settings") {
            await navigateToBasicSettings();
        } else if (item.id === "settings") {
            await navigateToSettings();
        } else if (item.id === "basic-oracles" || item.id === "advanced-oracles") {
            await navigateToTestOracles();
        } else if (item.id === "test-goals") {
            await navigateToTestGoals();
        } else if (item.id === "composition") {
            await navigateToAdvancedCompositionFlow();
        } else if (item.id === "policies") {
            await navigateToAdvancedPolicies();
        } else if (item.id === "run") {
            await navigateToRun();
        } else if (item.id === "cli") {
            await navigateToCli();
        } else if (item.id === "results") {
            await navigateToResults();
        } else if (item.id === "state-model") {
            await navigateToStateModel();
        } else if (item.id === "logs") {
            await navigateToLogs();
        }
    }

    async function openWorkspaceManagementDialog() {
        await guardApplicationTransition(async () => {
            workspaceCreateDraft = defaultWorkspaceCreateDraft(workspaces, selectedWorkspaceName);
            workspaceRenameDraft = defaultWorkspaceRenameDraft(selectedWorkspaceName);
            workspaceManagementTab = "create";
            workspaceManagementError = "";
            workspaceManagementDialogOpen = true;
        });
    }

    function closeWorkspaceManagementDialog() {
        if (saving) {
            return;
        }

        workspaceManagementDialogOpen = false;
        workspaceManagementError = "";
    }

    function closeWorkspaceManagementDialogFromBackdrop(event) {
        if (event.target === event.currentTarget) {
            closeWorkspaceManagementDialog();
        }
    }

    async function createWorkspaceFromDialog() {
        const validation = workspaceCreateValidation(workspaceCreateDraft, workspaces);
        if (!validation.valid) {
            workspaceManagementError = validation.message;
            return;
        }

        saving = true;
        workspaceManagementError = "";
        message = "";

        try {
            const request = workspaceCreateRequest(workspaceCreateDraft);
            const createdWorkspace = await loadJson("/api/workspaces", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(request)
            });
            await refreshInitialData();
            currentPage = "settings";
            await loadWorkspace(createdWorkspace?.name || request.name);
            workspaceManagementDialogOpen = false;
            showTemporaryMessage(`Workspace ${request.name} created.`);
        } catch (createError) {
            reportClientError("Unable to create workspace", createError);
            workspaceManagementError = createError.message || "Unable to create workspace.";
        } finally {
            saving = false;
        }
    }

    async function renameWorkspaceFromDialog() {
        const validation = workspaceRenameValidation(workspaceRenameDraft, selectedWorkspaceName, workspaces);
        if (!validation.valid) {
            workspaceManagementError = validation.message;
            return;
        }

        saving = true;
        workspaceManagementError = "";
        message = "";

        try {
            const request = workspaceRenameRequest(workspaceRenameDraft);
            const renamedWorkspace = await loadJson(`/api/workspaces/${selectedWorkspaceName}/rename`, {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(request)
            });
            await refreshInitialData();
            currentPage = "settings";
            await loadWorkspace(renamedWorkspace?.name || request.name);
            workspaceManagementDialogOpen = false;
            showTemporaryMessage(`Workspace renamed to ${request.name}.`);
        } catch (renameError) {
            reportClientError("Unable to rename workspace", renameError);
            workspaceManagementError = renameError.message || "Unable to rename workspace.";
        } finally {
            saving = false;
        }
    }

    function openStateModelExternalTab(url = STATE_MODEL_URL) {
        window.open(url, "_blank", "noopener,noreferrer");
    }

    function workspaceAvailableInSharedRuntime(workspaceSummary = selectedWorkspaceSummary) {
        return Boolean(workspaceSummary);
    }

    $: selectedWorkspaceAvailableInSharedRuntime = workspaceAvailableInSharedRuntime();

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

    function workspaceSettingBoolean(settingKey, defaultValue = false) {
        const value = workspaceSettingValue(settingKey);
        if (value === "") {
            return defaultValue;
        }

        return value === true || `${value}`.toLowerCase() === "true";
    }

    function workspaceSettingDefaultValue(settingKey) {
        const setting = findWorkspaceSetting(settingKey);
        return setting?.defaultValue || "";
    }

    function normalizedCliAgentSettings(settings) {
        const source = settings || {};
        const valueOrDefault = (value, defaultValue) => {
            if (value === null || value === undefined || value === "") {
                return defaultValue;
            }

            return value;
        };

        return {
            apiKeyEnvVarName: valueOrDefault(source.apiKeyEnvVarName, DEFAULT_CLI_AGENT_SETTINGS.apiKeyEnvVarName),
            baseUrl: source.baseUrl ?? DEFAULT_CLI_AGENT_SETTINGS.baseUrl,
            model: valueOrDefault(source.model, DEFAULT_CLI_AGENT_SETTINGS.model),
            reasoningEffort: valueOrDefault(source.reasoningEffort, DEFAULT_CLI_AGENT_SETTINGS.reasoningEffort),
            sandboxMode: valueOrDefault(source.sandboxMode, DEFAULT_CLI_AGENT_SETTINGS.sandboxMode),
            approvalPolicy: valueOrDefault(source.approvalPolicy, DEFAULT_CLI_AGENT_SETTINGS.approvalPolicy),
            allowNetworkAccess: Boolean(source.allowNetworkAccess),
            skipGitRepoCheck: source.skipGitRepoCheck !== false,
            promptTitle: valueOrDefault(source.promptTitle, DEFAULT_CLI_AGENT_SETTINGS.promptTitle),
            promptText: valueOrDefault(source.promptText, DEFAULT_CLI_AGENT_SETTINGS.promptText)
        };
    }

    function cliAgentSettingsFromWorkspace() {
        return normalizedCliAgentSettings({
            apiKeyEnvVarName: workspaceSettingValue(CLI_AGENT_SETTING_KEYS.apiKeyEnvVarName),
            baseUrl: workspaceSettingValue(CLI_AGENT_SETTING_KEYS.baseUrl),
            model: workspaceSettingValue(CLI_AGENT_SETTING_KEYS.model),
            reasoningEffort: workspaceSettingValue(CLI_AGENT_SETTING_KEYS.reasoningEffort),
            sandboxMode: workspaceSettingValue(CLI_AGENT_SETTING_KEYS.sandboxMode),
            approvalPolicy: workspaceSettingValue(CLI_AGENT_SETTING_KEYS.approvalPolicy),
            allowNetworkAccess: workspaceSettingBoolean(
                CLI_AGENT_SETTING_KEYS.allowNetworkAccess,
                DEFAULT_CLI_AGENT_SETTINGS.allowNetworkAccess
            ),
            skipGitRepoCheck: workspaceSettingBoolean(
                CLI_AGENT_SETTING_KEYS.skipGitRepoCheck,
                DEFAULT_CLI_AGENT_SETTINGS.skipGitRepoCheck
            ),
            promptTitle: workspaceSettingValue(CLI_AGENT_SETTING_KEYS.promptTitle),
            promptText: workspaceSettingValue(CLI_AGENT_SETTING_KEYS.promptText)
        });
    }

    function syncWorkspaceRuntimeSettings() {
        selectedWorkspaceSutConnector = normalizeSettingDisplayValue(
            parsePropertiesContent(workspaceDocument?.testSettings?.content || "").SUTConnector
                || workspaceSettingValue("SUTConnector")
        );
        selectedWorkspaceSutConnectorValue = normalizeSettingDisplayValue(
            parsePropertiesContent(workspaceDocument?.testSettings?.content || "").SUTConnectorValue
                || workspaceSettingValue("SUTConnectorValue")
        );
        selectedWorkspaceCliStateProjectionMode = normalizeSettingDisplayValue(
            parsePropertiesContent(workspaceDocument?.testSettings?.content || "").CliStateProjectionMode
                || workspaceSettingValue("CliStateProjectionMode")
                || workspaceSettingDefaultValue("CliStateProjectionMode")
        );
        cliAgentSettings = cliAgentSettingsFromWorkspace();
    }

    function findWorkspaceSetting(settingKey) {
        for (const settingsGroup of workspaceDocument?.settingsGroups || []) {
            const setting = (settingsGroup.settings || []).find((candidate) => candidate.key === settingKey);
            if (setting) {
                return setting;
            }
        }

        return null;
    }

    function setWorkspaceSettingByKey(settingKey, value) {
        const setting = findWorkspaceSetting(settingKey);
        if (!setting) {
            return;
        }

        setSettingValue(setting, `${value}`);
    }

    function hasCliAgentSettingsChanges() {
        return JSON.stringify(normalizedCliAgentSettings(cliAgentSettings))
            !== JSON.stringify(normalizedCliAgentSettings(savedCliAgentSettings));
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

    function hasCurrentSelectedSourceChanges() {
        if (!selectedSourceFile?.category) {
            return false;
        }

        return hasSelectedSourceChanges([selectedSourceFile.category]);
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

    async function guardApplicationTransition(action, nextEditor = "__leave__") {
        if (currentPage === "test-goals" && testGoalDirty) {
            openTestGoalGuard(action);
            return;
        }

        if (currentPage === "cli" && hasCliAgentSettingsChanges()) {
            openCliAgentSettingsGuard(action);
            return;
        }

        await guardConfigurationTransition(action, nextEditor);
    }

    async function guardCliAgentSettingsTransition(action) {
        if (hasCliAgentSettingsChanges()) {
            openCliAgentSettingsGuard(action);
            return;
        }

        await action();
    }

    function openCliAgentSettingsGuard(action) {
        pendingConfigurationAction = action;
        pendingGuardKind = "cli-agent";
        openUnsavedSettingsDialog(
            "Unsaved Agent CLI Settings",
            "Detected unsaved Agent CLI settings. Save them before continuing, discard them, or cancel this navigation.",
            "Save"
        );
    }

    function openTestGoalGuard(action) {
        pendingConfigurationAction = action;
        pendingGuardKind = "test-goal";
        openUnsavedSettingsDialog(
            "Unsaved Test Goal Changes",
            "Detected unsaved Test Goal changes. Save them before continuing, discard them, or cancel this navigation.",
            "Save"
        );
    }

    async function discardUnsavedConfigurationChanges() {
        const action = pendingConfigurationAction;
        if (pendingGuardKind === "cli-agent") {
            cliAgentSettings = normalizedCliAgentSettings(savedCliAgentSettings);
        } else if (pendingGuardKind === "test-goal") {
            discardTestGoalChanges();
        } else {
            await refreshWorkspaceDocument();
        }
        closeUnsavedSettingsDialog();
        if (action) {
            await action();
        }
    }

    async function saveUnsavedConfigurationChanges() {
        const action = pendingConfigurationAction;
        let saved;
        if (pendingGuardKind === "cli-agent") {
            saved = await saveCliAgentSettings();
        } else if (pendingGuardKind === "test-goal") {
            saved = await saveTestGoalFile();
        } else {
            saved = await saveCurrentGuardedEditor();
        }
        if (!saved) {
            closeUnsavedSettingsDialog();
            return;
        }

        if (pendingGuardKind !== "cli-agent" && hasCurrentGuardedChanges()) {
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
            selectedWorkspaceAvailableInSharedRuntime
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
        if (setting.key === "CliStateProjectionMode") {
            selectedWorkspaceCliStateProjectionMode = normalizeSettingDisplayValue(value);
        }
        if (Object.values(CLI_AGENT_SETTING_KEYS).includes(setting.key)) {
            cliAgentSettings = cliAgentSettingsFromWorkspace();
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

    async function loadResults(workspaceName = selectedWorkspaceName) {
        if (!workspaceName) {
            resultsData = null;
            selectedResultGroup = null;
            selectedResultFile = null;
            return;
        }

        try {
            resultsData = await loadJson(resultListUrl(workspaceName));
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

    function resetTestGoalSelection() {
        selectedTestGoalFile = null;
        selectedTestGoalFolderPath = "";
        testGoalDraftContent = "";
        savedTestGoalContent = "";
    }

    function testGoalApiPath(workspaceName, suffix = "") {
        const encodedWorkspaceName = encodeURIComponent(workspaceName || selectedWorkspaceName);
        return `/api/workspaces/${encodedWorkspaceName}/test-goals${suffix}`;
    }

    async function loadTestGoalTree(workspaceName = selectedWorkspaceName) {
        if (!workspaceName) {
            testGoalTree = null;
            return;
        }

        try {
            testGoalTree = await loadJson(testGoalApiPath(workspaceName));
        } catch (testGoalError) {
            reportClientError("Unable to load Test Goals", testGoalError);
            testGoalTree = null;
        }
    }

    async function loadTestGoalFileNow(goalFile) {
        if (!goalFile?.path) {
            selectedTestGoalFile = null;
            selectedTestGoalFolderPath = "";
            testGoalDraftContent = "";
            savedTestGoalContent = "";
            return;
        }

        try {
            const goalPath = encodeURIComponent(goalFile.path);
            selectedTestGoalFile = await loadJson(`${testGoalApiPath(selectedWorkspaceName, "/file")}?path=${goalPath}`);
            selectedTestGoalFolderPath = null;
            testGoalDraftContent = selectedTestGoalFile.content || "";
            savedTestGoalContent = selectedTestGoalFile.content || "";
        } catch (testGoalError) {
            reportClientError(`Unable to load Test Goal ${goalFile.path}`, testGoalError);
        }
    }

    async function loadTestGoalFile(goalFile) {
        if (testGoalDirty) {
            openTestGoalGuard(() => loadTestGoalFileNow(goalFile));
            return;
        }

        await loadTestGoalFileNow(goalFile);
    }

    async function selectTestGoalFolder(folderPath) {
        if (testGoalDirty) {
            openTestGoalGuard(() => selectTestGoalFolderNow(folderPath));
            return;
        }

        selectTestGoalFolderNow(folderPath);
    }

    function selectTestGoalFolderNow(folderPath) {
        const nextState = testGoalFolderSelectionState(folderPath);
        selectedTestGoalFolderPath = nextState.selectedTestGoalFolderPath;
        selectedTestGoalFile = nextState.selectedTestGoalFile;
        testGoalDraftContent = nextState.testGoalDraftContent;
        savedTestGoalContent = nextState.savedTestGoalContent;
    }

    function setTestGoalDraftContent(content) {
        testGoalDraftContent = content;
    }

    function discardTestGoalChanges() {
        testGoalDraftContent = savedTestGoalContent;
    }

    async function saveTestGoalFile() {
        if (!selectedTestGoalFile?.path) {
            return false;
        }

        saving = true;
        message = "";

        try {
            const goalPath = encodeURIComponent(selectedTestGoalFile.path);
            selectedTestGoalFile = await loadJson(`${testGoalApiPath(selectedWorkspaceName, "/file")}?path=${goalPath}`, {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({ content: testGoalDraftContent })
            });
            testGoalDraftContent = selectedTestGoalFile.content || "";
            savedTestGoalContent = selectedTestGoalFile.content || "";
            await loadTestGoalTree();
            showTemporaryMessage("Test goal saved.");
            return true;
        } catch (testGoalError) {
            reportClientError(`Unable to save Test Goal ${selectedTestGoalFile.path}`, testGoalError);
            return false;
        } finally {
            saving = false;
        }
    }

    async function createTestGoalFile(path) {
        if (testGoalDirty) {
            openTestGoalGuard(() => createTestGoalFile(path));
            return;
        }

        saving = true;
        message = "";

        try {
            const goalPath = encodeURIComponent(path);
            selectedTestGoalFile = await loadJson(`${testGoalApiPath(selectedWorkspaceName, "/file")}?path=${goalPath}`, {
                method: "POST"
            });
            testGoalDraftContent = selectedTestGoalFile.content || "";
            savedTestGoalContent = selectedTestGoalFile.content || "";
            await loadTestGoalTree();
            selectedTestGoalFolderPath = null;
            showTemporaryMessage("Test goal created.");
        } catch (testGoalError) {
            reportClientError(`Unable to create Test Goal ${path}`, testGoalError);
        } finally {
            saving = false;
        }
    }

    async function createTestGoalFolder(path) {
        saving = true;
        message = "";

        try {
            const goalPath = encodeURIComponent(path);
            testGoalTree = await loadJson(`${testGoalApiPath(selectedWorkspaceName, "/folder")}?path=${goalPath}`, {
                method: "POST"
            });
            showTemporaryMessage("Test goal folder created.");
        } catch (testGoalError) {
            reportClientError(`Unable to create Test Goal folder ${path}`, testGoalError);
        } finally {
            saving = false;
        }
    }

    async function deleteTestGoalPath(path) {
        if (!path) {
            return;
        }

        saving = true;
        message = "";

        try {
            const goalPath = encodeURIComponent(path);
            testGoalTree = await loadJson(`${testGoalApiPath(selectedWorkspaceName)}?path=${goalPath}`, {
                method: "DELETE"
            });
            if (selectedTestGoalFile?.path === path || selectedTestGoalFile?.path?.startsWith(`${path}/`)) {
                selectedTestGoalFile = null;
                selectedTestGoalFolderPath = "";
                testGoalDraftContent = "";
                savedTestGoalContent = "";
            }
            showTemporaryMessage("Test goal item deleted.");
        } catch (testGoalError) {
            reportClientError(`Unable to delete Test Goal item ${path}`, testGoalError);
        } finally {
            saving = false;
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
            title: "Edit Settings",
            saveLabel: "Save Settings",
            dirty: hasSettingsChanges(),
            save: () => saveWorkspaceFile(
                `/api/workspaces/${selectedWorkspaceName}/test-settings`,
                workspaceDocument.testSettings.content
            )
        };
    } else if (selectedEditor === "settings-form") {
        currentEditorDocument = {
            title: "Edit Settings",
            saveLabel: "Save Settings",
            dirty: hasSettingsChanges(),
            save: saveVisualSettings
        };
    } else if (selectedEditor === "policies-properties") {
        currentEditorDocument = {
            title: "Edit policies.properties",
            saveLabel: "Save policies.properties",
            dirty: hasPoliciesPropertiesChanges(),
            save: () => saveWorkspaceFile(
                `/api/workspaces/${selectedWorkspaceName}/policies-properties`,
                workspaceDocument.policiesProperties.content
            )
        };
    } else if (selectedEditor === "composition-properties") {
        currentEditorDocument = {
            title: "Edit composition.properties",
            saveLabel: "Save composition.properties",
            dirty: hasCompositionPropertiesChanges(),
            save: () => saveWorkspaceFile(
                `/api/workspaces/${selectedWorkspaceName}/composition-properties`,
                workspaceDocument.compositionProperties.content
            )
        };
    } else if (selectedEditor !== "java-policies" && selectedEditor !== "java-composition" && selectedSourceFile) {
        currentEditorDocument = {
            title: selectedSourceFile.name,
            saveLabel: "Save source",
            dirty: hasSelectedSourceChanges([selectedSourceFile.category]),
            save: saveSelectedSource
        };
    } else {
        currentEditorDocument = null;
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
            selectedResultFile = await loadJson(resultFileUrl(selectedWorkspaceName, resultFile));
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
            const currentGroupPath = selectedResultGroup?.path || "";
            const refreshedResults = await loadJson(resultFileUrl(selectedWorkspaceName, resultFile), {
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
            const refreshedResults = await loadJson(resultGroupDeleteUrl(selectedWorkspaceName, resultGroup), {
                method: "DELETE"
            });
            applyRefreshedResults(refreshedResults);
        } catch (deleteError) {
            reportClientError(`Unable to delete result output folder ${resultGroup.name}`, deleteError);
        }
    }

    async function startRemoteSpyMode() {
        if (!selectedWorkspaceName) {
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
        if (!selectedWorkspaceName) {
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
        await guardCliAgentSettingsTransition(startCliManualSessionNow);
    }

    async function startCliManualSessionNow() {
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
        await guardCliAgentSettingsTransition(startCliAgentSessionNow);
    }

    async function startCliAgentSessionNow() {
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
        if (!workspaceDocument?.settingsGroups) {
            return false;
        }

        saving = true;
        message = "";

        try {
            const normalizedSettings = normalizedCliAgentSettings(cliAgentSettings);
            setWorkspaceSettingByKey(CLI_AGENT_SETTING_KEYS.apiKeyEnvVarName, normalizedSettings.apiKeyEnvVarName);
            setWorkspaceSettingByKey(CLI_AGENT_SETTING_KEYS.baseUrl, normalizedSettings.baseUrl);
            setWorkspaceSettingByKey(CLI_AGENT_SETTING_KEYS.model, normalizedSettings.model);
            setWorkspaceSettingByKey(CLI_AGENT_SETTING_KEYS.reasoningEffort, normalizedSettings.reasoningEffort);
            setWorkspaceSettingByKey(CLI_AGENT_SETTING_KEYS.sandboxMode, normalizedSettings.sandboxMode);
            setWorkspaceSettingByKey(CLI_AGENT_SETTING_KEYS.approvalPolicy, normalizedSettings.approvalPolicy);
            setWorkspaceSettingByKey(CLI_AGENT_SETTING_KEYS.allowNetworkAccess, normalizedSettings.allowNetworkAccess);
            setWorkspaceSettingByKey(CLI_AGENT_SETTING_KEYS.skipGitRepoCheck, normalizedSettings.skipGitRepoCheck);
            setWorkspaceSettingByKey(CLI_AGENT_SETTING_KEYS.promptTitle, normalizedSettings.promptTitle);
            setWorkspaceSettingByKey(CLI_AGENT_SETTING_KEYS.promptText, normalizedSettings.promptText);
            await saveVisualSettings();
            cliAgentSettings = objectSnapshot(normalizedSettings);
            savedCliAgentSettings = objectSnapshot(normalizedSettings);
            showTemporaryMessage("Agent CLI settings saved.");
            return true;
        } catch (cliError) {
            reportClientError("Unable to save Agent CLI settings", cliError);
            return false;
        } finally {
            saving = false;
        }
    }

    $: selectedWorkspaceSummary = workspaces.find((workspace) => workspace.name === selectedWorkspaceName) || null;
    $: workspaceCreateValidationState = workspaceCreateValidation(workspaceCreateDraft, workspaces);
    $: workspaceRenameValidationState = workspaceRenameValidation(workspaceRenameDraft, selectedWorkspaceName, workspaces);

    onMount(async () => {
        startScriptlessPolling();
        try {
            currentRole = storedWebStudioRole();
            currentPage = pageForRole(currentRole, currentPage);
            await refreshInitialData();
            if (workspaces.length > 0) {
                const defaultWorkspace = workspaces.find((workspace) => workspace.name === "webdriver_generic") || workspaces[0];
                await loadWorkspace(defaultWorkspace.name);
                if (currentPage === "basic-settings") {
                    openBasicSettingsImmediate();
                }
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
            <button type="button" class="secondary page-workspace-action" on:click={openWorkspaceManagementDialog}>
                Workspace
            </button>
            <select
                id="page-workspace-select"
                value={selectedWorkspaceName}
                on:change={(event) => {
                    const nextWorkspaceName = event.currentTarget.value;
                    guardApplicationTransition(async () => {
                        await loadWorkspace(nextWorkspaceName);
                    });
                }}
            >
                {#each workspaces as workspace}
                    <option value={workspace.name}>{workspace.name}</option>
                {/each}
            </select>
        </div>
        <div class="page-nav-role">
            <label for="page-role-select">Role</label>
            <select
                id="page-role-select"
                value={currentRole}
                on:change={(event) => changeWebStudioRole(event.currentTarget.value)}
            >
                <option value={WEB_STUDIO_ROLES.BASIC}>Basic</option>
                <option value={WEB_STUDIO_ROLES.ADVANCED}>Advanced</option>
            </select>
        </div>
        <div class="page-nav-menu">
            <button
                class:secondary={!menuHasActivePage(testConfigurationMenuItems(currentRole), currentPage)}
                type="button"
                on:click={() => toggleNavMenu("configure")}
            >
                ⚙️ Test Configuration ▼
            </button>
            {#if activeNavMenu === "configure"}
                <div class="page-nav-dropdown">
                    {#each testConfigurationMenuItems(currentRole) as item}
                        <button type="button" class:secondary={item.id !== currentPage} disabled={item.disabled} on:click={() => navigateFromMenu(item)}>
                            {item.label}
                        </button>
                    {/each}
                </div>
            {/if}
        </div>
        <button class:secondary={currentPage !== "oracles"} on:click={navigateToTestOracles}>
            🔮 Test Oracles
        </button>
        <button class:secondary={currentPage !== "test-goals"} on:click={navigateToTestGoals}>
            🎯 Test Goals
        </button>
        <button class:secondary={currentPage !== "spy"} on:click={navigateToSpy}>
            🔍 Spy Mode
        </button>
        <div class="page-nav-menu">
            <button
                class:secondary={!menuHasActivePage(runModeMenuItems(), currentPage)}
                type="button"
                on:click={() => toggleNavMenu("run")}
            >
                🔄 Run Modes ▼
            </button>
            {#if activeNavMenu === "run"}
                <div class="page-nav-dropdown">
                    {#each runModeMenuItems() as item}
                        <button type="button" class:secondary={item.id !== currentPage} disabled={item.disabled} on:click={() => navigateFromMenu(item)}>
                            {item.label}
                        </button>
                    {/each}
                </div>
            {/if}
        </div>
        <div class="page-nav-menu">
            <button
                class:secondary={!menuHasActivePage(resultMenuItems(currentRole), currentPage)}
                type="button"
                on:click={() => toggleNavMenu("results")}
            >
                👁️ View Results ▼
            </button>
            {#if activeNavMenu === "results"}
                <div class="page-nav-dropdown">
                    {#each resultMenuItems(currentRole) as item}
                        <button type="button" class:secondary={item.id !== currentPage} disabled={item.disabled} on:click={() => navigateFromMenu(item)}>
                            {item.label}
                        </button>
                    {/each}
                </div>
            {/if}
        </div>
    </nav>

    {#if currentPage === "basic-settings"}
        <BasicSettingsView
            currentEditorDocument={currentEditorDocument}
            loading={loading}
            openTestSettings={openTestSettings}
            openVisualSettings={openVisualSettings}
            openVisualSettingsGroup={openVisualSettingsGroup}
            regexValidationResults={regexValidationResults}
            savedTestSettingsContent={savedTestSettingsContent}
            saving={saving}
            setSettingValue={setSettingValue}
            selectedEditor={selectedEditor}
            selectedSettingsGroupId={selectedSettingsGroupId}
            restoreSettingDefault={restoreSettingDefault}
            validateRegexExpression={validateRegexExpression}
            workspaceDocument={workspaceDocument}
        />
    {/if}

    {#if currentPage === "oracles"}
        <TestOraclesView
            compositionFlowNodes={compositionFlowNodes}
            currentEditorDocument={currentEditorDocument}
            compileSelectedJavaSource={compileSelectedJavaSource}
            compileWorkspaceProfile={compileWorkspaceProfile}
            createCompositionModuleSource={createCompositionModuleSource}
            javaCompileResult={javaCompileResult}
            closeCompositionSourceEditor={closeCompositionSourceEditor}
            loading={loading}
            openOraclePanel={openOraclePanel}
            openTestSettings={openTestSettings}
            openVisualSettings={openVisualSettings}
            openVisualSettingsGroup={openVisualSettingsGroup}
            regexValidationResults={regexValidationResults}
            savedTestSettingsContent={savedTestSettingsContent}
            saving={saving}
            selectedOraclePanelId={selectedOraclePanelId}
            setSettingValue={setSettingValue}
            selectCompositionFlowNode={selectCompositionFlowNode}
            selectedCompositionFlowNode={selectedCompositionFlowNode}
            selectedEditor={selectedEditor}
            selectedSettingsGroupId={selectedSettingsGroupId}
            selectedSourceFile={selectedSourceFile}
            selectedSourceSavedContent={selectedSourceFile?.name ? savedSourceContents[selectedSourceFile.name] || "" : ""}
            restoreSettingDefault={restoreSettingDefault}
            validateRegexExpression={validateRegexExpression}
            workspaceDocument={workspaceDocument}
        />
    {/if}

    {#if currentPage === "settings"}
        <TestSettingsPageView
            currentEditorDocument={currentEditorDocument}
            loading={loading}
            openTestSettings={openTestSettings}
            openVisualSettings={openVisualSettings}
            openVisualSettingsGroup={openVisualSettingsGroup}
            regexValidationResults={regexValidationResults}
            savedTestSettingsContent={savedTestSettingsContent}
            saving={saving}
            setSettingValue={setSettingValue}
            selectedEditor={selectedEditor}
            selectedSettingsGroupId={selectedSettingsGroupId}
            restoreSettingDefault={restoreSettingDefault}
            validateRegexExpression={validateRegexExpression}
            workspaceDocument={workspaceDocument}
        />
    {/if}

    {#if currentPage === "composition"}
        <TestCompositionPageView
            compositionFlowNodes={compositionFlowNodes}
            currentEditorDocument={currentEditorDocument}
            isSelectedEditor={isSelectedEditor}
            openCompositionProperties={openCompositionProperties}
            openJavaComposition={openJavaComposition}
            compileSelectedJavaSource={compileSelectedJavaSource}
            compileWorkspaceProfile={compileWorkspaceProfile}
            createCompositionModuleSource={createCompositionModuleSource}
            javaCompileResult={javaCompileResult}
            closeCompositionSourceEditor={closeCompositionSourceEditor}
            saving={saving}
            selectCompositionFlowNode={selectCompositionFlowNode}
            selectedCompositionFlowNode={selectedCompositionFlowNode}
            selectedEditor={selectedEditor}
            selectedSourceFile={selectedSourceFile}
            selectedSourceSavedContent={selectedSourceFile?.name ? savedSourceContents[selectedSourceFile.name] || "" : ""}
            savedCompositionPropertiesContent={savedCompositionPropertiesContent}
            workspaceDocument={workspaceDocument}
        />
    {/if}

    {#if currentPage === "policies"}
        <TestPoliciesPageView
            activePolicySourceFiles={activePolicySourceFiles}
            currentEditorDocument={currentEditorDocument}
            inactivePolicySourceFiles={inactivePolicySourceFiles}
            isPolicySourceSelected={isPolicySourceSelected}
            isSelectedEditor={isSelectedEditor}
            openJavaPolicies={openJavaPolicies}
            openPoliciesProperties={openPoliciesProperties}
            policySourceFiles={policySourceFiles}
            closePolicySourceEditor={closePolicySourceEditor}
            compileSelectedJavaSource={compileSelectedJavaSource}
            compileWorkspaceProfile={compileWorkspaceProfile}
            createPolicySource={createPolicySource}
            javaCompileResult={javaCompileResult}
            savedPoliciesPropertiesContent={savedPoliciesPropertiesContent}
            saving={saving}
            selectSource={selectSource}
            selectedEditor={selectedEditor}
            selectedSourceFile={selectedSourceFile}
            selectedSourceSavedContent={selectedSourceFile?.name ? savedSourceContents[selectedSourceFile.name] || "" : ""}
            togglePolicySourceActivation={togglePolicySourceActivation}
            workspaceDocument={workspaceDocument}
        />
    {/if}

    {#if currentPage === "run"}
        <RunTestarView
            saving={saving}
            scriptlessStatus={scriptlessStatus}
            selectedWorkspaceName={selectedWorkspaceName}
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
                savedCliAgentSettings={savedCliAgentSettings}
                saving={saving}
                saveCliAgentSettings={saveCliAgentSettings}
                selectedWorkspaceSutConnector={selectedWorkspaceSutConnector}
                selectedWorkspaceSutConnectorValue={selectedWorkspaceSutConnectorValue}
                selectedWorkspaceCliStateProjectionMode={selectedWorkspaceCliStateProjectionMode}
                selectedWorkspaceAvailableInCli={Boolean(selectedWorkspaceName)}
                startCliAgentSession={startCliAgentSession}
                startCliManualSession={startCliManualSession}
                runCliManualCommand={runCliManualCommand}
                stopCliAgentSession={stopCliAgentSession}
                stopCliManualSession={stopCliManualSession}
            />
        {/key}
    {/if}

    {#if currentPage === "test-goals"}
        <TestGoalsView
            testGoalDraftContent={testGoalDraftContent}
            testGoalDirty={testGoalDirty}
            testGoalTree={testGoalTree}
            createTestGoalFile={createTestGoalFile}
            createTestGoalFolder={createTestGoalFolder}
            deleteTestGoalPath={deleteTestGoalPath}
            discardTestGoalChanges={discardTestGoalChanges}
            loadTestGoalFile={loadTestGoalFile}
            saveTestGoalFile={saveTestGoalFile}
            saving={saving}
            selectedWorkspaceName={selectedWorkspaceName}
            selectedTestGoalFolderPath={selectedTestGoalFolderPath}
            selectedTestGoalFile={selectedTestGoalFile}
            selectTestGoalFolder={selectTestGoalFolder}
            setTestGoalDraftContent={setTestGoalDraftContent}
        />
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

    {#if workspaceManagementDialogOpen}
        <div class="composition-modal-backdrop" role="presentation" on:click={closeWorkspaceManagementDialogFromBackdrop}>
            <div
                class="composition-modal state-model-dialog workspace-management-dialog"
                role="dialog"
                aria-modal="true"
                aria-labelledby="workspace-management-dialog-title"
            >
                <div class="composition-modal-header">
                    <div>
                        <h2 id="workspace-management-dialog-title">Manage Workspace</h2>
                        <p>Create a workspace from an existing base, or rename the selected workspace.</p>
                    </div>
                </div>
                <div class="workspace-management-tabs" role="tablist" aria-label="Workspace management actions">
                    <button
                        type="button"
                        class:secondary={workspaceManagementTab !== "create"}
                        on:click={() => {
                            workspaceManagementTab = "create";
                            workspaceManagementError = "";
                        }}
                        disabled={saving}
                    >
                        Create Workspace
                    </button>
                    <button
                        type="button"
                        class:secondary={workspaceManagementTab !== "rename"}
                        on:click={() => {
                            workspaceManagementTab = "rename";
                            workspaceManagementError = "";
                        }}
                        disabled={saving || !selectedWorkspaceName}
                    >
                        Rename Workspace
                    </button>
                </div>
                <div class="composition-modal-body workspace-management-form">
                    {#if workspaceManagementTab === "create"}
                        <label>
                            <span>Workspace name</span>
                            <input
                                type="text"
                                bind:value={workspaceCreateDraft.name}
                                placeholder="platform_application (e.g., webdriver_parabank)"
                                disabled={saving}
                            />
                        </label>
                        <label>
                            <span>Base workspace</span>
                            <select bind:value={workspaceCreateDraft.baseWorkspace} disabled={saving}>
                                {#each workspaces as workspace}
                                    <option value={workspace.name}>{workspace.name}</option>
                                {/each}
                            </select>
                        </label>
                        <label class="workspace-management-checkbox">
                            <input
                                type="checkbox"
                                bind:checked={workspaceCreateDraft.copyTestGoals}
                                disabled={saving}
                            />
                            <span>Copy Test Goals from base workspace</span>
                        </label>
                        {#if workspaceManagementError || workspaceCreateValidationState.message}
                            <p class:settings-validation-invalid={workspaceManagementError || !workspaceCreateValidationState.valid}>
                                {workspaceManagementError || workspaceCreateValidationState.message}
                            </p>
                        {/if}
                    {:else}
                        <label>
                            <span>Current workspace</span>
                            <input type="text" value={selectedWorkspaceName} disabled />
                        </label>
                        <label>
                            <span>New workspace name</span>
                            <input
                                type="text"
                                bind:value={workspaceRenameDraft.name}
                                placeholder="platform_application (e.g., webdriver_parabank)"
                                disabled={saving}
                            />
                        </label>
                        <p class="workspace-management-note">
                            Existing output results for {selectedWorkspaceName} will move to the renamed workspace.
                        </p>
                        {#if workspaceManagementError || workspaceRenameValidationState.message}
                            <p class:settings-validation-invalid={workspaceManagementError || !workspaceRenameValidationState.valid}>
                                {workspaceManagementError || workspaceRenameValidationState.message}
                            </p>
                        {/if}
                    {/if}
                </div>
                <div class="composition-modal-actions">
                    {#if workspaceManagementTab === "create"}
                        <button
                            type="button"
                            on:click={createWorkspaceFromDialog}
                            disabled={saving || !workspaceCreateValidationState.valid}
                        >
                            Create
                        </button>
                    {:else}
                        <button
                            type="button"
                            on:click={renameWorkspaceFromDialog}
                            disabled={saving || !workspaceRenameValidationState.valid}
                        >
                            Rename
                        </button>
                    {/if}
                    <button type="button" class="secondary" on:click={closeWorkspaceManagementDialog} disabled={saving}>
                        Discard
                    </button>
                </div>
            </div>
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
