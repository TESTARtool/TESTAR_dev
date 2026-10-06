# State-Model JSON Exports

[State model documentation](./README.md) | [Inference and snapshots](./state-model.md) | [Action identity](../core/action-identity.md)

## Configurable Export Contract

Abstract, Hybrid, Concrete, and Sequence Traces exports share the configurable dialog, property selection, selective artifact preparation, and ZIP packaging described below.

### Export Dialog

Both live analysis and the static viewer must provide one `Export Model` action. It opens a dialog with a format selector, `Include widget trees`, and `Include screenshots`. A collapsed `Advanced Properties` panel contains searchable property selections for states, actions, widgets, transitions, and sequences where relevant. Property groups offer `Select all`, `Clear selection`, and `Use semantic defaults`. The dialog ends with `Export` and `Cancel`.

The initial selection is Hybrid, widget trees excluded, screenshots included, and available semantic/technical properties selected: text, titles, names, IDs, roles, values, locators, URLs, descriptions, interaction status, and verdict information. Coordinates, dimensions, colors, fonts, raw HTML, runtime handles, and custom properties remain available for explicit advanced selection. Essential identities and relationships are preserved independently of these defaults. Choices apply to the current export and are independent of inference settings.

The property inventory must be derived from available model data rather than a hardcoded list of SUT properties. A selected property is included only on records where it exists. Clearing a group's selection means exporting no optional properties for that group. Widget property controls are active when widget trees are included.

Essential identity and relationship fields remain mandatory; the dialog explains that these fields are always preserved, separately from optional property checkboxes. Changing property selections preserves the identities, transitions, initial-state references, and widget parent/child relationships required by the selected format. Export filtering operates on copied export data rather than stored model records or inference abstraction.

### Selectable Formats

| Format | Exported meaning |
| --- | --- |
| Abstract | Abstract states, actions, and all abstract transitions, with deterministic representative concrete artifacts when available. |
| Hybrid | Abstract entities and transitions together with every associated persisted concrete state and concrete transition record. |
| Concrete | Concrete states, actions, and source/action/target transitions; available abstract lineage is retained as references. |
| Sequence Traces | Each recorded sequence and its ordered state/action steps, preserving repeated executions as separate occurrences. |

All formats must include format/version/model metadata, relevant initial-state or sequence-start references, and warnings. Concrete must distinguish transitions even when one action or widget appears in several transitions. Sequence Traces must retain sequence and occurrence identifiers and recorded timing/order information. An initial state can have no preceding action. Unresolved occurrence associations must be explicit rather than borrowing an action from another step with the same abstract ID.

All four formats are available in both viewers. Sequence property controls appear when Sequence Traces is selected; transition property controls also cover recorded sequence steps.

### Shared Export Options and Data Availability

Live and offline export must apply the same options and transformation rules. Equivalent captured model data and options must produce equivalent JSON and screenshot references. `Metadata` must record the applied options, distinguishing intentionally excluded artifacts from requested but unavailable artifacts.

When trees are excluded, state artifacts omit `WidgetTree`; widget-tree retrieval and transformation are skipped. When screenshots are excluded, screenshot retrieval, decoding, and ZIP entries are skipped, and artifact screenshot fields are omitted. These intentional exclusions produce no missing-artifact warnings. Requested but unavailable trees remain empty with a warning; requested but unavailable screenshots remain `null` with a warning.

Opening the dialog retrieves property names and availability metadata, without sending graph values, trees, or screenshot bytes to the browser. The backend derives these names from the model graph and a widget property-name query. Confirmed live exports retrieve optional artifacts according to the chosen options. Abstract retrieves deterministic representatives; Hybrid and Concrete retrieve all concrete records. Sequence Traces retrieves only state and action artifacts unambiguously referenced by recorded occurrences.

Offline options are limited by the snapshot's captured content. Snapshot metadata must identify whether trees and images were captured and which property groups are available. A tree option unavailable in the snapshot must be disabled with an explanation. Offline export uses captured data and requires no OrientDB connection. Deliberately omitted snapshot content is reported as an availability limitation, rather than reconstructed or silently represented as complete data.

