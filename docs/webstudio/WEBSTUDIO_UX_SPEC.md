# Web Studio UX Specification

## Purpose

This document defines visual and interaction contracts for TESTAR Web Studio.

The functional specification defines what the application must do.
This UX specification defines how the interface remains clear, stable, and usable while those functions execute.

## Core UX Principles

### Stable Layout

Major panels, menus, headers, and action areas must stay in predictable positions.

Dynamic content must not resize the main page layout unexpectedly.

Preferred pattern:

1. Reserve fixed-size areas for dynamic content.
2. Render idle, loading, success, warning, and error states inside those areas.
3. Scroll internally when content exceeds the reserved area.

Dynamic elements can be added at runtime, but each item should keep a fixed or predictable size.

Examples:

- generated result file rows
- verdict type rows
- policy rows
- settings rows
- debug file rows
- CLI command buttons

### Visible Working Canvas

The primary Web Studio canvas should fit in a full browser window at approximately `1920x1080`.

The user should not need full-page scrolling to find primary panels or actions.

If content grows, the smallest useful container should scroll internally.

Static panels should not show accidental scrollbars in the default desktop layout.

If a panel reserves space for future dynamic items, the UI should make that reservation intentional through a count, hint, empty state, or fixed row capacity.

### Clear Context

The user should always understand:

- which workspace/profile is selected
- which Web Studio page is active, such as Configuration, Generate, Spy, CLI, Results, State Model, or Debug Files
- which SUT target is configured
- whether the selected workspace is loaded and which SUT target it configures
- whether an action is idle, waiting, running, failed, or complete

### Visual Feedback

Feedback should appear near the action or data it explains.

Examples:

- Java compile diagnostics appear in the Java editor area
- runtime progress appears in the runtime header
- verdict summaries appear near the selected output result
- setting validation appears near the corresponding setting

Feedback must not push unrelated panels or actions into new positions.

### Single Edit Responsibility

Each concept should have one primary editing location.

Examples:

- runtime pages may show `SUTConnectorValue`, but editing remains in configuration

## Role-Based UX

Web Studio should support role-based user experiences.

The selected role changes which workflows and configuration options are primary, but must not create a different workspace format.

The role selector belongs in the top navigation near the workspace selector.

Changing role should preserve the selected workspace.

Changing role should use the same unsaved-change modal as page navigation when the current view is dirty.

Workspace selection and workspace management belong to the shared top navigation and are available in every role.

## Top Navigation UX

The top navigation should group pages by workflow intent instead of exposing every page as a flat button.

Target top navigation:

- `Workspace <selected workspace>`
- `Role <selected role>`
- `Test Configuration`
- `Test Oracles`
- `Test Goals`
- `Spy Mode`
- `Run Modes`
- `View Results`

`Test Configuration` should contain core configuration views:

- `Test Settings`
- `Composition Flow`
- `Policies`

`Test Oracles` should be a direct top-level workflow because oracle configuration is central to test design.

`Test Goals` should be a direct top-level workflow because goal authoring is central to CLI and future agentic execution.

`Spy Mode` should remain a direct top-level action.

`Run Modes` should contain execution views:

- `Generate Mode`
- `CLI Mode`
- `MCP Mode` (disabled until implemented)

`View Results` should contain inspection views:

- `Test Reports`
- `State Model`
- `Debug Files`

Role changes should keep the top-level navigation groups in stable positions.

Role-specific visibility should be applied inside dropdown menus when possible.

Basic role can hide or simplify advanced dropdown entries, but should keep the same top-level group positions as Advanced role.

### Advanced Role UX

Advanced role keeps the current detailed Web Studio interface.

Advanced users can access low-level files and implementation-oriented editors directly.

Examples:

- raw `test.settings`
- `composition.properties`
- Java composition flow
- `policies.properties`
- Java policies
- debug files

### Basic Role UX

Basic role should be workflow-oriented.

Basic users should see labels and panels based on testing tasks rather than internal file names.

Basic navigation should use the same top-level groups as Advanced navigation.

Basic dropdown contents may hide or simplify advanced entries.

Shared navigation labels and icons should remain consistent between Basic and Advanced roles.

Top navigation should avoid shifting shared buttons when users switch roles.

Basic role uses the stable Test Oracles, Test Goals, Spy Mode, Generate Mode, CLI Mode, MCP Mode, and Test Results views.

The Basic implementation includes a Basic `Test Configuration` page backed by the visual settings form.

