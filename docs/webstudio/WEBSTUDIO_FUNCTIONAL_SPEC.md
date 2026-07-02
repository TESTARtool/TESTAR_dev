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
- test result and debug file access from the shared distribution output/runtime
- state model analysis from the selected shared distribution workspace

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

### CLI State Projection Mode

`CliStateProjectionMode` controls how a captured TESTAR state is projected for CLI/agent consumers.

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

The default value is `INTERACTIVE_SEMANTIC_WIDGETS`.

The `Edit Settings` view must render `CliStateProjectionMode` as a dropdown using the available enum values.

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

- run controls are enabled when a shared workspace is selected and no Generate execution is already running
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

Run controls are enabled when a shared workspace is selected and no Spy execution is already running.

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
- focused editing of Agent CLI settings stored in the selected workspace `test.settings`
- prompt-driven execution of managed Test Goals stored in the selected workspace

Workspace selection:

- WebStudio uses the shared workspace list for CLI mode with other testar modes
- WebStudio sends the selected workspace to CLI startup
- CLI validates connector values server-side and reports startup errors to the user
- Manual and Agent CLI execution use the selected workspace settings, including `CliStateProjectionMode` and `AgentCLI...` values

## Test Goals

Test Goals are reusable goal definitions consumed by AI guided execution modes (e.g., CLI mode).

Test Goals are workspace assets. Each workspace may contain its own `test_goals` folder next to its settings, composition, policies, and Java sources.

Repository defaults live under:

- `testar/resources/settings/{workspace}/test_goals`

The distributed editable copy lives under:

- `testar/target/install/testar/bin/settings/{workspace}/test_goals`

WebStudio works with the distributed `test_goals` folder for the selected workspace.

Changing the selected workspace changes the Test Goals tree. WebStudio must not mix Test Goals from different workspaces in the same view.

### Supported Goal Files

Managed executable test goals use YAML files.

Allowed executable extensions:

- `.yaml`
- `.yml`

### YAML Contract

Each executable goal file must use this shape:

```yaml
version: 1
goals:
  - id: unique-goal-id
    title: Human readable title
    category: functional
    narrative: >
      Optional user story or context.
    objective: Clear objective the agent must complete.
    inputs:
      platform: webdriver
      sut: https://example.org/
    prerequisites:
      - Optional setup condition.
    expected_outcomes:
      - Observable expected outcome.
    validation_notes:
      - Optional validation guidance.
```

Required fields:

- `version`
- `goals`
- `goals[].id`
- `goals[].title`
- `goals[].objective`
- `goals[].expected_outcomes`

Recommended fields:

- `goals[].category`
- `goals[].inputs.platform`
- `goals[].inputs.sut`
- `goals[].validation_notes`

Goal identifiers should use kebab-case.

### Authoring Behavior

WebStudio should provide a dedicated `Test Goals` view.

The view should support:

- browsing folders and YAML goal files under the selected workspace `test_goals` folder
- creating folders
- creating YAML goal files
- editing YAML goal files
- deleting folders and files with confirmation
- saving edited YAML goal files
- discarding unsaved edits
- validating the required YAML shape before saving or before execution

All file operations must stay inside the selected workspace `test_goals` folder.

Path traversal, absolute paths, and unsafe symlink escapes must be rejected.

If the selected workspace has no Test Goals yet, the view should show a stable empty state and allow creating folders or YAML files under that workspace.

### CLI Mode Integration

Test Goals are not selected for execution in the authoring view.

The `Test Goals` view is only responsible for creating, editing, deleting, and organizing YAML goal files and directories.

CLI Mode remains prompt-driven.

The user prompt can instruct the agent to execute:

- one named YAML test goal file
- multiple named YAML test goal files
- the YAML test goals in one named directory
- the YAML test goals in multiple named directories
- all YAML test goals under a named parent directory

WebStudio must include the selected workspace `test_goals` root path in the generated Agent CLI prompt so the agent can resolve the user-requested goal files or directories.

The Agent CLI prompt contract must tell the agent:

- to resolve user-referenced goal paths relative to the selected workspace `test_goals` root
- to read the requested YAML goal files before execution
- to execute every goal defined in each requested YAML file
- to execute all matching YAML goal files when the user instruction is broad or matches multiple goals
- to use recursive directory execution when the user asks for all goals in a directory

Directory execution means recursive YAML discovery when the user asks for all goals in a directory.

