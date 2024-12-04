package org.testar.statemodel.analysis.condition;

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
}
