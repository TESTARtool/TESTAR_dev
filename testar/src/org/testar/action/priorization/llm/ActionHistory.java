package org.testar.action.priorization.llm;

import org.testar.monkey.Pair;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.Widget;

import java.util.ArrayList;

public class ActionHistory {
    private ArrayList<Action> actions;
    private int maxEntries;

    public ActionHistory(int maxEntries) {
        actions = new ArrayList<>();
        this.maxEntries = maxEntries;
    }

    public ArrayList<Action> getActions() {
        return actions;
    }

    public void addToHistory(Action action) {
        if(actions.size() == maxEntries) {
            actions.remove(0);
        }

        actions.add(action);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("Last %d actions taken (higher is newer): ", actions.size()));

        int i = 1;
        for (var pair : actions) {
            // TODO: Rework for new prompting.
            i++;
        }

        return builder.toString();
    }
}
