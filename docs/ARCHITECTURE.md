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

- `Policy`
  Root marker interface for policy families that can be registered in shared policy contexts.
  File: `core/src/org/testar/core/policy/Policy.java`
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

- `TagEnabledPolicy`
- `TagBlockedPolicy`
- `ConfiguredWidgetFilterPolicy`
- `ZIndexTopLevelPolicy`

### Composite policy classes

The `Composite...Policy` classes in `org.testar.engine.policy.composite` are not platform rules by themselves.

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

The composite package also contains internal composition helpers:

- `CompositePolicySupport`
  Shared internal helper that evaluates policy lists with a composition rule.
- `CompositePolicyRule`
  Declares the composition rule used by a composite, such as `ANY` or `ALL`.

These two classes reduce duplication across the named composite wrappers. Services and factories should depend on the named composites, not on these internal helpers directly.

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

- `engine/src/org/testar/engine/policy/SessionPolicyContext.java`

That shared context is now a typed policy registry.

It stores effective policies by their policy-interface type, for example:

- `ClickablePolicy.class`
- `TypeablePolicy.class`
- `ScrollablePolicy.class`
- `EnabledPolicy.class`
- `BlockedPolicy.class`
- `WidgetFilterPolicy.class`
- `VisiblePolicy.class`
- `AtCanvasPolicy.class`
- `TopLevelPolicy.class`

This shows the intended role of `engine`:

- the runtime consumes one effective policy of each kind
- composite policies are the mechanism that joins multiple implementations before execution
- the shared session context can grow with new policy families without adding one new field and getter per policy type

Services can consume different subsets of the same shared policy context. For example, action derivation may use clickable, typeable, visible, enabled, and top-level policies, while a state projection may use visible, at-canvas, top-level, and widget-filter policies.

### Plugin default policy composition

The `plugin` module now owns the default platform policy bundles that are used to build a session.

Current default composition entry point:

- `plugin/src/org/testar/plugin/policy/PlatformPolicyContexts.java`

This means the layering is now:

- `engine`
  Defines the policy registry contract and the reusable composite implementations.
- `plugin`
  Decides which default policy bundle should be used for a platform session such as Windows or WebDriver.
- `cli` and `testar`
  Can later override or extend those defaults through higher-level configuration.

This keeps `SessionPolicyContext` reusable while moving platform default choices out of engine service factories.

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
- `engine.policy.composite` composes them into one effective policy per capability
- `plugin.policy` provides default platform bundles
- engine contexts consume the resulting session policy context

The diagram stays intentionally simple. It focuses on policy families and composition semantics rather than showing every concrete consumer or every concrete platform class in full detail.

## Services

The service architecture follows the same modular composition idea as the policy architecture.

At the service level, the question is not "which widget rule applies?" but:

- which runtime capability is needed
- which layer owns the reusable implementation
- which layer provides the platform-native implementation
- which layer assembles the final service pipeline for a session

### Service architecture overview

The current service architecture is based on these rules:

1. `core` defines service contracts.
2. `engine` provides reusable service implementations and service-level orchestration.
3. platform modules provide native implementations where platform access is required.
4. `plugin` assembles the final service instances for a platform session.
5. entry points such as `cli` and `testar` invoke the composed services.

This means services follow a different responsibility split than policies:

- policies refine runtime decisions
- services execute runtime work

Examples:

- `StateService`
  Captures or prepares a `State`.
- `ActionDerivationService`
  Produces available `Action` objects from a `State`.
- `ActionExecutionService`
  Executes a selected `Action`.
- `ActionSelectorService`
  Chooses one `Action` from the derived set.
- `OracleEvaluationService`
  Produces and stores `Verdict` information.

### Current service contracts

The current stable service families are:

- `SystemService`
  Starts and stops the SUT lifecycle.
- `StateService`
  Produces the current `State` from a running `SUT`.
- `ActionDerivationService`
  Produces the current `Action` set from a `SUT` and `State`.
- `ActionExecutionService`
  Executes an `Action` against a `SUT` and `State`.
- `ActionSelectorService`
  Chooses one `Action` from a derived action set.
- `OracleEvaluationService`
  Stores and retrieves `Verdict` information for a `State`.

These contracts remain intentionally small. They describe capability boundaries, not algorithm details.

