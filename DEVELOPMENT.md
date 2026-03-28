# Development

TESTAR is being refactored toward a modular runtime with a first-class CLI control surface.

This repository still contains legacy protocol inheritance and mixed responsibilities. During the refactor, the goal is to improve the architecture step by step without breaking the existing scriptless testing logic.

## Current Priorities

- extract reusable runtime services from protocol-driven logic
- keep generate mode stable while services are introduced
- move platform-specific behavior behind module adapters
- make the runtime usable from both generate mode and CLI mode
- keep every refactor step compiling and tested

## Refactor Rules

- prefer composition over inheritance
- do not add new business logic directly to `DefaultProtocol` unless it is temporary compatibility glue
- keep `core` focused on contracts, value objects, and reusable abstractions
- keep `testar` focused on orchestration, compatibility, and high-level runtime flow
- keep platform-specific low-level behavior inside platform modules such as `windows`, `webdriver`, and `android`
- do not introduce a CLI-specific runtime that bypasses shared services
- do not mix architectural refactors with large mechanical cleanup in one commit
- do not remove old compatibility paths until extracted replacements are covered by tests

## Commit Rules

Every refactor step should be small enough to review and revert safely.

Each step should:

1. compile
2. pass affected tests
3. end in a focused commit

Recommended commit scope:

- one extraction
- one adapter migration
- one test slice
- one mechanical cleanup concern

Examples:

- `refactor(core): add runtime service contracts`
- `test(testar): add characterization tests for state flow`
- `refactor(windows): add modular session adapter`

## Testing Rules

- every behavior-preserving extraction should keep existing tests green
- every behavior change must add or adapt tests in the same step
- every new service should have direct unit tests where practical
- add characterization tests before moving risky legacy logic
- prefer small tests around one responsibility over broad integration-only coverage

Typical validation commands:

```powershell
.\gradlew.bat :core:test
.\gradlew.bat :windows:test
.\gradlew.bat :testar:test
.\gradlew.bat build
```

## Code Style

Use these style rules during the refactor:

- use 4-space indentation
- do not use tabs for indentation
- use braces for control flow
- keep one statement per line
- remove unused imports
- keep classes and methods focused on one responsibility
- prefer explicit names over abbreviated names
- add comments only where intent is not obvious from the code

These rules are progressively enforced through `config/checkstyle/checkstyle.xml`. Existing legacy code may still violate some rules; do not block incremental progress on mass-formatting unless the current step is explicitly a cleanup step.

## Module Direction

### `core`

Owns:

- service contracts
- execution/session abstractions
- shared value objects
- reusable interfaces

Should not own:

- platform-specific UI automation
- `DefaultProtocol`-style orchestration

### `testar`

Owns:

- generate and spy orchestration
- compatibility layers during migration
- service wiring

Should move away from:

- direct ownership of all runtime responsibilities inside `DefaultProtocol`

### Platform modules

Own:

- session start and stop adapters
- state capture adapters
- action derivation and direct execution primitives
- platform-specific widget semantics

## Legacy Protocol Guidance

Current protocol inheritance remains in place during migration, but it is transitional.

Rules:

- avoid adding new features by extending the protocol chain if a service extraction is possible
- when touching `GenericUtilsProtocol`, prefer extracting helpers into collaborators
- when touching `DefaultProtocol`, prefer delegation to extracted services
- when touching `GenerateMode` or `SpyMode`, move responsibilities out instead of adding more coupling

## CLI Direction

The CLI is a first-class public surface, not a side utility.

Rules:

- the CLI must call shared runtime services
- the CLI should return structured machine-readable results
- platform specifics must stay behind platform adapters
- CLI commands should be safe for one-command-per-step agent control

## Documentation

Keep the following documents aligned with the codebase:

- [`REFACTOR.md`](TESTAR_dev/REFACTOR.md)
- [`REFACTOR_ROADMAP.md`](TESTAR_dev/REFACTOR_ROADMAP.md)
- this file

If the architecture direction changes, update these documents in the same branch.
