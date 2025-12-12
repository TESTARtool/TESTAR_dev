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
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.testar.statemodel.changedetection.DeltaAction.Direction;

/**
 * Compares incoming/outgoing action sets between two states.
 */
public class ActionSetComparator {

    private ActionSetComparator() { }

    public static ActionSetDiff compare(StateSnapshot oldState,
                                        StateSnapshot newState,
                                        ActionPrimaryKeyProvider descriptionProvider) {
        Objects.requireNonNull(oldState, "oldState cannot be null");
        Objects.requireNonNull(newState, "newState cannot be null");
        Objects.requireNonNull(descriptionProvider, "descriptionProvider cannot be null");

        List<DeltaAction> addedIncoming = diff(newState.getIncomingActionIds(), oldState.getIncomingActionIds(), Direction.INCOMING, descriptionProvider);
        List<DeltaAction> removedIncoming = diff(oldState.getIncomingActionIds(), newState.getIncomingActionIds(), Direction.INCOMING, descriptionProvider);
        List<DeltaAction> addedOutgoing = diff(newState.getOutgoingActionIds(), oldState.getOutgoingActionIds(), Direction.OUTGOING, descriptionProvider);
        List<DeltaAction> removedOutgoing = diff(oldState.getOutgoingActionIds(), newState.getOutgoingActionIds(), Direction.OUTGOING, descriptionProvider);

        return new ActionSetDiff(addedIncoming, removedIncoming, addedOutgoing, removedOutgoing);
    }

    private static List<DeltaAction> diff(List<String> primary, List<String> secondary, Direction direction, ActionPrimaryKeyProvider descriptionProvider) {
        Set<String> secondarySet = new HashSet<>(secondary);
        List<DeltaAction> deltas = new ArrayList<>();
        for (String actionId : primary) {
            if (!secondarySet.contains(actionId)) {
                deltas.add(new DeltaAction(actionId, descriptionProvider.getPrimaryKey(actionId), direction));
            }
        }
        return deltas;
    }

}
