package org.testar.statemodel.axini;

public class BehaviorOperation {
	private String actionCall;   // e.g., "Left_Click_at_Register()"
	private String nextBehavior; // e.g., "ParaBank | Register - AST3ST4R"

	public BehaviorOperation() {}

	public BehaviorOperation(String actionCall, String nextBehavior) {
		this.actionCall = actionCall;
		this.nextBehavior = nextBehavior;
	}

	public String getActionCall() {
		return actionCall;
	}

	public String getNextBehavior() {
		return nextBehavior;
	}

	public void setActionCall(String actionCall) {
		this.actionCall = actionCall;
	}

	public void setNextBehavior(String nextBehavior) {
		this.nextBehavior = nextBehavior;
	}

	@Override
	public String toString() {
		return "BehaviorOperation{" +
				"actionCall='" + actionCall + '\'' +
				", nextBehavior='" + nextBehavior + '\'' +
				'}';
	}
}