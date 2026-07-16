<script>
    import TestSettingsView from "./TestSettingsView.svelte";
    import { TEST_SETTINGS_EXCLUDED_SETTING_KEYS, TEST_SETTINGS_GROUP_IDS } from "./webStudioRoles.js";

    export let currentEditorDocument = null;
    export let loading = false;
    export let openTestSettings;
    export let openVisualSettings;
    export let openVisualSettingsGroup;
    export let regexValidationResults = {};
    export let savedTestSettingsContent = "";
    export let saving = false;
    export let setSettingValue;
    export let selectedEditor = "";
    export let selectedSettingsGroupId = "";
    export let restoreSettingDefault;
    export let validateRegexExpression;
    export let workspaceDocument = null;
</script>

<main class="studio-layout">
    <section class="panel sidebar">
        <TestSettingsView
            allowedSettingsGroupIds={TEST_SETTINGS_GROUP_IDS}
            excludedSettingKeys={TEST_SETTINGS_EXCLUDED_SETTING_KEYS}
            currentEditorDocument={currentEditorDocument}
            loading={loading}
            openTestSettings={openTestSettings}
            openVisualSettings={openVisualSettings}
            openVisualSettingsGroup={openVisualSettingsGroup}
            regexValidationResults={regexValidationResults}
            renderContent={false}
            renderSidebar={true}
            savedTestSettingsContent={savedTestSettingsContent}
            saving={saving}
            setSettingValue={setSettingValue}
            selectedEditor={selectedEditor}
            selectedSettingsGroupId={selectedSettingsGroupId}
            restoreSettingDefault={restoreSettingDefault}
            validateRegexExpression={validateRegexExpression}
            workspaceDocument={workspaceDocument}
        />
    </section>

    <section class="panel content-panel">
        <TestSettingsView
            allowedSettingsGroupIds={TEST_SETTINGS_GROUP_IDS}
            excludedSettingKeys={TEST_SETTINGS_EXCLUDED_SETTING_KEYS}
            currentEditorDocument={currentEditorDocument}
            loading={loading}
            openTestSettings={openTestSettings}
            openVisualSettings={openVisualSettings}
            openVisualSettingsGroup={openVisualSettingsGroup}
            regexValidationResults={regexValidationResults}
            renderContent={true}
            renderSidebar={false}
            savedTestSettingsContent={savedTestSettingsContent}
            saving={saving}
            setSettingValue={setSettingValue}
            selectedEditor={selectedEditor}
            selectedSettingsGroupId={selectedSettingsGroupId}
            restoreSettingDefault={restoreSettingDefault}
            validateRegexExpression={validateRegexExpression}
            workspaceDocument={workspaceDocument}
        />
    </section>
</main>
