package org.testar.statemodel.axini.model;

/** 
 * AMP single behavior operation mapping an action call to a next behavior.
 * 
 * stores the Axini model information of e.g.,
 *
 * Left_Click_At_AboutUs(); behave_as 'ParaBank | Customer'
 * 
 * */
public class BehaviorOperation {
	private String actionCall;   // e.g., Left_Click_At_AboutUs()
	private String nextBehavior; // e.g., ParaBank | Customer

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

	// Debugging string representation, not used for rendering the AMP code
	@Override
	public String toString() {
		return "BehaviorOperation{" +
				"actionCall='" + actionCall + '\'' +
				", nextBehavior='" + nextBehavior + '\'' +
				'}';
	}
}
