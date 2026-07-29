<script>
    // Implements WS-UX-STATE-MODEL-001: state model status dialog with open/stop/close actions.
    export let dialog = {
        open: false,
        title: "",
        message: "",
        status: "STOPPED",
        url: "",
        running: false
    };
    export let saving = false;
    export let onOpen = () => {};
    export let onStop = () => {};
    export let onClose = () => {};

    function closeFromBackdrop(event) {
        if (event.currentTarget === event.target) {
            onClose();
        }
    }
</script>

{#if dialog.open}
    <div class="composition-modal-backdrop" role="presentation" on:click={closeFromBackdrop}>
        <div
            class="composition-modal state-model-dialog"
            role="dialog"
            aria-modal="true"
            aria-labelledby="state-model-dialog-title"
        >
            <div class="composition-modal-header">
                <div>
                    <h2 id="state-model-dialog-title">{dialog.title}</h2>
                    <p>{dialog.message}</p>
                </div>
            </div>
            <div class="composition-modal-actions">
                {#if dialog.running}
                    <button type="button" on:click={() => onOpen(dialog.url)}>
                        Open State Model
                    </button>
                    <button type="button" class="danger" on:click={onStop} disabled={saving}>
                        Stop State Model
                    </button>
                {/if}
                <button type="button" class="secondary" on:click={onClose}>
                    Close
                </button>
            </div>
        </div>
    </div>
{/if}
