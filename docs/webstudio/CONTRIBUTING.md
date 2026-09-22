# Contributing to WebStudio Documentation

This guide defines how WebStudio requirements, acceptance scenarios, traces, code markers, and tests should evolve together.

## Requirements

Use stable identifiers:

- `WS-FUNC-...` for functional behavior
- `WS-UX-...` for interaction and presentation behavior
- `WS-SCENARIO-...` for acceptance scenarios

Write one observable behavior per requirement. Prefer `must` for required behavior and `should` only for intentional recommendations.

Requirements should state what WebStudio provides. Avoid implementation details, repeated background information, historical development phases, and lists of behavior that the application does not provide.

Define cross-cutting behavior once. Capability specifications should reference shared contracts instead of reproducing them for every editor or view.

## Acceptance Scenarios

Add acceptance scenarios for workflows with meaningful regression risk, including:

- persistence and dirty-state transitions
- guarded navigation
- destructive actions
- asynchronous runtime state
- workspace-scoped data
- multi-step editor workflows

Do not create scenarios for every static label or simple rendering detail.

Use concise Given/When/Then statements. Every `WS-SCENARIO-*` block must contain explicit `Given`, `When`, and `Then` lines. Keep verification references close to the scenario without repeating full trace information.

## Traceability

Every `WS-FUNC-*` and `WS-UX-*` requirement must have a trace entry.

A trace entry should link only to:

- the requirement specification
- primary production implementation files
- primary automated verification files

Do not list every helper, component, or indirectly related test. Repository search and source markers provide the detailed path when needed.

`SPEC_TRACE.md` is the authoritative many-to-many map. Source markers are optional navigation hints, not a second trace database.

The trace document must not repeat requirement or acceptance-scenario text.

## Source Markers

Use source markers only where they add useful navigation:

```java
// Implements WS-FUNC-STATE-MODEL-001: controls the state model analysis lifecycle.
```

```js
// Verifies WS-FUNC-TEST-SETTINGS-001: settings persistence request contract.
```

Prefer a class-level, file-level, or test-suite marker when the surrounding code has one clear responsibility. Do not annotate every helper or individual assertion.

Keep at most one marker in the primary owning service, model, or component for a requirement. A method-level marker is appropriate only when a mixed-responsibility file owns the behavior in one contained area.

Do not add markers to DTOs, endpoint wrappers, pass-through components, generic helpers, or lower-level reusable modules. Those files belong in `SPEC_TRACE.md` when they are materially relevant. Reusable modules should use their own module requirement namespace if module-level specifications are introduced later.

Add verification markers only to focused suites that directly exercise the named contract. A marker does not need to enumerate every related requirement listed for that suite in `SPEC_TRACE.md`.

## Verification

Select test layers according to the behavior:

- model tests for pure decisions, parsing, synchronization, and state transitions
- API contract tests for URLs, methods, headers, payloads, and responses
- backend unit or integration tests for services, storage, compilation, and runtime orchestration
- workflow tests for high-risk user journeys spanning multiple views or actions

A refactor that moves orchestration must preserve workflow-level verification. Unit tests for extracted helpers are useful but do not replace tests of the complete user transition.

## Architecture and Guides

Architecture documentation owns module boundaries, dependencies, runtime flows, and design invariants.

Guides own user and developer procedures, examples, and configuration instructions.

Specifications may link to architecture documents or guides, but should not copy their content.

## Bugs

Keep the bug backlog focused on confirmed open defects.

When fixing a bug:

1. Identify the requirement that was violated.
2. Add or update an acceptance scenario when the regression risk is significant.
3. Add automated verification at the appropriate level.
4. Remove the fixed item from the active backlog.

Git history and automated tests provide the long-term record of fixed defects.

## Required Checks

Run trace validation after changing specifications, scenarios, traces, or source markers:

```powershell
node docs/webstudio/validate-spec-trace.mjs
```

For frontend behavior changes:

```powershell
cd webstudio/frontend
npm.cmd run test
npm.cmd run build
```

For backend behavior changes, run the relevant Gradle module tests.

## Review Checklist

- The behavior is documented in one authoritative place.
- Existing requirement and scenario identifiers remain stable unless intentionally retired.
- Acceptance scenarios cover meaningful regression risks.
- Trace entries contain only primary implementation and verification links.
- Source markers are useful and sparse.
- Relevant automated tests pass.
- Trace validation passes.

