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

import org.fruit.Assert;
import org.fruit.Util;
import org.fruit.alayer.Action;
import org.fruit.alayer.Role;
import org.fruit.alayer.SUT;
import org.fruit.alayer.State;
import org.fruit.alayer.TaggableBase;
import org.fruit.alayer.Tags;

/**
 * An action that simply waits a given amount of seconds.
 */
public final class Wait extends TaggableBase implements Action {

	private static final long serialVersionUID = 8248189921206790701L;
	private final double waitTime;
	private final boolean oveheadDuration;
	
	public Wait(double waitTime){ this(waitTime, false); }
	
	public Wait(double waitTime, boolean overheadDuration){
		Assert.isTrue(waitTime >= 0);
		this.oveheadDuration = overheadDuration;
		this.waitTime = waitTime;
	}
	
	public void run(SUT system, State state, double duration) {
		Assert.isTrue(duration >= 0);
		Util.pause(waitTime);
		if(!oveheadDuration)
			Util.pause(Math.max(0, waitTime - duration));  // sleep the rest of the time
	}
	
	public String toString(){
		return "Wait for " + (oveheadDuration ? "exactly " : "") + waitTime + " seconds";
	}	
	
	// by urueda
	@Override
	public String toString(Role... discardParameters) {
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
		return "(" + waitTime + ")";
	}
	
}