/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2024-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2024-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.llm.condition;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testar.core.tag.Tag;
import org.testar.llm.condition.TestCondition.ConditionComparator;

/**
 * Check condition evaluator that returns true when 'Check:' conditions are met in a given state
 */
public class CheckConditionEvaluator extends BasicConditionEvaluator {

	protected static final Logger logger = LogManager.getLogger();

	public CheckConditionEvaluator(Tag<?> evaluatorTag, String checkContent) {
		this(evaluatorTag, checkContent, TestCondition.ConditionComparator.GREATER_THAN, 0);
	}

	public CheckConditionEvaluator(Tag<?> evaluatorTag, String checkContent, ConditionComparator comparator, int threshold) {

		// Null-safe fallback checks
		if (checkContent == null) {
			logger.log(Level.WARN, "CheckConditionEvaluator: Received null test goal, treating as empty test goal.");
			checkContent = "";
		}
		if (evaluatorTag == null) {
			logger.log(Level.WARN, "CheckConditionEvaluator: Received null evaluatorTag. No condition will be added.");
			return;
		}

		// Replace line breaks and split the goal lines (case-insensitive splitting)
		String[] lines = checkContent.replaceAll("(\\r|\\n|\\\\n)", "\n").split("\n");

		// Add the'Check:' statements as evaluator conditions
		for (String lineStatement : lines) {
			// Remove leading and trailing spaces
			String strippedStatement = lineStatement.strip();

			// Check if the line starts with 'Check:' (case-insensitive)
			if (strippedStatement.toLowerCase().startsWith("check:")) {
				// Remove the 'Check:' keyword and trim the remaining line
				String searchStatement = strippedStatement.substring(6).strip();
				// Add the condition to the evaluator
				logger.log(Level.INFO, String.format("CheckConditionEvaluator: %s", searchStatement));
				addCondition(new StateCondition(evaluatorTag.name(), searchStatement, comparator, threshold));
			}
		}

		if(getConditions().isEmpty()) {
			logger.log(Level.WARN, String.format("CheckConditionEvaluator, no valid 'Check:' statements found in content: %s", checkContent));
		}
	}

}
