# Modular Architecture

This document defines the current modular architecture of `TESTAR_dev`.

It describes the architecture that is already present in the codebase today. It is not a proposal for a future ideal architecture. Some parts are still transitional, but the contracts and layering described here are the active foundation that new modules should follow.

## Overview

The architecture is based on two complementary concepts:

- `services`: runtime capabilities that operate on a running SUT, a captured state, or a set of actions
- `policies`: decision rules that refine how reusable runtime logic behaves for a specific platform or mode

At a high level, the architecture separates responsibilities by layer:

1. `core`
   Defines stable contracts, shared domain objects, tags, actions, and policy interfaces.
2. `engine`
   Provides reusable implementations that compose `core` contracts into executable runtime behavior.
3. Platform modules such as `windows`, `webdriver`, and `android`
   Provide native implementations of platform-specific services and policies.
4. `plugin`
   Composes the services and policies for a concrete platform session.
5. `cli`
   Exposes the architecture through daemon-backed command execution and session management.
6. `testar`
   Exposes the architecture through the classic TESTAR runtime and user-interface driven execution model.

Supporting modules such as `config`, `statemodel`, `reporting`, `dialog`, `oracle`, `coverage`, and `llm` provide additional capabilities around this main service-and-policy architecture.

### Service and policy flow

The intended flow is:

1. a composition layer selects a platform and runtime configuration
2. platform modules provide native facts and native interaction capabilities
3. engine modules apply reusable logic on top of those capabilities
4. entry-point modules such as `cli` and `testar` invoke the composed runtime

This means that:

- `core` defines what a capability is
- `engine` defines reusable ways to use those capabilities
- platform modules define how those capabilities work on a concrete platform
- `plugin` decides which implementations are assembled together for a session

### Current module roles

The modules currently play these roles:

- `core`
  Owns the stable contracts and base domain model.
- `engine`
  Owns reusable logic such as default action derivation, action execution, action resolution, policy composition, and state processing.
- `windows`
  Owns Windows-native system, state, and policy implementations.
- `webdriver`
  Owns WebDriver-native system, state, and policy implementations.
- `android`
  Owns Android-native system, state, and policy implementations.
- `plugin`
  Owns platform orchestration and session composition.
- `cli`
  Owns command parsing, daemon communication, settings loading, and session-oriented automation commands.
- `testar`
  Owns the classic TESTAR runtime modes and higher-level protocol orchestration.

### Design intent

The architecture aims to keep these boundaries clear:

- `core` should not know about concrete platforms
- `engine` should not hardcode platform-specific heuristics
- platform modules should not own global orchestration
- entry-point modules should not contain low-level platform logic

In practice, the most important rule is:

- composition happens outside the contracts

So the architecture prefers:

- injecting a `ClickablePolicy`
- injecting a `TypeablePolicy`
- injecting an `EnabledPolicy`
- injecting a `BlockedPolicy`
- injecting a `StateService`

instead of:

- hardcoding platform decisions inside reusable runtime logic

## Core contracts

The `core` module defines the stable contracts that other modules implement or compose.

The main contract packages are:

- `org.testar.core.service`
- `org.testar.core.policy`
- `org.testar.core.action`
- `org.testar.core.state`
- `org.testar.core.tag`

### Service contracts

The primary runtime service contracts are in `org.testar.core.service`.

- `SystemService`
  Starts and stops the System Under Test.
  File: `core/src/org/testar/core/service/SystemService.java`
- `StateService`
  Builds the current `State` for a running `SUT`.
  File: `core/src/org/testar/core/service/StateService.java`
- `ActionDerivationService`
  Derives the currently available `Action` set from a `SUT` and a `State`.
  File: `core/src/org/testar/core/service/ActionDerivationService.java`
- `ActionExecutionService`
  Executes a selected `Action` against a `SUT` and `State`.
  File: `core/src/org/testar/core/service/ActionExecutionService.java`
- `ActionSelectorService`
  Selects one `Action` from a derived action set.
  File: `core/src/org/testar/core/service/ActionSelectorService.java`
- `OracleEvaluationService`
  Produces and stores `Verdict` information for a `State`.
  File: `core/src/org/testar/core/service/OracleEvaluationService.java`

These contracts intentionally stay small. They define the capability boundary, not the implementation strategy.

### Policy contracts

The main policy contracts are in `org.testar.core.policy`.

