# Workspace Management

This specification defines workspace selection, creation, rename behavior, and the workspace selector interaction contract.

## Functional Requirements

### WS-FUNC-WORKSPACE-MANAGEMENT-001 - Workspace Creation and Rename

Traceability: [`WS-FUNC-WORKSPACE-MANAGEMENT-001`](../SPEC_TRACE.md#ws-func-workspace-management-001---workspace-creation-and-rename)

WebStudio exposes workspace-level management from the workspace selector area.

A workspace is a named profile directory containing settings, composition, policies, Java sources, Test Goals, and workspace Oracles.

The workspace management dialog supports:

- creating a new workspace by cloning an existing workspace
- renaming the currently selected workspace

The creation panel requires a new workspace name and an existing base workspace. The name must be a unique safe folder name containing only letters, numbers, underscores, or hyphens.

The cloned workspace copies:

- `test.settings`
- `composition.properties`
- `policies.properties`
- Java services, capabilities, and policies

The dialog provides optional copy controls for Test Goals and workspace Oracles. Both are checked by default.

When Test Goals are copied, the base workspace `test_goals` folder is copied. When the option is disabled, the new workspace receives an empty `test_goals` folder.

When Oracles are copied, the base workspace `oracles` folder is copied. When the option is disabled, the new workspace receives empty `oracles/dsl`, `oracles/java`, and `oracles/compiled` folders.

After successful creation, WebStudio refreshes the workspace list, selects the new workspace, and opens `Test Settings` with `Edit Settings` as the default editor.

If creation fails, the dialog remains open and shows the error.

The rename panel shows the current workspace name and requires a different unique safe folder name.

After successful rename, WebStudio refreshes the workspace list, selects the renamed workspace, and opens `Test Settings` with `Edit Settings` as the default editor.

Workspace rename also renames the matching output results workspace folder when it exists:

- `output/<old-workspace>` becomes `output/<new-workspace>`
- existing generated result folders remain inside the renamed output workspace folder
- rename succeeds when the old output folder does not exist
- rename fails when the destination output folder already exists

If rename fails, the dialog remains open and shows the error.

### Workspace Selection

The startup default is the first workspace whose name starts with `webdriver_` when available. If no such workspace exists, the first workspace is selected.

Changing the selected workspace reloads the workspace document and resets editor state. Workspace-scoped Test Goals, Oracles, results, and settings are refreshed for the new selection.

## UX Requirements

### WS-UX-WORKSPACE-MANAGEMENT-001 - Workspace Management Modal

Traceability: [`WS-UX-WORKSPACE-MANAGEMENT-001`](../SPEC_TRACE.md#ws-ux-workspace-management-001---workspace-management-modal)

The workspace selector area is the primary place for workspace selection and management.

The selector area provides a `Workspace` action that opens a workspace management modal. The modal exposes separate `Create Workspace` and `Rename Workspace` areas or tabs.

`Create Workspace`:

- requires a new workspace name and existing base workspace
- shows `Copy Test Goals`, checked by default
- shows `Copy Java and DSL Oracles`, checked by default
- explains that copied Oracles are workspace Java and DSL files
- keeps `Create` disabled until the values are valid

`Rename Workspace`:

- shows the current workspace name
- requires a valid, unique, different new name
- explains that existing output results move to the renamed workspace
- keeps `Rename` disabled until the value is valid

`Discard` closes the modal without applying changes. After creation or rename, the affected workspace is selected automatically.

The modal must not resize the page layout. Validation feedback appears inside the modal.

## Acceptance Scenarios

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
