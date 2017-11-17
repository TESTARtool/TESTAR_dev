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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.fruit.Assert;
import org.fruit.alayer.Action;
import org.fruit.alayer.Widget;

import com.tinkerpop.blueprints.Vertex;

import nl.ou.testar.a11y.protocols.Evaluator;
import nl.ou.testar.a11y.reporting.EvaluationResult;
import nl.ou.testar.a11y.reporting.EvaluationResults;

/**
 * Specification of WCAG 2.0 according to WCAG2ICT
 * @author Davy Kager
 *
 */
public final class WCAG2ICT implements Evaluator {
	
	/**
	 * The implementation version
	 */
	static final String VERSION = "20171112";
	
	/**
	 * The base part for anchor links, e.g. to success criteria
	 */
	
	private final List<AbstractPrinciple> principles = new ArrayList<>();
	
	/**
	 * Constructs the WCAG 2.0 specification
	 */
	public WCAG2ICT() {
		principles.add(new PerceivablePrinciple());
		principles.add(new OperablePrinciple());
		principles.add(new UnderstandablePrinciple());
		principles.add(new RobustPrinciple());
	}
	
	/**
	 * Gets all principles in WCAG2ICT
	 * @return The list of principles.
	 */
	public List<AbstractPrinciple> getPrinciples() {
		return Collections.unmodifiableList(principles);
	}
	
	/**
	 * Evaluates the accessibility of the given state
	 * This will collect evaluation results from all principles in WCAG2ICT.
	 * This method executes oracles in state analysis.
	 * @param widgets The widgets to consider.
	 * @return The results of the evaluation.
	 */
	@Override
	public EvaluationResults evaluate(List<Widget> widgets) {
		EvaluationResults results = new EvaluationResults();
		for (AbstractPrinciple p : principles)
			for (EvaluationResult result : p.evaluate(widgets).getResults())
				results.add(result);
		return results;
	}
	
	/**
	 * Derives the possible actions from the given state
	 * This will collect actions from all principles in WCAG2ICT.
	 * The actions are specific to accessibility.
	 * This method derives actions in state analysis.
	 * @param widgets The widgets to consider.
	 * @return The set of actions.
	 */
	@Override
	public Set<Action> deriveActions(List<Widget> widgets) {
		Set<Action> actions = new HashSet<>();
		for (AbstractPrinciple p : principles)
			actions.addAll(p.deriveActions(widgets));
		return actions;
	}
	
	/**
	 * Evaluates the overall accessibility of the SUT by querying the given graph
	 * This will collect evaluation results from all principles in WCAG2ICT.
	 * This method executes oracles in offline analysis.
	 * @param vertices All state vertices.
	 * @return The results of the evaluation.
	 */
	@Override
	public EvaluationResults query(Iterable<Vertex> vertices) {
		EvaluationResults results = new EvaluationResults();
		for (AbstractPrinciple p : principles)
			for (EvaluationResult result : p.query(vertices).getResults())
				results.add(result);
		return results;
	}
	
	/**
	 * Gets a success criterion by its qualified number, e.g. "1.1.1"
	 * @param number The qualified number.
	 * @return The success criterion,
	 */
	public SuccessCriterion getSuccessCriterionByNumber(String number) {
		Assert.isTrue(number.length() == 5);
		String[] parts = number.split(".");
		Assert.isTrue(parts.length == 3);
		int p = Integer.parseInt(parts[0]),
				g = Integer.parseInt(parts[1]),
				s = Integer.parseInt(parts[2]);
		Assert.isTrue(p > 0 && p <= principles.size());
		AbstractPrinciple principle = principles.get(p-1);
		Assert.isTrue(g > 0 && g <= principle.getGuidelines().size());
		AbstractGuideline guideline = principle.getGuidelines().get(g-1);
		Assert.isTrue(s > 0 && s <= guideline.getSuccessCriteria().size());
		return guideline.getSuccessCriteria().get(s);
	}
	
	@Override
	public String getImplementationVersion() {
		return "WCAG2ICT-" + VERSION;
	}
	
}
