<script>
    import TestCompositionView from "./TestCompositionView.svelte";
    import TestSettingsView from "./TestSettingsView.svelte";
    import {
        TEST_ORACLE_PANEL_IDS,
        TEST_ORACLE_PANELS,
        TEST_ORACLE_SETTING_KEYS,
        activeOracleSummaries,
        oracleCompositionNodes
    } from "./testOraclesModel.js";

    export let compositionFlowNodes = [];
    export let currentEditorDocument = null;
    export let compileSelectedJavaSource;
    export let compileWorkspaceProfile;
    export let createCompositionModuleSource;
    export let javaCompileResult = null;
    export let closeCompositionSourceEditor;
    export let loading = false;
    export let openOraclePanel;
    export let openTestSettings;
    export let openVisualSettings;
    export let openVisualSettingsGroup;
    export let regexValidationResults = {};
    export let savedTestSettingsContent = "";
    export let saving = false;
    export let selectedOraclePanelId = TEST_ORACLE_PANEL_IDS.ACTIVE;
    export let setSettingValue;
    export let selectCompositionFlowNode;
    export let selectedCompositionFlowNode = null;
    export let selectedEditor = "";
    export let selectedSettingsGroupId = "";
    export let selectedSourceFile = null;
    export let selectedSourceSavedContent = "";
    export let restoreSettingDefault;
    export let validateRegexExpression;
    export let workspaceDocument = null;

    $: basicOracleNodes = oracleCompositionNodes(compositionFlowNodes);
    $: oracleCompositionNode = basicOracleNodes[0] || null;
    $: oracleSummaries = activeOracleSummaries(workspaceDocument, oracleCompositionNode);
    $: selectedPanel = TEST_ORACLE_PANELS.find((panel) => panel.id === selectedOraclePanelId) || TEST_ORACLE_PANELS[0];
    $: selectedSettingKeys = TEST_ORACLE_SETTING_KEYS[selectedOraclePanelId] || [];
    $: selectedSettingsGroupIds = selectedOraclePanelId === TEST_ORACLE_PANEL_IDS.WEBDRIVER_CONSOLE
        ? ["webdriver"]
        : ["oracles"];
    $: settingsPanelSelected = selectedSettingKeys.length > 0;
    $: javaCompositionSelected = selectedOraclePanelId === TEST_ORACLE_PANEL_IDS.JAVA_COMPOSITION;
</script>

<main class="studio-layout">
    <section class="panel sidebar">
        <section class="sidebar-section">
            <h3>Test Oracles</h3>
            <div class="source-list">
                {#each TEST_ORACLE_PANELS as panel}
                    <button
                        type="button"
                        class="source-item"
                        class:selected={selectedOraclePanelId === panel.id}
                        on:click={() => openOraclePanel(panel.id)}
                    >
                        <span>{panel.label}</span>
                    </button>
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
        {:else if settingsPanelSelected}
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
                restoreSettingDefault={restoreSettingDefault}
                validateRegexExpression={validateRegexExpression}
                workspaceDocument={workspaceDocument}
            />
        {:else if javaCompositionSelected}
            <TestCompositionView
                compositionFlowNodes={basicOracleNodes}
                compositionFlowTitle="Java Oracle Composition"
                compositionFlowDescription="Inspect and edit the Custom Oracle Services composition node for this workspace."
                currentEditorDocument={currentEditorDocument}
                compileSelectedJavaSource={compileSelectedJavaSource}
                compileWorkspaceProfile={compileWorkspaceProfile}
                createCompositionModuleSource={createCompositionModuleSource}
                javaCompileResult={javaCompileResult}
                closeCompositionSourceEditor={closeCompositionSourceEditor}
                renderContent={true}
                renderSidebar={false}
                saving={saving}
                selectCompositionFlowNode={selectCompositionFlowNode}
                selectedCompositionFlowNode={selectedCompositionFlowNode}
                selectedEditor={selectedEditor}
                selectedSourceFile={selectedSourceFile}
                selectedSourceSavedContent={selectedSourceSavedContent}
                showCompositionProfileCompile={true}
                singleNodeFlow={true}
                workspaceDocument={workspaceDocument}
            />
        {/if}
    </section>
</main>
