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

import org.fruit.alayer.State;

/**
 * Graph test sequence stopping utility. 
 * 
 * @author Urko Rueda Molina (alias: urueda)
 */
public class WalkStopper {

	private boolean alive = true; // is walk alive?
	private boolean status = true; // test verdict?
	private State endState = null; // test sequence ending state?
	
	/**
	 * Is test sequence active? Is graph still being populated?
	 * @return
	 */
	public boolean continueWalking(){
		return alive;
	}
	
	/**
	 * Force walk to stop.
	 * @param status Test verdict: 'true' test OK, 'false' test FAIL.
	 * @param endState Ending state.
	 */
	public void stopWalk(boolean status, State endState){
		//System.out.println("STOPPED WALK!");
		alive = false;
		this.status = status;
		this.endState = endState;
	}
	
	public boolean walkStatus(){
		return status;
	}
	
	public State walkEndState(){
		return endState;
	}
	
}
