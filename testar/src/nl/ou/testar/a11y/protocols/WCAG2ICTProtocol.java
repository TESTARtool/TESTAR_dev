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

import java.util.Set;

import org.fruit.alayer.Action;
import org.fruit.alayer.SUT;
import org.fruit.alayer.State;
import org.fruit.alayer.Verdict;
import org.fruit.alayer.exceptions.ActionBuildException;
import org.fruit.monkey.DefaultProtocol;

import nl.ou.testar.a11y.wcag2.EvaluationResults;
import nl.ou.testar.a11y.wcag2.WCAG2ICT;

/**
 * Test protocol for WCAG2ICT
 * @author Davy Kager
 *
 */
public class WCAG2ICTProtocol extends DefaultProtocol {
	
	private final WCAG2ICT wcag;

	/**
	 * Constructs a new WCAG2ICT test protocol
	 */
	public WCAG2ICTProtocol() {
		super();
		wcag = new WCAG2ICT();
	}

	@Override
	protected Verdict getVerdict(State state) {
		Verdict verdict = super.getVerdict(state);
		if (!verdict.equals(Verdict.OK)) {
			// something went wrong upstream
			return verdict;
		}
		EvaluationResults results = wcag.evaluate(getTopWidgets(state));
		return results.getOverallVerdict();
	}

	@Override
	protected Set<Action> deriveActions(SUT system, State state) throws ActionBuildException {
		Set<Action> actions = super.deriveActions(system, state);
		if (actions.isEmpty()) {
			// no upstream actions, so evaluate accessibility
			actions = wcag.deriveActions(getTopWidgets(state));
		}
		return actions;
	}

}