The Basic `Test Configuration` page should show only the agreed Basic settings groups.

Raw `test.settings` editing belongs to the Advanced `Test Settings` page.

Oracle-related settings should appear in `Test Oracles`.

The visual WebDriver settings form should hide browser-console oracle controls because those belong to `Test Oracles`.

Basic workflow groups:

- `Test Settings`
- `Test Oracles`
- `Test Goals`
- `Spy Mode`
- `Run Modes`
- `View Results`

Basic role should prioritize:

- configuring the SUT connector and target
- configuring begin sequences or test sequences
- configuring Test Oracles
- creating and editing Test Goals
- running Spy, Generate, CLI, and MCP modes
- inspecting Test Results

Basic role should avoid making raw files the first interaction.

Low-level editors can still exist as explicit advanced actions inside the relevant workflow.

### Intermediate Role UX

Intermediate role is a future decision.

Do not implement Intermediate-specific UI until its scope is defined.

## Test Oracles View

The `Test Oracles` view should provide a left-panel workflow in this order:

- `Active Oracles`
- `GUI Regex Oracles`
- `Windows Process Oracles`
- `WebDriver Console Oracles`
- `Log Regex Oracles`
- `Enable Extended Oracles`
- `|- Java Oracle Files`
- `|- DSL Oracle Files`

`Active Oracles` should be the default panel and should be read-only.

`Active Oracles` should show configured or inactive status without editing controls.

The extended oracle panels should use a unified oracle inventory for the selected workspace.

The inventory should be grouped by:

- workspace Java oracles
- DSL source files

The extended oracle workflow should use stable page sections:

- `Enable Extended Oracles` should show available Java oracles as checkbox rows.
- `Java Oracle Files` should show workspace Java files with a file browser, editor, and `Save and Compile` action.
- `DSL Oracle Files` should show workspace DSL files with a file browser and editor.

Workspace Java oracles should show whether they are active and editable.

DSL source files should be editable in the Test Oracles view.

DSL source files should use the Monaco editor.

The Monaco editor should provide syntax coloring, autocomplete, and lightweight local diagnostics from backend-generated Rascal DSL metadata.

Local diagnostics should help users catch common typing and structure mistakes while editing.

The feedback panel should still show the authoritative Rascal backend validation and generation result after `Save and Generate Java-DSL`.

Rascal backend diagnostics should appear as Monaco editor markers with hover messages when source ranges are available.

The DSL editor should expose a single `Save and Generate Java-DSL` action near the source editor controls.

Generated Java files should be visible through the workspace Java oracle group after DSL generation.

Enable and disable controls should operate on Java oracle class names and update `ExtendedOracles`.

The `Enable Extended Oracles` panel should provide `Enable All` and `Disable All` actions for the visible Java oracle class list.

The `Search in all settings` toolbar should appear in Test Settings and should stay hidden in Test Oracles panels.

Validation, compilation, and generation feedback should appear in fixed-size panels near the related editor or inventory group.

DSL feedback should show success, failure, generated Java path when available, and source diagnostics without resizing the page.

Java oracle compile feedback should use the same fixed-size style as other Java editor compile feedback.

Java and DSL oracle delete actions should open a confirmation dialog before deleting files.

The Test Oracles view should guide users toward Java and DSL-generated `Oracle` classes for individual oracle checks.

## Workspace Selector UX Contract

The workspace selector area is the primary place for workspace selection and workspace management.

Expected behavior:

- a `Workspace` action to the left of the workspace selector opens a workspace management modal
- the modal exposes separate areas or tabs for `Create Workspace` and `Rename Workspace`
- `Create Workspace` requires a new workspace name and an existing workspace as base
- `Create Workspace` shows `Copy Test Goals`, checked by default, and allows unchecking it
- `Create Workspace` shows `Copy Java and DSL Oracles`, checked by default, and allows unchecking it
- `Create Workspace` explains that copied oracles are workspace Java and DSL oracle files
- `Create` remains disabled until the workspace name and base workspace are valid
- `Rename Workspace` shows the current workspace name and requires a new valid workspace name
- `Rename Workspace` indicates that existing output results for the current workspace will move to the renamed workspace
- `Rename` remains disabled until the new name is valid, unique, and different from the current workspace name
- `Discard` closes the modal without applying changes
- after creation or rename, the affected workspace is selected automatically

The workspace management modal must not resize the page layout.

