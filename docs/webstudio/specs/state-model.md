# State Model

State Model covers workspace-scoped state inference and the lifecycle of the OrientDB-backed analysis service.

## Functional Requirements

### WS-FUNC-STATE-MODEL-001 - State Model Analysis Lifecycle

Traceability: [`WS-FUNC-STATE-MODEL-001`](../SPEC_TRACE.md#ws-func-state-model-001---state-model-analysis-lifecycle)

Scriptless Generate and CLI modes can generate state models when state model settings are enabled for the selected workspace. The execution mode prepares the configured OrientDB storage, including download or bootstrap when required.

Spy mode uses the no-op state model manager and only inspects the current SUT state.

`View State Model` opens the external analysis URL after server-side preparation. Startup can take time while OrientDB initializes or recovers a datastore, so WebStudio keeps a visible startup status until the service is running or has failed.

State model analysis resolves from the selected workspace and the shared distribution runtime home:

- runtime home: `testar/target/install/testar/bin`
- workspace datastore paths are resolved against that runtime home
- analysis assets are served from `output/graphs` under the runtime home
- Generate and CLI can contribute to the same datastore when they use the same workspace and datastore settings

When analysis is running, WebStudio provides an action to open the analysis URL and an action to stop the analysis server owned by the current WebStudio process.

If no workspace is selected, the selected workspace is unavailable, no generated model exists, or startup fails, WebStudio shows a friendly `Unable To Open State Model` dialog and logs diagnostic details server-side.

## UX Requirements

### WS-UX-STATE-MODEL-001 - State Model Dialog and Actions

Traceability: [`WS-UX-STATE-MODEL-001`](../SPEC_TRACE.md#ws-ux-state-model-001---state-model-dialog-and-actions)

State model errors are shown as user-facing messages rather than raw server or browser console errors. Startup and datastore recovery appear in a visible status dialog. While analysis is running, the dialog exposes actions to open the analysis page and stop the analysis server.
