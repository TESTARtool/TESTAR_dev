# Web Studio Functional Specification

## Purpose

TESTAR Web Studio is the browser-based management and execution surface for:

- configuration of TESTAR workspaces and composition profiles
- Generate mode execution
- Spy mode execution
- CLI mode execution
- test results inspection
- state model analysis
- debug file inspection

The Web Studio frontend is a client of the Web Studio server. The server coordinates:

- workspace loading and saving
- TESTAR install distribution usage
- CLI distribution usage
- Spy session orchestration with the TESTAR install distribution
- Scriptless Generate session execution with the TESTAR install distribution
- Test result and debug file access for TESTAR and CLI distributions
- State model analysis for TESTAR and CLI distributions

## Core Concepts

### Workspace

A workspace is a named profile directory containing configuration and Java files.

For TESTAR execution, the workspace is resolved from:

- `testar/target/install/testar/bin/settings/<workspace>`

For CLI execution, the workspace is resolved from:

- `cli/target/install/testar-cli/settings/<workspace>`

### Editors

The configuration area exposes multiple editors:

- `Edit composition.properties file`
- `Edit Java Composition Flow`

- `Edit policies.properties file`
- `Edit Java Policies`

- `Edit test.settings file`
- `Edit Settings`

- source editors for the related Composition and Policies Java files

### Composition Views

The two composition views are:

- `Edit composition.properties file` allows users to edit the file content directly.
The `Edit composition.properties file` view is treated as one logical editing area for unsaved-change behavior.

- `Edit Java Composition Flow` renders the services and capabilities as connected flow nodes.
The `Edit Java Composition Flow` view is treated as one logical editing area for unsaved-change and uncompiled-change behavior.

### Policies Views

The two policies views are:

- `Edit policies.properties file` allows users to edit the file content directly.
The `Edit policies.properties file` view is treated as one logical editing area for unsaved-change behavior.

- `Edit Java Policies` renders the existing Java policies and lists them as active or available.
The `Edit Java Policies` view is treated as one logical editing area for unsaved-change and uncompiled-change behavior.

### Settings Views

The two settings views are:

- `Edit test.settings file` allows users to edit the file content directly.
The `Edit test.settings file` view is treated as one logical editing area for unsaved-change behavior.

- `Edit Settings` renders the content of the `test.settings` file into a visual grouped form of settings.
The `Edit Settings` view is treated as one logical editing area for unsaved-change behavior.

### State Observation Mode

`StateObservationMode` controls how a captured TESTAR state is projected for consumers. Currently, it is applied to CLI/agent state output.

The setting must not redefine the canonical captured state used by TESTAR internals.

Available modes:

- `FULL_STATE`: expose the captured state without observation filtering
- `LEAF_WIDGETS`: expose leaf widgets only
- `SEMANTIC_WIDGETS`: expose widgets selected by the semantic descriptor
- `INTERACTIVE_WIDGETS`: expose widgets with interaction capability, such as clickable, typeable, scrollable, or selectable widgets
- `INTERACTIVE_SEMANTIC_WIDGETS`: expose interaction-capable widgets plus semantic widgets
- `ACTIONABLE_WIDGETS`: expose widgets that are interaction-capable and currently action-eligible
- `ACTIONABLE_SEMANTIC_WIDGETS`: expose action-eligible widgets plus semantic widgets
- `TEXTUAL_CONTEXT`: expose semantic textual context

The distinction between interactive and actionable is intentional:

- interactive means the widget has an interaction capability
- actionable means the widget is interactive and also passes enabled, non-blocked, visible, widget-filter, and top-level policies

The default value is `FULL_STATE`.

The `Edit Settings` view must render `StateObservationMode` as a dropdown using the available enum values.

## Navigation Rules

### Workspace Selection

- The startup default workspace is the first workspace whose name starts with `webdriver_` when available.
- If no `webdriver_xxx` workspace is available, the first workspace is selected.
- Changing the selected workspace reloads the workspace document and resets editor state.

### Page Navigation

Main pages:

- Test Configuration
- Spy Mode
- Generate Mode
- CLI Mode
- View Test Results
- View State Model
- Inspect Debug Files

## Test Configuration

### Unsaved Composition File Behavior

Unsaved composition protection applies to the composition file editing area.

Protected views:

- `Edit composition.properties file`

The unsaved dialog must appear when leaving the `Edit composition.properties file`.

It must appear when:

- leaving composition file configuration for another top-level page while the edit composition file view is dirty
- changing workspace while the edit composition file view is dirty
- switching from the edit composition file view to another configuration editor
- switching from `Edit composition.properties file` to `Edit Java Composition Flow`

It must not appear when:

- switching to another view without changing anything
- navigating to the edit composition file view after the user already left the edit composition file view

Dialog actions:

