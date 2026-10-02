# Action Identity

[Core documentation](./README.md) | [Architecture](../ARCHITECTURE.md#action-identity)

## Purpose

TESTAR identifies actions so exploration and state-model recording can recognize equivalent behavior while retaining distinct concrete executions.

Actions receive an abstract ID in `Tags.AbstractID` and a concrete ID in `Tags.ConcreteID`, using the `AA` and `AC` prefixes respectively.

## Behavioral Contract

### Identity Inputs

| Component | Abstract identity | Concrete identity |
| --- | --- | --- |
| State and origin widget | Abstract IDs | Concrete IDs |
| Action kind | Implementation and role | Implementation and role |
| Parameters | Behavior-distinguishing parameters | Full execution parameters |

The current state and target widgets are identified before action identification. An action without an origin widget has an explicit absent-target identity. An attached origin widget supplies its IDs even when it has no `Tags.Path`.

### Stability and Distinctions

- Equivalent action instances receive the same IDs for the same identity inputs, independently of action-set order or the presence of other actions.
- Interchangeable typed and pasted values share an abstract identity for the same operation and target. Their concrete identities retain the full effective input, including applicable `Tags.InputText` overrides.
- Behavior distinctions include selected options, mouse buttons, keys, drag destinations, relative target offsets, and compound step order and relative timing. Different implementations or roles also distinguish actions on the same widget.
- Identity is derived from execution data. Descriptions and incidental Java object identities remain presentation information.
- Widget-relative positions use target IDs and relative position parameters. Cached screen-coordinate changes leave action IDs unchanged when the identity inputs remain unchanged. Absolute-position actions include their coordinates.
- Widget and environment identification entry points follow the same rules. Missing required state or target IDs cause identification to fail explicitly.
- State-model consumers retain the assigned action IDs. Concrete input variants can reference their shared abstract action.

### Extension Contract

Custom actions provide deterministic parameters through `Action.getIdentityParameters(state, abstractIdentity)`. With `true`, the method supplies behavior distinctions while abstracting interchangeable input; with `false`, it supplies full execution parameters.

The default action implementation supplies empty abstract parameters and uses `toParametersString()` for concrete parameters. Custom actions with behavior variants or presentation-oriented parameter strings override this default. For example, a selection action includes its option in abstract parameters, whereas a typing action includes its text only in concrete parameters.

Position implementations provide stable parameters through `Position.getIdentityParameters(state, abstractIdentity)`. The state allows widget-relative positions to resolve their target and read the appropriate target ID. Identification reads position parameters without executing the action, performing hit testing, or updating cached coordinates.

Identity fields have explicit boundaries, and action hashing uses UTF-8 so identical non-ASCII identity data is encoded consistently across platforms. Stability applies to the same identity contract and inputs.

## Acceptance Scenarios

Verification links identify the JUnit test classes that directly check each scenario. Representative actions and input values are included below; the test classes contain the detailed fixtures and assertions.

### Action IDs Stay Stable When Other Actions Are Added or Reordered

Verification: [`ActionIdentityTest.java`](../../core/test/org/testar/core/action/ActionIdentityTest.java)

Given the current state and a widget have assigned IDs\
And the action set contains only `PasteText("same")` targeting that widget\
And TESTAR has assigned abstract and concrete IDs to that paste action\
When TESTAR identifies a new set containing `Type("another")` followed by a new `PasteText("same")`\
And both actions target the same widget in the unchanged state\
Then the new paste action has the same abstract and concrete IDs as the original paste action

When TESTAR identifies the same two actions in reverse order\
Then the paste action's abstract and concrete IDs still match those of the original paste action

### Different Typed Values Share an Abstract Action

Verification: [`ActionIdentityTest.java`](../../core/test/org/testar/core/action/ActionIdentityTest.java), [`BuildAndroidActions.java`](../../android/test/org/testar/android/actions/BuildAndroidActions.java)

Given the current state and a widget have assigned IDs\
And two typing actions target that same widget, one entering `"first"` and the other `"second"`\
When TESTAR identifies both actions\
Then the actions have the same abstract ID because they perform the same typing operation\
And they have different concrete IDs because they enter different text

The typing comparison is verified for core `Type`, compound text replacement, and `AndroidActionType`.

### Different Pasted Values Share an Abstract Action

Verification: [`ActionIdentityTest.java`](../../core/test/org/testar/core/action/ActionIdentityTest.java)

Given the current state and a widget have assigned IDs\
And `PasteText("first")` and `PasteText("second")` target that same widget\
When TESTAR identifies both paste actions\
Then the paste actions have the same abstract ID\
And they have different concrete IDs because they paste different text

### Remote Typing Retains the Full Concrete Input

Verification: [`WebdriverActionIdentityTest.java`](../../webdriver/test/org/testar/webdriver/action/WebdriverActionIdentityTest.java)

Given the current state and a widget have assigned IDs\
And two WebDriver remote typing actions target that widget\
And their text values are `"same-long-prefix-first"` and `"same-long-prefix-second"`\
When TESTAR identifies both actions\
Then their abstract IDs are equal\
And their concrete IDs differ even though their text shares a long prefix

When the same two text values are used in remote scroll-and-type actions targeting that widget\
Then those scroll-and-type actions also share an abstract ID and have different concrete IDs\
And their abstract ID differs from the remote typing actions because scrolling adds behavior

### Updating Effective Input Changes the Concrete ID

Verification: [`ActionIdentityTest.java`](../../core/test/org/testar/core/action/ActionIdentityTest.java)

Given the current state has assigned IDs\
And `Type("initial")` and `PasteText("initial")` have no attached origin widget\
And TESTAR has assigned abstract and concrete IDs to each action\
When the effective input of each action is changed to `"updated"` through `Tags.InputText`\
And TESTAR identifies those actions again in the unchanged state\
Then each action keeps its own original abstract ID\
And each action receives a concrete ID different from its own original concrete ID

### Selecting Different Options Produces Different Action IDs

Verification: [`WebdriverActionIdentityTest.java`](../../webdriver/test/org/testar/webdriver/action/WebdriverActionIdentityTest.java)

Given the current state and the `"cars"` selection widget have assigned IDs\
And two `WdSelectListAction` actions target that widget using the `ID` lookup method\
And one selects `"saab"` while the other selects `"Saab"`\
When TESTAR identifies both selection actions\
Then their abstract IDs differ because option values are case-sensitive\
And their concrete IDs also differ

When a new action selects `"saab"` using the same widget and lookup method\
Then its abstract and concrete IDs match those of the original `"saab"` selection action

### Click and Long-Click Remain Distinct on the Same Widget

Verification: [`ActionIdentityTest.java`](../../core/test/org/testar/core/action/ActionIdentityTest.java), [`BuildAndroidActions.java`](../../android/test/org/testar/android/actions/BuildAndroidActions.java)

Given the current state and a widget have assigned IDs\
And a click action and a long-click action target that same widget\
And both actions have the same role\
When TESTAR identifies both actions\
Then their abstract IDs differ because clicking and long-clicking are different behaviors\
And their concrete IDs also differ

The core test uses compound mouse-down/up actions with relative durations `[0, 1]` and `[1, 1]`. The Android test compares `AndroidActionClick` and `AndroidActionLongClick`, which share the `LeftClickAt` role.

### Different Drag Destinations Produce Different Action IDs

Verification: [`ActionIdentityTest.java`](../../core/test/org/testar/core/action/ActionIdentityTest.java)

Given the current state, an origin widget, and two destination widgets have assigned IDs\
And the destination widgets have different abstract and concrete IDs\
And two drag actions start from the same origin widget and have the same role\
And one drag ends at the first destination while the other ends at the second\
When TESTAR identifies both drag actions\
Then their abstract IDs differ\
And their concrete IDs differ because the destination is part of the action identity

### Moving a Widget Preserves Its Relative Action IDs

Verification: [`ActionIdentityTest.java`](../../core/test/org/testar/core/action/ActionIdentityTest.java)

Given the current state and a widget have assigned IDs\
And the widget is at screen position `(0, 0)` with size `100 x 40`\
And TESTAR has identified a mouse-move action targeting its center at relative offset `(0.5, 0.5)`\
When the widget moves to screen position `(100, 200)` without changing its size\
And the action's cached screen coordinates are refreshed\
And TESTAR identifies the action again with the same state IDs, widget IDs, and relative offset\
Then its abstract and concrete IDs match those assigned before the widget moved

### Environment Action Identification

Verification: [`ActionIdentityTest.java`](../../core/test/org/testar/core/action/ActionIdentityTest.java), [`DefaultActionIdentifierServiceTest.java`](../../engine/test/org/testar/engine/service/DefaultActionIdentifierServiceTest.java)

Given the current state has assigned IDs\
And an `ActivateSystem` action has no attached origin widget\
And TESTAR has assigned its abstract and concrete IDs through action-set identification\
When TESTAR identifies that same action through environment-action identification in the unchanged state\
Then its abstract and concrete IDs match those assigned through action-set identification

The engine test also includes widget-targeted `Type("first")` / `Type("second")` actions in the set, with widget IDs present and `Tags.Path` absent.

### An Attached Widget Must Have IDs Before Its Action Is Identified

Verification: [`ActionIdentityTest.java`](../../core/test/org/testar/core/action/ActionIdentityTest.java)

Given the current state has assigned IDs\
And `Type("value")` targets an attached widget with neither an abstract nor a concrete ID\
When TESTAR tries to identify that typing action\
Then identification fails with `NoSuchTagException` because the attached widget has not been identified

### State-Model Actions Keep Distinct Concrete IDs and a Shared Abstract Action

Verification: [`ConcreteActionFactoryTest.java`](../../statemodel/test/org/testar/statemodel/ConcreteActionFactoryTest.java)

Given the current state and a widget have assigned IDs\
And `Type("first")` and `Type("second")` target that same widget\
And TESTAR has identified both actions, assigning a shared abstract ID and different concrete IDs\
And an abstract model action represents that shared abstract ID\
When the state-model factory creates a concrete model action for each typing action using that same abstract model action\
Then each concrete model action retains the concrete ID assigned to its corresponding typing action\
And the two concrete model actions have different IDs\
And both reference the same abstract model action

## Primary Implementation

- [CodingManager.java](../../core/src/org/testar/core/CodingManager.java) assigns action IDs; [ActionIdentity.java](../../core/src/org/testar/core/action/ActionIdentity.java) builds canonical identity data.
- [Action.java](../../core/src/org/testar/core/action/Action.java) and [Position.java](../../core/src/org/testar/core/alayer/Position.java) define the parameter extension contracts.
- [DefaultActionIdentifierService.java](../../engine/src/org/testar/engine/service/DefaultActionIdentifierService.java) integrates identification into the engine.
