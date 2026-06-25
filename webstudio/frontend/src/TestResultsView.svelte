<script>
    import {
        filterResultFiles,
        filterResultGroups,
        formatResultFileLabel,
        sortedResultGroups,
        summarizeResultGroup
    } from "./testResultsViewModel.js";

    export let deleteResultFile;
    export let deleteResultGroup;
    export let loadResultFile;
    export let resultsData = null;
    export let resultSource = "generate";
    export let selectedResultFile = null;
    export let selectedResultGroup = null;
    export let selectResultSource;
    export let selectResultGroup;
    export let showResultSummary;

    let outputSortMode = "latest";
    let outputFilterMode = "all";
    let fileFilterMode = "all";
    let deleteDialog = {
        open: false,
        type: "",
        title: "",
        message: "",
        targetName: ""
    };

    function openDeleteGroupDialog() {
        if (!selectedResultGroup) {
            return;
        }

        deleteDialog = {
            open: true,
            type: "group",
            title: "Delete Output Result Folder",
            message: "This deletes the whole output result folder and all reports inside it. This action cannot be undone.",
            targetName: selectedResultGroup.name
        };
    }

    function openDeleteFileDialog() {
        if (!selectedResultFile) {
            return;
        }

        deleteDialog = {
            open: true,
            type: "file",
            title: "Delete Generated File",
            message: "This deletes only the selected generated report file. This action cannot be undone.",
            targetName: selectedResultFile.name
        };
    }

    function closeDeleteDialog() {
        deleteDialog = {
            open: false,
            type: "",
            title: "",
            message: "",
            targetName: ""
        };
    }

    function closeDeleteDialogFromBackdrop(event) {
        if (event.currentTarget === event.target) {
            closeDeleteDialog();
        }
    }

    async function confirmDelete() {
        const deleteType = deleteDialog.type;
        closeDeleteDialog();

        if (deleteType === "file") {
            await deleteResultFile(selectedResultFile);
            return;
        }

        if (deleteType === "group") {
            await deleteResultGroup(selectedResultGroup);
        }
    }

    $: sortedGroups = sortedResultGroups(resultsData?.groups || [], outputSortMode);
    $: visibleResultGroups = filterResultGroups(sortedGroups, outputFilterMode);
    $: visibleResultFiles = filterResultFiles(selectedResultGroup?.files || [], fileFilterMode);
    $: resultSummary = summarizeResultGroup(selectedResultGroup);
    $: maxVerdictCount = Math.max(...resultSummary.verdictGroups.map((group) => group.count), 1);
    $: totalOutcomeCount = Math.max(resultSummary.okCount + resultSummary.failedVerdictCount, 1);
    $: showingReport = selectedResultFile !== null;
</script>

