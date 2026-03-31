/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2024-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2024-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.llm;

import org.testar.llm.condition.TestCondition;

import java.util.List;

/**
 * Contains a test goal for the llm to complete along with the conditions under which the test goal is considered
 * completed.
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
