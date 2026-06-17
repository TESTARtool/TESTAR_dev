<script>
    import { tick } from "svelte";

    export let saving = false;
    export let scriptlessStatus = null;
    export let selectedWorkspaceName = "";
    export let startGenerate;
    export let stopGenerate;

    let consoleOutputElement = null;
    let lastRenderedConsoleOutput = "";

    function formatSequenceOutcomeLabel(sequenceOutcome) {
        if (!sequenceOutcome) {
            return "";
        }

        if (sequenceOutcome.label) {
            return sequenceOutcome.label;
        }

        if (sequenceOutcome.outputPath) {
            const outputPathSegments = sequenceOutcome.outputPath.split(/[\\/]/);
            const fileName = outputPathSegments[outputPathSegments.length - 1] || "";
            const trimmedExtension = fileName.replace(/\.html?$/i, "");
            const sequenceIndex = trimmedExtension.indexOf("_sequence_");
            if (sequenceIndex >= 0) {
                return trimmedExtension.substring(sequenceIndex + 1);
            }

            if (trimmedExtension) {
                return trimmedExtension;
            }
        }

        return `sequence_${sequenceOutcome.sequenceNumber}`;
    }

    $: completedSequenceCount = scriptlessStatus?.sequenceOutcomes?.length || 0;
    $: plannedSequenceCount = scriptlessStatus?.plannedSequenceCount || 0;
    $: testResultsOutcomeProgress = plannedSequenceCount > 0
        ? `${completedSequenceCount}/${plannedSequenceCount}`
        : null;

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
            <h2>Generate Mode Run</h2>
        </div>
        <div class="button-row">
            <button on:click={startGenerate} disabled={!selectedWorkspaceName || saving || scriptlessStatus?.status === "running"}>
                Run Generate Mode
            </button>
            <button class="secondary" on:click={stopGenerate} disabled={saving}>
                Stop
            </button>
        </div>
    </div>

    <div class="status-grid run-status-grid">
        <section class="status-card run-outcomes-card">
            <div class="run-outcomes-header">
                <h3>Test Results Outcomes</h3>
                {#if testResultsOutcomeProgress}
                    <span class="run-outcomes-progress">{testResultsOutcomeProgress}</span>
                {/if}
            </div>
            {#if scriptlessStatus?.sequenceOutcomes?.length > 0}
                <div class="run-outcomes-layout">
                    <section class="run-outcomes-panel">
                        <div class="sequence-graph run-sequence-graph">
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
                        </div>
                    </section>

                    <section class="run-outcomes-panel">
                        <div class="run-outcome-list">
                            {#each scriptlessStatus.sequenceOutcomes as sequenceOutcome}
                                <article
                                    class:run-outcome-item-failed={sequenceOutcome.status === "failed"}
                                    class:run-outcome-item-ok={sequenceOutcome.status !== "failed"}
                                    class="run-outcome-item"
                                >
                                    <div class="run-outcome-item-copy">
                                        <span>{formatSequenceOutcomeLabel(sequenceOutcome)}</span>
                                    </div>
                                    <span class="run-outcome-item-status">{sequenceOutcome.status === "failed" ? "FAILED" : "OK"}</span>
                                </article>
                            {/each}
                        </div>
                    </section>
                </div>
            {:else}
                <p class="progress-message">No completed sequences yet.</p>
            {/if}
        </section>
    </div>

    <section class="status-console">
        <div class="section-header">
            <div>
                <h3>Console Output</h3>
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
        </div>
        <pre bind:this={consoleOutputElement} class="code console-output">{scriptlessStatus?.consoleOutput || "No console output yet."}</pre>
    </section>
</section>
