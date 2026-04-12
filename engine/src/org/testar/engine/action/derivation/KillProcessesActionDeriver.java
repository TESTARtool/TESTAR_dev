/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.engine.action.derivation;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.testar.core.Assert;
import org.testar.core.Pair;
import org.testar.core.action.Action;
import org.testar.core.action.KillProcess;
import org.testar.core.state.SUT;
import org.testar.core.state.State;
import org.testar.core.tag.Tags;
import org.testar.engine.policy.SessionPolicyContext;

/**
 * Produces a forced kill-process action for matching non-SUT processes.
 */
public final class KillProcessesActionDeriver implements ActionDeriver {

    private final Pattern processPattern;

    public KillProcessesActionDeriver(String processRegex) {
        Assert.notNull(processRegex);
        this.processPattern = Pattern.compile(processRegex);
    }

    @Override
    public Set<Action> derive(SUT system, State state, SessionPolicyContext context) {
        Set<Action> actions = new LinkedHashSet<>();
        if (system == null) {
            return Collections.emptySet();
        }
        Long sutPid = system.get(Tags.PID, null);
        List<Pair<Long, String>> runningProcesses = system.getRunningProcesses();
        state.set(Tags.RunningProcesses, runningProcesses == null
                ? Collections.<Pair<Long, String>>emptyList()
                : runningProcesses);
        if (runningProcesses == null) {
            return Collections.emptySet();
        }
        for (Pair<Long, String> process : runningProcesses) {
            if (process != null
                    && process.left() != null
                    && process.right() != null
                    && (sutPid == null || process.left().longValue() != sutPid.longValue())
                    && processPattern.matcher(process.right()).matches()) {
                Action action = KillProcess.byName(process.right(), 0);
                action.set(Tags.Desc, "Kill Process with name '" + process.right() + "'");
                action.mapOriginWidget(state);
                actions.add(action);
                return Collections.unmodifiableSet(actions);
            }
        }
        return Collections.emptySet();
    }
}
