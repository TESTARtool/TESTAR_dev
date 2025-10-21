package org.testar.statemodel.axini;

public class InitialVariable {
	private String name;
	private String type;
	private String value;
	private String page;
	private String behavior;

	public InitialVariable() {}

	public InitialVariable(String name, String type, String value, String page, String behavior) {
		this.name = name;
		this.type = type;
		this.value = value;
		this.page = page;
		this.behavior = behavior;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public String getValue() {
		return value;
	}

	public String getPage() {
		return page;
	}

	public String getBehavior() {
		return behavior;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public void setBehavior(String behavior) {
		this.behavior = behavior;
	}

	@Override
	public String toString() {
		return "InitialVariable{" +
				"name='" + name + '\'' +
				", type='" + type + '\'' +
				", value='" + value + '\'' +
				", page='" + page + '\'' +
				", behavior='" + behavior + '\'' +
				'}';
	}
}
