package org.testar.json.object;

import org.apache.commons.codec.binary.Base64;

public class StateModelTestSequenceStepJsonObject implements Comparable<StateModelTestSequenceStepJsonObject> {

	//String concreteActionId;
	//String stateScreenshot;
	String actionDescription;
	String timestamp;
	
	public StateModelTestSequenceStepJsonObject(/*String concreteActionId, byte[] stateScreenshot,*/
			String actionDescription, String timestamp) {
		//this.concreteActionId = concreteActionId;
		//this.stateScreenshot =  Base64.encodeBase64String(stateScreenshot);
		this.actionDescription = actionDescription;
		this.timestamp = timestamp;
	}

	@Override
	public int compareTo(StateModelTestSequenceStepJsonObject o) {
        if ((this.timestamp.compareTo(o.timestamp)) >0) return 1;
        else return 0;
	}
	
}
