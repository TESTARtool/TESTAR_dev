package org.testar.statemodel.axini;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GuiState {

	@JsonProperty("AbstractID")
	private String id;

	@JsonProperty("WebTitle")
	private String title;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
