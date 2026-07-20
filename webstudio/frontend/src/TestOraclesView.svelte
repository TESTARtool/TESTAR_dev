<script>
    import TestSettingsView from "./TestSettingsView.svelte";
    import {
        TEST_ORACLE_PANEL_IDS,
        TEST_ORACLE_PANELS,
        TEST_ORACLE_SETTING_KEYS,
        activeOracleSummaries,
        dslDiagnosticText,
        extendedOracleCheckboxItems,
        javaOracleSourceFiles,
        closedOracleDeleteDialog,
        dslFileNameForCreation,
        oracleDeleteDialogForPath,
        oracleFilesByOrigin,
        testOracleFilePanels,
        testOracleRootPanels
    } from "./testOraclesModel.js";

    export let currentEditorDocument = null;
    export let loading = false;
    export let openOraclePanel;
    export let openTestSettings;
    export let openVisualSettings;
    export let openVisualSettingsGroup;
    export let regexValidationResults = {};
    export let savedTestSettingsContent = "";
    export let saving = false;
    export let selectedOraclePanelId = TEST_ORACLE_PANEL_IDS.ACTIVE;
    export let testOracleInventory = null;
    export let setSettingValue;
    export let selectedEditor = "";
    export let selectedSettingsGroupId = "";
    export let selectedOracleSourceFile = null;
    export let oracleSourceDraftContent = "";
    export let oracleSourceDirty = false;
    export let oracleDslResult = null;
    export let javaCompileResult = null;
    export let compileOracleJavaFile;
    export let createOracleDslFile;
    export let createOracleJavaFile;
    export let deleteOracleDslFile;
    export let deleteOracleJavaFile;
    export let discardOracleSourceChanges;
    export let generateJavaFromOracleDslFile;
    export let loadOracleDslFile;
    export let loadOracleJavaFile;
    export let setOracleSourceDraftContent;
    export let toggleExtendedOracle;
    export let setAllExtendedOraclesEnabled;
    export let restoreSettingDefault;
    export let validateRegexExpression;
    export let workspaceDocument = null;

    $: oracleSummaries = activeOracleSummaries(workspaceDocument);
    $: selectedPanel = TEST_ORACLE_PANELS.find((panel) => panel.id === selectedOraclePanelId) || TEST_ORACLE_PANELS[0];
    $: selectedSettingKeys = TEST_ORACLE_SETTING_KEYS[selectedOraclePanelId] || [];
    $: selectedSettingsGroupIds = selectedOraclePanelId === TEST_ORACLE_PANEL_IDS.WEBDRIVER_CONSOLE
        ? ["webdriver"]
        : ["oracles"];
    $: settingsPanelSelected = selectedSettingKeys.length > 0;
    $: extendedEnablementSelected = selectedOraclePanelId === TEST_ORACLE_PANEL_IDS.EXTENDED_ENABLEMENT;
    $: javaFilesSelected = selectedOraclePanelId === TEST_ORACLE_PANEL_IDS.JAVA_FILES;
    $: dslFilesSelected = selectedOraclePanelId === TEST_ORACLE_PANEL_IDS.DSL_FILES;
    $: rootOraclePanels = testOracleRootPanels();
    $: oracleFilePanels = testOracleFilePanels();
    $: extendedOracleItems = extendedOracleCheckboxItems(testOracleInventory);
    $: javaOracleFiles = javaOracleSourceFiles(testOracleInventory);
    $: dslOracleFiles = oracleFilesByOrigin(testOracleInventory, "DSL_SOURCE");
    let newJavaFilePath = "";
    let newDslFilePath = "";
    let deleteDialog = closedOracleDeleteDialog();

    function normalizedJavaPath(path) {
        const trimmedPath = path.trim();
        if (!trimmedPath) {
            return "";
        }

        return trimmedPath.toLowerCase().endsWith(".java") ? trimmedPath : `${trimmedPath}.java`;
    }

    async function createJavaFile() {
        const path = normalizedJavaPath(newJavaFilePath);
        if (!path) {
            return;
        }

        await createOracleJavaFile(path);
        newJavaFilePath = "";
    }

    async function createDslFile() {
        const path = dslFileNameForCreation(newDslFilePath);
        if (!path) {
            return;
        }

        await createOracleDslFile(path);
        newDslFilePath = "";
    }

    function openDeleteDialog(type, path) {
        if (!path) {
            return;
        }

        deleteDialog = oracleDeleteDialogForPath(path, type);
    }

    function closeDeleteDialog() {
        deleteDialog = closedOracleDeleteDialog();
    }

    function closeDeleteDialogFromBackdrop(event) {
        if (event.target === event.currentTarget) {
            closeDeleteDialog();
        }
    }

    async function confirmDelete() {
        const { type, path } = deleteDialog;
        closeDeleteDialog();
        if (type === "java") {
            await deleteOracleJavaFile(path);
            return;
        }

        if (type === "dsl") {
            await deleteOracleDslFile(path);
        }
    }
