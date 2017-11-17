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

import nl.ou.testar.a11y.wcag2.SuccessCriterion.Level;

/**
 * A WCAG 2.0 guideline
 * @author Davy Kager
 *
 */
public final class PredictableGuideline extends AbstractGuideline {

	private static final long serialVersionUID = 7865244281809536826L;

	PredictableGuideline(AbstractPrinciple parent) {
		super(2, "Predictable", parent);
		criteria.add(new SuccessCriterion(1, "On Focus",
				this, Level.A, "consistent-behavior-receive-focus"));
		criteria.add(new SuccessCriterion(2, "On Input",
				this, Level.A, "consistent-behavior-unpredictable-change"));
		criteria.add(new SuccessCriterion(3, "Consistent Navigation",
				this, Level.AA, "consistent-behavior-consistent-locations"));
		criteria.add(new SuccessCriterion(4, "Consistent Identification",
				this, Level.AA, "consistent-behavior-consistent-functionality"));
	}

}
