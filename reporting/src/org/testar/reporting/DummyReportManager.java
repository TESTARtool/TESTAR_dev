/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2025-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2025-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.reporting;

import java.util.List;
import java.util.Set;

import org.testar.core.action.Action;
import org.testar.core.state.State;
import org.testar.core.verdict.Verdict;

public class DummyReportManager implements Reporting {

    @Override
    public void addState(State state) {

    }

    @Override
    public void addActions(Set<Action> actions) {

    }

    @Override
    public void addActionsAndUnvisitedActions(Set<Action> actions, Set<String> concreteIdsOfUnvisitedActions) {

    }

    @Override
    public void addSelectedAction(State state, Action action) {

    }

    @Override
    public void addTestVerdicts(List<Verdict> verdicts) {

    }

    @Override
    public void finishReport() {

    }

}
