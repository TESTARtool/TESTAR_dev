package org.testar.json.object;

import com.fasterxml.jackson.annotation.JsonCreator;

public class StateModelTestSequenceJsonObject {
	
	String sequenceId;
	int numberSequenceNodes;
	String startDateTime;
	String verdict;
	boolean foundErrors;
	int numberErrors;
	boolean sequenceDeterministic;
	
	@JsonCreator
	public StateModelTestSequenceJsonObject(String sequenceId, int numberSequenceNodes, String startDateTime, 
			String verdict, boolean foundErrors, int numberErrors, boolean sequenceDeterministic) {
		this.sequenceId = sequenceId;
		this.numberSequenceNodes = numberSequenceNodes;
		this.startDateTime = startDateTime;
		this.verdict = verdict;
		this.foundErrors = foundErrors;
		this.numberErrors = numberErrors;
		this.sequenceDeterministic = sequenceDeterministic;
	}

}
