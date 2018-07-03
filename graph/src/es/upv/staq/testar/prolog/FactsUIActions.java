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


package es.upv.staq.testar.prolog;

import java.util.HashSet;
import java.util.Set;

import org.fruit.alayer.Action;
import org.fruit.alayer.Role;
import org.fruit.alayer.State;
import org.fruit.alayer.Tags;
import org.fruit.alayer.actions.ActionRoles;

import es.upv.staq.testar.NativeLinker;

/**
 * Facts for TESTAR actions (UI user events).
 * 
 * @author Urko Rueda Molina (alias: urueda)
 *
 */
public class FactsUIActions {
	
	// action(A,S,W,T,O) 			: A = UI action, number 'O' in the test sequence, of type T over widget W in state S
	// 					   			  O = 0 if it is a discovered action (not executed)
	// action(A,S,W,'LeftClick',0) 	: A = feasible left click UI action over widget W of state S
	public static final String FACT_ACTION = "action";

	public static Set<String> getRules() {
		Set<String> actionsRules = new HashSet<String>();
		actionsRules.add(FACT_ACTION + "(A,S,W," + ActionRoles.LeftClick + ",0):-widget(W,S),role(W,R),member(R,['" 
		+ NativeLinker.getNativeRole_Button() + "','"
		+ NativeLinker.getNativeRole_Menuitem()
		+ "'])."); // action(A,S,W,'LeftClick',0)
		return actionsRules;
	}

    // Atoms:
    public static Set<String> getFacts(State state, Set<Action> actions) {
    	Set<String> facts = new HashSet<String>();
    	
    	// atoms
    	for (Action a : actions) {
    		facts.addAll(getAtoms(state,a));
    	}
    	
    	return facts;
    }
    
    private static Set<String> getAtoms(State state, Action a) {
    	Set<String> facts = new HashSet<String>();
	
    	String sid = state.get(Tags.ConcreteID);
    	String aid = a.get(Tags.ConcreteID);
    	String role = "null";
    	Role r = a.get(Tags.Role,null);
    	if (r != null) {
    		role = r.name();
    	}
		String target = a.get(Tags.TargetID,null);
		if (target == null) {
			target = "null";
		}
	    facts.add(FACT_ACTION + "('" + aid + "','" + sid + "','" + target + "','" + role + "',0)."); // action(A,S,W,T,0)
    	
    	return facts;
    }
    
    public static void debugQueries(JIPrologWrapper wrapper) {
    	wrapper.debugQuery(FACT_ACTION + "(A,S,W,T,O).");
    }
}
