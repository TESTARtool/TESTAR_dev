/***************************************************************************************************
 *
 * Copyright (c) 2024 - 2025 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2024 - 2025 Open Universiteit - www.ou.nl
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
