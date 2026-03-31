/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2024-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2024-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.llm.action;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testar.core.action.Action;
import org.testar.core.tag.Tag;
import org.testar.core.tag.Tags;
import org.testar.core.state.Widget;
import org.testar.core.action.CompoundAction;
import org.testar.core.action.PasteText;
import org.testar.core.action.Type;
import org.testar.webdriver.action.WdRemoteTypeAction;
import org.testar.webdriver.action.WdSelectListAction;

import java.util.ArrayList;

/**
 * Action history for keeping track of actions that were previously executed.
 */
public class ActionHistory {
    protected static final Logger logger = LogManager.getLogger();

    private ArrayList<Action> actions;
    private int maxEntries;
    private final Tag<String> descriptionTag;

    /**
     * Creates a new ActionHistory.
     * @param maxEntries Max amount of actions to keep track of.
     */
    public ActionHistory(int maxEntries) {
        this(maxEntries, Tags.Desc);
    }

    /**
     * Creates a new ActionHistory.
     * @param maxEntries Max amount of actions to keep track of.
     * @param descriptionTag The tag to be used for obtaining the action/widget description.
     */
    public ActionHistory(int maxEntries, Tag<String> descriptionTag) {
        actions = new ArrayList<>();
        this.maxEntries = maxEntries;
        this.descriptionTag = descriptionTag;
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

        for (var action : actions) {
            Widget widget = action.get(Tags.OriginWidget);
            String type = action.get(Tags.Role).name();
            String description = widget.get(descriptionTag, "Unknown Widget");

            if(action instanceof WdSelectListAction) {
                // Special case for combobox/select list actions
                WdSelectListAction selectListAction = (WdSelectListAction) action;
                String selectWidget = selectListAction.getTarget();
                String value = selectListAction.getValue();
                builder.append(String.format("Set value of ComboBox '%s' to '%s'", selectWidget, value));
            } else {
                switch(type) {
                    case "ClickTypeInto":
                    case "PasteTextInto":
                    case "RemoteType":
                    case "RemoteScrollType":
                        String input = getCompoundActionInputText(action);
                        // TODO: Differentiate between types of input fields (numeric, password, etc.)
                        builder.append(String.format("Typed '%s' in TextField '%s'", input, description));
                        break;
                    case "LeftClickAt":
                    case "RemoteClick":
                    case "RemoteScrollClick":
                        builder.append(String.format("Clicked on '%s'", description));
                        break;
                    case "HistoryBackScript":
                        // TODO: Decide if it makes sense to rely on the LLM to make this control decision
                        builder.append("Go History back in the browser");
                        break;
                    case "CloseTabScript":
                        // TODO: Decide if it makes sense to rely on the LLM to make this control decision
                        builder.append("Close current browser tab");
                        break;
                    case "HitESC":
                        // TODO: Decide if it makes sense to rely on the LLM to make this control decision
                        builder.append("Hit the ESC key");
                        break;
                    default:
                        logger.log(Level.WARN, "Unsupported action type for action history: " + type);
                        break;
                }
            }

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
                    return ((Type)innerAction).get(Tags.InputText, "Unknown Type Input");
                }

                if(innerAction instanceof PasteText) {
                    return ((PasteText)innerAction).get(Tags.InputText, "Unknown Paste Input");
                }
            }
        }

        if(action instanceof WdRemoteTypeAction) {
            CharSequence input = ((WdRemoteTypeAction) action).getKeys();
            return input == null ? "Unknown Action Input" : input.toString();
        }

        return action.get(Tags.InputText, "Unknown Action Input");
    }
}
