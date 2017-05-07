/**********************************************************************************************
 *                                                                                            *
 * COPYRIGHT (2016):                                                                          *
 * Universitat Politecnica de Valencia                                                        *
 * Camino de Vera, s/n                                                                        *
 * 46022 Valencia, Spain                                                                      *
 * www.upv.es                                                                                 *
 *                                                                                            * 
 * D I S C L A I M E R:                                                                       *
 * This software has been developed by the Universitat Politecnica de Valencia (UPV)          *
 * in the context of the STaQ (Software Testing and Quality) research group: staq.dsic.upv.es *
 * This software is distributed FREE of charge under the TESTAR license, as an open           *
 * source project under the BSD3 license (http://opensource.org/licenses/BSD-3-Clause)        *                                                                                        * 
 *                                                                                            *
 **********************************************************************************************/

package es.upv.staq.testar.algorithms;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.fruit.alayer.Action;
import org.fruit.alayer.State;
import org.fruit.alayer.Tags;

import es.upv.staq.testar.graph.IEnvironment;
import es.upv.staq.testar.prolog.JIPrologWrapper;
import es.upv.staq.testar.prolog.PrologUtil;

/**
 * A prolog walker.
 * 
 * @author Urko Rueda Molina (alias: urueda)
 *
 */
public class PrologWalker extends AbstractWalker {

	private Random rnd;
	
	public PrologWalker(Random rnd){
		this.rnd = rnd;
	}

	/**
	 * Sample prolog selection: Actions in menu items.
	 */
	@Override
	public Action selectAction(IEnvironment env, State state, Set<Action> actions, JIPrologWrapper jipWrapper) {	
		List<List<String>> solutions = jipWrapper.setQuery("action(A,S,W,T,O),role(W,'UIAMenuItem').");
		//PrologUtil.printSolutions(solutions);
		List<String> solutionsA = PrologUtil.getSolutions("A", solutions);
		List<Action> candidates = new ArrayList<Action>(solutionsA.size());
		Action a;
		for (String as : solutionsA){
			a = get(as,actions);
			if (a != null)
				candidates.add(a);
		}
		if (candidates.isEmpty())
			return new ArrayList<Action>(actions).get(rnd.nextInt(actions.size()));
		else
			return new ArrayList<Action>(candidates).get(rnd.nextInt(candidates.size()));
	}

	private Action get(String concreteID, Set<Action> actions){
		for (Action a : actions){
			if (a.get(Tags.ConcreteID).equals(concreteID))
				return a;
		}
		return null;
	}
	
}
