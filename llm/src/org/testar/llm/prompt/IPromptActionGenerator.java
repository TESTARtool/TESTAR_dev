/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2024-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2024-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.llm.prompt;

import org.testar.llm.action.ActionHistory;
import org.testar.core.action.Action;
import org.testar.core.state.State;
import org.testar.core.tag.Tag;
import java.util.Set;

/**
 * Interface for prompt generators for action selection with llms.
 */
public interface IPromptActionGenerator {

	Tag<String> getDescriptionTag();

	boolean attachImage();

    /**
     * Generates a prompt for action selection with large language models.
     * @param actions Available actions in the current state.
     * @param state The current state.
     * @param history The action history.
     * @param appName The name of the SUT.
     * @param currentTestGoal The current test goal.
     * @param previousTestGoal The previous test goal if one was completed, ignored if empty string.
     * @return The generated prompt.
     */
    String generateActionSelectionPrompt(Set<Action> actions, State state, ActionHistory history,
                          String appName, String currentTestGoal, String previousTestGoal);
}
