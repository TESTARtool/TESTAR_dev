/*****************************************************************************************
 *                                                                                       *
 * COPYRIGHT (2016):                                                                     *
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
 * source project under the BSD3 licence (http://opensource.org/licenses/BSD-3-Clause)   *                                                                                        * 
 *                                                                                       *
 *****************************************************************************************/

package es.upv.staq.testar.graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.fruit.alayer.Action;
import org.fruit.alayer.Role;
import org.fruit.alayer.Tags;
import org.fruit.alayer.actions.ActionRoles;

/**
 * Graph state exploration status.
 * 
 * @author Urko Rueda Molina (alias: urueda)
 *
 */
public class GraphStateExploration {

	private Set<String> unexploredActions; // actions IDs (abstract)
	
	// textbox fields => typing actions with oo texts parameter => huge set population	
	private Map<String,Integer> unexploredTypings; // action ID (abstract) x typed count

	public GraphStateExploration(){
		this.unexploredActions = new HashSet<String>();
		this.unexploredTypings = new HashMap<String,Integer>();
	}
	
	public void updateUnexploredActions(IEnvironment env, IGraphState gs,
										Set<Action> availableActions, Set<String> exploredActions){
		String aid;
		Role role;
		synchronized(this.unexploredActions){
			for (Action a : availableActions){
				aid = a.get(Tags.AbstractID);
				role = a.get(Tags.Role, null);
				if (role != null && Role.isOneOf(role, ActionRoles.Type, ActionRoles.ClickTypeInto)){ // typing action
					if (this.unexploredTypings.get(aid) == null)
						this.unexploredTypings.put(aid,new Integer(0));
					else
						continue; // skip loop action
				} else if (exploredActions.contains(aid))
					continue; // skip explored action
				this.unexploredActions.add(aid);
			}
		}
	}
	
	/**
	 * 
	 * @param aid Abstract action ID.
	 */
	public void actionExplored(String aid){
		synchronized(this.unexploredActions){
			Integer typedCount = this.unexploredTypings.get(aid);
			if (typedCount == null)
				this.unexploredActions.remove(aid);
			else{
				if (typedCount.intValue() + 1 >= Grapher.TYPING_TEXTS_FOR_EXECUTED_ACTION)
					this.unexploredActions.remove(aid);
				else
					this.unexploredTypings.put(aid, new Integer(typedCount.intValue() + 1));
			}
		}
	}
		
	/**
	 * 
	 * @param aid Abstract action ID.
	 */
	public void actionUnexplored(String aid){
		synchronized(this.unexploredActions){
			this.unexploredActions.add(aid);
		}		
	}
	
	public int getUnexploredActionsSize(){
		synchronized(this.unexploredActions){
			return this.unexploredActions.size();
		}
	}
	
	public String getUnexploredActionsString(){
		synchronized(this.unexploredActions){
			if (this.unexploredActions.isEmpty())
				return "[]";
			else {
				StringBuilder sb = new StringBuilder();
				for (String ua : this.unexploredActions){
					sb.append(ua + ",");
				}
				return "[" + sb.toString().substring(0,sb.length()-1) + "]";
			}
		}
	}
	
}