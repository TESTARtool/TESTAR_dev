# State Model Inference and Analysis

[State model documentation](./README.md) | [Distribution architecture](../architecture_distribution_workspace.md)

## Default Inference and Live Analysis

`StateModelInference` is disabled by default. When enabled, Generate and CLI sessions record abstract states/actions, their concrete occurrences, and sequence transitions. `AbstractStateAttributes` controls abstraction; application name/version and the selected attributes identify the model. Compatible runs can contribute to the same persisted model.

The datastore is configured through `DataStore`, `DataStoreType`, `DataStoreDirectory` or `DataStoreServer`, database name, and credentials. Supported OrientDB connection types are `plocal` and `remote`. Relative local paths resolve against the TESTAR runtime home. `DataStoreMode` selects instant, delayed, hybrid, or no persistence; `StateModelStoreWidgets` controls storage of complete concrete widget trees.

Spy sessions use the dummy manager. Generate finishes recording its sequences before ending the model session. CLI finalizes its sequence and reports before closing its platform session. Session shutdown flushes the configured persistence manager and releases its database connection.

Live analysis opens the persisted datastore through a separately managed analysis service, normally at `http://localhost:8090/models`. It provides graph and sequence analysis against the datastore. WebStudio exposes opening and stopping this service. Its packaged runtime assets are under `.runtime/graphs`; the exact analysis-service path is separate from generated run outputs.

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
      run.json
      run.js
      css/
      js/
      model/
        elements.json
        elements.js
        images.js
        <state-or-action-id>.png
```

Open `<run>/state-model/index.html` directly in a browser. The workspace `state-models.html` index links to completed snapshots. All required viewer libraries, graph elements, and available state/action screenshots are local. Embedded JavaScript data allows direct `file://` viewing without a server, network requests, or an active OrientDB connection. The viewer supports layouts, labels, graph fitting, selection details, and screenshot zooming. Missing screenshots are shown explicitly as unavailable.

Export reads its connection configuration from the session settings and validates that the destination run belongs to the configured workspace output directory. Temporary files are removed after failure, and existing snapshots are preserved. Export failures are reported in the console and leave the completed execution's existing reports and persistence result intact.

WebStudio waits while static export is active rather than treating a quiet export as a stuck completed Generate run. Export completion, including failure cleanup, releases this protection; normal idle-process cleanup can then resume.

## Acceptance Scenarios

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

### Export Failure Preserves Execution Artifacts

Verification: [`StaticGraphExporterTest.java`](../../statemodel/test/org/testar/statemodel/analysis/export/StaticGraphExporterTest.java)

Given analysis fails or does not produce the requested graph file\
When static export is attempted\
Then the temporary connection is closed\
And temporary snapshot files are removed\
And existing snapshots and execution reports remain unchanged

### WebStudio Waits for Export

Verification: [`ScriptlessExecutionAdapterTest.java`](../../webstudio/test/org/testar/webstudio/execution/ScriptlessExecutionAdapterTest.java)

Given Generate has completed its configured sequences\
And static graph export has started\
When console output remains quiet beyond the normal completed-run idle grace period\
Then WebStudio keeps the process running and shows export progress\
When export signals completion\
Then the usual idle cleanup can stop a process that remains stuck

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

Run Java verification with `./gradlew :statemodel:test :config:test :webstudio:test`. Run the dependency-free viewer tests with `./gradlew :statemodel:staticGraphViewerTest` (requires Node.js). A manual browser smoke check can use a Generate or CLI run with static export enabled, followed by opening the exported `index.html` after runtime shutdown.
