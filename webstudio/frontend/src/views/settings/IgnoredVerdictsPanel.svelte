<script>
    import { clearIgnoredVerdicts, loadIgnoredVerdicts, removeIgnoredVerdicts } from "./ignoredVerdictsApi.js";
    export let loadJson;
    export let workspaceName;

    let entries = [];
    let selected = new Set();
    let loadedWorkspace = "";
    let loading = false;
    let saving = false;
    let error = "";
    let confirmAction = "";

    $: if (workspaceName && workspaceName !== loadedWorkspace) {
        loadedWorkspace = workspaceName;
        entries = [];
        selected = new Set();
        refresh(workspaceName);
    }

    async function refresh(name = workspaceName) {
        loading = true;
        error = "";
        try {
            const loaded = await loadIgnoredVerdicts(loadJson, name);
            if (name === workspaceName) {
                entries = loaded;
            }
        } catch (cause) {
            if (name === workspaceName) {
                error = cause.message;
            }
        } finally {
            if (name === workspaceName) {
                loading = false;
            }
        }
    }

    function toggleEntry(entry, checked) {
        const next = new Set(selected);
        if (checked) {
            next.add(entry);
        } else {
            next.delete(entry);
        }
        selected = next;
    }

    async function confirmChange() {
        const action = confirmAction;
        confirmAction = "";
        saving = true;
        error = "";
        try {
            entries = action === "remove"
                ? await removeIgnoredVerdicts(loadJson, workspaceName, [...selected])
                : await clearIgnoredVerdicts(loadJson, workspaceName);
            selected = new Set();
        } catch (cause) {
            error = cause.message;
        } finally {
            saving = false;
        }
    }
</script>

<section class="status-card ignored-verdicts-panel">
    <div class="section-header">
        <div>
            <h3>Ignored Verdicts</h3>
            <p>Previously reported verdict messages for this workspace. Changes apply to future runs.</p>
        </div>
        <div class="button-row">
            <button type="button" class="secondary" disabled={loading || saving || selected.size === 0}
                on:click={() => confirmAction = "remove"}>Remove selected</button>
            <button type="button" class="secondary" disabled={loading || saving || entries.length === 0}
                on:click={() => confirmAction = "clear"}>Clear all</button>
        </div>
    </div>
    {#if loading}
        <p class="progress-message">Loading ignored verdicts...</p>
    {:else if error}
        <p class="progress-message">{error}</p>
    {:else if entries.length === 0}
        <p class="progress-message">No ignored verdicts for this workspace.</p>
    {:else}
        <div class="ignored-verdicts-list">
            {#each entries as entry (entry)}
                <label class="settings-checkbox-row">
                    <input type="checkbox" checked={selected.has(entry)}
                        on:change={(event) => toggleEntry(entry, event.currentTarget.checked)} />
                    <span>{entry}</span>
                </label>
            {/each}
        </div>
    {/if}
</section>

{#if confirmAction}
    <div class="composition-modal-backdrop" role="presentation">
        <div class="composition-modal state-model-dialog" role="dialog" aria-modal="true" aria-labelledby="ignored-verdicts-confirm-title">
            <div class="composition-modal-header">
                <h3 id="ignored-verdicts-confirm-title">{confirmAction === "clear" ? "Clear Ignored Verdicts" : "Remove Ignored Verdicts"}</h3>
            </div>
            <div class="composition-modal-body">
                <p>{confirmAction === "clear" ? "Remove all ignored verdicts for this workspace?" : `Remove ${selected.size} selected ignored verdict(s)?`}</p>
                <div class="button-row">
                    <button type="button" class="secondary" on:click={() => confirmAction = ""}>Cancel</button>
                    <button type="button" on:click={confirmChange}>Confirm</button>
                </div>
            </div>
        </div>
    </div>
{/if}
