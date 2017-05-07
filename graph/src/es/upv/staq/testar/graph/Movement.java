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


/**
 * Represents a movement in a test sequence as an executed action from a SUT state.
 * 
 * @author Urko Rueda Molina (alias: urueda)
 */
public class Movement {
	
	private IGraphState vertex;
	private IGraphAction edge;
	
	/**
	 * Constructor.
	 * @param v Graph state before action execution.
	 * @param e Executed graph action.
	 */
	public Movement(IGraphState v, IGraphAction e){
		vertex=v;
		edge=e;
	}

	public IGraphState getVertex() {
		return vertex;
	}

	public IGraphAction getEdge() {
		return edge;
	}
	
	@Override
	public String toString(){
		return "Movement: edge_" + getEdge().toString() + " @vertex_" + getVertex().toString();
	}
	
}
