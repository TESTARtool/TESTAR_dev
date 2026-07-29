<script>
    // Implements WS-UX-WORKSPACE-MANAGEMENT-001: modal contract for create/rename workspace actions.
    export let open = false;
    export let activeTab = "create";
    export let workspaces = [];
    export let selectedWorkspaceName = "";
    export let createDraft = {
        name: "",
        baseWorkspace: "",
        copyTestGoals: true,
        copyOracles: true
    };
    export let renameDraft = {
        name: ""
    };
    export let createValidation = {
        valid: false,
        message: ""
    };
    export let renameValidation = {
        valid: false,
        message: ""
    };
    export let error = "";
    export let saving = false;
    export let onClose = () => {};
    export let onCreate = () => {};
    export let onRename = () => {};
    export let onTabChange = () => {};
    export let onCreateDraftChange = () => {};
    export let onRenameDraftChange = () => {};

    function closeFromBackdrop(event) {
        if (event.currentTarget === event.target) {
            onClose();
        }
    }

    function updateCreateDraft(changes) {
        onCreateDraftChange({
            ...createDraft,
            ...changes
        });
    }

    function updateRenameDraft(changes) {
        onRenameDraftChange({
            ...renameDraft,
            ...changes
        });
    }
</script>

{#if open}
    <div class="composition-modal-backdrop" role="presentation" on:click={closeFromBackdrop}>
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
                    class:secondary={activeTab !== "create"}
                    on:click={() => onTabChange("create")}
                    disabled={saving}
                >
                    Create Workspace
                </button>
                <button
                    type="button"
                    class:secondary={activeTab !== "rename"}
                    on:click={() => onTabChange("rename")}
                    disabled={saving || !selectedWorkspaceName}
                >
                    Rename Workspace
                </button>
            </div>
            <div class="composition-modal-body workspace-management-form">
                {#if activeTab === "create"}
                    <label>
                        <span>Workspace name</span>
                        <input
                            type="text"
                            value={createDraft.name}
                            on:input={(event) => updateCreateDraft({ name: event.currentTarget.value })}
                            placeholder="platform_application (e.g., webdriver_parabank)"
                            disabled={saving}
                        />
                    </label>
                    <label>
                        <span>Base workspace</span>
                        <select
                            value={createDraft.baseWorkspace}
                            on:change={(event) => updateCreateDraft({ baseWorkspace: event.currentTarget.value })}
                            disabled={saving}
                        >
                            {#each workspaces as workspace}
                                <option value={workspace.name}>{workspace.name}</option>
                            {/each}
                        </select>
                    </label>
                    <label class="workspace-management-checkbox">
                        <input
                            type="checkbox"
                            checked={createDraft.copyTestGoals}
                            on:change={(event) => updateCreateDraft({ copyTestGoals: event.currentTarget.checked })}
                            disabled={saving}
                        />
                        <span>Copy Test Goals from base workspace</span>
                    </label>
                    <label class="workspace-management-checkbox">
                        <input
                            type="checkbox"
                            checked={createDraft.copyOracles}
                            on:change={(event) => updateCreateDraft({ copyOracles: event.currentTarget.checked })}
                            disabled={saving}
                        />
                        <span>Copy Java and DSL Oracles from base workspace</span>
                    </label>
                    <p class="workspace-management-note">
                        Java and DSL oracle files live in the workspace oracles directory. Uncheck this to create an empty oracle workspace.
                    </p>
                    {#if error || createValidation.message}
                        <p class:settings-validation-invalid={error || !createValidation.valid}>
                            {error || createValidation.message}
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
                            value={renameDraft.name}
                            on:input={(event) => updateRenameDraft({ name: event.currentTarget.value })}
                            placeholder="platform_application (e.g., webdriver_parabank)"
                            disabled={saving}
                        />
                    </label>
                    <p class="workspace-management-note">
                        Existing output results for {selectedWorkspaceName} will move to the renamed workspace.
                    </p>
                    {#if error || renameValidation.message}
                        <p class:settings-validation-invalid={error || !renameValidation.valid}>
                            {error || renameValidation.message}
                        </p>
                    {/if}
                {/if}
            </div>
            <div class="composition-modal-actions">
                {#if activeTab === "create"}
                    <button type="button" on:click={onCreate} disabled={saving || !createValidation.valid}>
                        Create
                    </button>
                {:else}
                    <button type="button" on:click={onRename} disabled={saving || !renameValidation.valid}>
                        Rename
                    </button>
                {/if}
                <button type="button" class="secondary" on:click={onClose} disabled={saving}>
                    Discard
                </button>
            </div>
        </div>
    </div>
{/if}
