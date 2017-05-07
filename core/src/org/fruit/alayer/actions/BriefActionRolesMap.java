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
 * This software is distributed FREE of charge under the TESTAR license, as an open      *
 * source project under the BSD3 licence (http://opensource.org/licenses/BSD-3-Clause)   *                                                                                        * 
 *                                                                                       *
 *****************************************************************************************/

package org.fruit.alayer.actions;

import java.util.HashMap;

/**
 * ActionRoles brief map.
 * 
 * @author Urko Rueda Molina (alias: urueda)
 *
 */
public class BriefActionRolesMap {

	public static final HashMap<String,String> map = new HashMap<String,String>(){{
		put("LeftClickAt","LC"); // ActionRoles.LeftClickAt
		put("RightClickAt","RC"); // ActionRoles.RightClickAt
		put("ClickTypeInto","T"); // ActionRoles.ClickTypeInto
	}};
	
}