Agent CLI execution must still finalize each goal session with `LLM_COMPLETE` or `LLM_INVALID` and execute `shutdownDaemon` after the final goal session.

### Agent CLI Settings

Agent CLI settings are stored in the selected workspace `test.settings` file with `AgentCLI...` names.

The `Edit Settings` view must expose these settings in an `Agent CLI` group.

The CLI mode view may also expose a focused `Agent CLI Settings` panel for the same settings.

If users edit Agent CLI settings in CLI mode and then leave CLI mode, change workspace, or start Manual/Agent CLI execution, WebStudio must show an unsaved settings dialog.

Dialog actions:

- `Save`: persist Agent CLI settings to the selected workspace `test.settings`, then continue the pending action
- `Discard`: restore the last persisted Agent CLI settings in the CLI panel, then continue the pending action
- `Cancel`: keep the user in CLI mode and abort the pending action

### CLI Verdict Finalization

Manual CLI execution may stop a session with plain `stopSession`.

Plain `stopSession` must finalize the session with an `LLM_COMPLETE` verdict.

Agent CLI execution must stop a session with an explicit LLM verdict:

- `stopSession LLM_COMPLETE <reason>`
- `stopSession LLM_INVALID <reason>`

`LLM_COMPLETE` means the agent completed the test goal and verified the expected result.

`LLM_INVALID` means the agent found an invalid result, could not verify the expected result, or could not complete the goal with reliable evidence.

WebStudio must include this finalization rule in the Agent CLI prompt contract.

If an Agent CLI execution ends without an explicit LLM verdict, WebStudio should treat the execution as incomplete or invalid and surface that status to the user.

When CLI reporting is enabled, generated report artifacts must include the final CLI verdict and reason.

## Test Results

The Test Results page supports inspection of output folders from the shared TESTAR distribution.

Output result folder names must include the execution mode token after the timestamp:

- `generate` for scriptless Generate mode
- `cli` for CLI mode

Example names:

- `2026-06-29_13h23m23s_generate_webdriver_parabank_1`
- `2026-06-29_13h23m23s_cli_webdriver_parabank_1`

### Output Result Selection

The page must support:

- output result folder selection
- output result folder sorting by date or name
- output result folder filtering by result type
- output result folder filtering by execution mode
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

Generate output summaries use `OK` and `FAILED` labels.

`OK` result files are successful outcomes.

`FAILED` result files are issue outcomes.

CLI output summaries use `COMPLETED` and `INVALID` labels.

`LLM_COMPLETE` result files are successful outcomes.

`LLM_INVALID` result files are issue outcomes.

When a generated file is selected, the right preview area must show the corresponding HTML report.

The user must be able to return from the HTML report preview to the run-level verdict outcome summary.

### Empty State

The Test Results page must support an empty state.

The empty state is shown when:

- no output result folders exist
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
- folders with issues
- folders with only successful sequences
- all execution modes
- Generate execution mode
- CLI execution mode

Generated file filtering should support:

- all files
- issue files
- successful files

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

Scriptless Generate mode and CLI mode can generate state models when the selected workspace enables state model settings.

The execution mode is responsible for automatic OrientDB preparation, including download/bootstrap when required by the configured state model storage settings.

`View State Model` opens the external analysis mode URL after server-side preparation.

The user must see a friendly modal dialog instead of raw server or browser console errors when analysis cannot be opened.

### Runtime Resolution

- State model analysis uses the selected workspace and the shared distribution runtime home.
- The shared runtime home is `testar/target/install/testar/bin`.
- State model datastore paths are resolved from the selected workspace settings against the shared runtime home.
- `View State Model` uses the selected shared workspace directly.
- Generate and CLI executions can both contribute state model data to the same configured datastore when they use the same selected workspace and datastore settings.

### Error Handling

- If no workspace is selected, WebStudio must show an `Unable To Open State Model` dialog.
- If the selected workspace is unavailable in the shared runtime, WebStudio must show an `Unable To Open State Model` dialog.
- If the selected workspace is available but no generated model exists yet, WebStudio must show a dialog asking the user to run Generate or CLI execution with state model enabled first.
- If analysis startup fails for another reason, WebStudio must show a user-facing `Unable To Open State Model` dialog and log details server-side.

## Debug Files

The debug files page supports:

- listing available debug files in the distribution root
- loading file contents into an inspection panel
