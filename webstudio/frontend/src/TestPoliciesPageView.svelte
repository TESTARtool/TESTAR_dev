<script>
    import TestPoliciesView from "./TestPoliciesView.svelte";

    export let activePolicySourceFiles = [];
    export let currentEditorDocument = null;
    export let inactivePolicySourceFiles = [];
    export let isPolicySourceSelected;
    export let isSelectedEditor;
    export let openJavaPolicies;
    export let openPoliciesProperties;
    export let policySourceFiles = [];
    export let closePolicySourceEditor;
    export let compileSelectedJavaSource;
    export let compileWorkspaceProfile;
    export let createPolicySource;
    export let javaCompileResult = null;
    export let saving = false;
    export let selectSource;
    export let selectedEditor = "";
    export let selectedSourceFile = null;
    export let selectedSourceSavedContent = "";
    export let savedPoliciesPropertiesContent = "";
    export let togglePolicySourceActivation;
    export let workspaceDocument = null;
</script>

<main class="studio-layout">
    <section class="panel sidebar">
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
    </section>

    <section class="panel content-panel">
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
    </section>
</main>
