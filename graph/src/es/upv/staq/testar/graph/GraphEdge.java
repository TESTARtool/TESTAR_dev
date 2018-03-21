/**********************************************************************************************
 *                                                                                            *
 * COPYRIGHT (2017):                                                                          *
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

package es.upv.staq.testar.graph;

/**
 * A graph edge representation as a pair: action concrete ID x target state concrete ID
 * 
 * @author Urko Rueda Molina (alias: urueda)
 *
 */
public class GraphEdge {
	
	private String actionID, // concrete ID
				   targetStateID; // concrete ID
	
	private GraphEdge(){}
	
	public GraphEdge(String actionID, String targetStateID){
		this();
		this.actionID = actionID;
		this.targetStateID = targetStateID;
	}
	
	public String getActionID(){
		return this.actionID;
	}
	
	public String getTargetStateID(){
		return this.targetStateID;
	}

	@Override
	public boolean equals(Object o){
		if (o == this) return true;
		if (o == null) return false;
		if (!(o instanceof GraphEdge)) return false;
		GraphEdge edge = (GraphEdge) o;
		return edge.getActionID().equals(this.getActionID()) &&
			   edge.getTargetStateID().equals(this.getTargetStateID());
	}
	
	@Override
	public int hashCode(){
		return (this.actionID + this.targetStateID).hashCode(); 
	}
	
}
