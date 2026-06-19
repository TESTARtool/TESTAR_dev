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
- `startSession windows <path>`
- `startSession webdriver <url>`
- `startSession android <apk>`
- `getState`
- `getStateScreenshot`
- `getDerivedActions`
- `executeAction click <semanticText>`
- `executeAction type <semanticText> <inputText>`
- `executeAction select <semanticText> <value>`
- `stopSession`
- `shutdownDaemon`

## Semantic action matching

For `executeAction click`, `executeAction type`, and WebDriver-only `executeAction select`, `<semanticText>` is intended to match the derived action description text.

Use short meaningful text that is likely to appear in the action description, for example:

- `executeAction click Open`
- `executeAction type input_contact This_is_a_message`
- `executeAction select select_amount 99999`

## Action availability

- `executeAction click` is available in Windows, WebDriver, and Android sessions.
- `executeAction type` is available in Windows, WebDriver, and Android sessions.
- `executeAction select` only makes sense in WebDriver sessions, where HTML select/dropdown elements can expose selectable values.
- Do not use `executeAction select` in Windows or Android sessions. For Windows or Android dropdowns or combo boxes, inspect `getDerivedActions` and use the available derived `click` or `type` actions instead.

## Workflow

1. Work from the CLI distribution root.
2. Start one session with the selected platform and target.
3. Use `getState`, `getStateScreenshot`, and `getDerivedActions` to inspect the live UI.
4. Execute actions one at a time.
5. Re-check state for execution evidence.
6. Stop the session when the goal is finished.
7. Shutdown the daemon after stopping the session.

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

Windows testing:

- `testar-cli.bat sessionStatus`
- `testar-cli.bat startSession windows notepad.exe`
- `testar-cli.bat getState`
- `testar-cli.bat getStateScreenshot`
- `testar-cli.bat getDerivedActions`
- `testar-cli.bat executeAction click Open`
- `testar-cli.bat executeAction type Editor Writing_text_in_Notepad`
- `testar-cli.bat stopSession`
- `testar-cli.bat shutdownDaemon`

WebDriver testing:

- `testar-cli.bat sessionStatus`
- `testar-cli.bat startSession webdriver https://para.testar.org/`
- `testar-cli.bat getState`
- `testar-cli.bat getStateScreenshot`
- `testar-cli.bat getDerivedActions`
- `testar-cli.bat executeAction click a_about`
- `testar-cli.bat executeAction type input_contact This_is_a_message`
- `testar-cli.bat executeAction select select_amount 99999`
- `testar-cli.bat stopSession`
- `testar-cli.bat shutdownDaemon`

Android testing:

- `testar-cli.bat sessionStatus`
- `testar-cli.bat startSession android ApiDemos-debug.apk`
- `testar-cli.bat getState`
- `testar-cli.bat getStateScreenshot`
- `testar-cli.bat getDerivedActions`
- `testar-cli.bat executeAction click Graphics`
- `testar-cli.bat executeAction type username Writing_text_in_Android`
- `testar-cli.bat stopSession`
- `testar-cli.bat shutdownDaemon`

## Operational rules

- Use one stable session per goal when possible.
- Run commands sequentially.
- Treat `startSession`, `stopSession`, and `shutdownDaemon` as the public lifecycle commands.
- Prefer observing the live state through CLI commands.
- Keep evidence grounded in command outputs.
