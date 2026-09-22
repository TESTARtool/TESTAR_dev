# WebStudio Foundations

This specification contains behavior shared by multiple WebStudio capabilities: roles, navigation, source editors, and common interaction principles.

## Functional Requirements

### WS-FUNC-TOP-NAV-ROLES-001 - Role-Based Navigation

Traceability: [`WS-FUNC-TOP-NAV-ROLES-001`](../SPEC_TRACE.md#ws-func-top-nav-roles-001---role-based-navigation)

WebStudio must support role-based user experiences.

The selected role defines which workflows and configuration options are primary in the frontend. It must not change the underlying workspace data model. The selected role is a frontend preference persisted locally in the browser.

Changing role must use the same unsaved-change guard as normal page navigation when the current view has unsaved changes.

Workspace selection and workspace management are shared capabilities available from the top navigation for all roles.

Advanced users can access the full WebStudio configuration and inspection surface, including raw and visual settings, composition, policies, Test Oracles, Test Goals, Spy, Generate, CLI, reports, state model analysis, and debug files.

Basic users configure and execute tests through workflow-oriented panels. Basic role includes the visual settings groups `SUT Connection`, `SUT Execution`, `Filters`, `Agent CLI`, `WebDriver`, and `Android Appium`, together with Test Oracles, Test Goals, Spy, Generate, CLI, the future MCP entry, and Test Reports.

Basic role must preserve the same workspace files and settings used by Advanced role. Changes made in Basic role must remain visible and editable in Advanced role.

Intermediate role behavior remains unspecified until its scope is explicitly defined.

### WS-FUNC-WORKSPACE-SOURCE-EDITOR-001 - Workspace Document and Source Editor API

Traceability: [`WS-FUNC-WORKSPACE-SOURCE-EDITOR-001`](../SPEC_TRACE.md#ws-func-workspace-source-editor-001---workspace-document-and-source-editor-api)

The Test Configuration area exposes separate settings, composition, and policies pages.

`Test Settings` exposes `Edit Settings`.

`Composition Flow` exposes the `composition.properties` editor and the Java composition flow editor.

`Policies` exposes the `policies.properties` editor and the Java policies editor.

Java source editors are used by composition and policies pages for their related Java files. Test Oracles exposes its own oracle workflow.

## Navigation

WebStudio top navigation is grouped by user intent:

- `Workspace <selected workspace>`
- `Role <selected role>`
- `Test Configuration`
- `Test Oracles`
- `Test Goals`
- `Spy Mode`
- `Run Modes`
- `View Results`

`Test Configuration` contains `Test Settings`, `Composition Flow`, and `Policies`.

`Test Oracles` and `Test Goals` are direct workflows. `Spy Mode` remains a direct top-level action.

`Run Modes` contains `Generate Mode`, `CLI Mode`, and the disabled-until-implemented `MCP Mode` entry.

`View Results` contains `Test Reports`, `State Model`, and `Debug Files`.

## UX Requirements

### WS-UX-TOP-NAV-ROLES-001 - Top Navigation and Role Selector

Traceability: [`WS-UX-TOP-NAV-ROLES-001`](../SPEC_TRACE.md#ws-ux-top-nav-roles-001---top-navigation-and-role-selector)

Major panels, menus, headers, and action areas remain in predictable positions. Role changes preserve the selected workspace and use the same unsaved-change modal as page navigation when the current view is dirty.

The role selector belongs in the top navigation near the workspace selector. Role changes keep top-level navigation groups in stable positions. Role-specific visibility is applied inside dropdown menus when possible.

Basic and Advanced roles use the same top-level navigation positions and shared labels and icons. Basic role may hide or simplify advanced dropdown entries, but it does not create a different workspace format.

### WS-UX-SOURCE-EDITOR-001 - Source Editor State and Document Selection

Traceability: [`WS-UX-SOURCE-EDITOR-001`](../SPEC_TRACE.md#ws-ux-source-editor-001---source-editor-state-and-document-selection)

Workspace source editors provide consistent source selection, save labels, and diagnostics handling across settings, composition, policies, and oracle source editing.

- editor headers identify the selected document or source file
- save actions match the selected document type
- switching editors clears stale source selection and diagnostics
- Java diagnostics remain associated with the selected source editor
- source editor panels scroll internally when content exceeds the visible area

### Stable Layout and Feedback

The primary canvas should fit a full browser window at approximately `1920x1080`. Primary workflow screens avoid page-level scrolling; dynamic lists, consoles, editors, properties, and diagnostics scroll internally.

Dynamic content renders idle, loading, success, warning, and error states inside reserved areas without moving unrelated panels or actions.

The user should always understand the selected workspace, active page, configured SUT target, loaded state, and whether an action is idle, waiting, running, failed, or complete.

Each concept has one primary editing location. Runtime pages may display `SUTConnectorValue`, but configuration remains its editing location.

### Top-Level Layout

The top navigation remains visible while the current page header and primary panels remain accessible. Static panels do not grow unexpectedly when lists or feedback messages change.

### Source and Modal Interaction

Modals overlay the current page without resizing it. Focused source editing, major blocking information, explicit destructive confirmation, and unsaved-work protection use modal dialogs where appropriate.

Guard dialogs explain the work at risk, the primary action, the effect of `Discard`, and the effect of `Cancel`. Compile-related guard dialogs use `Save and Compile` as the primary action.

### Viewport and Scroll

The main navigation, current page header, and primary panels remain accessible in a full browser window. Primary workflow screens avoid page-level scrolling; settings groups, source editors, diagnostics, generated outputs, verdict lists, logs, consoles, widget properties, and report previews scroll internally.

Normal labels and buttons avoid horizontal scrolling. Overflowing text uses shorter labels, wrapping, or a detail tooltip where appropriate.

### Visual Semantics

Status colors are meaningful and consistent, but color is not the only signal. Labels, badges, or text also communicate the state:

- green: successful or healthy state
- red: failed, invalid, or blocking issue
- amber/orange: warning or needs attention
- neutral: idle, unavailable, or informational

### Accessibility and Interaction

- buttons use button elements
- dialogs use dialog semantics
- clickable non-button regions have appropriate roles and keyboard handling
- disabled actions explain unavailable state where practical
- important text is visible against its background
- critical information does not rely only on hover

## Acceptance Scenarios

### WS-SCENARIO-TOP-NAV-001 - Guarded Role Selector Does Not Show Uncommitted Role

Verification: `webStudioRoles.test.js`, `committedSelectModel.test.js`, `configurationGuard.test.js`

Given the user is in the `Advanced` role
And the user has unsaved settings changes
When the user selects the `Basic` role
Then the unsaved changes guard is shown
And the role selector still shows `Advanced` while the role change is pending

When the user clicks `Cancel`
Then the application remains in the `Advanced` role
And the role selector shows `Advanced`

When the user accepts the guarded role change
Then the application changes to the `Basic` role
And the role selector shows `Basic`
