package org.testar.action.priorization.llm;

import org.testar.monkey.Pair;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.Tags;

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

        actions.add(new Pair<>(action, parameters));
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("Last %d actions taken (higher is newer): ", actions.size()));

        int i = 1;
        for (var pair : actions) {
            Action action = pair.left();
            String title = action.get(Tags.Title);
            String role = action.get(Tags.Role).name();
            String parameters = pair.right();
            builder.append(String.format("%d: (%s,%s,%s). ", i, title, role, parameters));
            i++;
        }

        return builder.toString();
    }
}
