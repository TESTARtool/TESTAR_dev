/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.engine.service;

import org.testar.core.Assert;
import org.testar.core.service.OracleEvaluationService;
import org.testar.core.state.SUT;
import org.testar.core.state.State;
import org.testar.core.tag.Tags;
import org.testar.core.verdict.Verdict;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class ComposedOracleEvaluationService implements OracleEvaluationService {

    private final List<OracleEvaluationService> oracleEvaluationServices;

    public ComposedOracleEvaluationService(List<OracleEvaluationService> oracleEvaluationServices) {
        Assert.notNull(oracleEvaluationServices);
        Assert.isTrue(!oracleEvaluationServices.isEmpty());
        this.oracleEvaluationServices = Collections.unmodifiableList(new ArrayList<OracleEvaluationService>(oracleEvaluationServices));
    }

    public static ComposedOracleEvaluationService compose(OracleEvaluationService... oracleEvaluationServices) {
        Assert.notNull(oracleEvaluationServices);
        return new ComposedOracleEvaluationService(Arrays.asList(oracleEvaluationServices));
    }

    @Override
    public List<Verdict> getVerdicts(SUT system, State state) {
        Assert.notNull(state);

        List<Verdict> verdicts = new ArrayList<Verdict>();
        for (OracleEvaluationService oracleEvaluationService : oracleEvaluationServices) {
            List<Verdict> serviceVerdicts = oracleEvaluationService.getVerdicts(system, state);
            if (serviceVerdicts != null) {
                verdicts.addAll(serviceVerdicts);
            }
        }

        normalizeVerdicts(verdicts);
        state.set(Tags.OracleVerdicts, verdicts);
        return verdicts;
    }

    @Override
    public void addVerdict(State state, Verdict verdict) {
        Assert.notNull(state, verdict);

        List<Verdict> verdicts = state.get(Tags.OracleVerdicts, new ArrayList<Verdict>());
        if (verdicts.size() == 1 && verdicts.contains(Verdict.OK)) {
            verdicts = new ArrayList<Verdict>();
        }

        verdicts.add(verdict);
        state.set(Tags.OracleVerdicts, verdicts);
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
