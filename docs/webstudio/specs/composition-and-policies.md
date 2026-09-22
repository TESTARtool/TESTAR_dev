# Composition Flow and Policies

This specification defines the composition profile and Java policy editors used by the selected workspace.

## Functional Requirements

### WS-FUNC-COMPOSITION-FLOW-001 - Composition File and Java Flow

Traceability: [`WS-FUNC-COMPOSITION-FLOW-001`](../SPEC_TRACE.md#ws-func-composition-flow-001---composition-file-and-java-flow)

`Composition Flow` provides two related editors:

- `Edit composition.properties file`, which edits the composition file directly
- `Edit Java Composition Flow`, which renders services and capabilities as connected flow nodes

The composition file editor is one logical editing area for unsaved-change behavior. The Java composition flow is one logical editing area for unsaved and uncompiled-change behavior.

The Java source editor supports `Refresh Java` and `Save and Compile`. Plain source save without compilation is not the primary action.

When compilation fails, WebStudio reports the diagnostics, keeps the user in the composition flow, and aborts pending navigation. If compilation was triggered from a guard dialog, that dialog closes so the user can inspect the source and diagnostics.

### WS-FUNC-POLICIES-001 - Policies File and Java Policies

Traceability: [`WS-FUNC-POLICIES-001`](../SPEC_TRACE.md#ws-func-policies-001---policies-file-and-java-policies)

`Policies` provides two related editors:

- `Edit policies.properties file`, which edits the policies file directly
- `Edit Java Policies`, which lists active and available Java policies and edits the selected source

The policies file editor is one logical editing area for unsaved-change behavior. The Java policies view is one logical editing area for unsaved and uncompiled-change behavior.

The Java policy editor supports `Save and Compile`. Plain source save without compilation is not the primary action.

When compilation fails, WebStudio reports the diagnostics, keeps the user in the Java policies view, and aborts pending navigation. If compilation was triggered from a guard dialog, that dialog closes so the user can inspect the source and diagnostics.

## UX Requirements

### WS-UX-COMPOSITION-FLOW-001 - Composition Flow Editor View

Traceability: [`WS-UX-COMPOSITION-FLOW-001`](../SPEC_TRACE.md#ws-ux-composition-flow-001---composition-flow-editor-view)

The Java composition flow is a visual map of active services and capabilities.

- flow nodes keep stable positions
- clicking a node opens a focused editor modal
- Java source feedback appears inside the modal
- the source editor action area remains stable
- `Refresh Java` refreshes or reopens the source linked to the selected node
- `Save and Compile` saves and validates the Java source

### WS-UX-POLICIES-001 - Java Policies Editor View

Traceability: [`WS-UX-POLICIES-001`](../SPEC_TRACE.md#ws-ux-policies-001---java-policies-editor-view)

The Java policies view compares active and available policies.

- active and available lists keep stable panel positions
- policy lists scroll internally when needed
- selecting a policy opens a focused source editor modal
- closing a policy source modal returns to the policy overview
- Java source feedback appears inside the modal
