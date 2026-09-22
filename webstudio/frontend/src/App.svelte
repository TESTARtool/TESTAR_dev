<script>
    import { onDestroy, onMount } from "svelte";
    import AppOverlays from "./app/AppOverlays.svelte";
    import TopNavigation from "./app/TopNavigation.svelte";
    import BasicSettingsView from "./views/settings/BasicSettingsView.svelte";
    import TestCompositionPageView from "./views/composition/TestCompositionPageView.svelte";
    import TestPoliciesPageView from "./views/policies/TestPoliciesPageView.svelte";
    import TestSettingsPageView from "./views/settings/TestSettingsPageView.svelte";
    import RuntimePages from "./views/runtime/RuntimePages.svelte";
    import {
        RUNTIME_ACTIONS,
        RUNTIME_FEEDBACK_ACTIONS,
        runtimeActionFeedbackMessage,
        runtimePageForAction,
        runtimeStatusReturnedError
    } from "./views/runtime/runtimeModel.js";
    import {
        executeSpyActionRequest,
        executeSpyWidgetDefaultActionRequest,
        executeSpyWidgetDirectTypeRequest,
        loadCliStatusRequest,
        loadRemoteSpyStatusRequest,
        loadScriptlessStatusRequest,
        refreshRemoteSpyRequest,
        runCliManualCommandRequest,
        startCliAgentSessionRequest,
        startCliManualSessionRequest,
        startGenerateRequest,
        startLocalSpyRequest,
        startRemoteSpyRequest,
        stopCliAgentSessionRequest,
        stopCliManualSessionRequest,
        stopRemoteSpyRequest,
        stopScriptlessRequest
    } from "./views/runtime/runtimeApi.js";
    import TestResultsView from "./views/results/TestResultsView.svelte";
    import InspectLogsView from "./views/debug/InspectLogsView.svelte";
    import TestGoalsView from "./views/goals/TestGoalsView.svelte";
    import TestOraclesView from "./views/oracles/TestOraclesView.svelte";
    import {
        compileOracleJavaFileRequest,
        createOracleDslFileRequest,
        createOracleJavaFileRequest,
        deleteOracleDslFileRequest,
        deleteOracleJavaFileRequest,
        generateJavaFromOracleDslFileRequest,
        loadDslOracleMetadataRequest,
        loadOracleDslFileRequest,
        loadOracleJavaFileRequest,
        loadTestOracleInventoryRequest,
        saveOracleJavaFileRequest
    } from "./views/oracles/testOraclesApi.js";
    import {
        saveTestSettingsRequest,
        validateRegexRequest
    } from "./views/settings/settingsApi.js";
    import {
        buildTestSettingsContent,
        canRestoreSettingDefault,
        restoredSettingDefaultValue,
        updatedRegexValidationResults
    } from "./views/settings/settingsEditorModel.js";
    import {
        loadDebugFileRequest,
        loadDebugFilesRequest
    } from "./views/debug/debugFilesApi.js";
    import {
        createTestGoalFileRequest,
        createTestGoalFolderRequest,
        deleteTestGoalPathRequest,
        loadTestGoalFileRequest,
        loadTestGoalTreeRequest,
        saveTestGoalFileRequest
    } from "./views/goals/testGoalsApi.js";
    import {
        clearedTestGoalSelectionState,
        loadedTestGoalFileState,
        shouldClearTestGoalSelectionAfterDelete,
        testGoalFolderSelectionState
    } from "./views/goals/testGoalsModel.js";
    import {
        TEST_ORACLE_PANEL_IDS,
        clearedOracleSourceState,
        extendedOracleCheckboxItems,
        extendedOracleItemsWithEnablement,
        extendedOracleSettingValue,
        loadedOracleSourceState,
        oracleDslGenerationErrorResult,
        oracleJavaCompileErrorResult,
        oracleSettingsGroupId,
        shouldClearOracleSourceAfterDelete
    } from "./views/oracles/testOraclesModel.js";
    import { refreshedResultSelectionState } from "./views/results/testResultsViewModel.js";
    import {
        cliAgentSettingsGuardDetails,
        configurationDirtyAreas as buildConfigurationDirtyAreas,
        guardDialogDetails,
        guardSaveTarget,
        initialPendingGuardState,
        pendingGuardState,
        settingsEditorSelected,
        shouldGuardConfigurationTransition,
        testGoalGuardDetails
    } from "./app/configurationGuard.js";
    import {
        clearedSourceSelectionState,
        currentEditorDocumentDescriptor,
        currentEditorDocumentState,
        openedEditorSelectionState,
        selectedAllowedSettingsGroupId,
        selectedSettingsGroupForEditor
    } from "./models/editorSelectionModel.js";
    import {
        createCompositionModuleSourceRequest,
        createPolicySourceRequest,
        compileWorkspaceProfileRequest,
        compileWorkspaceSourceRequest,
        loadWorkspaceDocumentRequest,
        loadWorkspaceSourceRequest,
        saveWorkspaceCompositionPropertiesRequest,
        saveWorkspacePoliciesPropertiesRequest,
        saveWorkspaceSourceRequest
    } from "./models/sourceEditorApi.js";
    import {
        buildCompositionFlowNodes,
        compositionSourceFilesFromWorkspace,
        policySourceFilesFromWorkspace,
        refreshedSelectedCompositionFlowNode
    } from "./views/composition/compositionFlowModel.js";
    import {
        inferPolicyPropertyKey,
        sourceClassName,
        sourceFilesForReferencedClasses,
        sourceFilesNotInReferenceSet,
        updatedPolicyPropertiesContent
    } from "./models/sourceEditorModel.js";
    import {
        STATE_MODEL_DEFAULT_URL,
        stateModelDialogFromStatus,
        stateModelDialogMessage,
        stateModelExternalUrl,
        stateModelRequestedMessage,
        stateModelShouldOpenExternalTab,
        stateModelShouldPollStatus,
        stateModelStoppedMessage,
        stateModelWorkspaceDialog
    } from "./app/stateModelNavigation.js";
    import {
        loadStateModelStatusRequest,
        openStateModelRequest,
        stopStateModelRequest
    } from "./app/stateModelApi.js";
    import { objectSnapshot, settingsChanged } from "./models/editorDirtyState.js";
    import {
        deleteResultFileRequest,
        deleteResultGroupRequest,
        loadResultFileRequest,
        loadResultListRequest
    } from "./api/resultApi.js";
    import {
        defaultWorkspaceCreateDraft,
        defaultWorkspaceRenameDraft,
        workspaceCreateValidation,
        workspaceRenameValidation
    } from "./models/workspaceManagementModel.js";
    import {
        createWorkspaceRequest,
        renameWorkspaceRequest
    } from "./models/workspaceManagementApi.js";
    import {
        CLI_AGENT_SETTING_KEYS,
        DEFAULT_CLI_AGENT_SETTINGS,
        cliAgentSettingsFromWorkspace,
        clearedWorkspaceDocumentState,
        emptyWorkspaceState,
        findWorkspaceSetting,
        normalizeSettingDisplayValue,
        normalizedCliAgentSettings,
        parsePropertiesContent,
        preferredDefaultWorkspace,
        updatedSavedSourceContents,
        validSettingsGroupId,
        workspaceDocumentBaseline,
        workspaceDocumentWithSettingsContentValues,
        workspaceSummaryForName
    } from "./models/workspaceSettingsModel.js";
    import {
        BASIC_ROLE_SETTINGS_GROUP_IDS,
        TEST_SETTINGS_GROUP_IDS,
        WEB_STUDIO_ROLES,
        normalizeWebStudioRole,
        pageForRole,
        storeWebStudioRole as storeWebStudioRoleInStorage,
        storedWebStudioRole as storedWebStudioRoleFromStorage,
        workspaceManagementLandingPageForRole
    } from "./app/webStudioRoles.js";
    import {
        NAVIGATION_ACTIONS,
        navigationActionForMenuItem,
        toggledNavMenu
    } from "./app/webStudioNavigation.js";

    let workspaces = [];
    let selectedWorkspaceName = "";
    let selectedWorkspaceSummary = null;
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
    let testOracleInventory = null;
    let testOracleInventoryLoading = false;
    let dslOracleMetadata = null;
    let selectedOracleSourceFile = null;
    let oracleSourceDraftContent = "";
    let savedOracleSourceContent = "";
    let oracleDslResult = null;
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
        message: "",
        status: "STOPPED",
        url: STATE_MODEL_DEFAULT_URL,
        running: false
    };
    let stateModelStatusTimer = null;
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
    let pendingGuard = initialPendingGuardState();

    $: testGoalDirty = selectedTestGoalFile !== null
        && testGoalDraftContent !== savedTestGoalContent;
    $: oracleSourceDirty = selectedOracleSourceFile !== null
        && oracleSourceDraftContent !== savedOracleSourceContent;
    $: settingsDirty = settingsChanged(
        workspaceDocument?.testSettings?.content,
        savedTestSettingsContent,
        visualSettingsDirty
    );

    // Shared feedback helpers keep API errors and temporary user messages consistent.
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

    // State model analysis is started by navigation, but its dialog/status lifecycle is owned here.
    function openStateModelDialog(title, dialogMessage, stateModelStatus = {}) {
        stateModelDialog = {
            open: true,
            title,
            message: dialogMessage,
            status: stateModelStatus.status || "STOPPED",
            url: stateModelStatus.url || STATE_MODEL_DEFAULT_URL,
            running: Boolean(stateModelStatus.running)
        };
    }

    function closeStateModelDialog() {
        stopStateModelStatusPolling();
        stateModelDialog = {
            open: false,
            title: "",
            message: "",
            status: "STOPPED",
            url: STATE_MODEL_DEFAULT_URL,
            running: false
        };
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
        pendingGuard = initialPendingGuardState();
    }

    function showStateModelStatus(statusResponse) {
        const dialog = stateModelDialogFromStatus(statusResponse);
        openStateModelDialog(
            dialog.title,
            dialog.message,
            dialog.status
        );
    }

    function stopStateModelStatusPolling() {
        if (stateModelStatusTimer !== null) {
            window.clearTimeout(stateModelStatusTimer);
            stateModelStatusTimer = null;
        }
    }

    function scheduleStateModelStatusPolling() {
        stopStateModelStatusPolling();
        stateModelStatusTimer = window.setTimeout(refreshStateModelStatus, 2000);
    }

    async function refreshStateModelStatus() {
        try {
            const statusResponse = await loadStateModelStatusRequest(loadJson);
            showStateModelStatus(statusResponse);
            if (stateModelShouldPollStatus(statusResponse)) {
                scheduleStateModelStatusPolling();
            }
        } catch (statusError) {
            reportClientError("Unable to refresh state model analysis status", statusError);
        }
    }

    async function stopStateModelAnalysis() {
        saving = true;
        try {
            const statusResponse = await stopStateModelRequest(loadJson);
            showStateModelStatus(statusResponse);
            showTemporaryMessage(stateModelStoppedMessage(statusResponse));
        } catch (stopError) {
            reportClientError("Unable to stop state model analysis", stopError);
            openStateModelDialog(
                "Unable To Stop State Model",
                stopError.message || "State model analysis could not be stopped."
            );
        } finally {
            saving = false;
        }
    }

    // Central JSON loader normalizes backend error responses before view-specific handlers use them.
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

    // Initial data and workspace loading reset dependent view state before applying fresh backend data.
    async function refreshInitialData() {
        const [workspaceResponse, cliStatusResponse, scriptlessResponse, spyResponse] = await Promise.all([
            loadJson("/api/workspaces"),
            loadCliStatusRequest(loadJson),
            loadScriptlessStatusRequest(loadJson),
            loadRemoteSpyStatusRequest(loadJson)
        ]);

        workspaces = workspaceResponse;
        cliStatus = cliStatusResponse;
        scriptlessStatus = scriptlessResponse;
        spyState = spyResponse;
    }

    // Implements WS-FUNC-WORKSPACE-SOURCE-EDITOR-001: load workspace documents and reset editor state for the selected workspace.
    async function loadWorkspace(workspaceName) {
        if (!workspaceName) {
            const emptyState = emptyWorkspaceState();
            selectedWorkspaceSummary = emptyState.selectedWorkspaceSummary;
            selectedWorkspaceName = emptyState.selectedWorkspaceName;
            workspaceDocument = emptyState.workspaceDocument;
            selectedWorkspaceSutConnector = emptyState.selectedWorkspaceSutConnector;
            selectedWorkspaceSutConnectorValue = emptyState.selectedWorkspaceSutConnectorValue;
            selectedWorkspaceCliStateProjectionMode = emptyState.selectedWorkspaceCliStateProjectionMode;
            cliAgentSettings = emptyState.cliAgentSettings;
            savedCliAgentSettings = emptyState.savedCliAgentSettings;
            savedTestSettingsContent = emptyState.savedTestSettingsContent;
            savedCompositionPropertiesContent = emptyState.savedCompositionPropertiesContent;
            savedPoliciesPropertiesContent = emptyState.savedPoliciesPropertiesContent;
            savedSourceContents = emptyState.savedSourceContents;
            selectedSettingsGroupId = emptyState.selectedSettingsGroupId;
            visualSettingsDirty = false;
            selectedSourceName = "";
            selectedSourceFile = null;
            selectedEditor = "java-composition";
            selectedCompositionFlowNode = null;
            regexValidationResults = {};
            javaCompileResult = null;
            resetTestGoalSelection();
            resetOracleSourceSelection();
            testOracleInventory = null;
            testOracleInventoryLoading = false;
            testGoalTree = null;
            resultsData = null;
            selectedResultGroup = null;
            selectedResultFile = null;
            return;
        }

        loading = true;
        message = "";
        selectedWorkspaceName = workspaceName;
        const clearedDocumentState = clearedWorkspaceDocumentState();
        workspaceDocument = clearedDocumentState.workspaceDocument;
        selectedWorkspaceSutConnector = clearedDocumentState.selectedWorkspaceSutConnector;
        selectedWorkspaceSutConnectorValue = clearedDocumentState.selectedWorkspaceSutConnectorValue;
        selectedWorkspaceCliStateProjectionMode = clearedDocumentState.selectedWorkspaceCliStateProjectionMode;
        cliAgentSettings = clearedDocumentState.cliAgentSettings;
        savedCliAgentSettings = clearedDocumentState.savedCliAgentSettings;
        savedTestSettingsContent = clearedDocumentState.savedTestSettingsContent;
        savedCompositionPropertiesContent = clearedDocumentState.savedCompositionPropertiesContent;
        savedPoliciesPropertiesContent = clearedDocumentState.savedPoliciesPropertiesContent;
        savedSourceContents = clearedDocumentState.savedSourceContents;
        selectedSettingsGroupId = clearedDocumentState.selectedSettingsGroupId;
        visualSettingsDirty = false;
        selectedSourceName = "";
        selectedSourceFile = null;
        selectedEditor = "java-composition";
        selectedCompositionFlowNode = null;
        regexValidationResults = {};
        javaCompileResult = null;
        resetTestGoalSelection();
        resetOracleSourceSelection();
        testOracleInventory = null;
        testOracleInventoryLoading = false;
        testGoalTree = null;
        resultsData = null;
        selectedResultGroup = null;
        selectedResultFile = null;

        try {
            workspaceDocument = await loadWorkspaceDocumentRequest(loadJson, workspaceName);
            const documentBaseline = workspaceDocumentBaseline(workspaceDocument);
            savedTestSettingsContent = documentBaseline.savedTestSettingsContent;
            savedCompositionPropertiesContent = documentBaseline.savedCompositionPropertiesContent;
            savedPoliciesPropertiesContent = documentBaseline.savedPoliciesPropertiesContent;
            savedSourceContents = {};
            visualSettingsDirty = false;
            selectedWorkspaceSutConnector = documentBaseline.selectedWorkspaceSutConnector;
            selectedWorkspaceSutConnectorValue = documentBaseline.selectedWorkspaceSutConnectorValue;
            selectedWorkspaceCliStateProjectionMode = documentBaseline.selectedWorkspaceCliStateProjectionMode;
            cliAgentSettings = documentBaseline.cliAgentSettings;
            savedCliAgentSettings = documentBaseline.savedCliAgentSettings;
            selectedSourceName = "";
            selectedSourceFile = null;
            selectedEditor = "java-composition";
            selectedSettingsGroupId = documentBaseline.selectedSettingsGroupId;
            selectedCompositionFlowNode = null;
            regexValidationResults = {};
            javaCompileResult = null;
            resetTestGoalSelection();
            resetOracleSourceSelection();
            testOracleInventory = null;
            if (currentPage === "test-goals") {
                await loadTestGoalTree(workspaceName);
            } else if (currentPage === "oracles") {
                await loadTestOracleInventory(workspaceName);
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

    // Implements WS-UX-SOURCE-EDITOR-001: centralize source selection so editor views clear stale state consistently.
    async function selectSource(sourceName, editorId = null) {
        if (!selectedWorkspaceName || !sourceName) {
            selectedSourceName = "";
            selectedSourceFile = null;
            return;
        }

        selectedSourceName = sourceName;
        selectedSourceFile = await loadWorkspaceSourceRequest(loadJson, selectedWorkspaceName, sourceName);
        savedSourceContents = updatedSavedSourceContents(savedSourceContents, sourceName, selectedSourceFile);
        selectedEditor = editorId || `source:${sourceName}`;
        javaCompileResult = null;
    }

    function clearSelectedSource() {
        const nextState = clearedSourceSelectionState({
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
        const nextState = openedEditorSelectionState({
            selectedSourceName,
            selectedSourceFile,
            selectedEditor,
            selectedCompositionFlowNode,
            javaCompileResult
        }, editorId);

        selectedSourceName = nextState.selectedSourceName;
        selectedSourceFile = nextState.selectedSourceFile;
        selectedEditor = nextState.selectedEditor;
        selectedCompositionFlowNode = nextState.selectedCompositionFlowNode;
        javaCompileResult = nextState.javaCompileResult;
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
        if (selectedEditor === "settings-form") {
            return;
        }

        await guardConfigurationTransition(async () => {
            workspaceDocument = workspaceDocumentWithSettingsContentValues(workspaceDocument);
            openEditorImmediate("settings-form");
            selectedSettingsGroupId = selectedSettingsGroupForEditor(workspaceDocument, "", selectedSettingsGroupId);
        }, "settings-form");
    }

    async function openVisualSettingsGroup(groupId) {
        await guardConfigurationTransition(async () => {
            workspaceDocument = workspaceDocumentWithSettingsContentValues(workspaceDocument);
            openEditorImmediate("settings-form");
            selectedSettingsGroupId = selectedSettingsGroupForEditor(workspaceDocument, groupId, selectedSettingsGroupId);
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

        workspaceDocument = await loadWorkspaceDocumentRequest(loadJson, selectedWorkspaceName);
        const documentBaseline = workspaceDocumentBaseline(workspaceDocument);
        savedTestSettingsContent = documentBaseline.savedTestSettingsContent;
        savedCompositionPropertiesContent = documentBaseline.savedCompositionPropertiesContent;
        savedPoliciesPropertiesContent = documentBaseline.savedPoliciesPropertiesContent;
        visualSettingsDirty = false;
        selectedWorkspaceSutConnector = documentBaseline.selectedWorkspaceSutConnector;
        selectedWorkspaceSutConnectorValue = documentBaseline.selectedWorkspaceSutConnectorValue;
        selectedWorkspaceCliStateProjectionMode = documentBaseline.selectedWorkspaceCliStateProjectionMode;
        cliAgentSettings = documentBaseline.cliAgentSettings;
        savedCliAgentSettings = documentBaseline.savedCliAgentSettings;
    }

    async function restoreEditorState(editorId, sourceName) {
        selectedEditor = editorId;
        selectedCompositionFlowNode = refreshedSelectedCompositionFlowNode(compositionFlowNodes, selectedCompositionFlowNode);
        selectedSettingsGroupId = validSettingsGroupId(workspaceDocument, selectedSettingsGroupId);
        if (sourceName) {
            await selectSource(sourceName, editorId);
        }
    }

    // Implements WS-FUNC-COMPOSITION-FLOW-001 and WS-FUNC-POLICIES-001:
    // saves composition/policies properties and compiles Java configuration sources.
    // Workspace document persistence also covers raw settings, visual settings, policies, and profile compilation.
    async function saveWorkspaceFile(fileKind, content) {
        saving = true;
        message = "";
        const activeEditorId = selectedEditor;
        const activeSourceName = selectedSourceName;

        try {
            if (fileKind === "composition-properties") {
                await saveWorkspaceCompositionPropertiesRequest(loadJson, selectedWorkspaceName, content);
            } else if (fileKind === "policies-properties") {
                await saveWorkspacePoliciesPropertiesRequest(loadJson, selectedWorkspaceName, content);
            } else {
                throw new Error(`Unsupported workspace file kind: ${fileKind}`);
            }

            await refreshWorkspaceDocument();
            await restoreEditorState(activeEditorId, activeSourceName);
            await refreshScriptlessStatus();
            savedTestSettingsContent = workspaceDocument?.testSettings?.content || "";
            visualSettingsDirty = false;
            showTemporaryMessage("Workspace file saved.");
        } catch (saveError) {
            reportClientError(`Unable to save ${fileKind}`, saveError);
        } finally {
            saving = false;
        }
    }

    async function saveSelectedSource() {
        if (!selectedWorkspaceName || !selectedSourceFile) {
            return;
        }

        saving = true;
        message = "";
        const activeEditorId = selectedEditor;
        const activeSourceName = selectedSourceName;

        try {
            await saveWorkspaceSourceRequest(loadJson, selectedWorkspaceName, selectedSourceFile.name, selectedSourceFile.content);
            await refreshWorkspaceDocument();
            await restoreEditorState(activeEditorId, activeSourceName);
            await refreshScriptlessStatus();
            showTemporaryMessage("Workspace file saved.");
        } catch (saveError) {
            reportClientError(`Unable to save source ${selectedSourceFile.name}`, saveError);
        } finally {
            saving = false;
        }
    }

    async function persistSelectedSourceForCompile() {
        if (!selectedWorkspaceName || !selectedSourceFile?.name) {
            return;
        }

        const activeEditorId = selectedEditor;
        const activeSourceName = selectedSourceName;

        await saveWorkspaceSourceRequest(loadJson, selectedWorkspaceName, selectedSourceFile.name, selectedSourceFile.content);

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
            javaCompileResult = await compileWorkspaceSourceRequest(loadJson, selectedWorkspaceName, selectedSourceFile.name);
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
            javaCompileResult = await compileWorkspaceProfileRequest(loadJson, selectedWorkspaceName);
            return javaCompileResult;
        } catch (compileError) {
            reportClientError(`Unable to compile workspace profile ${selectedWorkspaceName}`, compileError);
            return null;
        } finally {
            saving = false;
        }
    }

    function touchWorkspaceDocument() {
        workspaceDocument = workspaceDocument
            ? {
                ...workspaceDocument,
                settingsGroups: [...(workspaceDocument.settingsGroups || [])]
            }
            : workspaceDocument;
    }

    async function saveVisualSettings() {
        if (!workspaceDocument?.settingsGroups) {
            return false;
        }

        saving = true;
        message = "";

        try {
            const nextContent = buildTestSettingsContent(
                workspaceDocument.testSettings.content,
                workspaceDocument.settingsGroups
            );

            workspaceDocument.testSettings.content = nextContent;
            await saveTestSettingsRequest(loadJson, selectedWorkspaceName, nextContent);
            savedTestSettingsContent = nextContent;
            visualSettingsDirty = false;
            touchWorkspaceDocument();
            showTemporaryMessage("Settings saved.");
            return true;
        } catch (saveError) {
            reportClientError("Unable to save settings", saveError);
            return false;
        } finally {
            saving = false;
        }
    }

    async function validateRegexExpression(setting) {
        if (!setting?.key) {
            return;
        }

        touchWorkspaceDocument();

        try {
            const validationResult = await validateRegexRequest(loadJson, setting.value);
            regexValidationResults = updatedRegexValidationResults(regexValidationResults, setting.key, validationResult);
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

        if (!canRestoreSettingDefault(setting)) {
            return;
        }

        setting.value = restoredSettingDefaultValue(setting);
        setting.regexValidation = null;
        visualSettingsDirty = true;
        touchWorkspaceDocument();
        regexValidationResults = updatedRegexValidationResults(regexValidationResults, setting.key, null);
    }

    // Generate mode and shared runtime polling keep the status panels synchronized with backend execution.
    async function startGenerate() {
        if (!selectedWorkspaceName) {
            return;
        }

        saving = true;
        message = "";
        currentPage = runtimePageForAction(RUNTIME_ACTIONS.GENERATE);

        try {
            scriptlessStatus = await startGenerateRequest(loadJson, selectedWorkspaceName);
            if (runtimeStatusReturnedError(scriptlessStatus)) {
                reportClientError("Generate mode returned an error status", scriptlessStatus.message);
            } else {
                showTemporaryMessage(runtimeActionFeedbackMessage(RUNTIME_FEEDBACK_ACTIONS.GENERATE_STARTED, scriptlessStatus));
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
            scriptlessStatus = await stopScriptlessRequest(loadJson);
            showTemporaryMessage(runtimeActionFeedbackMessage(RUNTIME_FEEDBACK_ACTIONS.GENERATE_STOPPED, scriptlessStatus));
        } catch (executionError) {
            reportClientError("Unable to stop Generate mode", executionError);
        } finally {
            saving = false;
        }
    }

    async function refreshScriptlessStatus() {
        scriptlessStatus = await loadScriptlessStatusRequest(loadJson);
    }

    async function refreshCliStatus() {
        cliStatus = await loadCliStatusRequest(loadJson);
    }

    async function refreshRemoteSpyStatus() {
        spyState = await loadRemoteSpyStatusRequest(loadJson);
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

    // Role and navigation changes share the same guarded transition path as editor changes.
    function storedWebStudioRole() {
        return storedWebStudioRoleFromStorage(typeof window === "undefined" ? null : window.localStorage);
    }

    function storeWebStudioRole(role) {
        storeWebStudioRoleInStorage(typeof window === "undefined" ? null : window.localStorage, role);
    }

    function openAllowedSettingsImmediate(allowedSettingsGroupIds) {
        openEditorImmediate("settings-form");
        selectedSettingsGroupId = selectedAllowedSettingsGroupId(workspaceDocument, allowedSettingsGroupIds, selectedSettingsGroupId);
    }

    function openBasicSettingsImmediate() {
        openAllowedSettingsImmediate(BASIC_ROLE_SETTINGS_GROUP_IDS);
    }

    function openTestSettingsImmediate() {
        openAllowedSettingsImmediate(TEST_SETTINGS_GROUP_IDS);
    }

    async function openOraclePanel(panelId) {
        await guardConfigurationTransition(async () => {
            selectedOraclePanelId = panelId;
            if (panelId === TEST_ORACLE_PANEL_IDS.ACTIVE) {
                return;
            }

            if (panelId === TEST_ORACLE_PANEL_IDS.EXTENDED_ENABLEMENT) {
                openEditorImmediate("settings-form");
                selectedSettingsGroupId = oracleSettingsGroupId(panelId);
                return;
            }

            if (panelId === TEST_ORACLE_PANEL_IDS.JAVA_FILES || panelId === TEST_ORACLE_PANEL_IDS.DSL_FILES) {
                openEditorImmediate("oracle-source");
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
            await loadTestOracleInventory();
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

    // Implements WS-FUNC-TOP-NAV-ROLES-001: role and menu transitions run through the shared navigation guard.
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
        activeNavMenu = toggledNavMenu(activeNavMenu, menuId);
    }

    async function navigateFromMenu(item) {
        const navigationAction = navigationActionForMenuItem(item);

        if (navigationAction === NAVIGATION_ACTIONS.NONE) {
            return;
        }

        activeNavMenu = "";

        if (navigationAction === NAVIGATION_ACTIONS.BASIC_SETTINGS) {
            await navigateToBasicSettings();
        } else if (navigationAction === NAVIGATION_ACTIONS.SETTINGS) {
            await navigateToSettings();
        } else if (navigationAction === NAVIGATION_ACTIONS.TEST_ORACLES) {
            await navigateToTestOracles();
        } else if (navigationAction === NAVIGATION_ACTIONS.TEST_GOALS) {
            await navigateToTestGoals();
        } else if (navigationAction === NAVIGATION_ACTIONS.COMPOSITION_FLOW) {
            await navigateToAdvancedCompositionFlow();
        } else if (navigationAction === NAVIGATION_ACTIONS.POLICIES) {
            await navigateToAdvancedPolicies();
        } else if (navigationAction === NAVIGATION_ACTIONS.GENERATE_MODE) {
            await navigateToRun();
        } else if (navigationAction === NAVIGATION_ACTIONS.CLI_MODE) {
            await navigateToCli();
        } else if (navigationAction === NAVIGATION_ACTIONS.TEST_RESULTS) {
            await navigateToResults();
        } else if (navigationAction === NAVIGATION_ACTIONS.STATE_MODEL) {
            await navigateToStateModel();
        } else if (navigationAction === NAVIGATION_ACTIONS.DEBUG_LOGS) {
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
            const { request, workspace } = await createWorkspaceRequest(loadJson, workspaceCreateDraft);
            await refreshInitialData();
            currentPage = workspaceManagementLandingPageForRole(currentRole);
            await loadWorkspace(workspace?.name || request.name);
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
            const { request, workspace } = await renameWorkspaceRequest(loadJson, selectedWorkspaceName, workspaceRenameDraft);
            await refreshInitialData();
            currentPage = workspaceManagementLandingPageForRole(currentRole);
            await loadWorkspace(workspace?.name || request.name);
            workspaceManagementDialogOpen = false;
            showTemporaryMessage(`Workspace renamed to ${request.name}.`);
        } catch (renameError) {
            reportClientError("Unable to rename workspace", renameError);
            workspaceManagementError = renameError.message || "Unable to rename workspace.";
        } finally {
            saving = false;
        }
    }

    function openStateModelExternalTab(url = STATE_MODEL_DEFAULT_URL) {
        window.open(url, "_blank", "noopener,noreferrer");
    }

    // Unsaved-change guards decide whether navigation, role changes, and runtime launches may continue.
    function setWorkspaceSettingByKey(settingKey, value) {
        const setting = findWorkspaceSetting(workspaceDocument, settingKey);
        if (!setting) {
            return;
        }

        setSettingValue(setting, `${value}`);
    }

    function hasCliAgentSettingsChanges() {
        return JSON.stringify(normalizedCliAgentSettings(cliAgentSettings))
            !== JSON.stringify(normalizedCliAgentSettings(savedCliAgentSettings));
    }

    function hasSettingsChanges() {
        return settingsChanged(
            workspaceDocument?.testSettings?.content,
            savedTestSettingsContent,
            visualSettingsDirty
        );
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

    function hasOracleSourceChanges() {
        return oracleSourceDirty;
    }

    // Implements WS-FUNC-CONFIG-GUARD-001 and WS-UX-CONFIG-GUARD-001:
    // coordinates dirty-state checks, guard dialogs, save/discard actions, and pending navigation.
    function configurationDirtyAreas() {
        return buildConfigurationDirtyAreas({
            settingsDirty: hasSettingsChanges(),
            compositionPropertiesDirty: hasCompositionPropertiesChanges(),
            compositionFlowDirty: hasSelectedSourceChanges(["service", "capability"]),
            policiesPropertiesDirty: hasPoliciesPropertiesChanges(),
            policiesFlowDirty: hasSelectedSourceChanges(["policy"]),
            oracleSourceDirty: hasOracleSourceChanges()
        });
    }

    // Implements WS-FUNC-TEST-SETTINGS-001: saves either the visual settings form or raw test.settings editor.
    async function saveCurrentSettingsEditor() {
        if (selectedEditor === "settings-form") {
            return saveVisualSettings();
        }

        if (selectedEditor === "test-settings" && workspaceDocument?.testSettings) {
            saving = true;
            message = "";
            try {
                await saveTestSettingsRequest(loadJson, selectedWorkspaceName, workspaceDocument.testSettings.content);
                savedTestSettingsContent = workspaceDocument.testSettings.content || "";
                visualSettingsDirty = false;
                workspaceDocument = workspaceDocumentWithSettingsContentValues(workspaceDocument);
                touchWorkspaceDocument();
                showTemporaryMessage("Settings saved.");
                return true;
            } catch (saveError) {
                reportClientError("Unable to save settings", saveError);
                return false;
            } finally {
                saving = false;
            }
        }

        return false;
    }

    async function saveCurrentGuardedEditor() {
        if (settingsEditorSelected(selectedEditor)) {
            return saveCurrentSettingsEditor();
        }

        if (selectedEditor === "composition-properties" && workspaceDocument?.compositionProperties) {
            await saveWorkspaceFile(
                "composition-properties",
                workspaceDocument.compositionProperties.content
            );
            return true;
        }

        if (selectedEditor === "policies-properties" && workspaceDocument?.policiesProperties) {
            await saveWorkspaceFile(
                "policies-properties",
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

        if (selectedEditor === "oracle-source") {
            if (selectedOracleSourceFile?.category === "java-oracle") {
                return saveOracleJavaFile();
            }

            if (selectedOracleSourceFile?.category === "dsl-oracle") {
                return generateJavaFromOracleDslFile();
            }
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

        pendingGuard = pendingGuardState(action);
        const dialogDetails = guardDialogDetails(selectedEditor, selectedOracleSourceFile);
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
        const dialogDetails = cliAgentSettingsGuardDetails();
        pendingGuard = pendingGuardState(action, dialogDetails.kind);
        openUnsavedSettingsDialog(
            dialogDetails.title,
            dialogDetails.message,
            dialogDetails.saveLabel
        );
    }

    function openTestGoalGuard(action) {
        const dialogDetails = testGoalGuardDetails();
        pendingGuard = pendingGuardState(action, dialogDetails.kind);
        openUnsavedSettingsDialog(
            dialogDetails.title,
            dialogDetails.message,
            dialogDetails.saveLabel
        );
    }

    async function discardUnsavedConfigurationChanges() {
        const action = pendingGuard.action;
        const saveTarget = guardSaveTarget(pendingGuard.kind);
        if (saveTarget === "cli-agent") {
            cliAgentSettings = normalizedCliAgentSettings(savedCliAgentSettings);
        } else if (saveTarget === "test-goal") {
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
        const action = pendingGuard.action;
        const saveTarget = guardSaveTarget(pendingGuard.kind);
        let saved;
        if (saveTarget === "cli-agent") {
            saved = await saveCliAgentSettings();
        } else if (saveTarget === "test-goal") {
            saved = await saveTestGoalFile();
        } else {
            saved = await saveCurrentGuardedEditor();
        }
        if (!saved) {
            closeUnsavedSettingsDialog();
            return;
        }

        if (saveTarget !== "cli-agent" && hasCurrentGuardedChanges()) {
            return;
        }

        closeUnsavedSettingsDialog();
        if (action) {
            await action();
        }
    }

    // State model navigation starts analysis when needed and keeps the modal actionable during startup.
    async function navigateToStateModel() {
        const workspaceDialog = stateModelWorkspaceDialog(selectedWorkspaceName);
        if (workspaceDialog) {
            openStateModelDialog(workspaceDialog.title, workspaceDialog.message);
            return;
        }

        saving = true;
        message = "";

        try {
            const response = await openStateModelRequest(loadJson, selectedWorkspaceName);
            showStateModelStatus(response);
            if (stateModelShouldOpenExternalTab(response)) {
                openStateModelExternalTab(stateModelExternalUrl(response));
            } else if (stateModelShouldPollStatus(response)) {
                scheduleStateModelStatusPolling();
            }
            showTemporaryMessage(stateModelRequestedMessage(response));
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
            cliAgentSettings = cliAgentSettingsFromWorkspace(workspaceDocument);
        }
        visualSettingsDirty = true;
        touchWorkspaceDocument();
    }

    function toPropertiesContent(properties) {
        return Object.entries(properties)
            .map(([key, value]) => `${key}=${value}`)
            .join("\n")
            .concat("\n");
    }

    // Composition Flow and Policies map selected nodes to editable Java/profile sources.
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

    async function togglePolicySourceActivation(sourceFile, enablePolicy) {
        if (!selectedWorkspaceName || !workspaceDocument?.policiesProperties?.content || !sourceFile) {
            return;
        }

        const propertyKey = inferPolicyPropertyKey(sourceFile, workspaceDocument?.policyDefinitions || []);
        if (!propertyKey) {
            reportClientError(`Unable to infer policy seam for ${sourceFile.name}`, new Error("Unknown policy seam"));
            return;
        }

        const className = sourceClassName(sourceFile);
        const properties = parsePropertiesContent(workspaceDocument.policiesProperties.content);
        const nextProperties = updatedPolicyPropertiesContent(properties, propertyKey, className, enablePolicy);

        await saveWorkspaceFile(
            "policies-properties",
            toPropertiesContent(nextProperties)
        );
    }

    // Test Results and Debug Files are workspace-scoped output views with independent loading state.
    async function loadResults(workspaceName = selectedWorkspaceName) {
        if (!workspaceName) {
            resultsData = null;
            selectedResultGroup = null;
            selectedResultFile = null;
            return;
        }

        try {
            resultsData = await loadResultListRequest(loadJson, workspaceName);
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
            debugFiles = await loadDebugFilesRequest(loadJson);
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
            selectedDebugFile = await loadDebugFileRequest(loadJson, debugFile);
        } catch (debugFileError) {
            selectedDebugFile = {
                name: debugFile.name,
                path: debugFile.path,
                content: debugFileError?.message || "Unable to load the selected debug file."
            };
            reportClientError(`Unable to load debug file ${debugFile.name}`, debugFileError);
        }
    }

    // Test Goals and Test Oracles have their own source selections, separate from Composition/Policy Java files.
    function resetTestGoalSelection() {
        const nextState = clearedTestGoalSelectionState();
        selectedTestGoalFolderPath = nextState.selectedTestGoalFolderPath;
        selectedTestGoalFile = nextState.selectedTestGoalFile;
        testGoalDraftContent = nextState.testGoalDraftContent;
        savedTestGoalContent = nextState.savedTestGoalContent;
    }

    function resetOracleSourceSelection() {
        const nextState = clearedOracleSourceState();
        selectedOracleSourceFile = nextState.selectedOracleSourceFile;
        oracleSourceDraftContent = nextState.oracleSourceDraftContent;
        savedOracleSourceContent = nextState.savedOracleSourceContent;
        oracleDslResult = nextState.oracleDslResult;
        javaCompileResult = nextState.javaCompileResult;
    }

    // Test Oracles combine settings-backed enablement with workspace Java and DSL oracle files.
    async function loadTestOracleInventory(workspaceName = selectedWorkspaceName) {
        if (!workspaceName) {
            testOracleInventory = null;
            testOracleInventoryLoading = false;
            return;
        }

        testOracleInventoryLoading = true;
        try {
            testOracleInventory = await loadTestOracleInventoryRequest(loadJson, workspaceName);
        } catch (oracleError) {
            reportClientError("Unable to load Test Oracles", oracleError);
            testOracleInventory = null;
        } finally {
            testOracleInventoryLoading = false;
        }
    }

    // Implements WS-FUNC-ORACLE-DSL-EDITOR-001: load backend-generated DSL metadata used by Monaco assistance.
    async function loadDslOracleMetadata() {
        try {
            dslOracleMetadata = await loadDslOracleMetadataRequest(loadJson);
        } catch (metadataError) {
            dslOracleMetadata = null;
            reportClientError("Unable to load DSL oracle metadata", metadataError);
        }
    }

    // Implements WS-FUNC-ORACLE-JAVA-ENABLEMENT-001: write Java oracle checkbox state into ExtendedOracles.
    function updateExtendedOraclesSetting(nextValue) {
        for (const settingsGroup of workspaceDocument?.settingsGroups || []) {
            const setting = (settingsGroup.settings || []).find((item) => item.key === "ExtendedOracles");
            if (setting) {
                setSettingValue(setting, nextValue);
                return;
            }
        }
    }

    function toggleExtendedOracle(oracleName, enabled) {
        const oracleItems = extendedOracleCheckboxItems(testOracleInventory).map((item) => ({
            ...item,
            active: item.name === oracleName ? enabled : item.active
        }));
        const nextValue = extendedOracleSettingValue(oracleItems);
        updateExtendedOraclesSetting(nextValue);
        testOracleInventory = testOracleInventory
            ? {
                ...testOracleInventory,
                activeOracles: nextValue ? nextValue.split(",") : [],
                items: (testOracleInventory.items || []).map((item) => ({
                    ...item,
                    active: item.name === oracleName ? enabled : item.active
                }))
            }
            : testOracleInventory;
    }

    function setAllExtendedOraclesEnabled(enabled) {
        const oracleItems = extendedOracleItemsWithEnablement(
            extendedOracleCheckboxItems(testOracleInventory),
            enabled
        );
        const nextValue = extendedOracleSettingValue(oracleItems);
        updateExtendedOraclesSetting(nextValue);
        testOracleInventory = testOracleInventory
            ? {
                ...testOracleInventory,
                activeOracles: nextValue ? nextValue.split(",") : [],
                items: (testOracleInventory.items || []).map((item) => ({
                    ...item,
                    active: item.origin === "BUILT_IN" || item.origin === "WORKSPACE_JAVA"
                        ? enabled
                        : item.active
                }))
            }
            : testOracleInventory;
    }

    async function loadOracleDslFile(oracleFile) {
        if (!oracleFile?.path) {
            await guardConfigurationTransition(async () => {
                resetOracleSourceSelection();
            }, "oracle-source:none");
            return;
        }

        if (selectedOracleSourceFile?.category === "dsl-oracle" && selectedOracleSourceFile.location === oracleFile.path) {
            return;
        }

        await guardConfigurationTransition(async () => {
            try {
                selectedOracleSourceFile = await loadOracleDslFileRequest(loadJson, selectedWorkspaceName, oracleFile.path);
                const nextState = loadedOracleSourceState(selectedOracleSourceFile);
                selectedOracleSourceFile = nextState.selectedOracleSourceFile;
                oracleSourceDraftContent = nextState.oracleSourceDraftContent;
                savedOracleSourceContent = nextState.savedOracleSourceContent;
                oracleDslResult = nextState.oracleDslResult;
                javaCompileResult = nextState.javaCompileResult;
            } catch (oracleError) {
                reportClientError(`Unable to load DSL oracle ${oracleFile.path}`, oracleError);
            }
        }, `oracle-source:${oracleFile.path}`);
    }

    async function loadOracleJavaFile(oracleFile) {
        if (!oracleFile?.path) {
            await guardConfigurationTransition(async () => {
                resetOracleSourceSelection();
            }, "oracle-source:none");
            return;
        }

        if (selectedOracleSourceFile?.category === "java-oracle" && selectedOracleSourceFile.location === oracleFile.path) {
            return;
        }

        await guardConfigurationTransition(async () => {
            try {
                selectedOracleSourceFile = await loadOracleJavaFileRequest(loadJson, selectedWorkspaceName, oracleFile.path);
                const nextState = loadedOracleSourceState(selectedOracleSourceFile);
                selectedOracleSourceFile = nextState.selectedOracleSourceFile;
                oracleSourceDraftContent = nextState.oracleSourceDraftContent;
                savedOracleSourceContent = nextState.savedOracleSourceContent;
                oracleDslResult = nextState.oracleDslResult;
                javaCompileResult = nextState.javaCompileResult;
            } catch (oracleError) {
                reportClientError(`Unable to load Java oracle ${oracleFile.path}`, oracleError);
            }
        }, `oracle-source:${oracleFile.path}`);
    }

    function setOracleSourceDraftContent(content) {
        oracleSourceDraftContent = content;
        oracleDslResult = null;
    }

    function discardOracleSourceChanges() {
        oracleSourceDraftContent = savedOracleSourceContent;
    }

    async function saveOracleJavaFile() {
        if (!selectedOracleSourceFile?.location) {
            return false;
        }

        saving = true;
        message = "";

        try {
            selectedOracleSourceFile = await saveOracleJavaFileRequest(
                loadJson,
                selectedWorkspaceName,
                selectedOracleSourceFile.location,
                oracleSourceDraftContent
            );
            const nextState = loadedOracleSourceState(selectedOracleSourceFile);
            selectedOracleSourceFile = nextState.selectedOracleSourceFile;
            oracleSourceDraftContent = nextState.oracleSourceDraftContent;
            savedOracleSourceContent = nextState.savedOracleSourceContent;
            javaCompileResult = nextState.javaCompileResult;
            await loadTestOracleInventory();
            showTemporaryMessage("Java oracle saved.");
            return true;
        } catch (oracleError) {
            reportClientError(`Unable to save Java oracle ${selectedOracleSourceFile.location}`, oracleError);
            return false;
        } finally {
            saving = false;
        }
    }

    async function compileOracleJavaFile() {
        if (!selectedOracleSourceFile?.location) {
            return false;
        }

        saving = true;
        message = "";

        try {
            javaCompileResult = await compileOracleJavaFileRequest(
                loadJson,
                selectedWorkspaceName,
                selectedOracleSourceFile.location,
                oracleSourceDraftContent
            );
            if (javaCompileResult?.success) {
                savedOracleSourceContent = oracleSourceDraftContent;
                await refreshWorkspaceDocument();
                await loadTestOracleInventory();
                showTemporaryMessage("Java oracle compiled.");
            }

            return javaCompileResult?.success === true;
        } catch (oracleError) {
            reportClientError(`Unable to compile Java oracle ${selectedOracleSourceFile.location}`, oracleError);
            javaCompileResult = oracleJavaCompileErrorResult(selectedOracleSourceFile, oracleError?.message);
            return false;
        } finally {
            saving = false;
        }
    }

    async function createOracleDslFile(path) {
        if (!path) {
            return;
        }

        saving = true;
        message = "";

        try {
            selectedOracleSourceFile = await createOracleDslFileRequest(loadJson, selectedWorkspaceName, path);
            const nextState = loadedOracleSourceState(selectedOracleSourceFile);
            selectedOracleSourceFile = nextState.selectedOracleSourceFile;
            oracleSourceDraftContent = nextState.oracleSourceDraftContent;
            savedOracleSourceContent = nextState.savedOracleSourceContent;
            oracleDslResult = nextState.oracleDslResult;
            javaCompileResult = nextState.javaCompileResult;
            await loadTestOracleInventory();
            showTemporaryMessage("DSL oracle created.");
        } catch (oracleError) {
            reportClientError(`Unable to create DSL oracle ${path}`, oracleError);
        } finally {
            saving = false;
        }
    }

    async function createOracleJavaFile(path) {
        if (!path) {
            return;
        }

        saving = true;
        message = "";

        try {
            selectedOracleSourceFile = await createOracleJavaFileRequest(loadJson, selectedWorkspaceName, path);
            const nextState = loadedOracleSourceState(selectedOracleSourceFile);
            selectedOracleSourceFile = nextState.selectedOracleSourceFile;
            oracleSourceDraftContent = nextState.oracleSourceDraftContent;
            savedOracleSourceContent = nextState.savedOracleSourceContent;
            oracleDslResult = nextState.oracleDslResult;
            javaCompileResult = nextState.javaCompileResult;
            await refreshWorkspaceDocument();
            await loadTestOracleInventory();
            showTemporaryMessage("Java oracle created.");
        } catch (oracleError) {
            reportClientError(`Unable to create Java oracle ${path}`, oracleError);
        } finally {
            saving = false;
        }
    }

    async function deleteOracleDslFile(path) {
        if (!path) {
            return;
        }

        saving = true;
        message = "";

        try {
            testOracleInventory = await deleteOracleDslFileRequest(loadJson, selectedWorkspaceName, path);
            if (shouldClearOracleSourceAfterDelete(selectedOracleSourceFile, path)) {
                resetOracleSourceSelection();
            }
            showTemporaryMessage("DSL oracle deleted.");
        } catch (oracleError) {
            reportClientError(`Unable to delete DSL oracle ${path}`, oracleError);
        } finally {
            saving = false;
        }
    }

    async function generateJavaFromOracleDslFile() {
        if (!selectedOracleSourceFile?.location) {
            return false;
        }

        saving = true;
        message = "";

        try {
            oracleDslResult = await generateJavaFromOracleDslFileRequest(
                loadJson,
                selectedWorkspaceName,
                selectedOracleSourceFile.location,
                oracleSourceDraftContent
            );
            if (oracleDslResult?.success) {
                savedOracleSourceContent = oracleSourceDraftContent;
                await refreshWorkspaceDocument();
                await loadTestOracleInventory();
                showTemporaryMessage("DSL oracle saved and Java oracle generated.");
                return true;
            }

            return false;
        } catch (oracleError) {
            reportClientError(`Unable to generate Java from DSL oracle ${selectedOracleSourceFile.location}`, oracleError);
            oracleDslResult = oracleDslGenerationErrorResult(oracleError?.message);
            return false;
        } finally {
            saving = false;
        }
    }

    async function deleteOracleJavaFile(path) {
        if (!path) {
            return;
        }

        saving = true;
        message = "";

        try {
            testOracleInventory = await deleteOracleJavaFileRequest(loadJson, selectedWorkspaceName, path);
            if (shouldClearOracleSourceAfterDelete(selectedOracleSourceFile, path)) {
                resetOracleSourceSelection();
            }
            showTemporaryMessage("Java oracle deleted.");
        } catch (oracleError) {
            reportClientError(`Unable to delete Java oracle ${path}`, oracleError);
        } finally {
            saving = false;
        }
    }

    // Test Goals are authored per workspace and guarded like other editable workspace documents.
    async function loadTestGoalTree(workspaceName = selectedWorkspaceName) {
        if (!workspaceName) {
            testGoalTree = null;
            return;
        }

        try {
            testGoalTree = await loadTestGoalTreeRequest(loadJson, workspaceName);
        } catch (testGoalError) {
            reportClientError("Unable to load Test Goals", testGoalError);
            testGoalTree = null;
        }
    }

    async function loadTestGoalFileNow(goalFile) {
        if (!goalFile?.path) {
            resetTestGoalSelection();
            return;
        }

        try {
            selectedTestGoalFile = await loadTestGoalFileRequest(loadJson, selectedWorkspaceName, goalFile.path);
            const nextState = loadedTestGoalFileState(selectedTestGoalFile);
            selectedTestGoalFolderPath = nextState.selectedTestGoalFolderPath;
            selectedTestGoalFile = nextState.selectedTestGoalFile;
            testGoalDraftContent = nextState.testGoalDraftContent;
            savedTestGoalContent = nextState.savedTestGoalContent;
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
            selectedTestGoalFile = await saveTestGoalFileRequest(
                loadJson,
                selectedWorkspaceName,
                selectedTestGoalFile.path,
                testGoalDraftContent
            );
            const nextState = loadedTestGoalFileState(selectedTestGoalFile);
            selectedTestGoalFolderPath = nextState.selectedTestGoalFolderPath;
            selectedTestGoalFile = nextState.selectedTestGoalFile;
            testGoalDraftContent = nextState.testGoalDraftContent;
            savedTestGoalContent = nextState.savedTestGoalContent;
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
            selectedTestGoalFile = await createTestGoalFileRequest(loadJson, selectedWorkspaceName, path);
            const nextState = loadedTestGoalFileState(selectedTestGoalFile);
            selectedTestGoalFolderPath = nextState.selectedTestGoalFolderPath;
            selectedTestGoalFile = nextState.selectedTestGoalFile;
            testGoalDraftContent = nextState.testGoalDraftContent;
            savedTestGoalContent = nextState.savedTestGoalContent;
            await loadTestGoalTree();
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
            testGoalTree = await createTestGoalFolderRequest(loadJson, selectedWorkspaceName, path);
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
            testGoalTree = await deleteTestGoalPathRequest(loadJson, selectedWorkspaceName, path);
            if (shouldClearTestGoalSelectionAfterDelete(selectedTestGoalFile, path)) {
                resetTestGoalSelection();
            }
            showTemporaryMessage("Test goal item deleted.");
        } catch (testGoalError) {
            reportClientError(`Unable to delete Test Goal item ${path}`, testGoalError);
        } finally {
            saving = false;
        }
    }

    // Derived view state feeds child components with current source lists and editor document metadata.
    $: if (workspaceDocument?.sourceFiles) {
        policySourceFiles = policySourceFilesFromWorkspace(workspaceDocument);
        compositionSourceFiles = compositionSourceFilesFromWorkspace(workspaceDocument);
        activePolicySourceFiles = sourceFilesForReferencedClasses(policySourceFiles, workspaceDocument, "policies");
        inactivePolicySourceFiles = sourceFilesNotInReferenceSet(policySourceFiles, workspaceDocument, "policies");
        compositionFlowNodes = buildCompositionFlowNodes(workspaceDocument, compositionSourceFiles.concat(policySourceFiles));
        selectedCompositionFlowNode = refreshedSelectedCompositionFlowNode(compositionFlowNodes, selectedCompositionFlowNode);
    } else {
        policySourceFiles = [];
        compositionSourceFiles = [];
        activePolicySourceFiles = [];
        inactivePolicySourceFiles = [];
        compositionFlowNodes = [];
    }

    $: {
        const editorDocumentDescriptor = currentEditorDocumentDescriptor({
            workspaceDocument,
            selectedEditor,
            selectedSourceFile
        });

        currentEditorDocument = currentEditorDocumentState({
            descriptor: editorDocumentDescriptor,
            settingsDirty,
            policiesPropertiesDirty: hasPoliciesPropertiesChanges(),
            compositionPropertiesDirty: hasCompositionPropertiesChanges(),
            selectedSourceDirty: hasSelectedSourceChanges([editorDocumentDescriptor?.sourceCategory]),
            saveCurrentSettingsEditor,
            saveVisualSettings,
            savePoliciesProperties: () => saveWorkspaceFile(
                "policies-properties",
                workspaceDocument.policiesProperties.content
            ),
            saveCompositionProperties: () => saveWorkspaceFile(
                "composition-properties",
                workspaceDocument.compositionProperties.content
            ),
            saveSelectedSource
        });
    }

    $: if (selectedEditor === "settings-form" && !selectedSettingsGroupId && workspaceDocument?.settingsGroups?.length > 0) {
        selectedSettingsGroupId = selectedSettingsGroupForEditor(workspaceDocument, "", selectedSettingsGroupId);
    }

    // Result selection and deletion update the workspace-scoped report model without reloading the whole app.
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
            selectedResultFile = await loadResultFileRequest(loadJson, selectedWorkspaceName, resultFile);
        } catch (fileError) {
            reportClientError(`Unable to load result file ${resultFile.name}`, fileError);
        }
    }

    function applyRefreshedResults(refreshedResults, preferredGroupPath = "") {
        const nextState = refreshedResultSelectionState(refreshedResults, preferredGroupPath);
        resultsData = nextState.resultsData;
        selectedResultGroup = nextState.selectedResultGroup;
        selectedResultFile = nextState.selectedResultFile;
    }

    async function deleteResultFile(resultFile) {
        if (!resultFile?.path) {
            return;
        }

        try {
            const currentGroupPath = selectedResultGroup?.path || "";
            const refreshedResults = await deleteResultFileRequest(loadJson, selectedWorkspaceName, resultFile);
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
            const refreshedResults = await deleteResultGroupRequest(loadJson, selectedWorkspaceName, resultGroup);
            applyRefreshedResults(refreshedResults);
        } catch (deleteError) {
            reportClientError(`Unable to delete result output folder ${resultGroup.name}`, deleteError);
        }
    }

    // Runtime execution actions drive Spy, Generate, and CLI modes while preserving shared status polling.
    async function startRemoteSpyMode() {
        if (!selectedWorkspaceName) {
            return;
        }

        saving = true;
        message = "";
        currentPage = runtimePageForAction(RUNTIME_ACTIONS.REMOTE_SPY);

        try {
            spyState = await startRemoteSpyRequest(loadJson, selectedWorkspaceName);
            showTemporaryMessage(runtimeActionFeedbackMessage(RUNTIME_FEEDBACK_ACTIONS.REMOTE_SPY_STARTED, spyState));
        } catch (spyError) {
            reportClientError("Unable to start remote Spy Mode", spyError);
        } finally {
            saving = false;
        }
    }

    // Implements WS-FUNC-COMPOSITION-FLOW-001: creates or opens the Java source linked to a composition node.
    async function createCompositionModuleSource(flowNode) {
        if (!selectedWorkspaceName || !flowNode?.propertyKey) {
            return;
        }

        saving = true;
        message = "";

        try {
            const sourceFile = await createCompositionModuleSourceRequest(loadJson, selectedWorkspaceName, flowNode.propertyKey);

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

    // Implements WS-FUNC-POLICIES-001: creates Java policy source files for selected policy seams.
    async function createPolicySource(policyDefinition) {
        if (!selectedWorkspaceName || !policyDefinition?.propertyKey) {
            return;
        }

        saving = true;
        message = "";

        try {
            const sourceFile = await createPolicySourceRequest(loadJson, selectedWorkspaceName, policyDefinition.propertyKey);

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
            spyState = await refreshRemoteSpyRequest(loadJson);
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
            spyState = await stopRemoteSpyRequest(loadJson);
            showTemporaryMessage(runtimeActionFeedbackMessage(RUNTIME_FEEDBACK_ACTIONS.REMOTE_SPY_STOPPED, spyState));
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
            spyState = await executeSpyActionRequest(loadJson, actionId);
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
            spyState = await executeSpyWidgetDefaultActionRequest(loadJson, widgetId);
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
        currentPage = runtimePageForAction(RUNTIME_ACTIONS.LOCAL_SPY);

        try {
            scriptlessStatus = await startLocalSpyRequest(loadJson, selectedWorkspaceName);
            showTemporaryMessage(runtimeActionFeedbackMessage(RUNTIME_FEEDBACK_ACTIONS.LOCAL_SPY_STARTED, scriptlessStatus));
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
            scriptlessStatus = await stopScriptlessRequest(loadJson);
            showTemporaryMessage(runtimeActionFeedbackMessage(RUNTIME_FEEDBACK_ACTIONS.LOCAL_SPY_STOPPED, scriptlessStatus));
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
            spyState = await executeSpyWidgetDirectTypeRequest(loadJson, widgetId, text);
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
        currentPage = runtimePageForAction(RUNTIME_ACTIONS.CLI_MANUAL);

        try {
            cliStatus = await startCliManualSessionRequest(loadJson, selectedWorkspaceName);
            showTemporaryMessage(runtimeActionFeedbackMessage(RUNTIME_FEEDBACK_ACTIONS.CLI_MANUAL_STARTED, cliStatus));
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
        currentPage = runtimePageForAction(RUNTIME_ACTIONS.CLI_AGENT);

        try {
            cliStatus = await startCliAgentSessionRequest(loadJson, selectedWorkspaceName);
            showTemporaryMessage(runtimeActionFeedbackMessage(RUNTIME_FEEDBACK_ACTIONS.CLI_AGENT_STARTED, cliStatus));
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
            cliStatus = await runCliManualCommandRequest(loadJson, commandLine);
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
            cliStatus = await stopCliManualSessionRequest(loadJson);
            showTemporaryMessage(runtimeActionFeedbackMessage(RUNTIME_FEEDBACK_ACTIONS.CLI_MANUAL_STOPPED, cliStatus));
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
            cliStatus = await stopCliAgentSessionRequest(loadJson);
            showTemporaryMessage(runtimeActionFeedbackMessage(RUNTIME_FEEDBACK_ACTIONS.CLI_AGENT_STOPPED, cliStatus));
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

    $: selectedWorkspaceSummary = workspaceSummaryForName(workspaces, selectedWorkspaceName);
    $: workspaceCreateValidationState = workspaceCreateValidation(workspaceCreateDraft, workspaces);
    $: workspaceRenameValidationState = workspaceRenameValidation(workspaceRenameDraft, selectedWorkspaceName, workspaces);

    // Startup loads metadata, backend status, and the default workspace before child views render.
    onMount(async () => {
        startScriptlessPolling();
        try {
            currentRole = storedWebStudioRole();
            currentPage = pageForRole(currentRole, currentPage);
            await loadDslOracleMetadata();
            await refreshInitialData();
            if (workspaces.length > 0) {
                const defaultWorkspace = preferredDefaultWorkspace(workspaces);
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
    <TopNavigation
        activeNavMenu={activeNavMenu}
        currentPage={currentPage}
        currentRole={currentRole}
        selectedWorkspaceName={selectedWorkspaceName}
        workspaces={workspaces}
        onWorkspaceManage={openWorkspaceManagementDialog}
        onWorkspaceChange={(nextWorkspaceName) => {
            guardApplicationTransition(async () => {
                await loadWorkspace(nextWorkspaceName);
            });
        }}
        onRoleChange={changeWebStudioRole}
        onToggleMenu={toggleNavMenu}
        onNavigateFromMenu={navigateFromMenu}
        onNavigateToTestOracles={navigateToTestOracles}
        onNavigateToTestGoals={navigateToTestGoals}
        onNavigateToSpy={navigateToSpy}
    />
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
            currentEditorDocument={currentEditorDocument}
            loading={loading}
            openOraclePanel={openOraclePanel}
            openTestSettings={openTestSettings}
            openVisualSettings={openVisualSettings}
            openVisualSettingsGroup={openVisualSettingsGroup}
            regexValidationResults={regexValidationResults}
            savedTestSettingsContent={savedTestSettingsContent}
            saving={saving}
            selectedOraclePanelId={selectedOraclePanelId}
            testOracleInventory={testOracleInventory}
            testOracleInventoryLoading={testOracleInventoryLoading}
            setSettingValue={setSettingValue}
            selectedEditor={selectedEditor}
            selectedSettingsGroupId={selectedSettingsGroupId}
            selectedOracleSourceFile={selectedOracleSourceFile}
            oracleSourceDraftContent={oracleSourceDraftContent}
            oracleSourceDirty={oracleSourceDirty}
            oracleDslResult={oracleDslResult}
            javaCompileResult={javaCompileResult}
            dslOracleMetadata={dslOracleMetadata}
            compileOracleJavaFile={compileOracleJavaFile}
            createOracleDslFile={createOracleDslFile}
            createOracleJavaFile={createOracleJavaFile}
            deleteOracleDslFile={deleteOracleDslFile}
            deleteOracleJavaFile={deleteOracleJavaFile}
            discardOracleSourceChanges={discardOracleSourceChanges}
            generateJavaFromOracleDslFile={generateJavaFromOracleDslFile}
            loadOracleDslFile={loadOracleDslFile}
            loadOracleJavaFile={loadOracleJavaFile}
            setOracleSourceDraftContent={setOracleSourceDraftContent}
            toggleExtendedOracle={toggleExtendedOracle}
            setAllExtendedOraclesEnabled={setAllExtendedOraclesEnabled}
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

    <RuntimePages
        cliAgentSettings={cliAgentSettings}
        cliStatus={cliStatus}
        currentPage={currentPage}
        savedCliAgentSettings={savedCliAgentSettings}
        saving={saving}
        scriptlessStatus={scriptlessStatus}
        selectedWorkspaceName={selectedWorkspaceName}
        selectedWorkspaceSutConnector={selectedWorkspaceSutConnector}
        selectedWorkspaceSutConnectorValue={selectedWorkspaceSutConnectorValue}
        selectedWorkspaceCliStateProjectionMode={selectedWorkspaceCliStateProjectionMode}
        spyState={spyState}
        executeSpyAction={executeSpyAction}
        executeSpyWidgetDefaultAction={executeSpyWidgetDefaultAction}
        executeSpyWidgetDirectType={executeSpyWidgetDirectType}
        refreshRemoteSpyMode={refreshRemoteSpyMode}
        runCliManualCommand={runCliManualCommand}
        saveCliAgentSettings={saveCliAgentSettings}
        startCliAgentSession={startCliAgentSession}
        startCliManualSession={startCliManualSession}
        startGenerate={startGenerate}
        startLocalSpyMode={startLocalSpyMode}
        startRemoteSpyMode={startRemoteSpyMode}
        stopCliAgentSession={stopCliAgentSession}
        stopCliManualSession={stopCliManualSession}
        stopGenerate={stopGenerate}
        stopLocalSpyMode={stopLocalSpyMode}
        stopRemoteSpyMode={stopRemoteSpyMode}
    />

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

    <AppOverlays
        createDraft={workspaceCreateDraft}
        createValidation={workspaceCreateValidationState}
        error={workspaceManagementError}
        message={message}
        openWorkspaceDialog={workspaceManagementDialogOpen}
        renameDraft={workspaceRenameDraft}
        renameValidation={workspaceRenameValidationState}
        saving={saving}
        selectedWorkspaceName={selectedWorkspaceName}
        stateModelDialog={stateModelDialog}
        unsavedSettingsDialog={unsavedSettingsDialog}
        workspaceManagementTab={workspaceManagementTab}
        workspaces={workspaces}
        onCloseStateModelDialog={closeStateModelDialog}
        onCloseUnsavedDialog={closeUnsavedSettingsDialog}
        onCloseWorkspaceDialog={closeWorkspaceManagementDialog}
        onCreateWorkspace={createWorkspaceFromDialog}
        onDiscardUnsavedChanges={discardUnsavedConfigurationChanges}
        onOpenStateModelExternalTab={openStateModelExternalTab}
        onRenameWorkspace={renameWorkspaceFromDialog}
        onSaveUnsavedChanges={saveUnsavedConfigurationChanges}
        onStopStateModelAnalysis={stopStateModelAnalysis}
        onWorkspaceCreateDraftChange={(nextDraft) => {
            workspaceCreateDraft = nextDraft;
        }}
        onWorkspaceManagementTabChange={(nextTab) => {
            workspaceManagementTab = nextTab;
            workspaceManagementError = "";
        }}
        onWorkspaceRenameDraftChange={(nextDraft) => {
            workspaceRenameDraft = nextDraft;
        }}
    />
</div>
