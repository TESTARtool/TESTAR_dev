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

import org.fruit.alayer.Action;
import org.fruit.alayer.Widget;

import nl.ou.testar.GraphDB;
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
	static final String VERSION = "20171118";
	
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
	 * Evaluates the overall accessibility of the SUT by querying the given graph database
	 * This will collect evaluation results from all principles in WCAG2ICT.
	 * This method executes oracles in offline analysis.
	 * @param graphDB The graph database.
	 * @return The results of the evaluation.
	 */
	@Override
	public EvaluationResults query(GraphDB graphDB) {
		EvaluationResults results = new EvaluationResults();
		for (AbstractPrinciple p : principles)
			for (EvaluationResult result : p.query(graphDB).getResults())
				results.add(result);
		return results;
	}
	
	@Override
	public String getImplementationVersion() {
		return "WCAG2ICT-" + VERSION;
	}
	
}
