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
	private final Widget widget;
	
	/**
	 * Constructs a new evaluation result that does not apply to a single widget
	 * @param criterion The success criterion.
	 * @param type The problem type.
	 */
	public EvaluationResult(Type type) {
		this(type, null);
	}
	
	/**
	 * Constructs a new evaluation result that applies to a single widget
	 * @param criterion The success criterion.
	 * @param type The problem type.
	 */
	public EvaluationResult(Type type, Widget widget) {
		this.type = type;
		this.widget = widget;
	}
	
	/**
	 * Gets the problem type of this evaluation result
	 * @return The problem type.
	 */
	public Type getType() {
		return type;
	}
	
	/**
	 * Gets the widget that the success criterion associated with this evaluation result applies to
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

}
