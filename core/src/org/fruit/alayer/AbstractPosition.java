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
 * in the context of the STaQ (Software Testing & Quality) research group.                               *

 * This software is distributed FREE of charge under the TESTAR license, as an open      *
 * source project under the BSD3 licence (http://opensource.org/licenses/BSD-3-Clause)   *                                                                                        * 
 *                                                                                       *
 *****************************************************************************************/

package org.fruit.alayer;

/**
 * 
 * @author Urko Rueda Molina (alias: urueda)
 *
 */
public abstract class AbstractPosition implements Position {

	private static final long serialVersionUID = -4924203436239979302L;

	boolean obscuredByChildEnabled = true;

	@Override
	public void obscuredByChildFeature(boolean enable){
		this.obscuredByChildEnabled = enable;
	}
	
}
