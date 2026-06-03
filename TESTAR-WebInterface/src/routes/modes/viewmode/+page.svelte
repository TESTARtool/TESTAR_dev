<script lang="ts">
    import { onMount } from "svelte";
    import { expectedSequences } from "$lib/stores/progress";
    import {
        initWebSocket,
        wsLastMessage,
        wsStatus,
    } from "$lib/stores/websocket";

    type Run = {
        sequenceID?: string;
        directory?: string;
        files?: string[];
    };

    type ReportsResponse = {
        runs?: Run[];
        error?: string;
    };

    let loading = true;
    let loadMessage: string | null = null;
    let runReports: Run[] = [];

    function fileNameFromPath(path: string): string {
        return path.split(/[\\/]/).pop() ?? path;
    }

    async function loadReports() {
        loading = true;
        loadMessage = null;
        runReports = [];

        try {
            const response = await fetch("/api/reports");
            const payload = (await response.json()) as ReportsResponse;

            if (!response.ok) {
                loadMessage =
                    payload.error ??
                    `Could not load reports (${response.status})`;
                return;
            }

            const runs = payload.runs ?? [];

            if (runs.length === 0) {
                loadMessage = "No runs found.";
                return;
            }

            runReports = runs.map((run) => ({
                ...run,
                files: run.files?.filter((file) => file.trim() !== ""),
            }));
        } catch {
            loadMessage = "Could not reach the reports API.";
        } finally {
            loading = false;
        }
    }

    function viewFile(path: string) {
        window.open(
            `/api/reports/get?file=${encodeURIComponent(path)}`,
            "_blank",
        );
    }

    type Status = "OKAY" | "ERROR";
    let filesStatuses: Status[] = [];

    function addFile(status: Status) {
        if (filesStatuses.length < $expectedSequences) {
            filesStatuses = [...filesStatuses, status];
        }
    }

    $: progress =
        $expectedSequences > 0
            ? (filesStatuses.length / $expectedSequences) * 100
            : 0;

    interface LogEntry {
        id: number;
        message: string;
        timestamp: string;
    }

    let logs: LogEntry[] = [];
    let logContainer: HTMLDivElement;
    let waitingForFaultCheck: boolean = false;

    $: if ($wsLastMessage) {
        const msg = $wsLastMessage;

        if (msg.type === "log" && msg.log) {
            processLogMessage(msg.log);
        } else if (msg.type === "status") {
            if (msg.status === "finished") loadReports();
            addLog(`[${msg.status.toUpperCase()}] ${msg.message}`);
            scrollToBottom();
        } else if (msg.type === "error") {
            addLog(`[ERROR] ${msg.message}`);
            scrollToBottom();
        }
    }

    function processLogMessage(logMessage: string): void {
        if (waitingForFaultCheck) {
            if (isSequenceFault(logMessage)) {
                addFile("ERROR");
            } else {
                addFile("OKAY");
            }
            waitingForFaultCheck = false;
        }

        if (isSequenceFinished(logMessage)) {
            waitingForFaultCheck = true;
        }

        addLog(logMessage);
        scrollToBottom();
    }

    function isSequenceFinished(message: string): boolean {
        const pattern = /^Sequence \d+ finished\.(\n)?$/;
        return pattern.test(message);
    }

    function isSequenceFault(message: string): boolean {
        return message.trim() === "Sequence contained faults!";
    }

    function addLog(logMessage: string): void {
        logs = [
            ...logs,
            {
                id: Date.now() + Math.random(),
                message: logMessage,
                timestamp: new Date().toLocaleTimeString(),
            },
        ];
    }

    function scrollToBottom(): void {
        if (logContainer) {
            setTimeout(() => {
                logContainer.scrollTop = logContainer.scrollHeight;
            }, 0);
        }
    }

    function clearLog(): void {
        logs = [];
    }

    onMount(async () => {
        if ($wsStatus === WebSocket.CLOSED) initWebSocket();
        await loadReports();
    });
</script>

