<script>
    import { contentChanged } from "./editorDirtyState.js";
    import { editorModalCanClose } from "./editorModalState.js";

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
    export let renderContent = true;
    export let renderSidebar = true;
    export let saving = false;
    export let selectCompositionFlowNode;
    export let selectedCompositionFlowNode = null;
    export let selectedEditor = "";
    export let selectedSourceFile = null;
    export let selectedSourceSavedContent = "";
    export let savedCompositionPropertiesContent = "";
    export let workspaceDocument = null;

    const compositionFlowGroups = [
        { label: "Capabilities", match: /capability/i },
        { label: "Services", match: /service/i },
        { label: "Oracle", match: /oracle/i },
        { label: "Action loop", match: /action/i }
    ];

    function flowMode(flowNode) {
        return flowNode?.mode || flowNode?.color || "default";
    }

    function flowModeLabel(flowNode) {
        const mode = flowMode(flowNode);

        if (mode === "custom") {
            return "Custom";
        }

        if (mode === "oracle") {
            return "Oracle group";
        }

        if (mode === "invalid") {
            return "Missing reference";
        }

        return "Default";
    }

    function flowImplementation(flowNode) {
        const sourceFileName = typeof flowNode?.sourceFile === "string"
            ? flowNode.sourceFile
            : flowNode?.sourceFile?.name;

        return (
            flowNode?.configuredClassName ||
            flowNode?.className ||
            sourceFileName ||
            flowNode?.fileName ||
            flowNode?.description ||
            "Default implementation"
        );
    }

    function flowRole(flowNode) {
        const title = flowNode?.title || flowNode?.label || "Flow node";
        const group = compositionFlowGroups.find((item) => item.match.test(title));

        return flowNode?.role || group?.label || "Composition node";
    }

    function flowNodeTitle(flowNode) {
        return flowNode?.title || flowNode?.label || "Composition node";
    }

    function closeCompositionModal() {
        if (!editorModalCanClose(saving)) {
            return;
        }

        closeCompositionSourceEditor();
    }

    $: selectedCompositionMode = flowMode(selectedCompositionFlowNode);
    $: selectedSourceDirty = selectedSourceFile
        ? contentChanged(selectedSourceFile.content, selectedSourceSavedContent)
        : false;
    $: compositionPropertiesDirty = contentChanged(
        workspaceDocument?.compositionProperties?.content,
        savedCompositionPropertiesContent
    );
    $: flowStartNodes = compositionFlowNodes.slice(0, 4);
    $: flowStopNode = compositionFlowNodes[4];
    $: flowStateNodes = compositionFlowNodes.slice(5, 9);
    $: flowActionNodes = compositionFlowNodes.slice(9, 13);
    $: flowExtraNodes = compositionFlowNodes.slice(13);
</script>

