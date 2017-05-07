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

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import org.fruit.Assert;
import org.fruit.Util;
import org.fruit.alayer.Action;
import org.fruit.alayer.Role;
import org.fruit.alayer.SUT;
import org.fruit.alayer.State;
import org.fruit.alayer.TaggableBase;
import org.fruit.alayer.Tags;
import org.fruit.alayer.devices.KBKeys;
import org.fruit.alayer.exceptions.ActionFailedException;

/**
 * An action that types a given text on the StandardKeyboard of the SUT.
 */
public final class Type extends TaggableBase implements Action {

	private static final long serialVersionUID = 2555715152455716781L;
	private static final CharsetEncoder asciiEncoder = Charset.forName("US-ASCII").newEncoder();
	private final String text;
	
	public Type(String text){
		Assert.hasText(text);
		checkAscii(text);
		this.text = text;
	}
	
	public void run(SUT system, State state, double duration) throws ActionFailedException {
		Assert.isTrue(duration >= 0);
		Assert.notNull(system);
		
		double d = duration / text.length();
		Action shiftDown = new KeyDown(KBKeys.VK_SHIFT);
		Action shiftUp = new KeyUp(KBKeys.VK_SHIFT);
		for(int i = 0; i < text.length(); i++){
			char c = text.charAt(i);
			boolean shift = false;

			if(Character.isLetter(c)){
				if(Character.isLowerCase(c))
					c = Character.toUpperCase(c);
				else
					shift = true;
			}
			
			KBKeys key = getKey(c);
						
			if(shift)
				shiftDown.run(system, state, .0);
			new KeyDown(key).run(system, state, .0);
			new KeyUp(key).run(system, state, .0);
			if(shift)
				shiftUp.run(system, state, .0);
			Util.pause(d);
		}
	}
		
	public static void checkAscii(String text){
	    if (!asciiEncoder.canEncode(text))
	    	throw new IllegalArgumentException("This string is not an ascii string!");
	}
	
	private KBKeys getKey(char c){
		for(KBKeys k : KBKeys.values())
			if(k.code() == (int) c)
				return k;
		throw new IllegalArgumentException("Unable to find the corresponding keycode for character '" + c + "(" + ((int)c) +  ")'!");
	}
	
	public String toString(){ return "Type text '" + text + "'"; }
	
	// by urueda
	@Override
	public String toString(Role... discardParameters) {
		for (Role r : discardParameters){
			if (r.name().equals(ActionRoles.Type.name()))
				return "Text typed";
		}
		return toString();
	}	

	// by urueda
	@Override
	public String toShortString() {
		Role r = get(Tags.Role, null);
		if (r != null)
			return r.toString();
		else
			return toString();
	}

	// by urueda
	@Override
	public String toParametersString() {
		return "(" + text + ")";
	}
	
}