/***************************************************************************************************
 *
 * Copyright (c) 2025 Open Universiteit - www.ou.nl
 * Copyright (c) 2025 Universitat Politecnica de Valencia - www.upv.es
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************************************/

package org.testar.statemodel.changedetection;

import org.testar.statemodel.AbstractState;
import org.testar.statemodel.AbstractStateModel;
import org.testar.statemodel.AbstractStateTransition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Traversal-based state models comparator.
 * Connects to the initial abstract state and uses the action descriptions as the matching key (desc or action id), 
 * and tracks handled states/actions to avoid double counting.
 */
class GraphTraversalComparator {

    private final ActionPrimaryKeyProvider primaryKeyProvider;

    GraphTraversalComparator(ActionPrimaryKeyProvider primaryKeyProvider) {
        this.primaryKeyProvider = Objects.requireNonNull(primaryKeyProvider, "primaryKeyProvider cannot be null");
    }

    ChangeDetectionResult compare(AbstractStateModel oldModel, AbstractStateModel newModel) {
        Graph oldGraph = buildGraph(oldModel);
        Graph newGraph = buildGraph(newModel);

        TraversalContext ctx = new TraversalContext(oldGraph, newGraph);

        Node oldStart = chooseInitialState(oldGraph);
        Node newStart = chooseInitialState(newGraph);

        if (oldStart != null && newStart != null) {
            compareStates(oldStart, newStart, ctx);
        }

        // Any remaining unmapped states/actions are added/removed
        List<DeltaState> addedStates = computeAddedStates(ctx);
        List<DeltaState> removedStates = computeRemovedStates(ctx);

        Map<String, ActionSetDiff> changedActions = computeChangedActions(ctx);
        Map<String, VertexPropertyDiff> changedStates = ctx.changedStates;

        return new ChangeDetectionResult(
                oldModel.getModelIdentifier(),
                newModel.getModelIdentifier(),
                addedStates,
                removedStates,
                changedStates,
                changedActions
        );
    }

    private void compareStates(Node oldNode, Node newNode, TraversalContext ctx) {
        if (ctx.isMapped(oldNode.id, newNode.id)) {
            return;
        }

        ctx.mapStates(oldNode.id, newNode.id);
        oldNode.handled = true;
        newNode.handled = true;

        VertexPropertyDiff propDiff = StatePropertyComparator.compare(oldNode.state, newNode.state);
        if (!propDiff.isEmpty()) {
            ctx.changedStates.put(newNode.id, propDiff);
        }

        // Match outgoing actions by comparable key (description preferred)
        for (Edge newEdge : newNode.outgoing) {
            if (newEdge.handled) {
                continue;
            }
            Edge match = ctx.findMatchingOutgoing(oldNode, newEdge.comparableKey);
            if (match == null) {
                ctx.addedEdges.add(newEdge);
            } else {
                match.handled = true;
                newEdge.handled = true;
                ctx.matchedEdges.add(new EdgePair(match, newEdge));

                Node oldTarget = ctx.oldGraph.nodes.get(match.targetId);
                Node newTarget = ctx.newGraph.nodes.get(newEdge.targetId);

                if (oldTarget != null && newTarget != null) {
                    // if mapping already exists but points elsewhere, skip recursion
                    if (!ctx.hasConflictingMapping(oldTarget.id, newTarget.id) && !oldTarget.handled && !newTarget.handled) {
                        compareStates(oldTarget, newTarget, ctx);
                    }
                }
            }
        }

        // Remaining unhandled outgoing actions on old node are removed
        for (Edge oldEdge : oldNode.outgoing) {
            if (!oldEdge.handled) {
                ctx.removedEdges.add(oldEdge);
            }
        }
    }

    private List<DeltaState> computeAddedStates(TraversalContext ctx) {
        List<DeltaState> added = new ArrayList<>();
        for (Node newNode : ctx.newGraph.nodes.values()) {
            if (!ctx.newToOld.containsKey(newNode.id)) {
                added.add(toDeltaState(newNode, ctx.newGraph));
            }
        }
        return added;
    }

