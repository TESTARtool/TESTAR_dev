<script>
    import TestSettingsView from "./TestSettingsView.svelte";
    import { BASIC_ROLE_SETTINGS_GROUP_IDS, TEST_SETTINGS_EXCLUDED_SETTING_KEYS } from "../../app/webStudioRoles.js";

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

    const settingsDescription = "Basic setup for SUT connection, execution, filters, Agent CLI, WebDriver, and Android Appium.";
</script>

<main class="studio-layout">
    <section class="panel sidebar">
        <TestSettingsView
            allowedSettingsGroupIds={BASIC_ROLE_SETTINGS_GROUP_IDS}
            allowSettingsFileToggle={false}
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
            settingsDescription={settingsDescription}
            restoreSettingDefault={restoreSettingDefault}
            validateRegexExpression={validateRegexExpression}
            workspaceDocument={workspaceDocument}
        />
    </section>

    <section class="panel content-panel">
        <TestSettingsView
            allowedSettingsGroupIds={BASIC_ROLE_SETTINGS_GROUP_IDS}
            allowSettingsFileToggle={false}
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
            settingsDescription={settingsDescription}
            restoreSettingDefault={restoreSettingDefault}
            validateRegexExpression={validateRegexExpression}
            workspaceDocument={workspaceDocument}
        />
    </section>
</main>
