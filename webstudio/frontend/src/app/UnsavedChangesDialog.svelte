<script>
    export let dialog = {
        open: false,
        title: "",
        message: "",
        saveLabel: "Save"
    };
    export let saving = false;
    export let onSave = () => {};
    export let onDiscard = () => {};
    export let onCancel = () => {};

    function closeFromBackdrop(event) {
        if (event.currentTarget === event.target) {
            onCancel();
        }
    }
</script>

{#if dialog.open}
    <div class="composition-modal-backdrop" role="presentation" on:click={closeFromBackdrop}>
        <div
            class="composition-modal state-model-dialog"
            role="dialog"
            aria-modal="true"
            aria-labelledby="unsaved-settings-dialog-title"
        >
            <div class="composition-modal-header">
                <div>
                    <h2 id="unsaved-settings-dialog-title">{dialog.title}</h2>
                    <p>{dialog.message}</p>
                </div>
            </div>
            <div class="composition-modal-actions">
                <button type="button" on:click={onSave} disabled={saving}>
                    {dialog.saveLabel}
                </button>
                <button type="button" class="secondary" on:click={onDiscard} disabled={saving}>
                    Discard
                </button>
                <button type="button" class="secondary" on:click={onCancel} disabled={saving}>
                    Cancel
                </button>
            </div>
        </div>
    </div>
{/if}