- `ClickablePolicy`
  Decides whether a widget should be considered clickable.
  File: `core/src/org/testar/core/policy/ClickablePolicy.java`
- `TypeablePolicy`
  Decides whether a widget should be considered typeable.
  File: `core/src/org/testar/core/policy/TypeablePolicy.java`
- `ScrollablePolicy`
  Decides whether a widget should be considered scrollable.
  File: `core/src/org/testar/core/policy/ScrollablePolicy.java`
- `EnabledPolicy`
  Decides whether a widget should be considered enabled as part of generic runtime eligibility.
  File: `core/src/org/testar/core/policy/EnabledPolicy.java`
- `BlockedPolicy`
  Decides whether a widget should be considered blocked as part of generic runtime eligibility.
  File: `core/src/org/testar/core/policy/BlockedPolicy.java`
- `WidgetFilterPolicy`
  Decides whether a widget should be filtered for state, action, oracle, or reporting logic.
  File: `core/src/org/testar/core/policy/WidgetFilterPolicy.java`
- `VisiblePolicy`
  Decides whether a widget should be considered visible for state, action, oracle, or reporting logic.
  File: `core/src/org/testar/core/policy/VisiblePolicy.java`
- `AtCanvasPolicy`
  Decides whether a widget should be considered inside the actionable or inspectable canvas area.
  File: `core/src/org/testar/core/policy/AtCanvasPolicy.java`
- `TopLevelPolicy`
  Decides whether a widget should be treated as a top-level widget for higher-level queries or orchestration.
  File: `core/src/org/testar/core/policy/TopLevelPolicy.java`

These policies are intentionally platform-agnostic. A policy contract does not know whether the widget comes from Windows UI Automation, WebDriver, Android, or another platform.

They are also not limited to action derivation. The same policy family can be reused by other services and queries such as:

- visible-widget state projections
- clickable/typeable widget state projections
- enabled and unblocked widget eligibility checks
- oracle evaluation scoped to visible widgets
- reporting or agent-facing filtered widget views

### Action resolution contracts

The `core` action package also defines contracts for resolving user- or mode-level action choices.

- `ActionResolver`
  Resolves a concrete `Action` from a set of actions and external arguments.
  File: `core/src/org/testar/core/action/resolver/ActionResolver.java`
- `ResolvedAction`
  Wraps the resolved `Action` selected by an `ActionResolver`.
  File: `core/src/org/testar/core/action/resolver/ResolvedAction.java`

This contract is especially important for `cli`, where commands such as `executeAction click ...` and `executeAction type ...` need a stable abstraction that is independent from concrete action implementations.

### Shared domain contracts

The core service contracts operate on shared domain types defined in `core`.

- `SUT`
  Represents the running System Under Test.
  File: `core/src/org/testar/core/state/SUT.java`
- `State`
  Represents a snapshot of the current system state.
  File: `core/src/org/testar/core/state/State.java`
- `Widget`
  Represents one node in the state tree.
  File: `core/src/org/testar/core/state/Widget.java`
- `Action`
  Represents an executable interaction.
  File: `core/src/org/testar/core/action/Action.java`
- `Verdict`
  Represents an oracle result.
  File: `core/src/org/testar/core/verdict/Verdict.java`
- `Tags`
  Defines shared metadata attached to systems, states, widgets, and actions.
  File: `core/src/org/testar/core/tag/Tags.java`

These types are the shared vocabulary that allows platform modules, engine logic, `cli`, and `testar` to collaborate through stable contracts.

## Engine

The `engine` module provides reusable runtime composition on top of the `core` contracts.

For the service-and-policy architecture, the main role of `engine` is to turn multiple policy implementations into one effective runtime policy per capability.

That means `engine` sits between:

- `core`, which defines the contracts
- platform modules, which provide concrete policy implementations
- composition layers such as `plugin`, which decide what should be assembled for a session

### Engine policy package

The policy composition classes are in:

- `org.testar.engine.policy`

Current policy classes in that package are:

- `CompositeClickablePolicy`
- `CompositeTypeablePolicy`
- `CompositeScrollablePolicy`
- `CompositeEnabledPolicy`
- `CompositeBlockedPolicy`
- `CompositeWidgetFilterPolicy`
- `CompositeVisiblePolicy`
- `CompositeAtCanvasPolicy`
- `CompositeTopLevelPolicy`
- `TagEnabledPolicy`
- `TagBlockedPolicy`
- `ConfiguredWidgetFilterPolicy`
- `ZIndexTopLevelPolicy`

