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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testar.statemodel.changedetection.VertexPropertyDiff;

/**
 * Traversal context used by {@link GraphTraversalComparator}.
 *
 * It maintains:
 * - state mapping old->new (and inverse)
 * - handled edges/states to avoid infinite traversal loops
 * - collected added/removed edges and changed states
 */
final class TraversalContext {

    final TraversalGraph oldGraph;
    final TraversalGraph newGraph;
    final Map<String, String> oldToNew = new HashMap<>();
    final Map<String, String> newToOld = new HashMap<>();
    final Map<String, VertexPropertyDiff> changedStates = new HashMap<>();
    final List<TraversalEdge> addedEdges = new ArrayList<>();
    final List<TraversalEdge> removedEdges = new ArrayList<>();
    final List<TraversalEdgePair> matchedEdges = new ArrayList<>();

    TraversalContext(TraversalGraph oldGraph, TraversalGraph newGraph) {
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

    TraversalEdge findMatchingOutgoing(TraversalNode oldNode, String comparableKey) {
        for (TraversalEdge e : oldNode.outgoing) {
            if (!e.handled && e.comparableKey.equals(comparableKey)) {
                return e;
            }
        }
        return null;
    }

}
