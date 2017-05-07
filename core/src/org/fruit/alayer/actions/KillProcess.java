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

import java.util.Collections;

import org.fruit.Assert;
import org.fruit.Util;
import org.fruit.alayer.Action;
import org.fruit.alayer.Role;
import org.fruit.alayer.SUT;
import org.fruit.alayer.State;
import org.fruit.alayer.TaggableBase;
import org.fruit.alayer.Tags;
import org.fruit.alayer.devices.ProcessHandle;
import org.fruit.alayer.exceptions.ActionFailedException;

/**
 * An action that kills a process by PID or by Name.
 */
public class KillProcess extends TaggableBase implements Action {
	private static final long serialVersionUID = -1777427445519403935L;
	final String name;
	final Long pid;
	final double waitTime;

	public static KillProcess byName(String name, double timeToWaitForProcessToAppear){ return new KillProcess(name, null, timeToWaitForProcessToAppear); }
	public static KillProcess byPID(long pid, double timeToWaitForProcessToAppear){ return new KillProcess(null, pid, timeToWaitForProcessToAppear); }

	private KillProcess(String name, Long pid, double waitTime){
		Assert.isTrue(!(name == null && pid == null) && waitTime >= 0);
		this.name = name;
		this.pid = pid;
		this.waitTime = waitTime;
	}

	public void run(SUT system, State state, double duration) throws ActionFailedException{
		Assert.notNull(system);
		Assert.isTrue(duration >= 0);
		
		double start = Util.time();
		Util.pause(waitTime);
		
		for(ProcessHandle ph : Util.makeIterable(system.get(Tags.ProcessHandles, Collections.<ProcessHandle>emptyList().iterator()))){
			if((pid != null && ph.pid() == pid) || (name != null && name.equals(ph.name()))){
				ph.kill();
				return;
			}
		}
		
		Util.pause(duration - (Util.time() - start));
	}
	
	public String toString(){ return "KillProcess"; }
	
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
		return "";
	}
	
}