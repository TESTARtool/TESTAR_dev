<script>
    // Implements WS-UX-TEST-SETTINGS-004: included and excluded Spy attributes.
    import {
        availableSpyTags,
        defaultSpyTags,
        selectedSpyTags,
        updateSpyTags
    } from "./spyTagsModel.js";

    export let loadJson;
    export let setting;
    export let setSettingValue;

    let options = [];
    let loading = true;
    let error = "";
    let search = "";

    loadJson("/api/settings/spy-tags")
        .then((available) => {
            options = available;
        })
        .catch((cause) => {
            error = cause.message;
        })
        .finally(() => {
            loading = false;
        });

    $: selected = selectedSpyTags(setting?.value);
    $: allOptions = availableSpyTags(options, setting?.value);
    $: searchText = search.trim().toLowerCase();
    $: visibleOptions = allOptions.filter((option) =>
        !searchText || option.key.toLowerCase().includes(searchText) || option.group.toLowerCase().includes(searchText)
    );
    $: includedOptions = visibleOptions.filter((option) => selected.has(option.key));
    $: excludedOptions = visibleOptions.filter((option) => !selected.has(option.key));

    function changeSelection(names, included) {
        setSettingValue(setting, updateSpyTags(setting.value, names, included));
    }

    function restoreDefaults() {
        setSettingValue(setting, defaultSpyTags(options));
    }
</script>

<div class="spy-tags-editor">
    {#if loading}
        <p class="progress-message">Loading available Spy attributes...</p>
    {:else if error}
        <p class="progress-message">Unable to load Spy attributes: {error}</p>
    {:else}
        <div class="spy-tags-toolbar">
            <label class="field-label" for="spy-tag-search">Find attribute</label>
            <input id="spy-tag-search" type="search" bind:value={search} placeholder="Search name or platform" />
        </div>

        <div class="button-row spy-tags-actions">
            <button type="button" class="secondary" on:click={() => changeSelection(options.map((option) => option.key), true)}>Include all</button>
            <button type="button" class="secondary" on:click={() => setSettingValue(setting, "")}>Exclude all</button>
            <button type="button" class="secondary" on:click={() => changeSelection(options.filter((option) => option.group === "Windows").map((option) => option.key), true)}>Include Windows tags</button>
            <button type="button" class="secondary" on:click={() => changeSelection(options.filter((option) => option.group === "WebDriver").map((option) => option.key), true)}>Include WebDriver tags</button>
            <button type="button" class="secondary" on:click={restoreDefaults}>Restore defaults</button>
        </div>

        <div class="spy-tags-lists">
            <fieldset class="spy-tags-list">
                <legend>Excluded ({allOptions.length - selected.size})</legend>
                <div class="spy-tags-list-content">
                    {#each excludedOptions as option (option.key)}
                        <label class="settings-checkbox-row" title={option.group}>
                            <input type="checkbox" checked={false} on:change={() => changeSelection([option.key], true)} />
                            <span>{option.key}</span>
                            <small>{option.group}</small>
                        </label>
                    {/each}
                </div>
            </fieldset>

            <fieldset class="spy-tags-list">
                <legend>Included ({selected.size})</legend>
                <div class="spy-tags-list-content">
                    {#each includedOptions as option (option.key)}
                        <label class="settings-checkbox-row" title={option.group}>
                            <input type="checkbox" checked={true} on:change={() => changeSelection([option.key], false)} />
                            <span>{option.key}</span>
                            <small>{option.group}</small>
                        </label>
                    {/each}
                </div>
            </fieldset>
        </div>
    {/if}
</div>
