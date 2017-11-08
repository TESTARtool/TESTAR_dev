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

package nl.ou.testar.a11y.wcag2;

import org.fruit.alayer.Widget;

import nl.ou.testar.a11y.reporting.EvaluationResult;

/**
 * The result of evaluating a WCAG2ICT success criterion
 * @author Davy Kager
 *
 */
public final class WCAG2EvaluationResult extends EvaluationResult {
	
	private static final long serialVersionUID = -3449985990033944575L;
	private final SuccessCriterion criterion;
	
	WCAG2EvaluationResult(SuccessCriterion criterion, Type type) {
		this(criterion, type, null);
	}

	WCAG2EvaluationResult(SuccessCriterion criterion, Type type, Widget widget) {
		super(type, widget);
		this.criterion = criterion;
	}
	
	/**
	 * Gets the success criterion associated with this evaluation result
	 * @return The success criterion.
	 */
	public SuccessCriterion getSuccessCriterion() {
		return criterion;
	}
	
	@Override
	public double getVerdictSeverity() {
		return criterion.getVerdictSeverity();
	}
	
}
