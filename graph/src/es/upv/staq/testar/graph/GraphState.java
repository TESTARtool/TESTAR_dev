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
 * source project under the BSD3 licence (http://opensource.org/licenses/BSD-3-Clause)   *                                                                                        * 
 *                                                                                       *
 *****************************************************************************************/

package es.upv.staq.testar.graph;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.fruit.alayer.Action;
import org.fruit.alayer.State;
import org.fruit.alayer.Tags;
import org.fruit.alayer.Verdict;
import org.fruit.alayer.Widget;

/**
 * Represents a grah state.
 * 
 * @author Urko Rueda Molina (alias: urueda)
 *
 */
public class GraphState implements IGraphState {
	
	//private WeakReference<State> state;
	//private Object stateZipped;
	
	private Map<String,Integer> stateWidgetsExecCount = new HashMap<String,Integer>();; // widget ID -> execution count (how many actions on the widget)
	private Map<String,String> widgetsHierarchy = new HashMap<String,String>(); // widget ID -> parent widget ID
	private Map<String,Map<String,String>> widgetsProperties = new HashMap<String,Map<String,String>>(); // widget ID -> widget properties
		
	private String stateShotPath = null;
	
	private String concreteID, abstract_R_ID = null, abstract_R_T_ID = null, abstract_R_T_P_ID = null;
	
	private int count = 1; // number of times state was traversed
	
	private GraphStateExploration actionsExploration = new GraphStateExploration();;
	
	private Verdict verdict;

	private boolean knowledge = false;
	private boolean revisited = false;

	public GraphState(String id){
		this.concreteID = id;
		this.abstract_R_ID = id;
		this.abstract_R_T_ID = id;
		this.abstract_R_T_P_ID = id;
	}

	public GraphState(String id, String abstract_R_ID){
		this.concreteID = id;
		this.abstract_R_ID = abstract_R_ID;
		this.abstract_R_T_ID = id;
		this.abstract_R_T_P_ID = id;		
	}
	
	/**
	 * @param state Non null state
	 */
	public GraphState(State state){
		//this.state = new WeakReference<State>((state == null) ? new StdState() : state);
		//this.stateZipped = ZipManager.compress(state);
		//if (this.stateZipped == state)
		//	this.state = null; // compression failed
		this.concreteID = state.get(Tags.ConcreteID);
		this.abstract_R_ID = state.get(Tags.Abstract_R_ID);
		this.abstract_R_T_ID = state.get(Tags.Abstract_R_ID);
		this.abstract_R_T_P_ID = state.get(Tags.Abstract_R_ID);
		if (state.childCount() > 0){
			String wid;
			Map<String,String> wprops;
			for (Widget w : state){
				if (w != state){
					wid = w.get(Tags.ConcreteID);
					this.stateWidgetsExecCount.put(wid,new Integer(0));
					this.widgetsHierarchy.put(wid,w.parent().get(Tags.ConcreteID));
					wprops = new HashMap<String,String>();					
					wprops.put(Tags.Title.name(),w.get(Tags.Title,""));
					wprops.put(Tags.Role.name(),w.get(Tags.Role).name());
					wprops.put(Tags.Path.name(),w.get(Tags.Path));
					this.widgetsProperties.put(wid,wprops);
				}
			}
		}
		this.verdict = state.get(Tags.OracleVerdict, Verdict.OK);
		if (this.verdict.severity() == Verdict.SEVERITY_OK)
			this.verdict = null;
	}
	
	/*@Override
	public State getState(){
		State s = this.state == null ? null : this.state.get();
		if (s != null)
			return s;
		if (this.stateZipped instanceof byte[])
			return (State) ZipManager.uncompress((byte[])this.stateZipped);
		else
			return (State) this.stateZipped;
	}*/

	@Override
	public String getConcreteID(){
		return this.concreteID;
	}

	@Override
	public String getAbstract_R_ID(){
		return this.abstract_R_ID;
	}
	
	@Override
	public String getAbstract_R_T_ID(){
		return this.abstract_R_T_ID;
	}
	
	@Override
	public String getAbstract_R_T_P_ID(){
		return this.abstract_R_T_P_ID;
	}
	
	@Override
	public Map<String,Integer> getStateWidgetsExecCount(){
		return this.stateWidgetsExecCount;
	}
	
	@Override
	public String getParent(String widgetID){
		return this.widgetsHierarchy.get(widgetID);
	}
	
	@Override
	public Map<String,String> getWidgetProperties(String widgetID){
		return this.widgetsProperties.get(widgetID);
	}
	
	@Override
	public void setStateshot(String scrShotPath) {
		this.stateShotPath = scrShotPath;
	}

	@Override
	public String getStateshot(){
		return this.stateShotPath;
	}
	
	@Override
	public int getCount(){
		return this.count;
	}

	@Override
	public void setCount(int count){
		this.count = count;
	}
	
	@Override
	public void incCount(){
		this.count++;
		this.revisited = true;
	}

	@Override
	public void updateUnexploredActions(IEnvironment env,
										Set<Action> availableActions, Set<String> exploredActions){
		actionsExploration.updateUnexploredActions(env,this,availableActions,exploredActions);
	}

	@Override
	public int getUnexploredActionsSize(){
		return actionsExploration.getUnexploredActionsSize();
	}
	
	@Override
	public String getUnexploredActionsString(){
		return actionsExploration.getUnexploredActionsString();
	}

	@Override
	public void actionExplored(String aid){
		actionsExploration.actionExplored(aid);
	}
	
	@Override
	public void actionUnexplored(String aid){
		actionsExploration.actionUnexplored(aid);
	}
	
	@Override
	public Verdict getVerdict(){
		return this.verdict;
	}
	
	@Override
	public int hashCode(){
		return this.concreteID.hashCode();
	}

	@Override
	public String toString(){
		return this.concreteID;
	}

	public boolean equals(Object o){
		if(this == o)
			return true;
		if(!(o instanceof GraphState))
			return false;
		return concreteID.equals(((GraphState)o).getConcreteID());
	}
	
	public void actionExecuted(String targetWidgetID){
		Integer wc = this.stateWidgetsExecCount.get(targetWidgetID);
		if (wc != null)
			this.stateWidgetsExecCount.put(targetWidgetID, new Integer(wc.intValue() + 1));
	}
	
	public void knowledge(boolean k){
		this.knowledge = k;
	}

	public boolean knowledge(){
		return this.knowledge;
	}
	
	public boolean revisited(){
		return this.revisited;
	}
		
}
