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
package org.testar.monkey.alayer.actions;

import org.testar.monkey.Assert;
import org.testar.monkey.Util;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.Role;
import org.testar.monkey.alayer.SUT;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.TaggableBase;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.devices.MouseButtons;
import org.testar.monkey.alayer.exceptions.ActionFailedException;
import org.testar.monkey.alayer.exceptions.NoSuchTagException;

/**
 * An action which releases a given Button on the StandardMouse of an SUT.
 */
public final class MouseUp extends TaggableBase implements Action {
	private static final long serialVersionUID = -3335028037420381947L;
	private final MouseButtons btn;
	
	public MouseUp(MouseButtons btn){
		Assert.notNull(btn);
		this.btn = btn;
	}
	
	public String toString() { return "Release Mouse Button " + btn; }

	// by urueda
	@Override
	public String toString(Role... discardParameters) {
		for (Role r : discardParameters){
			if (r.name().equals(ActionRoles.MouseUp.name()))
				return "Mouse button released";
		}
		return toString();
	}	

	public void run(SUT system, State state, double duration) {
		try{
			Assert.notNull(system);
			Util.pause(duration);
			system.get(Tags.StandardMouse).release(btn);
		}catch(NoSuchTagException tue){
			throw new ActionFailedException(tue);
		}
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
		//return "(" + btn.toString() + ")";
		return "";
	}
	
}
