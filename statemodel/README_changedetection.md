# TESTAR StateModel – Change Detection

This document describes the **Change Detection** implementation in the `statemodel` framework: 

- Core comparison logic (`statemodel\src\org\testar\statemodel\changedetection`)
- Visualization analysis (`statemodel\src\org\testar\statemodel\analysis\changedetection`)
- Merged graph rendering (`statemodel\resources\graphs/changedetection.jsp`)

## Goals and scope

- Compare **two persisted OrientDB State Models** and classify differences as:
  - **Added** (states and actions that exists only in new model)
  - **Removed** (states and actions that exists only in old model)
  - **Changed** (matched state exists in both but differs in properties and/or its action set)
  - **Unchanged** (matched state and relevant properties/actions are the same)
- Build a **merged visualization graph** for the web analysis mode.
- Use **action descriptions** (Desc) as the preferred key for matching actions across models, with **actionId fallback**.

The comparison is primarily performed on the **Abstract layer** (abstract states and abstract actions). Concrete layer entities are still used **server-side** for screenshots and additional metadata such as descriptions.

### Web entry points

- `GET /changedetection` → `ChangeDetectionServlet`
  - Renders the page and model selectors.
- `GET /changedetection-graph?oldModelIdentifier=...&newModelIdentifier=...` → `ChangeDetectionGraphServlet`
  - Returns the merged graph as JSON (nodes/edges + status + metadata).

Both servlets use `ChangeDetectionFacade` to ensure the persistence layer is opened/closed consistently.

## Core comparison logic

The core comparison logic is under `org.testar.statemodel.changedetection`:

- `...changedetection`
  - `ChangeDetectionEngine` – runs the comparison.
  - `ChangeDetectionEngineFactory` – creates engines with different primary-key providers.
  - `ChangeDetectionResult` – aggregated result (added/removed/changed).
- `...changedetection.delta` (**DTOs / deltas**)
  - `DeltaState`, `DeltaAction`, `ActionSetDiff`, `DiffType`
- `...changedetection.diff` (**property diffing**)
  - `PropertyDiff`, `VertexPropertyDiff`
  - `VertexPropertyComparator`, `StatePropertyExtractor`, `StatePropertyComparator`
- `...changedetection.key` (**action primary-key providers**)
  - `ActionPrimaryKeyProvider`
  - `DefaultActionPrimaryKeyProvider` (fallback implementation)
  - `OrientDbActionPrimaryKeyProvider` (Desc preferred, actionId fallback; includes caching)
- `...changedetection.algorithm` (**traversal-based comparator internals**)
  - `GraphTraversalComparator` and traversal helper classes (`Traversal*`, etc.)

## Visualization analysis

Visualization-specific logic lives under `org.testar.statemodel.analysis.changedetection`:

- `ChangeDetectionFacade`
  - Opens persistence, loads model graph elements, builds the merged graph JSON, then closes everything.
- `ChangeDetectionAnalysisService`
  - Loads `AbstractStateModel` instances and invokes the core engine.
- `ChangeDetectionGraphBuilder`
  - Converts `ChangeDetectionResult` + graph elements into the JSON format expected by the JSP/Cytoscape frontend.
  - Assigns screenshots to abstract states using related concrete state screenshots.
- Helpers: `...analysis.changedetection.helpers.*`
  - Example responsibilities: resolve statuses with precedence rules, index graph elements, assign screenshots.

## Merged graph rendering

- Frontend: `statemodel/resources/graphs/changedetection.jsp`
  - Cytoscape rendering, details sidebar, and pixel-diff (pixelmatch) for old vs new screenshots.

## Core algorithm (what “changed” means)

### Traversal-based matching

`GraphTraversalComparator` performs a traversal starting from the **initial abstract state** in both models:

- States are **mapped** old <-> new during traversal to avoid double-counting and infinite loops.
- For a mapped state pair:
  - Properties are compared (via `StatePropertyComparator` → `VertexPropertyComparator`).
  - Outgoing action edges are compared using an **action primary key** (see below).
- Remaining unmapped states become:
  - **Added** (only reachable in new)
  - **Removed** (only reachable in old)

### Action identity: `primaryKey`

The comparison and merge logic use **one canonical identity for actions**, called `primaryKey`:

1. Prefer the action **Desc** resolved from OrientDB (via `OrientDbActionPrimaryKeyProvider`).
2. If no Desc exists (or it is empty/invalid), fallback to the **actionId**.

This same `primaryKey` is used for:

- Matching outgoing action sets between old/new during traversal.
- Merging edges in the merged visualization graph.
- Displaying action labels in the UI.

## Visualization semantics (status precedence)

The UI shows node/edge status based on the merged result. A key rule:

- **Added/Removed dominates Changed** for visualization.

This prevents a state from being shown as “changed” when it is actually new/removed but happens to appear in `changedActions` (e.g., due to incoming/outgoing diffs being reported for a newly added state).

## Screenshots and diffs

- State node screenshots are derived from the first concrete state screenshot linked to the abstract state.
- The frontend uses `pixelmatchInterop.js` to compute and display a visual change diff (when both screenshots exist).

## Notes / limitations

- Results depend heavily on the **abstraction attributes** used when building the models (small abstraction changes can create large diffs).
- Very large graphs can be expensive to render.
- If you see OrientDB “file locked by another process” errors when using plocal mode, ensure analysis requests always go through `ChangeDetectionFacade` so connections are closed promptly.
