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
    export let selectSettingsGroup;
    export let selectedEditor = "";
    export let selectedCompositionFlowNode = null;
    export let selectedSettingsGroupId = "";
    export let selectedSourceFile = null;
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

    function flowEdgeLabel(flowNode, index) {
        return flowNode?.edgeLabel || compositionFlowNodes[index + 1]?.incomingLabel || "next";
    }

    function openSelectedCompositionSource() {
        if (!selectedCompositionFlowNode) {
            return;
        }

        const sourceName =
            (typeof selectedCompositionFlowNode.sourceFile === "string"
                ? selectedCompositionFlowNode.sourceFile
                : selectedCompositionFlowNode.sourceFile?.name) ||
            selectedCompositionFlowNode.fileName ||
            selectedCompositionFlowNode.name;

        if (sourceName) {
            selectSource(sourceName, "java-composition");
        }
    }

    $: selectedCompositionMode = flowMode(selectedCompositionFlowNode);
    $: selectedCompositionHasSource = Boolean(
        selectedCompositionFlowNode?.sourceFile?.name
            || selectedCompositionFlowNode?.sourceFile
            || selectedCompositionFlowNode?.fileName
            || selectedCompositionFlowNode?.name
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

    function openSettingsGroup(groupId) {
        if (groupId) {
            openVisualSettings();
            selectSettingsGroup(groupId);
            expandedSettingsGroups = {
                ...expandedSettingsGroups,
                [groupId]: true
            };
        }
    }

    $: normalizedSettingsSearch = normalizedSearchText(settingsSearch);

    $: filteredSettingsGroups = (workspaceDocument?.settingsGroups || [])
        .map((settingsGroup) => ({
            ...settingsGroup,
            settings: (settingsGroup.settings || []).filter((setting) => matchesSettingsSearch(setting, settingsSearch))
        }))
        .filter((settingsGroup) => settingsGroup.settings.length > 0);

    $: settingsNavigationGroups = workspaceDocument?.settingsGroups || [];
    $: visibleSettingsGroups = normalizedSettingsSearch
        ? filteredSettingsGroups
        : filteredSettingsGroups.filter((settingsGroup) => settingsGroup.id === selectedSettingsGroupId);

    $: {
        const nextWorkspaceKey = workspaceDocument?.workspaceName || "";

        if (nextWorkspaceKey !== settingsExpansionWorkspaceKey) {
            settingsExpansionWorkspaceKey = nextWorkspaceKey;
            expandedSettingsGroups = workspaceDocument?.settingsGroups?.length > 0
                ? { [workspaceDocument.settingsGroups[0].id]: true }
                : {};
        }
    }

    $: flowStartNodes = compositionFlowNodes.slice(0, 4);
    $: flowStopNode = compositionFlowNodes[4];
    $: flowStateNodes = compositionFlowNodes.slice(5, 9);
    $: flowActionNodes = compositionFlowNodes.slice(9, 13);
    $: flowExtraNodes = compositionFlowNodes.slice(13);

</script>

<main class="studio-layout">
    <section class="panel sidebar">
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

        <section class="sidebar-section">
            <h3>Test Policies</h3>
            <div class="source-list">
                <button
                    class:selected={isSelectedEditor("policies-properties")}
                    class="source-item"
                    on:click={openPoliciesProperties}
                >
                    <span>Edit policies.properties file</span>
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

        <section class="sidebar-section">
            <h3>Test Settings</h3>
            <div class="source-list">
                <button
                    class:selected={isSelectedEditor("test-settings")}
                    class="source-item"
                    on:click={openTestSettings}
                >
                    <span>Edit test.settings file</span>
                </button>
            </div>
            <div class="settings-sidebar-tree">
                <button
                    class:selected={isSelectedEditor("settings-form")}
                    class="source-item settings-tree-root"
                    on:click={openVisualSettings}
                >
                    <span>Edit Settings</span>
                </button>
                {#if settingsNavigationGroups.length > 0}
                    <div class="settings-sidebar-nav">
                        {#each settingsNavigationGroups as settingsGroup}
                            <button
                                type="button"
                                class="source-item settings-nav-item"
                                class:selected={selectedEditor === "settings-form" && selectedSettingsGroupId === settingsGroup.id}
                                on:click={() => openSettingsGroup(settingsGroup.id)}
                            >
                                <span>{settingsGroup.title}</span>
                            </button>
                        {/each}
                    </div>
                {/if}
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
                        {#if settingsNavigationGroups.length > 0}
                            <div class="settings-tabs" role="tablist" aria-label="Settings categories">
                                {#each settingsNavigationGroups as settingsGroup}
                                    <button
                                        type="button"
                                        class="settings-tab"
                                        class:selected={selectedSettingsGroupId === settingsGroup.id}
                                        aria-pressed={selectedSettingsGroupId === settingsGroup.id}
                                        on:click={() => openSettingsGroup(settingsGroup.id)}
                                    >
                                        <span>{settingsGroup.title}</span>
                                    </button>
                                {/each}
                            </div>
                        {/if}
                    </section>

                    <section class="settings-groups top-gap">
                        {#if visibleSettingsGroups.length > 0}
                            {#each visibleSettingsGroups as settingsGroup (settingsGroup.id)}
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
                                                <label
                                                    class="settings-field"
                                                    class:settings-field-wide={setting.type === "list"}
                                                    class:settings-field-boolean={setting.type === "boolean"}
                                                >
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
                            <p class="progress-message">
                                {#if normalizedSettingsSearch}
                                    No settings matched the current search.
                                {:else}
                                    Select a settings category to inspect its fields.
                                {/if}
                            </p>
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
                            <div class="composition-modal composition-modal-policy" role="dialog" tabindex="-1" aria-modal="true" aria-labelledby="policy-modal-title">
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
                            <div class="flow-graph flow-graph-clock">
                                <svg class="flow-clock-connectors" viewBox="0 0 1000 420" preserveAspectRatio="none" aria-hidden="true">
                                    <defs>
                                        <marker id="flow-clock-arrow" viewBox="0 0 10 10" refX="8" refY="5" markerWidth="6" markerHeight="6" orient="auto">
                                            <path d="M 0 0 L 10 5 L 0 10 z"></path>
                                        </marker>
                                    </defs>
                                    <!-- SystemService -> StopCriteriaCapability -->
                                    <path class="flow-clock-path" d="M130 75 C285 30 445 40 520 30" marker-end="url(#flow-clock-arrow)"></path>
                                    <!-- ActionDerivationService -> StopCriteriaCapability -->
                                    <path class="flow-clock-path" d="M390 75 C455 42 485 50 520 50" marker-end="url(#flow-clock-arrow)"></path>
                                    <!-- StopCriteriaCapability -> StateService -->
                                    <path class="flow-clock-path" d="M730 40 C810 50 830 65 860 75" marker-end="url(#flow-clock-arrow)"></path>
                                    <!-- Custom Oracle Services -> ActionExecutionService -->
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