    private List<DeltaState> computeRemovedStates(TraversalContext ctx) {
        List<DeltaState> removed = new ArrayList<>();
        for (Node oldNode : ctx.oldGraph.nodes.values()) {
            if (!ctx.oldToNew.containsKey(oldNode.id)) {
                removed.add(toDeltaState(oldNode, ctx.oldGraph));
            }
        }
        return removed;
    }

    private Map<String, ActionSetDiff> computeChangedActions(TraversalContext ctx) {
        Map<String, MutableActionDiff> diffByNewState = new HashMap<>();

        for (Edge added : ctx.addedEdges) {
            // source outgoing
            MutableActionDiff sourceDiff = diffByNewState.computeIfAbsent(added.sourceId, k -> new MutableActionDiff());
            sourceDiff.addOutgoing.add(new DeltaAction(added.actionId, added.description, DeltaAction.Direction.OUTGOING));
            // target incoming
            MutableActionDiff targetDiff = diffByNewState.computeIfAbsent(added.targetId, k -> new MutableActionDiff());
            targetDiff.addIncoming.add(new DeltaAction(added.actionId, added.description, DeltaAction.Direction.INCOMING));
            // mark state as changed if not already present
            ctx.changedStates.putIfAbsent(added.sourceId, new VertexPropertyDiff(new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));
        }

        for (Edge removed : ctx.removedEdges) {
            String mappedSource = ctx.oldToNew.get(removed.sourceId);
            String mappedTarget = ctx.oldToNew.get(removed.targetId);
            if (mappedSource != null) {
                MutableActionDiff sourceDiff = diffByNewState.computeIfAbsent(mappedSource, k -> new MutableActionDiff());
                sourceDiff.remOutgoing.add(new DeltaAction(removed.actionId, removed.description, DeltaAction.Direction.OUTGOING));
                ctx.changedStates.putIfAbsent(mappedSource, new VertexPropertyDiff(new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));
            }
            if (mappedTarget != null) {
                MutableActionDiff targetDiff = diffByNewState.computeIfAbsent(mappedTarget, k -> new MutableActionDiff());
                targetDiff.remIncoming.add(new DeltaAction(removed.actionId, removed.description, DeltaAction.Direction.INCOMING));
                ctx.changedStates.putIfAbsent(mappedTarget, new VertexPropertyDiff(new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));
            }
        }

        Map<String, ActionSetDiff> result = new HashMap<>();
        for (Map.Entry<String, MutableActionDiff> entry : diffByNewState.entrySet()) {
            MutableActionDiff d = entry.getValue();
            result.put(entry.getKey(), new ActionSetDiff(d.addIncoming, d.remIncoming, d.addOutgoing, d.remOutgoing));
        }
        return result;
    }

    private DeltaState toDeltaState(Node node, Graph graph) {
        List<DeltaAction> incoming = node.incoming.stream()
                .map(e -> new DeltaAction(e.actionId, e.description, DeltaAction.Direction.INCOMING))
                .collect(Collectors.toList());
        List<DeltaAction> outgoing = node.outgoing.stream()
                .map(e -> new DeltaAction(e.actionId, e.description, DeltaAction.Direction.OUTGOING))
                .collect(Collectors.toList());
        return new DeltaState(node.id, new ArrayList<>(node.concreteIds), incoming, outgoing);
    }

    private Graph buildGraph(AbstractStateModel model) {
        Map<String, Node> nodes = new HashMap<>();
        List<Edge> edges = new ArrayList<>();

        for (AbstractState s : model.getStates()) {
            nodes.put(s.getStateId(), new Node(s));
        }

        for (Node n : nodes.values()) {
            Collection<AbstractStateTransition> outgoing = model.getOutgoingTransitionsForState(n.id);
            for (AbstractStateTransition t : outgoing) {
                Edge e = toEdge(t, nodes);
                n.outgoing.add(e);
                nodes.get(e.targetId).incoming.add(e);
                edges.add(e);
            }
        }

        return new Graph(nodes, edges);
    }

