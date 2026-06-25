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
- whether the current profile is compatible with the selected execution mode, such as Generate/Spy vs CLI
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

The Test Configuration page is the main authoring surface.

The left sidebar is the primary navigation for:

- composition profile files
- Java composition flow
- policy files
- Java policies
- test settings
- settings groups

The default Test Configuration landing view is `Edit Java Composition Flow`.

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

The settings area has two representations of the same data:

- raw `test.settings` text
- structured `Edit Settings` form

Expected behavior:

- settings group entries are always visible in the left sidebar
- selected group is visually clear
- settings groups are visible by default
- search is global and labelled as `Search in all settings`
- setting-specific feedback appears near the corresponding setting
- restore actions only appear for settings that support restore behavior
- enum settings use dropdowns with concrete values only and must not include a leading blank option

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
- workspace compatibility updates immediately when workspace selection changes
- command buttons do not overflow horizontally

## Test Results UX Contract

The results page should provide fast visual understanding of output health.

Expected behavior:

- the left panel contains `Output Results`
- `Output Results` allows switching between Generate and CLI
- `Output Results` includes sorting and filtering controls without hiding the selected source context
- the left panel also contains `Generated Files` for the selected output result
- selecting an output result folder shows a run-level verdict outcome summary in the right preview area
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

- no output result folders shows an empty message in the right preview area
- filters hiding all output result folders show an empty filtered-results message
- no generated files for a selected output folder shows an empty message in the `Generated Files` panel
- deleting the last output result folder shows the default Test Results empty view

## State Model UX Contract

Expected behavior:

- state model errors are shown as friendly UI messages, not raw server errors

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
