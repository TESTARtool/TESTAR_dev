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

package org.testar.statemodel.changedetection.delta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class DeltaState {

    private final String stateId;
    private final List<String> concreteStateIds;
    private final List<DeltaAction> incomingDeltaActions;
    private final List<DeltaAction> outgoingDeltaActions;

    public DeltaState(String stateId,
                      List<String> concreteStateIds,
                      List<DeltaAction> incomingDeltaActions,
                      List<DeltaAction> outgoingDeltaActions) {
        this.stateId = Objects.requireNonNull(stateId, "stateId cannot be null");
        if (stateId.trim().isEmpty()) {
            throw new IllegalArgumentException("stateId cannot be empty or blank");
        }
        this.concreteStateIds = copyConcreteStateIdList(concreteStateIds);
        this.incomingDeltaActions = copyDeltaActionList(incomingDeltaActions);
        this.outgoingDeltaActions = copyDeltaActionList(outgoingDeltaActions);
    }

    private static List<String> copyConcreteStateIdList(List<String> source) {
        List<String> copy = new ArrayList<>();
        if (source != null) {
            for (String item : source) {
                Objects.requireNonNull(item, "ConcreteStateID cannot be null");
                if (item.trim().isEmpty()) {
                    throw new IllegalArgumentException("ConcreteStateID cannot be empty or blank");
                }
                copy.add(item);
            }
        }
        return copy;
    }

    private static List<DeltaAction> copyDeltaActionList(List<DeltaAction> source) {
        List<DeltaAction> copy = new ArrayList<>();
        if (source != null) {
            for (DeltaAction item : source) {
                copy.add(Objects.requireNonNull(item, "DeltaAction cannot be null"));
            }
        }
        return copy;
    }

    public String getStateId() {
        return stateId;
    }

    public List<String> getConcreteStateIds() {
        return Collections.unmodifiableList(concreteStateIds);
    }

    public List<DeltaAction> getIncomingDeltaActions() {
        return Collections.unmodifiableList(incomingDeltaActions);
    }

    public List<DeltaAction> getOutgoingDeltaActions() {
        return Collections.unmodifiableList(outgoingDeltaActions);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof DeltaState)) {
            return false;
        }
        DeltaState other = (DeltaState) obj;
        return stateId.equals(other.stateId) &&
                concreteStateIds.equals(other.concreteStateIds) &&
                incomingDeltaActions.equals(other.incomingDeltaActions) &&
                outgoingDeltaActions.equals(other.outgoingDeltaActions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stateId, concreteStateIds, incomingDeltaActions, outgoingDeltaActions);
    }

}
