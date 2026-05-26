# Scriptless Configuration Guide

This guide explains how to configure and extend the scriptless TESTAR runtime without editing internal protocol classes.

It focuses on the supported customization seams:

- `test.settings`
- `composition.properties`
- `policies.properties`

## Purpose

Use this guide when you need to answer:

- where should this customization go?
- which file should I edit?
- should I use a policy, a service wrapper, or an identifier-service wrapper?

## Configuration files

### `test.settings`

Use `test.settings` for:

- connector selection
- SUT target settings
- runtime mode settings
- built-in filtering and state-model settings
- selecting:
  - `CompositionProfile`
  - `CustomCompositionResource`
  - `CustomPoliciesResource`

Do not use `test.settings` to declare Java wrapper classes directly.

### `composition.properties`

Use `composition.properties` when you need to wrap or extend:

- services
- scriptless capabilities

Current service-side keys:

- `systemServiceClass`
- `stateServiceClass`
- `stateIdentifierServiceClass`
- `actionDerivationServiceClass`
- `actionIdentifierServiceClass`
- `actionSelectorServiceClass`
- `actionExecutionServiceClass`
- `oracleComposerClass`

Current capability-side keys:

- `settingsCapabilityClass`
- `testSessionCapabilityClass`
- `testSequenceCapabilityClass`
- `stopCriteriaCapabilityClass`

Each class acts as a wrapper around the built-in delegate.

### `policies.properties`

Use `policies.properties` when you need to change widget or state decision rules.

Current policy seams include:

- `clickablePolicies`
- `typeablePolicies`
- `scrollablePolicies`
- `selectablePolicies`
- `enabledPolicies`
- `blockedPolicies`
- `widgetFilterPolicies`
- `visiblePolicies`
- `topLevelPolicies`

Use additive mode when you want to extend built-in behavior.

Use replacement mode when you want to discard the built-in policy family for that seam.

## Where a customization should go

Use this decision guide:

- new rule for whether a widget is clickable, visible, enabled, blocked, or filtered
  - use a policy in `policies.properties`
- new behavior around state capture, action derivation, action selection, action execution, or oracle composition
  - use a service wrapper in `composition.properties`
- new TESTAR runtime lifecycle behavior such as session hooks, sequence hooks, or stop criteria
  - use a capability wrapper in `composition.properties`
- new state or action identification adaptation for SUT-specific or domain-specific needs
  - use `stateIdentifierServiceClass` or `actionIdentifierServiceClass`
- new platform-native low-level behavior
  - this is internal development work in a platform module, not normal scriptless configuration

## Common recipes

### 1. Add a custom policy

Use this when the customization is a rule such as:

- hide widgets outside the browser canvas
- block denied links
- redefine clickable/typeable eligibility

Steps:

1. Create the Java policy class in the chosen settings workspace.
2. Add the class name to `policies.properties`.
3. Decide whether it should be additive or replacement.

Example:

```properties
visiblePolicies=WebdriverCanvasVisiblePolicy
replaceVisiblePolicies=false
```

Concrete workspace example:

- `testar/resources/settings/webdriver_generic/policies.properties`
- `testar/resources/settings/webdriver_generic/WebdriverCanvasVisiblePolicy.java`
- `testar/resources/settings/webdriver_generic/WebdriverLinkDeniedFilterPolicy.java`

### 2. Wrap a service

Use this when the customization changes runtime behavior rather than a boolean rule.

Examples:

- decorate `StateService`
- decorate `ActionSelectorService`
- decorate `ActionExecutionService`

Steps:

1. Create a wrapper class that accepts the built-in delegate.
2. Register it in `composition.properties`.
3. Keep the wrapper focused on the specific SUT behavior you need.

Example:

```properties
stateServiceClass=MyCustomStateService
```

Concrete workspace examples:

- `testar/resources/settings/webdriver_generic/composition.properties`
- `testar/resources/settings/webdriver_generic/WebdriverParabankTestSequenceLoginCapability.java`
- `testar/resources/settings/webdriver_generic/WebdriverWidgetLeafOverlapOracleComposer.java`

Those two examples show:

- a `testSequenceCapabilityClass` wrapper that performs a SUT-specific login sequence
- an `oracleComposerClass` wrapper that adds a visual overlap oracle on top of the built-in verdict flow

### 3. Adapt state or action identification

Use this when the built-in state or action identification mechanism needs SUT-specific or domain-specific adaptation.

Examples:

- dynamic widget attributes that make states unstable
- action identity that depends on volatile widget data
- domain-specific fields that should dominate state equivalence
- SUT-specific widget attributes that should be normalized before identifiers are built

Steps:

1. Create a `StateIdentifierService` wrapper or `ActionIdentifierService` wrapper.
2. Adapt the widget or action data before delegating to the built-in identifier service.
3. Register the wrapper in `composition.properties`.

Example:

```properties
stateIdentifierServiceClass=WebdriverParabankStateIdentifierService
```

Concrete workspace example:

- `testar/resources/settings/webdriver_generic/WebdriverParabankStateIdentifierService.java`

This seam replaces the old protocol-level customization style for state and action identification.

## Backed example workspace

The shipped `testar/resources/settings/webdriver_generic` workspace now demonstrates the three main customization categories together:

- policy examples in `testar/resources/settings/webdriver_generic/policies.properties`
- service-side identification adaptation in `testar/resources/settings/webdriver_generic/WebdriverParabankStateIdentifierService.java`
- capability and oracle composition in `testar/resources/settings/webdriver_generic/composition.properties`

## What not to customize first

Do not start by editing:

- `ComposedProtocol`
- plan classes
- `PlatformOrchestrator`
- engine internals

unless the supported seams are clearly insufficient.

The preferred order is:

1. settings
2. external policy wrapper
3. external service or capability wrapper
4. internal module change only when the supported seams cannot express the need

## Constructor expectations

Wrapper loading supports delegates first.

Typical service wrapper shape:

```java
public final class MyStateService implements StateService {

    private final StateService delegate;

    public MyStateService(StateService delegate) {
        this.delegate = delegate;
    }
}
```

Typical capability wrapper shape:

```java
public final class MyTestSequenceCapability extends TestSequenceCapability {

    private final TestSequenceCapability delegate;

    public MyTestSequenceCapability(TestSequenceCapability delegate) {
        this.delegate = delegate;
    }
}
```

Typical identifier wrapper shape:

```java
public final class MyStateIdentifierService implements StateIdentifierService {

    private final StateIdentifierService delegate;

    public MyStateIdentifierService(StateIdentifierService delegate) {
        this.delegate = delegate;
    }
}
```

## Practical rule of thumb

If the change answers:

- "is this widget considered X?"
  - use a policy
- "how should runtime behavior be extended for this SUT?"
  - use a service or capability wrapper
- "how should state or action identification be adapted for this SUT?"
  - use an identifier-service wrapper

If the change does not fit any of those seams cleanly, it is probably internal architecture work rather than normal scriptless configuration.
