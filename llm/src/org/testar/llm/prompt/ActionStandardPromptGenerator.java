/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2024-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2024-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.llm.prompt;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testar.llm.action.ActionHistory;
import org.testar.core.action.Action;
import org.testar.core.state.State;
import org.testar.core.tag.Tag;
import org.testar.core.tag.Tags;
import org.testar.core.state.Widget;
import org.testar.core.exceptions.NoSuchTagException;

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
    private final boolean attachImage;

    /**
     * Creates a new standard prompt generator.
     */
    public ActionStandardPromptGenerator() {
        this(Tags.Desc); // Tags.Desc is the default description Tag
    }

    /**
     * Creates a new standard prompt generator.
     * @param attachImage Indicate if an image should be attache together with the text prompt.
     */
    public ActionStandardPromptGenerator(boolean attachImage) {
        this(Tags.Desc, attachImage); // Tags.Desc is the default description Tag
    }

    /**
     * Creates a new standard prompt generator with a specific descriptionTag
     * @param descriptionTag The tag to be used for obtaining the action/widget description.
     */
    public ActionStandardPromptGenerator(Tag<String> descriptionTag) {
        this(descriptionTag, false); // Do not attach an image by default
    }

    /**
     * Creates a new standard prompt generator with a specific descriptionTag
     * @param descriptionTag The tag to be used for obtaining the action/widget description.
     * @param attachImage Indicate if an image should be attache together with the text prompt.
     */
    public ActionStandardPromptGenerator(Tag<String> descriptionTag, boolean attachImage) {
        this.descriptionTag = descriptionTag;
        this.attachImage = attachImage;
    }

    @Override
    public Tag<String> getDescriptionTag() {
        return this.descriptionTag;
    }

    @Override
    public boolean attachImage() {
        return this.attachImage;
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

        if(attachImage) {
            builder.append("An image of the current state is attached so you can observe what actionable widgets are present and which actions were executed. ");
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
