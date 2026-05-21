/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.engine.service;

import java.util.ArrayList;
import java.util.List;

import org.testar.core.Assert;
import org.testar.core.service.OracleEvaluationService;
import org.testar.core.state.SUT;
import org.testar.core.state.State;
import org.testar.core.tag.Tags;
import org.testar.core.verdict.Verdict;
import org.testar.engine.oracle.OracleEvaluationPlan;

public final class ComposedOracleEvaluationService implements OracleEvaluationService {

    private final OracleEvaluationPlan plan;

    public ComposedOracleEvaluationService(OracleEvaluationPlan plan) {
        this.plan = Assert.notNull(plan);
    }

    public static ComposedOracleEvaluationService compose(OracleEvaluationPlan plan) {
        return new ComposedOracleEvaluationService(plan);
    }

    @Override
    public List<Verdict> getVerdicts(SUT system, State state) {
        Assert.notNull(state);

        List<Verdict> verdicts = plan.oracleEvaluationService().getVerdicts(system, state);
        if (verdicts == null) {
            verdicts = new ArrayList<Verdict>();
        }

        normalizeVerdicts(verdicts);
        state.set(Tags.OracleVerdicts, verdicts);
        return verdicts;
    }

    @Override
    public void addVerdict(State state, Verdict verdict) {
        plan.oracleEvaluationService().addVerdict(state, verdict);
    }

    private void normalizeVerdicts(List<Verdict> verdicts) {
        if (verdicts.isEmpty()) {
            verdicts.add(Verdict.OK);
            return;
        }

        if (verdicts.size() > 1) {
            verdicts.removeIf(verdict -> verdict.severity() == Verdict.OK.severity());
        }
    }
}
