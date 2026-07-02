<script>
    import TestCompositionView from "./TestCompositionView.svelte";
    import TestPoliciesView from "./TestPoliciesView.svelte";
    import TestSettingsView from "./TestSettingsView.svelte";

    export let activePolicySourceFiles = [];
    export let compositionFlowNodes = [];
    export let currentEditorDocument = null;
    export let inactivePolicySourceFiles = [];
    export let isPolicySourceSelected;
    export let isSelectedEditor;
    export let loading = false;
    export let openCompositionProperties;
    export let openJavaComposition;
    export let openJavaPolicies;
    export let openPoliciesProperties;
    export let openTestSettings;
    export let openVisualSettings;
    export let openVisualSettingsGroup;
    export let policySourceFiles = [];
    export let closeCompositionSourceEditor;
    export let closePolicySourceEditor;
    export let compileSelectedJavaSource;
    export let compileWorkspaceProfile;
    export let createCompositionModuleSource;
    export let createPolicySource;
    export let javaCompileResult = null;
    export let regexValidationResults = {};
    export let saving = false;
    export let savedCompositionPropertiesContent = "";
    export let savedPoliciesPropertiesContent = "";
    export let savedTestSettingsContent = "";
    export let setSettingValue;
    export let selectSource;
    export let selectCompositionFlowNode;
    export let selectedEditor = "";
    export let selectedCompositionFlowNode = null;
    export let selectedSettingsGroupId = "";
    export let selectedSourceFile = null;
    export let selectedSourceDirty = false;
    export let selectedSourceSavedContent = "";
    export let restoreSettingDefault;
    export let togglePolicySourceActivation;
    export let validateRegexExpression;
    export let workspaceDocument = null;

    function isCompositionEditor(editorId) {
        return editorId === "composition-properties" || editorId === "java-composition";
    }

    function isPoliciesEditor(editorId) {
        return editorId === "policies-properties" || editorId === "java-policies";
    }

    function isSettingsEditor(editorId) {
        return editorId === "settings-form" || editorId === "test-settings";
    }
</script>

<main class="studio-layout">
    <section class="panel sidebar">
        <TestCompositionView
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
            renderContent={false}
            renderSidebar={true}
            saving={saving}
            selectCompositionFlowNode={selectCompositionFlowNode}
            selectedCompositionFlowNode={selectedCompositionFlowNode}
            selectedEditor={selectedEditor}
            selectedSourceFile={selectedSourceFile}
            selectedSourceSavedContent={selectedSourceSavedContent}
            savedCompositionPropertiesContent={savedCompositionPropertiesContent}
            workspaceDocument={workspaceDocument}
        />

        <TestPoliciesView
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
            renderContent={false}
            renderSidebar={true}
            saving={saving}
            selectSource={selectSource}
            selectedEditor={selectedEditor}
            selectedSourceFile={selectedSourceFile}
            selectedSourceSavedContent={selectedSourceSavedContent}
            savedPoliciesPropertiesContent={savedPoliciesPropertiesContent}
            togglePolicySourceActivation={togglePolicySourceActivation}
            workspaceDocument={workspaceDocument}
        />

        <TestSettingsView
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
            restoreSettingDefault={restoreSettingDefault}
            validateRegexExpression={validateRegexExpression}
            workspaceDocument={workspaceDocument}
        />
    </section>

    <section class="panel content-panel">
        {#if loading}
            <p>Loading workspace...</p>
        {:else if workspaceDocument}
            {#if isCompositionEditor(selectedEditor)}
                <TestCompositionView
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
                    renderContent={true}
                    renderSidebar={false}
                    saving={saving}
                    selectCompositionFlowNode={selectCompositionFlowNode}
                    selectedCompositionFlowNode={selectedCompositionFlowNode}
                    selectedEditor={selectedEditor}
                    selectedSourceFile={selectedSourceFile}
                    selectedSourceSavedContent={selectedSourceSavedContent}
                    savedCompositionPropertiesContent={savedCompositionPropertiesContent}
                    workspaceDocument={workspaceDocument}
                />
            {:else if isPoliciesEditor(selectedEditor)}
                <TestPoliciesView
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
                    renderContent={true}
                    renderSidebar={false}
                    saving={saving}
                    selectSource={selectSource}
                    selectedEditor={selectedEditor}
                    selectedSourceFile={selectedSourceFile}
                    selectedSourceSavedContent={selectedSourceSavedContent}
                    savedPoliciesPropertiesContent={savedPoliciesPropertiesContent}
                    togglePolicySourceActivation={togglePolicySourceActivation}
                    workspaceDocument={workspaceDocument}
                />
            {:else if isSettingsEditor(selectedEditor) && currentEditorDocument}
                <TestSettingsView
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
                    restoreSettingDefault={restoreSettingDefault}
                    validateRegexExpression={validateRegexExpression}
                    workspaceDocument={workspaceDocument}
                />
            {:else if currentEditorDocument && selectedSourceFile}
                <section class="editor-section">
                    <div class="section-header">
                        <div>
                            <h2>{currentEditorDocument.title}</h2>
                        </div>
                        <button class="secondary" disabled={saving || !selectedSourceDirty} on:click={currentEditorDocument.save}>
                            {currentEditorDocument.saveLabel}
                        </button>
                    </div>
                    <textarea bind:value={selectedSourceFile.content}></textarea>
                </section>
            {/if}
        {/if}
    </section>
</main>
