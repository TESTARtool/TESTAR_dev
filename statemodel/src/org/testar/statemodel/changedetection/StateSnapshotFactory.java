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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.testar.statemodel.AbstractState;
import org.testar.statemodel.AbstractStateModel;
import org.testar.statemodel.AbstractStateTransition;

/**
 * Builds {@link StateSnapshot}s from an {@link AbstractStateModel}.
 */
public class StateSnapshotFactory {

    private StateSnapshotFactory() { }

    public static List<StateSnapshot> from(AbstractStateModel model) {
        Objects.requireNonNull(model, "model cannot be null");

        Map<String, Set<String>> incomingActions = new HashMap<>();
        Map<String, Set<String>> outgoingActions = new HashMap<>();

        // collect incoming/outgoing action ids per state using the model transitions
        for (AbstractState state : model.getStates()) {
            incomingActions.put(state.getStateId(), new HashSet<>());
            outgoingActions.put(state.getStateId(), new HashSet<>());
        }

        for (AbstractState state : model.getStates()) {
            for (AbstractStateTransition transition : model.getOutgoingTransitionsForState(state.getStateId())) {
                outgoingActions.get(state.getStateId()).add(transition.getActionId());
                incomingActions.computeIfAbsent(transition.getTargetStateId(), id -> new HashSet<>())
                        .add(transition.getActionId());
            }
        }

        List<StateSnapshot> snapshots = new ArrayList<>();
        for (AbstractState state : model.getStates()) {
            List<String> concreteIds = new ArrayList<>(state.getConcreteStateIds());
            List<String> incoming = new ArrayList<>(incomingActions.getOrDefault(state.getStateId(), new HashSet<>()));
            List<String> outgoing = new ArrayList<>(outgoingActions.getOrDefault(state.getStateId(), new HashSet<>()));

            snapshots.add(new StateSnapshot(state.getStateId(), concreteIds, incoming, outgoing));
        }
        return snapshots;
    }

}
