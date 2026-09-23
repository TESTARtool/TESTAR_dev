# Test Oracles

This specification defines the workspace-scoped Test Oracles view, Extended Oracle enablement, Java oracle files, DSL oracle files, and Rascal diagnostics.

## Functional Requirements

### WS-FUNC-TEST-ORACLES-001 - Workspace-Scoped Test Oracles

Traceability: [`WS-FUNC-TEST-ORACLES-001`](../SPEC_TRACE.md#ws-func-test-oracles-001---workspace-scoped-test-oracles)

The `Test Oracles` view is available in Basic and Advanced roles.

Its left-panel workflow is:

- `Active Oracles`
- `GUI Regex Oracles`
- `Windows Process Oracles`
- `WebDriver Console Oracles`
- `Log Regex Oracles`
- `Enable Extended Oracles`
- `Java Oracle Files`
- `DSL Oracle Files`

`Active Oracles` is the default panel, read-only, and summarizes active or configured oracle mechanisms.

Extended Oracle panels expose one workspace oracle inventory containing Java sources under `settings/<workspace>/oracles/java` and DSL source files under `settings/<workspace>/oracles/dsl`.

The active Java oracle class names are stored in `ExtendedOracles`. Enablement checkboxes derive their state from that setting and use the normal `Save Settings` flow.

`Enable Extended Oracles` enables or disables existing Java oracles. `Java Oracle Files` creates, edits, deletes, saves, and compiles workspace Java files. `DSL Oracle Files` creates, edits, deletes, validates, and generates Java from `.testar` files.

Workspace Java sources compile into `settings/<workspace>/oracles/compiled`. Java oracle class names must be unique inside the selected workspace.

Bundled workspace Java oracles are precompiled when the TESTAR distribution is built. Runtime workspaces use lazy freshness checks: modified sources compile together when their inventory is first required, and unchanged inventories are reused until Java sources, DSL files, or enablement settings change.

Java source edits use `Save and Compile`. DSL source edits use `Save and Generate Java-DSL`, which saves the source, validates it, and generates Java oracle classes into the workspace Java oracle directory.

Generated Java files use the DSL file name with the `.java` extension. Generated Java oracle classes follow the same compile, enable, disable, and execution flow as manually written workspace Java oracles. New manual and generated Java oracle classes are enabled by default by adding their class names to `ExtendedOracles`.

Deleting Java or DSL oracle files requires a confirmation dialog naming the selected file.

### WS-FUNC-ORACLE-DSL-EDITOR-001 - Rascal DSL Metadata and Diagnostics

Traceability: [`WS-FUNC-ORACLE-DSL-EDITOR-001`](../SPEC_TRACE.md#ws-func-oracle-dsl-editor-001---rascal-dsl-metadata-and-diagnostics)

The WebStudio DSL editor uses metadata generated from Rascal oracle module sources for widget types, fields, root statement keywords, condition operators, connector keywords, and locale suggestions.

The editor provides autocomplete, syntax coloring, and lightweight local diagnostics. Rascal backend validation remains authoritative and returns structured diagnostics containing severity, message, line, column, end line, and end column when source locations are available.

Backend diagnostics are shown in the feedback panel and may be shown as Monaco markers when source ranges are available.

### WS-FUNC-ORACLE-JAVA-ENABLEMENT-001 - Workspace Java Oracles and ExtendedOracles Enablement

Traceability: [`WS-FUNC-ORACLE-JAVA-ENABLEMENT-001`](../SPEC_TRACE.md#ws-func-oracle-java-enablement-001---workspace-java-oracles-and-extendedoracles-enablement)

Individual oracle checks are implemented as Java or DSL-generated `Oracle` classes and enabled through `ExtendedOracles`.

Deleting a Java oracle file removes its declared oracle class names from `ExtendedOracles`, so the deleted classes are no longer enabled or reported as active after the inventory is refreshed.

Oracle composition customization belongs to Composition Flow and changes verdict aggregation behavior rather than individual oracle definitions.

### WS-SCENARIO-ORACLE-JAVA-DELETE-001 - Deleting a Java Oracle Clears Enablement

Verification: `TestOracleServiceTest.java`, `testOraclesModel.test.js`

Given a workspace Java oracle class is compiled, enabled in `ExtendedOracles`, and visible in `Active Oracles`
When the user confirms deletion of the Java oracle file
Then the Java oracle file is deleted
And the deleted class is removed from the `ExtendedOracles` setting
And the deleted class is absent from `Enable Extended Oracles`
And the deleted class is absent from `Active Oracles`
And reloading the workspace keeps the deleted class absent from all three locations

## UX Requirements

### WS-UX-TEST-ORACLES-001 - Test Oracles Configuration View

Traceability: [`WS-UX-TEST-ORACLES-001`](../SPEC_TRACE.md#ws-ux-test-oracles-001---test-oracles-configuration-view)

The `Active Oracles` panel is read-only and the default panel. Extended Oracle panels use a unified inventory for the selected workspace, grouped into workspace Java oracles and DSL source files.

`Enable Extended Oracles` shows Java oracle class names as checkbox rows and provides `Enable All` and `Disable All` actions for visible classes. `Java Oracle Files` provides a file browser, editor, and `Save and Compile`. `DSL Oracle Files` provides a file browser and Monaco editor.

Search in all settings remains available in Test Settings and is hidden from Test Oracles panels.

Compilation, validation, and generation feedback use fixed-size panels near the related editor or inventory. Delete actions use confirmation dialogs. Layouts remain stable while feedback is displayed.

### WS-UX-ORACLE-DSL-EDITOR-001 - Monaco DSL Editor Assistance

Traceability: [`WS-UX-ORACLE-DSL-EDITOR-001`](../SPEC_TRACE.md#ws-ux-oracle-dsl-editor-001---monaco-dsl-editor-assistance)

The DSL source editor uses Monaco with syntax coloring, autocomplete, lightweight local diagnostics from Rascal metadata, and backend diagnostic markers with hover messages when ranges are available.

The editor exposes one `Save and Generate Java-DSL` action. The feedback panel shows success, failure, generated Java paths, and source diagnostics without resizing the page.

### WS-UX-ORACLE-JAVA-ENABLEMENT-001 - Workspace Java Oracle Management

Traceability: [`WS-UX-ORACLE-JAVA-ENABLEMENT-001`](../SPEC_TRACE.md#ws-ux-oracle-java-enablement-001---workspace-java-oracle-management)

Workspace Java oracles show whether they are active and editable. Generated Java files appear in the Java oracle inventory after DSL generation. Enable and disable controls operate on class names and update `ExtendedOracles`.

After a confirmed Java oracle deletion, the active summary and enablement list are refreshed together and do not retain the deleted class.
