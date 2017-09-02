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
package org.fruit.alayer;

import java.util.List;

import org.fruit.Pair;
import org.fruit.alayer.exceptions.SystemStopException;

/**
 * An SUT is a System Under Test and can be a graphical application, a process, a collection of processes or
 * even a collection of distributed services. A system is always in a specific <code>State</code>. <code>Action</code>'s
 * operate on a system and its state.
 * @see Action
 * @see State
 */
public interface SUT extends Taggable {
	
	/**
	 * Stops execution of the system. Implementations should try to first shut down
	 * the system "gently" and gradually get more aggressive if that fails. If it is
	 * not possible to shutdown the system, implementors of this method are supposed to throw a
	 * <code>SystemStopException</code>.
	 * @throws SystemStopException if the system cannot be stopped
	 */
	void stop() throws SystemStopException;
    
	/** Is the system running?
	 * @return returns whether the system is running
	 */
	boolean isRunning();
	
	/**
	 * Retrieves a text representation of SUT status (i.e. CPU/memory usage).
	 * @return The status.
	 * @author urueda
	 */
	String getStatus();
	
	/**
	 * Retrieves the running processes.
	 * @return A list of pairs &lt;PID,NAME&gt; with the PID/NAME of running processes.
	 * @author: urueda
	 */
	List<Pair<Long, String>> getRunningProcesses();
	
	/**
	 * Sets a native automation cache.
	 * @author: urueda
	 */
	void setNativeAutomationCache();
	
	/**
	 * Returns the native automation cache.
	 * @return
	 */
	AutomationCache getNativeAutomationCache();
	
}