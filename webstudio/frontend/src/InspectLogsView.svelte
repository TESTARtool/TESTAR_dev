<script>
    export let debugFiles = [];
    export let loadDebugFile;
    export let selectedDebugFile = null;
</script>

<section class="panel panel-wide status-panel">
    <div class="status-panel-header">
        <div>
            <p class="eyebrow">Debug Files</p>
            <h2>Inspect Debug Files</h2>
        </div>
    </div>

    <div class="results-layout">
        <section class="status-card">
            <h3>Available Log Files</h3>
            <div class="source-list result-list">
                {#if debugFiles?.length > 0}
                    {#each debugFiles as debugFile}
                        <button
                            class:selected={selectedDebugFile?.path === debugFile.path}
                            class="source-item debug-file-item"
                            on:click={() => loadDebugFile(debugFile)}
                        >
                            <span>{debugFile.name}</span>
                        </button>
                    {/each}
                {:else}
                    <p class="progress-message">No debug log files are currently available in the TESTAR runtime directory.</p>
                {/if}
            </div>
        </section>
    </div>

    <section class="status-console results-preview">
        <div class="section-header">
            <div>
                <h3>{selectedDebugFile?.name || "Log Preview"}</h3>
                <p>{selectedDebugFile?.path || "Select a log file to inspect its content."}</p>
            </div>
        </div>
        <pre class="code console-output">{selectedDebugFile?.content || "No log file selected."}</pre>
    </section>
</section>
