package org.testar.statemodel.axini;

public class InitialVariable {
	private String name;
	private String type;
	private String value;

	public InitialVariable() {}

	public InitialVariable(String name, String type, String value) {
		this.name = name;
		this.type = type;
		this.value = value;
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

	public void setName(String name) {
		this.name = name;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "InitialVariable{" +
				"name='" + name + '\'' +
				", type='" + type + '\'' +
				", value='" + value + '\'' +
				'}';
	}
}
