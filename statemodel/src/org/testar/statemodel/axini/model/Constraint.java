package org.testar.statemodel.axini.model;

/** 
 * field == expression constraint used in AMP receive/send clauses. 
 * 
 * stores the Axini model information of e.g.,
 * 
 * css == "a[href*='about.htm']"
 * text == "About Us"
 * title == "ParaBank | Customer"
 * */
public class Constraint {
	private String field; // e.g., css
	private String expression; // e.g., "a[href*='about.htm']"

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

	// Debugging string representation, not used for rendering the AMP code
	@Override
	public String toString() {
		return field + " == " + expression;
	}
}