### Shared Preparation and Separate Outputs

Static snapshots and live JSON exports must reuse backend model-data preparation with explicit options for optional artifacts and the required concrete-state scope. Graph retrieval, widget-tree retrieval, image access, availability metadata, and resource cleanup belong to this shared preparation contract. The browser transformation and ZIP packaging remain shared between live and offline JSON downloads.

The two outputs retain separate lifecycles: a static snapshot is a portable viewer prepared after model-session shutdown; a live JSON package is prepared on demand for the selected format and options. Static preparation captures all graph layers, while a live JSON request retrieves the artifacts required by its selected format. Format selection must preserve the full graph needed by the static viewer.

The [static tree-capture setting](./state-model.md#snapshot-content-and-preparation-cost) applies only to snapshot preparation. Live JSON export uses its own `Include widget trees` choice, independently of that workspace setting. Offline JSON export uses the same download options within the content already captured by its snapshot.

### Preparation Cost and Feedback

Static snapshot preparation and JSON download are separate operations. The snapshot preparation contract is defined in [Inference and snapshots](./state-model.md#snapshot-content-and-preparation-cost). Property filtering can reduce downloaded JSON size; reducing preparation cost also requires skipping optional retrieval and serialization before those data are loaded.

Export shows the current phase and elapsed time throughout graph preparation, tree/image retrieval, transfer, transformation, and ZIP creation. Live preparation streams actual completed/total tree and screenshot counts before returning the snapshot. Transfer displays received bytes; phases without a known total remain indeterminate rather than showing an invented percentage. ZIP preparation reports completed/total files.

Snapshot parsing, JSON transformation, image decoding, and ZIP creation run in a background browser worker in both viewers. The dialog remains responsive, collapses advanced options during preparation, prevents duplicate submissions, and restores controls after success or failure. Workers, object URLs, stream readers, and elapsed-time timers are released after use. A truncated stream or failed worker produces an error and permits retry without downloading partial data. Large packages report ZIP limits or browser-memory failures instead of presenting a successful partial download.

## Export Behavior

The live analysis graph and portable static viewer provide `Export Model`. The dialog downloads a ZIP containing `model_abstract.json`, `model_hybrid.json`, `model_concrete.json`, or `model_traces.json` and any available screenshots selected for that export.

Live export reads the persisted graph independently of visible layers or hidden elements, then retrieves the selected concrete artifacts. Offline export uses the embedded graph and the trees/images captured in its snapshot. It works directly through `file://` after the analysis server and OrientDB have stopped. Both use the same transformation and ZIP packaging implementation.

Exports represent the accumulated model identified by `ModelIdentifier`. Static exports reflect the time the snapshot was created; live exports reflect the datastore when requested. Changing layouts or labels affects presentation rather than exported identities. `StateModelStoreWidgets` determines which widget-tree data is persisted and available to export.

While preparing an export, the dialog controls are disabled and closing through Cancel or Escape is blocked. Completion identifies the package and reports unavailable requested artifacts. Errors remain visible and restore controls for retry. Missing requested screenshots have a `null` reference and missing requested trees are empty, both with JSON warnings. Intentionally excluded artifacts are omitted without warnings.

## Formats and Identities

Every format contains `Metadata`, relevant `InitialStates`, and `Warnings`. Abstract and Hybrid also contain:

- `Metadata`: `SchemaVersion`, `Format`, `ModelIdentifier`, `ModelScope`, and applied `Options`; offline exports also identify `SnapshotRunID`.
- `InitialStates`: the IDs of every initial abstract state.
- `AbstractStates`: abstract identities and properties.
- `AbstractActions`: unique abstract action identities.
- `AbstractTransitions`: source/action/target relationships, properties, and `Visited` status.
- `Warnings`: unavailable artifact descriptions.

An abstract export attaches one deterministic representative concrete state to each abstract state, and one representative concrete action to each abstract action. Representatives are selected by sorted graph-element ID. Each state artifact includes its properties, widget tree, and available screenshot. An abstract action with multiple target states retains every abstract transition.

A hybrid export attaches all persisted concrete states under each abstract state, including their individual widget trees and screenshots. Each abstract action includes its concrete transition records; each abstract transition also references the corresponding concrete source/action/target tuples. Different actions on the same widget, and the same concrete action leading to different targets, remain distinct.

`AbstractStateID`/`ConcreteStateID` identify states. `AbstractActionID`/`ConcreteActionID` identify actions. `AbstractWidgetID`/`ConcreteWidgetID` identify the action's origin widget. Graph-element IDs identify stored records and select screenshots, separately from these logical IDs. In particular, the concrete graph edge's `AbstractID` is an origin-widget ID, not an abstract action ID. Action association uses the source/target relationships and exact membership of `concreteActionIds`.

Updating an existing abstract transition must persist every accumulated concrete-action reference. This includes observation transitions recording state changes between agent commands, even when several concrete states share the same abstract state. Those associations must remain available after the datastore is closed and reopened.

Unvisited abstract actions have `Visited = false`, `TargetAbstractStateID = null`, and no executed concrete artifact. A missing or ambiguous action association is reported as an export error instead of guessing an identity.

Concrete transition records describe the persisted graph, not an ordered history of every execution. Repeated execution can update one record; sequence chronology remains in the sequence layer.

### Concrete

Concrete exports contain:

- `ConcreteStates`: each persisted state with `GraphNodeID`, `ConcreteStateID`, available `AbstractStateID`, `IsInitial`, selected properties, and requested artifacts.
- `ConcreteActions`: one entry per persisted action/transition record, including `TransitionID`, `ConcreteActionID`, `ConcreteActionUID`, available `AbstractActionID`, origin-widget IDs, source/target state IDs, selected action properties, and the requested screenshot.
- `ConcreteTransitions`: each `TransitionID` and its source/action/target IDs, available abstract action lineage, and selected transition properties.

`TransitionID` is the concrete graph-edge ID. Two transitions for `AC1` remain separate even when they share a source or origin widget. `InitialStates` identifies concrete states reached by recorded sequence-start occurrences or explicitly marked initial concrete records; an abstract initial state does not make every concrete variant initial. Missing abstract action lineage is `null` with a warning, preserving the concrete data.

### Sequence Traces

Sequence Traces exports contain the referenced concrete state/action/transition inventories above, plus `Sequences` and `UnassignedSteps`. Artifacts are included once in those inventories and referenced by occurrences.

Each sequence contains `SequenceID`, `GraphNodeID`, recorded `StartTime`, selected sequence properties, `StartOccurrenceIDs`, ordered `StateOccurrences`, and ordered `Steps`. A start-only sequence has one state occurrence and no steps; a sequence with no recorded observations has empty occurrence and step lists.

State occurrences retain `OccurrenceID` (the recorded `nodeId`, or the graph ID when absent), `GraphNodeID`, numeric `Order` from `nodeNr`, recorded `Timestamp`, concrete/abstract state references, `StateGraphNodeID`, `AssociationStatus`, and selected sequence properties. Steps retain their recorded `stepId` or graph ID as `OccurrenceID`, `GraphEdgeID`, source/target graph and occurrence references, target occurrence `Order`, recorded `Timestamp`, `ConcreteActionID`, `ConcreteActionUID`, resolved `ConcreteTransitionID`, `AssociationStatus`, and selected transition properties. IDs, chronology, and start references remain present when optional properties are cleared.

State association uses the occurrence's `Accessed` edge and checks its recorded concrete-state ID. When that edge is absent, an exact, unique recorded state-ID match is accepted. Action association requires exact concrete action ID and resolved source/target states; when a concrete action UID is recorded it must also match. A missing or ambiguous association retains the occurrence with a `null` resolved reference, explicit status, and warning. Steps without an assignable sequence remain in `UnassignedSteps`; cross-sequence edges fail export instead of inventing a trace.

Ordering uses recorded numeric node order, with graph IDs providing deterministic tie-breaking. Missing order remains `null` with a warning rather than an invented index. Timestamps retain the datastore's recorded values. Repeated executions retain separate occurrence IDs and can reference the same concrete transition. Their artifact references describe the persisted graph record, not an execution-specific screenshot or tree that was never recorded.

## Package and Snapshot Data

```text
model_hybrid.zip
  model_hybrid.json
  screenshots/
    states/<graph-node-id>.png
    actions/<graph-edge-id>.png
```

Screenshot references are relative to the extracted package root. Separate state/action folders and graph-record filenames prevent collisions when several actions target one widget or one logical action has several transitions. ZIP entries and JSON use UTF-8. Packages use uncompressed ZIP entries and are prepared in browser memory; large models and screenshots increase memory use and download size. ZIP size/file-count limits produce explicit errors.

Static snapshots package `model/widget-trees.json` when tree capture is enabled; the embedded loader supplies captured trees without network requests. Tree retrieval uses one database session and releases it afterwards. Live data requests accept a model identifier, format, and artifact choices, while output paths and datastore configuration remain backend-owned. JSON packages include selected SUT properties and screenshots; review these artifacts before sharing them.

## Acceptance Scenarios

These scenarios verify export behavior, rather than diagnosing application non-determinism. The example IDs are synthetic test labels: `SA`/`SC` identify abstract/concrete states, `AA`/`AC` identify actions, `WA`/`WC` identify widgets, and `ca1`/`ca2` identify stored transition records. `AC10` is a different action from `AC1`, not its tenth execution.

A state occurrence is one observation of a state during a sequence. A step connects two occurrences by executing an action. Its `Order` is a recorded sequence position, independent of the state's identity. Repeated steps can reference the same stored transition. Missing-reference scenarios deliberately use incomplete test data to verify safe export behavior.

### Semantic Defaults and Advanced Selection

Verification: [`model-json-export.test.cjs`](../../statemodel/test/js/model-json-export.test.cjs), [`model-export-controls.test.cjs`](../../statemodel/test/js/model-export-controls.test.cjs).

Given available model properties include text, IDs, roles, coordinates, and colors\
When the user opens `Export Model`\
Then `Advanced Properties` is collapsed\
And available text, IDs, names, roles, values, and interaction status are selected by default\
And coordinates and colors are available but unchecked\
When the user selects visual properties and then chooses `Use semantic defaults`\
Then the group's semantic selection is restored\
When the user explicitly selects visual properties again and exports\
Then those properties are included without changing essential identities

### Export Progress Reflects Actual Preparation

Verification: [`ModelExportServletTest.java`](../../statemodel/test/org/testar/statemodel/analysis/ModelExportServletTest.java), [`StaticGraphExporterIntegrationTest.java`](../../statemodel/test/org/testar/statemodel/analysis/export/StaticGraphExporterIntegrationTest.java), [`model-export-controls.test.cjs`](../../statemodel/test/js/model-export-controls.test.cjs).

Given a live export includes widget trees and screenshots\
When preparation reports the number of completed and total trees\
Then the dialog displays the reported count and elapsed time before the snapshot arrives\
When transfer and packaging run\
Then the dialog shows received bytes and packaging phases\
And work without a known total is shown as indeterminate\
When preparation fails or the stream ends before its snapshot arrives\
Then no partial package is downloaded\
And a visible error allows retry\
And the temporary database connection is released

### Browser Packaging Runs in the Background

Verification: [`model-export-runtime.test.cjs`](../../statemodel/test/js/model-export-runtime.test.cjs), [`model-export-controls.test.cjs`](../../statemodel/test/js/model-export-controls.test.cjs), [`model-export-browser.test.cjs`](../../statemodel/test/js/model-export-browser.test.cjs).

Given a synthetic captured-model fixture contains 500 concrete states and their widget trees\
When the user exports the model\
Then transformation and ZIP creation run in a background worker\
And the main thread remains available to service UI events\
And progress reports the processed states and packaged files\
When packaging succeeds or fails\
Then the worker and temporary resources are released

### Configured Properties Preserve Model Structure

Verification: [`model-json-export.test.cjs`](../../statemodel/test/js/model-json-export.test.cjs).

Given a model contains state titles, action input text, and widget names\
When the user selects state `WebTitle`, clears optional action properties, and selects widget `WebName`\
And exports Hybrid JSON with widget trees included\
Then only the selected optional properties appear in their corresponding property groups\
And essential state/action/widget IDs and transition references remain present\
And widget hierarchy remains intact\
And the stored model remains unchanged\
And metadata records the applied selections

### Excluding Optional Artifacts Skips Their Preparation

Verification: [`ModelExportSnapshotTest.java`](../../statemodel/test/org/testar/statemodel/analysis/export/ModelExportSnapshotTest.java), [`StaticGraphExporterIntegrationTest.java`](../../statemodel/test/org/testar/statemodel/analysis/export/StaticGraphExporterIntegrationTest.java), [`model-json-export.test.cjs`](../../statemodel/test/js/model-json-export.test.cjs).

Given the user disables widget trees and screenshots\
When live export prepares the selected model\
Then no widget-tree queries or screenshot-byte retrieval run for that export\
And JSON omits widget-tree and screenshot artifact fields\
And the ZIP contains no screenshot entries\
And intentional exclusions produce no missing-artifact warnings

### Offline Configuration Reflects Snapshot Availability

Verification: [`model-export-controls.test.cjs`](../../statemodel/test/js/model-export-controls.test.cjs), [`model-export-browser.test.cjs`](../../statemodel/test/js/model-export-browser.test.cjs).

Given a static snapshot was prepared without widget trees\
When the user opens `Export Model` offline\
Then the tree option is unavailable and explains that the snapshot contains no captured trees\
And available formats and property selections remain usable\
When the user exports with available content\
Then export completes without requesting the server or OrientDB

### Concrete Export Keeps Distinct Transitions

Verification: [`model-json-export.test.cjs`](../../statemodel/test/js/model-json-export.test.cjs), [`ModelExportSnapshotTest.java`](../../statemodel/test/org/testar/statemodel/analysis/export/ModelExportSnapshotTest.java).

Given concrete action `AC1` targets widget `WC1`\
And transition `ca1` records `SC1 --AC1--> SC1`\
And transition `ca2` records `SC1 --AC1--> SC2`\
And both transitions have available screenshots\
When the user exports Concrete JSON with screenshots included\
Then both source/action/target relationships remain in `ConcreteTransitions`\
And their `TransitionID` values remain `ca1` and `ca2`\
And the concrete action ID `AC1` remains separate from the origin-widget ID `WC1`\
And each transition references its own screenshot

### Concrete Initial States Follow Recorded Sequence Starts

Verification: [`model-json-export.test.cjs`](../../statemodel/test/js/model-json-export.test.cjs).

Given concrete states `SC1` and `SC3` belong to the initial abstract state `SA1`\
And a recorded sequence starts at `SC1` but none starts at `SC3`\
When the user exports Concrete JSON\
Then `InitialStates` includes `SC1`\
And `SC3` remains exported with `IsInitial = false`

### Sequence Export Preserves Repeated Occurrences

Verification: [`model-json-export.test.cjs`](../../statemodel/test/js/model-json-export.test.cjs), [`StaticGraphExporterIntegrationTest.java`](../../statemodel/test/org/testar/statemodel/analysis/export/StaticGraphExporterIntegrationTest.java).

Given sequence `SEQ1` starts at `SC1`\
And it executes action `AC1` twice, returning to `SC1` each time\
And both executions use the stored transition `ca1`\
And `ca1` has an available screenshot\
When the user exports Sequence Traces\
Then `SEQ1` retains three state occurrences, all referring to `SC1`\
And its two executions retain separate step occurrence IDs, `step1` and `step2`\
And both steps reference `ConcreteTransitionID = ca1`\
And their recorded timestamps and the sequence-start occurrence are retained\
And the screenshot for `ca1` is packaged once and referenced by both steps

### Sequence Ordering Uses Recorded Positions

Verification: [`model-json-export.test.cjs`](../../statemodel/test/js/model-json-export.test.cjs).

Given a synthetic fixture assigns recorded order strings `"0"`, `"2"`, and `"10"` to three state occurrences\
And its graph records are supplied in reverse order\
When the user exports Sequence Traces\
Then the occurrences appear in numeric order `0`, `2`, `10`\
And each step retains its target occurrence's recorded order\
And the exporter preserves those positions rather than renumbering them consecutively

The nonconsecutive values are deliberate sorting-test data; normal execution does not require gaps between recorded positions.

### Each Sequence References Its Own Transition

Verification: [`model-json-export.test.cjs`](../../statemodel/test/js/model-json-export.test.cjs).

Given `SEQ1` executes `AC1` from `SC1` back to `SC1` using transition `ca1`\
And `SEQ2` executes the same action ID from `SC1` to `SC2` using transition `ca2`\
When the user exports Sequence Traces\
Then the steps in `SEQ1` reference `ca1`\
And the step in `SEQ2` references `ca2`\
And their source and target occurrence references remain within their own sequences

### Trace Property Selection Preserves Recorded History

Verification: [`model-json-export.test.cjs`](../../statemodel/test/js/model-json-export.test.cjs).

Given a recorded sequence contains state occurrences and action steps with optional properties\
When the user clears optional action, transition, and sequence properties and exports Sequence Traces\
Then those optional property containers are empty\
And occurrence IDs, state/action references, transition references, recorded orders, and timestamps remain present

### Unresolved Trace Associations Remain Visible

Verification: [`model-json-export.test.cjs`](../../statemodel/test/js/model-json-export.test.cjs), [`ModelExportSnapshotTest.java`](../../statemodel/test/org/testar/statemodel/analysis/export/ModelExportSnapshotTest.java).

Given an incomplete fixture contains recorded step `step2` whose `ConcreteActionUID` matches no stored transition\
And another transition has the same action ID and endpoints but a different UID\
When the user exports Sequence Traces\
Then `step2` retains its recorded IDs and has `ConcreteTransitionID = null`\
And `AssociationStatus = unresolved` and a warning identify the missing association\
And the step remains unassociated with the other transition or its screenshot

### Missing State References Preserve the Recorded Occurrence

Verification: [`model-json-export.test.cjs`](../../statemodel/test/js/model-json-export.test.cjs), [`ModelExportSnapshotTest.java`](../../statemodel/test/org/testar/statemodel/analysis/export/ModelExportSnapshotTest.java), [`StaticGraphExporterIntegrationTest.java`](../../statemodel/test/org/testar/statemodel/analysis/export/StaticGraphExporterIntegrationTest.java).

Given an incomplete fixture contains a state occurrence naming a concrete state missing from the model\
When the user exports Sequence Traces\
Then the occurrence retains its recorded `ConcreteStateID`\
And `StateGraphNodeID = null`, `AssociationStatus = unresolved`, and a warning identify the missing state\
And affected steps remain present with unresolved transition references

### Empty and Start-Only Sequences Remain Exportable

Verification: [`model-json-export.test.cjs`](../../statemodel/test/js/model-json-export.test.cjs).

Given one recorded sequence has a starting state observation and no executed steps\
And another sequence has no state observations or steps\
When the user exports Sequence Traces\
Then the first sequence retains its starting occurrence and an empty `Steps` list\
And the second retains empty `StateOccurrences` and `Steps` lists

### Abstract Export Preserves All Abstract Relationships

Verification: [`model-json-export.test.cjs`](../../statemodel/test/js/model-json-export.test.cjs)

Given initial abstract state `SA1` has concrete states `SC1` and `SC2`\
And abstract action `AAclick` connects `SA1` to both `SA1` and `SA2`\
And representative concrete records have captured trees and available screenshots\
When the user exports Abstract JSON\
Then `InitialStates` includes `SA1`\
And `SA1` contains one deterministic representative concrete state with its tree and available screenshot\
And `AAclick` appears once in the action inventory\
And both source/action/target relationships remain in `AbstractTransitions`

### Hybrid Export Preserves Concrete Provenance

Verification: [`model-json-export.test.cjs`](../../statemodel/test/js/model-json-export.test.cjs)

Given abstract state `SA1` contains concrete states `SC1` and `SC2`\
And abstract state `SA2` contains concrete state `SC3`\
And `SC1` and `SC2` have stored widget trees and available screenshots\
And transition `ca1` records `SC1 --AC1--> SC3`\
And transition `ca4` records `SC1 --AC1--> SC2`\
And both transitions have available screenshots\
When the user exports Hybrid JSON with widget trees and screenshots included\
Then the entry for `SA1` includes both `SC1` and `SC2` in its `ConcreteStates` list, each with its own tree and screenshot\
And both concrete source/action/target relationships remain under their corresponding abstract transitions\
And the concrete action instances retain distinct screenshot references for `ca1` and `ca4`

### Repeated Observations Preserve Their Abstract Association

Verification: [`OrientDBManagerIntegrationTest.java`](../../statemodel/test/org/testar/statemodel/persistence/orientdb/OrientDBManagerIntegrationTest.java), [`model-json-export.test.cjs`](../../statemodel/test/js/model-json-export.test.cjs).

Given four concrete states `SC1`, `SC2`, `SC3`, and `SC4` share abstract state `SA1`\
And three observations record `SC1 -> SC2`, `SC2 -> SC3`, and `SC3 -> SC4` under action `AA_OBSERVATION`\
When the model is persisted and its datastore is reopened for export\
Then the abstract self-loop retains all three concrete-action references\
And Hybrid export preserves all three concrete source/action/target relationships\
And Abstract export retains `AA_OBSERVATION` with one deterministic representative concrete observation

### Actions Sharing a Widget Retain Separate Identities

Verification: [`model-json-export.test.cjs`](../../statemodel/test/js/model-json-export.test.cjs).

Given click action `AAclick` and type action `AAtype` share origin-widget identity `WA1`\
And the type action has a concrete instance containing `InputText`\
When the user exports Hybrid JSON\
Then `AbstractActions` contains separate entries for `AAclick` and `AAtype`\
And their concrete instances retain `AbstractWidgetID = WA1` separately from the action IDs\
And the type instance retains its own input text

### Action References Use Complete IDs

Verification: [`model-json-export.test.cjs`](../../statemodel/test/js/model-json-export.test.cjs).

Given a recorded trace step names action `AC1` without a concrete action UID\
And available transitions for `AC1` and `AC10` share the same source and target states\
When the user exports Sequence Traces\
Then the step resolves to the transition for `AC1`\
And `AC10` remains a different action despite containing the text `AC1`

### Export Is Independent of Graph Record Order

Verification: [`model-json-export.test.cjs`](../../statemodel/test/js/model-json-export.test.cjs).

Given two inputs contain the same model and widget-tree records in different array orders\
When each is exported using the same Abstract, Hybrid, or Concrete options\
Then the corresponding JSON content and packaged artifacts are identical\
And the input records remain unchanged

### Offline Export Uses Packaged Data

Verification: [`StaticGraphExporterIntegrationTest.java`](../../statemodel/test/org/testar/statemodel/analysis/export/StaticGraphExporterIntegrationTest.java), [`model-export-browser.test.cjs`](../../statemodel/test/js/model-export-browser.test.cjs), [`model-json-export.test.cjs`](../../statemodel/test/js/model-json-export.test.cjs)

Given a completed static snapshot includes captured widget trees, screenshots, and the shared exporter scripts\
And the analysis server and OrientDB are stopped\
When the user confirms JSON export in the static viewer\
Then it uses embedded graph, tree, and image data without fetching a server\
And the ZIP contains UTF-8 JSON and the referenced available image bytes

### Live Export Uses Complete Backend Data

Verification: [`ModelExportSnapshotTest.java`](../../statemodel/test/org/testar/statemodel/analysis/export/ModelExportSnapshotTest.java), [`ModelExportServletTest.java`](../../statemodel/test/org/testar/statemodel/analysis/ModelExportServletTest.java)

Given live analysis is showing a selected subset of model layers\
When the user confirms Hybrid export for `model-1` with widget trees and screenshots included\
Then the backend reads all model layers and the requested concrete artifacts\
And returns UTF-8 JSON containing the available images\
And browser caching is disabled

### Invalid Live Export Requests Are Rejected Before Data Access

Verification: [`ModelExportSnapshotTest.java`](../../statemodel/test/org/testar/statemodel/analysis/export/ModelExportSnapshotTest.java), [`ModelExportServletTest.java`](../../statemodel/test/org/testar/statemodel/analysis/ModelExportServletTest.java).

Given a live export request contains a missing or unsafe model identifier, an unsupported format, or an invalid artifact option\
When the backend receives the request\
Then it rejects the invalid request before reading the datastore

### Unvisited Actions Remain Explicit

Verification: [`model-json-export.test.cjs`](../../statemodel/test/js/model-json-export.test.cjs)

Given the model contains a derived action that has no recorded execution\
When the user exports Hybrid JSON\
Then the action remains in `AbstractActions` without an invented concrete execution\
And its transition has `Visited = false`, `TargetAbstractStateID = null`, and no concrete instances

### Requested but Missing Artifacts Remain Explicit

Verification: [`model-json-export.test.cjs`](../../statemodel/test/js/model-json-export.test.cjs).

Given recorded states or actions lack requested screenshots, or recorded states lack requested widget trees\
And the user includes those artifacts in the export options\
When JSON export completes\
Then missing screenshots have `null` references\
And missing trees are empty\
And JSON warnings describe unavailable artifacts\
And every non-null screenshot reference points to an entry in the package

### Export Controls Recover After Success or Failure

Verification: [`model-export-controls.test.cjs`](../../statemodel/test/js/model-export-controls.test.cjs), [`ModelExportServletTest.java`](../../statemodel/test/org/testar/statemodel/analysis/ModelExportServletTest.java)

Given the user confirms the export dialog\
When data loading or packaging is in progress\
Then export controls are disabled and another click cannot start a duplicate request\
And Cancel and Escape keep the dialog open\
When export succeeds\
Then the selected package is downloaded and the dialog closes

Given the user confirms the export dialog\
When data loading or packaging fails\
Then no partial package is downloaded\
And an error remains visible in the open dialog\
And controls become available for retry

## Implementation and Verification

- [ModelExportOptions.java](../../statemodel/src/org/testar/statemodel/analysis/export/ModelExportOptions.java), [ModelExportSnapshot.java](../../statemodel/src/org/testar/statemodel/analysis/export/ModelExportSnapshot.java), and [AnalysisManager.java](../../statemodel/src/org/testar/statemodel/analysis/AnalysisManager.java) share optional artifact preparation, representative selection, and property inventory.
- [ModelExportServlet.java](../../statemodel/src/org/testar/statemodel/analysis/ModelExportServlet.java) serves live data; [StaticGraphExporter.java](../../statemodel/src/org/testar/statemodel/analysis/export/StaticGraphExporter.java) packages offline data.
- [model-json-export.js](../../statemodel/resources/graphs/js/model-json-export.js) transforms data and creates the ZIP; [model-export-runtime.js](../../statemodel/resources/graphs/js/model-export-runtime.js) reads progress streams and runs that implementation in a worker; [model-export-controls.js](../../statemodel/resources/graphs/js/model-export-controls.js) manages options, feedback, and downloads.