<section class="results">
    <header class="results__header">
        <div>
            <p class="results__eyebrow">Results</p>
            <h1 class="results__title">Latest Test Reports</h1>
            <p class="results__description">
                Open a generated HTML report to inspect actions and outcomes.
            </p>
        </div>
        <button
            class="results__refresh"
            type="button"
            on:click={loadReports}
            disabled={loading}
        >
            {loading ? "Refreshing…" : "Refresh"}
        </button>
    </header>

    {#if loading}
        <div class="results__card results__card--muted" role="status">
            Loading reports…
        </div>
    {:else if loadMessage}
        <div class="results__card results__card--warn" role="alert">
            <h2 class="results__card-title">No reports yet</h2>
            <p>{loadMessage}</p>
        </div>
    {:else if runReports.length === 0}
        <div class="results__card results__card--muted" role="status">
            No results are available yet.
        </div>
    {:else}
        {#each runReports as run}
            <div class="results__meta">
                <span><strong>Sequence:</strong> {run.sequenceID}</span>
                {#if run.directory}
                    <span><strong>Directory:</strong> {run.directory}</span>
                {/if}
            </div>

            <ul class="results__list" aria-label="Available report files">
                {#each run.files ?? [] as path}
                    <li class="results__item">
                        <button
                            class="results__file-button"
                            on:click={() => viewFile(path)}
                        >
                            <span class="results__file-name"
                                >{fileNameFromPath(path)}</span
                            >
                            <span class="results__file-path">{path}</span>
                        </button>
                    </li>
                {/each}
            </ul>
        {/each}
    {/if}
</section>

<br />

<div id="Bar" style="--total: {$expectedSequences}">
    {#each filesStatuses as status}
        <div class={status}></div>
    {/each}
</div>

<p>
    {filesStatuses.length} / {$expectedSequences} files received ({progress.toFixed(
        0,
    )}%)
</p>

<div class="container">
    <header>
        <h1>Log Viewer</h1>
    </header>

    <div class="controls">
        <button on:click={clearLog}>Clear Log</button>
    </div>

    <!-- Scrollable log window -->
    <div class="log-window" bind:this={logContainer}>
        {#each logs as log (log.id)}
            <div class="log-entry">
                <span class="timestamp">[{log.timestamp}]</span>
                <span class="message">{log.message}</span>
            </div>
        {/each}

        {#if logs.length === 0}
            <div class="empty-state">No logs yet. Waiting for messages...</div>
        {/if}
    </div>
</div>

<style>
    #Bar {
        width: 100%;
        height: 30px;
        background: grey;
        display: grid;
        grid-template-columns: repeat(var(--total), 1fr);
    }

    #Bar > div {
        flex: 1;
        height: 100%;
        transition: background-color 0.3s ease;
    }

    .OKAY {
        background-color: green;
    }

    .WARNING {
        background-color: yellow;
    }

    .ERROR {
        background-color: red;
    }

    .container {
        max-width: 900px;
        margin: 0 auto;
        padding: 20px;
        font-family: "Courier New", monospace;
    }

    header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 20px;
    }

    h1 {
        margin: 0;
        font-size: 24px;
    }

    .status {
        display: flex;
        align-items: center;
        gap: 8px;
        font-size: 14px;
    }

    .controls {
        display: flex;
        gap: 10px;
        align-items: center;
        margin-bottom: 15px;
    }

    button {
        padding: 8px 16px;
        background-color: #3b82f6;
        color: white;
        border: none;
        border-radius: 4px;
        cursor: pointer;
        font-size: 14px;
    }

    button:hover {
        background-color: #2563eb;
    }

    .log-window {
        height: 500px;
        overflow-y: auto;
        background-color: #ececf4;
        border: 2px solid #dcdee1;
        border-radius: 6px;
        padding: 15px;
        color: #ececf4;
    }

    .log-entry {
        padding: 6px 0;
        line-height: 1.5;
        border-bottom: 1px solid #dcdee1;
    }

    .log-entry:last-child {
        border-bottom: none;
    }

    .timestamp {
        color: #000000;
        margin-right: 8px;
    }

    .message {
        color: #000000;
    }

    .empty-state {
        color: #000000;
        text-align: center;
        padding: 20px;
        font-style: italic;
    }

    /* Custom scrollbar */
    .log-window::-webkit-scrollbar {
        width: 10px;
    }

    .log-window::-webkit-scrollbar-track {
        background: #111827;
    }

    .log-window::-webkit-scrollbar-thumb {
        background: #4b5563;
        border-radius: 5px;
    }

    .log-window::-webkit-scrollbar-thumb:hover {
        background: #6b7280;
    }

    .results {
        max-width: 1100px;
        margin: 0 auto;
        display: flex;
        flex-direction: column;
        gap: 16px;
    }

    .results__header {
        display: flex;
        align-items: center;
        justify-content: space-between;
        gap: 16px;
        padding: 24px 28px;
        border-radius: 20px;
        background: linear-gradient(135deg, #ffffff 0%, #eef2ff 100%);
        border: 1px solid rgba(148, 163, 184, 0.2);
        box-shadow: 0 18px 36px rgba(15, 23, 42, 0.08);
    }

    .results__eyebrow {
        margin: 0 0 6px;
        font-size: 12px;
        letter-spacing: 0.18em;
        text-transform: uppercase;
        color: #64748b;
        font-weight: 600;
    }

    .results__title {
        margin: 0 0 6px;
        color: #0f172a;
        font-size: 30px;
        line-height: 1.15;
    }

    .results__description {
        margin: 0;
        color: #334155;
        font-size: 15px;
    }

    .results__refresh {
        padding: 10px 16px;
        border-radius: 999px;
        border: 1px solid #2563eb;
        background: #2563eb;
        color: #fff;
        font-weight: 600;
        font-size: 14px;
        cursor: pointer;
        transition:
            transform 0.12s ease,
            box-shadow 0.12s ease,
            opacity 0.12s ease;
        box-shadow: 0 8px 18px rgba(37, 99, 235, 0.24);
    }

    .results__refresh:hover:not(:disabled) {
        transform: translateY(-1px);
        box-shadow: 0 10px 20px rgba(37, 99, 235, 0.3);
    }

    .results__refresh:disabled {
        opacity: 0.65;
        cursor: not-allowed;
    }

    .results__card {
        border-radius: 14px;
        padding: 16px 18px;
        border: 1px solid rgba(148, 163, 184, 0.35);
    }

    .results__card--muted {
        background: #f8fafc;
        color: #475569;
    }

    .results__card--warn {
        background: #fff5f5;
        border-color: #fecaca;
        color: #991b1b;
    }

    .results__card-title {
        margin: 0 0 8px;
        font-size: 18px;
    }

    .results__meta {
        display: flex;
        flex-direction: column;
        gap: 6px;
        color: #334155;
        font-size: 14px;
    }

    .results__list {
        list-style: none;
        padding: 0;
        margin: 0;
        display: flex;
        flex-direction: column;
        gap: 10px;
    }

    .results__item {
        margin: 0;
    }

    .results__file-button {
        width: 100%;
        text-align: left;
        padding: 14px 16px;
        border-radius: 12px;
        border: 1px solid rgba(148, 163, 184, 0.3);
        background: #fff;
        cursor: pointer;
        display: flex;
        flex-direction: column;
        gap: 4px;
        transition:
            border-color 0.12s ease,
            box-shadow 0.12s ease,
            transform 0.12s ease;
    }

    .results__file-button:hover {
        border-color: rgba(37, 99, 235, 0.45);
        box-shadow: 0 8px 18px rgba(15, 23, 42, 0.08);
        transform: translateY(-1px);
    }

    .results__file-name {
        font-size: 15px;
        font-weight: 700;
        color: #0f172a;
    }

    .results__file-path {
        font-size: 12px;
        color: #64748b;
        word-break: break-all;
    }
</style>
