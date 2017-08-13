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

import org.fruit.Assert;
import org.fruit.alayer.Action;
import org.fruit.alayer.State;

/**
 * A WCAG principle
 * @author Davy Kager
 *
 */
public class Principle extends ItemBase {
	
	private final List<AbstractGuideline> guidelines = new ArrayList<AbstractGuideline>();

	/**
	 * Constructs a new principle
	 * @param nr The number of the principle.
	 * @param name The name of the principle.
	 */
	Principle(int nr, String name) {
		super(nr, name);
	}

	/**
	 * Gets all guidelines that belong to this principle
	 * @return The list of guidelines.
	 */
	List<AbstractGuideline> getGuidelines() {
		return Collections.unmodifiableList(guidelines);
	}
	
	/**
	 * Adds a guideline to this principle
	 * @param guideline The guideline.
	 */
	void addGuideline(AbstractGuideline guideline) {
		guidelines.add(Assert.notNull(guideline));
	}
	
	/**
	 * Evaluates the accessibility of the given state
	 * This will collect evaluation results from all guidelines that belong to this principle.
	 * @param state The state.
	 * @return The results of the evaluation.
	 */
	EvaluationResults evaluate(State state) {
		EvaluationResults results = new EvaluationResults();
		for (AbstractGuideline g : guidelines)
			for (EvaluationResult result : g.evaluate(state).getResults())
				results.add(result);
		return results;
	}
	
	/**
	 * Derives the follow-up actions from the given state
	 * This will collect actions from all guidelines that belong to this principle.
	 * The actions are specific to accessibility.
	 * @param state The state.
	 * @return The set of actions.
	 */
	Set<Action> deriveActions(State state) {
		Set<Action> actions = Collections.emptySet();
		for (AbstractGuideline g : guidelines)
			actions.addAll(g.deriveActions(state));
		return actions;
	}
		
}
