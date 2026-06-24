# Web Studio Bug Backlog

## Usage

Each bug entry should contain:

- identifier
- affected area
- reproduction steps
- expected behavior
- bug behavior
- status

## List of Bugs

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