### Service layering by module

The current layering is:

- `core`
  Declares the service contracts.
- `engine`
  Implements reusable service logic that can run on top of native services or action/state objects.
- platform modules such as `windows` and `webdriver`
  Provide native service implementations where platform access is required.
- `plugin`
  Chooses and composes the concrete service instances for a session.

This can be seen directly in the current code:

- native state capture lives in platform modules
  - `windows/src/org/testar/windows/service/WindowsStateService.java`
  - `webdriver/src/org/testar/webdriver/service/WebdriverStateService.java`
- reusable state composition lives in engine
  - `engine/src/org/testar/engine/service/ComposedStateService.java`
  - `engine/src/org/testar/engine/state/StateCompositionPlan.java`
- reusable action derivation orchestration lives in engine
  - `engine/src/org/testar/engine/service/ComposedActionDerivationService.java`
  - `engine/src/org/testar/engine/action/derivation/StateActionDeriver.java`
- platform session assembly lives in plugin
  - `plugin/src/org/testar/plugin/PlatformOrchestrator.java`

### Shared contexts versus service-specific dependencies

The current architecture now has one shared session-level policy registry:

- `engine/src/org/testar/engine/policy/SessionPolicyContext.java`

Its responsibility is:

- hold the effective policy set for one session
- expose those policies by contract type
- allow multiple services to consume the same runtime policy view

That shared context is intentionally limited to policies.

It should not become a generic container for all service runtime data.

The current boundary is:

- shared session context
  - policy objects such as `ClickablePolicy`, `VisiblePolicy`, `EnabledPolicy`, `TopLevelPolicy`
- service-specific dependencies
  - derivers
  - native delegates
  - selector strategies
  - text-input providers
  - fallback strategies

This keeps reuse high without turning the session context into an oversized runtime bag.

### Current reusable engine services

The current reusable engine service implementations are:

- `ComposedSystemService`
  Composes one native `SystemService` with a `SystemCompositionPlan` so session start/stop hooks can be added without changing the native platform service.
- `ComposedStateService`
  Applies engine-level state preparation and then delegates to a `StateCompositionPlan` using the shared `SessionPolicyContext`.
- `ComposedActionDerivationService`
  Runs action derivation in three ordered phases:
  - forced derivers
  - default derivers
  - fallback derivers
- `ComposedActionExecutionService`
  Composes one base `ActionExecutionService` with an `ActionExecutionPlan` so execution hooks can be added around the base executor.
- `ComposedActionSelectorService`
  Composes one primary `ActionSelectorService` with a fallback selector through an `ActionSelectorPlan`.
- `ComposedActionResolver`
  Composes one `ActionResolver` strategy through an `ActionResolverPlan`.
- `BasicActionExecutionService`
  Provides the current minimal base executor used by the default execution plans.

The important architectural point is that engine services do not own platform-native access.

Instead, engine services operate on:

- native services delegated from platform modules
- shared domain objects such as `State`, `Widget`, and `Action`
- shared policies from `SessionPolicyContext`
- service-specific plans such as `SystemCompositionPlan`, `StateCompositionPlan`, `ActionDerivationPlan`, `ActionSelectorPlan`, `ActionExecutionPlan`, and `ActionResolverPlan`

### First reference flow: system -> state -> derive actions -> select or resolve action -> execute action -> oracle

This is the first service pipeline that should guide the architecture.

#### Step 1: system

Current flow:

1. `plugin` chooses the native system service for the active platform.
2. `plugin` wraps it with `ComposedSystemService`.
3. the native service starts or connects to the SUT.
4. optional lifecycle hooks are applied through `SystemCompositionPlan`.

#### Step 2: state

Current flow:

1. `plugin` chooses the native state service for the active platform.
2. `plugin` wraps it with `ComposedStateService`.
3. the native service captures the raw platform `State`.
4. `ComposedStateService` applies reusable engine-side processing and then runs the selected `StateCompositionPlan`.

Current examples:

- Windows session:
  - `WindowsStateService.uiAutomation(...)`
  - wrapped by `ComposedStateService`
- WebDriver session:
  - `WebdriverStateService.browser(...)`
  - wrapped by `ComposedStateService`

So the current state architecture already follows:

- platform module captures
- engine enriches
- plugin composes

