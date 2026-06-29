---
name: testar-cli
description: "Use when operating the distributed testar-cli launcher to execute goals through CLI commands, keep one session alive per goal, and drive SUTs step by step."
---

# TESTAR CLI

Use this skill for the distributed `testar-cli` runtime that is bundled with TESTAR.

Treat the CLI distribution as an operational environment, not as a source-code workspace.

## When to use

- The user wants a goal executed through the installed `testar-cli` launcher.
- The task requires step-by-step control of a live Windows, WebDriver, or Android session.
- The task requires using `getState`, `getStateScreenshot`, and `getDerivedActions` as execution evidence.

## Runtime layout

- Distribution root contains the public launcher.
- This skill is copied into `.agents/skills/testar-cli/` inside the CLI distribution.

## Public launcher paths

- Windows: `testar-cli.bat`
- Linux or macOS: `./testar-cli`

## Public commands

- `sessionStatus`
- `startSession <workspace>`
- `startSession windows <path> [workspace]`
- `startSession webdriver <url> [workspace]`
- `startSession android <target> [workspace]`
- `getState`
- `getStateScreenshot`
- `getDerivedActions`
- `executeAction click --target <semanticText>`
- `executeAction type --target <semanticText> --text <inputText>`
- `executeAction select --target <semanticText> --value <value>`
- `stopSession LLM_COMPLETE <reason>`
- `stopSession LLM_INVALID <reason>`
- `shutdownDaemon`

## Semantic action matching

For `executeAction click`, `executeAction type`, and WebDriver-only `executeAction select`, `--target <semanticText>` is intended to match the derived action description text.

Use short meaningful text that is likely to appear in the action description, for example:

- `executeAction click --target "Open"`
- `executeAction type --target "input_contact" --text "This_is_a_message"`
- `executeAction select --target "select_amount" --value "99999"`

Use named options exactly as documented.

Wrong:

- `executeAction type widget input_username john`

Correct:

- `executeAction type --target "input_username" --text "john"`

Use double quotes around target, text, and value when they contain spaces or special shell characters.

## Action availability

- `executeAction click` is available in Windows, WebDriver, and Android sessions.
- `executeAction type` is available in Windows, WebDriver, and Android sessions.
- `executeAction select` only makes sense in WebDriver sessions, where HTML select/dropdown elements can expose selectable values.
- Do not use `executeAction select` in Windows or Android sessions. For Windows or Android dropdowns or combo boxes, inspect `getDerivedActions` and use the available derived `click` or `type` actions instead.

## Workflow

1. Work from the CLI distribution root.
2. Start one session with either a workspace or an explicit platform and target.
3. Use `getState`, `getStateScreenshot`, and `getDerivedActions` to inspect the live UI.
4. Execute actions one at a time.
5. Re-check state for execution evidence.
6. Stop the session when the goal is finished.
7. Shutdown the daemon after stopping the session.

After executing one or multiple test goal sessions, always run:

- `shutdownDaemon`

Do this after the final `stopSession ...` command so no daemon or SUT-driver process remains active.

## Test goal verdicts

Agent CLI executions must finish each test goal with exactly one explicit verdict command:

- `stopSession LLM_COMPLETE <reason>`
- `stopSession LLM_INVALID <reason>`

Use `LLM_COMPLETE` only when the requested test goal was completed and the expected result was verified with CLI evidence.

Use `LLM_INVALID` when the expected result is not verified, a bug or invalid state is observed, the goal cannot be completed, or the available evidence is not reliable enough to declare completion.

The `<reason>` must briefly explain the final decision using observed CLI evidence.

## Resilience and retry behavior rules

Some SUTs may need time to start, load screens, populate state information, or expose derived actions after an action is executed.

When `getState`, `getStateScreenshot`, or `getDerivedActions` fails, returns incomplete information, or does not yet reflect the expected UI change:

- Do not assume the goal failed immediately.
- Retry the same observation command after a short wait.
- Prefer re-running `getState` before `getDerivedActions` when the UI may still be loading.
- Retry a small number of times before changing strategy.
- Keep retries sequential; do not run overlapping commands.
- Ground decisions in the latest successful command output.

If derived actions are missing or stale, first refresh the state with:

- `getState`
- `getStateScreenshot`
- `getDerivedActions`

Only execute an action after the relevant action appears in the latest `getDerivedActions` output.

## Session examples

Workspace-driven Windows testing:

- `testar-cli.bat sessionStatus`
- `testar-cli.bat startSession windows_generic`
- `testar-cli.bat getState`
- `testar-cli.bat getStateScreenshot`
- `testar-cli.bat getDerivedActions`
- `testar-cli.bat executeAction click --target "Open"`
- `testar-cli.bat executeAction type --target "Editor" --text "Writing text in Notepad"`
- `testar-cli.bat stopSession LLM_COMPLETE "Notepad opened and text was entered successfully."`
- `testar-cli.bat shutdownDaemon`

Workspace-driven WebDriver testing:

- `testar-cli.bat sessionStatus`
- `testar-cli.bat startSession webdriver_generic`
- `testar-cli.bat getState`
- `testar-cli.bat getStateScreenshot`
- `testar-cli.bat getDerivedActions`
- `testar-cli.bat executeAction click --target "a_about"`
- `testar-cli.bat executeAction type --target "input_contact" --text "This is a message"`
- `testar-cli.bat executeAction select --target "select_amount" --value "99999"`
- `testar-cli.bat stopSession LLM_INVALID "There is no confirmation that the transaction was sent successfully."`
- `testar-cli.bat shutdownDaemon`

Workspace-driven Android testing:

- `testar-cli.bat sessionStatus`
- `testar-cli.bat startSession android_generic`
- `testar-cli.bat getState`
- `testar-cli.bat getStateScreenshot`
- `testar-cli.bat getDerivedActions`
- `testar-cli.bat executeAction click --target "Graphics"`
- `testar-cli.bat executeAction type --target "username" --text "Writing text in Android"`
- `testar-cli.bat stopSession LLM_COMPLETE "The expected Android state was verified."`
- `testar-cli.bat shutdownDaemon`

Explicit standalone startup wihtout workspace:

- `testar-cli.bat startSession windows notepad.exe`
- `testar-cli.bat startSession webdriver https://para.testar.org/`
- `testar-cli.bat startSession android ApiDemos-debug.apk`

Explicit standalone startup with workspace:

- `testar-cli.bat startSession windows notepad.exe windows_generic`
- `testar-cli.bat startSession webdriver https://para.testar.org/ webdriver_generic`
- `testar-cli.bat startSession android ApiDemos-debug.apk android_generic`

## Operational rules

- Use one stable session per goal when possible.
- Run commands sequentially.
- `startSession <workspace>` derives platform and target from `SUTConnector` and related settings in the selected workspace.
- `startSession <platform> <target> [workspace]` is valid for standalone multi-platform control. If `[workspace]` is provided, load that workspace and override platform and target from the command.
- Treat `startSession`, `stopSession`, and `shutdownDaemon` as the public lifecycle commands.
- Prefer observing the live state through CLI commands.
- Keep evidence grounded in command outputs.
