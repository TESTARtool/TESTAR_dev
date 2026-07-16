<script>
    import TestCompositionView from "./TestCompositionView.svelte";
    import TestSettingsView from "./TestSettingsView.svelte";
    import { isOracleCompositionSelected, oracleCompositionNodes } from "./basicConfigurationModel.js";
    import { BASIC_ROLE_SETTINGS_GROUP_IDS } from "./webStudioRoles.js";

    export let compositionFlowNodes = [];
    export let currentEditorDocument = null;
    export let compileSelectedJavaSource;
    export let compileWorkspaceProfile;
    export let createCompositionModuleSource;
    export let javaCompileResult = null;
    export let closeCompositionSourceEditor;
    export let openBasicOracleComposition;
    export let loading = false;
    export let openTestSettings;
    export let openVisualSettings;
    export let openVisualSettingsGroup;
    export let regexValidationResults = {};
    export let savedTestSettingsContent = "";
    export let saving = false;
    export let setSettingValue;
    export let selectCompositionFlowNode;
    export let selectedEditor = "";
    export let selectedCompositionFlowNode = null;
    export let selectedSettingsGroupId = "";
    export let selectedSourceFile = null;
    export let selectedSourceSavedContent = "";
    export let restoreSettingDefault;
    export let validateRegexExpression;
    export let workspaceDocument = null;

    $: basicOracleNodes = oracleCompositionNodes(compositionFlowNodes);
    $: oracleCompositionSelected = isOracleCompositionSelected(selectedEditor, selectedCompositionFlowNode);
</script>

<main class="studio-layout">
    <section class="panel sidebar">
        <section class="sidebar-section">
            <h3>Composition Profile</h3>
            <div class="source-list">
                <button
                    class:selected={oracleCompositionSelected}
                    class="source-item"
                    on:click={openBasicOracleComposition}
                >
                    <span>Edit Oracle Composition</span>
                </button>
            </div>
        </section>
        <TestSettingsView
            allowedSettingsGroupIds={BASIC_ROLE_SETTINGS_GROUP_IDS}
            allowSettingsFileToggle={false}
            currentEditorDocument={currentEditorDocument}
            loading={loading}
            openTestSettings={openTestSettings}
            openVisualSettings={openVisualSettings}
            openVisualSettingsGroup={openVisualSettingsGroup}
            regexValidationResults={regexValidationResults}
            renderContent={false}
            renderSidebar={true}
            savedTestSettingsContent={savedTestSettingsContent}
            saving={saving}
            setSettingValue={setSettingValue}
            selectedEditor={selectedEditor}
            selectedSettingsGroupId={selectedSettingsGroupId}
            settingsDescription="Basic setup for SUT connection, execution, filters, oracles, Agent CLI, WebDriver, and Android Appium."
            restoreSettingDefault={restoreSettingDefault}
            validateRegexExpression={validateRegexExpression}
            workspaceDocument={workspaceDocument}
        />
    </section>

    <section class="panel content-panel">
        {#if oracleCompositionSelected}
            <TestCompositionView
                compositionFlowNodes={basicOracleNodes}
                compositionFlowTitle="Edit Oracle Composition"
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
        {:else}
            <TestSettingsView
                allowedSettingsGroupIds={BASIC_ROLE_SETTINGS_GROUP_IDS}
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
                settingsDescription="Basic setup for SUT connection, execution, filters, oracles, Agent CLI, WebDriver, and Android Appium."
                restoreSettingDefault={restoreSettingDefault}
                validateRegexExpression={validateRegexExpression}
                workspaceDocument={workspaceDocument}
            />
        {/if}
    </section>
</main>
