# Test Settings

This specification defines the selected workspace `test.settings` editor and its visual/raw representations.

## Functional Requirements

### WS-FUNC-TEST-SETTINGS-001 - Workspace Test Settings Editor

Traceability: [`WS-FUNC-TEST-SETTINGS-001`](../SPEC_TRACE.md#ws-func-test-settings-001---workspace-test-settings-editor)

The settings editor is exposed as one `Edit Settings` entry for the selected workspace.

The view has two representations of the same `test.settings` data:

- visual settings form
- raw `test.settings` text editor

The visual form is the default representation. A toggle in the view header switches between representations. Both representations share one `Save Settings` action and are treated as one logical editing area for unsaved-change behavior.

The selected workspace settings include `CliStateProjectionMode`, rendered as a dropdown using the available enum values.

### Settings Persistence

When the user saves either representation, WebStudio persists the selected workspace `test.settings` content. A successful save updates the persisted baseline used by both representations and disables `Save Settings` until a new change is made.

The unsaved settings guard appears when the user leaves `Edit Settings` or toggles representations while settings are dirty. It does not appear when changing settings groups inside the visual form.

Guard actions are:

- `Save`: persist settings, then continue the pending action
- `Discard`: restore the persisted settings state, then continue the pending action
- `Cancel`: remain in the current editor and abort the pending action

### Save Action Enablement

`Save Settings` is enabled only when the selected workspace settings differ from the persisted settings state. The save button and unsaved-change guard use the same dirty-state calculation.

## UX Requirements

### WS-UX-TEST-SETTINGS-001 - Test Settings Editor View

Traceability: [`WS-UX-TEST-SETTINGS-001`](../SPEC_TRACE.md#ws-ux-test-settings-001---test-settings-editor-view)

The left sidebar shows one `Edit Settings` entry. The visual form is shown by default, and a compact toggle near the heading switches to the raw editor without adding another sidebar entry or resizing the layout.

- settings groups remain visible in the left sidebar
- the selected group is visually clear
- `Search in all settings` is global to the settings view
- setting-specific feedback appears near the corresponding setting
- restore actions appear only for settings that support restore behavior
- enum dropdowns contain concrete values without a leading blank option
- both representations share `Save Settings`
- `Save Settings` is disabled when settings match the persisted state
- `Save Settings` is enabled when either representation changes settings
- toggling representations while dirty uses the save/discard/cancel guard pattern

## Acceptance Scenarios

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
