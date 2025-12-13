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

package org.testar.statemodel.changedetection.algorithm;

import org.testar.statemodel.AbstractState;
import org.testar.statemodel.AbstractStateModel;
import org.testar.statemodel.AbstractStateTransition;
import org.testar.statemodel.changedetection.ActionPrimaryKeyProvider;
import org.testar.statemodel.changedetection.ActionSetDiff;
import org.testar.statemodel.changedetection.ChangeDetectionResult;
import org.testar.statemodel.changedetection.DeltaAction;
import org.testar.statemodel.changedetection.DeltaState;
import org.testar.statemodel.changedetection.StatePropertyComparator;
import org.testar.statemodel.changedetection.VertexPropertyDiff;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Traversal-based state models comparator.
 * Connects to the initial abstract state and uses the action descriptions as the matching key (desc or action id), 
 * and tracks handled states/actions to avoid double counting.
 */
public class GraphTraversalComparator {

    private final ActionPrimaryKeyProvider primaryKeyProvider;

    public GraphTraversalComparator(ActionPrimaryKeyProvider primaryKeyProvider) {
        this.primaryKeyProvider = Objects.requireNonNull(primaryKeyProvider, "primaryKeyProvider cannot be null");
    }

    public ChangeDetectionResult compare(AbstractStateModel oldModel, AbstractStateModel newModel) {
        TraversalGraph oldGraph = buildGraph(oldModel);
        TraversalGraph newGraph = buildGraph(newModel);

        TraversalContext ctx = new TraversalContext(oldGraph, newGraph);

        TraversalNode oldStart = chooseInitialState(oldGraph);
        TraversalNode newStart = chooseInitialState(newGraph);

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

    private void compareStates(TraversalNode oldNode, TraversalNode newNode, TraversalContext ctx) {
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
        for (TraversalEdge newEdge : newNode.outgoing) {
            if (newEdge.handled) {
                continue;
            }
            TraversalEdge match = ctx.findMatchingOutgoing(oldNode, newEdge.comparableKey);
            if (match == null) {
                ctx.addedEdges.add(newEdge);
            } else {
                match.handled = true;
                newEdge.handled = true;
                ctx.matchedEdges.add(new TraversalEdgePair(match, newEdge));

                TraversalNode oldTarget = ctx.oldGraph.nodes.get(match.targetId);
                TraversalNode newTarget = ctx.newGraph.nodes.get(newEdge.targetId);

                if (oldTarget != null && newTarget != null) {
                    // if mapping already exists but points elsewhere, skip recursion
                    if (!ctx.hasConflictingMapping(oldTarget.id, newTarget.id) && !oldTarget.handled && !newTarget.handled) {
                        compareStates(oldTarget, newTarget, ctx);
                    }
                }
            }
        }

        // Remaining unhandled outgoing actions on old node are removed
        for (TraversalEdge oldEdge : oldNode.outgoing) {
            if (!oldEdge.handled) {
                ctx.removedEdges.add(oldEdge);
            }
        }
    }

    private List<DeltaState> computeAddedStates(TraversalContext ctx) {
        List<DeltaState> added = new ArrayList<>();
        for (TraversalNode newNode : ctx.newGraph.nodes.values()) {
            if (!ctx.newToOld.containsKey(newNode.id)) {
                added.add(toDeltaState(newNode, ctx.newGraph));
            }
        }
        return added;
    }

    private List<DeltaState> computeRemovedStates(TraversalContext ctx) {
        List<DeltaState> removed = new ArrayList<>();
        for (TraversalNode oldNode : ctx.oldGraph.nodes.values()) {
            if (!ctx.oldToNew.containsKey(oldNode.id)) {
                removed.add(toDeltaState(oldNode, ctx.oldGraph));
            }
        }
        return removed;
    }

    private Map<String, ActionSetDiff> computeChangedActions(TraversalContext ctx) {
        Map<String, MutableActionDiff> diffByNewState = new HashMap<>();

        for (TraversalEdge added : ctx.addedEdges) {
            // source outgoing
            MutableActionDiff sourceDiff = diffByNewState.computeIfAbsent(added.sourceId, k -> new MutableActionDiff());
            sourceDiff.addOutgoing.add(new DeltaAction(added.actionId, added.description, DeltaAction.Direction.OUTGOING));
            // target incoming
            MutableActionDiff targetDiff = diffByNewState.computeIfAbsent(added.targetId, k -> new MutableActionDiff());
            targetDiff.addIncoming.add(new DeltaAction(added.actionId, added.description, DeltaAction.Direction.INCOMING));
            // mark state as changed if not already present
            ctx.changedStates.putIfAbsent(added.sourceId, new VertexPropertyDiff(new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));
        }

        for (TraversalEdge removed : ctx.removedEdges) {
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

    private DeltaState toDeltaState(TraversalNode node, TraversalGraph graph) {
        List<DeltaAction> incoming = node.incoming.stream()
                .map(e -> new DeltaAction(e.actionId, e.description, DeltaAction.Direction.INCOMING))
                .collect(Collectors.toList());
        List<DeltaAction> outgoing = node.outgoing.stream()
                .map(e -> new DeltaAction(e.actionId, e.description, DeltaAction.Direction.OUTGOING))
                .collect(Collectors.toList());
        return new DeltaState(node.id, new ArrayList<>(node.concreteIds), incoming, outgoing);
    }

    private TraversalGraph buildGraph(AbstractStateModel model) {
        Map<String, TraversalNode> nodes = new HashMap<>();
        List<TraversalEdge> edges = new ArrayList<>();

        for (AbstractState s : model.getStates()) {
            nodes.put(s.getStateId(), new TraversalNode(s));
        }

        for (TraversalNode n : nodes.values()) {
            Collection<AbstractStateTransition> outgoing = model.getOutgoingTransitionsForState(n.id);
            for (AbstractStateTransition t : outgoing) {
                TraversalEdge e = toEdge(t, nodes);
                n.outgoing.add(e);
                nodes.get(e.targetId).incoming.add(e);
                edges.add(e);
            }
        }

        return new TraversalGraph(nodes, edges);
    }

    private TraversalEdge toEdge(AbstractStateTransition t, Map<String, TraversalNode> nodes) {
        String actionId = t.getActionId();
        String desc = primaryKeyProvider.getPrimaryKey(actionId);
        String key = comparableKey(actionId, desc);
        return new TraversalEdge(t.getSourceStateId(), t.getTargetStateId(), actionId, desc, key);
    }

    private String comparableKey(String actionId, String description) {
        if (description == null || description.trim().isEmpty()) {
            return actionId;
        }
        // Workaround for actions with empty descriptions
        if (description.contains("at ''")) {
            return actionId;
        }
        return description;
    }

    private TraversalNode chooseInitialState(TraversalGraph graph) {
        for (TraversalNode n : graph.nodes.values()) {
            if (n.initial) {
                return n;
            }
        }
        return null;
    }

}
