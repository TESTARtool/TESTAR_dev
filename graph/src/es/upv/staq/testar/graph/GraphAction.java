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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.fruit.alayer.Action;
import org.fruit.alayer.Role;
import org.fruit.alayer.Tags;

/**
 * Represents a graph action.
 * 
 * @author Urko Rueda Molina (alias: urueda)
 *
 */
public class GraphAction implements IGraphAction {
	
	//private WeakReference<Action> action;
	//private Object actionZipped;
	
	private String stateShotPath = null;
	
	private String concreteID, abstractID;

	private String role = "null"; // graph action role (ActionRoles)
	private String detailedName = "???"; // graph action descriptive representation
	private String targetWidgetID = null; // for actions that apply to single target, keep the target ID (abstract)
	private String sourceStateID = null;
	private Map<String,String> targetStateIDs = new HashMap<String,String>(); // target state concrete ID x action order in a test sequence (may appear multiple times)
	
	private int count = 1; // number of times action was executed
		
	private List<Integer> memUsage = new ArrayList<Integer>();; // in KB
	private List<Long[]> cpuUsage = new ArrayList<Long[]>(); // in ms (user x event x frame)
	
	private boolean knowledge = false;
	private boolean revisited = false;
	
	public GraphAction(String id){
		this.concreteID = id;
		this.abstractID = id;
	}
	
	public GraphAction(String concreteID, String abstractID){
		this.concreteID = concreteID;
		this.abstractID = abstractID;
	}
	
	/**
	 * @param action Non null action.
	 */
	public GraphAction(Action action){ //, String stateactionID, String actionID, String abstractID){
		//this.action = new WeakReference<Action>((action == null) ? new NOP() : action);
		//this.actionZipped = ZipManager.compress(action);
		//if (this.actionZipped == action)
		//	this.action = null; // compression failed
		this.concreteID = action.get(Tags.ConcreteID);
		this.abstractID = action.get(Tags.AbstractID);
		Role r = action.get(Tags.Role,null);
		if (r != null)
			this.role = r.name();
		this.targetWidgetID = action.get(Tags.TargetID,null);
	}
	
	/*@Override
	public Action getAction(){
		Action a = this.action == null ? null : this.action.get();
		if (a != null)
			return a;
		if (this.actionZipped instanceof byte[])
			return (Action) ZipManager.uncompress((byte[])this.actionZipped);
		else
			return (Action) this.actionZipped;
	}*/
	
	@Override
	public String getConcreteID(){
		return this.concreteID;
	}
	
	@Override
	public String getAbstractID(){
		return this.abstractID;
	}
	
	@Override
	public void setStateshot(String scrShotPath) {
		stateShotPath = scrShotPath;
	}
	
	@Override
	public String getStateshot(){
		return stateShotPath;
	}		
	
	@Override
	public void setMemUsage(int memUsage){
		this.memUsage.add(new Integer(memUsage));
	}
	
	@Override
	public int getMemUsage(){
		if (this.memUsage.isEmpty())
			return -1;
		else
			return (this.memUsage.remove(0)).intValue();
	}
	
	@Override
	public void setCPUsage(long[] cpuUsage){
		this.cpuUsage.add(new Long[]{cpuUsage[0],cpuUsage[1],cpuUsage[2]});
	}
	
	@Override
	public long[] getCPUsage(){
		if (this.cpuUsage.isEmpty())
			return new long[]{ -1, -1, -1 };
		else{
			Long[] cu = this.cpuUsage.remove(0);
			return new long[]{ cu[0].longValue(), cu[1].longValue(), cu[2].longValue() };
		}
	}
	
	@Override
	public String getRole(){
		return this.role;
	}
	
	@Override
	public String getDetailedName() {
		return detailedName;
	}

	@Override
	public void setDetailedName(String detailedName){
		this.detailedName = detailedName;
	}
	
	@Override
	public String getTargetWidgetID(){
		return this.targetWidgetID;
	}
	
	@Override
	public void setTargetWidgetID(String targetWidgetID){
		this.targetWidgetID = targetWidgetID;
	}
	
	@Override
	public String getSourceStateID(){
		return this.sourceStateID;
	}
	
	@Override
	public void setSourceStateID(String sourceStateID){
		this.sourceStateID = sourceStateID;
	}
	
	@Override
	public Set<String> getTargetStateIDs(){
		return this.targetStateIDs.keySet();
	}
	
	@Override
	public void addTargetStateID(String targetStateID){
		if (!this.targetStateIDs.containsKey(targetStateID))
			this.targetStateIDs.put(targetStateID,"");
	}
	
	@Override	
	public int getCount(){
		return count;
	}

	@Override
	public void setCount(int count){
		this.count = count;
	}
	
	@Override
	public void incCount(){
		count++;
	}

	@Override
	public String getOrder(String targetStateID){
		return this.targetStateIDs.get(targetStateID);
	}

	@Override
	public String getOrder(Set<String> targetStatesID){
		String order = "", targetOrder;
		if (targetStatesID == null)
			return order;
		for (String tid : targetStatesID){
			targetOrder = this.getOrder(tid);
			if (targetOrder != null)
				order += targetOrder;
		}
		return order;
	}
	
	@Override
	public void addOrder(String targetStateID, String order){
		String o = this.targetStateIDs.get(targetStateID);
		this.targetStateIDs.put(targetStateID, (o == null ? "" : o) + "[" + order + "]");
	}
	
	@Override
	public String getLastOrder(String targetStateID){
		String order = this.targetStateIDs.get(targetStateID);
		if (order.isEmpty())
			return null;
		else
			return order.substring(order.lastIndexOf("[")+1,order.length()-1);
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
		if (o == null) return false;
		if(!(o instanceof GraphAction))
			return false;
		return this.concreteID.equals(((GraphAction)o).getConcreteID());
	}
	
	@Override
	public void knowledge(boolean k){
		this.knowledge = k;
	}

	@Override
	public boolean knowledge(){
		return this.knowledge;
	}
	
	//@Override
	public void revisited(boolean r){
		this.revisited = r;
	}
	
	@Override
	public boolean revisited(){
		return this.revisited;
	}


	
}
