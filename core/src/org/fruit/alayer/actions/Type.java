/***************************************************************************************************
*
* Copyright (c) 2013, 2014, 2015, 2016, 2017 Universitat Politecnica de Valencia - www.upv.es
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

			try {

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

			} catch(IllegalArgumentException e) {
				System.out.println("TESTAR support for better character encodings is being developed");
				System.out.println(e.getMessage());
			}
		}
	}
		
	public static void checkAscii(String text){
	    if (!asciiEncoder.canEncode(text))
	    	throw new IllegalArgumentException("This string is not an ascii string!");
	}
	
	private KBKeys getKey(char c) {
        for (KBKeys key : KBKeys.values()) {
            if (key.code() == (int) c) {
                return key;
            }
        }

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
