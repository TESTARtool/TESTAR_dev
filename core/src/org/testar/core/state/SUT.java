/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2013-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */

package org.testar.core.state;

import java.util.List;

import org.testar.core.Pair;
import org.testar.core.alayer.AutomationCache;
import org.testar.core.exceptions.SystemStopException;
import org.testar.core.tag.Taggable;

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
