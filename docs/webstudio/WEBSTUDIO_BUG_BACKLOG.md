# Web Studio Bug Backlog

## Usage

Each bug entry should contain:

- identifier
- affected area
- reproduction steps
- expected behavior
- bug behavior
- status

## Archived bugs

Bugs fixed during previous designs that no longer map to current UI concepts.

### WS-001 CLI Buttons Not Recomputed After Workspace Change

- Area: CLI Mode
- Status: fixed

Reproduction:

1. Open CLI mode with a non-CLI workspace selected.
2. Switch workspace to `cli_generic`.
3. Enter a target.

Expected:

- CLI start buttons enable immediately if the selected workspace is CLI-compatible

Bug:

- CLI start buttons remained disabled until view remount or page switching

Acceptance:

- CLI availability recomputes immediately when workspace changes

### WS-008 View State Model Did Nothing For Unsupported Workspace

- Area: Navigation / State Model Analysis
- Status: archived after shared runtime refactor

Reproduction:

1. Select a workspace that is not available in either the TESTAR runtime or the CLI runtime.
2. Click `View State Model`.

Expected:

- WebStudio shows a user-facing dialog explaining why state model analysis cannot be opened for the selected workspace

Bug:

- the button action returned silently when the selected workspace was not available in the TESTAR runtime workspace list
- the check was too strict because CLI workspaces can also generate state models

Reason archived:

- WebStudio now uses the selected shared workspace/runtime model
- the separate TESTAR-vs-CLI runtime availability concept no longer maps to the current State Model flow
- current expected behavior is specified in `WEBSTUDIO_FUNCTIONAL_SPEC.md`
- current dialog behavior remains covered by `stateModelNavigation.test.js`

## List of Bugs

Bugs fixed which are still relevant to current designs or behaviors.

### WS-002 Runtime Pages Missing SUT Target Summary

- Area: Generate Mode / Spy Mode
- Status: fixed

Reproduction:

1. Select a workspace with `SUTConnectorValue`.
2. Open Generate or Spy page.

Expected:

- runtime header shows the current `SUTConnectorValue`

Bug:

- summary was absent or displayed `No SUT configured.`

Acceptance:

- runtime header displays normalized `SUTConnectorValue`
- quoted values are rendered without wrapping quotes

### WS-003 Java Policy Editor Close Kept Modal Open

- Area: Test Configuration / Java Policies
- Status: fixed

Reproduction:

1. Open `Edit Java Policies`.
2. Select or create a Java policy source so the policy source editor modal opens.
3. Click `Close`.

Expected:

- the policy source editor modal closes
- the user returns to the Java policies overview

Bug:

- the modal remained open because the selected policy source was not cleared

Acceptance:

- closing the policy source editor clears the selected policy source
- the modal condition becomes false after clicking `Close`
- regression test `policyEditorState.test.js` passes

### WS-004 Failed Save And Compile Guard Hid Java Diagnostics

- Area: Test Configuration / Java Composition and Java Policies
- Status: fixed

Reproduction:

1. Open `Edit Java Composition Flow`.
2. Open a Java composition source, such as `WebdriverParabankTestSequenceLoginCapability.java`.
3. Edit the source so Java compilation fails.
4. Switch to another configuration view.
5. In the unsaved and uncompiled dialog, click `Save and Compile`.

Expected:

- compilation fails
- pending navigation is aborted
- the unsaved and uncompiled dialog closes
- the Java source editor remains visible with compilation diagnostics

Bug:

- the unsaved and uncompiled dialog remained open, preventing the user from inspecting the Java source editor and diagnostics

Acceptance:

- failed compile from the guard closes the guard dialog
- failed compile does not execute the pending navigation
- Java source editor stays visible with compilation diagnostics

### WS-005 CLI Target Session Panel Shows Accidental Tiny Scrollbar

- Area: CLI Mode
- Status: fixed

Reproduction:

1. Open CLI Mode in the default desktop layout.
2. Inspect the `Target CLI Session` panel.

Expected:

- static helper text and controls fit without a vertical scrollbar

Bug:

