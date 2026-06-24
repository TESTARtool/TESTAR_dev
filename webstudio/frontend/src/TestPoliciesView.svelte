<script>
    import { policySourceModalVisible } from "./policyEditorState.js";

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
    export let renderContent = true;
    export let renderSidebar = true;
    export let saving = false;
    export let selectSource;
    export let selectedEditor = "";
    export let selectedSourceFile = null;
    export let togglePolicySourceActivation;
    export let workspaceDocument = null;

    function closePolicyModal() {
        closePolicySourceEditor();
    }

    function leftPolicyDefinitions() {
        const leftPolicyKeys = new Set([
            "clickablePolicies",
            "typeablePolicies",
            "scrollablePolicies",
            "selectablePolicies"
        ]);

        return (workspaceDocument?.policyDefinitions || []).filter((policyDefinition) => leftPolicyKeys.has(policyDefinition.propertyKey));
    }

    function rightPolicyDefinitions() {
        const leftPolicyKeys = new Set([
            "clickablePolicies",
            "typeablePolicies",
            "scrollablePolicies",
            "selectablePolicies"
        ]);

        return (workspaceDocument?.policyDefinitions || []).filter((policyDefinition) => !leftPolicyKeys.has(policyDefinition.propertyKey));
    }
</script>

{#if renderSidebar && workspaceDocument}
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
{/if}

{#if renderContent && selectedEditor === "policies-properties" && currentEditorDocument}
    <section class="editor-section">
        <div class="section-header">
            <div>
                <h2>{currentEditorDocument.title}</h2>
            </div>
            <button class="secondary" disabled={saving} on:click={currentEditorDocument.save}>
                {currentEditorDocument.saveLabel}
            </button>
        </div>
        <textarea bind:value={workspaceDocument.policiesProperties.content}></textarea>
    </section>
{:else if renderContent && selectedEditor === "java-policies"}
    <section class="editor-section">
        <div class="section-header">
            <div>
                <h2>Edit Java Policies</h2>
                <p>Review the policy classes resolved from policies.properties and create new policy implementations directly from each policy seam.</p>
            </div>
            <button class="secondary" disabled={saving} on:click={compileWorkspaceProfile}>
                Compile Profile
            </button>
        </div>

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
                    <p>Java profile compilation succeeded for the current policy composition profile.</p>
                </div>
            {:else}
                <div class="compile-results-empty">
                    <p>Run "Compile Profile" to validate every Java policy in this composition profile.</p>
                </div>
            {/if}
        </section>

        <section class="manager-card top-gap">
            <div class="policy-definition-columns">
                <div class="policy-definition-list">
                    {#each leftPolicyDefinitions() as policyDefinition}
                        <article class="policy-definition-item">
                            <div>
                                <strong>{policyDefinition.label}</strong>
                                <small>{policyDefinition.configuredClassNames?.length || 0} configured</small>
                            </div>
                            <div class="policy-definition-actions">
                                <button
                                    type="button"
                                    class="secondary"
                                    disabled={saving}
                                    on:click={() => createPolicySource(policyDefinition)}
                                >
                                    Add Java Policy
                                </button>
                            </div>
                        </article>
                    {/each}
                </div>
                <div class="policy-definition-list">
                    {#each rightPolicyDefinitions() as policyDefinition}
                        <article class="policy-definition-item">
                            <div>
                                <strong>{policyDefinition.label}</strong>
                                <small>{policyDefinition.configuredClassNames?.length || 0} configured</small>
                            </div>
                            <div class="policy-definition-actions">
                                <button
                                    type="button"
                                    class="secondary"
                                    disabled={saving}
                                    on:click={() => createPolicySource(policyDefinition)}
                                >
                                    Add Java Policy
                                </button>
                            </div>
                        </article>
                    {/each}
                </div>
            </div>
        </section>

        <section class="manager-card top-gap">
            <h3>Policies Active In Profile</h3>
            <div class="source-list manager-list">
                {#if activePolicySourceFiles.length > 0}
                    {#each activePolicySourceFiles as sourceFile}
                        <div class="source-item manager-source-item" class:selected={isPolicySourceSelected(sourceFile)}>
                            <button
                                type="button"
                                class="manager-source-open"
                                on:click={() => selectSource(sourceFile.name, "java-policies")}
                            >
                                <span>{sourceFile.name}</span>
                                <small>active</small>
                            </button>
                            <button
                                type="button"
                                class="secondary manager-inline-action"
                                disabled={saving}
                                on:click={() => togglePolicySourceActivation(sourceFile, false)}
                            >
                                Disable
                            </button>
                        </div>
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
                        <div class="source-item manager-source-item" class:selected={isPolicySourceSelected(sourceFile)}>
                            <button
                                type="button"
                                class="manager-source-open"
                                on:click={() => selectSource(sourceFile.name, "java-policies")}
                            >
                                <span>{sourceFile.name}</span>
                                <small>available</small>
                            </button>
                            <button
                                type="button"
                                class="secondary manager-inline-action"
                                disabled={saving}
                                on:click={() => togglePolicySourceActivation(sourceFile, true)}
                            >
                                Enable
                            </button>
                        </div>
                    {/each}
                {:else}
                    <p class="progress-message">No extra policy Java files were found in this workspace.</p>
                {/if}
            </div>
        </section>

        {#if policySourceModalVisible(selectedSourceFile)}
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
                            <div class="button-row">
                                <button class="secondary" disabled={saving} on:click={compileSelectedJavaSource}>
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
                                    <p>Java compilation succeeded for the current policy source file.</p>
                                </div>
                            {:else}
                                <div class="compile-results-empty">
                                    <p>Run "Save and Compile" to validate the current policy source file.</p>
                                </div>
                            {/if}
                        </section>
                        <textarea bind:value={selectedSourceFile.content}></textarea>
                    </div>
                </div>
            </div>
        {/if}
    </section>
{/if}