    private Edge toEdge(AbstractStateTransition t, Map<String, Node> nodes) {
        String actionId = t.getActionId();
        String desc = primaryKeyProvider.getPrimaryKey(actionId);
        String key = comparableKey(actionId, desc);
        return new Edge(t.getSourceStateId(), t.getTargetStateId(), actionId, desc, key);
    }

    private String comparableKey(String actionId, String description) {
        if (description == null || description.trim().isEmpty() || description.contains("at ''")) {
            return actionId;
        }
        return description;
    }

    private Node chooseInitialState(Graph graph) {
        for (Node n : graph.nodes.values()) {
            if (n.initial) {
                return n;
            }
        }
        return null;
    }

    private static final class TraversalContext {
        final Graph oldGraph;
        final Graph newGraph;
        final Map<String, String> oldToNew = new HashMap<>();
        final Map<String, String> newToOld = new HashMap<>();
        final Map<String, VertexPropertyDiff> changedStates = new HashMap<>();
        final List<Edge> addedEdges = new ArrayList<>();
        final List<Edge> removedEdges = new ArrayList<>();
        final List<EdgePair> matchedEdges = new ArrayList<>();

        TraversalContext(Graph oldGraph, Graph newGraph) {
            this.oldGraph = oldGraph;
            this.newGraph = newGraph;
        }

        void mapStates(String oldId, String newId) {
            oldToNew.put(oldId, newId);
            newToOld.put(newId, oldId);
        }

        boolean isMapped(String oldId, String newId) {
            return newId.equals(oldToNew.get(oldId)) || oldId.equals(newToOld.get(newId));
        }

        boolean hasConflictingMapping(String oldId, String newId) {
            String mapped = oldToNew.get(oldId);
            return mapped != null && !mapped.equals(newId);
        }

        Edge findMatchingOutgoing(Node oldNode, String comparableKey) {
            for (Edge e : oldNode.outgoing) {
                if (!e.handled && e.comparableKey.equals(comparableKey)) {
                    return e;
                }
            }
            return null;
        }
    }

    private static final class Graph {
        final Map<String, Node> nodes;
        final List<Edge> edges;

        Graph(Map<String, Node> nodes, List<Edge> edges) {
            this.nodes = nodes;
            this.edges = edges;
        }
    }

    private static final class Node {
        final String id;
        final AbstractState state;
        final boolean initial;
        final Set<String> concreteIds;
        final List<Edge> outgoing = new ArrayList<>();
        final List<Edge> incoming = new ArrayList<>();
        boolean handled = false;

        Node(AbstractState state) {
            this.state = state;
            this.id = state.getStateId();
            this.initial = state.isInitial();
            this.concreteIds = new HashSet<>(state.getConcreteStateIds());
        }
    }

    private static final class Edge {
        final String sourceId;
        final String targetId;
        final String actionId;
        final String description;
        final String comparableKey;
        boolean handled = false;

        Edge(String sourceId, String targetId, String actionId, String description, String comparableKey) {
            this.sourceId = sourceId;
            this.targetId = targetId;
            this.actionId = actionId;
            this.description = description;
            this.comparableKey = comparableKey;
        }
    }

    private static final class EdgePair {
        final Edge oldEdge;
        final Edge newEdge;

        EdgePair(Edge oldEdge, Edge newEdge) {
            this.oldEdge = oldEdge;
            this.newEdge = newEdge;
        }
    }

    private static final class MutableActionDiff {
        final List<DeltaAction> addIncoming = new ArrayList<>();
        final List<DeltaAction> remIncoming = new ArrayList<>();
        final List<DeltaAction> addOutgoing = new ArrayList<>();
        final List<DeltaAction> remOutgoing = new ArrayList<>();
    }
}
