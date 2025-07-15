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

package org.testar.llm.prompt;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testar.action.priorization.llm.ActionHistory;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tag;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.Widget;
import org.testar.monkey.alayer.exceptions.NoSuchTagException;

import java.util.Set;

/**
 * Standard prompt generator for OpenAI and Gemini.
 * Includes the following information:
 * 1. The name of the SUT.
 * 2. The current test goal and previous test goal if given.
 * 3. The page title.
 * 4. The list of available actions.
 * 5. Action history.
 */
public class ActionStandardPromptGenerator implements IPromptActionGenerator {
    protected static final Logger logger = LogManager.getLogger();

    private final Tag<String> descriptionTag;

    /**
     * Creates a new standard prompt generator.
     */
    public ActionStandardPromptGenerator() {
        this(Tags.Desc); // Tags.Desc is the default description Tag
    }

    /**
     * Creates a new standard prompt generator with a specific descriptionTag
     * @param descriptionTag The tag to be used for obtaining the action/widget description.
     */
    public ActionStandardPromptGenerator(Tag<String> descriptionTag) {
        this.descriptionTag = descriptionTag;
    }

    @Override
    public Tag<String> getDescriptionTag() {
        return this.descriptionTag;
    }

    /**
     * Generates a standard prompt for use with large language models.
     * @param actions Available actions in the current state.
     * @param state The current state.
     * @param history The action history.
     * @param appName The name of the SUT.
     * @param currentTestGoal The current test goal.
     * @param previousTestGoal The previous test goal if one was completed, ignored if empty string.
     * @return The generated prompt.
     */
    @Override
    public String generateActionSelectionPrompt(Set<Action> actions, State state, ActionHistory history,
            String appName, String currentTestGoal, String previousTestGoal) {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("We are testing the \"%s\" GUI application. ", appName));

        if(StringUtils.isEmpty(previousTestGoal)) {
            builder.append(String.format("The objective of the test is: %s. ", currentTestGoal));
        } else {
            builder.append(String.format("The following objective was previously achieved: %s. ", previousTestGoal));
            builder.append(String.format("The current objective of the test is: %s. ", currentTestGoal));
        }

        builder.append("The following actions are available: ");

        for (Action action : actions) {
            try {
                Widget widget = action.get(Tags.OriginWidget);
                String type = action.get(Tags.Role).name();
                String actionId = action.get(Tags.AbstractID, "Unknown ActionId");
                String description = widget.get(descriptionTag, "");

                switch (type) {
                case "ClickTypeInto":
                case "PasteTextInto":
                    builder.append(String.format("%s: Type in Field '%s' ", actionId, description));
                    break;
                case "LeftClickAt":
                    builder.append(String.format("%s: Click on '%s' ", actionId, description));
                    break;
                case "HitESC":
                    // TODO: Decide if it makes sense to rely on the LLM to make this control decision
                    builder.append(String.format("%s: Hit the ESC key", actionId));
                    break;
                default:
                    logger.log(Level.WARN, "Unsupported action type for LLM action selection: " + type);
                    break;
                }

            } catch(NoSuchTagException e) {
                // This usually happens when OriginWidget is unknown, so we skip these.
                logger.log(Level.WARN, "Action is missing critical tags, skipping.");
            }

            builder.append(", ");
        }

        builder.append(". ");

        if(!history.getActions().isEmpty()) {
            builder.append(history.toString());
        }

        builder.append("Which action should be executed to accomplish the test goal?");

        return builder.toString();
    }

}
