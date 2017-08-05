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

package nl.ou.testar.a11y.wcag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.fruit.alayer.Action;
import org.fruit.alayer.SUT;
import org.fruit.alayer.State;
import org.fruit.alayer.Verdict;

/**
 * An abstract WCAG guideline
 * Subclasses implement specific guideline behavior.
 * @author Davy Kager
 *
 */
public abstract class AbstractGuideline extends ItemBase {
	
	protected final Principle parent;

	protected AbstractGuideline(int nr, String name, Principle parent) {
		super(nr, name);
		this.parent = parent;
	}
	
	@Override
	public String getNr() {
		return parent.getNr() + "." + nr;
	}
	
	protected abstract Verdict getVerdict(State state);
	protected abstract Set<Action> deriveActions(State state);
	
}
