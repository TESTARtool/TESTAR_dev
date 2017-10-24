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

package nl.ou.testar.a11y.protocols;

import java.util.List;
import java.util.Set;

import org.fruit.alayer.Action;
import org.fruit.alayer.Widget;

import com.tinkerpop.blueprints.Graph;

import nl.ou.testar.a11y.wcag2.EvaluationResults;

/**
 * Specifies the requirements for an object to be plugged into an AbstractProtocol to evaluate accessibility
 * Such objects need to be able to evaluate a given state and derive actions to move to a new state.
 * @author Davy Kager
 *
 */
public interface Evaluator {
	
	/**
	 * Evaluates the accessibility of the given state
	 * This method executes oracles in state analysis.
	 * @param widgets The widgets to consider.
	 * @return The results of the evaluation.
	 */
	public EvaluationResults evaluate(List<Widget> widgets);
	
	/**
	 * Derives the possible actions from the given state
	 * The actions are specific to accessibility.
	 * This method derives actions in state analysis.
	 * @param widgets The widgets to consider.
	 * @return The set of actions.
	 */
	public Set<Action> deriveActions(List<Widget> widgets);
	
	/**
	 * Evaluates the overall accessibility of the SUT by querying the given graph
	 * This method executes oracles in offline analysis.
	 * @param graphdb The graph to use.
	 * @return The results of the evaluation.
	 */
	public EvaluationResults query(Graph graph);

}
