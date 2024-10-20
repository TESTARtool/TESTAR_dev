package org.testar.action.priorization.llm;

/**
 * This is the syntax we want the LLM to respond with.
 * actionId refers to the action to take.
 * input refers to the characters to enter into the widget if applicable.
 */
public class LlmSelection {
    private String actionId;
    private String input;

    public LlmSelection(String actionId, String input) {
        this.actionId = actionId;
        this.input = input;
    }

    public String getActionId() {
        return actionId;
    }

    public String getInput() {
        return input;
    }
}
