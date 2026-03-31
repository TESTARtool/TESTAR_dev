/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2024-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2024-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.llm.prompt;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testar.android.tag.AndroidTags;
import org.testar.core.state.State;
import org.testar.core.tag.Tag;
import org.testar.core.state.Widget;
import org.testar.core.exceptions.NoSuchTagException;

public class OracleAndroidPromptGenerator implements IPromptOracleGenerator {

    protected static final Logger logger = LogManager.getLogger();

    private final Set<Tag<String>> oracleTags;
    private final boolean attachImage;

    /**
     * Creates a new oracle prompt generator for Android applications.
     */
    public OracleAndroidPromptGenerator() {
        this(false);
    }

    /**
     * Creates a new oracle prompt generator for Android applications with a set of oracleTags. 
     * @param attachImage Indicate if an image should be attached together with the text prompt. 
     */
    public OracleAndroidPromptGenerator(boolean attachImage) {
        this(new HashSet<>(Arrays.asList(AndroidTags.AndroidText)), attachImage); // AndroidTags.AndroidText is the default widget context
    }

    /**
     * Creates a new oracle prompt generator for Android applications with a set of oracleTags. 
     * @param oracleTags Are the tags to be used for applying the oracle. 
     */
    public OracleAndroidPromptGenerator(Set<Tag<String>> oracleTags) {
        this(oracleTags, false); // Do not attach an image by default
    }

    /**
     * Creates a new oracle prompt generator for Android applications with a set of oracleTags. 
     * @param oracleTags Are the tags to be used for applying the oracle. 
     * @param attachImage Indicate if an image should be attached together with the text prompt. 
     */
    public OracleAndroidPromptGenerator(Set<Tag<String>> oracleTags, boolean attachImage) {
        this.oracleTags = oracleTags;
        this.attachImage = attachImage;
    }

    @Override
    public boolean attachImage() {
        return this.attachImage;
    }

    @Override
    public String generateOraclePrompt(State state, String appName, String currentTestGoal, String previousTestGoal) {

        StringBuilder builder = new StringBuilder();
        builder.append(String.format("We are testing the \"%s\" Android application. ", appName));

        if(StringUtils.isEmpty(previousTestGoal)) {
            builder.append(String.format("The objective of the test is: %s. ", currentTestGoal));
        } else {
            builder.append(String.format("The following objective was previously achieved: %s. ", previousTestGoal));
            builder.append(String.format("The current objective of the test is: %s. ", currentTestGoal));
        }

        builder.append("The current state of the application contains the widgets: ");

        for (Widget widget : state) {
            // With Appium, only visible Android elements are fetch
            try {
                // Iterate trough the indicated widget Tags to create the widget content
                String widgetContent = "";

                for(Tag<String> tag : oracleTags) {
                    widgetContent = widgetContent.concat(widget.get(tag, "") + " ");
                }

                // If the widget content is not empty, add it to the Oracle conversation
                if(!widgetContent.trim().isEmpty()) {
                    builder.append(String.format("Widget: %s, ", widgetContent));
                }

            } catch(NoSuchTagException e) {
                logger.log(Level.WARN, "Widget Tag is missing, skipping.");
            }
        }

        builder.append(". ");

        if(attachImage) {
            builder.append("An image of the current state is attached. ");
        }

        builder.append("Is the test objective met in this state?");

        return builder.toString();
    }

}
