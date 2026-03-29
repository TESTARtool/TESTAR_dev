/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2021-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2021-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.reporting;

import java.util.List;
import java.util.Set;

import org.testar.core.action.Action;
import org.testar.core.state.State;
import org.testar.core.verdict.Verdict;

public interface Reporting {

    void addState(State state);

    void addActions(Set<Action> actions);

    void addActionsAndUnvisitedActions(Set<Action> actions, Set<String> concreteIdsOfUnvisitedActions);

    void addSelectedAction(State state, Action action);

    void addTestVerdicts(List<Verdict> verdicts);

    void finishReport();
}
