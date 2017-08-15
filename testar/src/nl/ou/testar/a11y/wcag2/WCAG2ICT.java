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
import java.util.List;
import java.util.Set;

import org.fruit.alayer.Action;
import org.fruit.alayer.SUT;
import org.fruit.alayer.State;
import org.fruit.alayer.Verdict;

/**
 * Specification of WCAG 2.0 according to WCAG2ICT
 * @author Davy Kager
 *
 */
public final class WCAG2ICT {
	
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
	 * @param state The state.
	 * @return The results of the evaluation.
	 */
	public EvaluationResults evaluate(State state) {
		EvaluationResults results = new EvaluationResults();
		for (AbstractPrinciple p : principles)
			for (EvaluationResult result : p.evaluate(state).getResults())
				results.add(result);
		return results;
	}
	
	/**
	 * Derives the follow-up actions from the given state
	 * This will collect actions from all principles in WCAG2ICT.
	 * The actions are specific to accessibility.
	 * @param state The state.
	 * @return The set of actions.
	 */
	public Set<Action> deriveActions(State state) {
		Set<Action> actions = Collections.emptySet();
		for (AbstractPrinciple p : principles)
			actions.addAll(p.deriveActions(state));
		return actions;
	}
	
}