### Composite policy classes

The `Composite...Policy` classes are not platform rules by themselves.

Their responsibility is to combine one or more concrete policies into a single runtime policy instance that can be consumed by an engine context.

This separation is important:

- a concrete policy defines one rule
- a composite policy defines how several rules become one effective policy

Examples:

- `CompositeClickablePolicy`
  Produces one `ClickablePolicy` from one or more clickable rules.
- `CompositeTypeablePolicy`
  Produces one `TypeablePolicy` from one or more typeable rules.
- `CompositeScrollablePolicy`
  Produces one `ScrollablePolicy` from one or more scrollable rules.
- `CompositeEnabledPolicy`
  Produces one `EnabledPolicy` from one or more enabled rules.
- `CompositeBlockedPolicy`
  Produces one `BlockedPolicy` from one or more blocked rules.
- `CompositeWidgetFilterPolicy`
  Produces one `WidgetFilterPolicy` from one or more explicit widget-filter rules.
- `CompositeVisiblePolicy`
  Produces one `VisiblePolicy` from one or more visibility rules.
- `CompositeAtCanvasPolicy`
  Produces one `AtCanvasPolicy` from one or more canvas-scope rules.
- `CompositeTopLevelPolicy`
  Produces one `TopLevelPolicy` from one or more top-level rules.

The composition semantics are intentionally different depending on the contract:

- clickable, typeable, and scrollable act as capability unions
  - if one policy grants the capability, the composed capability is available
- enabled acts as an eligibility intersection
  - if one policy says the widget is not enabled, the composed enabled result is false
- blocked acts as a blocking union
  - if one policy says the widget is blocked, the composed blocked result is true
- widget filtering acts as a filter intersection
  - if one policy rejects the widget, the composed filter rejects it
- visible acts as a visibility intersection
  - if one policy says the widget is not visible, the composed visible result is false
- at-canvas acts as a scope intersection
  - if one policy says the widget is outside the active canvas, the composed result is false
- top-level acts as a top-layer intersection
  - if one policy says the widget is not part of the top-level set, the composed result is false

These semantics are chosen to match the meaning of each contract, not just the fact that several policies may be present.

### Generic engine policies

The `engine` module also contains generic policy implementations that are not tied to a specific platform:

- `TagEnabledPolicy`
  Reads `Tags.Enabled` and exposes that as an `EnabledPolicy`.
- `TagBlockedPolicy`
  Reads `Tags.Blocked` and exposes that as a `BlockedPolicy`.
- `ConfiguredWidgetFilterPolicy`
  Applies configured hit-test and tag-regex filtering as a `WidgetFilterPolicy`.
- `ZIndexTopLevelPolicy`
  Reads `Tags.ZIndex` and `Tags.MaxZIndex` and exposes top-level membership as a `TopLevelPolicy`.

These classes are generic adapters from the shared tag model into the generic policy contracts.

### Policy composition in engine contexts

The current policy composition is visible in:

- `engine/src/org/testar/engine/action/derivation/ActionDerivationContext.java`

That context currently groups:

- `ClickablePolicy`
- `TypeablePolicy`
- `ScrollablePolicy`
- `EnabledPolicy`
- `BlockedPolicy`
- `WidgetFilterPolicy`

This shows the intended role of `engine`:

- the runtime consumes one effective policy of each kind
- composite policies are the mechanism that joins multiple implementations before execution

Other runtime contexts may group different subsets of the same policy families. For example, a state query focused on visible widgets could consume `VisiblePolicy`, `AtCanvasPolicy`, and `TopLevelPolicy` without depending on action-derivation concerns.

### Mermaid diagram

The following diagram focuses only on how services or contexts consume a composed policy view.

It intentionally ignores, for now:

- deriver internals
- state-service internals
- selector internals

Diagram file:

- `docs/architecture_policy_composition.mmd`

This diagram represents the current pattern at the policy-composition level:

- `core` defines the contract type
- platform modules and generic engine helpers provide concrete implementations
- `engine.policy` composes them into one effective policy per capability
- engine contexts consume those composed policies

The diagram stays intentionally simple. It focuses on policy families and composition semantics rather than showing every concrete consumer or every concrete platform class in full detail.

## Next sections

The next sections of this document should describe:

- platform modules such as `windows` and `webdriver`
- `plugin`
- `cli`
- `testar`
