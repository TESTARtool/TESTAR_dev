/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.scriptless.capability;

import org.testar.config.ConfigTags;
import org.testar.core.state.State;
import org.testar.core.tag.Tags;
import org.testar.core.util.Util;
import org.testar.core.verdict.Verdict;
import org.testar.scriptless.RuntimeContext;

import java.util.Collections;
import java.util.List;

public final class StopCriteriaCapability {

    public boolean stopTestSequence(RuntimeContext runtimeContext, State state) {
        List<Verdict> stateVerdicts = state.get(Tags.OracleVerdicts, Collections.singletonList(Verdict.OK));
        boolean faultySequence = !Verdict.helperAreAllVerdictsOK(stateVerdicts);

        return (!runtimeContext.settings().get(ConfigTags.StopGenerationOnFault) || !faultySequence)
                && state.get(Tags.IsRunning, false)
                && !state.get(Tags.NotResponding, false)
                && runtimeContext.actionCount() <= runtimeContext.settings().get(ConfigTags.SequenceLength)
                && (Util.time() - runtimeContext.startTime()) < runtimeContext.settings().get(ConfigTags.MaxTime);
    }

    public boolean stopTestSession(RuntimeContext runtimeContext) {
        return runtimeContext.sequenceCount() <= runtimeContext.settings().get(ConfigTags.Sequences)
                && (Util.time() - runtimeContext.startTime()) < runtimeContext.settings().get(ConfigTags.MaxTime);
    }
}
