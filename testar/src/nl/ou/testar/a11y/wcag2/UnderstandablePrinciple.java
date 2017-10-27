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

/**
 * A WCAG 2.0 principle
 * @author Davy Kager
 *
 */
public final class UnderstandablePrinciple extends AbstractPrinciple {

	private static final long serialVersionUID = 3077337682375890558L;

	UnderstandablePrinciple() {
		super(3, "Understandable");
		guidelines.add(new ReadableGuideline(this));
		guidelines.add(new PredictableGuideline(this));
		guidelines.add(new InputAssistanceGuideline(this));
	}

}
