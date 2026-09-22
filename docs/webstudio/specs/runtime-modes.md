# Runtime Modes

This capability covers runtime execution controls and monitoring for Generate, Spy, and CLI modes.

## Functional Requirements

### WS-FUNC-RUNTIME-EXECUTION-001 - Runtime Execution Modes

Traceability: [`WS-FUNC-RUNTIME-EXECUTION-001`](../SPEC_TRACE.md#ws-func-runtime-execution-001---runtime-execution-modes)

WebStudio supports Generate, Spy, and CLI execution through the shared workspace and runtime configuration.

### Generate Mode

Generate Mode launches and stops scriptless TESTAR execution.

The view exposes:

- current `SUTConnectorValue`
- runtime progress
- runtime adapter messages
- start and stop controls

Run controls are available when a shared workspace is selected and no Generate execution is already running. Starting calls the server scriptless-generate endpoint, stopping calls the scriptless stop endpoint, and polling updates console output and sequence outcomes.

### Spy Mode

Spy Mode supports:

- local Spy execution
- remote Spy execution
- state screenshot inspection
- widget hover and selection
- derived, default, and direct widget actions

Run controls are available when a shared workspace is selected and no Spy execution is already running. The view exposes the current `SUTConnectorValue`, runtime progress, and runtime label.

### CLI Mode

CLI Mode supports:

- manual CLI session start
- Agent CLI execution start
- CLI command execution
- CLI session stop
- editing Agent CLI settings stored in the selected workspace `test.settings`
- prompt-driven execution of managed Test Goals stored in the selected workspace

WebStudio uses the shared workspace list for CLI mode, sends the selected workspace to CLI startup, and reports server-side connector validation errors. Manual and Agent CLI execution use the selected workspace settings, including `CliStateProjectionMode` and `AgentCLI...` values.

### CLI State Projection Mode

`CliStateProjectionMode` controls how a captured TESTAR state is projected for CLI and Agent CLI consumers. It does not redefine the canonical captured state used by TESTAR internals.

Available modes are:

- `FULL_STATE`
- `LEAF_WIDGETS`
- `SEMANTIC_WIDGETS`
- `INTERACTIVE_WIDGETS`
- `INTERACTIVE_SEMANTIC_WIDGETS`
- `ACTIONABLE_WIDGETS`
- `ACTIONABLE_SEMANTIC_WIDGETS`
- `TEXTUAL_CONTEXT`

Interactive means that a widget has an interaction capability. Actionable additionally requires enabled, non-blocked, visible, widget-filter, and top-level policy checks. The default is `INTERACTIVE_SEMANTIC_WIDGETS`. The setting is edited in `Test Settings` using a dropdown.

## UX Requirements

### WS-UX-RUNTIME-EXECUTION-001 - Runtime Execution Pages

Traceability: [`WS-UX-RUNTIME-EXECUTION-001`](../SPEC_TRACE.md#ws-ux-runtime-execution-001---runtime-execution-pages)

Runtime pages prioritize monitoring and control rather than profile editing.

Generate and Spy headers share a stable structure:

- title area
- read-only `SUTConnectorValue`
- fixed-width progress indicator
- fixed-width runtime label
- action buttons

The header does not shift when runtime status changes or when the SUT value is long.

Spy behavior keeps the state screenshot within the available panel, scrolls widget properties internally, keeps action panels accessible, and prevents widget selection from moving the screenshot panel.

CLI behavior keeps manual controls, Agent CLI settings, and console output in stable panels. Console output scrolls internally, session start and stop do not shift panels, command buttons do not overflow horizontally, and Agent CLI settings edited in CLI mode use the same unsaved-change guard pattern as configuration settings.