</script>

<main class="studio-layout">
    <section class="panel sidebar">
        <section class="sidebar-section">
            <h3>Test Oracles</h3>
            <div class="oracle-sidebar-tree">
                {#each rootOraclePanels as panel}
                    <button
                        type="button"
                        class="source-item"
                        class:selected={selectedOraclePanelId === panel.id}
                        on:click={() => openOraclePanel(panel.id)}
                    >
                        <span>{panel.label}</span>
                    </button>
                    {#if panel.id === TEST_ORACLE_PANEL_IDS.EXTENDED_ENABLEMENT}
                        <div class="oracle-sidebar-nav">
                            {#each oracleFilePanels as filePanel}
                                <button
                                    type="button"
                                    class="source-item oracle-nav-item"
                                    class:selected={selectedOraclePanelId === filePanel.id}
                                    on:click={() => openOraclePanel(filePanel.id)}
                                >
                                    <span>{filePanel.label}</span>
                                </button>
                            {/each}
                        </div>
                    {/if}
                {/each}
            </div>
        </section>
    </section>

    <section class="panel content-panel">
        {#if loading}
            <p>Loading workspace...</p>
        {:else if selectedOraclePanelId === TEST_ORACLE_PANEL_IDS.ACTIVE}
            <section class="editor-section oracle-panel-section">
                <div class="section-header">
                    <div>
                        <h2>Active Oracles Summary</h2>
                        <p>Read-only summary of oracle mechanisms configured for the selected workspace.</p>
                    </div>
                </div>

                <section class="oracle-summary-grid top-gap">
                    {#each oracleSummaries as oracleSummary}
                        <article class="oracle-summary-card" class:oracle-summary-active={oracleSummary.active}>
                            <div>
                                <strong>{oracleSummary.label}</strong>
                                <span>{oracleSummary.active ? "Configured" : "Inactive"}</span>
                            </div>
                            <p>{oracleSummary.detail}</p>
                        </article>
                    {/each}
                </section>
            </section>
        {:else if settingsPanelSelected && !extendedEnablementSelected}
            <TestSettingsView
                allowedSettingsGroupIds={selectedSettingsGroupIds}
                allowedSettingKeys={selectedSettingKeys}
                allowSettingsFileToggle={false}
                currentEditorDocument={currentEditorDocument}
                loading={loading}
                openTestSettings={openTestSettings}
                openVisualSettings={openVisualSettings}
                openVisualSettingsGroup={openVisualSettingsGroup}
                regexValidationResults={regexValidationResults}
                renderContent={true}
                renderSidebar={false}
                savedTestSettingsContent={savedTestSettingsContent}
                saving={saving}
                setSettingValue={setSettingValue}
                selectedEditor={selectedEditor}
                selectedSettingsGroupId={selectedSettingsGroupId}
                settingsDescription={selectedPanel.description}
                showSettingsSearch={false}
                restoreSettingDefault={restoreSettingDefault}
                validateRegexExpression={validateRegexExpression}
                workspaceDocument={workspaceDocument}
            />
        {:else if extendedEnablementSelected}
            <section class="editor-section oracle-panel-section">
                <div class="section-header">
                    <div>
                        <h2>Enable Extended Oracles</h2>
                        <p>Enable or disable available Java oracles. Saving updates the ExtendedOracles setting.</p>
                    </div>
                    {#if currentEditorDocument}
                        <button
                            type="button"
                            disabled={saving || !currentEditorDocument.dirty}
                            on:click={currentEditorDocument.save}
                        >
                            {currentEditorDocument.saveLabel}
                        </button>
                    {/if}
                </div>

                {#if extendedOracleItems.length > 0}
                    <div class="button-row oracle-bulk-actions">
                        <button type="button" class="secondary" on:click={() => setAllExtendedOraclesEnabled(true)} disabled={saving}>
                            Enable All
                        </button>
                        <button type="button" class="secondary" on:click={() => setAllExtendedOraclesEnabled(false)} disabled={saving}>
                            Disable All
                        </button>
                    </div>
                {/if}

                <section class="oracle-inventory-list top-gap">
                    {#if extendedOracleItems.length === 0}
                        <div class="empty-state">No Java oracles are available for this workspace.</div>
                    {:else}
                        {#each extendedOracleItems as oracleItem}
                            <label class="oracle-inventory-row">
                                <input
                                    type="checkbox"
                                    checked={oracleItem.active}
                                    disabled={saving}
                                    on:change={(event) => toggleExtendedOracle(oracleItem.name, event.currentTarget.checked)}
                                />
                                <span>
                                    <strong>{oracleItem.name}</strong>
                                    <small>{oracleItem.sourceLabel}{oracleItem.overridesBuiltIn ? " - overrides built-in" : ""}</small>
                                </span>
                            </label>
                        {/each}
                    {/if}
                </section>
            </section>
        {:else if javaFilesSelected}
            <section class="editor-section oracle-panel-section">
                <div class="section-header">
                    <div>
                        <h2>Java Oracle Files</h2>
                        <p>Workspace Java oracle files generated by DSL or written manually.</p>
                    </div>
                    <div class="button-row">
                        <input
                            class="oracle-new-file-input"
                            bind:value={newJavaFilePath}
                            placeholder="NewOracle.java"
                            disabled={saving}
                        />
                        <button type="button" class="secondary" on:click={createJavaFile} disabled={saving || !newJavaFilePath.trim()}>
                            New Java File
                        </button>
                    </div>
                </div>

                <section class="oracle-file-layout top-gap">
                    <div class="oracle-file-browser">
                        {#if javaOracleFiles.length === 0}
                            <div class="empty-state">No workspace Java oracle files are available yet.</div>
                        {:else}
                            {#each javaOracleFiles as oracleFile}
                                <button
                                    type="button"
                                    class="source-item"
                                    class:selected={selectedOracleSourceFile?.location === oracleFile.path}
                                    on:click={() => loadOracleJavaFile(oracleFile)}
                                    disabled={saving}
                                >
                                    <span>{oracleFile.path}</span>
                                </button>
                            {/each}
                        {/if}
                    </div>

                    <div class="oracle-source-editor">
                        {#if selectedOracleSourceFile?.category === "java-oracle"}
                            <div class="section-header">
                                <div>
                                    <h3>{selectedOracleSourceFile.name}</h3>
                                    <p>{selectedOracleSourceFile.location}</p>
                                </div>
                                <div class="button-row">
                                    <button type="button" on:click={compileOracleJavaFile} disabled={saving}>
                                        Save and Compile
                                    </button>
                                    <button type="button" class="secondary" on:click={discardOracleSourceChanges} disabled={saving || !oracleSourceDirty}>
                                        Discard
                                    </button>
                                    <button type="button" class="secondary danger-button" on:click={() => openDeleteDialog("java", selectedOracleSourceFile.location)} disabled={saving}>
                                        <span class="result-delete-icon" aria-hidden="true"></span>
                                        Delete
                                    </button>
                                </div>
                            </div>
                            <textarea
                                class="oracle-source-textarea"
                                value={oracleSourceDraftContent}
                                on:input={(event) => setOracleSourceDraftContent(event.currentTarget.value)}
                                spellcheck="false"
                            ></textarea>
                            <section class="compile-results-panel" class:compile-results-success={javaCompileResult?.scope === "oracle-source" && javaCompileResult.success} class:compile-results-failed={javaCompileResult?.scope === "oracle-source" && !javaCompileResult.success} class:compile-results-idle={javaCompileResult?.scope !== "oracle-source"}>
                                {#if javaCompileResult?.scope === "oracle-source" && javaCompileResult.diagnostics?.length > 0}
                                    <div class="compile-diagnostics-list">
                                        {#each javaCompileResult.diagnostics as diagnostic}
                                            <article class="compile-diagnostic-row">
                                                <div>
                                                    <strong>{diagnostic.fileName || javaCompileResult.targetName}</strong>
                                                    <span>{diagnostic.relativePath}</span>
                                                </div>
                                                <small>{diagnostic.severity} {diagnostic.line > 0 ? `L${diagnostic.line}` : ""}{diagnostic.column > 0 ? `:${diagnostic.column}` : ""}</small>
                                                <p>{diagnostic.message}</p>
                                            </article>
                                        {/each}
                                    </div>
                                {:else if javaCompileResult?.scope === "oracle-source" && javaCompileResult.success}
                                    <div class="compile-results-empty">
                                        <p>Java oracle compilation succeeded for the current source file.</p>
                                    </div>
                                {:else}
                                    <div class="compile-results-empty">
                                        <p>Run "Save and Compile" to validate the current Java oracle source file.</p>
                                    </div>
                                {/if}
                            </section>
                        {:else}
                            <div class="empty-state">Select a Java oracle file to edit it.</div>
                        {/if}
                    </div>
                </section>
            </section>
        {:else if dslFilesSelected}
            <section class="editor-section oracle-panel-section">
                <div class="section-header">
                    <div>
                        <h2>DSL Oracle Files</h2>
                        <p>Create and edit root-level .testar DSL oracle files for this workspace.</p>
                    </div>
                    <div class="button-row">
                        <input
                            class="oracle-new-file-input"
                            bind:value={newDslFilePath}
                            placeholder="new_oracle.testar"
                            disabled={saving}
                        />
                        <button type="button" class="secondary" on:click={createDslFile} disabled={saving || !newDslFilePath.trim()}>
                            New DSL File
                        </button>
                    </div>
                </div>

                <section class="oracle-file-layout top-gap">
                    <div class="oracle-file-browser">
                        {#if dslOracleFiles.length === 0}
                            <div class="empty-state">No DSL oracle files are available yet.</div>
                        {:else}
                            {#each dslOracleFiles as oracleFile}
                                <button
                                    type="button"
                                    class="source-item"
                                    class:selected={selectedOracleSourceFile?.location === oracleFile.path}
                                    on:click={() => loadOracleDslFile(oracleFile)}
                                    disabled={saving}
                                >
                                    <span>{oracleFile.path}</span>
                                </button>
                            {/each}
                        {/if}
                    </div>

                    <div class="oracle-source-editor">
                        {#if selectedOracleSourceFile?.category === "dsl-oracle"}
                            <div class="section-header">
                                <div>
                                    <h3>{selectedOracleSourceFile.name}</h3>
                                    <p>{selectedOracleSourceFile.location}</p>
                                </div>
                                <div class="button-row">
                                    <button type="button" on:click={generateJavaFromOracleDslFile} disabled={saving}>
                                        Save and Generate Java-DSL
                                    </button>
                                    <button type="button" class="secondary" on:click={discardOracleSourceChanges} disabled={saving || !oracleSourceDirty}>
                                        Discard
                                    </button>
                                    <button type="button" class="secondary danger-button" on:click={() => openDeleteDialog("dsl", selectedOracleSourceFile.location)} disabled={saving}>
                                        <span class="result-delete-icon" aria-hidden="true"></span>
                                        Delete
                                    </button>
                                </div>
                            </div>
                            <textarea
                                class="oracle-source-textarea"
                                value={oracleSourceDraftContent}
                                on:input={(event) => setOracleSourceDraftContent(event.currentTarget.value)}
                                spellcheck="false"
                            ></textarea>
                            <section
                                class="oracle-dsl-feedback-panel"
                                class:oracle-dsl-feedback-success={oracleDslResult?.success}
                                class:oracle-dsl-feedback-failed={oracleDslResult && !oracleDslResult.success}
                            >
                                <div class="oracle-dsl-feedback-header">
                                    <strong>DSL Feedback</strong>
                                    <span>{oracleDslResult?.message || "Run Save and Generate Java-DSL to check this oracle."}</span>
                                </div>
                                {#if oracleDslResult?.generatedJavaPath}
                                    <p>Generated Java: {oracleDslResult.generatedJavaPath}</p>
                                {/if}
                                {#if oracleDslResult?.diagnostics?.length > 0}
                                    <div class="oracle-dsl-diagnostics">
                                        {#each oracleDslResult.diagnostics as diagnostic}
                                            <p>
                                                {dslDiagnosticText(diagnostic)}
                                            </p>
                                        {/each}
                                    </div>
                                {/if}
                            </section>
                        {:else}
                            <div class="empty-state">Select a DSL oracle file to edit it.</div>
                        {/if}
                    </div>
                </section>
            </section>
        {/if}
    </section>
</main>

{#if deleteDialog.open}
    <div class="composition-modal-backdrop" role="presentation" on:click={closeDeleteDialogFromBackdrop}>
        <div class="composition-modal state-model-dialog" role="dialog" tabindex="-1" aria-modal="true" aria-labelledby="delete-oracle-title">
            <div class="composition-modal-header">
                <div>
                    <span class="flow-node-kicker">Confirm Delete</span>
                    <h3 id="delete-oracle-title">{deleteDialog.title}</h3>
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
