package org.testar.action.priorization.llm;

import org.testar.monkey.Pair;
import org.testar.monkey.alayer.Action;

import java.util.ArrayList;

public class ActionHistory {
    private ArrayList<Pair<Action, String>> actions;
    private int maxEntries;

    public ActionHistory(int maxEntries) {
        actions = new ArrayList<>();
        this.maxEntries = maxEntries;
    }

    public ArrayList<Pair<Action, String>> getActions() {
        return actions;
    }

    public void addToHistory(Action action, String parameters) {
        if(actions.size() == maxEntries) {
            actions.remove(0);
        }

        actions.add(new Pair<Action, String>(action, parameters));
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("These were the last %d actions taken: ", actions.size()));

        // TODO: Finish

        return super.toString();
    }
}
