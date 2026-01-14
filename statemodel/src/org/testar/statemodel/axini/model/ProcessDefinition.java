package org.testar.statemodel.axini.model;

import java.util.ArrayList;
import java.util.List;

/**
 * AMP process model built from the GUI state model.
 * */
public class ProcessDefinition {
	private String name;
	private List<ActionDefinition> actions;
	private List<BehaviorDefinition> behaviors;
	private InitialVariable initialVariables;
	private String entryCall;
	private String entryBehave;

	public ProcessDefinition() {
		this.actions = new ArrayList<>();
		this.behaviors = new ArrayList<>();
	}

	public ProcessDefinition(String name) {
		this();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public List<ActionDefinition> getActions() {
		return actions;
	}

	public List<BehaviorDefinition> getBehaviors() {
		return behaviors;
	}

	public InitialVariable getInitialVariables() {
		return initialVariables;
	}

	public String getEntryCall() {
		return entryCall;
	}

	public String getEntryBehave() {
		return entryBehave;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setActions(List<ActionDefinition> actions) {
		this.actions = actions;
	}

	public void setBehaviors(List<BehaviorDefinition> behaviors) {
		this.behaviors = behaviors;
	}

	public void setInitialVariables(InitialVariable initialVariables) {
		this.initialVariables = initialVariables;
	}

	public void setEntryCall(String entryCall) {
		this.entryCall = entryCall;
	}

	public void setEntryBehave(String entryBehave) {
		this.entryBehave = entryBehave;
	}

	public void addAction(ActionDefinition action) {
		this.actions.add(action);
	}

	public void addBehavior(BehaviorDefinition behavior) {
		this.behaviors.add(behavior);
	}

	// Debugging string representation, not used for rendering the AMP code
	@Override
	public String toString() {
		return "ProcessDefinition{" +
				"name='" + name + '\'' +
				", actions=" + actions +
				", behaviors=" + behaviors +
				", initialVariables=" + initialVariables +
				", entryCall=" + entryCall +
				", entryBehave=" + entryBehave +
				'}';
	}
}
