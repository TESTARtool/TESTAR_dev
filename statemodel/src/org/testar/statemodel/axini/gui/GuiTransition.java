package org.testar.statemodel.axini.gui;

import com.fasterxml.jackson.annotation.JsonProperty;

/** DTO for GUI transition entries in the TESTAR state model JSON. */
public class GuiTransition {

	@JsonProperty("Source")
	private String sourceStateId;

	@JsonProperty("Target")
	private String targetStateId;

	@JsonProperty("Action")
	private String actionId;

	public String getSourceStateId() {
		return sourceStateId;
	}

	public void setSourceStateId(String sourceStateId) {
		this.sourceStateId = sourceStateId;
	}

	public String getActionId() {
		return actionId;
	}

	public void setActionId(String actionId) {
		this.actionId = actionId;
	}

	public String getTargetStateId() {
		return targetStateId;
	}

	public void setTargetStateId(String targetStateId) {
		this.targetStateId = targetStateId;
	}
}
