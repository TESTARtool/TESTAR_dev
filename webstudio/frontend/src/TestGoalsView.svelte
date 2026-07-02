<script>
    import {
        flattenTestGoalTree,
        isExecutableTestGoal,
        closedTestGoalDeleteDialog,
        testGoalDeleteDialogForPath,
        testGoalKindLabel,
        testGoalWorkspaceRootLabel
    } from "./testGoalsModel.js";

    export let testGoalTree = null;
    export let selectedTestGoalFile = null;
    export let testGoalDraftContent = "";
    export let testGoalDirty = false;
    export let saving = false;
    export let createTestGoalFile;
    export let createTestGoalFolder;
    export let deleteTestGoalPath;
    export let discardTestGoalChanges;
    export let loadTestGoalFile;
    export let saveTestGoalFile;
    export let setTestGoalDraftContent;
    export let selectedWorkspaceName = "";
    export let selectedTestGoalFolderPath = "";
    export let selectTestGoalFolder;

    let newFolderName = "";
    let newGoalName = "";
    let deleteDialog = closedTestGoalDeleteDialog();

    $: treeItems = flattenTestGoalTree(testGoalTree);
    $: rootFolderLabel = testGoalWorkspaceRootLabel(selectedWorkspaceName);
    $: selectedFolderLabel = selectedTestGoalFolderPath === "" ? rootFolderLabel : selectedTestGoalFolderPath;
    $: canCreateInSelectedFolder = selectedTestGoalFolderPath !== null;

    function childPath(folderPath, childName) {
        const normalizedChildName = childName.trim();
        if (!folderPath) {
            return normalizedChildName;
        }

        return `${folderPath}/${normalizedChildName}`;
    }

    function yamlGoalFileName(fileName) {
        const trimmedFileName = fileName.trim();
        if (trimmedFileName.endsWith(".yaml") || trimmedFileName.endsWith(".yml")) {
            return trimmedFileName;
        }

        return `${trimmedFileName}.yaml`;
    }

    async function createFile() {
        if (!canCreateInSelectedFolder || !newGoalName.trim()) {
            return;
        }

        await createTestGoalFile(childPath(selectedTestGoalFolderPath, yamlGoalFileName(newGoalName)));
        newGoalName = "";
    }

    async function createFolder() {
        if (!canCreateInSelectedFolder || !newFolderName.trim()) {
            return;
        }

        await createTestGoalFolder(childPath(selectedTestGoalFolderPath, newFolderName));
        newFolderName = "";
    }

    function closeDeleteDialog() {
        deleteDialog = closedTestGoalDeleteDialog();
    }

    function closeDeleteDialogFromBackdrop(event) {
        if (event.currentTarget === event.target) {
            closeDeleteDialog();
        }
    }

    async function confirmDelete() {
        const path = deleteDialog.path;
        closeDeleteDialog();
        await deleteTestGoalPath(path);
    }

    function deleteSelectedFile() {
        if (!selectedTestGoalFile?.path) {
            return;
        }

        deleteDialog = testGoalDeleteDialogForPath(selectedTestGoalFile.path, "file");
    }

    function deleteTreeItem(item) {
        if (!item?.path || item.path === "") {
            return;
        }

        deleteDialog = testGoalDeleteDialogForPath(item.path, item.type);
    }

    function selectTreeItem(item) {
        if (item.type === "folder") {
            selectTestGoalFolder(item.path);
            return;
        }

        if (item.type === "file" && isExecutableTestGoal(item)) {
            loadTestGoalFile(item);
        }
    }
</script>

