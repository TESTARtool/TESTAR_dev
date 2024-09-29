package org.testar.action.priorization.llm;

public class LlmSelection {
    private int id;
    private String input;

    public LlmSelection(int id, String input) {
        this.id = id;
        this.input = input;
    }

    public int getId() {
        return id;
    }

    public String getInput() {
        return input;
    }
}
