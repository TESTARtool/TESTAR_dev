package org.testar.statemodel.analysis.condition;

import org.testar.statemodel.StateModelManager;

// Returns true when all conditions are met in a given state
public class BasicConditionEvaluator extends ConditionEvaluator {

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
