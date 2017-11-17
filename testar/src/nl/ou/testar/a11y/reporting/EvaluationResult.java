/*****************************************************************************************
 *                                                                                       *
 * COPYRIGHT (2017):                                                                     *
 * Universitat Politecnica de Valencia                                                   *
 * Camino de Vera, s/n                                                                   *
 * 46022 Valencia, Spain                                                                 *
 * www.upv.es                                                                            *
 *                                                                                       * 
 * D I S C L A I M E R:                                                                  *
 * This software has been developed by the Universitat Politecnica de Valencia (UPV)     *
 * in the context of the TESTAR Proof of Concept project:                                *
 *               "UPV, Programa de Prueba de Concepto 2014, SP20141402"                  *
 * This sample is distributed FREE of charge under the TESTAR license, as an open        *
 * source project under the BSD3 licence (http://opensource.org/licenses/BSD-3-Clause)   *                                                                                        * 
 *                                                                                       *
 *****************************************************************************************/

package nl.ou.testar.a11y.reporting;

import java.io.Serializable;

import org.fruit.alayer.Tags;
import org.fruit.alayer.Verdict;
import org.fruit.alayer.Widget;

/**
 * The result of evaluating an accessibility rule
 * @author Davy Kager
 *
 */
public class EvaluationResult implements Serializable {
	
	private static final long serialVersionUID = -51527046346987231L;

	public enum Type {
		/**
		 * No problem found
		 */
		OK,
		
		/**
		 * An error
		 * This is a definite problem that can be detected automatically.
		 */
		ERROR,
		
		/**
		 * A warning
		 * This is a potential problem that can only be detected semi-automatically and needs expert confirmation.
		 */
		WARNING;
	}
	
	private final Type type;
	private final String message;
	private final Widget widget;
	
	/**
	 * Constructs a new evaluation result that does not apply to a single widget
	 * @param type The problem type.
	 * @param message The problem description.
	 */
	public EvaluationResult(Type type, String message) {
		this(type, message, null);
	}
	
	/**
	 * Constructs a new evaluation result that applies to a single widget
	 * @param type The problem type.
	 * @param message The problem description.
	 * @param widget The widget this evaluation result applies to.
	 */
	public EvaluationResult(Type type, String message, Widget widget) {
		this.type = type;
		this.message = message;
		this.widget = widget;
	}
	
	/**
	 * Gets the problem type
	 * @return The problem type.
	 */
	public Type getType() {
		return type;
	}
	
	/**
	 * Gets the problem message
	 * @return The problem message.
	 */
	public String getMessage() {
		return message;
	}
	
	/**
	 * Gets the widget that this evaluation result applies to
	 * @return The widget.
	 */
	public Widget getWidget() {
		return widget;
	}
	
	/**
	 * Computes the Verdict severity for the result
	 * @return The severity.
	 */
	public double getVerdictSeverity() {
		return type.equals(Type.OK) ? Verdict.SEVERITY_OK : Verdict.SEVERITY_FAIL;
	}
	
	@Override
	public String toString() {
		String ret = type.name() + ": " + message;
		if (widget != null)
			ret += " [Widget: \"" + widget.get(Tags.Title, "") + "\" (" + widget.get(Tags.Role) + ")]";
		return ret;
	}

}
