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

import java.util.ArrayList;
import java.util.List;

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
	private String targetID = null; // for actions that apply to single target, keep the target ID (abstract)
	
	private int count = 0; // number of times action was executed
	private String order = ""; // action order in a test sequence (may appear multiple times)
		
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
		this.targetID = action.get(Tags.TargetID,null);
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
	public String getTargetID(){
		return this.targetID;
	}
	
	@Override
	public void setTargetID(String targetID){
		this.targetID = targetID;
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
		this.revisited = true;
	}

	@Override
	public String getOrder(){
		return order;
	}
	
	@Override
	public void addOrder(String order){		
		this.order = this.order + "[" + order + "]";
	}
	
	@Override
	public String getLastOrder(){
		if (this.order.isEmpty())
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
		if(!(o instanceof GraphAction))
			return false;
		return this.concreteID.equals(((GraphAction)o).getConcreteID());
	}
	
	@Override
	public IGraphAction clone(String clonID){
		GraphAction gaCloned = new GraphAction(this.concreteID + clonID,this.abstractID + clonID);
		gaCloned.setDetailedName(getDetailedName());
		gaCloned.setStateshot(getStateshot());
		gaCloned.setTargetID(getTargetID());
		return gaCloned;
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