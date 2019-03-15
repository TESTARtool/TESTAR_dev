/***************************************************************************************************
*
* Copyright (c) 2017 Open Universiteit - www.ou.nl
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

package org.fruit.alayer.linux;

import org.fruit.alayer.devices.ProcessHandle;
import org.fruit.alayer.exceptions.SystemStopException;

/**
 * Another representation of a Linux process.
 */
public class LinuxProcessHandle implements ProcessHandle {

    //region Global variables

    private LinuxProcess _process;

    //endregion

    //region Constructors

    /**
     * Creates a new Linux process representation from another Linux process representation.
     * @param lp A Linux process representation.
     */
    LinuxProcessHandle(LinuxProcess lp) {
        _process = lp;
    }

  /**
     * Creates a new Linux process representation from another Linux process representation.
     * @param pid The PID of the linux process to create a LinuxProcessHandle representation for.
     */
    public LinuxProcessHandle(long pid) {
        _process = LinuxProcess.fromPid(pid);
    }

    //endregion

    //region ProcessHandle implementation

    /**
     * Stops the process.
     * @throws SystemStopException If an error occurs when trying to stop the process.
     */
    @Override
    public void kill() throws SystemStopException {
        _process.stop();
    }

    /**
     * Determines whether or not the process is currently running.
     * @return True if the process is running; False otherwise.
     */
    @Override
    public boolean isRunning() {
        return _process.isRunning();
    }

    @Override
    public String name() {
        return _process.getProcessName();
    }

    /**
     * The unique identifier of the Linux process.
     * @return The unique identifier of the Linux process.
     */
    @Override
    public long pid() {
        return _process.get_pid();
    }

    //endregion

}
