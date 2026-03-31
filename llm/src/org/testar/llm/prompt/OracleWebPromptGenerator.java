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
import org.testar.core.state.State;
import org.testar.core.tag.Tag;
import org.testar.webdriver.tag.WdTags;
import org.testar.core.state.Widget;
import org.testar.core.exceptions.NoSuchTagException;

public class OracleWebPromptGenerator implements IPromptOracleGenerator {

	protected static final Logger logger = LogManager.getLogger();

	private final Set<Tag<String>> oracleTags;
	private final boolean attachImage;

	/**
	 * Creates a new oracle prompt generator for web applications.
	 */
	public OracleWebPromptGenerator() {
		this(false); 
	}

	/**
	 * Creates a new oracle prompt generator for web applications with a set of oracleTags.
	 * @param attachImage Indicate if an image should be attached together with the text prompt.
	 */
	public OracleWebPromptGenerator(boolean attachImage) {
		this(new HashSet<>(Arrays.asList(WdTags.WebTextContent)), attachImage); // WdTags.WebTextContent is the default widget context
	}

	/**
	 * Creates a new oracle prompt generator for web applications with a set of oracleTags. 
	 * @param oracleTags Are the tags to be used for applying the oracle.
	 */
	public OracleWebPromptGenerator(Set<Tag<String>> oracleTags) {
		this(oracleTags, false); // Do not attach an image by default
	}

	/**
	 * Creates a new oracle prompt generator for web applications with a set of oracleTags. 
	 * @param oracleTags Are the tags to be used for applying the oracle.
	 * @param attachImage Indicate if an image should be attached together with the text prompt.
	 */
	public OracleWebPromptGenerator(Set<Tag<String>> oracleTags, boolean attachImage) {
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
		builder.append(String.format("We are testing the \"%s\" web application. ", appName));

		if(StringUtils.isEmpty(previousTestGoal)) {
			builder.append(String.format("The objective of the test is: %s. ", currentTestGoal));
		} else {
			builder.append(String.format("The following objective was previously achieved: %s. ", previousTestGoal));
			builder.append(String.format("The current objective of the test is: %s. ", currentTestGoal));
		}

		String pageTitle = state.get(WdTags.WebTitle, "");
		builder.append(String.format("We are currently on the following page: %s. ", pageTitle));

		builder.append("The current state of the application contains the widgets: ");

		for (Widget widget : state) {
			// Only create the prompt oracle with the information of visible widgets
			// Some widgets may exist at the DOM level but are not visible/displayed in the GUI
			if(widget.get(WdTags.WebIsFullOnScreen, false) && widget.get(WdTags.WebIsDisplayed, false)) {
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
		}

		builder.append(". ");

		if(attachImage) {
			builder.append("An image of the current state is attached. ");
		}

		builder.append("Is the test objective met in this state?");

		return builder.toString();
	}

}