- `Save`: persist current composition file changes, then continue the pending navigation
- `Discard`: restore persisted composition file state, then continue the pending navigation
- `Cancel`: keep the user in the current composition file editor and abort the pending navigation

### Unsaved and Uncompiled Composition Behavior

Unsaved and uncompiled composition protection applies to the composition flow editing area.

Protected views:

- `Edit Java Composition Flow`

The unsaved and uncompiled dialog must appear when leaving the `Edit Java Composition Flow`.

It must appear when:

- leaving composition flow configuration for another top-level page while the composition flow view is dirty
- changing workspace while the composition flow view is dirty
- switching from the composition flow view to another configuration editor
- switching from `Edit Java Composition Flow` to `Edit composition.properties file`
- closing a Java composition source editor while the selected Java source is dirty

It must not appear when:

- switching to another view without changing anything
- navigating to the composition flow view after the user already left the composition flow view

Dialog actions:

- `Save and Compile`: persist current composition flow changes, compile the current composition flow changes, then continue the pending navigation
- `Discard`: restore persisted composition flow state, then continue the pending navigation
- `Cancel`: keep the user in the current composition flow editor and abort the pending navigation

Java source editor actions:

- `Refresh Java`: reload or reopen the current Java composition source for the selected flow node
- `Save and Compile`: persist the current Java composition source file and run Java compilation
- plain Java source save without compilation must not be exposed as the primary source editor action

Invalid compilation:

- If the `Save and Compile` action causes a compilation issue, notify the user, keep the user in the current composition flow editor, and abort the pending navigation
- If compilation was triggered from the unsaved and uncompiled dialog, close that dialog so the user can inspect the Java source editor and compilation diagnostics

### Unsaved Policies File Behavior

Unsaved policies protection applies to the policies file editing area.

Protected views:

- `Edit policies.properties file`

The unsaved dialog must appear when leaving the `Edit policies.properties file`.

It must appear when:

- leaving policies file configuration for another top-level page while the edit policies file view is dirty
- changing workspace while the edit policies file view is dirty
- switching from the edit policies file view to another configuration editor
- switching from `Edit policies.properties file` to `Edit Java Policies`

It must not appear when:

- switching to another view without changing anything
- navigating to the edit policies file view after the user already left the edit policies file view

Dialog actions:

- `Save`: persist current policies file changes, then continue the pending navigation
- `Discard`: restore persisted policies file state, then continue the pending navigation
- `Cancel`: keep the user in the current policies file editor and abort the pending navigation

### Unsaved and Uncompiled Policies Behavior

Unsaved and uncompiled policies protection applies to the Java policies editing area.

Protected views:

- `Edit Java Policies`

The unsaved and uncompiled dialog must appear when leaving the `Edit Java Policies`.

It must appear when:

- leaving Java policies configuration for another top-level page while the Java policies view is dirty
- changing workspace while the Java policies view is dirty
- switching from the Java policies view to another configuration editor
- switching from `Edit Java Policies` to `Edit policies.properties file`
- closing a Java policy source editor while the selected Java source is dirty

It must not appear when:

- switching to another view without changing anything
- navigating to the Java policies view after the user already left the Java policies view

Dialog actions:

- `Save and Compile`: persist current Java policy changes, compile the current policy profile changes, then continue the pending navigation
- `Discard`: restore persisted Java policy state, then continue the pending navigation
- `Cancel`: keep the user in the current Java policies editor and abort the pending navigation

Java source editor actions:

- `Save and Compile`: persist the current Java policy source file and run Java compilation
- plain Java source save without compilation must not be exposed as the primary source editor action

Invalid compilation:

- If the `Save and Compile` action causes a compilation issue, notify the user, keep the user in the current Java policies editor, and abort the pending navigation
- If compilation was triggered from the unsaved and uncompiled dialog, close that dialog so the user can inspect the Java source editor and compilation diagnostics

### Unsaved Settings Behavior

Unsaved settings protection applies to the settings editing areas.

Protected views:

- `Edit test.settings file`
- `Edit Settings`

The unsaved dialog must appear when leaving one of the settings editing areas, or switching between settings views while there are unsaved changes.

It must appear when:

- leaving the settings configuration view for another top-level page while one of the settings views is dirty
- changing workspace while one of the settings views is dirty
- switching from a settings view to a non-settings configuration editor
- switching from `Edit test.settings` to `Edit Settings`
- switching from `Edit Settings` to `Edit test.settings`

It must not appear when:

- switching to another view without changing anything
- navigating to a settings view after the user already left the settings page
- changing settings without leaving the `Edit Settings` form view

Dialog actions:

- `Save`: persist current settings changes, then continue the pending navigation
- `Discard`: restore persisted settings state, then continue the pending navigation
- `Cancel`: keep the user in the current settings editor and abort the pending navigation

## Generate Mode

Generate Mode allows launching and stopping scriptless TESTAR execution.

