package org.testar.statemodel.analysis.condition;

import org.testar.monkey.alayer.Tag;
import org.testar.statemodel.analysis.condition.TestCondition.ConditionComparator;

/**
 * Gherkin condition evaluator that returns true when 'Then' conditions are met in a given state
 */
public class GherkinConditionEvaluator extends BasicConditionEvaluator {

    public GherkinConditionEvaluator(Tag<?> evaluatorTag, String gherkinContent) {
        this(evaluatorTag, gherkinContent, TestCondition.ConditionComparator.GREATER_THAN, 1);
    }

    public GherkinConditionEvaluator(Tag<?> evaluatorTag, String gherkinContent, ConditionComparator comparator, int threshold) {
        // Replace line breaks and split the Gherkin content into lines (case-insensitive splitting)
        String[] lines = gherkinContent.replaceAll("(\\r|\\n|\\\\n)", "").split("(?i)(?=\\b(Scenario|Given|When|Then|And)\\b)");

        // Add the Gherkin 'Then' statements as evaluator conditions
        for (String lineStatement : lines) {
            // Remove leading and trailing spaces
            String strippedStatement = lineStatement.strip();

            // Check if the line starts with "Then" (case-insensitive)
            if (strippedStatement.toLowerCase().startsWith("then")) {
                // Remove the "Then" keyword and trim the remaining line
                String searchStatement = strippedStatement.substring(4).strip();
                // Add the condition to the evaluator
                addCondition(new GherkinStateCondition(evaluatorTag.name(), searchStatement, comparator, threshold));
            }
        }
    }

}
