/***************************************************************************************************
*
* Copyright (c) 2016, 2017 Universitat Politecnica de Valencia - www.upv.es
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
*
* 1. Redistributions of source code must retain the above copyright notice,
* this list of conditions and the following disclaimer.
* 2. Redistributions in binary form must reproduce the above copyright
* notice, this list of conditions and the following disclaimer in the
* documentation and/or other materials provided with the distribution.
* 3. Neither the name of the copyright holder nor the names of its
* contributors may be used to endorse or promote products derived from
* this software without specific prior written permission.
*
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
* AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
* IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
* ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
* LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
* CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
* SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
* INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
* CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
* ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
* POSSIBILITY OF SUCH DAMAGE.
*******************************************************************************************************/


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
			System.out.println("[WalkerUtil] exception:");
			e.printStackTrace();
			return null;
		}
	}
	
}
