# WebStudio Spec Trace

This document maps stable specification IDs to implementation files and tests.

Keep the main functional and UX specs readable. Use this file when a reader needs to jump from a spec ID to the code and tests that implement it.

<a id="ws-func-test-goals-001---workspace-scoped-test-goals"></a>

## WS-FUNC-TEST-GOALS-001 - Workspace-Scoped Test Goals

**Specification**

- [Test Goals specification](./specs/test-goals.md#ws-func-test-goals-001---workspace-scoped-test-goals)

**Backend implementation**

- [TestGoalService.java](../../webstudio/src/org/testar/webstudio/testgoal/TestGoalService.java)

**Frontend implementation**

- [testGoalsApi.js](../../webstudio/frontend/src/views/goals/testGoalsApi.js)
- [testGoalsModel.js](../../webstudio/frontend/src/views/goals/testGoalsModel.js)

**Unit tests**

- [TestGoalServiceTest.java](../../webstudio/test/org/testar/webstudio/testgoal/TestGoalServiceTest.java)
- [testGoalsApi.test.js](../../webstudio/frontend/test/views/goals/testGoalsApi.test.js)
- [testGoalsModel.test.js](../../webstudio/frontend/test/views/goals/testGoalsModel.test.js)

**Integration tests**

- None yet.

**Related requirements**

- [WS-UX-TEST-GOALS-001](#ws-ux-test-goals-001---test-goals-authoring-view)

<a id="ws-ux-test-goals-001---test-goals-authoring-view"></a>

## WS-UX-TEST-GOALS-001 - Test Goals Authoring View

**Specification**

- [Test Goals specification](./specs/test-goals.md#ws-ux-test-goals-001---test-goals-authoring-view)

**Backend implementation**

- None.

**Frontend implementation**

- [TestGoalsView.svelte](../../webstudio/frontend/src/views/goals/TestGoalsView.svelte)
- [testGoalsModel.js](../../webstudio/frontend/src/views/goals/testGoalsModel.js)

**Unit tests**

- [testGoalsModel.test.js](../../webstudio/frontend/test/views/goals/testGoalsModel.test.js)

**Integration tests**

- None yet.

**Related requirements**

- [WS-FUNC-TEST-GOALS-001](#ws-func-test-goals-001---workspace-scoped-test-goals)

<a id="ws-func-workspace-management-001---workspace-creation-and-rename"></a>

## WS-FUNC-WORKSPACE-MANAGEMENT-001 - Workspace Creation and Rename

**Specification**

- [Workspace Management specification](./specs/workspace-management.md#ws-func-workspace-management-001---workspace-creation-and-rename)

**Backend implementation**

- [WorkspaceController.java](../../webstudio/src/org/testar/webstudio/api/WorkspaceController.java)
- [WorkspaceService.java](../../webstudio/src/org/testar/webstudio/workspace/WorkspaceService.java)

**Frontend implementation**

- [workspaceManagementApi.js](../../webstudio/frontend/src/models/workspaceManagementApi.js)
- [workspaceManagementModel.js](../../webstudio/frontend/src/models/workspaceManagementModel.js)

**Unit tests**

- [WorkspaceServiceManagementTest.java](../../webstudio/test/org/testar/webstudio/workspace/WorkspaceServiceManagementTest.java)
- [workspaceManagementApi.test.js](../../webstudio/frontend/test/models/workspaceManagementApi.test.js)
- [workspaceManagementModel.test.js](../../webstudio/frontend/test/models/workspaceManagementModel.test.js)

**Integration tests**

- None yet.

**Related requirements**

- [WS-UX-WORKSPACE-MANAGEMENT-001](#ws-ux-workspace-management-001---workspace-management-modal)

<a id="ws-ux-workspace-management-001---workspace-management-modal"></a>

## WS-UX-WORKSPACE-MANAGEMENT-001 - Workspace Management Modal

**Specification**

- [Workspace Management specification](./specs/workspace-management.md#ws-ux-workspace-management-001---workspace-management-modal)

**Backend implementation**

- None.

**Frontend implementation**

- [TopNavigation.svelte](../../webstudio/frontend/src/app/TopNavigation.svelte)
- [WorkspaceManagementDialog.svelte](../../webstudio/frontend/src/app/WorkspaceManagementDialog.svelte)
- [workspaceManagementModel.js](../../webstudio/frontend/src/models/workspaceManagementModel.js)

**Unit tests**

- [workspaceManagementModel.test.js](../../webstudio/frontend/test/models/workspaceManagementModel.test.js)

**Integration tests**

- None yet.

**Related requirements**

- [WS-FUNC-WORKSPACE-MANAGEMENT-001](#ws-func-workspace-management-001---workspace-creation-and-rename)

<a id="ws-func-test-results-001---workspace-scoped-test-results"></a>

## WS-FUNC-TEST-RESULTS-001 - Workspace-Scoped Test Results

**Specification**

- [Test Results specification](./specs/test-results.md#ws-func-test-results-001---workspace-scoped-test-results)

**Backend implementation**

- [ExecutionController.java](../../webstudio/src/org/testar/webstudio/api/ExecutionController.java)
- [ScriptlessExecutionAdapter.java](../../webstudio/src/org/testar/webstudio/execution/ScriptlessExecutionAdapter.java)
- [CliExecutionAdapter.java](../../webstudio/src/org/testar/webstudio/execution/CliExecutionAdapter.java)
- [ResultArtifactDeletion.java](../../webstudio/src/org/testar/webstudio/execution/ResultArtifactDeletion.java)
- [ResultVerdictStatus.java](../../webstudio/src/org/testar/webstudio/execution/ResultVerdictStatus.java)
- [ResultWorkspacePaths.java](../../webstudio/src/org/testar/webstudio/execution/ResultWorkspacePaths.java)

**Frontend implementation**

- [resultApi.js](../../webstudio/frontend/src/api/resultApi.js)
- [testResultsViewModel.js](../../webstudio/frontend/src/views/results/testResultsViewModel.js)

**Unit tests**

- [ScriptlessResultsWorkspaceScopeTest.java](../../webstudio/test/org/testar/webstudio/execution/ScriptlessResultsWorkspaceScopeTest.java)
- [ResultArtifactDeletionTest.java](../../webstudio/test/org/testar/webstudio/execution/ResultArtifactDeletionTest.java)
- [ResultVerdictStatusTest.java](../../webstudio/test/org/testar/webstudio/execution/ResultVerdictStatusTest.java)
- [ResultWorkspacePathsTest.java](../../webstudio/test/org/testar/webstudio/execution/ResultWorkspacePathsTest.java)
- [resultApi.test.js](../../webstudio/frontend/test/api/resultApi.test.js)
- [testResultsViewModel.test.js](../../webstudio/frontend/test/views/results/testResultsViewModel.test.js)

**Integration tests**

- None yet.

**Related requirements**

- [WS-UX-TEST-RESULTS-001](#ws-ux-test-results-001---test-results-inspection-view)

<a id="ws-ux-test-results-001---test-results-inspection-view"></a>

## WS-UX-TEST-RESULTS-001 - Test Results Inspection View

**Specification**

- [Test Results specification](./specs/test-results.md#ws-ux-test-results-001---test-results-inspection-view)

**Backend implementation**

- None.

**Frontend implementation**

- [TestResultsView.svelte](../../webstudio/frontend/src/views/results/TestResultsView.svelte)
- [testResultsViewModel.js](../../webstudio/frontend/src/views/results/testResultsViewModel.js)

**Unit tests**

- [testResultsViewModel.test.js](../../webstudio/frontend/test/views/results/testResultsViewModel.test.js)

**Integration tests**

- None yet.

**Related requirements**

- [WS-FUNC-TEST-RESULTS-001](#ws-func-test-results-001---workspace-scoped-test-results)

<a id="ws-func-state-model-001---state-model-analysis-lifecycle"></a>

## WS-FUNC-STATE-MODEL-001 - State Model Analysis Lifecycle

**Specification**

- [State Model specification](./specs/state-model.md#ws-func-state-model-001---state-model-analysis-lifecycle)

**Backend implementation**

- [StateModelAnalysisController.java](../../webstudio/src/org/testar/webstudio/api/StateModelAnalysisController.java)
- [StateModelAnalysisService.java](../../webstudio/src/org/testar/webstudio/analysis/StateModelAnalysisService.java)
- [StateModelStatusDto.java](../../webstudio/src/org/testar/webstudio/api/dto/StateModelStatusDto.java)

**Frontend implementation**

- [stateModelApi.js](../../webstudio/frontend/src/app/stateModelApi.js)
- [stateModelNavigation.js](../../webstudio/frontend/src/app/stateModelNavigation.js)

**Unit tests**

- [StateModelAnalysisServiceTest.java](../../webstudio/test/org/testar/webstudio/analysis/StateModelAnalysisServiceTest.java)
- [stateModelApi.test.js](../../webstudio/frontend/test/app/stateModelApi.test.js)
- [stateModelNavigation.test.js](../../webstudio/frontend/test/app/stateModelNavigation.test.js)

**Integration tests**

- None yet.

**Related requirements**

- [WS-UX-STATE-MODEL-001](#ws-ux-state-model-001---state-model-dialog-and-actions)

<a id="ws-ux-state-model-001---state-model-dialog-and-actions"></a>

## WS-UX-STATE-MODEL-001 - State Model Dialog and Actions

**Specification**

- [State Model specification](./specs/state-model.md#ws-ux-state-model-001---state-model-dialog-and-actions)

**Backend implementation**

- None.

**Frontend implementation**

- [StateModelDialog.svelte](../../webstudio/frontend/src/app/StateModelDialog.svelte)
- [stateModelNavigation.js](../../webstudio/frontend/src/app/stateModelNavigation.js)
- [AppOverlays.svelte](../../webstudio/frontend/src/app/AppOverlays.svelte)

**Unit tests**

- [stateModelNavigation.test.js](../../webstudio/frontend/test/app/stateModelNavigation.test.js)

**Integration tests**

- None yet.

**Related requirements**

- [WS-FUNC-STATE-MODEL-001](#ws-func-state-model-001---state-model-analysis-lifecycle)

<a id="ws-func-debug-files-001---runtime-debug-file-inspection"></a>

## WS-FUNC-DEBUG-FILES-001 - Runtime Debug File Inspection

**Specification**

- [Debug Files specification](./specs/debug-files.md#ws-func-debug-files-001---runtime-debug-file-inspection)

**Backend implementation**

- [WorkspaceController.java](../../webstudio/src/org/testar/webstudio/api/WorkspaceController.java)
- [WorkspaceService.java](../../webstudio/src/org/testar/webstudio/workspace/WorkspaceService.java)

**Frontend implementation**

- [debugFilesApi.js](../../webstudio/frontend/src/views/debug/debugFilesApi.js)

**Unit tests**

- [debugFilesApi.test.js](../../webstudio/frontend/test/views/debug/debugFilesApi.test.js)

**Integration tests**

- None yet.

**Related requirements**

- [WS-UX-DEBUG-FILES-001](#ws-ux-debug-files-001---debug-file-inspection-view)

<a id="ws-ux-debug-files-001---debug-file-inspection-view"></a>

## WS-UX-DEBUG-FILES-001 - Debug File Inspection View

**Specification**

- [Debug Files specification](./specs/debug-files.md#ws-ux-debug-files-001---debug-file-inspection-view)

**Backend implementation**

- None.

**Frontend implementation**

- [InspectLogsView.svelte](../../webstudio/frontend/src/views/debug/InspectLogsView.svelte)
- [debugFilesApi.js](../../webstudio/frontend/src/views/debug/debugFilesApi.js)

**Unit tests**

- [debugFilesApi.test.js](../../webstudio/frontend/test/views/debug/debugFilesApi.test.js)

**Integration tests**

- None yet.

**Related requirements**

- [WS-FUNC-DEBUG-FILES-001](#ws-func-debug-files-001---runtime-debug-file-inspection)

<a id="ws-func-runtime-execution-001---runtime-execution-modes"></a>

## WS-FUNC-RUNTIME-EXECUTION-001 - Runtime Execution Modes

**Specification**

- [Runtime Modes specification](./specs/runtime-modes.md#ws-func-runtime-execution-001---runtime-execution-modes)

**Backend implementation**

- [ExecutionController.java](../../webstudio/src/org/testar/webstudio/api/ExecutionController.java)
- [RemoteSpyController.java](../../webstudio/src/org/testar/webstudio/api/RemoteSpyController.java)
- [ScriptlessExecutionAdapter.java](../../webstudio/src/org/testar/webstudio/execution/ScriptlessExecutionAdapter.java)
- [CliExecutionAdapter.java](../../webstudio/src/org/testar/webstudio/execution/CliExecutionAdapter.java)
- [ComposedProtocol.java](../../testar/src/org/testar/scriptless/ComposedProtocol.java)
- [SpyMode.java](../../testar/src/org/testar/scriptless/mode/SpyMode.java)

**Frontend implementation**

- [runtimeApi.js](../../webstudio/frontend/src/views/runtime/runtimeApi.js)
- [runtimeModel.js](../../webstudio/frontend/src/views/runtime/runtimeModel.js)
- [RuntimePages.svelte](../../webstudio/frontend/src/views/runtime/RuntimePages.svelte)

**Unit tests**

- [runtimeApi.test.js](../../webstudio/frontend/test/views/runtime/runtimeApi.test.js)
- [runtimeModel.test.js](../../webstudio/frontend/test/views/runtime/runtimeModel.test.js)
- [runtimeModeControls.test.js](../../webstudio/frontend/test/models/runtimeModeControls.test.js)
- [ScriptlessSequenceVerdictTest.java](../../webstudio/test/org/testar/webstudio/execution/ScriptlessSequenceVerdictTest.java)
- [ScriptlessExecutionAdapterTest.java](../../webstudio/test/org/testar/webstudio/execution/ScriptlessExecutionAdapterTest.java)

**Integration tests**

- None yet.

**Related requirements**

- [WS-UX-RUNTIME-EXECUTION-001](#ws-ux-runtime-execution-001---runtime-execution-pages)

<a id="ws-ux-runtime-execution-001---runtime-execution-pages"></a>

## WS-UX-RUNTIME-EXECUTION-001 - Runtime Execution Pages

**Specification**

- [Runtime Modes specification](./specs/runtime-modes.md#ws-ux-runtime-execution-001---runtime-execution-pages)

**Backend implementation**

- None.

**Frontend implementation**

- [RunTestarView.svelte](../../webstudio/frontend/src/views/runtime/RunTestarView.svelte)
- [SpyModeView.svelte](../../webstudio/frontend/src/views/spy/SpyModeView.svelte)
- [CliModeView.svelte](../../webstudio/frontend/src/views/runtime/CliModeView.svelte)
- [runtimeModel.js](../../webstudio/frontend/src/views/runtime/runtimeModel.js)

**Unit tests**

- [runtimeModel.test.js](../../webstudio/frontend/test/views/runtime/runtimeModel.test.js)
- [runtimeModeControls.test.js](../../webstudio/frontend/test/models/runtimeModeControls.test.js)

**Integration tests**

- None yet.

**Related requirements**

- [WS-FUNC-RUNTIME-EXECUTION-001](#ws-func-runtime-execution-001---runtime-execution-modes)

<a id="ws-func-test-settings-001---workspace-test-settings-editor"></a>

## WS-FUNC-TEST-SETTINGS-001 - Workspace Test Settings Editor

**Specification**

- [Test Settings specification](./specs/settings.md#ws-func-test-settings-001---workspace-test-settings-editor)
- [Runtime Modes specification - CLI State Projection Mode](./specs/runtime-modes.md#cli-state-projection-mode)

**Backend implementation**

- [WorkspaceController.java](../../webstudio/src/org/testar/webstudio/api/WorkspaceController.java)
- [WorkspaceService.java](../../webstudio/src/org/testar/webstudio/workspace/WorkspaceService.java)
- [WorkspaceSettingsCatalog.java](../../webstudio/src/org/testar/webstudio/workspace/WorkspaceSettingsCatalog.java)

**Frontend implementation**

- [settingsApi.js](../../webstudio/frontend/src/views/settings/settingsApi.js)
- [settingsEditorModel.js](../../webstudio/frontend/src/views/settings/settingsEditorModel.js)
- [settingsSelectOptions.js](../../webstudio/frontend/src/views/settings/settingsSelectOptions.js)
- [workspaceSettingsModel.js](../../webstudio/frontend/src/models/workspaceSettingsModel.js)
- [App.svelte](../../webstudio/frontend/src/App.svelte)

**Unit tests**

- [WorkspaceSettingsCatalogCliAgentTest.java](../../webstudio/test/org/testar/webstudio/workspace/WorkspaceSettingsCatalogCliAgentTest.java)
- [settingsApi.test.js](../../webstudio/frontend/test/views/settings/settingsApi.test.js)
- [settingsEditorModel.test.js](../../webstudio/frontend/test/views/settings/settingsEditorModel.test.js)
- [settingsSelectOptions.test.js](../../webstudio/frontend/test/views/settings/settingsSelectOptions.test.js)
- [workspaceSettingsModel.test.js](../../webstudio/frontend/test/models/workspaceSettingsModel.test.js)

**Integration tests**

- None yet.

**Acceptance scenarios**

- [Test Settings scenarios](./specs/settings.md#acceptance-scenarios)

**Related requirements**

- [WS-UX-TEST-SETTINGS-001](#ws-ux-test-settings-001---test-settings-editor-view)
- [WS-FUNC-CONFIG-GUARD-001](#ws-func-config-guard-001---configuration-unsaved-change-protection)

<a id="ws-ux-test-settings-001---test-settings-editor-view"></a>

## WS-UX-TEST-SETTINGS-001 - Test Settings Editor View

**Specification**

- [Test Settings specification](./specs/settings.md#ws-ux-test-settings-001---test-settings-editor-view)

**Backend implementation**

- None.

**Frontend implementation**

- [TestSettingsPageView.svelte](../../webstudio/frontend/src/views/settings/TestSettingsPageView.svelte)
- [BasicSettingsView.svelte](../../webstudio/frontend/src/views/settings/BasicSettingsView.svelte)
- [TestSettingsView.svelte](../../webstudio/frontend/src/views/settings/TestSettingsView.svelte)
- [settingsEditorModel.js](../../webstudio/frontend/src/views/settings/settingsEditorModel.js)
- [settingsSelectOptions.js](../../webstudio/frontend/src/views/settings/settingsSelectOptions.js)

**Unit tests**

- [settingsEditorModel.test.js](../../webstudio/frontend/test/views/settings/settingsEditorModel.test.js)
- [settingsSelectOptions.test.js](../../webstudio/frontend/test/views/settings/settingsSelectOptions.test.js)

**Integration tests**

- None yet.

**Related requirements**

- [WS-FUNC-TEST-SETTINGS-001](#ws-func-test-settings-001---workspace-test-settings-editor)
- [WS-UX-CONFIG-GUARD-001](#ws-ux-config-guard-001---configuration-guard-dialogs-and-save-buttons)

<a id="ws-func-composition-flow-001---composition-file-and-java-flow"></a>

## WS-FUNC-COMPOSITION-FLOW-001 - Composition File and Java Flow

**Specification**

- [Composition Flow and Policies specification](./specs/composition-and-policies.md#ws-func-composition-flow-001---composition-file-and-java-flow)

**Backend implementation**

- [WorkspaceController.java](../../webstudio/src/org/testar/webstudio/api/WorkspaceController.java)
- [WorkspaceService.java](../../webstudio/src/org/testar/webstudio/workspace/WorkspaceService.java)

**Frontend implementation**

- [sourceEditorApi.js](../../webstudio/frontend/src/models/sourceEditorApi.js)
- [sourceEditorModel.js](../../webstudio/frontend/src/models/sourceEditorModel.js)
- [editorSelectionModel.js](../../webstudio/frontend/src/models/editorSelectionModel.js)
- [compositionFlowModel.js](../../webstudio/frontend/src/views/composition/compositionFlowModel.js)
- [App.svelte](../../webstudio/frontend/src/App.svelte)

**Unit tests**

- [sourceEditorApi.test.js](../../webstudio/frontend/test/models/sourceEditorApi.test.js)
- [sourceEditorModel.test.js](../../webstudio/frontend/test/models/sourceEditorModel.test.js)
- [editorSelectionModel.test.js](../../webstudio/frontend/test/models/editorSelectionModel.test.js)
- [compositionFlowModel.test.js](../../webstudio/frontend/test/views/composition/compositionFlowModel.test.js)

**Integration tests**

- None yet.

**Related requirements**

- [WS-UX-COMPOSITION-FLOW-001](#ws-ux-composition-flow-001---composition-flow-editor-view)
- [WS-FUNC-CONFIG-GUARD-001](#ws-func-config-guard-001---configuration-unsaved-change-protection)

<a id="ws-ux-composition-flow-001---composition-flow-editor-view"></a>

## WS-UX-COMPOSITION-FLOW-001 - Composition Flow Editor View

**Specification**

- [Composition Flow and Policies specification](./specs/composition-and-policies.md#ws-ux-composition-flow-001---composition-flow-editor-view)

**Backend implementation**

- None.

**Frontend implementation**

- [TestCompositionPageView.svelte](../../webstudio/frontend/src/views/composition/TestCompositionPageView.svelte)
- [TestCompositionView.svelte](../../webstudio/frontend/src/views/composition/TestCompositionView.svelte)
- [compositionFlowModel.js](../../webstudio/frontend/src/views/composition/compositionFlowModel.js)

**Unit tests**

- [compositionFlowModel.test.js](../../webstudio/frontend/test/views/composition/compositionFlowModel.test.js)
- [editorSelectionModel.test.js](../../webstudio/frontend/test/models/editorSelectionModel.test.js)

**Integration tests**

- None yet.

**Related requirements**

- [WS-FUNC-COMPOSITION-FLOW-001](#ws-func-composition-flow-001---composition-file-and-java-flow)
- [WS-UX-CONFIG-GUARD-001](#ws-ux-config-guard-001---configuration-guard-dialogs-and-save-buttons)

<a id="ws-func-policies-001---policies-file-and-java-policies"></a>

## WS-FUNC-POLICIES-001 - Policies File and Java Policies

**Specification**

- [Composition Flow and Policies specification](./specs/composition-and-policies.md#ws-func-policies-001---policies-file-and-java-policies)

**Backend implementation**

- [WorkspaceController.java](../../webstudio/src/org/testar/webstudio/api/WorkspaceController.java)
- [WorkspaceService.java](../../webstudio/src/org/testar/webstudio/workspace/WorkspaceService.java)

**Frontend implementation**

- [sourceEditorApi.js](../../webstudio/frontend/src/models/sourceEditorApi.js)
- [sourceEditorModel.js](../../webstudio/frontend/src/models/sourceEditorModel.js)
- [policyEditorState.js](../../webstudio/frontend/src/models/policyEditorState.js)
- [editorSelectionModel.js](../../webstudio/frontend/src/models/editorSelectionModel.js)
- [App.svelte](../../webstudio/frontend/src/App.svelte)

**Unit tests**

- [sourceEditorApi.test.js](../../webstudio/frontend/test/models/sourceEditorApi.test.js)
- [sourceEditorModel.test.js](../../webstudio/frontend/test/models/sourceEditorModel.test.js)
- [policyEditorState.test.js](../../webstudio/frontend/test/models/policyEditorState.test.js)
- [editorSelectionModel.test.js](../../webstudio/frontend/test/models/editorSelectionModel.test.js)

**Integration tests**

- None yet.

**Related requirements**

- [WS-UX-POLICIES-001](#ws-ux-policies-001---java-policies-editor-view)
- [WS-FUNC-CONFIG-GUARD-001](#ws-func-config-guard-001---configuration-unsaved-change-protection)

<a id="ws-ux-policies-001---java-policies-editor-view"></a>

## WS-UX-POLICIES-001 - Java Policies Editor View

**Specification**

- [Composition Flow and Policies specification](./specs/composition-and-policies.md#ws-ux-policies-001---java-policies-editor-view)

**Backend implementation**

- None.

**Frontend implementation**

- [TestPoliciesPageView.svelte](../../webstudio/frontend/src/views/policies/TestPoliciesPageView.svelte)
- [TestPoliciesView.svelte](../../webstudio/frontend/src/views/policies/TestPoliciesView.svelte)
- [policyEditorState.js](../../webstudio/frontend/src/models/policyEditorState.js)

**Unit tests**

- [policyEditorState.test.js](../../webstudio/frontend/test/models/policyEditorState.test.js)
- [editorSelectionModel.test.js](../../webstudio/frontend/test/models/editorSelectionModel.test.js)

**Integration tests**

- None yet.

**Related requirements**

- [WS-FUNC-POLICIES-001](#ws-func-policies-001---policies-file-and-java-policies)
- [WS-UX-CONFIG-GUARD-001](#ws-ux-config-guard-001---configuration-guard-dialogs-and-save-buttons)

<a id="ws-func-config-guard-001---configuration-unsaved-change-protection"></a>

## WS-FUNC-CONFIG-GUARD-001 - Configuration Unsaved-Change Protection

**Specification**

- [Configuration Guard specification](./specs/configuration-guard.md#ws-func-config-guard-001---configuration-unsaved-change-protection)

**Backend implementation**

- None.

**Frontend implementation**

- [configurationGuard.js](../../webstudio/frontend/src/app/configurationGuard.js)
- [editorDirtyState.js](../../webstudio/frontend/src/models/editorDirtyState.js)
- [editorModalState.js](../../webstudio/frontend/src/models/editorModalState.js)
- [App.svelte](../../webstudio/frontend/src/App.svelte)

**Unit tests**

- [configurationGuard.test.js](../../webstudio/frontend/test/app/configurationGuard.test.js)
- [editorDirtyState.test.js](../../webstudio/frontend/test/models/editorDirtyState.test.js)
- [editorModalState.test.js](../../webstudio/frontend/test/models/editorModalState.test.js)

**Integration tests**

- None yet.

**Related requirements**

- [WS-UX-CONFIG-GUARD-001](#ws-ux-config-guard-001---configuration-guard-dialogs-and-save-buttons)

<a id="ws-ux-config-guard-001---configuration-guard-dialogs-and-save-buttons"></a>

## WS-UX-CONFIG-GUARD-001 - Configuration Guard Dialogs and Save Buttons

**Specification**

- [Configuration Guard specification](./specs/configuration-guard.md#ws-ux-config-guard-001---configuration-guard-dialogs-and-save-buttons)

**Backend implementation**

- None.

**Frontend implementation**

- [configurationGuard.js](../../webstudio/frontend/src/app/configurationGuard.js)
- [editorDirtyState.js](../../webstudio/frontend/src/models/editorDirtyState.js)
- [editorModalState.js](../../webstudio/frontend/src/models/editorModalState.js)
- [App.svelte](../../webstudio/frontend/src/App.svelte)

**Unit tests**

- [configurationGuard.test.js](../../webstudio/frontend/test/app/configurationGuard.test.js)
- [editorDirtyState.test.js](../../webstudio/frontend/test/models/editorDirtyState.test.js)
- [editorModalState.test.js](../../webstudio/frontend/test/models/editorModalState.test.js)

**Integration tests**

- None yet.

**Related requirements**

- [WS-FUNC-CONFIG-GUARD-001](#ws-func-config-guard-001---configuration-unsaved-change-protection)

<a id="ws-func-test-oracles-001---workspace-scoped-test-oracles"></a>

## WS-FUNC-TEST-ORACLES-001 - Workspace-Scoped Test Oracles

**Specification**

- [Functional specification - Test Oracles View](./specs/test-oracles.md#ws-func-test-oracles-001---workspace-scoped-test-oracles)

**Backend implementation**

- [TestOracleController.java](../../webstudio/src/org/testar/webstudio/api/TestOracleController.java)
- [TestOracleService.java](../../webstudio/src/org/testar/webstudio/testoracle/TestOracleService.java)
- [TestOracleInventoryDto.java](../../webstudio/src/org/testar/webstudio/api/dto/TestOracleInventoryDto.java)
- [TestOracleItemDto.java](../../webstudio/src/org/testar/webstudio/api/dto/TestOracleItemDto.java)
- [TestOracleDslResultDto.java](../../webstudio/src/org/testar/webstudio/api/dto/TestOracleDslResultDto.java)

**Frontend implementation**

- [testOraclesApi.js](../../webstudio/frontend/src/views/oracles/testOraclesApi.js)
- [testOraclesModel.js](../../webstudio/frontend/src/views/oracles/testOraclesModel.js)

**Unit tests**

- [TestOracleServiceTest.java](../../webstudio/test/org/testar/webstudio/testoracle/TestOracleServiceTest.java)
- [testOraclesApi.test.js](../../webstudio/frontend/test/views/oracles/testOraclesApi.test.js)
- [testOraclesModel.test.js](../../webstudio/frontend/test/views/oracles/testOraclesModel.test.js)

**Integration tests**

- None yet.

**Related requirements**

- [WS-UX-TEST-ORACLES-001](#ws-ux-test-oracles-001---test-oracles-configuration-view)

<a id="ws-ux-test-oracles-001---test-oracles-configuration-view"></a>

## WS-UX-TEST-ORACLES-001 - Test Oracles Configuration View

**Specification**

- [UX specification - Test Oracles View](./specs/test-oracles.md#ws-ux-test-oracles-001---test-oracles-configuration-view)

**Backend implementation**

- None.

**Frontend implementation**

- [TestOraclesView.svelte](../../webstudio/frontend/src/views/oracles/TestOraclesView.svelte)
- [DslOracleEditor.svelte](../../webstudio/frontend/src/views/oracles/DslOracleEditor.svelte)
- [testOraclesModel.js](../../webstudio/frontend/src/views/oracles/testOraclesModel.js)

**Unit tests**

- [testOraclesModel.test.js](../../webstudio/frontend/test/views/oracles/testOraclesModel.test.js)

**Integration tests**

- None yet.

**Related requirements**

- [WS-FUNC-TEST-ORACLES-001](#ws-func-test-oracles-001---workspace-scoped-test-oracles)

<a id="ws-func-top-nav-roles-001---role-based-navigation"></a>

## WS-FUNC-TOP-NAV-ROLES-001 - Role-Based Navigation

**Specification**

- [WebStudio Foundations specification](./specs/foundations.md#ws-func-top-nav-roles-001---role-based-navigation)

**Backend implementation**

- None.

**Frontend implementation**

- [webStudioRoles.js](../../webstudio/frontend/src/app/webStudioRoles.js)
- [webStudioNavigation.js](../../webstudio/frontend/src/app/webStudioNavigation.js)
- [App.svelte](../../webstudio/frontend/src/App.svelte)

**Unit tests**

- [webStudioRoles.test.js](../../webstudio/frontend/test/app/webStudioRoles.test.js)
- [webStudioNavigation.test.js](../../webstudio/frontend/test/app/webStudioNavigation.test.js)

**Integration tests**

- None yet.

**Related requirements**

- [WS-UX-TOP-NAV-ROLES-001](#ws-ux-top-nav-roles-001---top-navigation-and-role-selector)
- [WS-FUNC-CONFIG-GUARD-001](#ws-func-config-guard-001---configuration-unsaved-change-protection)
- [WS-FUNC-WORKSPACE-MANAGEMENT-001](#ws-func-workspace-management-001---workspace-creation-and-rename)

<a id="ws-ux-top-nav-roles-001---top-navigation-and-role-selector"></a>

## WS-UX-TOP-NAV-ROLES-001 - Top Navigation and Role Selector

**Specification**

- [WebStudio Foundations specification](./specs/foundations.md#ws-ux-top-nav-roles-001---top-navigation-and-role-selector)

**Backend implementation**

- None.

**Frontend implementation**

- [TopNavigation.svelte](../../webstudio/frontend/src/app/TopNavigation.svelte)
- [webStudioRoles.js](../../webstudio/frontend/src/app/webStudioRoles.js)
- [webStudioNavigation.js](../../webstudio/frontend/src/app/webStudioNavigation.js)
- [App.svelte](../../webstudio/frontend/src/App.svelte)

**Unit tests**

- [webStudioRoles.test.js](../../webstudio/frontend/test/app/webStudioRoles.test.js)
- [webStudioNavigation.test.js](../../webstudio/frontend/test/app/webStudioNavigation.test.js)

**Integration tests**

- None yet.

**Related requirements**

- [WS-FUNC-TOP-NAV-ROLES-001](#ws-func-top-nav-roles-001---role-based-navigation)
- [WS-UX-CONFIG-GUARD-001](#ws-ux-config-guard-001---configuration-guard-dialogs-and-save-buttons)
- [WS-UX-WORKSPACE-MANAGEMENT-001](#ws-ux-workspace-management-001---workspace-management-modal)

<a id="ws-func-workspace-source-editor-001---workspace-document-and-source-editor-api"></a>

## WS-FUNC-WORKSPACE-SOURCE-EDITOR-001 - Workspace Document and Source Editor API

**Specification**

- [WebStudio Foundations specification](./specs/foundations.md#ws-func-workspace-source-editor-001---workspace-document-and-source-editor-api)

**Backend implementation**

- [WorkspaceController.java](../../webstudio/src/org/testar/webstudio/api/WorkspaceController.java)
- [WorkspaceService.java](../../webstudio/src/org/testar/webstudio/workspace/WorkspaceService.java)

**Frontend implementation**

- [sourceEditorApi.js](../../webstudio/frontend/src/models/sourceEditorApi.js)
- [sourceEditorModel.js](../../webstudio/frontend/src/models/sourceEditorModel.js)
- [editorSelectionModel.js](../../webstudio/frontend/src/models/editorSelectionModel.js)
- [App.svelte](../../webstudio/frontend/src/App.svelte)

**Unit tests**

- [sourceEditorApi.test.js](../../webstudio/frontend/test/models/sourceEditorApi.test.js)
- [sourceEditorModel.test.js](../../webstudio/frontend/test/models/sourceEditorModel.test.js)
- [editorSelectionModel.test.js](../../webstudio/frontend/test/models/editorSelectionModel.test.js)

**Integration tests**

- None yet.

**Related requirements**

- [WS-UX-SOURCE-EDITOR-001](#ws-ux-source-editor-001---source-editor-state-and-document-selection)
- [WS-FUNC-COMPOSITION-FLOW-001](#ws-func-composition-flow-001---composition-file-and-java-flow)
- [WS-FUNC-POLICIES-001](#ws-func-policies-001---policies-file-and-java-policies)
- [WS-FUNC-TEST-SETTINGS-001](#ws-func-test-settings-001---workspace-test-settings-editor)

<a id="ws-ux-source-editor-001---source-editor-state-and-document-selection"></a>

## WS-UX-SOURCE-EDITOR-001 - Source Editor State and Document Selection

**Specification**

- [WebStudio Foundations specification](./specs/foundations.md#ws-ux-source-editor-001---source-editor-state-and-document-selection)

**Backend implementation**

- None.

**Frontend implementation**

- [editorSelectionModel.js](../../webstudio/frontend/src/models/editorSelectionModel.js)
- [sourceEditorModel.js](../../webstudio/frontend/src/models/sourceEditorModel.js)
- [App.svelte](../../webstudio/frontend/src/App.svelte)

**Unit tests**

- [editorSelectionModel.test.js](../../webstudio/frontend/test/models/editorSelectionModel.test.js)
- [sourceEditorModel.test.js](../../webstudio/frontend/test/models/sourceEditorModel.test.js)

**Integration tests**

- None yet.

**Related requirements**

- [WS-FUNC-WORKSPACE-SOURCE-EDITOR-001](#ws-func-workspace-source-editor-001---workspace-document-and-source-editor-api)
- [WS-UX-COMPOSITION-FLOW-001](#ws-ux-composition-flow-001---composition-flow-editor-view)
- [WS-UX-POLICIES-001](#ws-ux-policies-001---java-policies-editor-view)
- [WS-UX-TEST-SETTINGS-001](#ws-ux-test-settings-001---test-settings-editor-view)

<a id="ws-func-oracle-dsl-editor-001---rascal-dsl-metadata-and-diagnostics"></a>

## WS-FUNC-ORACLE-DSL-EDITOR-001 - Rascal DSL Metadata and Diagnostics

**Specification**

- [Functional specification - Test Oracles View](./specs/test-oracles.md#ws-func-oracle-dsl-editor-001---rascal-dsl-metadata-and-diagnostics)

**Backend implementation**

- [DslOracleMetadataGenerator.java](../../rascal/src/org/testar/rascal/DslOracleMetadataGenerator.java)
- [DslOracleMetadata.java](../../rascal/src/org/testar/rascal/DslOracleMetadata.java)
- [DslOracleModelField.java](../../rascal/src/org/testar/rascal/DslOracleModelField.java)
- [DslOracleCompiler.java](../../rascal/src/org/testar/rascal/DslOracleCompiler.java)
- [DslOracleDiagnostic.java](../../rascal/src/org/testar/rascal/DslOracleDiagnostic.java)
- [Bridge.rsc](../../rascal/testar-oracle/lang/testar/Bridge.rsc)
- [TestOracleController.java](../../webstudio/src/org/testar/webstudio/api/TestOracleController.java)
- [TestOracleService.java](../../webstudio/src/org/testar/webstudio/testoracle/TestOracleService.java)
- [TestOracleDslDiagnosticDto.java](../../webstudio/src/org/testar/webstudio/api/dto/TestOracleDslDiagnosticDto.java)
- [TestOracleDslResultDto.java](../../webstudio/src/org/testar/webstudio/api/dto/TestOracleDslResultDto.java)

**Frontend implementation**

- [testOraclesApi.js](../../webstudio/frontend/src/views/oracles/testOraclesApi.js)
- [testOraclesModel.js](../../webstudio/frontend/src/views/oracles/testOraclesModel.js)
- [DslOracleEditor.svelte](../../webstudio/frontend/src/views/oracles/DslOracleEditor.svelte)
- [TestOraclesView.svelte](../../webstudio/frontend/src/views/oracles/TestOraclesView.svelte)

**Unit tests**

- [DslOracleMetadataGeneratorTest.java](../../rascal/test/org/testar/rascal/DslOracleMetadataGeneratorTest.java)
- [DslOracleCompilerTest.java](../../rascal/test/org/testar/rascal/DslOracleCompilerTest.java)
- [TestOracleServiceTest.java](../../webstudio/test/org/testar/webstudio/testoracle/TestOracleServiceTest.java)
- [testOraclesApi.test.js](../../webstudio/frontend/test/views/oracles/testOraclesApi.test.js)
- [testOraclesModel.test.js](../../webstudio/frontend/test/views/oracles/testOraclesModel.test.js)

**Integration tests**

- None yet.

**Related requirements**

- [WS-UX-ORACLE-DSL-EDITOR-001](#ws-ux-oracle-dsl-editor-001---monaco-dsl-editor-assistance)
- [WS-FUNC-TEST-ORACLES-001](#ws-func-test-oracles-001---workspace-scoped-test-oracles)
- [WS-FUNC-WORKSPACE-SOURCE-EDITOR-001](#ws-func-workspace-source-editor-001---workspace-document-and-source-editor-api)

<a id="ws-ux-oracle-dsl-editor-001---monaco-dsl-editor-assistance"></a>

## WS-UX-ORACLE-DSL-EDITOR-001 - Monaco DSL Editor Assistance

**Specification**

- [UX specification - Test Oracles View](./specs/test-oracles.md#ws-ux-oracle-dsl-editor-001---monaco-dsl-editor-assistance)

**Backend implementation**

- None.

**Frontend implementation**

- [DslOracleEditor.svelte](../../webstudio/frontend/src/views/oracles/DslOracleEditor.svelte)
- [testOraclesModel.js](../../webstudio/frontend/src/views/oracles/testOraclesModel.js)
- [TestOraclesView.svelte](../../webstudio/frontend/src/views/oracles/TestOraclesView.svelte)

**Unit tests**

- [testOraclesApi.test.js](../../webstudio/frontend/test/views/oracles/testOraclesApi.test.js)
- [testOraclesModel.test.js](../../webstudio/frontend/test/views/oracles/testOraclesModel.test.js)

**Integration tests**

- None yet.

**Related requirements**

- [WS-FUNC-ORACLE-DSL-EDITOR-001](#ws-func-oracle-dsl-editor-001---rascal-dsl-metadata-and-diagnostics)
- [WS-UX-TEST-ORACLES-001](#ws-ux-test-oracles-001---test-oracles-configuration-view)
- [WS-UX-SOURCE-EDITOR-001](#ws-ux-source-editor-001---source-editor-state-and-document-selection)

<a id="ws-func-oracle-java-enablement-001---workspace-java-oracles-and-extendedoracles-enablement"></a>

## WS-FUNC-ORACLE-JAVA-ENABLEMENT-001 - Workspace Java Oracles and ExtendedOracles Enablement

**Specification**

- [Functional specification - Test Oracles View](./specs/test-oracles.md#ws-func-oracle-java-enablement-001---workspace-java-oracles-and-extendedoracles-enablement)

**Backend implementation**

- [TestOracleController.java](../../webstudio/src/org/testar/webstudio/api/TestOracleController.java)
- [TestOracleService.java](../../webstudio/src/org/testar/webstudio/testoracle/TestOracleService.java)
- [OracleSelection.java](../../oracle/src/org/testar/oracle/OracleSelection.java)
- [testar/build.gradle](../../testar/build.gradle)
- [WorkspaceJavaCompileResultDto.java](../../webstudio/src/org/testar/webstudio/api/dto/WorkspaceJavaCompileResultDto.java)
- [WorkspaceJavaCompileDiagnosticDto.java](../../webstudio/src/org/testar/webstudio/api/dto/WorkspaceJavaCompileDiagnosticDto.java)

**Frontend implementation**

- [testOraclesApi.js](../../webstudio/frontend/src/views/oracles/testOraclesApi.js)
- [testOraclesModel.js](../../webstudio/frontend/src/views/oracles/testOraclesModel.js)
- [TestOraclesView.svelte](../../webstudio/frontend/src/views/oracles/TestOraclesView.svelte)
- [App.svelte](../../webstudio/frontend/src/App.svelte)

**Unit tests**

- [TestOracleServiceTest.java](../../webstudio/test/org/testar/webstudio/testoracle/TestOracleServiceTest.java)
- [OracleSelectionTest.java](../../oracle/test/org/testar/oracle/OracleSelectionTest.java)
- [testOraclesApi.test.js](../../webstudio/frontend/test/views/oracles/testOraclesApi.test.js)
- [testOraclesModel.test.js](../../webstudio/frontend/test/views/oracles/testOraclesModel.test.js)

**Acceptance scenarios**

- [Deleting a Java Oracle Clears Enablement](./specs/test-oracles.md#ws-scenario-oracle-java-delete-001---deleting-a-java-oracle-clears-enablement)

**Integration tests**

- None yet.

**Related requirements**

- [WS-UX-ORACLE-JAVA-ENABLEMENT-001](#ws-ux-oracle-java-enablement-001---workspace-java-oracle-management)
- [WS-FUNC-TEST-ORACLES-001](#ws-func-test-oracles-001---workspace-scoped-test-oracles)
- [WS-FUNC-ORACLE-DSL-EDITOR-001](#ws-func-oracle-dsl-editor-001---rascal-dsl-metadata-and-diagnostics)

<a id="ws-ux-oracle-java-enablement-001---workspace-java-oracle-management"></a>

## WS-UX-ORACLE-JAVA-ENABLEMENT-001 - Workspace Java Oracle Management

**Specification**

- [UX specification - Test Oracles View](./specs/test-oracles.md#ws-ux-oracle-java-enablement-001---workspace-java-oracle-management)

**Backend implementation**

- None.

**Frontend implementation**

- [TestOraclesView.svelte](../../webstudio/frontend/src/views/oracles/TestOraclesView.svelte)
- [testOraclesModel.js](../../webstudio/frontend/src/views/oracles/testOraclesModel.js)
- [testOraclesApi.js](../../webstudio/frontend/src/views/oracles/testOraclesApi.js)

**Unit tests**

- [testOraclesModel.test.js](../../webstudio/frontend/test/views/oracles/testOraclesModel.test.js)
- [testOraclesApi.test.js](../../webstudio/frontend/test/views/oracles/testOraclesApi.test.js)

**Integration tests**

- None yet.

**Related requirements**

- [WS-FUNC-ORACLE-JAVA-ENABLEMENT-001](#ws-func-oracle-java-enablement-001---workspace-java-oracles-and-extendedoracles-enablement)
- [WS-UX-TEST-ORACLES-001](#ws-ux-test-oracles-001---test-oracles-configuration-view)
- [WS-UX-SOURCE-EDITOR-001](#ws-ux-source-editor-001---source-editor-state-and-document-selection)