Header information:

- current `SUTConnectorValue`
- runtime progress indicator
- runtime adapter message

Runtime behavior:

- start calls the server execution endpoint for scriptless generate
- stop calls the scriptless stop endpoint
- polling updates console output and sequence outcomes

## Spy Mode

Spy Mode supports:

- local Spy execution
- remote Spy execution
- state screenshot inspection
- widget hover and selection
- derived/default/direct actions on widgets

Header information:

- current `SUTConnectorValue`
- runtime progress indicator
- runtime label

## CLI Mode

CLI Mode supports:

- manual CLI session start
- agent CLI execution start
- CLI command execution
- CLI session stop

Workspace compatibility:

- a workspace is CLI-compatible when backend summary says `availableInCli = true`
- `cli_*` workspaces are also treated as CLI-compatible in frontend guard logic

## Test Results

The Test Results page supports inspection of Generate and CLI output folders.

### Output Result Selection

The page must support:

- source toggle between Generate and CLI outputs
- output result folder selection
- output result folder sorting
- output result folder filtering
- generated file listing for the selected output folder
- generated file filtering
- verdict outcome summarization
- HTML report rendering

Default behavior:

- output result folders are sorted latest first
- selecting an output folder shows a run-level verdict outcome summary by default
- selecting an output folder must not automatically select the first generated file
- selecting a generated test sequence file manually shows the HTML report rendering

### Results Preview

When an output result folder is selected and no generated file is selected, the right preview area must show a run-level verdict outcome summary.

The summary should include:

- verdict outcomes
- failure type distribution
- sequence status overview
- enough metadata to understand which run is selected

When a generated file is selected, the right preview area must show the corresponding HTML report.

The user must be able to return from the HTML report preview to the run-level verdict outcome summary.

### Empty State

The Test Results page must support an empty state.

The empty state is shown when:

- no output result folders exist for the selected source
- filters hide all available output result folders
- the last output result folder was deleted

Expected empty-state behavior:

- the left panel remains visible
- `Output Results` remains available
- `Generated Files` shows an empty message
- the right preview area shows a clear empty-state message
- no stale folder, file, summary, or HTML report remains selected

### Filtering

Output result folder filtering should support:

- all folders
- folders with failures
- folders with only OK sequences

Generated file filtering should support:

- all files
- failed files
- OK files

Filtering must not delete files. It only changes visibility in Web Studio.

Removing the filtering category restores the visibility of all folders and files.

### Deletion

The page may support deleting generated result artifacts.

Deletion rules:

- deleting a generated file deletes only the selected test sequence report file
- deleting an output result folder deletes the whole selected run folder
- deleting an output result folder is more destructive than deleting a generated file
- delete actions must require a modal confirmation
- if confirmation is not explicit and clear, Web Studio must not delete anything
- after deleting a selected generated file, Web Studio must clear the selected file and return to the run-level verdict outcome summary
- after deleting a selected output result folder, Web Studio must refresh the output folder list and clear stale selections
- if deleting an output result folder removes all existing run-level reports, WebStudio must show the default Test Results empty view

## State Model

State model generation and analysis are runtime-specific.

Scriptless Generate mode and CLI mode can generate state models when the selected workspace enables state model settings.

The runtime execution is responsible for automatic OrientDB preparation, including download/bootstrap when required by the configured state model storage settings.

`View State Model` opens the external analysis mode URL after server-side preparation.

The user must see a friendly modal dialog instead of raw server or browser console errors when analysis cannot be opened.

### Runtime Resolution

- Each runtime owns its state model storage relative to its runtime home.
- Scriptless TESTAR runtime home is `testar/target/install/testar/bin`.
- CLI runtime home is `cli/target/install/testar-cli`.
- `View State Model` must resolve the selected workspace to the runtime where it is available.
- If the workspace is available in the TESTAR runtime, analysis uses TESTAR runtime settings and datastore paths.
- If the workspace is available in the CLI runtime, analysis uses CLI runtime settings and datastore paths.
- If the same workspace name exists in both runtimes, WebStudio must pass the intended runtime to the state model endpoint.
- When `View State Model` is clicked from CLI mode, WebStudio must request CLI runtime analysis when the selected workspace is CLI-compatible.

### Error Handling

- If no workspace is selected, WebStudio must show an `Unable To Open State Model` dialog.
- If the selected workspace is unavailable in both installed runtimes, WebStudio must show an `Unable To Open State Model` dialog.
- If the selected workspace is available but no generated model exists yet, WebStudio must show a dialog asking the user to run Generate or CLI execution with state model enabled first.
- If analysis startup fails for another reason, WebStudio must show a user-facing `Unable To Open State Model` dialog and log details server-side.

## Debug Files

The debug files page supports:

- listing available debug files in the distribution root
- loading file contents into an inspection panel
