/***************************************************************************************************
 *
 * Copyright (c) 2024 - 2025 Open Universiteit - www.ou.nl
 * Copyright (c) 2024 - 2025 Universitat Politecnica de Valencia - www.upv.es
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************************************/

package org.testar.action.priorization.llm;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.Tag;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.Widget;
import org.testar.monkey.alayer.actions.CompoundAction;
import org.testar.monkey.alayer.actions.PasteText;
import org.testar.monkey.alayer.actions.Type;
import org.testar.monkey.alayer.actions.WdSelectListAction;

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
            String actionId = action.get(Tags.AbstractID, "Unknown ActionId");
            String description = widget.get(descriptionTag, "Unknown Widget");

            if(action instanceof WdSelectListAction) {
                // Special case for combobox/select list actions
                WdSelectListAction selectListAction = (WdSelectListAction) action;
                String selectWidget = selectListAction.getTarget();
                String value = selectListAction.getValue();
                builder.append(String.format("%s: Set value of ComboBox '%s' to '%s'", actionId, selectWidget, value));
            } else {
                switch(type) {
                    case "ClickTypeInto":
                    case "PasteTextInto":
                        String input = getCompoundActionInputText(action);
                        // TODO: Differentiate between types of input fields (numeric, password, etc.)
                        builder.append(String.format("%s: Typed '%s' in TextField '%s'", actionId, input, description));
                        break;
                    case "LeftClickAt":
                        builder.append(String.format("%s: Clicked on '%s'", actionId, description));
                        break;
                    case "HistoryBackScript":
                        // TODO: Decide if it makes sense to rely on the LLM to make this control decision
                        builder.append(String.format("%s: Go History back in the browser", actionId));
                        break;
                    case "CloseTabScript":
                        // TODO: Decide if it makes sense to rely on the LLM to make this control decision
                        builder.append(String.format("%s: Close current browser tab", actionId));
                        break;
                    case "HitESC":
                        // TODO: Decide if it makes sense to rely on the LLM to make this control decision
                        builder.append(String.format("%s: Hit the ESC key", actionId));
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