{#if renderSidebar && workspaceDocument}
    <section class="sidebar-section">
        <h3>Composition profile</h3>
        <div class="source-list">
            <button
                class:selected={isSelectedEditor("composition-properties")}
                class="source-item"
                on:click={openCompositionProperties}
            >
                <span>Edit composition.properties file</span>
            </button>
            <button
                class:selected={isSelectedEditor("java-composition")}
                class="source-item"
                on:click={openJavaComposition}
            >
                <span>Edit Java Composition Flow</span>
            </button>
        </div>
    </section>
{/if}

{#if renderContent && selectedEditor === "composition-properties" && currentEditorDocument}
    <section class="editor-section">
        <div class="section-header">
            <div>
                <h2>{currentEditorDocument.title}</h2>
            </div>
            <button class="secondary" disabled={saving || !compositionPropertiesDirty} on:click={currentEditorDocument.save}>
                {currentEditorDocument.saveLabel}
            </button>
        </div>
        <textarea bind:value={workspaceDocument.compositionProperties.content}></textarea>
    </section>
{:else if renderContent && selectedEditor === "java-composition"}
    <section class="editor-section">
        <section class="composition-flow top-gap" aria-label="Composition architecture graph">
            <div class="composition-flow-header">
                <div>
                    <h3>Edit Java Composition Flow</h3>
                    <p>Click a node to inspect and configure a TESTAR service or capability.</p>
                </div>
                <button class="secondary" disabled={saving} on:click={compileWorkspaceProfile}>
                    Compile Profile
                </button>
                <div class="flow-legend" aria-label="Node state legend">
                    <span><i class="legend-dot legend-default"></i>Default</span>
                    <span><i class="legend-dot legend-custom"></i>Custom</span>
                    <span><i class="legend-dot legend-oracle"></i>Oracle</span>
                </div>
            </div>

            {#if compositionFlowNodes.length > 0}
                <div class="flow-graph flow-graph-clock">
                    <svg class="flow-clock-connectors" viewBox="0 0 1000 420" preserveAspectRatio="none" aria-hidden="true">
                        <defs>
                            <marker id="flow-clock-arrow" viewBox="0 0 10 10" refX="8" refY="5" markerWidth="6" markerHeight="6" orient="auto">
                                <path d="M 0 0 L 10 5 L 0 10 z"></path>
                            </marker>
                        </defs>
                        <path class="flow-clock-path" d="M130 65 C285 30 445 40 520 30" marker-end="url(#flow-clock-arrow)"></path>
                        <path class="flow-clock-path" d="M390 65 C455 42 485 50 520 50" marker-end="url(#flow-clock-arrow)"></path>
                        <path class="flow-clock-path" d="M730 40 C810 45 830 55 860 65" marker-end="url(#flow-clock-arrow)"></path>
                        <path class="flow-clock-path" d="M750 390 C725 405 590 405 510 390" marker-end="url(#flow-clock-arrow)"></path>
                    </svg>

                    <section class="flow-group flow-group-start flow-group-up" aria-label="Composition setup">
                        {#each flowStartNodes.slice().reverse() as flowNode}
                            <button
                                class="flow-node"
                                class:flow-default={flowMode(flowNode) === "default"}
                                class:flow-custom={flowMode(flowNode) === "custom"}
                                class:flow-oracle={flowMode(flowNode) === "oracle"}
                                class:flow-invalid={flowMode(flowNode) === "invalid"}
                                class:flow-selected={selectedCompositionFlowNode === flowNode}
                                on:click={() => selectCompositionFlowNode(flowNode)}
                            >
                                <span class="flow-node-kicker">{flowRole(flowNode)}</span>
                                <strong>{flowNodeTitle(flowNode)}</strong>
                                <small>{flowImplementation(flowNode)}</small>
                                <span class="flow-badge">{flowModeLabel(flowNode)}</span>
                            </button>
                        {/each}
                    </section>

                    {#if flowStopNode}
                        <section class="flow-group flow-group-stop" aria-label="Stop criteria gate">
                            <button
                                class="flow-node"
                                class:flow-default={flowMode(flowStopNode) === "default"}
                                class:flow-custom={flowMode(flowStopNode) === "custom"}
                                class:flow-oracle={flowMode(flowStopNode) === "oracle"}
                                class:flow-invalid={flowMode(flowStopNode) === "invalid"}
                                class:flow-selected={selectedCompositionFlowNode === flowStopNode}
                                on:click={() => selectCompositionFlowNode(flowStopNode)}
                            >
                                <span class="flow-node-kicker">{flowRole(flowStopNode)}</span>
                                <strong>{flowNodeTitle(flowStopNode)}</strong>
                                <small>{flowImplementation(flowStopNode)}</small>
                                <span class="flow-badge">{flowModeLabel(flowStopNode)}</span>
                            </button>
                        </section>
                    {/if}

                    <section class="flow-group flow-group-state flow-group-down" aria-label="State and oracle services">
                        {#each flowStateNodes as flowNode}
                            <button
                                class="flow-node"
                                class:flow-default={flowMode(flowNode) === "default"}
                                class:flow-custom={flowMode(flowNode) === "custom"}
                                class:flow-oracle={flowMode(flowNode) === "oracle"}
                                class:flow-invalid={flowMode(flowNode) === "invalid"}
                                class:flow-selected={selectedCompositionFlowNode === flowNode}
                                on:click={() => selectCompositionFlowNode(flowNode)}
                            >
                                <span class="flow-node-kicker">{flowRole(flowNode)}</span>
                                <strong>{flowNodeTitle(flowNode)}</strong>
                                <small>{flowImplementation(flowNode)}</small>
                                <span class="flow-badge">{flowModeLabel(flowNode)}</span>
                            </button>
                        {/each}
                    </section>

                    <section class="flow-group flow-group-actions flow-group-up" aria-label="Action services">
                        {#each flowActionNodes as flowNode}
                            <button
                                class="flow-node"
                                class:flow-default={flowMode(flowNode) === "default"}
                                class:flow-custom={flowMode(flowNode) === "custom"}
                                class:flow-oracle={flowMode(flowNode) === "oracle"}
                                class:flow-invalid={flowMode(flowNode) === "invalid"}
                                class:flow-selected={selectedCompositionFlowNode === flowNode}
                                on:click={() => selectCompositionFlowNode(flowNode)}
                            >
                                <span class="flow-node-kicker">{flowRole(flowNode)}</span>
                                <strong>{flowNodeTitle(flowNode)}</strong>
                                <small>{flowImplementation(flowNode)}</small>
                                <span class="flow-badge">{flowModeLabel(flowNode)}</span>
                            </button>
                        {/each}
                    </section>

                    {#if flowExtraNodes.length > 0}
                        <section class="flow-group flow-group-extra" aria-label="Additional composition nodes">
                            {#each flowExtraNodes as flowNode}
                                <button
                                    class="flow-node"
                                    class:flow-default={flowMode(flowNode) === "default"}
                                    class:flow-custom={flowMode(flowNode) === "custom"}
                                    class:flow-oracle={flowMode(flowNode) === "oracle"}
                                    class:flow-invalid={flowMode(flowNode) === "invalid"}
                                    class:flow-selected={selectedCompositionFlowNode === flowNode}
                                    on:click={() => selectCompositionFlowNode(flowNode)}
                                >
                                    <span class="flow-node-kicker">{flowRole(flowNode)}</span>
                                    <strong>{flowNodeTitle(flowNode)}</strong>
                                    <small>{flowImplementation(flowNode)}</small>
                                    <span class="flow-badge">{flowModeLabel(flowNode)}</span>
                                </button>
                            {/each}
                        </section>
                    {/if}
                </div>
            {:else}
                <p class="progress-message">No active service or capability classes were resolved for this composition.</p>
            {/if}
        </section>

        {#if selectedCompositionFlowNode}
            <div class="composition-modal-backdrop" on:click|self={closeCompositionModal} role="presentation">
                <div class="composition-modal" role="dialog" tabindex="-1" aria-modal="true" aria-labelledby="composition-modal-title">
                    <div class="composition-modal-header">
                        <div>
                            <span class="flow-node-kicker">Composition Node</span>
                            <h3 id="composition-modal-title">{flowNodeTitle(selectedCompositionFlowNode)}</h3>
                        </div>
                        <div class="composition-modal-actions">
                            <span
                                class="inspector-mode"
                                class:flow-default={selectedCompositionMode === "default"}
                                class:flow-custom={selectedCompositionMode === "custom"}
                                class:flow-oracle={selectedCompositionMode === "oracle"}
                                class:flow-invalid={selectedCompositionMode === "invalid"}
                            >
                                {flowModeLabel(selectedCompositionFlowNode)}
                            </span>
                            <button class="secondary" disabled={saving} on:click={closeCompositionModal}>Close</button>
                        </div>
                    </div>

                    <div class="inspector-actions">
                        {#if selectedCompositionFlowNode.propertyKey && selectedCompositionMode === "default"}
                            <button
                                class="secondary"
                                disabled={saving}
                                on:click={() => createCompositionModuleSource(selectedCompositionFlowNode)}
                            >
                                Create Java Class
                            </button>
                        {:else if !selectedCompositionFlowNode.propertyKey}
                            <button class="secondary" disabled title="This flow node is driven by the composed runtime and has no dedicated override property">No direct override</button>
                        {/if}
                    </div>

                    <div class="composition-modal-body">
                        {#if selectedSourceFile && (selectedSourceFile.category === "service" || selectedSourceFile.category === "capability")}
                            <div class="section-header">
                                <div>
                                    <h3>{selectedSourceFile.name}</h3>
                                </div>
                                <div class="button-row">
                                    <button
                                        class="secondary"
                                        disabled={saving}
                                        on:click={() => createCompositionModuleSource(selectedCompositionFlowNode)}
                                    >
                                        Refresh Java
                                    </button>
                                    <button class="secondary" disabled={saving || !selectedSourceDirty} on:click={compileSelectedJavaSource}>
                                        Save and Compile
                                    </button>
                                </div>
                            </div>
                            <section class="compile-results-panel" class:compile-results-success={javaCompileResult?.scope === "source" && javaCompileResult.success} class:compile-results-failed={javaCompileResult?.scope === "source" && !javaCompileResult.success} class:compile-results-idle={javaCompileResult?.scope !== "source"}>
                                {#if javaCompileResult?.scope === "source" && javaCompileResult.diagnostics?.length > 0}
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
                                {:else if javaCompileResult?.scope === "source" && javaCompileResult.success}
                                    <div class="compile-results-empty">
                                        <p>Java compilation succeeded for the current composition source file.</p>
                                    </div>
                                {:else}
                                    <div class="compile-results-empty">
                                        <p>Run "Save and Compile" to validate the current composition source file.</p>
                                    </div>
                                {/if}
                            </section>
                            <textarea bind:value={selectedSourceFile.content}></textarea>
                        {:else}
                            <div class="composition-modal-empty">
                                <h4>No custom source file is assigned</h4>
                                <p>
                                    This node is currently using the default implementation. To edit code here, create or assign a custom Java implementation for this composition node.
                                </p>
                            </div>
                        {/if}
                    </div>
                </div>
            </div>
        {/if}

        <section class="compile-results-panel top-gap" class:compile-results-success={javaCompileResult?.scope === "profile" && javaCompileResult.success} class:compile-results-failed={javaCompileResult?.scope === "profile" && !javaCompileResult.success} class:compile-results-idle={javaCompileResult?.scope !== "profile"}>
            {#if javaCompileResult?.scope === "profile" && javaCompileResult.diagnostics?.length > 0}
                <div class="compile-diagnostics-list">
                    {#each javaCompileResult.diagnostics as diagnostic}
                        <article class="compile-diagnostic-row">
                            <div>
                                <strong>{diagnostic.fileName || "workspace"}</strong>
                                <span>{diagnostic.relativePath}</span>
                            </div>
                            <small>{diagnostic.severity} {diagnostic.line > 0 ? `L${diagnostic.line}` : ""}{diagnostic.column > 0 ? `:${diagnostic.column}` : ""}</small>
                            <p>{diagnostic.message}</p>
                        </article>
                    {/each}
                </div>
            {:else if javaCompileResult?.scope === "profile" && javaCompileResult.success}
                <div class="compile-results-empty">
                    <p>Java profile compilation succeeded for the current composition profile.</p>
                </div>
            {:else}
                <div class="compile-results-empty">
                    <p>Run "Compile Profile" to validate every Java composition class in this profile.</p>
                </div>
            {/if}
        </section>
    </section>
{/if}
