/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2025-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2025-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.llm.condition;

import org.openqa.selenium.InvalidArgumentException;
import org.testar.core.state.State;
import org.testar.statemodel.StateModelManager;
import org.testar.statemodel.util.QueryHelper;

public class StateTransitionCondition extends TestCondition {

	private final String originStateField;
	private final String originStateMessage;

	private final String actionField;
	private final String actionMessage;

	private final String destStateField;
	private final String destStateMessage;

	public StateTransitionCondition(String originStateField, String originStateMessage, 
			String actionField, String actionMessage, 
			String destStateField, String destStateMessage, 
			ConditionComparator comparator, int threshold) {
		super(comparator, threshold);

		this.originStateField = originStateField;
		this.originStateMessage = originStateMessage;

		this.actionField = actionField;
		this.actionMessage = actionMessage;

		this.destStateField = destStateField;
		this.destStateMessage = destStateMessage;
	}

	public String getOriginStateMessage() {
		return originStateMessage;
	}

	public String getActionMessage() {
		return actionMessage;
	}

	public String getDestStateMessage() {
		return destStateMessage;
	}

	/**
	 * Evaluates whether this condition is true.
	 * @param modelIdentifier modelIdentifier of the current test sequence.
	 * @param stateModelManager stateModelManager of the current test sequence.
	 * @return True if condition is met.
	 */
	@Override
	public boolean evaluate(String modelIdentifier, StateModelManager stateModelManager) {
		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append("SELECT COUNT(*) ");
		queryBuilder.append("AS found ");
		queryBuilder.append("FROM ConcreteAction ");
		queryBuilder.append("WHERE `" + this.actionField + "` LIKE '%" + this.actionMessage + "%' ");
		queryBuilder.append("AND out.uid LIKE '" + modelIdentifier + "%' ");
		queryBuilder.append("AND out." + this.originStateField +" containstext '" + this.originStateMessage + "' ");
		queryBuilder.append("AND in.uid LIKE '" + modelIdentifier + "%' ");
		queryBuilder.append("AND in." + this.destStateField + " containstext '" + this.destStateMessage + "'");

		String query = queryBuilder.toString();
		String result = stateModelManager.queryStateModel(query);

		int matches = QueryHelper.parseCountQueryResponse(result, "found");
		int threshold = getThreshold();

		switch(getComparator()) {
		case EQUAL:
			return matches == threshold;
		case LESS_THAN:
			return matches < threshold;
		case GREATER_THAN:
			return matches > threshold;
		case LESS_THAN_EQUALS:
			return matches <= threshold;
		case GREATER_THAN_EQUALS:
			return matches >= threshold;
		default:
			throw new InvalidArgumentException("Invalid comparator for condition!");
		}
	}

	@Override
	public boolean evaluate(State state) {
		// State Transitions only work with the State Model
		return false;
	}
}
