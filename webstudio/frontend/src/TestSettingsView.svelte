<script>
    export let currentEditorDocument = null;
    export let loading = false;
    export let openTestSettings;
    export let openVisualSettings;
    export let regexValidationResults = {};
    export let renderContent = true;
    export let renderSidebar = true;
    export let saving = false;
    export let setSettingValue;
    export let selectSettingsGroup;
    export let selectedEditor = "";
    export let selectedSettingsGroupId = "";
    export let restoreSettingDefault;
    export let validateRegexExpression;
    export let workspaceDocument = null;

    function regexValidation(setting) {
        return setting?.regexValidation || regexValidationResults?.[setting?.key] || null;
    }

    function canRestoreSetting(setting) {
        return setting?.key === "SuspiciousTags" || setting?.key === "SuspiciousProcessOutput";
    }

    function normalizedSearchText(text) {
        return (text || "").trim().toLowerCase();
    }

    function matchesSettingsSearch(setting, searchText) {
        const normalized = normalizedSearchText(searchText);

        if (!normalized) {
            return true;
        }

        return [
            setting?.key,
            setting?.type,
            setting?.value,
            setting?.description
        ]
            .filter(Boolean)
            .some((item) => item.toLowerCase().includes(normalized));
    }

    let settingsSearch = "";
    let expandedSettingsGroups = {};
    let settingsExpansionWorkspaceKey = "";

    function toggleSettingsGroup(groupId) {
        expandedSettingsGroups = {
            ...expandedSettingsGroups,
            [groupId]: !expandedSettingsGroups[groupId]
        };
    }

    function openSettingsGroup(groupId) {
        if (groupId) {
            openVisualSettings();
            selectSettingsGroup(groupId);
            expandedSettingsGroups = {
                ...expandedSettingsGroups,
                [groupId]: true
            };
        }
    }

    $: normalizedSettingsSearch = normalizedSearchText(settingsSearch);

    $: filteredSettingsGroups = (workspaceDocument?.settingsGroups || [])
        .map((settingsGroup) => ({
            ...settingsGroup,
            settings: (settingsGroup.settings || []).filter((setting) => matchesSettingsSearch(setting, settingsSearch))
        }))
        .filter((settingsGroup) => settingsGroup.settings.length > 0);

    $: settingsNavigationGroups = workspaceDocument?.settingsGroups || [];
    $: visibleSettingsGroups = normalizedSettingsSearch
        ? filteredSettingsGroups
        : filteredSettingsGroups.filter((settingsGroup) => settingsGroup.id === selectedSettingsGroupId);

    $: {
        const nextWorkspaceKey = workspaceDocument?.workspaceName || "";

        if (nextWorkspaceKey !== settingsExpansionWorkspaceKey) {
            settingsExpansionWorkspaceKey = nextWorkspaceKey;
            expandedSettingsGroups = workspaceDocument?.settingsGroups?.length > 0
                ? Object.fromEntries(
                    workspaceDocument.settingsGroups.map((settingsGroup) => [settingsGroup.id, true])
                )
                : {};
        }
    }
</script>

