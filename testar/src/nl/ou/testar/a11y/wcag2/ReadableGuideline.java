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

import java.util.List;

import org.fruit.alayer.Widget;

import nl.ou.testar.a11y.reporting.EvaluationResults;
import nl.ou.testar.a11y.wcag2.SuccessCriterion.Level;
import nl.ou.testar.a11y.windows.AccessibilityUtil;

/**
 * A WCAG 2.0 guideline
 * @author Davy Kager
 *
 */
public final class ReadableGuideline extends AbstractGuideline {

	private static final long serialVersionUID = -7061749135757191395L;

	ReadableGuideline(AbstractPrinciple parent) {
		super(1, "Readable", parent);
		criteria.add(new SuccessCriterion(1, "Language of Page", this, Level.A));
		criteria.add(new SuccessCriterion(2, "Language of Parts", this, Level.AA));
	}
	
	@Override
	public EvaluationResults evaluate(List<Widget> widgets) {
		EvaluationResults results = new EvaluationResults();
		SuccessCriterion sc = getSuccessCriterionByName("Language of Page");
		for (Widget w : widgets)
			if (AccessibilityUtil.isWindow(w) && AccessibilityUtil.getLanguage(w) == 0)
				results.add(new WCAG2EvaluationResult(sc, WCAG2EvaluationResult.Type.ERROR, w));
			else
				results.add(evaluationPassed(sc));
		return results;
	}

}
