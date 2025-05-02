package org.testar.statemodel.analysis.condition;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testar.monkey.alayer.Tag;
import org.testar.statemodel.analysis.condition.TestCondition.ConditionComparator;

/**
 * Check condition evaluator that returns true when 'Check:' conditions are met in a given state
 */
public class CheckConditionEvaluator extends BasicConditionEvaluator {

	protected static final Logger logger = LogManager.getLogger();

	public CheckConditionEvaluator(Tag<?> evaluatorTag, String checkContent) {
		this(evaluatorTag, checkContent, TestCondition.ConditionComparator.GREATER_THAN, 0);
	}

	public CheckConditionEvaluator(Tag<?> evaluatorTag, String checkContent, ConditionComparator comparator, int threshold) {
		// Replace line breaks and split the 'Check:' content into lines (case-insensitive splitting)
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
