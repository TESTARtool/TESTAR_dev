package org.testar.json.object;

import java.util.SortedSet;

import com.fasterxml.jackson.annotation.JsonCreator;

public class StateModelTestSequenceJsonObject implements Comparable<StateModelTestSequenceJsonObject> {
	
	String sequenceId;
	int numberSequenceNodes;
	String startDateTime;
	String verdict;
	boolean foundErrors;
	int numberErrors;
	boolean sequenceDeterministic;
	SortedSet <StateModelTestSequenceStepJsonObject> sequenceActionSteps;
	
	@JsonCreator
	public StateModelTestSequenceJsonObject(String sequenceId, int numberSequenceNodes, String startDateTime, 
			String verdict, boolean foundErrors, int numberErrors, boolean sequenceDeterministic, 
			SortedSet <StateModelTestSequenceStepJsonObject> sequenceActionSteps) {
		this.sequenceId = sequenceId;
		this.numberSequenceNodes = numberSequenceNodes;
		this.startDateTime = startDateTime;
		this.verdict = verdict;
		this.foundErrors = foundErrors;
		this.numberErrors = numberErrors;
		this.sequenceDeterministic = sequenceDeterministic;
		this.sequenceActionSteps = sequenceActionSteps;
	}

	@Override
	public int compareTo(StateModelTestSequenceJsonObject o) {
        if ((this.startDateTime.compareTo(o.startDateTime)) >0) return 1;
        else return 0;
	}

}
