package org.testar.statemodel.axini;

import java.util.ArrayList;
import java.util.List;

public class BehaviorDefinition {
	private String name;
	private boolean nonTerminating;
	private List<BehaviorOperation> operations;

	public BehaviorDefinition() {
		this.operations = new ArrayList<>();
	}

	public BehaviorDefinition(String name, boolean nonTerminating) {
		this.name = name;
		this.nonTerminating = nonTerminating;
		this.operations = new ArrayList<>();
	}

	public String getName() {
		return name;
	}

	public boolean isNonTerminating() {
		return nonTerminating;
	}

	public List<BehaviorOperation> getOperations() {
		return operations;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setNonTerminating(boolean nonTerminating) {
		this.nonTerminating = nonTerminating;
	}

	public void setOperations(List<BehaviorOperation> operations) {
		this.operations = operations;
	}

	public void addOperation(BehaviorOperation operation) {
		this.operations.add(operation);
	}

	@Override
	public String toString() {
		return "BehaviorDefinition{" +
				"name='" + name + '\'' +
				", nonTerminating=" + nonTerminating +
				", operations=" + operations +
				'}';
	}
}