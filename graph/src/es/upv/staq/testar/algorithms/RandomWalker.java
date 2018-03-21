/*****************************************************************************************
 *                                                                                       *
 * COPYRIGHT (2015):                                                                     *
 * Universitat Politecnica de Valencia                                                   *
 * Camino de Vera, s/n                                                                   *
 * 46022 Valencia, Spain                                                                 *
 * www.upv.es                                                                            *
 *                                                                                       * 
 * D I S C L A I M E R:                                                                  *
 * This software has been developed by the Universitat Politecnica de Valencia (UPV)     *
 * in the context of the TESTAR Proof of Concept project:                                *
 *               "UPV, Programa de Prueba de Concepto 2014, SP20141402"                  *
 * This graph project is distributed FREE of charge under the TESTAR license, as an open *
 * source project under the BSD3 licence (http://opensource.org/licenses/BSD-3-Clause)   *
 *                                                                                       * 
 *****************************************************************************************/

package es.upv.staq.testar.algorithms;

import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

import org.fruit.alayer.Action;
import org.fruit.alayer.State;

import es.upv.staq.testar.graph.IEnvironment;
import es.upv.staq.testar.prolog.JIPrologWrapper;

/**
 * A random walker.
 * 
 * @author Urko Rueda Molina (alias: urueda)
 *
 */
public class RandomWalker extends AbstractWalker {

	protected Random rnd;
	
	public RandomWalker(Random rnd){
		this.rnd = rnd;
	}

	@Override
	public Action selectAction(IEnvironment env, State state, Set<Action> actions, JIPrologWrapper jipWrapper) {
		return new ArrayList<Action>(actions).get(rnd.nextInt(actions.size()));
	}

}
