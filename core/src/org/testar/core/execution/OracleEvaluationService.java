/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.core.execution;

import java.util.List;

import org.testar.core.state.State;
import org.testar.core.verdict.Verdict;

public interface OracleEvaluationService {

    List<Verdict> getVerdicts(State state);

    void addVerdict(State state, Verdict verdict);
}
