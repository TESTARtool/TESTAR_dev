/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.scriptless.capability;

import org.testar.config.ConfigTags;
import org.testar.core.state.State;
import org.testar.core.tag.Tags;
import org.testar.core.verdict.Verdict;
import org.testar.scriptless.ComposedProtocol;

import java.util.Collections;
import java.util.List;

/**
 * Shared sequence and session stop criteria for protocol implementations.
 */
public final class StopCriteriaCapability {

    public boolean moreActions(ComposedProtocol protocol, State state) {
        List<Verdict> stateVerdicts = state.get(Tags.OracleVerdicts, Collections.singletonList(Verdict.OK));
        boolean faultySequence = !Verdict.helperAreAllVerdictsOK(stateVerdicts);

        return (!protocol.settings().get(ConfigTags.StopGenerationOnFault) || !faultySequence)
                && state.get(Tags.IsRunning, false)
                && !state.get(Tags.NotResponding, false)
                && protocol.runtimeContext().actionCount() <= protocol.settings().get(ConfigTags.SequenceLength)
                && (org.testar.core.util.Util.time() - protocol.runtimeContext().startTime()) < protocol.settings().get(ConfigTags.MaxTime);
    }

    public boolean moreSequences(ComposedProtocol protocol) {
        return protocol.runtimeContext().sequenceCount() <= protocol.settings().get(ConfigTags.Sequences)
                && (org.testar.core.util.Util.time() - protocol.runtimeContext().startTime()) < protocol.settings().get(ConfigTags.MaxTime);
    }
}
