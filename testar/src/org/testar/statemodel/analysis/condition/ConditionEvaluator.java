package org.testar.statemodel.analysis.condition;

import org.testar.statemodel.StateModelManager;

import java.util.ArrayList;
import java.util.List;

public abstract class ConditionEvaluator {
    private List<TestCondition> conditions;

    public ConditionEvaluator() {
        conditions = new ArrayList<>();
    }

   public void addCondition(TestCondition condition) {
        conditions.add(condition);
   }

    public abstract boolean evaluateConditions(String modelIdentifier, StateModelManager stateModelManager);

    public List<TestCondition> getConditions() {
        return conditions;
    }
}
