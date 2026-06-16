<script>
    export let loadResultFile;
    export let scriptlessResults = null;
    export let selectedResultFile = null;
    export let selectedResultGroup = null;
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

<section class="panel panel-wide status-panel">
    <div class="results-top-layout">
        <section class="status-card results-groups-card">
            <h4 class="eyebrow">Generated Test Output Results</h4>
            <div class="source-list result-list result-list-tall">
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

        <section class="status-card results-summary-card">
            <div class="verdict-summary-grid">
                <section class="verdict-summary-section verdict-summary-section-outcomes">
                    <div class="verdict-summary-section-header">
                        <span class="eyebrow">Verdict Outcomes</span>
                    </div>

                    <div class="verdict-outcome-card">
                        <div class="verdict-outcome-stats">
                            <article class="verdict-outcome-stat verdict-outcome-stat-ok">
                                <span class="verdict-summary-label">OK</span>
                                <strong>{resultSummary.okCount}</strong>
                            </article>
                            <article class="verdict-outcome-stat verdict-outcome-stat-failed">
                                <span class="verdict-summary-label">FAILED</span>
                                <strong>{resultSummary.failedVerdictCount}</strong>
                            </article>
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
                    </div>
                </section>

                <section class="verdict-summary-section verdict-summary-section-types verdict-types-section">
                    <div class="verdict-summary-section-header">
                        <span class="eyebrow">Failure Verdict Types</span>
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
                </section>
            </div>
        </section>
    </div>

    <section class="status-card results-files-card">
        <h4 class="eyebrow">Generated Files</h4>
        <div class="source-list result-list result-list-medium">
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

    <section class="status-console results-preview">
        {#if selectedResultFile?.contentType === "text/html"}
            <iframe class="result-frame" title={selectedResultFile.name} srcdoc={selectedResultFile.content}></iframe>
        {:else}
            <pre class="code console-output">{selectedResultFile?.content || "No result file selected."}</pre>
        {/if}
    </section>
</section>
