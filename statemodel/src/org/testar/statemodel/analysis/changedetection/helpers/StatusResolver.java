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

package org.testar.statemodel.analysis.changedetection.helpers;

import org.testar.statemodel.changedetection.ChangeDetectionResult;
import org.testar.statemodel.changedetection.delta.DeltaState;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Computes node statuses for change detection visualization:
 * - added/removed dominates everything
 * - changed applies only if the state is not already added/removed
 * - otherwise unchanged
 */
public final class StatusResolver {

    public static final String ADDED = "added";
    public static final String REMOVED = "removed";
    public static final String CHANGED = "changed";
    public static final String UNCHANGED = "unchanged";

    public Map<String, String> buildStatusByState(ChangeDetectionResult result) {
        Objects.requireNonNull(result, "result");

        Map<String, String> statusByState = new HashMap<String, String>();
        for (DeltaState s : result.getAddedStates()) {
            statusByState.put(s.getStateId(), ADDED);
        }
        for (DeltaState s : result.getRemovedStates()) {
            statusByState.put(s.getStateId(), REMOVED);
        }
        for (String id : result.getChangedStates().keySet()) {
            statusByState.putIfAbsent(id, CHANGED);
        }
        for (String id : result.getChangedActions().keySet()) {
            if (result.getChangedActions().get(id) != null && !result.getChangedActions().get(id).isEmpty()) {
                statusByState.putIfAbsent(id, CHANGED);
            }
        }

        return statusByState;
    }

}