- the panel showed a tiny vertical scrollbar caused by non-essential helper text occupying too much vertical space

Acceptance:

- the static target session panel does not show an accidental scrollbar in the default desktop layout
- helper text remains concise

### WS-006 Debug Log List Reserved Space Looked Like Uncontrolled Growth

- Area: Inspect Debug Files
- Status: fixed

Reproduction:

1. Open `Inspect Debug Files`.
2. Inspect the `Available Log Files` panel.

Expected:

- the list area communicates that it has fixed capacity and scrolls when more files exist

Bug:

- the panel had empty space at the bottom that looked like it would dynamically grow as logs appeared

Acceptance:

- log file rows have fixed height
- the list area has fixed height and scrolls internally
- the panel shows file count and explanatory helper text

### WS-007 Settings Dropdowns Rendered Empty Options

- Area: Test Configuration / Edit Settings
- Status: fixed

Reproduction:

1. Open `Edit Settings`.
2. Inspect dropdown settings backed by configured option lists, such as `SUTConnector` or `DataStoreType`.

Expected:

- configured dropdown settings show only valid selectable values
- enum dropdown settings show only enum values

Bug:

- string-backed dropdown settings rendered an empty option before the configured values

Affected settings:

- `SUTConnector`
- `LlmReasoning`
- `DataStoreType`
- `DataStoreMode`
- `ActionSelectionAlgorithm`

Acceptance:

- configured dropdown settings do not render a blank option
- enum dropdown settings do not render a blank option
- optional non-dropdown string settings can still render a blank option
- regression test `settingsSelectOptions.test.js` passes

### WS-009 Generate And Spy Buttons Disabled By Stale Runtime-Origin Gate

- Area: Generate Mode / Spy Mode
- Status: fixed

Reproduction:

1. Select a workspace after the shared runtime/workspace refactor.
2. Open Generate Mode or Spy Mode.
3. Observe the run buttons.

Expected:

- Generate and Spy start buttons enable when a workspace is selected and no execution is already running
- workspace origin metadata must not disable scriptless runtime controls

Bug:

- Generate and Spy still required `availableInTestar`
- workspaces not marked with that old TESTAR-only flag left the buttons disabled

Acceptance:

- Generate and Spy buttons use selected shared workspace availability
- no selected workspace still disables the buttons
- saving or active execution still disables the buttons
- regression test `runtimeModeControls.test.js` passes

### WS-010 LLM Complete Results Shown As Issues

- Area: Test Results / CLI Reports
- Status: fixed

Reproduction:

1. Run an Agent CLI execution that finishes with `LLM_COMPLETE`.
2. Open `View Test Results`.
3. Inspect `Output Results`, `Test Verdict Files`, and success/issue filters.

Expected:

- `LLM_COMPLETE` result files are successful outcomes
- output folders containing only `LLM_COMPLETE` files are successful
- `Successful only` includes `LLM_COMPLETE`
- `Issues only` excludes `LLM_COMPLETE`

Bug:

- shared result listing still treated only `OK` as successful
- `LLM_COMPLETE` files were marked as failed, causing red rows and incorrect filters

Acceptance:

- shared result status classification treats `OK` and `LLM_COMPLETE` as successful
- `LLM_INVALID` and non-success verdicts remain issue outcomes
- regression test `ResultVerdictStatusTest` passes

### WS-011 Test Goals Folder Selection Kept YAML Editor Open

- Area: Test Goals
- Status: fixed

Reproduction:

1. Open `Test Goals`.
2. Select a YAML goal file.
3. Select a folder in the goal tree.

Expected:

- the right panel switches from YAML editing to the selected-folder creation panel
- if the YAML editor has unsaved changes, the unsaved-change guard appears before switching

Bug:

- folder selection was local to the view while YAML file selection was stored in the parent view state
- selecting a folder did not clear the selected YAML file, so the right panel kept showing the YAML editor

Acceptance:

- folder selection is stored in the parent Test Goals state
- selecting a folder clears the selected YAML file after the unsaved-change guard is resolved
- regression test `testGoalFolderSelectionState` passes
