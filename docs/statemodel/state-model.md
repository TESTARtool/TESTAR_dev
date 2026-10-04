# State Model Inference and Analysis

[State model documentation](./README.md) | [Distribution architecture](../architecture_distribution_workspace.md)

## Default Inference and Live Analysis

`StateModelInference` is disabled by default. When enabled, Generate and CLI sessions record abstract states/actions, their concrete occurrences, and sequence transitions. `AbstractStateAttributes` controls abstraction; application name/version and the selected attributes identify the model. Compatible runs can contribute to the same persisted model.

The datastore is configured through `DataStore`, `DataStoreType`, `DataStoreDirectory` or `DataStoreServer`, database name, and credentials. Supported OrientDB connection types are `plocal` and `remote`. Relative local paths resolve against the TESTAR runtime home. `DataStoreMode` selects instant, delayed, hybrid, or no persistence; `StateModelStoreWidgets` controls storage of complete concrete widget trees.

Spy sessions use the dummy manager. Generate finishes recording its sequences before ending the model session. CLI finalizes its sequence and reports before closing its platform session. Session shutdown flushes the configured persistence manager and releases its database connection.

Live analysis opens the persisted datastore through a separately managed analysis service, normally at `http://localhost:8090/models`. It provides graph and sequence analysis against the datastore. WebStudio exposes opening and stopping this service. Its packaged runtime assets are under `output/graphs`, separate from workspace-scoped run outputs.

## Optional Static Export

Set this workspace setting to enable automatic export:

```properties
StateModelInference = true
StateModelExportStaticGraph = true
```

`StateModelExportStaticGraph` defaults to `false` and appears in WebStudio's State Model settings form. It requires a persisted OrientDB model (`DataStoreMode` other than `none`) and a run output directory.

After Generate or CLI model-session shutdown successfully flushes and closes persistence, export temporarily opens an analysis connection, reads the abstract/concrete/sequence layers and their relationships, and closes that connection in a `finally` block. Repeated shutdown calls perform this work once. Normal orderly cleanup, including an interrupted run that reaches cleanup, can produce a snapshot; forcibly killing the process cannot guarantee export.

The snapshot contains the persisted model at export time, including earlier runs with the same model identifier. Run metadata identifies which execution produced the snapshot and lists its HTML reports. It does not reinterpret report filenames as a single goal or sequence verdict. Datastore credentials remain runtime configuration.

```text
output/<workspace>/
  state-models.html
  <run>/
    reports/
    state-model/
      index.html
      widget-tree.html
      run.json
      run.js
      css/
      js/
      model/
        elements.json
        elements.js
        images.js
        widget-trees.json
        widget-trees.js
        <state-or-action-id>.png
```

Open `<run>/state-model/index.html` directly in a browser. The workspace `state-models.html` index links to completed snapshots. All required viewer libraries, graph elements, and available state/action screenshots are local. Embedded JavaScript data allows direct `file://` viewing without a server, network requests, or an active OrientDB connection. The viewer supports layouts, labels, graph fitting, selection details, and screenshot zooming. Missing screenshots are shown explicitly as unavailable.

Export reads its connection configuration from the session settings and validates that the destination run belongs to the configured workspace output directory. Temporary files are removed after failure, and existing snapshots are preserved. Export failures are reported in the console and leave the completed execution's existing reports and persistence result intact.

The live graph and static viewer also provide [configurable Abstract, Hybrid, Concrete, and Sequence Traces JSON exports](./json-export.md), including available screenshots. Static snapshots can optionally capture persisted concrete widget trees for offline downloads and inspection.

In a snapshot with captured trees, selecting a concrete state exposes `Inspect Widget Tree`, which opens `widget-tree.html` in a separate browser tab. The page identifies the selected concrete state, snapshot run, and model. It shows the recorded hierarchy and the selected widget's read-only attributes and IDs side by side, with independent scrolling; narrow screens stack the panels. Child branches render on expansion in numeric path order. SUT text is rendered as text rather than executable markup. Trees omitted during capture and requested but unavailable trees have distinct explanations. The page loads embedded snapshot data independently and remains usable after the graph tab closes, on reload, and with OrientDB and the analysis server stopped.

WebStudio waits throughout model finalization, starting before persistence flush and datastore shutdown and continuing through optional static export. Console progress explains when the datastore is closing and when export is running. Finalization completion, including failure cleanup, releases this protection and restarts the normal idle grace period. The explicit Stop control remains available.

## Snapshot Content and Preparation Cost

`StateModelExportStaticGraphIncludeWidgetTrees` defaults to `false`. The State Model form places `StateModelExportStaticGraph` and `StateModelExportStaticGraphIncludeWidgetTrees` at the bottom of the panel, in that order. The tree-capture checkbox is enabled only while static export is enabled. Both choices use the normal settings save flow.

```properties
StateModelExportStaticGraph = true
StateModelExportStaticGraphIncludeWidgetTrees = false
```

