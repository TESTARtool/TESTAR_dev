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
public class FactsAction {
	
	// action(A,S,W,T,O) 			: A = UI action, number 'O' in the test sequence, of type T over widget W in state S
	// 					   			  O = 0 if it is a discovered action (not executed)
	// action(A,S,W,'LeftClick',0) 	: A = feasible left click UI action over widget W of state S
	public static final String FACT_ACTION = "action";

	public static Set<String> getRules(){
		Set<String> actionsRules = new HashSet<String>();
		actionsRules.add(FACT_ACTION + "(A,S,W," + ActionRoles.LeftClick + ",0):-widget(W,S),role(W,R),member(R,['" +
			NativeLinker.getNativeRole_Button() + "','" +
			NativeLinker.getNativeRole_Menuitem() +
			"'])."); // action(A,S,W,'LeftClick',0)
		return actionsRules;
	}

    // Atoms:
    public static Set<String> getFacts(State state, Set<Action> actions){
    	Set<String> facts = new HashSet<String>();
    	
    	// atoms
    	for (Action a : actions)
    		facts.addAll(getAtoms(state,a));
    	
    	return facts;
    }
    
    private static Set<String> getAtoms(State state, Action a){
    	Set<String> facts = new HashSet<String>();
	
    	String sid = state.get(Tags.ConcreteID);
    	String aid = a.get(Tags.ConcreteID);
    	String role = "null";
    	Role r = a.get(Tags.Role,null);
    	if (r != null)
    		role = r.name();
		String target = a.get(Tags.TargetID,null);
		if (target == null)
			target = "null";
	    facts.add(FACT_ACTION + "('" + aid + "','" + sid + "','" + target + "','" + role + "',0)."); // action(A,S,W,T,0)
    	
    	return facts;
    }
    
    public static void debugQueries(JIPrologWrapper wrapper){
    	wrapper.debugQuery(FACT_ACTION + "(A,S,W,T,O).");
		//wrapper.debugQuery("clickable(W,S).");
    }
    
}