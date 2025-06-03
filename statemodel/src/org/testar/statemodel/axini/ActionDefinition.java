package org.testar.statemodel.axini;

import java.util.List;

public class ActionDefinition {
	private String name;

	private String receivedStimulus;
	private List<Constraint> receiveConstraints;

	private String sentResponse;
	private List<Constraint> sendConstraints;

	public ActionDefinition() {}

	public ActionDefinition(String name,
			String receivedStimulus,
			List<Constraint> receiveConstraints,
			String sentResponse,
			List<Constraint> sendConstraints) {
		this.name = name;
		this.receivedStimulus = receivedStimulus;
		this.receiveConstraints = receiveConstraints;
		this.sentResponse = sentResponse;
		this.sendConstraints = sendConstraints;
	}

	public String getName() {
		return name;
	}

	public String getReceivedStimulus() {
		return receivedStimulus;
	}

	public List<Constraint> getReceiveConstraints() {
		return receiveConstraints;
	}

	public String getSentResponse() {
		return sentResponse;
	}

	public List<Constraint> getSendConstraints() {
		return sendConstraints;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setReceivedStimulus(String receivedStimulus) {
		this.receivedStimulus = receivedStimulus;
	}

	public void setReceiveConstraints(List<Constraint> receiveConstraints) {
		this.receiveConstraints = receiveConstraints;
	}

	public void setSentResponse(String sentResponse) {
		this.sentResponse = sentResponse;
	}

	public void setSendConstraints(List<Constraint> sendConstraints) {
		this.sendConstraints = sendConstraints;
	}

	@Override
	public String toString() {
		return "ActionDefinition{" +
				"name='" + name + '\'' +
				", receivedStimulus='" + receivedStimulus + '\'' +
				", receiveConstraints=" + receiveConstraints +
				", sentResponse='" + sentResponse + '\'' +
				", sendConstraints=" + sendConstraints +
				'}';
	}
}