<section class="panel panel-wide status-panel results-workspace">
    <div class="results-workspace-layout">
        <aside class="status-card results-sidebar">
            <section class="results-sidebar-section">
                <div class="results-source-header">
                    <h4 class="eyebrow">Output Results</h4>
                    <div class="button-row results-source-toggle">
                        <button
                            type="button"
                            class:secondary={resultSource !== "generate"}
                            on:click={() => selectResultSource("generate")}
                        >
                            Generate
                        </button>
                        <button
                            type="button"
                            class:secondary={resultSource !== "cli"}
                            on:click={() => selectResultSource("cli")}
                        >
                            CLI
                        </button>
                    </div>
                </div>

                <div class="results-filter-row">
                    <label>
                        <span>Sort</span>
                        <select bind:value={outputSortMode}>
                            <option value="latest">Latest first</option>
                            <option value="oldest">Oldest first</option>
                            <option value="name">Name</option>
                        </select>
                    </label>
                    <label>
                        <span>Filter</span>
                        <select bind:value={outputFilterMode}>
                            <option value="all">All</option>
                            <option value="failed">Failures</option>
                            <option value="ok">OK only</option>
                        </select>
                    </label>
                </div>

                <div class="source-list result-list result-list-tall">
                    {#if visibleResultGroups.length > 0}
                        {#each visibleResultGroups as resultGroup}
                            <button
                                class:selected={selectedResultGroup?.path === resultGroup.path}
                                class:result-file-failed={resultGroup.status === "failed"}
                                class:result-file-ok={resultGroup.status !== "failed"}
                                class="source-item result-file-item"
                                on:click={() => selectResultGroup(resultGroup)}
                            >
                                <span>{resultGroup.name}</span>
                            </button>
                        {/each}
                    {:else if resultsData?.groups?.length > 0}
                        <p class="progress-message">No output results match the current filter.</p>
                    {:else}
                        <p class="progress-message">No {resultSource === "cli" ? "CLI" : "Generate"} output groups detected yet.</p>
                    {/if}
                </div>

                <div class="results-delete-actions">
                    <button
                        type="button"
                        class="secondary"
                        disabled={!selectedResultGroup}
                        on:click={openDeleteGroupDialog}
                    >
                        Delete Output Folder
                    </button>
                </div>
            </section>

            <section class="results-sidebar-section">
                <div class="results-source-header">
                    <h4 class="eyebrow">Generated Verdict Files</h4>
                </div>
                <div class="results-filter-row results-filter-row-single">
                    <label>
                        <span>Filter</span>
                        <select bind:value={fileFilterMode}>
                            <option value="all">All</option>
                            <option value="failed">Failures</option>
                            <option value="ok">OK only</option>
                        </select>
                    </label>
                </div>
                <div class="source-list result-list result-list-medium">
                    {#if visibleResultFiles.length > 0}
                        {#each visibleResultFiles as resultFile}
                            <button
                                class:selected={selectedResultFile?.name === resultFile.name}
                                class:result-file-failed={resultFile.status === "failed"}
                                class:result-file-ok={resultFile.status !== "failed"}
                                class="source-item result-file-item"
                                title={resultFile.name}
                                on:click={() => loadResultFile(resultFile)}
                            >
                                <span>{formatResultFileLabel(resultFile.name)}</span>
                            </button>
                        {/each}
                    {:else if selectedResultGroup}
                        <p class="progress-message">No generated files match the current filter.</p>
                    {:else}
                        <p class="progress-message">Select an output result folder to inspect generated files.</p>
                    {/if}
                </div>
                <div class="results-delete-actions">
                    <button
                        type="button"
                        class="secondary"
                        disabled={!selectedResultFile}
                        on:click={openDeleteFileDialog}
                    >
                        Delete File
                    </button>
                </div>
            </section>
        </aside>

        <section class="status-card results-report-panel">
            <header class="results-report-header">
                <div>
                    <h4 class="eyebrow">Result Preview</h4>
                </div>
                {#if selectedResultGroup}
                    <p class="results-report-meta">{showingReport ? selectedResultFile.name : `${selectedResultGroup.name} summary`}</p>
                {/if}
                {#if showingReport}
                    <button type="button" class="secondary" on:click={showResultSummary}>
                        Back To Summary
                    </button>
                {/if}
            </header>

            <div class="status-console results-preview">
                {#if showingReport && selectedResultFile?.contentType === "text/html"}
                    <iframe class="result-frame" title={selectedResultFile.name} srcdoc={selectedResultFile.content}></iframe>
                {:else if showingReport}
                    <pre class="code console-output">{selectedResultFile?.content || "No result file selected."}</pre>
                {:else if selectedResultGroup}
                    <section class="results-summary-dashboard">
                        <section class="results-summary-card">
                            <div class="verdict-summary-grid">
                                <div class="verdict-summary-header-inline">
                                    <span class="eyebrow">Verdict Outcomes</span>
                                    <div class="verdict-summary-totals">
                                        <span class="verdict-total verdict-total-ok">{resultSummary.okCount} OK</span>
                                        <span class="verdict-total verdict-total-failed">{resultSummary.failedVerdictCount} FAILED</span>
                                    </div>
                                </div>

                                <div class="verdict-outcome-track" aria-hidden="true">
                                    <div
                                        class="verdict-outcome-segment verdict-outcome-segment-ok"
                                        style={`width: ${(resultSummary.okCount / totalOutcomeCount) * 100}%`}
                                    ></div>
                                    <div
                                        class="verdict-outcome-segment verdict-outcome-segment-failed"
                                        style={`width: ${(resultSummary.failedVerdictCount / totalOutcomeCount) * 100}%`}
                                    ></div>
                                </div>

                                {#if resultSummary.verdictGroups.length > 0}
                                    <div class="verdict-types-scroll">
                                        {#each resultSummary.verdictGroups as verdictGroup}
                                            <article class="verdict-type-row">
                                                <div class="verdict-summary-copy">
                                                    <span class="verdict-summary-label">{verdictGroup.label}</span>
                                                    <strong>{verdictGroup.count}</strong>
                                                </div>
                                                <div class="verdict-summary-bar-track">
                                                    <div
                                                        class="verdict-summary-bar"
                                                        style={`width: ${(verdictGroup.count / maxVerdictCount) * 100}%; background: linear-gradient(90deg, ${verdictGroup.color}, ${verdictGroup.color});`}
                                                    ></div>
                                                </div>
                                            </article>
                                        {/each}
                                    </div>
                                {:else}
                                    <p class="progress-message">No test result files were generated with failures.</p>
                                {/if}
                            </div>
                        </section>
                        <section class="results-run-metadata">
                            <article>
                                <span class="eyebrow">Selected Run</span>
                                <strong>{selectedResultGroup.name}</strong>
                            </article>
                            <article>
                                <span class="eyebrow">Generated Verdict Files</span>
                                <strong>{selectedResultGroup.files?.length || 0}</strong>
                            </article>
                            <article>
                                <span class="eyebrow">Sequences</span>
                                <strong>{selectedResultGroup.totalSequenceCount || selectedResultGroup.files?.length || 0}</strong>
                            </article>
                        </section>
                    </section>
                {:else}
                    <div class="composition-modal-empty results-empty-state">
                        <h4>No Test Results Selected</h4>
                        <p>Select an output result folder to inspect the run-level verdict summary.</p>
                    </div>
                {/if}
            </div>
        </section>
    </div>

    {#if deleteDialog.open}
        <div class="composition-modal-backdrop" role="presentation" on:click={closeDeleteDialogFromBackdrop}>
            <div class="composition-modal state-model-dialog" role="dialog" tabindex="-1" aria-modal="true" aria-labelledby="delete-results-title">
                <div class="composition-modal-header">
                    <div>
                        <span class="flow-node-kicker">Confirm Delete</span>
                        <h3 id="delete-results-title">{deleteDialog.title}</h3>
                    </div>
                </div>
                <div class="composition-modal-body">
                    <p>{deleteDialog.message}</p>
                    <strong class="results-delete-dialog-target">{deleteDialog.targetName}</strong>
                    <div class="button-row results-delete-dialog-actions">
                        <button type="button" class="secondary" on:click={closeDeleteDialog}>
                            Cancel
                        </button>
                        <button type="button" on:click={confirmDelete}>
                            Delete
                        </button>
                    </div>
                </div>
            </div>
        </div>
    {/if}
</section>