<section class="panel panel-wide test-goals-page">
    <div class="status-panel-header">
        <div class="status-panel-title-group">
            <p class="eyebrow">Test Goals</p>
        </div>
    </div>

    <div class="test-goals-layout">
        <section class="status-card test-goals-browser">
            <div class="test-goals-card-header">
                <div>
                    <h3>Goal Files</h3>
                    <p>Select a folder to create children, or select a YAML goal to edit it.</p>
                </div>
            </div>

            <div class="test-goals-tree">
                {#if treeItems.length === 0}
                    <div class="empty-state">
                        No test goals are available yet.
                    </div>
                {:else}
                    {#each treeItems as item}
                        <div class="test-goals-tree-row" style={`--goal-depth: ${item.depth};`}>
                            <button
                                type="button"
                                class:test-goal-selected={selectedTestGoalFile?.path === item.path}
                                class:test-goal-folder-selected={item.type === "folder" && selectedTestGoalFolderPath === item.path}
                                class:test-goal-folder={item.type === "folder"}
                                class:test-goal-reference={item.type === "file" && !isExecutableTestGoal(item)}
                                on:click={() => selectTreeItem(item)}
                                disabled={item.type === "file" && !isExecutableTestGoal(item)}
                            >
                                <span>{item.path === "" ? "Root" : item.type === "folder" ? "Folder" : isExecutableTestGoal(item) ? "Goal" : "File"}</span>
                                <strong>{item.path === "" ? rootFolderLabel : item.name}</strong>
                                <small>{testGoalKindLabel(item)}</small>
                            </button>
                            {#if item.path !== ""}
                                <button
                                    type="button"
                                    class="secondary test-goal-delete"
                                    on:click={() => deleteTreeItem(item)}
                                    disabled={saving}
                                    aria-label={`Delete ${item.path}`}
                                >
                                    <span class="result-delete-icon" aria-hidden="true"></span>
                                    Delete
                                </button>
                            {:else}
                                <span class="test-goal-root-lock">Root</span>
                            {/if}
                        </div>
                    {/each}
                {/if}
            </div>
        </section>

        <section class="status-card test-goals-editor">
            <div class="test-goals-card-header">
                <div>
                    <h3>{selectedTestGoalFile?.name || (canCreateInSelectedFolder ? "Selected Folder" : "No Goal Selected")}</h3>
                    <p>
                        {#if selectedTestGoalFile}
                            {selectedTestGoalFile.path}
                        {:else if canCreateInSelectedFolder}
                            {selectedFolderLabel}
                        {:else}
                            Select a folder to create goals, or select a YAML goal to edit it.
                        {/if}
                    </p>
                </div>
                {#if selectedTestGoalFile?.executable}
                    <span class="run-outcome-item-status">YAML</span>
                {:else if selectedTestGoalFile}
                    <span class="run-outcome-item-status muted">Reference</span>
                {/if}
            </div>

            {#if selectedTestGoalFile}
                <textarea
                    class="test-goals-textarea"
                    value={testGoalDraftContent}
                    on:input={(event) => setTestGoalDraftContent(event.currentTarget.value)}
                    spellcheck="false"
                ></textarea>

                <div class="test-goals-editor-actions">
                    <div>
                        {#if testGoalDirty}
                            <span class="settings-validation-message settings-validation-invalid">Unsaved changes</span>
                        {:else}
                            <span class="settings-validation-message settings-validation-valid">Saved</span>
                        {/if}
                    </div>
                    <div class="button-row">
                        <button type="button" on:click={saveTestGoalFile} disabled={saving || !testGoalDirty}>
                            Save
                        </button>
                        <button type="button" class="secondary" on:click={discardTestGoalChanges} disabled={saving || !testGoalDirty}>
                            Discard
                        </button>
                        <button type="button" class="secondary danger-button" on:click={deleteSelectedFile} disabled={saving}>
                            <span class="result-delete-icon" aria-hidden="true"></span>
                            Delete File
                        </button>
                    </div>
                </div>
            {:else}
                {#if canCreateInSelectedFolder}
                    <div class="test-goals-create-panel">
                        <span>Selected folder</span>
                        <strong>{selectedFolderLabel}</strong>
                        <div class="test-goals-create-row">
                            <input
                                type="text"
                                placeholder="New folder name"
                                value={newFolderName}
                                on:input={(event) => newFolderName = event.currentTarget.value}
                            />
                            <button type="button" class="secondary" on:click={createFolder} disabled={saving || !newFolderName.trim()}>
                                New Folder
                            </button>
                        </div>
                        <div class="test-goals-create-row">
                            <input
                                type="text"
                                placeholder="New YAML goal name"
                                value={newGoalName}
                                on:input={(event) => newGoalName = event.currentTarget.value}
                            />
                            <button type="button" on:click={createFile} disabled={saving || !newGoalName.trim()}>
                                New YAML Goal
                            </button>
                        </div>
                    </div>
                {:else}
                    <div class="empty-state test-goals-empty-editor">
                        Select a folder to create a test goal, or select a YAML goal to edit it.
                    </div>
                {/if}
            {/if}
        </section>
    </div>

    {#if deleteDialog.open}
        <div class="composition-modal-backdrop" role="presentation" on:click={closeDeleteDialogFromBackdrop}>
            <div class="composition-modal state-model-dialog" role="dialog" tabindex="-1" aria-modal="true" aria-labelledby="delete-test-goal-title">
                <div class="composition-modal-header">
                    <div>
                        <span class="flow-node-kicker">Confirm Delete</span>
                        <h3 id="delete-test-goal-title">{deleteDialog.title}</h3>
                    </div>
                </div>
                <div class="composition-modal-body">
                    <p>{deleteDialog.message}</p>
                    <strong class="results-delete-dialog-target">{deleteDialog.path}</strong>
                    <div class="button-row results-delete-dialog-actions">
                        <button type="button" class="secondary" on:click={closeDeleteDialog}>
                            Cancel
                        </button>
                        <button type="button" class="danger-button" on:click={confirmDelete} disabled={saving}>
                            <span class="result-delete-icon" aria-hidden="true"></span>
                            Delete
                        </button>
                    </div>
                </div>
            </div>
        </div>
    {/if}
</section>
