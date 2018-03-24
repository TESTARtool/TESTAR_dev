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

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import org.fruit.alayer.State;

import es.upv.staq.testar.graph.IEnvironment;
import es.upv.staq.testar.graph.IGraphState;

/**
 * Utility class for walkers.
 * 
 * @author Urko Rueda Molina (alias: urueda)
 *
 */
public class WalkerUtil {

	public static List<IGraphState> getPathToLessExploredState(IWalker walker, IEnvironment env, State currentState){
		try {
			List<IGraphState> pathTargetState = null;
			IGraphState cgs = env.get(currentState);
			if (cgs == null || !env.stateAtGraph(cgs)){
				System.out.println("[WalkerUtil] Unexplored state found while trying to get a path to less explored!");
				return null;
			}
			System.out.println("[WalkerUtil] Trying to get a path to less explored");
			
			Collection<IGraphState> statesSet = env.getGraphStates();
			if (statesSet == null || statesSet.isEmpty()){
				System.out.println("[WalkerUtil] Empty graph - no graph states");
				return null;
			}
			
			IGraphState[] states = statesSet.toArray(new IGraphState[statesSet.size()]);
			System.out.println("[WalkerUtil] Sorting <" + states.length + "> graph states");
			Arrays.sort(states, new Comparator<IGraphState>(){
				@Override
				public int compare(IGraphState s1, IGraphState s2) { // reverse ordering (big -> small)
					if (walker.getStateReward(env,s1) > walker.getStateReward(env,s2))
						return -1;
					else if (walker.getStateReward(env,s1) < walker.getStateReward(env,s2))
						return 1;
					else
						return 0;
					}
			});			
			System.out.println("[WalkerUtil] Sorted <" + states.length + "> graph states");
			
			IGraphState gsGoal = null; int i=0;
			while (i < states.length && (pathTargetState == null || pathTargetState.isEmpty())){
				gsGoal = states[i]; i++;
				if (gsGoal == cgs || walker.getStateReward(env,gsGoal) == 0){
					System.out.println("[WalkerUtil] Current state is one of less explored");
					return null; // current state is proper exploration goal
				}
				System.out.println("[WalkerUtil] Get path to state <" + gsGoal.getConcreteID() + "> [" + i + "/" + states.length + "]");
				pathTargetState = env.getPath(cgs,gsGoal);
			}
	
			if (pathTargetState != null && !pathTargetState.isEmpty())
				pathTargetState.add(0, cgs);
			return pathTargetState;
		} catch (Exception e){
			System.out.println("WalkerUtil exception:");
			e.printStackTrace();
			return null;
		}
	}
	
}