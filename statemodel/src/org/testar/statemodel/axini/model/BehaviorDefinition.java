package org.testar.statemodel.axini.model;

import java.util.ArrayList;
import java.util.List;

/** 
 * AMP behavior definition with a list of possible operations. 
 * 
 * stores the Axini model information of e.g.,
 *
 * behavior('ParaBank | Welcome', :non_terminating) {
 *   repeat {
 *     o { Left_Click_At_AboutUs(); behave_as 'ParaBank | Customer'}
 *   }
 * }
 * 
 * */
public class BehaviorDefinition {
	private String name; // e.g., ParaBank | Customer
	private boolean nonTerminating; // e.g., true to generate :non_terminating
	private List<BehaviorOperation> operations; // e.g., Left_Click_At_AboutUs(); behave_as 'ParaBank | Customer'

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

	// Debugging string representation, not used for rendering the AMP code
	@Override
	public String toString() {
		return "BehaviorDefinition{" +
				"name='" + name + '\'' +
				", nonTerminating=" + nonTerminating +
				", operations=" + operations +
				'}';
	}
}
