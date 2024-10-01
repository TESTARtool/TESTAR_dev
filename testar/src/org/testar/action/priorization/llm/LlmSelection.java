package org.testar.action.priorization.llm;

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