Validation feedback appears inside the modal.

## Viewport and Scroll Contract

Expected desktop behavior:

- main navigation remains visible
- current page header remains visible
- primary panels remain visible where applicable
- page-level scrolling is avoided in primary workflow screens
- dynamic lists, consoles, editors, properties, and diagnostics scroll internally

Allowed internal scroll regions:

- settings groups
- Java source editors
- compile diagnostics
- generated outputs and files
- verdict type lists
- logs and consoles
- widget properties
- result report preview

Avoid horizontal scrolling for normal labels and buttons.

If text overflows horizontally, prefer:

- shorter display labels
- smaller text for compact command buttons
- wrapping secondary text
- moving verbose values into detail panels
- using tooltips for full values

Example:

- CLI command buttons such as `getStateScreenshot` must not overflow the panel. If the button size is fixed, reduce label font size or use a shorter visible label with a full tooltip.

## Modal and Dialog Contract

Modals must overlay the current page without resizing it.

Use modals for:

- protecting unsaved or uncompiled work
- focused source editing
- major blocking information
- explicit user confirmation

Guard dialogs must clearly communicate:

- what kind of work is at risk
- what the primary action does
- what `Discard` does
- what `Cancel` does

Compile-related guard dialogs use `Save and Compile` as the primary action.

If compilation fails from a guard dialog:

- the dialog closes
- navigation is aborted
- the user remains in the relevant Java editor
- diagnostics are visible in the reserved compile feedback area

## Configuration UX Contract

The `Test Configuration` area is the main technical configuration surface.

The `Test Configuration` dropdown routes to focused pages:

- `Test Settings`
- `Composition Flow`
- `Policies`

`Test Settings`, `Composition Flow`, and `Policies` should be separate page-level views.

The default configuration landing view is `Test Settings`.

### Composition

The Java composition flow is a visual map of active services and capabilities.

Expected behavior:

- flow nodes keep stable positions
- clicking a node opens a focused editor modal
- Java source feedback appears inside the modal
- source editor action area remains stable
- `Refresh Java` refreshes or reopens the source linked to the selected node
- `Save and Compile` saves and validates the Java source

### Policies

The Java policies view compares active and available policies.

Expected behavior:

- active and available lists keep stable panel positions
- policy lists scroll internally when needed
- selecting a policy opens a focused source editor modal
- closing a policy source modal returns to the policy overview
- Java source feedback appears inside the modal

### Settings

The left sidebar shows a single `Edit Settings` entry for workspace settings.

The `Edit Settings` panel has two representations of the same data:

- visual settings form
- raw `test.settings` text editor

The visual settings form is shown by default.

A compact toggle near the `Edit Settings` heading switches between the visual form and raw text editor.

The toggle must not add a second left-sidebar entry and must not resize the surrounding layout.

Expected behavior:

- settings group entries are always visible in the left sidebar
- selected group is visually clear
- settings groups are visible by default
- search is global and labelled as `Search in all settings`
- setting-specific feedback appears near the corresponding setting
- restore actions only appear for settings that support restore behavior
- enum settings use dropdowns with concrete values only and must not include a leading blank option
- both settings representations share one `Save Settings` action
- `Save Settings` is disabled while settings match the persisted state
- `Save Settings` becomes enabled when either representation changes settings
- toggling between representations while settings are dirty uses the same save/discard/cancel guard pattern

### Save Buttons

Save actions should communicate whether there is work to persist.

Expected behavior:

- save buttons are disabled when the corresponding editor has no detected changes
- save buttons become enabled when the same dirty-state logic used by guard dialogs detects changes
- disabled save buttons remain visible to preserve layout stability
- guard dialogs still appear when users leave dirty editors without saving

## Runtime UX Contract

Runtime pages include Generate, Spy, and CLI.

Runtime pages prioritize monitoring and control, not profile editing.

### Generate and Spy

Generate and Spy headers should share a consistent structure:

- title area
- read-only `SUTConnectorValue`
- fixed-width progress indicator
- fixed-width runtime label
- action buttons

The header must not shift when runtime status changes or when the SUT value is long.

### Spy

Expected behavior:

- state screenshot fits the available panel
- widget properties scroll internally
- action panels remain accessible
- selecting widgets does not move the screenshot panel

### CLI

Expected behavior:

- manual CLI controls, agent settings, and console remain in stable panels
- console output scrolls internally
- session start/stop does not shift panels
- command buttons do not overflow horizontally
- Agent CLI settings edited in CLI mode use the same unsaved-change dialog pattern as configuration settings

