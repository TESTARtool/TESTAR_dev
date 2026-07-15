/***************************************************************************************************
 *
 * Copyright (c) 2025 - 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2025 - 2026 Open Universiteit - www.ou.nl
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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testar.statemodel.analysis.condition.StateModelTraceCondition.StateModelTrace;
import org.testar.statemodel.analysis.condition.StateModelTraceCondition.TraceCriteria;
import org.testar.statemodel.analysis.condition.StateModelTraceCondition.TraceCriterion;
import org.testar.statemodel.analysis.condition.StateModelTraceCondition.TraceOperator;
import org.testar.statemodel.analysis.condition.TestCondition.ConditionComparator;

/**
 * Builds transition-completion conditions from a JSON StateModelTraces block.
 */
public class TransitionConditionEvaluator extends BasicConditionEvaluator {

	protected static final Logger logger = LogManager.getLogger();

	public TransitionConditionEvaluator(String checkContent) {
		this(checkContent, TestCondition.ConditionComparator.GREATER_THAN, 0);
	}

	public TransitionConditionEvaluator(String checkContent, ConditionComparator comparator, int threshold) {
		if (checkContent == null) {
			logger.log(Level.WARN, "TransitionConditionEvaluator: Received null test goal, treating as empty test goal.");
			checkContent = "";
		}

		checkContent = normalizeGoalContent(checkContent);
		loadJsonTraceConditions(checkContent, comparator, threshold);
	}

	public static String extractActionSelectionGoal(String checkContent) {
		if (checkContent == null) {
			return "";
		}

		checkContent = normalizeGoalContent(checkContent);
		JsonBlockBounds jsonBlockBounds = findStateModelTracesJsonBlockBounds(checkContent);

		if (jsonBlockBounds == null) {
			return checkContent.strip();
		}

		String beforeJson = checkContent.substring(0, jsonBlockBounds.start);
		String afterJson = checkContent.substring(jsonBlockBounds.end + 1);

		return (beforeJson + afterJson).strip();
	}

	private static String normalizeGoalContent(String checkContent) {
		return checkContent.replace("\\r\\n", "\n").replace("\\n", "\n").replace("\\r", "\n");
	}

	private void loadJsonTraceConditions(String checkContent, ConditionComparator comparator, int threshold) {
		String jsonContent = extractJsonContent(checkContent);

		if (jsonContent == null) {
			logger.log(Level.WARN, "TransitionConditionEvaluator: No StateModelTraces JSON block found.");
			return;
		}

		try {
			JsonObject root = new JsonParser().parse(jsonContent).getAsJsonObject();

			if (!root.has("StateModelTraces") || !root.get("StateModelTraces").isJsonArray()) {
				logger.log(Level.WARN, "TransitionConditionEvaluator: JSON block does not contain a StateModelTraces array.");
				return;
			}

			List<StateModelTrace> traces = parseStateModelTraces(root.getAsJsonArray("StateModelTraces"));

			if (traces.isEmpty()) {
				logger.log(Level.WARN, "TransitionConditionEvaluator: StateModelTraces JSON did not contain valid traces.");
				return;
			}

			addCondition(new StateModelTraceCondition(traces, comparator, threshold));
			logger.log(Level.INFO, String.format("TransitionConditionEvaluator: Loaded %d state model trace conditions.", traces.size()));
		} catch (IllegalStateException | JsonSyntaxException e) {
			logger.log(Level.WARN, String.format("TransitionConditionEvaluator: Unable to parse StateModelTraces JSON: %s", e.getMessage()));
		}
	}

	private String extractJsonContent(String checkContent) {
		JsonBlockBounds jsonBlockBounds = findStateModelTracesJsonBlockBounds(checkContent);

		if (jsonBlockBounds == null) {
			return null;
		}

		return checkContent.substring(jsonBlockBounds.start, jsonBlockBounds.end + 1);
	}

	private static JsonBlockBounds findStateModelTracesJsonBlockBounds(String checkContent) {
		// Goal text can contain ordinary braces; only a valid StateModelTraces object is a condition block.
		for (int start = 0; start < checkContent.length(); start++) {
			if (checkContent.charAt(start) != '{') {
				continue;
			}

			int end = findJsonObjectEnd(checkContent, start);

			if (end <= start) {
				continue;
			}

			String candidate = checkContent.substring(start, end + 1);

			if (hasStateModelTracesArray(candidate)) {
				return new JsonBlockBounds(start, end);
			}
		}

		return null;
	}

