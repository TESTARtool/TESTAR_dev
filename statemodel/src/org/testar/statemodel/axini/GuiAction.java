package org.testar.statemodel.axini;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GuiAction {

	@JsonProperty("AbstractID")
	private String id;

	@JsonProperty("Desc")
	private String desc;

	@JsonProperty("WebHref")
	private String webHref;

	@JsonProperty("WebSelector")
	private String selector;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getWebHref() {
		return webHref;
	}

	public void setWebHref(String webHref) {
		this.webHref = webHref;
	}

	public String getSelector() {
		return selector;
	}

	public void setSelector(String selector) {
		this.selector = selector;
	}
}
