# State Model

State Model covers workspace-scoped state inference and the lifecycle of the OrientDB-backed analysis service.

## Functional Requirements

### WS-FUNC-STATE-MODEL-001 - State Model Analysis Lifecycle

Traceability: [`WS-FUNC-STATE-MODEL-001`](../SPEC_TRACE.md#ws-func-state-model-001---state-model-analysis-lifecycle)

Scriptless Generate and CLI modes can generate state models when state model settings are enabled for the selected workspace. The execution mode prepares the configured OrientDB storage, including download or bootstrap when required.

Spy mode uses the no-op state model manager and only inspects the current SUT state.

The State Model settings form exposes `StateModelExportStaticGraph`, disabled by default. Enabling it produces a portable model snapshot at `<run>/state-model/index.html` after Generate or CLI model-session shutdown. The current run's output workspace contains a `state-models.html` index. The snapshot opens directly in a browser using local assets and embedded graph/screenshot data. Its contents reflect the persisted model, which can include earlier runs. The [state-model module specification](../../statemodel/state-model.md) defines the export lifecycle and acceptance scenarios.

While a completed Generate run finalizes its state model, WebStudio keeps the process running through datastore shutdown and optional static export, with visible progress for these phases. Completion or failure restarts the normal idle-cleanup grace period. The explicit Stop control remains available.

The analysis graph and static viewer offer an `Export Model` dialog for Abstract, Hybrid, Concrete, or Sequence Traces JSON and optional trees/screenshots. Semantic text, identifiers, names, roles, values, and interaction status are selected by default; a collapsed `Advanced Properties` panel exposes the complete available property inventory, including optional coordinates and colors. Mandatory identities and relationships remain intact; trace exports preserve recorded sequence occurrences, order, and timing. Preparation displays phases, available counts, and elapsed time while browser packaging runs in the background. Offline choices reflect captured snapshot content. The [JSON export specification](../../statemodel/json-export.md) defines the formats and verification.

The State Model form places `StateModelExportStaticGraph` and `StateModelExportStaticGraphIncludeWidgetTrees` at the bottom, in that order. Tree capture defaults to `false` and its checkbox is enabled only while static export is enabled. Snapshot creation and JSON downloads share data preparation while retaining independent choices and lifecycles. The [snapshot preparation contract](../../statemodel/state-model.md#snapshot-content-and-preparation-cost) defines availability metadata and console progress.

`View State Model` opens the external analysis URL after server-side preparation. Startup can take time while OrientDB initializes or recovers a datastore, so WebStudio keeps a visible startup status until the service is running or has failed.

State model analysis resolves from the selected workspace and the shared distribution runtime home:

- runtime home: `testar/target/install/testar/bin`
- workspace datastore paths are resolved against that runtime home
- analysis assets are served from `output/graphs` under the runtime home, separately from workspace run outputs
- Generate and CLI can contribute to the same datastore when they use the same workspace and datastore settings

When analysis is running, WebStudio provides an action to open the analysis URL and an action to stop the analysis server owned by the current WebStudio process.

If no workspace is selected, the selected workspace is unavailable, no generated model exists, or startup fails, WebStudio shows a friendly `Unable To Open State Model` dialog and logs diagnostic details server-side.

## UX Requirements

### WS-UX-STATE-MODEL-001 - State Model Dialog and Actions

Traceability: [`WS-UX-STATE-MODEL-001`](../SPEC_TRACE.md#ws-ux-state-model-001---state-model-dialog-and-actions)

State model errors are shown as user-facing messages rather than raw server or browser console errors. Startup and datastore recovery appear in a visible status dialog. While analysis is running, the dialog exposes actions to open the analysis page and stop the analysis server.

Static snapshots with captured widget trees open read-only hierarchy and attribute inspection in a separate browser tab from a selected concrete state. The inspector identifies the state and snapshot, with independently scrollable hierarchy and attribute panels. It uses local snapshot data independently of the graph tab and analysis service. The [offline inspection contract](../../statemodel/state-model.md#captured-widget-trees-are-inspectable-offline) defines its verification.
