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

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * State used for change detection. 
 */
public class StateSnapshot {

    private final String stateId;
    private final List<String> concreteStateIds;
    private final List<String> incomingActionIds;
    private final List<String> outgoingActionIds;

    public StateSnapshot(String stateId,
                         List<String> concreteStateIds,
                         List<String> incomingActionIds,
                         List<String> outgoingActionIds) {
        this.stateId = Objects.requireNonNull(stateId, "stateId cannot be null");
        if (stateId.trim().isEmpty()) {
            throw new IllegalArgumentException("stateId cannot be empty or blank");
        }
        this.concreteStateIds = copySafe(concreteStateIds, "concrete state id");
        this.incomingActionIds = copySafe(incomingActionIds, "incoming action id");
        this.outgoingActionIds = copySafe(outgoingActionIds, "outgoing action id");
    }

    private static List<String> copySafe(List<String> source, String label) {
        if (source == null) {
            return Collections.emptyList();
        }
        for (String value : source) {
            Objects.requireNonNull(value, label + " cannot be null");
            if (value.trim().isEmpty()) {
                throw new IllegalArgumentException(label + " cannot be empty or blank");
            }
        }
        return Collections.unmodifiableList(source);
    }

    public String getStateId() {
        return stateId;
    }

    public List<String> getConcreteStateIds() {
        return concreteStateIds;
    }

    public List<String> getIncomingActionIds() {
        return incomingActionIds;
    }

    public List<String> getOutgoingActionIds() {
        return outgoingActionIds;
    }

}
