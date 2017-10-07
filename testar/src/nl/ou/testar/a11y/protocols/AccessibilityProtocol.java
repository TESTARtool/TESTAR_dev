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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.fruit.alayer.Action;
import org.fruit.alayer.SUT;
import org.fruit.alayer.State;
import org.fruit.alayer.Tags;
import org.fruit.alayer.Verdict;
import org.fruit.alayer.Widget;
import org.fruit.alayer.exceptions.ActionBuildException;
import org.fruit.monkey.DefaultProtocol;

import nl.ou.testar.a11y.wcag2.EvaluationResults;
import nl.ou.testar.a11y.wcag2.WCAG2Tags;
import nl.ou.testar.a11y.windows.AccessibilityUtil;

/**
 * Accessibility evaluation protocol
 * @author Davy Kager
 *
 */
public class AccessibilityProtocol extends DefaultProtocol {
	
	/**
	 * The accessibility evaluator
	 */
	protected final Evaluator evaluator;
	
	/**
	 * The relevant widgets
	 * This needs to be updated after every state change.
	 */
	protected List<Widget> relevantWidgets;

	/**
	 * Constructs a new WCAG2ICT test protocol
	 */
	public AccessibilityProtocol(Evaluator evaluator) {
		super();
		this.evaluator = evaluator;
	}

	/**
	 * Protocol method: evaluates the given state
	 * @param state The state.
	 * @return The verdict.
	 */
	@Override
	protected Verdict getVerdict(State state) {
		Verdict verdict = super.getVerdict(state);
		if (!verdict.equals(Verdict.OK)) {
			// something went wrong upstream
			return verdict;
		}
		// safe only the relevant widgets to use when computing a verdict and deriving actions
		relevantWidgets = getRelevantWidgets(state);
		EvaluationResults results = evaluator.evaluate(relevantWidgets);
		state.set(WCAG2Tags.EvaluationResults, results);
		return results.getOverallVerdict();
	}

	/**
	 * Protocol method: derives the follow-up actions from the given state
	 * @param state The state.
	 * @return The set of actions.
	 */
	@Override
	protected Set<Action> deriveActions(SUT system, State state) throws ActionBuildException {
		Set<Action> actions = super.deriveActions(system, state);
		if (actions.isEmpty()) {
			// no upstream actions, so evaluate accessibility
			actions = evaluator.deriveActions(relevantWidgets);
		}
		return actions;
	}
	
	private List<Widget> getRelevantWidgets(State state) {
		List<Widget> widgets = new ArrayList<>();
		double maxZIndex = state.get(Tags.MaxZIndex);
		for (Widget w : state)
			if (isUnfiltered(w)
					&& w.get(Tags.ZIndex) == maxZIndex
					&& AccessibilityUtil.isRelevant(w))
				widgets.add(w);
		return widgets;
	}

}
