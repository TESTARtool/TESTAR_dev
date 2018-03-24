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

import java.util.Set;

/**
 * Graph action/edge.
 * 
 * @author Urko Rueda Molina (alias: urueda)
 *
 */
public interface IGraphAction {

	//public Action getAction();

	public String getConcreteID(); 
	public String getAbstractID(); 
	
	public void setStateshot(String scrShotPath);	
	public String getStateshot();

	/**
	 * @param memUsage In KB.
	 */
	public void setMemUsage(int memUsage);
	public int getMemUsage();
	
	/**
	 * @param cpuUsage User x system ... In ms.
	 */
	public void setCPUsage(long[] cpuUsage);
	public long[] getCPUsage();
	
	public String getRole();
	
	public String getDetailedName();
	public void setDetailedName(String detailedName);

	public String getTargetWidgetID();
	public void setTargetWidgetID(String targetWidgetID);
	
	public String getSourceStateID();
	public void setSourceStateID(String SourceStateID);
	public Set<String> getTargetStateIDs();
	public void addTargetStateID(String targetStateID);
	
	public int getCount();
	public void setCount(int count);	
	public void incCount();

	public String getOrder(String targetStateID);
	public String getOrder(Set<String> targetStatesID);
	public void addOrder(String targetStateID, String order);
	public String getLastOrder(String targetStateID);
		
	public void knowledge(boolean k);
	public boolean knowledge();
	public void revisited(boolean r);
	public boolean revisited();
	
}
