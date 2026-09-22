# WebStudio Documentation

This directory documents the behavior, user experience, implementation traceability, and known issues of TESTAR WebStudio.

The documentation is organized into capability specifications. Existing requirement and scenario identifiers remain stable.

## Current Documents

- [`SPEC_TRACE.md`](./SPEC_TRACE.md): requirement-to-implementation and test mapping
- [`BUG_BACKLOG.md`](./BUG_BACKLOG.md): known and historical defects
- [`CONTRIBUTING.md`](./CONTRIBUTING.md): documentation and verification rules
- [`specs/`](./specs/README.md): capability specifications and acceptance scenarios

## Structure

WebStudio specifications should be organized by capability rather than by document type.

```text
docs/webstudio/
|-- README.md
|-- CONTRIBUTING.md
|-- specs/
|   |-- README.md
|   |-- foundations.md
|   |-- configuration-guard.md
|   |-- workspace-management.md
|   |-- settings.md
|   |-- composition-and-policies.md
|   |-- test-oracles.md
|   |-- runtime-modes.md
|   |-- test-goals.md
|   |-- test-results.md
|   |-- state-model.md
|   `-- debug-files.md
|-- SPEC_TRACE.md
|-- BUG_BACKLOG.md
`-- validate-spec-trace.mjs
```

The capability boundaries are authoritative.

## Information Ownership

Each capability specification should contain:

- observable functional requirements
- interaction and presentation requirements specific to that capability
- acceptance scenarios for regression-prone workflows

Cross-cutting behavior belongs in `foundations.md` or a focused cross-cutting specification. Navigation, layout stability, modal behavior, accessibility, and source-editor conventions belong in `foundations.md`; dirty-state and unsaved-change protection belongs in `configuration-guard.md`.

The trace document maps stable requirement identifiers to primary implementation and verification files. It does not repeat requirement or scenario text.

Architecture documents explain module responsibilities and technical boundaries. Guides explain how users or developers perform tasks. Neither should duplicate behavioral requirements.

The bug backlog contains open defects and a concise archive of design-relevant fixes. A fixed defect should become an acceptance scenario when its behavior remains important to protect.

## Change Rules

For each changed capability:

1. Update its functional, UX, and acceptance content in the same capability specification.
2. Preserve existing `WS-FUNC-*`, `WS-UX-*`, and `WS-SCENARIO-*` identifiers unless the behavior is intentionally replaced.
3. Update trace links when primary implementation or verification ownership changes.
4. Run trace validation and relevant automated tests.

Do not recreate separate functional, UX, or acceptance-scenario monoliths. If specifications conflict with implementation, record the conflict for review before choosing the authoritative behavior.

## Documentation Principles

- Describe what WebStudio does, not the current internal implementation sequence.
- Prefer positive, observable requirements.
- Define shared behavior once and reference it from individual capabilities.
- Keep acceptance scenarios focused on workflows likely to regress.
- Keep implementation details in architecture documents, code, and tests.
- Keep traceability compact and navigable.