{#if renderSidebar && loading}
    <p>Loading workspace...</p>
{:else if renderSidebar && workspaceDocument}
    <section class="sidebar-section">
        <h3>Test Settings</h3>
        <div class="source-list">
            <button
                class:selected={selectedEditor === "test-settings"}
                class="source-item"
                on:click={openTestSettings}
            >
                <span>Edit test.settings file</span>
            </button>
        </div>
        <div class="settings-sidebar-tree">
            <button
                class:selected={selectedEditor === "settings-form"}
                class="source-item settings-tree-root"
                on:click={openVisualSettings}
            >
                <span>Edit Settings</span>
            </button>
            {#if settingsNavigationGroups.length > 0}
                <div class="settings-sidebar-nav">
                    {#each settingsNavigationGroups as settingsGroup}
                        <button
                            type="button"
                            class="source-item settings-nav-item"
                            class:selected={selectedEditor === "settings-form" && selectedSettingsGroupId === settingsGroup.id}
                            on:click={() => openSettingsGroup(settingsGroup.id)}
                        >
                            <span>{settingsGroup.title}</span>
                        </button>
                    {/each}
                </div>
            {/if}
        </div>
    </section>

{/if}

{#if renderContent && loading}
    <p>Loading workspace...</p>
{:else if renderContent && workspaceDocument}
    {#if selectedEditor === "settings-form" && currentEditorDocument}
        <section class="editor-section">
            <div class="section-header">
                <div>
                    <h2>{currentEditorDocument.title}</h2>
                    <p>Main common TESTAR settings with grouped controls. Use the test.settings editor for advanced control.</p>
                </div>
                <button class="secondary" disabled={saving} on:click={currentEditorDocument.save}>
                    {currentEditorDocument.saveLabel}
                </button>
            </div>

            <section class="settings-toolbar top-gap">
                <div class="settings-search">
                    <label class="field-label" for="settings-search">Search in all settings</label>
                    <input
                        id="settings-search"
                        type="search"
                        bind:value={settingsSearch}
                        placeholder="Search by key, value, type, or description"
                    />
                </div>
            </section>

            <section class="settings-groups top-gap">
                {#if visibleSettingsGroups.length > 0}
                    {#each visibleSettingsGroups as settingsGroup (settingsGroup.id)}
                        <article class="settings-group-card">
                            <button
                                type="button"
                                class="settings-group-toggle"
                                aria-expanded={normalizedSettingsSearch ? true : Boolean(expandedSettingsGroups[settingsGroup.id])}
                                on:click={() => toggleSettingsGroup(settingsGroup.id)}
                            >
                                <div class="settings-group-header">
                                    <div>
                                        <h3>{settingsGroup.title}</h3>
                                        <p>{settingsGroup.description}</p>
                                    </div>
                                    <div class="settings-group-summary">
                                        <span class="settings-group-count">{settingsGroup.settings.length}</span>
                                        <span class="settings-group-chevron">
                                            {#if normalizedSettingsSearch ? true : Boolean(expandedSettingsGroups[settingsGroup.id])}
                                                Hide
                                            {:else}
                                                Show
                                            {/if}
                                        </span>
                                    </div>
                                </div>
                            </button>

                            {#if normalizedSettingsSearch ? true : Boolean(expandedSettingsGroups[settingsGroup.id])}
                                <div class="settings-fields">
                                    {#each settingsGroup.settings as setting}
                                        <div
                                            class="settings-field"
                                            class:settings-field-wide={setting.type === "list"}
                                            class:settings-field-boolean={setting.type === "boolean"}
                                        >
                                            <div class="settings-field-header">
                                                <span class="settings-field-label">{setting.key}</span>
                                                {#if setting.regexCapable}
                                                    <div
                                                        class="settings-validation-message settings-validation-inline"
                                                        class:settings-validation-valid={regexValidation(setting)?.valid}
                                                        class:settings-validation-invalid={regexValidation(setting) && !regexValidation(setting).valid}
                                                        class:settings-validation-idle={!regexValidation(setting)}
                                                    >
                                                        {#if regexValidation(setting)}
                                                            <span>{regexValidation(setting).message}</span>
                                                        {:else}
                                                            <span>No validation executed yet.</span>
                                                        {/if}
                                                    </div>
                                                {/if}
                                                <div class="settings-field-actions">
                                                    {#if setting.regexCapable}
                                                        <button
                                                            type="button"
                                                            class="secondary settings-action-button"
                                                            on:click={() => validateRegexExpression(setting)}
                                                        >
                                                            Check Regex
                                                        </button>
                                                    {/if}
                                                    {#if canRestoreSetting(setting)}
                                                        <button
                                                            type="button"
                                                            class="secondary settings-action-button"
                                                            on:click={() => restoreSettingDefault(setting)}
                                                        >
                                                            Restore Default
                                                        </button>
                                                    {/if}
                                                </div>
                                            </div>

                                            {#if setting.type === "boolean"}
                                                <span class="settings-checkbox-row">
                                                    <input
                                                        type="checkbox"
                                                        checked={setting.value === "true"}
                                                        on:change={(event) => {
                                                            setSettingValue(setting, event.currentTarget.checked ? "true" : "false");
                                                        }}
                                                    />
                                                    <span>{setting.value === "true" ? "Enabled" : "Disabled"}</span>
                                                </span>
                                            {:else if setting.options?.length > 0}
                                                <select
                                                    value={setting.value}
                                                    on:change={(event) => {
                                                        setSettingValue(setting, event.currentTarget.value);
                                                    }}
                                                >
                                                    <option value=""></option>
                                                    {#each setting.options as option}
                                                        <option value={option}>{option}</option>
                                                    {/each}
                                                </select>
                                            {:else if setting.type === "list"}
                                                <textarea
                                                    class="settings-list-input"
                                                    value={setting.value}
                                                    on:input={(event) => {
                                                        setSettingValue(setting, event.currentTarget.value);
                                                    }}
                                                    placeholder="Keep TESTAR list syntax here"
                                                ></textarea>
                                            {:else if setting.type === "integer"}
                                                <input
                                                    type="number"
                                                    step="1"
                                                    value={setting.value}
                                                    on:input={(event) => {
                                                        setSettingValue(setting, event.currentTarget.value);
                                                    }}
                                                />
                                            {:else if setting.type === "number"}
                                                <input
                                                    type="number"
                                                    step="any"
                                                    value={setting.value}
                                                    on:input={(event) => {
                                                        setSettingValue(setting, event.currentTarget.value);
                                                    }}
                                                />
                                            {:else}
                                                <input
                                                    type="text"
                                                    value={setting.value}
                                                    on:input={(event) => {
                                                        setSettingValue(setting, event.currentTarget.value);
                                                    }}
                                                />
                                            {/if}

                                            {#if canRestoreSetting(setting)}
                                                <small class="settings-field-default">
                                                    Default: {setting.defaultValue === "" ? "(empty)" : setting.defaultValue}
                                                </small>
                                            {/if}

                                            <div class="settings-field-footer">
                                                {#if setting.description}
                                                    <small class="settings-field-help">{setting.description}</small>
                                                {/if}
                                            </div>
                                        </div>
                                    {/each}
                                </div>
                            {/if}
                        </article>
                    {/each}
                {:else}
                    <p class="progress-message">
                        {#if normalizedSettingsSearch}
                            No settings matched the current search.
                        {:else}
                            Select a settings category to inspect its fields.
                        {/if}
                    </p>
                {/if}
            </section>
        </section>
    {:else if selectedEditor === "test-settings" && currentEditorDocument}
        <section class="editor-section">
            <div class="section-header">
                <div>
                    <h2>{currentEditorDocument.title}</h2>
                </div>
                <button class="secondary" disabled={saving} on:click={currentEditorDocument.save}>
                    {currentEditorDocument.saveLabel}
                </button>
            </div>
            <textarea bind:value={workspaceDocument.testSettings.content}></textarea>
        </section>
    {/if}
{/if}
