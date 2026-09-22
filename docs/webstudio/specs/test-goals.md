# Test Goals

This specification defines workspace-scoped YAML Test Goals, their authoring view, and their prompt-driven CLI integration.

## Functional Requirements

### WS-FUNC-TEST-GOALS-001 - Workspace-Scoped Test Goals

Traceability: [`WS-FUNC-TEST-GOALS-001`](../SPEC_TRACE.md#ws-func-test-goals-001---workspace-scoped-test-goals)

Test Goals are reusable goal definitions consumed by AI-guided execution modes such as CLI mode.

Test Goals are workspace assets. Each workspace may contain a `test_goals` folder next to its settings, composition, policies, and Java sources.

Repository defaults live under `testar/resources/settings/{workspace}/test_goals`. The distributed editable copy lives under `testar/target/install/testar/bin/settings/{workspace}/test_goals`. WebStudio works with the distributed folder for the selected workspace and must not mix goals from different workspaces.

### Supported Goal Files

Managed executable goals use YAML files with `.yaml` or `.yml` extensions.

### YAML Contract

Each executable goal file uses this shape:

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

Required fields are `version`, `goals`, `goals[].id`, `goals[].title`, `goals[].objective`, and `goals[].expected_outcomes`.

Recommended fields are `goals[].category`, `goals[].inputs.platform`, `goals[].inputs.sut`, and `goals[].validation_notes`. Goal identifiers use kebab-case.

### Authoring Behavior

The dedicated `Test Goals` view supports browsing, folder creation, YAML file creation, YAML editing, saving, discarding, deletion with confirmation, and validation of the required YAML shape before saving or execution.

All file operations remain inside the selected workspace `test_goals` folder. Path traversal, absolute paths, and unsafe symlink escapes are rejected.

When a selected workspace has no Test Goals, the view shows a stable empty state and allows creating folders or YAML files.

### CLI Mode Integration

The Test Goals view manages files and folders; it does not select goals for execution. CLI Mode remains prompt-driven.

The user prompt can request one or multiple named YAML files, one or multiple named directories, or all YAML goals under a named parent directory.

The generated Agent CLI prompt includes the selected workspace `test_goals` root path and instructs the agent to:

- resolve referenced paths relative to that root
- read requested YAML files before execution
- execute every goal in each requested file
- execute all matching files when an instruction matches multiple goals
- discover YAML files recursively when directory execution is requested
- finalize each goal session with `LLM_COMPLETE` or `LLM_INVALID`
- execute `shutdownDaemon` after the final goal session

### Agent CLI Settings

Agent CLI settings use `AgentCLI...` names in the selected workspace `test.settings` file. The `Edit Settings` view exposes them in an `Agent CLI` group. CLI Mode may expose a focused editor for the same settings.

If Agent CLI settings are edited in CLI Mode and the user leaves CLI Mode, changes workspace, or starts Manual/Agent CLI execution, WebStudio shows the unsaved settings dialog:

- `Save` persists the settings and continues the pending action
- `Discard` restores the persisted settings and continues the pending action
- `Cancel` keeps the user in CLI Mode and aborts the pending action

### CLI Verdict Finalization

Manual CLI execution may stop with plain `stopSession`, which finalizes the session with `LLM_COMPLETE`.

Agent CLI execution stops with an explicit verdict:

- `stopSession LLM_COMPLETE <reason>`
- `stopSession LLM_INVALID <reason>`

`LLM_COMPLETE` means the agent completed the goal and verified the expected result. `LLM_INVALID` means the agent found an invalid result, could not verify the expected result, or could not complete the goal with reliable evidence.

If an Agent CLI execution ends without an explicit LLM verdict, WebStudio treats it as incomplete or invalid and surfaces that status. When CLI reporting is enabled, generated report artifacts include the final CLI verdict and reason.

## UX Requirements

### WS-UX-TEST-GOALS-001 - Test Goals Authoring View

Traceability: [`WS-UX-TEST-GOALS-001`](../SPEC_TRACE.md#ws-ux-test-goals-001---test-goals-authoring-view)

Test Goals use a dedicated authoring view. The left panel shows a folder/file tree rooted at the selected workspace `test_goals` folder, and the editor panel shows the selected YAML file.

- changing workspace refreshes the tree for that workspace
- folder and file lists have fixed size and scroll internally
- the view contains file and folder management controls only
- delete actions require modal confirmation
- unclear confirmation cancels deletion
- unsaved YAML edits use the save/discard/cancel guard pattern
- YAML validation feedback appears in a stable panel
- unsupported files may be shown as grey non-executable reference files