This configuration creates a lightweight portable graph. The new setting controls snapshot capture, while `StateModelStoreWidgets` controls recording during inference and the JSON export dialog controls each subsequent download. Enabling capture includes only trees available in the persisted model; it does not enable recording retroactively.

When tree capture is disabled, preparation skips tree queries and tree serialization. The graph and available images remain captured. `run.json` records `widgetTreesCaptured` and `screenshotsCaptured`, allowing the offline export dialog to explain which options are usable. The viewer retains a small empty tree-loader script, while `widget-trees.json` is created only when tree capture is enabled.

Static preparation must retain the abstract, concrete, and sequence graph layers and their relationships independently of a later JSON format selection. Abstract, Hybrid, Concrete, and Sequence Traces are download choices, rather than settings that restrict the portable graph. Shared preparation options and separate output lifecycles are defined in the [JSON export contract](./json-export.md#shared-preparation-and-separate-outputs).

When tree capture is enabled, preparation retrieves persisted trees for the selected accumulated model using one database session. Console progress reports completed/total trees, and static export completion reports preparation duration. Connections and query results are released after success or failure. Existing snapshots remain immutable. Captured trees are currently accumulated in memory before packaging.

## Acceptance Scenarios

### Static Tree Capture Is an Independent Setting

Verification: [`StateModelExportSettingsTest.java`](../../config/test/org/testar/config/settings/StateModelExportSettingsTest.java), [`WorkspaceSettingsCatalogStateModelTest.java`](../../webstudio/test/org/testar/webstudio/workspace/WorkspaceSettingsCatalogStateModelTest.java), [`settingsFieldState.test.js`](../../webstudio/frontend/test/views/settings/settingsFieldState.test.js).

Given a workspace uses default settings\
When its State Model form is shown\
Then the two static-export controls appear at the bottom of the panel\
And tree capture is unchecked and disabled while static export is unchecked\
When the user enables static export, enables tree capture, and saves settings\
Then `StateModelExportStaticGraphIncludeWidgetTrees = true` is persisted\
And `StateModelStoreWidgets` remains unchanged

### Lightweight Snapshot Skips Tree Capture

Verification: [`StaticGraphExporterTest.java`](../../statemodel/test/org/testar/statemodel/analysis/export/StaticGraphExporterTest.java), [`ModelExportSnapshotTest.java`](../../statemodel/test/org/testar/statemodel/analysis/export/ModelExportSnapshotTest.java), [`StaticGraphExporterIntegrationTest.java`](../../statemodel/test/org/testar/statemodel/analysis/export/StaticGraphExporterIntegrationTest.java).

Given `StateModelExportStaticGraph = true`\
And `StateModelExportStaticGraphIncludeWidgetTrees = false`\
When snapshot preparation runs\
Then widget-tree retrieval and serialization are skipped\
And all graph layers and captured screenshots remain available\
And snapshot metadata indicates that trees were intentionally excluded

### Static Export Is Optional

Verification: [`StateModelExportSettingsTest.java`](../../config/test/org/testar/config/settings/StateModelExportSettingsTest.java), [`WorkspaceSettingsCatalogStateModelTest.java`](../../webstudio/test/org/testar/webstudio/workspace/WorkspaceSettingsCatalogStateModelTest.java)

Given a workspace uses default settings\
When the State Model settings are loaded\
Then `StateModelExportStaticGraph` is disabled\
When the user enables the setting in the State Model form and saves settings\
Then the workspace configuration contains `StateModelExportStaticGraph = true`

### Persistence Completes Before Export

Verification: [`ModelManagerLifecycleTest.java`](../../statemodel/test/org/testar/statemodel/ModelManagerLifecycleTest.java)

Given the model manager has an export completion action\
When the session ends and persistence shutdown succeeds\
Then export runs after persistence shutdown\
And a repeated session-end notification performs neither operation again

Given persistence shutdown fails\
When the session ends\
Then the caller receives the persistence error\
And export does not run against unflushed data\
When shutdown is retried and persistence succeeds\
Then export runs after that successful shutdown

### Portable Workspace Snapshot

Verification: [`StaticGraphExporterTest.java`](../../statemodel/test/org/testar/statemodel/analysis/export/StaticGraphExporterTest.java), [`StaticGraphExporterIntegrationTest.java`](../../statemodel/test/org/testar/statemodel/analysis/export/StaticGraphExporterIntegrationTest.java), [`static-graph-viewer.test.cjs`](../../statemodel/test/js/static-graph-viewer.test.cjs)

Given a persisted model contains abstract and concrete states and their relationships\
And the current run is inside `output/webdriver_generic`\
When static export finishes\
Then `<run>/state-model/index.html` and the workspace snapshot index exist\
And the graph, viewer assets, and available screenshots are packaged locally\
And the viewer initializes from embedded graph data without fetching it\
And metadata identifies the run and accumulated model scope\
And the temporary analysis connection is closed so the datastore can be reopened

### Missing Screenshots Remain Understandable

Verification: [`StaticGraphExporterTest.java`](../../statemodel/test/org/testar/statemodel/analysis/export/StaticGraphExporterTest.java), [`static-graph-viewer.test.cjs`](../../statemodel/test/js/static-graph-viewer.test.cjs)

Given the exported graph includes a concrete state or action without a screenshot\
When the user selects that graph element\
Then its attributes remain visible\
And the viewer shows `State screenshot unavailable` or `Action screenshot unavailable` instead of a broken image

### Captured Widget Trees Are Inspectable Offline

Verification: [`static-graph-viewer.test.cjs`](../../statemodel/test/js/static-graph-viewer.test.cjs), [`widget-tree-inspector.test.cjs`](../../statemodel/test/js/widget-tree-inspector.test.cjs), [`StaticGraphExporterTest.java`](../../statemodel/test/org/testar/statemodel/analysis/export/StaticGraphExporterTest.java), [`StaticGraphExporterIntegrationTest.java`](../../statemodel/test/org/testar/statemodel/analysis/export/StaticGraphExporterIntegrationTest.java).

Given a static snapshot includes a concrete state's captured widget tree\
And the analysis server and OrientDB are stopped\
When the user selects that state and opens `Inspect Widget Tree`\
Then a separate browser tab shows that state's captured hierarchy without a network request\
And it identifies the concrete state, snapshot run, and model\
And the original graph selection remains unchanged\
When the user expands a branch and selects a child\
Then children follow numeric sibling path order\
And the selected widget's captured attributes and IDs are shown read-only beside the hierarchy\
And inspecting the tree leaves the embedded snapshot unchanged

Given the user has opened a captured tree in its own browser tab\
When the user closes the original graph tab and reloads the inspector\
Then the same state's hierarchy and attributes remain available from local snapshot files

Given tree capture was disabled or a state has no available tree\
When the user selects that concrete state\
Then the viewer explains whether the tree was not captured or is unavailable

Given the standalone inspector URL has a missing, unknown, or non-concrete state ID\
When the page opens\
Then it explains how to select a concrete state in the graph

### Export Failure Preserves Execution Artifacts

Verification: [`StaticGraphExporterTest.java`](../../statemodel/test/org/testar/statemodel/analysis/export/StaticGraphExporterTest.java)

Given analysis fails or does not produce the requested graph file\
When static export is attempted\
Then the temporary connection is closed\
And temporary snapshot files are removed\
And existing snapshots and execution reports remain unchanged

### WebStudio Waits for Datastore Shutdown and Export

Verification: [`ScriptlessStateModelFinalizationTest.java`](../../webstudio/test/org/testar/webstudio/execution/ScriptlessStateModelFinalizationTest.java), [`ScriptlessExecutionAdapterTest.java`](../../webstudio/test/org/testar/webstudio/execution/ScriptlessExecutionAdapterTest.java), [`ModelManagerLifecycleTest.java`](../../statemodel/test/org/testar/statemodel/ModelManagerLifecycleTest.java)

Given Generate has completed its configured sequences\
And model finalization is flushing persistence and closing OrientDB before static export starts\
When console output remains quiet beyond the normal completed-run idle grace period\
Then WebStudio keeps the process running and shows finalization progress\
When datastore shutdown succeeds and static export starts\
Then WebStudio shows export progress and continues protecting the process\
And export completion alone keeps protection active until model finalization ends\
When model finalization ends\
Then the usual idle grace period restarts\
And idle cleanup can stop a process that remains stuck after that grace period

Given static export is disabled\
When model finalization takes longer than the completed-run idle grace period\
Then WebStudio waits for datastore shutdown before resuming idle cleanup

Given persistence shutdown or export fails\
When model finalization exits through failure cleanup\
Then its idle-cleanup protection is released\
And the failure remains available to the caller

Given model finalization is still running\
When the user explicitly stops Generate\
Then WebStudio stops the runtime process

### Unavailable Models and Runs Skip Export

Verification: [`StaticGraphExporterTest.java`](../../statemodel/test/org/testar/statemodel/analysis/export/StaticGraphExporterTest.java)

Given inference is disabled, persistence is `none`, or the current run is unavailable or outside the configured workspace output\
When static export is requested\
Then analysis does not open and a console message explains the skipped or rejected export

## Implementation and Verification

- [StateModelManagerFactory.java](../../statemodel/src/org/testar/statemodel/StateModelManagerFactory.java) attaches optional export to the model lifecycle; [ModelManager.java](../../statemodel/src/org/testar/statemodel/ModelManager.java) orders shutdown and completion.
- [StaticGraphExporter.java](../../statemodel/src/org/testar/statemodel/analysis/export/StaticGraphExporter.java) packages the snapshot using [AnalysisManager.java](../../statemodel/src/org/testar/statemodel/analysis/AnalysisManager.java).
- [PlatformOrchestrator.java](../../plugin/src/org/testar/plugin/PlatformOrchestrator.java) supplies the current run directory for both Generate and CLI.
- [graphs-static](../../statemodel/resources/graphs-static) contains the portable viewer; Gradle packages these resources in the state-model JAR.
