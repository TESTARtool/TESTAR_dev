package org.testar.statemodel.axini;

import java.util.ArrayList;
import java.util.List;

public class ProcessDefinition {
	private String name;
	private List<ActionDefinition> actions;
	private List<BehaviorDefinition> behaviors;
	private InitialVariable initialVariables;
	private List<String> entryCalls;

	public ProcessDefinition() {
		this.actions = new ArrayList<>();
		this.behaviors = new ArrayList<>();
		this.entryCalls = new ArrayList<>();
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

	public List<String> getEntryCalls() {
		return entryCalls;
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

	public void setEntryCalls(List<String> entryCalls) {
		this.entryCalls = entryCalls;
	}

	public void addAction(ActionDefinition action) {
		this.actions.add(action);
	}

	public void addBehavior(BehaviorDefinition behavior) {
		this.behaviors.add(behavior);
	}

	public void addEntryCall(String call) {
		this.entryCalls.add(call);
	}

	@Override
	public String toString() {
		return "ProcessDefinition{" +
				"name='" + name + '\'' +
				", actions=" + actions +
				", behaviors=" + behaviors +
				", initialVariables=" + initialVariables +
				", entryCalls=" + entryCalls +
				'}';
	}
}