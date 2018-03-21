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
