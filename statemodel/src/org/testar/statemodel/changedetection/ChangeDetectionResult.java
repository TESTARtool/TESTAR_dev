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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;

/**
 * Aggregated result for a change detection run between two state models.
 */
public class ChangeDetectionResult {

    private final String oldModelIdentifier;
    private final String newModelIdentifier;
    private final List<DeltaState> addedStates;
    private final List<DeltaState> removedStates;
    private final Map<String, VertexPropertyDiff> changedStates;
    private final Map<String, ActionSetDiff> changedActions;

    public ChangeDetectionResult(String oldModelIdentifier,
                                 String newModelIdentifier,
                                 List<DeltaState> addedStates,
                                 List<DeltaState> removedStates,
                                 Map<String, VertexPropertyDiff> changedStates,
                                 Map<String, ActionSetDiff> changedActions) {
        this.oldModelIdentifier = Objects.requireNonNull(oldModelIdentifier, "oldModelIdentifier cannot be null");
        this.newModelIdentifier = Objects.requireNonNull(newModelIdentifier, "newModelIdentifier cannot be null");
        if (oldModelIdentifier.trim().isEmpty() || newModelIdentifier.trim().isEmpty()) {
            throw new IllegalArgumentException("model identifiers cannot be empty or blank");
        }
        this.addedStates = copyStates(addedStates);
        this.removedStates = copyStates(removedStates);
        this.changedStates = copyChanged(changedStates);
        this.changedActions = copyChangedActions(changedActions);
    }

    private static List<DeltaState> copyStates(List<DeltaState> source) {
        List<DeltaState> copy = new ArrayList<>();
        if (source != null) {
            for (DeltaState state : source) {
                copy.add(Objects.requireNonNull(state, "delta state cannot be null"));
            }
        }
        return copy;
    }

    private static Map<String, VertexPropertyDiff> copyChanged(Map<String, VertexPropertyDiff> source) {
        Map<String, VertexPropertyDiff> copy = new HashMap<>();
        if (source != null) {
            for (Map.Entry<String, VertexPropertyDiff> entry : source.entrySet()) {
                Objects.requireNonNull(entry.getKey(), "changed state id cannot be null");
                Objects.requireNonNull(entry.getValue(), "changed diff cannot be null");
                copy.put(entry.getKey(), entry.getValue());
            }
        }
        return copy;
    }

    private static Map<String, ActionSetDiff> copyChangedActions(Map<String, ActionSetDiff> source) {
        Map<String, ActionSetDiff> copy = new HashMap<>();
        if (source != null) {
            for (Map.Entry<String, ActionSetDiff> entry : source.entrySet()) {
                Objects.requireNonNull(entry.getKey(), "changed actions state id cannot be null");
                Objects.requireNonNull(entry.getValue(), "changed actions diff cannot be null");
                copy.put(entry.getKey(), entry.getValue());
            }
        }
        return copy;
    }

    public String getOldModelIdentifier() {
        return oldModelIdentifier;
    }

    public String getNewModelIdentifier() {
        return newModelIdentifier;
    }

    public List<DeltaState> getAddedStates() {
        return Collections.unmodifiableList(addedStates);
    }

    public List<DeltaState> getRemovedStates() {
        return Collections.unmodifiableList(removedStates);
    }

    public Map<String, VertexPropertyDiff> getChangedStates() {
        return Collections.unmodifiableMap(changedStates);
    }

    public Map<String, ActionSetDiff> getChangedActions() {
        return Collections.unmodifiableMap(changedActions);
    }

    public boolean hasDifferences() {
        return !addedStates.isEmpty() || !removedStates.isEmpty() || !changedStates.isEmpty() || !changedActions.isEmpty();
    }

}