#### Step 3: derive actions

Current flow:

1. `plugin` creates the `SessionPolicyContext` through `PlatformPolicyContexts`.
2. `plugin` chooses the platform-appropriate derivation plan.
3. `ComposedActionDerivationService` is created from the shared policy context and the derivation plan.
4. `ComposedActionDerivationService` runs the configured derivation phases.
5. `StateActionDeriver` traverses the `State`.
6. `StateActionDeriver` asks the active policies whether a widget should participate.
7. the platform-specific `WidgetActionDeriver` returns the platform-appropriate actions for each eligible widget.

This is where policies and services meet most clearly.

The current derivation gating in `StateActionDeriver` uses:

- `EnabledPolicy`
- `BlockedPolicy`
- `WidgetFilterPolicy`
- `VisiblePolicy`
- `AtCanvasPolicy`
- `TopLevelPolicy`

Then the widget-level deriver uses capability policies such as:

- `ClickablePolicy`
- `TypeablePolicy`
- `ScrollablePolicy`

So the derive-actions architecture is:

- service decides traversal and orchestration
- policies decide eligibility and capabilities
- platform widget derivers decide which concrete actions to build

#### Step 4: select or resolve action

Current flow:

Current flow has two variants:

1. a choose-action service receives the current `State` and derived `Action` set
2. selector-based runtime flow:
   `ComposedActionSelectorService` uses the selected `ActionSelectorPlan`
3. resolver-based external-command flow:
   `ComposedActionResolver` uses the selected `ActionResolverPlan`
4. one concrete `Action` is selected or resolved for execution

Current selector examples:

- `RandomActionSelector`
- `QLearningActionSelector`
- `ComposedActionSelectorService`

Current resolver examples:

- `DescriptionActionResolver`
- `ComposedActionResolver`

Selection and resolution stay as separate contracts, but the overview architecture treats both as the same conceptual step:

- choose one concrete action from the derived action set

#### Step 5: execute action

Current flow:

1. the selected `Action` is passed to `ComposedActionExecutionService`
2. the selected `ActionExecutionPlan` runs before-execution hooks
3. the base execution service executes the concrete action
4. after-execution hooks observe the execution result

#### Step 6: oracle

Current flow:

1. oracle evaluation examines the current state after execution
2. verdict information is produced and stored
3. later reporting and higher-level orchestration consume those results

### Service architecture rule of thumb

When deciding where new logic belongs, use this split:

- if the logic answers "should this widget be considered X?"
  - it is probably a policy
- if the logic answers "how do we build, derive, execute, or select?"
  - it is probably a service
- if the logic answers "which concrete implementations should be active in this session?"
  - it belongs in composition, currently `plugin`

### Action resolution architecture

Action resolution is not modeled as the same contract as action selection, but it now follows the same composition pattern:

- `core`
  defines `ActionResolver` and `ResolvedAction`
- `engine`
  provides `ComposedActionResolver` and `ActionResolverPlan`
- `plugin`
  composes the active resolver strategy for the session

This keeps the semantics separate:

- selection
  chooses one action autonomously from a derived set
- resolution
  matches one action against external arguments

But it keeps the architecture aligned:

- contract
- plan
- composed engine implementation
- plugin composition

`PlatformOrchestrator` now composes the active runtime services in this conceptual order:

- system
- state
- derive actions
- choose action
- execute action
- oracle

### Mermaid diagram

The service architecture is documented separately from the policy-composition diagram.

Diagram file:

- `docs/architecture_overview_service_pipeline.mmd`
- `docs/architecture_action_derivation_service_pipeline.mmd`
- `docs/architecture_action_selection_service_pipeline.mmd`
- `docs/architecture_action_resolution_pipeline.mmd`

The service overview diagram focuses on:

- service contracts
- engine reusable services
- platform-native service implementations
- plugin-side service assembly
- the current `system -> state -> derive actions -> select or resolve action -> execute action -> oracle` flow

The action-derivation diagram focuses on:

- derivation plans
- the shared session policy context
- the composed derivation service
- state traversal
- platform-specific widget derivation
- generic engine derivers used by desktop-style plans

## Next sections

The next sections of this document should describe:

- platform modules such as `windows` and `webdriver`
- `plugin`
- `cli`
- `testar`
