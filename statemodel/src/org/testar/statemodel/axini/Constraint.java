package org.testar.statemodel.axini;

public class Constraint {
	private String field;
	private String expression;

	public Constraint() {}

	public Constraint(String field, String expression) {
		this.field = field;
		this.expression = expression;
	}

	public String getField() {
		return field;
	}

	public String getExpression() {
		return expression;
	}

	public void setField(String field) {
		this.field = field;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	@Override
	public String toString() {
		return field + " == " + expression;
	}
}
