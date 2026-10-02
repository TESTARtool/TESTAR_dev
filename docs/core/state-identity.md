# State Identity

[Core documentation](./README.md) | [Architecture](../ARCHITECTURE.md#state-identity)

## Purpose

TESTAR assigns abstract and concrete IDs to captured states and widgets before action identification, exploration, reporting, and state-model recording consume them.

Widget IDs use the `WA` and `WC` prefixes; state IDs use `SA` and `SC`. Role-based identity variants use the same encoding rules with their respective attribute selections.

## Behavioral Contract

### Attribute Selection and Encoding

- Abstract identity uses the attributes selected in `AbstractStateAttributes`.
- Concrete identity uses the registered state-management attributes, as supplied by the platform's tag mappings.
- Attribute names, declared types, and values have explicit field boundaries. Missing values, empty strings, and literal `"null"` are distinct.
- Attribute selections are sorted deterministically. Selecting the same attributes in another order produces the same IDs.
- Selecting a control pattern also includes its associated child attributes in deterministic order.
- Identity hashing uses UTF-8 for widgets, states, and model identifiers.

### Root and Tree Structure

State identity includes the root's selected attributes and the complete ordered widget tree. Parent/child structure is encoded explicitly using preorder traversal and child counts. Identification follows `child()` and `childCount()`, independently of the state's iterator implementation.

Sibling order remains meaningful: reversing distinguishable siblings changes state identity. Changing parent/child relationships can change state IDs even when the individual widget attribute values and widget IDs remain unchanged.

Root attributes follow the same selections as descendant attributes. For WebDriver, the captured root URL contributes through `WebWidgetHref`: it affects concrete identity and affects abstract identity when that attribute is selected. Android root activity follows the equivalent rule for `AndroidWidgetActivity`. Context changes leave widget IDs unchanged when their own selected attributes remain unchanged.

### Configuration Ownership

Runtime identification services own immutable configurations created from their session settings. Creating or initializing another session leaves an existing service's configuration unchanged. The state-model factory also obtains its abstraction configuration from the supplied settings.

Direct `CodingManager` callers use explicitly initialized defaults. Initialization replaces the previous selections atomically. An empty or entirely unrecognized abstract selection resets to `WidgetControlType`. Array setters and getters use defensive copies, including the default selection.

### Model Identity and Customization

The model identifier includes the application name, application version, and selected abstract attribute names and types. Application fields have explicit boundaries.

Action IDs also change when their input state/widget IDs change.

Workspaces customize identification through `stateIdentifierServiceClass`. A wrapper receives the session's configured delegate and can normalize selected data before delegating or implement a different identification policy. A custom policy should use a distinct application version when it changes the interpretation of persisted models.

## Acceptance Scenarios

### Attribute Boundaries Preserve Different Values

Verification: [`StateIdentityTest.java`](../../core/test/org/testar/core/state/StateIdentityTest.java)

Given abstract and concrete widget identity both use `Path` and `Title`\
And one widget has path `"ab"` and title `"c"`\
And another widget has path `"a"` and title `"bc"`\
When TESTAR identifies both widgets\
Then their abstract IDs differ\
And their concrete IDs differ even though concatenating the values without boundaries would produce `"abc"`

### Missing Values Differ From Text Values

Verification: [`StateIdentityTest.java`](../../core/test/org/testar/core/state/StateIdentityTest.java)

Given three otherwise equivalent widgets have a missing title, the literal title `"null"`, and an empty title\
And title is selected for abstract and concrete identity\
When TESTAR identifies the widgets\
Then all three abstract IDs are distinct\
And all three concrete IDs are distinct

### Initialization and Configuration Copies Are Independent

Verification: [`StateIdentityTest.java`](../../core/test/org/testar/core/state/StateIdentityTest.java)

Given direct identification was initialized with `WebWidgetId`\
When it is initialized again with an empty or entirely unrecognized selection\
Then its abstract selection becomes `WidgetControlType`\
And concrete identification uses the registered state-management attributes

Given a caller supplies attribute arrays or obtains them from a getter\
When the caller changes those arrays\
Then the stored identification configuration remains unchanged

### Sessions Keep Their Own Identification Configuration

Verification: [`DefaultStateIdentifierServiceTest.java`](../../engine/test/org/testar/engine/service/DefaultStateIdentifierServiceTest.java), [`StateModelManagerFactoryTest.java`](../../statemodel/test/org/testar/statemodel/StateModelManagerFactoryTest.java)

Given one service identifies states by title and another identifies them by role\
When a state's title changes from `"Submit"` to `"Cancel"` while its role stays unchanged\
And the services identify the state in alternating order\
Then the title-based abstract ID changes\
And the role-based abstract ID stays unchanged

Given a model is created with `WebWidgetId` in its supplied settings\
When global coding defaults are changed to `AndroidWidgetResourceId`\
And another model is created with the same supplied settings and application name/version\
Then both model identifiers are equal\
And selecting `WidgetControlType` in the supplied settings produces a different model identifier

### Root Context Follows the Selected Abstraction

Verification: [`TestWebdriverStateManagementTag.java`](../../webdriver/test/org/testar/webdriver/tag/TestWebdriverStateManagementTag.java), [`TestAndroidStateManagementTag.java`](../../android/test/org/testar/android/tag/TestAndroidStateManagementTag.java)

Given a WebDriver state has unchanged widgets and root URL `https://example.org/first`\
When only the root URL changes to `https://example.org/second`\
Then the concrete state ID changes\
And the abstract state ID changes when `WebWidgetHref` is selected\
And the abstract state ID stays unchanged when the selection is `WidgetControlType,WebWidgetId`\
And the widgets retain their own IDs for each unchanged selection

Given an Android state includes `AndroidWidgetActivity` in its abstract selection\
When only the root activity changes from `LoginActivity` to `AccountActivity`\
Then its abstract and concrete state IDs change\
And the unchanged child widget retains its abstract ID

### Hierarchy and Sibling Order Are Explicit

Verification: [`StateIdentityTest.java`](../../core/test/org/testar/core/state/StateIdentityTest.java)

Given one state contains sibling widgets titled `"first"` and `"second"`\
And another state contains widget `"first"` with widget `"second"` as its child\
And both states select the same attributes and have equivalent root attributes\
When TESTAR identifies both states\
Then their abstract and concrete state IDs differ\
And corresponding widgets retain equal IDs because their selected attribute values are equal

Given two states have equivalent roots and distinguishable sibling widgets\
When those siblings appear in opposite orders\
Then their abstract and concrete state IDs differ

### Tree Identification Is Independent of Iterator Behavior

Verification: [`StateIdentityTest.java`](../../core/test/org/testar/core/state/StateIdentityTest.java)

Given two states have equivalent roots and ordered child trees\
And one state's iterator returns no widgets\
When TESTAR identifies both states through their child relationships\
Then their abstract and concrete state IDs are equal\
And equivalent child widgets receive equal abstract IDs

### Unicode Identity Uses UTF-8

Verification: [`StateIdentityTest.java`](../../core/test/org/testar/core/state/StateIdentityTest.java)

Given a widget's selected title is `"café 中文"`\
When TESTAR identifies the widget\
Then its concrete ID matches the hash calculated from the encoded identity using UTF-8

Given a state's selected root title and the application name contain the same Unicode text\
When TESTAR identifies the state and calculates its model identifier\
Then both match hashes calculated from their encoded identities using UTF-8

## Primary Implementation

- [StateIdentity.java](../../core/src/org/testar/core/state/StateIdentity.java) owns immutable attribute selection and root/tree identification.
- [IdentityEncoding.java](../../core/src/org/testar/core/util/IdentityEncoding.java) supplies field boundaries and UTF-8 hashing.
- [CodingManager.java](../../core/src/org/testar/core/CodingManager.java) exposes direct identification and initialization entry points.
- [DefaultStateIdentifierService.java](../../engine/src/org/testar/engine/service/DefaultStateIdentifierService.java) applies session-owned identification in the shared engine pipeline.
- [StateModelManagerFactory.java](../../statemodel/src/org/testar/statemodel/StateModelManagerFactory.java) creates the model using its supplied abstraction settings.
