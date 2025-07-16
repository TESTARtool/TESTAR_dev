/***************************************************************************************************
 *
 * Copyright (c) 2025 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2025 Open Universiteit - www.ou.nl
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

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testar.monkey.alayer.Tag;
import org.testar.statemodel.analysis.condition.TestCondition.ConditionComparator;

public class TransitionConditionEvaluator extends BasicConditionEvaluator {

	protected static final Logger logger = LogManager.getLogger();

	public TransitionConditionEvaluator(Tag<?> originTag, Tag<?> actionTag, Tag<?> destTag, String checkContent) {
		this(originTag, actionTag, destTag, checkContent, TestCondition.ConditionComparator.GREATER_THAN, 0);
	}

	public TransitionConditionEvaluator(Tag<?> originTag, Tag<?> actionTag, Tag<?> destTag, 
			String checkContent, ConditionComparator comparator, int threshold) {

		// Null-safe fallback checks
		if (checkContent == null) {
			logger.log(Level.WARN, "TransitionConditionEvaluator: Received null test goal, treating as empty test goal.");
			checkContent = "";
		}
		if (originTag == null || actionTag == null || destTag == null) {
			logger.log(Level.WARN, String.format(
					"TransitionConditionEvaluator: One or more tags are null. originTag=%s, actionTag=%s, destTag=%s. No condition will be added.",
					String.valueOf(originTag), String.valueOf(actionTag), String.valueOf(destTag)));
			return;
		}

		// Replace line breaks and split the goal lines (case-insensitive splitting)
		String[] lines = checkContent.replaceAll("(\\r|\\n|\\\\n)", "\n").split("\n");

		List<String> origins = new ArrayList<>();
		List<String> actions = new ArrayList<>();
		List<String> dests = new ArrayList<>();

		for (String lineStatement : lines) {
			// Remove leading and trailing spaces
			String strippedStatement = lineStatement.strip();

			// Check if the line starts with 'Origin:', 'Action:', or 'Dest:' (case-insensitive)
			if (strippedStatement.toLowerCase().startsWith("origin:")) {
				String origin = strippedStatement.substring(7).strip();
				origins.add(origin);
				logger.log(Level.INFO, String.format("TransitionConditionEvaluator Origin: %s", origin));
			} else if (strippedStatement.toLowerCase().startsWith("action:")) {
				String action = strippedStatement.substring(7).strip();
				actions.add(action);
				logger.log(Level.INFO, String.format("TransitionConditionEvaluator Action: %s", action));
			} else if (strippedStatement.toLowerCase().startsWith("dest:")) {
				String dest = strippedStatement.substring(5).strip();
				dests.add(dest);
				logger.log(Level.INFO, String.format("TransitionConditionEvaluator Dest: %s", dest));
			}
		}

		if (origins.size() > 1 || actions.size() > 1 || dests.size() > 1) {
			logger.log(Level.WARN, "TransitionConditionEvaluator: Multiple 'Origin:', 'Action:', or 'Dest:' statements found. " +
					"Only the first complete block will be used.");
		}

		if (!origins.isEmpty() && !actions.isEmpty() && !dests.isEmpty()) {
			String origin = origins.get(0);
			String action = actions.get(0);
			String dest = dests.get(0);

			logger.log(Level.INFO, String.format(
					"TransitionConditionEvaluator Loading: Origin='%s', Action='%s', Dest='%s'",
					origin, action, dest));

			addCondition(new StateTransitionCondition(
					originTag.name(), origin,
					actionTag.name(), action,
					destTag.name(), dest,
					comparator, threshold
					));
		} else {
			logger.log(Level.WARN, String.format(
					"TransitionConditionEvaluator: Missing one or more required fields (Origin, Action, Dest) in: %s", checkContent));
		}
	}
}
