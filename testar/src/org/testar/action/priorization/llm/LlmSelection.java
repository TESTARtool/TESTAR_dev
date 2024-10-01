package org.testar.action.priorization.llm;

/**
 * This is the syntax we want the LLM to respond with.
 * actionId refers to the action to take.
 * input refers to the characters to enter into the widget if applicable.
 */
public class LlmSelection {
    private int actionId;
    private String input;

    public LlmSelection(int actionId, String input) {
        this.actionId = actionId;
        this.input = input;
    }

    public int getActionId() {
        return actionId;
    }

    public String getInput() {
        return input;
    }
}
