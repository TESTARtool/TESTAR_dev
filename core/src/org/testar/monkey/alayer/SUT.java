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
package org.testar.monkey.alayer;

import java.util.List;

import org.testar.monkey.Pair;
import org.testar.monkey.alayer.exceptions.SystemStopException;

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
