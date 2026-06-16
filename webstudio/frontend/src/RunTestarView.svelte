<script>
    import { tick } from "svelte";

    export let saving = false;
    export let scriptlessStatus = null;
    export let selectedWorkspaceName = "";
    export let startGenerate;
    export let stopGenerate;

    let consoleOutputElement = null;
    let lastRenderedConsoleOutput = "";

    $: completedSequenceCount = scriptlessStatus?.sequenceOutcomes?.length || 0;
    $: plannedSequenceCount = scriptlessStatus?.plannedSequenceCount || 0;
    $: testResultsOutcomeTitle = plannedSequenceCount > 0
        ? `Test Results Outcomes (${completedSequenceCount}/${plannedSequenceCount})`
        : "Test Results Outcomes";

    $: if (consoleOutputElement && scriptlessStatus?.consoleOutput !== undefined) {
        const currentConsoleOutput = scriptlessStatus.consoleOutput || "";
        if (currentConsoleOutput !== lastRenderedConsoleOutput) {
            lastRenderedConsoleOutput = currentConsoleOutput;
            tick().then(() => {
                if (consoleOutputElement) {
                    consoleOutputElement.scrollTop = consoleOutputElement.scrollHeight;
                }
            });
        }
    }
</script>

<section class="panel panel-wide status-panel">
    <div class="status-panel-header">
        <div>
            <p class="eyebrow">Runtime</p>
            <h2>Generate Run</h2>
        </div>
        <div class="button-row">
            <button on:click={startGenerate} disabled={!selectedWorkspaceName || saving || scriptlessStatus?.status === "running"}>
                Run Generate
            </button>
            <button class="secondary" on:click={stopGenerate} disabled={saving}>
                Stop
            </button>
        </div>
    </div>

    <div class="status-grid">
        <section class="status-card">
            <div class="progress-panel">
                <div class="progress-header">
                    <strong>{scriptlessStatus?.status || "idle"}</strong>
                </div>
                <div class="progress-track">
                    <div
                        class:progress-running={scriptlessStatus?.status === "running"}
                        class:progress-error={scriptlessStatus?.status === "error"}
                        class:progress-idle={scriptlessStatus?.status !== "running" && scriptlessStatus?.status !== "error"}
                        class="progress-bar"
                    ></div>
                </div>
                <p class="progress-message">{scriptlessStatus?.message || "No scriptless status available."}</p>
            </div>
        </section>

        <section class="status-card">
            <h3>{testResultsOutcomeTitle}</h3>
            <div class="sequence-graph">
                {#if scriptlessStatus?.sequenceOutcomes?.length > 0}
                    {#each scriptlessStatus.sequenceOutcomes as sequenceOutcome}
                        <div
                            class:sequence-failed={sequenceOutcome.status === "failed"}
                            class:sequence-ok={sequenceOutcome.status !== "failed"}
                            class="sequence-block"
                            title={`Sequence ${sequenceOutcome.sequenceNumber}: ${sequenceOutcome.status === "failed" ? "Failed" : "OK"}`}
                        >
                            <span>{sequenceOutcome.sequenceNumber}</span>
                        </div>
                    {/each}
                {:else}
                    <p class="progress-message">No completed sequences yet.</p>
                {/if}
            </div>
        </section>
    </div>

    <section class="status-console">
        <div class="section-header">
            <div>
                <h3>Console Output</h3>
                <p>Live TESTAR process output captured by Web Studio.</p>
            </div>
        </div>
        <pre bind:this={consoleOutputElement} class="code console-output">{scriptlessStatus?.consoleOutput || "No console output yet."}</pre>
    </section>
</section>
