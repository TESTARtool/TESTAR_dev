package org.testar.statemodel.axini.model;

import java.util.List;

/** 
 * AMP action definition derived from a GUI action. 
 * 
 * stores the Axini model information of e.g.,
 *
 * def Left_Click_At_AboutUs()
 *   receive 'click', constraint: %(css == "a[href*='about.htm']" && text == “About Us”)
 *   send 'page_title', constraint: %(title == "ParaBank | Customer")
 * end
 *
 * */
public class ActionDefinition {
	private String name; // e.g., Left_Click_At_AboutUs

	private String receivedStimulus; // e.g., click
	private List<Constraint> receiveConstraints; // e.g., (css == "a[href*='about.htm']" && text == “About Us”)

	private String sentResponse; // e.g., page_title
	private List<Constraint> sendConstraints; // e.g., (title == "ParaBank | Customer")

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

	// Debugging string representation, not used for rendering the AMP code
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
