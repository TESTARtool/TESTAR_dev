/***************************************************************************************************
*
* Copyright (c) 2015, 2016, 2017 Universitat Politecnica de Valencia - www.upv.es
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

	private boolean knowledge = false,
					revisited = false;

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
	public void actionExecuted(String targetWidgetID){
		Integer wc = this.stateWidgetsExecCount.get(targetWidgetID);
		if (wc != null)
			this.stateWidgetsExecCount.put(targetWidgetID, new Integer(wc.intValue() + 1));
	}
	
	@Override
	public void knowledge(boolean k){
		this.knowledge = k;
	}

	@Override
	public boolean knowledge(){
		return this.knowledge;
	}
	
	@Override
	public void revisited(boolean r){
		this.revisited = r;
	}
	
	@Override
	public boolean revisited(){
		return this.revisited;
	}
	
	@Override
	public int hashCode(){
		return this.concreteID.hashCode();
	}

	@Override
	public String toString(){
		return this.concreteID;
	}

	@Override
	public boolean equals(Object o){
		if(this == o)
			return true;
		if(!(o instanceof GraphState))
			return false;
		return this.concreteID.equals(((GraphState)o).getConcreteID());
	}	
	
}
