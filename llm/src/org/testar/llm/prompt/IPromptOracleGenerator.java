/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2024-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2024-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.llm.prompt;

import org.testar.core.state.State;

/**
 * Interface for prompt generators for test oracles with llms.
 */
public interface IPromptOracleGenerator {

    boolean attachImage();

    /**
     * Generates a prompt for test oracles with large language models.
     * @param state The current state.
     * @param appName The name of the SUT.
     * @param currentTestGoal The current test goal.
     * @param previousTestGoal The previous test goal if one was completed, ignored if empty string.
     * @return The generated prompt.
     */
    String generateOraclePrompt(State state, String appName, String currentTestGoal, String previousTestGoal);

}
