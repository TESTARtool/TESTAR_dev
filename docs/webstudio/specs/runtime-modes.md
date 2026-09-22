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

Each Generate sequence outcome preserves every generated verdict report for that sequence. The sequence status remains the aggregate status used by progress and sequence indicators, while the detailed outcome list exposes one entry for each verdict report.

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

Generate Mode renders one detailed outcome row for every verdict report produced by a sequence, including multiple verdicts such as `V001`, `V002`, and `V003`.

## Acceptance Scenarios

### WS-SCENARIO-RUNTIME-GENERATE-001 - Generate Shows All Sequence Verdicts

Verification: `ScriptlessSequenceVerdictTest.java`, `runtimeModel.test.js`

Given Generate Mode has produced multiple verdict reports for sequence 1
And Generate Mode has produced multiple verdict reports for sequence 2 in the same run
And the sequence 1 reports are named `sequence_1_V001_WARNING_ACCESSIBILITY_FAULT.html`, `sequence_1_V002_WARNING_ACCESSIBILITY_FAULT.html`, `sequence_1_V003_WARNING_ACCESSIBILITY_FAULT.html`, `sequence_1_V004_WARNING_WEB_INVARIANT_FAULT.html`, and `sequence_1_V005_SUSPICIOUS_LOG.html`
And the sequence 2 reports are named `sequence_2_V001_SUSPICIOUS_TAG.html`, `sequence_2_V002_SUSPICIOUS_TAG.html`, `sequence_2_V003_SUSPICIOUS_TAG.html`, and `sequence_2_V004_WARNING_WEB_INVARIANT_FAULT.html`
And every listed report has a failed verdict
When the Generate status is displayed
Then the sequence graph shows exactly two sequence blocks numbered 1 and 2
And both sequence blocks show the aggregate status `FAILED`
And the Test Results Outcomes panel shows exactly nine detailed verdict entries
And the five sequence 1 entries appear together before the four sequence 2 entries
And every entry preserves its complete report label and individual status
And the detailed list does not show standalone `sequence_1` or `sequence_2` entries
And no verdict from sequence 1 is displayed under sequence 2
