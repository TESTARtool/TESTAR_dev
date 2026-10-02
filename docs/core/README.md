# Core Documentation

This directory specifies shared TESTAR behavior owned by the `core` module. Specifications are organized by capability, with supporting verification in the modules that consume or specialize the core contracts.

## Specifications

- [State identity](./state-identity.md): widget/state attribute encoding, root context, ordered hierarchy, and session-owned identification configuration.
- [Action identity](./action-identity.md): stable abstract and concrete action IDs, parameter abstraction, and widget/environment action identification.

## Documentation Structure

Each capability document contains its behavioral contract, representative acceptance scenarios, and links to the primary implementation. Write scenarios as plain Markdown Given/When/Then statements with explicit line breaks. Place a `Verification:` line above each scenario listing its test-class filenames, with links to those files. Include representative inputs in the scenario or a short accompanying paragraph. Test classes contain the detailed cases and assertions.

[Architecture](../ARCHITECTURE.md) describes module ownership, extension boundaries, and integration. Capability specifications define the behavior those implementations must preserve. Update the relevant contract, scenarios, and verification links together when behavior changes.
