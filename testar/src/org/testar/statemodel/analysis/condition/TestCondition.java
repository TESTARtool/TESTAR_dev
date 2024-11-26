package org.testar.statemodel.analysis.condition;

import org.testar.statemodel.StateModelManager;

// Conditions are used to evaluate whether a test goal was reached when testing with LLMs.
public abstract class TestCondition {
    private StateCondition.ConditionComparator comparator;
    private int threshold;

    public TestCondition(StateCondition.ConditionComparator comparator, int threshold) {
        this.comparator = comparator;
        this.threshold = threshold;
    }

    public enum ConditionComparator {
        GREATER_THAN,
        EQUAL,
        LESS_THAN,
        GREATER_THAN_EQUALS,
        LESS_THAN_EQUALS
    }

    public StateCondition.ConditionComparator getComparator() {
        return comparator;
    }

    public int getThreshold() {
        return threshold;
    }

    public abstract boolean evaluate(String modelIdentifier, StateModelManager stateModelManager);
}