### Test Goals

Expected behavior:

- Test Goals use a dedicated authoring view
- the left panel shows a folder/file tree rooted at the selected workspace `test_goals` folder
- changing the selected workspace refreshes the Test Goals tree for that workspace
- the editor panel shows the selected YAML goal file
- folder and file lists have fixed panel size and scroll internally
- the view contains only file and folder management controls
- delete actions require modal confirmation
- unclear confirmation always cancels deletion
- unsaved YAML edits use the same save/discard/cancel guard pattern as other editors
- YAML validation feedback appears in a stable panel and does not resize the layout dynamically
- unsupported files may be shown as grey non-executable reference files

Avoid:

- adding execution controls to the Test Goals view
- allowing file operations outside the selected workspace `test_goals` folder
- mixing Test Goals from multiple workspaces in one tree
- treating arbitrary free-text files as managed executable goals

## Test Results UX Contract

The results page should provide fast visual understanding of output health.

Expected behavior:

- the left panel contains `Output Results`
- `Output Results` reads from the selected workspace output folder
- changing the selected workspace refreshes `Output Results`
- results from other workspaces are not shown in the selected workspace list
- `Output Results` includes sorting by date or name
- `Output Results` includes filtering by the result type
- `Output Results` can be filtered by execution mode: All, Generate, or CLI
- the left panel also contains `Generated Files` for the selected output result
- selecting an output result folder shows a run-level verdict outcome summary in the right preview area
- Generate summaries use `OK` and `FAILED` wording
- CLI summaries use `COMPLETED` and `INVALID` wording
- selecting an output result folder does not automatically select the first generated file
- selecting a generated file replaces the summary with the HTML report preview
- the user can return from HTML report preview to the run-level verdict outcome summary
- output folders can be sorted, with latest first as the default
- output folders can be filtered without deleting or modifying files
- generated files can be filtered without deleting or modifying files
- green/red status color is consistent
- selected result remains visually distinct
- summary cards have fixed or predictable height
- failure type rows scroll internally when needed
- generated files panel has fixed height and scrolls internally when needed
- generated file rows keep fixed or predictable size
- report preview uses the remaining stable area
- destructive delete actions are not embedded as tiny accidental row buttons
- delete actions use a modal confirmation
- unclear confirmation always cancels deletion
- empty states keep the left panel visible and replace stale preview content with clear guidance

Avoid:

- panel height changing based on number of verdicts
- horizontal scrolling for generated file names where a shorter label, ellipsis, or detail tooltip can preserve context
- ambiguous color semantics
- stale selected folders, files, summaries, or HTML reports after filtering or deletion

Deletion confirmation behavior:

- deleting one generated file must name the selected file
- deleting one output result folder must name the selected folder
- deleting one output result folder must use stronger wording than deleting one generated file
- `Cancel` or closing the modal must not delete anything
- if the user does not clearly confirm the exact destructive action, Web Studio must not delete anything
- after deleting a generated file, the preview returns to the run-level verdict outcome summary
- after deleting an output result folder, stale selections are cleared before the list is refreshed

Empty state behavior:

- no output result folders for the selected workspace shows an empty message in the right preview area
- filters hiding all output result folders show an empty filtered-results message
- no generated files for a selected output folder shows an empty message in the `Generated Files` panel
- deleting the last output result folder shows the default Test Results empty view

## State Model UX Contract

Expected behavior:

- state model errors are shown as friendly UI messages, not raw server errors
- startup and datastore recovery are shown as a visible starting state
- running analysis exposes actions to open the analysis page and stop the analysis server

## Debug UX Contract

Expected behavior:

- debug file list scrolls internally
- selected debug file content scrolls internally
- empty state is explicit when no debug files exist

## Visual Semantics

Color must be meaningful and consistent.

Recommended semantics:

- green: OK, success, healthy state
- red: failed, invalid, blocking issue
- amber/orange: warning, suspicious, needs attention
- neutral: idle, unavailable, informational

Color must not be the only signal.

Labels, badges, or text should also communicate the state.

## Accessibility and Interaction Contract

Expected behavior:

- buttons use button elements
- dialogs use dialog semantics
- clickable non-button regions have appropriate roles and keyboard handling
- disabled actions explain unavailable state where practical
- important text is visible against its background
- critical information does not rely only on hover
