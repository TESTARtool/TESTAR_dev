<script>
    import StateModelDialog from "./StateModelDialog.svelte";
    import UnsavedChangesDialog from "./UnsavedChangesDialog.svelte";
    import WorkspaceManagementDialog from "./WorkspaceManagementDialog.svelte";

    export let createDraft;
    export let createValidation;
    export let error = "";
    export let message = "";
    export let openWorkspaceDialog = false;
    export let renameDraft;
    export let renameValidation;
    export let saving = false;
    export let selectedWorkspaceName = "";
    export let stateModelDialog;
    export let unsavedSettingsDialog;
    export let workspaceManagementTab = "create";
    export let workspaces = [];
    export let onCloseStateModelDialog = () => {};
    export let onCloseUnsavedDialog = () => {};
    export let onCloseWorkspaceDialog = () => {};
    export let onCreateWorkspace = () => {};
    export let onDiscardUnsavedChanges = () => {};
    export let onOpenStateModelExternalTab = () => {};
    export let onRenameWorkspace = () => {};
    export let onSaveUnsavedChanges = () => {};
    export let onStopStateModelAnalysis = () => {};
    export let onWorkspaceCreateDraftChange = () => {};
    export let onWorkspaceManagementTabChange = () => {};
    export let onWorkspaceRenameDraftChange = () => {};
</script>

{#if message}
    <div class="toast-message">
        {message}
    </div>
{/if}

<WorkspaceManagementDialog
    open={openWorkspaceDialog}
    activeTab={workspaceManagementTab}
    workspaces={workspaces}
    selectedWorkspaceName={selectedWorkspaceName}
    createDraft={createDraft}
    renameDraft={renameDraft}
    createValidation={createValidation}
    renameValidation={renameValidation}
    error={error}
    saving={saving}
    onClose={onCloseWorkspaceDialog}
    onCreate={onCreateWorkspace}
    onRename={onRenameWorkspace}
    onTabChange={onWorkspaceManagementTabChange}
    onCreateDraftChange={onWorkspaceCreateDraftChange}
    onRenameDraftChange={onWorkspaceRenameDraftChange}
/>

<!-- Implements WS-UX-STATE-MODEL-001: wires state model dialog actions from App state. -->
<StateModelDialog
    dialog={stateModelDialog}
    saving={saving}
    onOpen={onOpenStateModelExternalTab}
    onStop={onStopStateModelAnalysis}
    onClose={onCloseStateModelDialog}
/>

<UnsavedChangesDialog
    dialog={unsavedSettingsDialog}
    saving={saving}
    onSave={onSaveUnsavedChanges}
    onDiscard={onDiscardUnsavedChanges}
    onCancel={onCloseUnsavedDialog}
/>
