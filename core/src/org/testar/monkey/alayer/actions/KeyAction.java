/***************************************************************************************************
*
* Copyright (c) 2016 - 2023 Universitat Politecnica de Valencia - www.upv.es
* Copyright (c) 2018 - 2023 Open Universiteit - www.ou.nl
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
*
* 1. Redistributions of source code must retain the above copyright notice,
* this list of conditions and the following disclaimer.
* 2. Redistributions in binary form must reproduce the above copyright
* notice, this list of conditions and the following disclaimer in the
* documentation and/or other materials provided with the distribution.
* 3. Neither the name of the copyright holder nor the names of its
* contributors may be used to endorse or promote products derived from
* this software without specific prior written permission.
*
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
* AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
* IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
* ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
* LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
* CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
* SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
* INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
* CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
* ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
* POSSIBILITY OF SUCH DAMAGE.
*******************************************************************************************************/

package org.testar.monkey.alayer.actions;

import org.testar.monkey.Assert;
import org.testar.monkey.Util;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.Role;
import org.testar.monkey.alayer.SUT;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.TaggableBase;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.devices.KBKeys;
import org.testar.monkey.alayer.devices.Keyboard;
import org.testar.monkey.alayer.exceptions.ActionFailedException;
import org.testar.monkey.alayer.exceptions.NoSuchTagException;

/**
 * An action which presses a given Key on the Keyboard.
 */
public abstract class KeyAction extends TaggableBase implements Action {
	private static final long serialVersionUID = 4379174151668501105L;
	protected final KBKeys key;

	public KeyAction(KBKeys key){
		Assert.notNull(key);
		this.key = key;
	}
	
	public abstract String toString();

	public final void run(SUT system, State state, double duration) {
		try{
			Assert.notNull(system);
			Util.pause(duration);
			if (key.equals(KBKeys.VK_ARROBA) ||
				key.equals(KBKeys.VK_EXCLAMATION_MARK) ||
				key.equals(KBKeys.VK_UNDERSCORE)) // java.awt.Robot throwing "Invalid key code"
				altNumpad(system, Integer.toString(key.code()));
			else
				performKeyAction(system,key);
		}catch(NoSuchTagException tue){
			throw new ActionFailedException(tue);
		}
	}
	
	protected abstract void performKeyAction(SUT system, KBKeys key);
	
	protected void altNumpad(SUT system, String numpadCodes){
		if (numpadCodes == null || !numpadCodes.matches("^\\d+$")){
			System.out.println("Unknown key: " + numpadCodes);
			return;
		}               
		Keyboard keyb = system.get(Tags.StandardKeyboard);
		keyb.press(KBKeys.VK_ALT);
		for (char charater : numpadCodes.toCharArray()){
			KBKeys NUMPAD_KEY = getNumpad(charater);
			if (NUMPAD_KEY != null){
				keyb.press(NUMPAD_KEY);
				keyb.release(NUMPAD_KEY);
			}
		}
		keyb.release(KBKeys.VK_ALT);        
	}	

	private KBKeys getNumpad(char numberChar){
		switch (numberChar){
		  case '0' : return KBKeys.VK_NUMPAD0;
		  case '1' : return KBKeys.VK_NUMPAD1;
		  case '2' : return KBKeys.VK_NUMPAD2;
		  case '3' : return KBKeys.VK_NUMPAD3;
		  case '4' : return KBKeys.VK_NUMPAD4;
		  case '5' : return KBKeys.VK_NUMPAD5;
		  case '6' : return KBKeys.VK_NUMPAD6;
		  case '7' : return KBKeys.VK_NUMPAD7;
		  case '8' : return KBKeys.VK_NUMPAD8;
		  case '9' : return KBKeys.VK_NUMPAD9;
  		default  : System.out.println("AltNumpad - not a number 0-9: " + numberChar);
		return null;
		}
	}
	
	@Override
	public String toShortString(){
		Role r = get(Tags.Role, null);
		if (r != null)
			return r.toString();
		else
			return toString();
	}
    
	@Override
	public String toParametersString(){
		return "(" + key.toString() + ")";
	}
	
	public abstract boolean equals(Object o);
	
}
