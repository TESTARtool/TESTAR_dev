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

import org.openqa.selenium.InvalidArgumentException;
import org.testar.monkey.alayer.State;
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
