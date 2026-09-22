# WebStudio Acceptance Scenarios

This document captures user-facing workflows that are important enough to protect with automated tests.

Use these scenarios to complement the functional and UX specifications. Keep them short, concrete, and focused on behavior that can regress during refactors.

## Test Settings

Related requirements: [`WS-FUNC-TEST-SETTINGS-001`](./WEBSTUDIO_SPEC_TRACE.md#ws-func-test-settings-001---workspace-test-settings-editor), [`WS-UX-TEST-SETTINGS-001`](./WEBSTUDIO_SPEC_TRACE.md#ws-ux-test-settings-001---test-settings-editor-view), [`WS-FUNC-CONFIG-GUARD-001`](./WEBSTUDIO_SPEC_TRACE.md#ws-func-config-guard-001---configuration-unsaved-change-protection)

### WS-SCENARIO-SETTINGS-001 - Visual Settings Save

Verification: `settingsApi.test.js`, `settingsEditorModel.test.js`, `editorDirtyState.test.js`, `editorSelectionModel.test.js`

Given the user is editing the visual settings form
When the user changes a setting value
Then the `Save Settings` button is enabled
When the user clicks `Save Settings`
Then the generated `test.settings` content is persisted
And the `Save Settings` button is disabled
And the visual settings form keeps the saved value

### WS-SCENARIO-SETTINGS-002 - Raw Settings Save

Verification: `settingsApi.test.js`, `editorDirtyState.test.js`, `editorSelectionModel.test.js`

Given the user is editing raw `test.settings`
When the user changes a setting value
Then the `Save Settings` button is enabled
When the user clicks `Save Settings`
Then the raw `test.settings` content is persisted
And the `Save Settings` button is disabled

### WS-SCENARIO-SETTINGS-003 - Raw To Visual Settings Guard

Verification: `configurationGuard.test.js`, `workspaceSettingsModel.test.js`, `editorDirtyState.test.js`

Given the user is editing raw `test.settings`
And the user changes a setting value
When the user clicks `Show settings form`
Then the unsaved changes guard is shown

When the user clicks `Cancel`
Then the user remains in the raw `test.settings` editor

When the user clicks `Save`
Then the raw `test.settings` content is persisted
And the visual settings form is shown
And the visual settings form shows the saved value
And the `Save Settings` button is disabled

When the user clicks `Discard`
Then the raw `test.settings` content is not persisted
And the visual settings form is shown
And the visual settings form shows the previous saved value
And the `Save Settings` button is disabled

### WS-SCENARIO-SETTINGS-004 - Visual To Raw Settings Guard

Verification: `configurationGuard.test.js`, `settingsEditorModel.test.js`, `editorDirtyState.test.js`

Given the user is editing the visual settings form
And the user changes a setting value
When the user clicks `Show settings file`
Then the unsaved changes guard is shown

When the user clicks `Cancel`
Then the user remains in the visual settings form

When the user clicks `Save`
Then the generated `test.settings` content is persisted
And the raw `test.settings` editor is shown
And the raw `test.settings` content contains the saved value
And the `Save Settings` button is disabled

When the user clicks `Discard`
Then the generated `test.settings` content is not persisted
And the raw `test.settings` editor is shown
And the raw `test.settings` content contains the previous saved value
And the `Save Settings` button is disabled

### WS-SCENARIO-SETTINGS-005 - Visual Settings Group Switch

Verification: `configurationGuard.test.js`, `editorSelectionModel.test.js`

Given the user is editing the visual settings form
And the user changes a setting value
Then the `Save Settings` button is enabled
When the user opens another settings group
Then the user can continue editing the visual settings form
And the `Save Settings` button is still enabled

### WS-SCENARIO-SETTINGS-006 - Settings To Non-Settings View Guard

Verification: `configurationGuard.test.js`, `editorDirtyState.test.js`

Given the user is editing either the visual settings form or raw `test.settings`
When the user changes a setting value
Then the `Save Settings` button is enabled
When the user navigates to another non-settings view
Then the unsaved changes guard is shown

## Top Navigation

Related requirements: [`WS-FUNC-TOP-NAV-ROLES-001`](./WEBSTUDIO_SPEC_TRACE.md#ws-func-top-nav-roles-001---role-based-navigation), [`WS-UX-TOP-NAV-ROLES-001`](./WEBSTUDIO_SPEC_TRACE.md#ws-ux-top-nav-roles-001---role-based-top-navigation)

### WS-SCENARIO-TOP-NAV-001 - Guarded Role Selector Does Not Show Uncommitted Role

Verification: `webStudioRoles.test.js`, `committedSelectModel.test.js`, `configurationGuard.test.js`

Given the user is in the `Advanced` role
And the user has unsaved settings changes
When the user selects the `Basic` role
Then the unsaved changes guard is shown
And the role selector still shows `Advanced` while the role change is pending

When the user clicks `Cancel`
Then the application remains in the `Advanced` role
And the role selector shows `Advanced`

When the user accepts the guarded role change
Then the application changes to the `Basic` role
And the role selector shows `Basic`

### WS-SCENARIO-TOP-NAV-002 - Guarded Workspace Selector Does Not Show Uncommitted Workspace

Verification: `committedSelectModel.test.js`, `configurationGuard.test.js`

Given the user selected workspace is `webdriver_generic`
And the user has unsaved settings changes
When the user selects workspace `android_generic`
Then the unsaved changes guard is shown
And the workspace selector still shows `webdriver_generic` while the workspace change is pending

When the user clicks `Cancel`
Then the application remains in workspace `webdriver_generic`
And the workspace selector shows `webdriver_generic`

When the user accepts the guarded workspace change
Then the application changes to workspace `android_generic`
And the workspace selector shows `android_generic`
