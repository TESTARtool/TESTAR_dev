package org.testar.actions.selects;

import org.testar.actions.ActionBase;

public class ActionSelect extends ActionBase {
	private ActionType actionType;
	private String value;
	private Integer maxWait;

	/**
	 * Gets the action type.
	 *
	 * @return the action type
	 */
	public ActionType getActionType() {
		return actionType;
	}

	/**
	 * Sets the action type.
	 *
	 * @param actionType the action type to set
	 */
	public void setActionType(ActionType actionType) {
		this.actionType = actionType;
	}

	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Sets the value.
	 *
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * Gets the maximum wait period for this action select.
	 *
	 * @return the maximum wait period in ms
	 */
	public Integer getMaxWait() {
		return maxWait;
	}

	/**
	 * Sets the maximum wait period for this action select.
	 *
	 * @param maxWait the new maximum wait period
	 */
	public void setMaxWait(Integer maxWait) {
		this.maxWait = maxWait;
	}
}
