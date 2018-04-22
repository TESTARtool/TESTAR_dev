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
