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
import java.util.Map;
import java.util.Map.Entry;

import org.fruit.alayer.Tags;
import org.fruit.alayer.Widget;

import nl.ou.testar.GraphDB;
import nl.ou.testar.a11y.reporting.EvaluationResults;
import nl.ou.testar.a11y.wcag2.SuccessCriterion.Level;
import nl.ou.testar.a11y.windows.AccessibilityUtil;

/**
 * A WCAG 2.0 guideline
 * @author Davy Kager
 *
 */
public final class NavigableGuideline extends AbstractGuideline {

	private static final long serialVersionUID = 7746462844461205071L;
	
	private static final int SAME_TITLE_THRESHOLD = 3;

	NavigableGuideline(AbstractPrinciple parent) {
		super(4, "Navigable", parent);
		criteria.add(new SuccessCriterion(1, "Bypass Blocks",
				this, Level.A, "navigation-mechanisms-skip"));
		criteria.add(new SuccessCriterion(2, "Page Titled",
				this, Level.A, "navigation-mechanisms-title"));
		criteria.add(new SuccessCriterion(3, "Focus Order",
				this, Level.A, "navigation-mechanisms-focus-order"));
		criteria.add(new SuccessCriterion(4, "Link Purpose (In Context)",
				this, Level.A, "navigation-mechanisms-refs"));
		criteria.add(new SuccessCriterion(5, "Multiple Ways",
				this, Level.AA, "navigation-mechanisms-mult-loc"));
		criteria.add(new SuccessCriterion(6, "Headings and Labels",
				this, Level.AA, "navigation-mechanisms-descriptive"));
		criteria.add(new SuccessCriterion(7, "Focus Visible",
				this, Level.AA, "navigation-mechanisms-focus-visible"));
	}
	
	@Override
	public EvaluationResults evaluate(List<Widget> widgets) {
		EvaluationResults results = new EvaluationResults();
		for (Widget w : widgets)
			// used during offline analysis
			w.set(WCAG2Tags.WCAG2IsWindow, AccessibilityUtil.isWindow(w));
		return results;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public EvaluationResults query(GraphDB graphDB) {
		EvaluationResults results = new EvaluationResults();
		SuccessCriterion sc = getSuccessCriterionByName("Page Titled");
		String gremlinTitleCount = "_().has('@class','Widget')" +
				".has('" + WCAG2Tags.WCAG2IsWindow.name() +"',true)" +
				".groupCount{it." + Tags.Title.name() + "}.cap";
		List<Object> titleCounts = graphDB.getObjectsFromGremlinPipe(gremlinTitleCount);
		// the list contains one map with title counts
		Map<String, Long> titleCount = (Map<String, Long>)titleCounts.get(0);
		boolean hasViolations = false;
		for (Entry<String, Long> entry : titleCount.entrySet()) {
			if (entry.getValue() > SAME_TITLE_THRESHOLD) {
				hasViolations = true;
				results.add(new WCAG2EvaluationResult(sc, WCAG2EvaluationResult.Type.WARNING,
						"Possible ambiguous title \"" + entry.getKey() +
						"\" appeared " + entry.getValue() + " times"));
			}
		}
		if (!hasViolations)
			results.add(evaluationPassed(sc));
		return results;
	}

}
