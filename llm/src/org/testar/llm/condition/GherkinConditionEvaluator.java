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
 * Gherkin condition evaluator that returns true when 'Then' conditions are met in a given state
 */
public class GherkinConditionEvaluator extends BasicConditionEvaluator {

    protected static final Logger logger = LogManager.getLogger();

    public GherkinConditionEvaluator(Tag<?> evaluatorTag, String gherkinContent) {
        this(evaluatorTag, gherkinContent, TestCondition.ConditionComparator.GREATER_THAN, 0);
    }

    public GherkinConditionEvaluator(Tag<?> evaluatorTag, String gherkinContent, ConditionComparator comparator, int threshold) {
        // Replace line breaks and split the Gherkin content into lines (case-insensitive splitting)
        String[] lines = gherkinContent.replaceAll("(\\r|\\n|\\\\n)", "\n").split("(?i)(?=\\b(Scenario|Given|When|Then|And)\\b)");

        // Add the Gherkin 'Then' statements as evaluator conditions
        for (String lineStatement : lines) {
            // Remove leading and trailing spaces
            String strippedStatement = lineStatement.strip();

            // Check if the line starts with "Then" (case-insensitive)
            if (strippedStatement.toLowerCase().startsWith("then")) {
                // Remove the "Then" keyword and trim the remaining line
                String searchStatement = strippedStatement.substring(4).strip();
                // Add the condition to the evaluator
                logger.log(Level.INFO, String.format("GherkinConditionEvaluator: %s", searchStatement));
                addCondition(new StateCondition(evaluatorTag.name(), searchStatement, comparator, threshold));
            }
        }

        if(getConditions().isEmpty()) {
        	logger.log(Level.WARN, String.format("GherkinConditionEvaluator, no valid 'Then' statements found in content: %s", gherkinContent));
        }
    }

}
