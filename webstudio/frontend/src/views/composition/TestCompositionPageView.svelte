<script>
    import TestCompositionView from "./TestCompositionView.svelte";

    export let compositionFlowNodes = [];
    export let currentEditorDocument = null;
    export let isSelectedEditor;
    export let openCompositionProperties;
    export let openJavaComposition;
    export let compileSelectedJavaSource;
    export let compileWorkspaceProfile;
    export let createCompositionModuleSource;
    export let javaCompileResult = null;
    export let closeCompositionSourceEditor;
    export let saving = false;
    export let selectCompositionFlowNode;
    export let selectedCompositionFlowNode = null;
    export let selectedEditor = "";
    export let selectedSourceFile = null;
    export let selectedSourceSavedContent = "";
    export let savedCompositionPropertiesContent = "";
    export let workspaceDocument = null;
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
    </section>

    <section class="panel content-panel">
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
    </section>
</main>
