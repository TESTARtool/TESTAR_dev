package org.testar.action.priorization.llm;

import org.testar.statemodel.analysis.condition.TestCondition;

import java.util.List;

/**
 * Contains a test goal for the llm to complete along with the conditions under which the test goal is considered
 * completed. TODO: Rework GUI to support this new structure.
 */
public class LlmTestGoal {
    private String testGoal;
    private List<TestCondition> completionConditions;

    /**
     * Creates a new test goal object.
     * @param testGoal The instructions for the llm to execute a task.
     * @param completionConditions The conditions under which the task is considered completed.
     */
    public LlmTestGoal(String testGoal, List<TestCondition> completionConditions) {
        this.testGoal = testGoal;
        this.completionConditions = completionConditions;
    }

    public String getTestGoal() {
        return testGoal;
    }

    public List<TestCondition> getCompletionConditions() {
        return completionConditions;
    }
}
