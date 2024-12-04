package org.testar.statemodel.analysis.condition;

import org.testar.statemodel.StateModelManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Condition evaluators are used to check if a given condition is true during one or more states.
 * This can for example, be used to check if certain text is present on a webpage.
 */
public abstract class ConditionEvaluator {
    private List<TestCondition> conditions;

    public ConditionEvaluator() {
        conditions = new ArrayList<>();
    }

    /**
     * Adds a new condition that will be evaluated when evaluateConditions is called.
     * @param condition New condition to add.
     */
    public void addCondition(TestCondition condition) {
        conditions.add(condition);
    }

    /**
     * Evaluates all conditions added to the ConditionEvaluator based on the state model.
     * @param modelIdentifier The modelIdentifier of the test sequence.
     * @param stateModelManager The stateModelManager of the test sequence.
     * @return True if all conditions evaluate to true.
     */
    public abstract boolean evaluateConditions(String modelIdentifier, StateModelManager stateModelManager);

    public List<TestCondition> getConditions() {
        return conditions;
    }
}
