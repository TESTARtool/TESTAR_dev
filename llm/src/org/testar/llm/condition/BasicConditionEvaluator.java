/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2024-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2024-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.llm.condition;

import org.testar.core.state.State;
import org.testar.statemodel.StateModelManager;

/**
 * Basic condition evaluator that returns true when all conditions are met in a given state
 */
public class BasicConditionEvaluator extends ConditionEvaluator {

    /**
     * Evaluates all conditions added to the ConditionEvaluator based on the state model.
     * @param modelIdentifier The modelIdentifier of the test sequence.
     * @param stateModelManager The stateModelManager of the test sequence.
     * @return True if all conditions evaluate to true.
     */
    public boolean evaluateConditions(String modelIdentifier, StateModelManager stateModelManager) {
        for(TestCondition condition : getConditions()) {
            boolean evaluation = condition.evaluate(modelIdentifier, stateModelManager);

            if(!evaluation) {
                return false;
            }
        }

        // All conditions are true
        return true;
    }

    /**
     * Evaluates all conditions added to the ConditionEvaluator based on the state.
     * @param state The current state.
     * @return True if all conditions evaluate to true.
     */
    public boolean evaluateConditions(State state) {
        for(TestCondition condition : getConditions()) {
            boolean evaluation = condition.evaluate(state);

            if(!evaluation) {
                return false;
            }
        }

        // All conditions are true
        return true;
    }
}
