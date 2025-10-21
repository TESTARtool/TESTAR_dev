package org.testar.statemodel.axini;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GuiStateModel {

	@JsonProperty("InitialUrl")
	private String initialUrl;

	@JsonProperty("InitialPage")
	private String initialPage;

	@JsonProperty("InitialIdentifier")
	private String initialIdentifier;

	@JsonProperty("ConcreteState")
	private List<GuiState> states;

	@JsonProperty("ConcreteAction")
	private List<GuiAction> actions;

	@JsonProperty("ConcreteTransitions")
	private List<GuiTransition> transitions;

	public String getInitialUrl() {
		return initialUrl;
	}

	public void setInitialUrl(String initialUrl) {
		this.initialUrl = initialUrl;
	}

	public String getInitialPage() {
		return initialPage;
	}

	public void setInitialPage(String initialPage) {
		this.initialPage = initialPage;
	}

	public String getInitialIdentifier() {
		return initialIdentifier;
	}

	public void setInitialIdentifier(String initialIdentifier) {
		this.initialIdentifier = initialIdentifier;
	}

	public List<GuiState> getStates() {
		return states;
	}

	public void setStates(List<GuiState> states) {
		this.states = states;
	}

	public List<GuiAction> getActions() {
		return actions;
	}

	public void setActions(List<GuiAction> actions) {
		this.actions = actions;
	}

	public List<GuiTransition> getTransitions() {
		return transitions;
	}

	public void setTransitions(List<GuiTransition> transitions) {
		this.transitions = transitions;
	}

}
