package org.testar.action.priorization.llm.prompt;

import org.testar.action.priorization.llm.ActionHistory;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;

import java.util.Set;

/**
 * Interface for prompt generators for llms.
 */
public interface IPromptGenerator {
    /**
     * Generates a prompt for use with large language models.
     * @param actions Available actions in the current state.
     * @param state The current state.
     * @param history The action history.
     * @param appName The name of the SUT.
     * @param currentTestGoal The current test goal.
     * @param previousTestGoal The previous test goal if one was completed, ignored if empty string.
     * @return The generated prompt.
     */
    String generatePrompt(Set<Action> actions, State state, ActionHistory history,
                          String appName, String currentTestGoal, String previousTestGoal);
}
