package org.testar.actions.selects;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ActionSelectSequence {
	private String id;
	private String description;
	private List<ActionSelect> actionSelects;
	private List<Integer> probabilities;
	@JsonIgnore
	private int counter = 0;

	/**
	 * Gets the id of the sequence
	 *
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the id of the sequence
	 *
	 * @param id the new id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Get description of the sequence
	 * 
	 * @return the description of the sequence
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Set the description of the sequence
	 * 
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the list of action selects
	 */
	public List<ActionSelect> getActionSelects() {
		return actionSelects;
	}

	/**
	 * @param list of action selects to set
	 */
	public void setActionSelects(List<ActionSelect> actionSelects) {
		this.actionSelects = actionSelects;
	}
	
	/**
	 * @return The chance this sequence should be selected.
	 * 
	 */
	@JsonIgnore
	public int getProbability() {
		if (getCounter() >= probabilities.size()) {
			// select the last one
			return probabilities.get(probabilities.size()-1);
		}
		return probabilities.get(getCounter());
	}

	/**
	 * Gets the probabilities.
	 *
	 * @return the probabilities
	 */
	public List<Integer> getProbabilities() {
		return probabilities;
	}

	/**
	 * Set the chance the sequence should be selected.
	 * 
	 * @param array of probabilities, i.e. the chance of selecting. A value between 0 and 100.
	 */
	public void setProbabilities(List<Integer> probabilities) {
		this.probabilities = probabilities;
	}

	public int getCounter() {
		return counter;
	}

	public void incrementCounter() {
		counter++;
	}

}