	private static int findJsonObjectEnd(String content, int start) {
		int depth = 0;
		boolean inString = false;
		boolean escaped = false;

		for (int index = start; index < content.length(); index++) {
			char current = content.charAt(index);

			if (escaped) {
				escaped = false;
				continue;
			}

			if (current == '\\' && inString) {
				escaped = true;
				continue;
			}

			if (current == '"') {
				inString = !inString;
				continue;
			}

			if (inString) {
				continue;
			}

			if (current == '{') {
				depth++;
			} else if (current == '}') {
				depth--;

				if (depth == 0) {
					return index;
				}
			}
		}

		return -1;
	}

	private static boolean hasStateModelTracesArray(String candidate) {
		try {
			JsonElement element = new JsonParser().parse(candidate);

			if (!element.isJsonObject()) {
				return false;
			}

			JsonObject object = element.getAsJsonObject();
			return object.has("StateModelTraces") && object.get("StateModelTraces").isJsonArray();
		} catch (IllegalStateException | JsonSyntaxException e) {
			return false;
		}
	}

	private List<StateModelTrace> parseStateModelTraces(JsonArray traceElements) {
		List<StateModelTrace> traces = new ArrayList<>();

		for (JsonElement traceElement : traceElements) {
			if (!traceElement.isJsonObject()) {
				logger.log(Level.WARN, "TransitionConditionEvaluator: Ignoring non-object trace entry.");
				continue;
			}

			JsonObject traceObject = traceElement.getAsJsonObject();
			String name = getString(traceObject, "name");
			TraceCriteria originState = parseCriteria(traceObject, "originState");
			TraceCriteria action = parseCriteria(traceObject, "action");
			TraceCriteria targetState = parseCriteria(traceObject, "targetState");

			traces.add(new StateModelTrace(name, originState, action, targetState));
		}

		return traces;
	}

	private TraceCriteria parseCriteria(JsonObject traceObject, String fieldName) {
		if (!traceObject.has(fieldName) || !traceObject.get(fieldName).isJsonObject()) {
			return new TraceCriteria(null, null);
		}

		JsonObject criteriaObject = traceObject.getAsJsonObject(fieldName);
		List<TraceCriterion> include = parseCriterionList(criteriaObject, "include");
		List<TraceCriterion> exclude = parseCriterionList(criteriaObject, "exclude");

		return new TraceCriteria(include, exclude);
	}

	private List<TraceCriterion> parseCriterionList(JsonObject criteriaObject, String fieldName) {
		List<TraceCriterion> criteria = new ArrayList<>();

		if (!criteriaObject.has(fieldName) || !criteriaObject.get(fieldName).isJsonArray()) {
			return criteria;
		}

		for (JsonElement criterionElement : criteriaObject.getAsJsonArray(fieldName)) {
			if (!criterionElement.isJsonObject()) {
				logger.log(Level.WARN, "TransitionConditionEvaluator: Ignoring non-object criterion entry.");
				continue;
			}

			JsonObject criterionObject = criterionElement.getAsJsonObject();
			String tag = getString(criterionObject, "tag");
			String value = getString(criterionObject, "value");

			if (tag == null || tag.isBlank() || value == null) {
				logger.log(Level.WARN, "TransitionConditionEvaluator: Ignoring criterion without tag or value.");
				continue;
			}

			criteria.add(new TraceCriterion(tag, parseOperator(getString(criterionObject, "operator")), value));
		}

		return criteria;
	}

	private TraceOperator parseOperator(String operator) {
		if (operator == null || operator.isBlank()) {
			return TraceOperator.CONTAINS;
		}

		switch(operator.toLowerCase()) {
			case "contains":
				return TraceOperator.CONTAINS;
			case "equals":
				return TraceOperator.EQUALS;
			case "regex":
				return TraceOperator.REGEX;
			default:
				logger.log(Level.WARN, String.format(
						"TransitionConditionEvaluator: Unknown operator '%s', using contains.", operator));
				return TraceOperator.CONTAINS;
		}
	}

	private String getString(JsonObject object, String fieldName) {
		if (!object.has(fieldName) || object.get(fieldName).isJsonNull()) {
			return null;
		}

		return object.get(fieldName).getAsString();
	}

	private static class JsonBlockBounds {
		private final int start;
		private final int end;

		private JsonBlockBounds(int start, int end) {
			this.start = start;
			this.end = end;
		}
	}
}
