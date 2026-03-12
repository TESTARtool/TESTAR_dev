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

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Captures added/removed incoming and outgoing actions for a state that exists in both models.
 */
public class ActionSetDiff {

    private final List<DeltaAction> addedIncoming;
    private final List<DeltaAction> removedIncoming;
    private final List<DeltaAction> addedOutgoing;
    private final List<DeltaAction> removedOutgoing;

    public ActionSetDiff(List<DeltaAction> addedIncoming,
                         List<DeltaAction> removedIncoming,
                         List<DeltaAction> addedOutgoing,
                         List<DeltaAction> removedOutgoing) {
        this.addedIncoming = requireNoNulls(addedIncoming, "addedIncoming");
        this.removedIncoming = requireNoNulls(removedIncoming, "removedIncoming");
        this.addedOutgoing = requireNoNulls(addedOutgoing, "addedOutgoing");
        this.removedOutgoing = requireNoNulls(removedOutgoing, "removedOutgoing");
    }

    private static List<DeltaAction> requireNoNulls(List<DeltaAction> source, String label) {
        Objects.requireNonNull(source, label + " cannot be null");
        for (DeltaAction action : source) {
            Objects.requireNonNull(action, label + " entry cannot be null");
        }
        return source;
    }

    public List<DeltaAction> getAddedIncoming() {
        return Collections.unmodifiableList(addedIncoming);
    }

    public List<DeltaAction> getRemovedIncoming() {
        return Collections.unmodifiableList(removedIncoming);
    }

    public List<DeltaAction> getAddedOutgoing() {
        return Collections.unmodifiableList(addedOutgoing);
    }

    public List<DeltaAction> getRemovedOutgoing() {
        return Collections.unmodifiableList(removedOutgoing);
    }

    public boolean isEmpty() {
        return addedIncoming.isEmpty() && removedIncoming.isEmpty()
                && addedOutgoing.isEmpty() && removedOutgoing.isEmpty();
    }

}
