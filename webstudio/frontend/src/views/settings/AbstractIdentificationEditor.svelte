<script>
    import {
        defaultAbstractStateTags,
        selectedAbstractStateTags,
        updatedAbstractStateTags
    } from "./abstractIdentificationModel.js";
    export let loadJson;
    export let setting;
    export let setSettingValue;

    let options = [];
    let error = "";
    let loading = true;

    loadJson("/api/settings/abstract-state-tags")
        .then((available) => {
            options = available;
        })
        .catch((cause) => {
            error = cause.message;
        })
        .finally(() => {
            loading = false;
        });

    $: selected = selectedAbstractStateTags(setting?.value);
    $: groups = [
        { id: "Common", title: "Common attributes" },
        { id: "Windows", title: "Windows attributes" },
        { id: "WebDriver", title: "WebDriver attributes" },
        { id: "Android", title: "Android attributes" }
    ];

    function updateSelection(key, checked) {
        setSettingValue(setting, updatedAbstractStateTags(setting.value, key, checked));
    }

    function restoreDefaults() {
        setSettingValue(setting, defaultAbstractStateTags(options));
    }
</script>

<div class="abstract-identification-editor">
    {#if loading}
        <p class="progress-message">Loading available attributes...</p>
    {:else if error}
        <p class="progress-message">Unable to load attributes: {error}</p>
    {:else}
        <div class="abstract-identification-groups">
            {#each groups as group}
                <fieldset class="settings-group-card" class:abstract-identification-common={group.id === "Common"}>
                    <legend>{group.title}</legend>
                    <div class="abstract-identification-options">
                        {#each options.filter((option) => option.group === group.id) as option (option.key)}
                            <label class="settings-checkbox-row" title={option.label}>
                                <input type="checkbox" checked={selected.has(option.key)}
                                    on:change={(event) => updateSelection(option.key, event.currentTarget.checked)} />
                                <span>{option.key}</span>
                            </label>
                        {/each}
                    </div>
                </fieldset>
            {/each}
        </div>
        <button type="button" class="secondary" on:click={restoreDefaults}>Restore defaults</button>
    {/if}
</div>
