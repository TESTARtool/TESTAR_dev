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

package org.fruit.a11y.wcag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.fruit.alayer.Action;
import org.fruit.alayer.SUT;
import org.fruit.alayer.State;
import org.fruit.alayer.Verdict;

public final class WCAG2Dot0 {
	
	private final List<Principle> principles = new ArrayList<Principle>();
	
	public WCAG2Dot0() {
	}

	public List<Principle> getPrinciples() {
		return principles;
	}
	
	public List<Verdict> getVerdicts(State state) {
		List<Verdict> verdicts = new ArrayList<Verdict>();
		for (Principle p : getPrinciples())
			for (AbstractGuideline g : p.getGuidelines())
				verdicts.add(g.getVerdict(state));
		return verdicts;
	}
	
	public Set<Action> deriveActions(State state) {
		Set<Action> actions = Collections.emptySet();
		for (Principle p : getPrinciples())
			for (AbstractGuideline g : p.getGuidelines())
				actions.addAll(g.deriveActions(state));
		return actions;
	}
	
}
