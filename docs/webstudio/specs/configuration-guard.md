# Configuration Guard

This cross-cutting capability defines dirty-state detection, save enablement, and guarded navigation for configuration editors.

## Functional Requirements

### WS-FUNC-CONFIG-GUARD-001 - Configuration Unsaved-Change Protection

Traceability: [`WS-FUNC-CONFIG-GUARD-001`](../SPEC_TRACE.md#ws-func-config-guard-001---configuration-unsaved-change-protection)

Configuration editors expose one guard contract for dirty state:

- settings, composition, and policies editors compare their current content with the persisted baseline
- Java composition and policy editors also track uncompiled source changes
- save actions are enabled only when the corresponding editor is dirty
- leaving a dirty editor, changing workspace, changing role, or navigating to another applicable view opens the unsaved-change dialog
- switching between a properties-file editor and its Java editor opens the guard when the current editor is dirty
- closing a dirty Java source editor opens the guard
- switching between visual settings groups does not open the guard

The dialog actions are consistent:

- `Save` or `Save and Compile` persists the current changes and continues the pending navigation only after success
- `Discard` restores the persisted baseline and continues the pending navigation
- `Cancel` keeps the current editor and aborts the pending navigation

Compilation failures keep the user in the relevant Java editor, show diagnostics, and abort the pending navigation.

The settings, composition, and policies specifications define the editor-specific save and compilation behavior. This specification defines the shared guard contract only.

## UX Requirements

### WS-UX-CONFIG-GUARD-001 - Configuration Guard Dialogs and Save Buttons

Traceability: [`WS-UX-CONFIG-GUARD-001`](../SPEC_TRACE.md#ws-ux-config-guard-001---configuration-guard-dialogs-and-save-buttons)

The `Test Configuration` area routes to separate `Test Settings`, `Composition Flow`, and `Policies` views, with `Test Settings` as the default. Save buttons remain visible for layout stability, are disabled when their editor matches the persisted baseline, and become enabled using the same dirty-state calculation used by the guard.

Guard dialogs clearly identify the pending action and provide `Save`, `Discard`, and `Cancel` actions with stable placement. A modal or compile operation cannot be dismissed in a way that bypasses an in-progress save or compile operation.
