package org.testar.action.priorization.llm;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.Widget;
import org.testar.monkey.alayer.actions.CompoundAction;
import org.testar.monkey.alayer.actions.PasteText;
import org.testar.monkey.alayer.actions.Type;

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

    /**
     * Clears the action history.
     */
    public void clear() {
        actions.clear();
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
            Widget widget = action.get(Tags.OriginWidget);
            String type = action.get(Tags.Role).name();
            // String actionId = action.get(Tags.ConcreteID, "Unknown ActionId");
            // Using TESTAR's actionId for action history results in some LLMs mistaking historical actions
            // for available actions.
            String actionId = String.valueOf(i);
            String description = widget.get(Tags.Desc, "Unknown Widget");

            // TODO: Select list/combobox actions
            switch(type) {
                case "ClickTypeInto":
                    String input = getCompoundActionInputText(action);
                    // TODO: Differentiate between types of input fields (numeric, password, etc.)
                    builder.append(String.format("%s: Typed '%s' in TextField '%s'", actionId, input, description));
                    break;
                case "LeftClickAt":
                    builder.append(String.format("%s: Clicked on '%s'", actionId, description));
                    break;
                default:
                    logger.log(Level.WARN, "Unsupported action type for action history: " + type);
                    break;
            }

            i++;
            builder.append(", ");
        }

        builder.append(". ");

        return builder.toString();
    }

    /**
     * Temporary hack to get the text input from input actions (such as text fields).
     * Actions derived by Testar are in the form of CompoundActions
     * @param action CompoundAction.
     * @return Input text of the compound action.
     */
    private String getCompoundActionInputText(Action action) {
    	//TODO: Create single actions in protocol so this is not necessary?
    	if(action instanceof CompoundAction) {
    		for(Action innerAction : ((CompoundAction)action).getActions()) {

    			if(innerAction instanceof Type) {
    				return ((Type)innerAction).get(Tags.InputText, "Unknown Input");
    			}

    			if(innerAction instanceof PasteText) {
    				return ((PasteText)innerAction).get(Tags.InputText, "Unknown Input");
    			}
    		}
    	}

    	return "Unknown Input";
    }
}
