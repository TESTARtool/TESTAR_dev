/******************************************************************************************
 * COPYRIGHT:                                                                             *
 * Universitat Politecnica de Valencia 2013                                               *
 * Camino de Vera, s/n                                                                    *
 * 46022 Valencia, Spain                                                                  *
 * www.upv.es                                                                             *
 *                                                                                        * 
 * D I S C L A I M E R:                                                                   *
 * This software has been developed by the Universitat Politecnica de Valencia (UPV)      *
 * in the context of the european funded FITTEST project (contract number ICT257574)      *
 * of which the UPV is the coordinator. As the sole developer of this source code,        *
 * following the signed FITTEST Consortium Agreement, the UPV should decide upon an       *
 * appropriate license under which the source code will be distributed after termination  *
 * of the project. Until this time, this code can be used by the partners of the          *
 * FITTEST project for executing the tasks that are outlined in the Description of Work   *
 * (DoW) that is annexed to the contract with the EU.                                     *
 *                                                                                        * 
 * Although it has already been decided that this code will be distributed under an open  *
 * source license, the exact license has not been decided upon and will be announced      *
 * before the end of the project. Beware of any restrictions regarding the use of this    *
 * work that might arise from the open source license it might fall under! It is the      *
 * UPV's intention to make this work accessible, free of any charge.                      *
 *****************************************************************************************/

/**
 *  @author Sebastian Bauersfeld
 */
package org.fruit.alayer.actions;

import org.fruit.alayer.Role;
import org.fruit.alayer.SUT;
import org.fruit.alayer.Tags;
import org.fruit.alayer.devices.KBKeys;

/**
 * An action which presses a given Key on the Keyboard.
 */
public final class KeyDown extends //TaggableBase 
		KeyAction { // by urueda 

	private static final long serialVersionUID = -462251384755779329L;
		
	public KeyDown(KBKeys key){
		super(key);
	}
	
	public String toString() { return "Press Key " + key; }

	// by urueda
	@Override
	public String toString(Role... discardParameters) {
		for (Role r : discardParameters){
			if (r.name().equals(ActionRoles.KeyDown.name()))
				return "Key pressed";
		}
		return toString();
	}
	
	// by urueda
	@Override
	protected void keyAction(SUT system, KBKeys key) {		
		system.get(Tags.StandardKeyboard).press(key);		
	}
	
}