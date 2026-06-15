<script>
    export let loadResultFile;
    export let scriptlessResults = null;
    export let selectedResultFile = null;
    export let selectedResultGroup = null;
    export let selectResultGroup;
</script>

<section class="panel panel-wide status-panel">
    <div class="status-panel-header">
        <p class="eyebrow">Test Results</p>
    </div>

    <div class="results-layout">
        <section class="status-card">
            <h3>Generated Test Output Results</h3>
            <div class="source-list result-list">
                {#if scriptlessResults?.groups?.length > 0}
                    {#each scriptlessResults.groups as resultGroup}
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
                {:else}
                    <p class="progress-message">No generated output groups detected yet.</p>
                {/if}
            </div>
        </section>

        <section class="status-card">
            <h3>Generated Files</h3>
            <div class="source-list result-list">
                {#if selectedResultGroup?.files?.length > 0}
                    {#each selectedResultGroup.files as resultFile}
                        <button
                            class:selected={selectedResultFile?.name === resultFile.name}
                            class:result-file-failed={resultFile.status === "failed"}
                            class:result-file-ok={resultFile.status !== "failed"}
                            class="source-item result-file-item"
                            on:click={() => loadResultFile(resultFile)}
                        >
                            <span>{resultFile.name}</span>
                        </button>
                    {/each}
                {:else}
                    <p class="progress-message">No generated files detected for this output group.</p>
                {/if}
            </div>
        </section>
    </div>

    <section class="status-console results-preview">
        {#if selectedResultFile?.contentType === "text/html"}
            <iframe class="result-frame" title={selectedResultFile.name} srcdoc={selectedResultFile.content}></iframe>
        {:else}
            <pre class="code console-output">{selectedResultFile?.content || "No result file selected."}</pre>
        {/if}
    </section>
</section>
