<script>
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
    export let policySourceFiles = [];
    export let saveSelectedSource;
    export let saving = false;
    export let selectSource;
    export let selectCompositionFlowNode;
    export let selectedEditor = "";
    export let selectedCompositionFlowNode = null;
    export let selectedSourceFile = null;
    export let selectedWorkspaceName = "";
    export let workspaceDocument = null;
    export let workspaces = [];
    export let loadWorkspace;

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
        return (
            flowNode?.configuredClassName ||
            flowNode?.className ||
            flowNode?.sourceFile ||
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

    function flowEdgeLabel(flowNode, index) {
        return flowNode?.edgeLabel || compositionFlowNodes[index + 1]?.incomingLabel || "next";
    }

    function openSelectedCompositionSource() {
        if (!selectedCompositionFlowNode) {
            return;
        }

        const sourceName =
            selectedCompositionFlowNode.sourceFile ||
            selectedCompositionFlowNode.fileName ||
            selectedCompositionFlowNode.name;

        if (sourceName) {
            selectSource(sourceName, "java-composition");
        }
    }

    $: selectedCompositionMode = flowMode(selectedCompositionFlowNode);
    $: selectedCompositionHasSource = Boolean(
        selectedCompositionFlowNode?.sourceFile || selectedCompositionFlowNode?.fileName || selectedCompositionFlowNode?.name
    );

    function closeCompositionModal() {
        selectCompositionFlowNode(null);
    }

    function closePolicyModal() {
        openJavaPolicies();
    }

    function normalizedSearchText(text) {
        return (text || "").trim().toLowerCase();
    }

    function matchesSettingsSearch(setting, searchText) {
        const normalized = normalizedSearchText(searchText);

        if (!normalized) {
            return true;
        }

        return [
            setting?.key,
            setting?.type,
            setting?.value,
            setting?.description
        ]
            .filter(Boolean)
            .some((item) => item.toLowerCase().includes(normalized));
    }

    let settingsSearch = "";
    let expandedSettingsGroups = {};
    let settingsExpansionWorkspaceKey = "";

    function toggleSettingsGroup(groupId) {
        expandedSettingsGroups = {
            ...expandedSettingsGroups,
            [groupId]: !expandedSettingsGroups[groupId]
        };
    }

    $: normalizedSettingsSearch = normalizedSearchText(settingsSearch);

    $: filteredSettingsGroups = (workspaceDocument?.settingsGroups || [])
        .map((settingsGroup) => ({
            ...settingsGroup,
            settings: (settingsGroup.settings || []).filter((setting) => matchesSettingsSearch(setting, settingsSearch))
        }))
        .filter((settingsGroup) => settingsGroup.settings.length > 0);

    $: {
        const nextWorkspaceKey = workspaceDocument?.workspaceName || "";

        if (nextWorkspaceKey !== settingsExpansionWorkspaceKey) {
            settingsExpansionWorkspaceKey = nextWorkspaceKey;
            expandedSettingsGroups = workspaceDocument?.settingsGroups?.length > 0
                ? { [workspaceDocument.settingsGroups[0].id]: true }
                : {};
        }
    }

</script>

<main class="studio-layout">
    <section class="panel sidebar">
        <section class="sidebar-section">
            <h3>Composition profile</h3>
            <div class="source-list">
                <select
                    id="workspace-select"
                    value={selectedWorkspaceName}
                    on:change={(event) => loadWorkspace(event.currentTarget.value)}
                >
                    {#each workspaces as workspace}
                        <option value={workspace.name}>{workspace.name}</option>
                    {/each}
                </select>
                <button
                    class:selected={isSelectedEditor("composition-properties")}
                    class="source-item"
                    on:click={openCompositionProperties}
                >
                    <span>Edit composition.properties</span>
                </button>
                <button
                    class:selected={isSelectedEditor("java-composition")}
                    class="source-item"
                    on:click={openJavaComposition}
                >
                    <span>Edit Java Composition</span>
                </button>
            </div>
        </section>

        <section class="sidebar-section">
            <h3>Test Settings</h3>
            <div class="source-list">
                <button
                    class:selected={isSelectedEditor("settings-form")}
                    class="source-item"
                    on:click={openVisualSettings}
                >
                    <span>Edit Settings</span>
                </button>
                <button
                    class:selected={isSelectedEditor("test-settings")}
                    class="source-item"
                    on:click={openTestSettings}
                >
                    <span>Edit test.settings</span>
                </button>
            </div>
        </section>

        <section class="sidebar-section">
            <h3>Test Policies</h3>
            <div class="source-list">
                <button
                    class:selected={isSelectedEditor("policies-properties")}
                    class="source-item"
                    on:click={openPoliciesProperties}
                >
                    <span>Edit policies.properties</span>
                </button>
                <button
                    class:selected={isSelectedEditor("java-policies")}
                    class="source-item"
                    on:click={openJavaPolicies}
                >
                    <span>Edit Java Policies</span>
                    <small>{policySourceFiles.length}</small>
                </button>
            </div>
        </section>
    </section>

    <section class="panel content-panel">
        {#if loading}
            <p>Loading workspace...</p>
        {:else if workspaceDocument}
            {#if selectedEditor === "settings-form" && currentEditorDocument}
                <section class="editor-section">
                    <div class="section-header">
                        <div>
                            <h2>{currentEditorDocument.title}</h2>
                            <p>Main common TESTAR settings with grouped controls. Use the test.settings editor for advanced control.</p>
                        </div>
                        <button class="secondary" disabled={saving} on:click={currentEditorDocument.save}>
                            {currentEditorDocument.saveLabel}
                        </button>
                    </div>

                    <section class="settings-toolbar top-gap">
                        <div class="settings-search">
                            <label class="field-label" for="settings-search">Search settings</label>
                            <input
                                id="settings-search"
                                type="search"
                                bind:value={settingsSearch}
                                placeholder="Search by key, value, type, or description"
                            />
                        </div>
                    </section>

                    <section class="settings-groups top-gap">
                        {#if filteredSettingsGroups.length > 0}
                            {#each filteredSettingsGroups as settingsGroup (settingsGroup.id)}
                                <article class="settings-group-card">
                                    <button
                                        type="button"
                                        class="settings-group-toggle"
                                        aria-expanded={normalizedSettingsSearch ? true : Boolean(expandedSettingsGroups[settingsGroup.id])}
                                        on:click={() => toggleSettingsGroup(settingsGroup.id)}
                                    >
                                        <div class="settings-group-header">
                                            <div>
                                                <h3>{settingsGroup.title}</h3>
                                                <p>{settingsGroup.description}</p>
                                            </div>
                                            <div class="settings-group-summary">
                                                <span class="settings-group-count">{settingsGroup.settings.length}</span>
                                                <span class="settings-group-chevron">
                                                    {#if normalizedSettingsSearch ? true : Boolean(expandedSettingsGroups[settingsGroup.id])}
                                                        Hide
                                                    {:else}
                                                        Show
                                                    {/if}
                                                </span>
                                            </div>
                                        </div>
                                    </button>

                                    {#if normalizedSettingsSearch ? true : Boolean(expandedSettingsGroups[settingsGroup.id])}
                                        <div class="settings-fields">
                                            {#each settingsGroup.settings as setting}
                                                <label class="settings-field">
                                                    <span class="settings-field-label">{setting.key}</span>

                                                    {#if setting.type === "boolean"}
                                                        <span class="settings-checkbox-row">
                                                            <input
                                                                type="checkbox"
                                                                checked={setting.value === "true"}
                                                                on:change={(event) => {
                                                                    setting.value = event.currentTarget.checked ? "true" : "false";
                                                                }}
                                                            />
                                                            <span>{setting.value === "true" ? "Enabled" : "Disabled"}</span>
                                                        </span>
                                                    {:else if setting.options?.length > 0}
                                                        <select bind:value={setting.value}>
                                                            <option value=""></option>
                                                            {#each setting.options as option}
                                                                <option value={option}>{option}</option>
                                                            {/each}
                                                        </select>
                                                    {:else if setting.type === "list"}
                                                        <textarea
                                                            class="settings-list-input"
                                                            bind:value={setting.value}
                                                            placeholder="Keep TESTAR list syntax here"
                                                        ></textarea>
                                                    {:else if setting.type === "integer"}
                                                        <input
                                                            type="number"
                                                            step="1"
                                                            value={setting.value}
                                                            on:input={(event) => {
                                                                setting.value = event.currentTarget.value;
                                                            }}
                                                        />
                                                    {:else if setting.type === "number"}
                                                        <input
                                                            type="number"
                                                            step="any"
                                                            value={setting.value}
                                                            on:input={(event) => {
                                                                setting.value = event.currentTarget.value;
                                                            }}
                                                        />
                                                    {:else}
                                                        <input
                                                            type="text"
                                                            bind:value={setting.value}
                                                        />
                                                    {/if}

                                                    {#if setting.description}
                                                        <small class="settings-field-help">{setting.description}</small>
                                                    {/if}
                                                </label>
                                            {/each}
                                        </div>
                                    {/if}
                                </article>
                            {/each}
                        {:else}
                            <p class="progress-message">No settings matched the current search.</p>
                        {/if}
                    </section>
                </section>
            {:else if currentEditorDocument}
                <section class="editor-section">
                    <div class="section-header">
                        <div>
                            <h2>{currentEditorDocument.title}</h2>
                        </div>
                        <button class="secondary" disabled={saving} on:click={currentEditorDocument.save}>
                            {currentEditorDocument.saveLabel}
                        </button>
                    </div>
                    {#if selectedEditor === "test-settings"}
                        <textarea bind:value={workspaceDocument.testSettings.content}></textarea>
                    {:else if selectedEditor === "policies-properties"}
                        <textarea bind:value={workspaceDocument.policiesProperties.content}></textarea>
                    {:else if selectedEditor === "composition-properties"}
                        <textarea bind:value={workspaceDocument.compositionProperties.content}></textarea>
                    {:else if selectedSourceFile}
                        <textarea bind:value={selectedSourceFile.content}></textarea>
                    {/if}
                </section>
            {:else if selectedEditor === "java-policies"}
                <section class="editor-section">
                    <div class="section-header">
                        <div>
                            <h2>Edit Java Policies</h2>
                            <p>Review the policy classes resolved from policies.properties and open them for editing.</p>
                        </div>
                    </div>

                    <section class="manager-card top-gap">
                        <h3>Policies Active In Profile</h3>
                        <div class="source-list manager-list">
                            {#if activePolicySourceFiles.length > 0}
                                {#each activePolicySourceFiles as sourceFile}
                                    <button
                                        class:selected={isPolicySourceSelected(sourceFile)}
                                        class="source-item"
                                        on:click={() => selectSource(sourceFile.name, "java-policies")}
                                    >
                                        <span>{sourceFile.name}</span>
                                        <small>active</small>
                                    </button>
                                {/each}
                            {:else}
                                <p class="progress-message">No referenced Java policies were resolved in this workspace.</p>
                            {/if}
                        </div>
                    </section>

                    <section class="manager-card top-gap">
                        <h3>Available Policy Files Not In Profile</h3>
                        <div class="source-list manager-list">
                            {#if inactivePolicySourceFiles.length > 0}
                                {#each inactivePolicySourceFiles as sourceFile}
                                    <button
                                        class:selected={isPolicySourceSelected(sourceFile)}
                                        class="source-item"
                                        on:click={() => selectSource(sourceFile.name, "java-policies")}
                                    >
                                        <span>{sourceFile.name}</span>
                                        <small>available</small>
                                    </button>
                                {/each}
                            {:else}
                                <p class="progress-message">No extra policy Java files were found in this workspace.</p>
                            {/if}
                        </div>
                    </section>

                    {#if selectedSourceFile?.category === "policy"}
                        <div class="composition-modal-backdrop" on:click|self={closePolicyModal} role="presentation">
                            <div class="composition-modal" role="dialog" tabindex="-1" aria-modal="true" aria-labelledby="policy-modal-title">
                                <div class="composition-modal-header">
                                    <div>
                                        <span class="flow-node-kicker">Policy Source</span>
                                        <h3 id="policy-modal-title">{selectedSourceFile.name}</h3>
                                    </div>
                                    <div class="composition-modal-actions">
                                        <button class="secondary" on:click={closePolicyModal}>Close</button>
                                    </div>
                                </div>

                                <div class="composition-modal-body">
                                    <div class="section-header">
                                        <div>
                                            <h3>Policy Source Editor</h3>
                                            <p>Edit the selected policy source directly in this modal.</p>
                                        </div>
                                        <button class="secondary" disabled={saving} on:click={saveSelectedSource}>
                                            Save Policy
                                        </button>
                                    </div>
                                    <textarea bind:value={selectedSourceFile.content}></textarea>
                                </div>
                            </div>
                        </div>
                    {/if}
                </section>
            {:else if selectedEditor === "java-composition"}
                <section class="editor-section">
                    <section class="composition-flow top-gap" aria-label="Composition architecture graph">
                        <div class="composition-flow-header">
                            <div>
                                <h3>Edit Java Composition</h3>
                                <p>Click a node to inspect and configure a TESTAR service or capability.</p>
                            </div>
                            <div class="flow-legend" aria-label="Node state legend">
                                <span><i class="legend-dot legend-default"></i>Default</span>
                                <span><i class="legend-dot legend-custom"></i>Custom</span>
                                <span><i class="legend-dot legend-oracle"></i>Oracle</span>
                            </div>
                        </div>

                        {#if compositionFlowNodes.length > 0}
                            <div class="flow-graph flow-graph-circular">
                                <div class="flow-top-pipeline">
                                    {#each compositionFlowNodes.slice(0, 4) as flowNode, index}
                                        <article class="flow-graph-step flow-top-step">
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

                                            {#if index < 3}
                                                <div class="flow-edge" aria-hidden="true">
                                                    <svg class="flow-edge-svg" viewBox="0 0 80 54" preserveAspectRatio="none">
                                                        <defs>
                                                            <marker id={`flow-arrow-top-${index}`} viewBox="0 0 10 10" refX="5" refY="5" markerWidth="5" markerHeight="5" orient="auto-start-reverse">
                                                                <path d="M 0 0 L 10 5 L 0 10 z"></path>
                                                            </marker>
                                                        </defs>
                                                        <path d="M40 2 C40 18 40 34 40 48" marker-end={`url(#flow-arrow-top-${index})`}></path>
                                                    </svg>
                                                    <span class="flow-edge-label">{flowEdgeLabel(flowNode, index)}</span>
                                                </div>
                                            {/if}
                                        </article>
                                    {/each}
                                </div>

                                {#if compositionFlowNodes.length > 4}
                                    <div class="flow-circular-stage" aria-label="Composition loop">
                                        <svg class="flow-circular-connectors" viewBox="0 0 1000 720" preserveAspectRatio="none" aria-hidden="true">
                                            <defs>
                                                <marker id="flow-circular-arrow" viewBox="0 0 10 10" refX="5" refY="5" markerWidth="5" markerHeight="5" orient="auto-start-reverse">
                                                    <path d="M 0 0 L 10 5 L 0 10 z"></path>
                                                </marker>
                                            </defs>
                                            <path class="flow-circular-path" d="M500 0 C500 55 315 35 250 86" marker-end="url(#flow-circular-arrow)"></path>
                                            <path class="flow-circular-path" d="M250 164 C250 205 250 229 250 270" marker-end="url(#flow-circular-arrow)"></path>
                                            <path class="flow-circular-path" d="M250 348 C250 389 250 413 250 454" marker-end="url(#flow-circular-arrow)"></path>
                                            <path class="flow-circular-path" d="M250 532 C250 590 335 612 430 626" marker-end="url(#flow-circular-arrow)"></path>
                                            <path class="flow-circular-path" d="M570 626 C665 612 750 590 750 532" marker-end="url(#flow-circular-arrow)"></path>
                                            <path class="flow-circular-path" d="M750 454 C750 413 750 389 750 348" marker-end="url(#flow-circular-arrow)"></path>
                                            <path class="flow-circular-path" d="M750 270 C750 229 750 205 750 164" marker-end="url(#flow-circular-arrow)"></path>
                                            <path class="flow-circular-path" d="M750 86 C685 35 500 55 500 0" marker-end="url(#flow-circular-arrow)"></path>
                                        </svg>

                                        {#each compositionFlowNodes.slice(4, 8) as flowNode, localIndex}
                                            <article class="flow-circular-node-slot flow-left-step" style={`grid-row: ${localIndex + 1};`}>
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
                                            </article>
                                        {/each}

                                        {#if compositionFlowNodes[8]}
                                            <article class="flow-circular-node-slot flow-bottom-step">
                                                <button
                                                    class="flow-node"
                                                    class:flow-default={flowMode(compositionFlowNodes[8]) === "default"}
                                                    class:flow-custom={flowMode(compositionFlowNodes[8]) === "custom"}
                                                    class:flow-oracle={flowMode(compositionFlowNodes[8]) === "oracle"}
                                                    class:flow-invalid={flowMode(compositionFlowNodes[8]) === "invalid"}
                                                    class:flow-selected={selectedCompositionFlowNode === compositionFlowNodes[8]}
                                                    on:click={() => selectCompositionFlowNode(compositionFlowNodes[8])}
                                                >
                                                    <span class="flow-node-kicker">{flowRole(compositionFlowNodes[8])}</span>
                                                    <strong>{flowNodeTitle(compositionFlowNodes[8])}</strong>
                                                    <small>{flowImplementation(compositionFlowNodes[8])}</small>
                                                    <span class="flow-badge">{flowModeLabel(compositionFlowNodes[8])}</span>
                                                </button>
                                            </article>
                                        {/if}

                                        {#each compositionFlowNodes.slice(9, 13) as flowNode, localIndex}
                                            <article class="flow-circular-node-slot flow-right-step" style={`grid-row: ${4 - localIndex};`}>
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
                                            </article>
                                        {/each}

                                        {#if compositionFlowNodes.length > 13}
                                            {#each compositionFlowNodes.slice(13) as flowNode, index}
                                                <article class="flow-circular-node-slot flow-extra-step" style={`grid-row: ${index + 1};`}>
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
                                                </article>
                                            {/each}
                                        {/if}
                                    </div>
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
                                        <button class="secondary" on:click={closeCompositionModal}>Close</button>
                                    </div>
                                </div>

                                <div class="inspector-actions">
                                    {#if selectedCompositionMode === "default"}
                                        <button class="secondary" disabled title="Backend action not wired in this UI pass">Create New Java Class</button>
                                        <button class="secondary" disabled title="Backend action not wired in this UI pass">Use Existing Java Class</button>
                                    {:else}
                                        <button class="secondary" disabled={!selectedCompositionHasSource} on:click={openSelectedCompositionSource}>Refresh Source</button>
                                        <button class="secondary" disabled title="Backend action not wired in this UI pass">Replace Implementation</button>
                                        <button class="secondary" disabled title="Backend action not wired in this UI pass">Reset To Default</button>
                                    {/if}
                                </div>

                                <div class="composition-modal-body">
                                    {#if selectedSourceFile && (selectedSourceFile.category === "service" || selectedSourceFile.category === "capability")}
                                        <div class="section-header">
                                            <div>
                                                <h3>{selectedSourceFile.name}</h3>
                                                <p>Edit the selected service or capability source directly in this modal.</p>
                                            </div>
                                            <button class="secondary" disabled={saving} on:click={saveSelectedSource}>
                                                Save Composition
                                            </button>
                                        </div>
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
                </section>
            {/if}
        {/if}
    </section>
</main>
