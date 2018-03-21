package org.fruit.alayer.linux;


import org.fruit.Proc;
import org.fruit.alayer.exceptions.ActionFailedException;


/**
 * Class to activate a Linux process.
 */
public class LinuxProcessActivator implements Proc {


    //region Global variables


    private final long _pid;


    //endregion


    //region Constructors


    /**
     * Creates a new activator action for a Linux process.
     * @param pid The PID of the Linux process to activate.
     */
    LinuxProcessActivator(long pid) {
        _pid = pid;
    }


    //endregion


    //region Proc Implementation


    /**
     * Runs the action - activates the Linux process by PID.
     */
    @Override
    public void run() {

        if (!LinuxProcess.activate(_pid)) {
            throw new ActionFailedException("Could not activate the application with PID: " + _pid);
        }

    }


    //endregion


}