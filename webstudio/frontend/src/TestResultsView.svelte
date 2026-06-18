<script>
    export let loadResultFile;
    export let resultsData = null;
    export let resultSource = "generate";
    export let selectedResultFile = null;
    export let selectedResultGroup = null;
    export let selectResultSource;
    export let selectResultGroup;

    const verdictPattern = /_V\d+_([^.]+)\.html$/i;
    const verdictColorPalette = [
        "#c26a2d",
        "#d44949",
        "#7d5bd6",
        "#2b7a9a",
        "#8f4da8",
        "#d27b2c",
        "#4f7d47",
        "#9c4f36",
        "#5a6fc7",
        "#b6577e"
    ];

    function formatVerdictLabel(verdictKey) {
        if (!verdictKey) {
            return "Unknown";
        }

        return verdictKey;
    }

    function formatResultFileLabel(fileName) {
        if (!fileName) {
            return "";
        }

        const trimmedExtension = fileName.replace(/\.html?$/i, "");
        const sequenceIndex = trimmedExtension.indexOf("_sequence_");
        if (sequenceIndex >= 0) {
            return trimmedExtension.substring(sequenceIndex + 1);
        }

        return trimmedExtension;
    }

    function colorForVerdict(verdictKey) {
        if (!verdictKey) {
            return verdictColorPalette[0];
        }

        let hash = 0;
        for (let index = 0; index < verdictKey.length; index += 1) {
            hash = ((hash << 5) - hash) + verdictKey.charCodeAt(index);
            hash |= 0;
        }

        return verdictColorPalette[Math.abs(hash) % verdictColorPalette.length];
    }

    function summarizeResultGroup(resultGroup) {
        if (!resultGroup) {
            return {
                okCount: 0,
                failedSequenceCount: 0,
                failedVerdictCount: 0,
                verdictGroups: []
            };
        }

        const verdictCounts = new Map();
        const failedSequences = new Set();
        let explicitOkCount = 0;

        for (const resultFile of resultGroup.files || []) {
            const verdictMatch = resultFile.name.match(verdictPattern);
            if (!verdictMatch) {
                continue;
            }

            const verdictKey = verdictMatch[1];
            if (verdictKey === "OK") {
                explicitOkCount += 1;
                continue;
            }

            verdictCounts.set(verdictKey, (verdictCounts.get(verdictKey) || 0) + 1);

            const sequenceMatch = resultFile.name.match(/_sequence_(\d+)/i);
            if (sequenceMatch) {
                failedSequences.add(sequenceMatch[1]);
            }
        }

        const totalSequences = resultGroup.totalSequenceCount || 0;
        const okCount = Math.max(explicitOkCount, Math.max(0, totalSequences - failedSequences.size));

        const verdictGroups = Array.from(verdictCounts.entries())
            .map(([key, count]) => ({
                key,
                label: formatVerdictLabel(key),
                count,
                color: colorForVerdict(key)
            }))
            .sort((left, right) => right.count - left.count || left.label.localeCompare(right.label));

        const failedVerdictCount = verdictGroups.reduce((total, group) => total + group.count, 0);

        return {
            okCount,
            failedSequenceCount: failedSequences.size,
            failedVerdictCount,
            verdictGroups
        };
    }

    $: resultSummary = summarizeResultGroup(selectedResultGroup);
    $: maxVerdictCount = Math.max(...resultSummary.verdictGroups.map((group) => group.count), 1);
    $: totalOutcomeCount = Math.max(resultSummary.okCount + resultSummary.failedVerdictCount, 1);
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
                <div class="source-list result-list result-list-tall">
                    {#if resultsData?.groups?.length > 0}
                        {#each resultsData.groups as resultGroup}
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
                        <p class="progress-message">No {resultSource === "cli" ? "CLI" : "Generate"} output groups detected yet.</p>
                    {/if}
                </div>
            </section>

            <section class="results-sidebar-section status-card results-summary-card">
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

            <section class="results-sidebar-section">
                <h4 class="eyebrow">Generated Files</h4>
                <div class="source-list result-list result-list-medium">
                    {#if selectedResultGroup?.files?.length > 0}
                        {#each selectedResultGroup.files as resultFile}
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
                    {:else}
                        <p class="progress-message">No generated files detected for this output group.</p>
                    {/if}
                </div>
            </section>
        </aside>

        <section class="status-card results-report-panel">
            <header class="results-report-header">
                <div>
                    <h4 class="eyebrow">Result Preview</h4>
                </div>
                {#if selectedResultGroup}
                    <p class="results-report-meta">{selectedResultFile?.name || "No result file selected."}</p>
                {/if}
            </header>

            <div class="status-console results-preview">
                {#if selectedResultFile?.contentType === "text/html"}
                    <iframe class="result-frame" title={selectedResultFile.name} srcdoc={selectedResultFile.content}></iframe>
                {:else}
                    <pre class="code console-output">{selectedResultFile?.content || "No result file selected."}</pre>
                {/if}
            </div>
        </section>
    </div>
</section>
