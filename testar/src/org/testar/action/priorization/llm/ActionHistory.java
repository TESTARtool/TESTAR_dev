package org.testar.action.priorization.llm;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.Widget;
import org.testar.monkey.alayer.actions.WdRemoteTypeAction;

import java.util.ArrayList;

/**
 * Action history for keeping track of actions that were previously executed.
 */
public class ActionHistory {
    protected static final Logger logger = LogManager.getLogger();

    private ArrayList<Action> actions;
    private int maxEntries;

    /**
     * Creates a new ActionHistory.
     * @param maxEntries Max amount of actions to keep track of.
     */
    public ActionHistory(int maxEntries) {
        actions = new ArrayList<>();
        this.maxEntries = maxEntries;
    }

    public ArrayList<Action> getActions() {
        return actions;
    }

    /**
     * Adds an action to the action history. Removes the first entry if maxEntries is exceeded.
     * @param action Action to add.
     */
    public void addToHistory(Action action) {
        if(actions.size() == maxEntries) {
            actions.remove(0);
        }

        actions.add(action);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        if(actions.size() == 1) {
            builder.append("This is the last action we executed: ");
        } else {
            builder.append(String.format("These are the last %d actions we executed: ", actions.size()));
        }

        int i = 1;
        for (var action : actions) {
            if(i != 1) {
                builder.append(", ");
            }
            String type = action.get(Tags.Role).name();
            Widget widget = action.get(Tags.OriginWidget);
            String description = widget.get(Tags.Desc, "Unknown Widget");

            switch(type) {
                case "RemoteType":
                    WdRemoteTypeAction typeAction = (WdRemoteTypeAction)action;
                    String input = typeAction.getKeys().toString();
                    // TODO: Differentiate between types of input fields (numeric, password, etc.)
                    builder.append(String.format("%d: Typed '%s' in TextField '%s'", i, input, description));
                    break;
                case "RemoteClick":
                    builder.append(String.format("%d: Clicked on '%s'", i, description));
                    break;
                default:
                    logger.log(Level.WARN, "Unsupported action type for action history: " + type);
                    break;
            }
            i++;
        }
        builder.append(". ");

        return builder.toString();
    }
}
