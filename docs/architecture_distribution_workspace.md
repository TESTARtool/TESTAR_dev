# Distribution and Workspace Architecture

This document defines the target distribution and workspace model for TESTAR, TESTAR CLI, and WebStudio.

It extends the high-level architecture rules in `docs/ARCHITECTURE.md`.

## Target Design

TESTAR should move toward one installed distribution that contains separate entry points:

- `testar`
- `testar-cli`

This is a packaging unification, not an architecture merge.

The runtime implementations remain separate where needed:

- `testar` scriptless remains mode and protocol oriented
- `testar-cli` remains daemon/session oriented
- `Spy`, `Generate`, manual CLI, and agent CLI can keep mode-specific orchestration
- shared services, policies, state model storage, and reporting infrastructure are reused where their contracts match

## Distribution Layout

The intended installed layout is:

```text
target/install/testar/
  bin/
    testar.bat
    testar-cli.bat
  settings/
    webdriver_generic/
    windows_generic/
    android_generic/
  output/
  .runtime/
```

The exact launcher names can vary by platform, but the installed distribution should expose both TESTAR and CLI entry points from the same runtime root.

## Workspace Model

WebStudio should show one workspace list.

Examples:

- `webdriver_generic`
- `windows_generic`
- `android_generic`

The same workspace can be used by:

- scriptless `Generate`
- scriptless `Spy`
- local scriptless `Spy`
- manual CLI
- agent CLI
- state model analysis, when a model exists

The workspace is user-facing.

The execution mode is implementation-facing.

## CLI Startup Contracts

TESTAR CLI supports two startup contracts.

WebStudio should use the workspace-driven contract:

```text
startSession <workspace>
```

Standalone `testar-cli` should also support the explicit contract:

```text
startSession <platform> <target> [workspace]
```

The workspace-driven contract derives platform and target from the selected workspace settings.

The explicit contract is intended for standalone and multi-platform agent use cases where the agent needs to switch platforms or targets directly.

When the optional `[workspace]` is provided, CLI loads that workspace's settings, policies, and services, then overrides the session platform and target from the command arguments.

When the optional `[workspace]` is omitted, CLI uses its documented default workspace/configuration.

## CLI Platform and Target Mapping

For the workspace-driven contract, CLI mode should derive its platform and target from the same SUT settings used by scriptless TESTAR.

Mapping rules:

- `SUTConnector = WEB_DRIVER`
  - CLI platform: `webdriver`
  - CLI target: `SUTConnectorValue`
- `SUTConnector = ANDROID_APPIUM`
  - CLI platform: `android`
  - CLI target: the configured Android application target
- `SUTConnector = COMMAND_LINE`
  - CLI platform: `windows`
  - CLI target: `SUTConnectorValue`
- desktop attach-style connectors, such as window title or process name, are not valid for workspace-driven CLI mode

The CLI user interface should not require a separate workspace profile only to know platform and target.

Explicit standalone startup can still pass platform and target directly:

- `startSession webdriver https://example.org [workspace]`
- `startSession windows notepad.exe [workspace]`
- `startSession android ApiDemos-debug.apk [workspace]`

## Composition Compatibility

Scriptless composition resources may contain entries that do not apply to CLI.

Examples:

- `settingsCapabilityClass`
- `testSessionCapabilityClass`
- `testSequenceCapabilityClass`
- `stopCriteriaCapabilityClass`
- scriptless-only oracle composer classes

CLI mode should tolerate these entries.

The preferred behavior is:

- ignore unsupported scriptless-only entries
- emit a clear warning at CLI session start to inform users
- do not fail the session only because scriptless-only entries exist
- load and compile only the external Java classes that the current CLI path actually uses

Warnings should be visible in WebStudio or CLI logs, but they should not be repeated noisily on every command.

Runtime class loading is not full profile validation.

Full Java profile validation belongs to explicit tooling such as WebStudio `Compile Profile`.

This prevents CLI from failing only because unrelated scriptless-only Java files exist in a shared workspace.

Oracle composer entries are scriptless-only.

CLI agent runs should use the agent as the oracle for LLM-complete verdicts, such as `LLM_COMPLETE_VALID` and `LLM_COMPLETE_INVALID`.

## Shared Settings

The same `test.settings` file can contain settings used by different modes.

Mode-specific settings must be documented and ignored by modes that do not use them.

Examples:

- `Mode`
  - scriptless TESTAR setting
  - not a CLI session setting
- `Sequences` and `SequenceLength`
  - scriptless `Generate` settings
  - not required by manual CLI sessions
- `StateObservationMode`
  - currently used by CLI and agent state projection
- agent CLI settings
  - used by agent CLI mode
  - ignored by scriptless `Generate` and `Spy`

## Policies and Semantics

Policies are not inherently CLI-only or scriptless-only.

The same policy families can be reused by several consumers:

- clickable
- typeable
- scrollable
- selectable
- visible
- enabled
- blocked
- top-level
- widget filtering

The consumer determines the meaning:

- action derivation can use policies to decide available actions
- CLI state projection can use policies to shape state observations

Therefore, policies should remain part of the shared workspace model.

## Shared Runtime Paths

A unified distribution should resolve these paths from the same runtime root:

- `settings`
- `output`
- `.runtime`
- local state model datastore

This avoids split behavior where scriptless TESTAR and CLI write to different output folders or different OrientDB directories.

State model analysis should resolve the datastore associated with the selected workspace and shared runtime root.

## WebStudio Behavior

WebStudio should:

- show one workspace selector
- derive CLI platform and target from the selected workspace settings
- start CLI sessions with the workspace-driven contract, `startSession <workspace>`
- show warnings when mode-specific entries are ignored
- keep mode-specific controls in their own views
- read generated output results from the shared distribution output folder

WebStudio reuses equivalent workspaces across execution modes.
