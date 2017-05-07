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

import java.util.Map;
import java.util.Set;

import org.fruit.alayer.Action;
import org.fruit.alayer.Verdict;

public interface IGraphState {
	
	//public State getState();
	
	public String getConcreteID();
	public String getAbstract_R_ID();
	public String getAbstract_R_T_ID();
	public String getAbstract_R_T_P_ID();
	
	public Map<String,Integer> getStateWidgetsExecCount();
	public String getParent(String widgetID);
	public Map<String,String> getWidgetProperties(String widgetID);

	public void setStateshot(String scrShotPath);
	public String getStateshot();

	public int getCount();
	public void setCount(int count);
	public void incCount();
		
	public void updateUnexploredActions(IEnvironment env,
										Set<Action> availableActions,
										Set<String> exploredActions);
	
	public int getUnexploredActionsSize();
	public String getUnexploredActionsString();
	
	/**
	 * 
	 * @param aid Abstract action ID.
	 */
	public void actionExplored(String aid);
	/**
	 * 
	 * @param aid Abstract action ID.
	 */
	public void actionUnexplored(String aid);	
	
	public Verdict getVerdict();
		
	public void actionExecuted(String targetWidgetID);
	
	public void knowledge(boolean k);
	public boolean knowledge();
	public boolean revisited();
	
}
