package org.testar.json.object;

public class StateModelTestSequenceStepJsonObject implements Comparable<StateModelTestSequenceStepJsonObject> {

	String actionDescription;
	String timestamp;
	
	public StateModelTestSequenceStepJsonObject(String actionDescription, String timestamp) {
		this.actionDescription = actionDescription;
		this.timestamp = timestamp;
	}

	@Override
	public int compareTo(StateModelTestSequenceStepJsonObject o) {
        if ((this.timestamp.compareTo(o.timestamp)) >0) return 1;
        else return 0;
	}
	
}
