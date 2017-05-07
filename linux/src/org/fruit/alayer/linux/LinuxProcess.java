package org.fruit.alayer.linux;

import org.fruit.Assert;
import org.fruit.FruitException;
import org.fruit.Util;
import org.fruit.alayer.SUT;
import org.fruit.alayer.SUTBase;
import org.fruit.alayer.Tag;
import org.fruit.alayer.Tags;
import org.fruit.alayer.devices.AWTKeyboard;
import org.fruit.alayer.devices.AWTMouse;
import org.fruit.alayer.devices.Keyboard;
import org.fruit.alayer.devices.Mouse;
import org.fruit.alayer.exceptions.SystemStartException;
import org.fruit.alayer.exceptions.SystemStopException;
import org.fruit.alayer.linux.atspi.AtSpiAccessible;
import org.fruit.alayer.linux.atspi.TreeWalker;
import org.fruit.alayer.linux.util.JavaHelper;
import org.fruit.alayer.linux.util.xdotools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


/**
 * Represents a Linux process.
 */
public class LinuxProcess extends SUTBase {


    //region Global variables


    private static final String Command_AllProcesses = "ps -axo pid,pcpu,size,comm";
    private static final String Command_KillProcess = "kill %1$d";
    private static final String Command_FindProcess = "ps -p %1$d -o pid,pcpu,size,comm";
    private static final String Command_FindProcessName = "ps -p %1$d -o pid,cmd";


    private static final String EmptyString = "";


    private static final int PidIndex = 0;
    private static final int CpuIndex = 1;
    private static final int SizeIndex = 2;
    private static final int ProcessNameIndex = 1;

    private static final int ProcessInfoNameLength = 2;
    private static final int ProcessInfoLength = 4;


    private Process _process;


    private final Keyboard _kbd = AWTKeyboard.build();
    private final Mouse _mouse = AWTMouse.build();


    //endregion


    //region Properties


    private long _pid;


    /**
     * Gets the PID of the Linux process.
     * @return The PID of the Linux process.
     */
    public long get_pid() {
        return _pid;
    }


    //endregion


    //region Constructors


    /**
     * Private constructor.
     * @param p the encapsulating Linux process.
     */
    private LinuxProcess(Process p) {
        _process = p;
        retrievePid(p);
    }


    /**
     * Creates a Linux process from a PID.
     * @param pid The PID of the Linux process.
     */
    private LinuxProcess(long pid) {
        _pid = pid;
    }


    //endregion


    //region Other needed functionality


    /**
     * Retrieves a LinuxProcess instance from a path.
     * @param path path to a Linux application.
     * @return a LinuxProcess instance.
     * @throws SystemStartException If the application cannot be launched.
     */
    public static LinuxProcess fromExecutable(String path) throws SystemStartException {

        try{

            Assert.notNull(path);

            try {

                Process p = Runtime.getRuntime().exec(path);
                LinuxProcess lp = new LinuxProcess(p);

                // Set the description to the path.
                lp.set(Tags.Desc, path);

                return lp;

            } catch (IOException e) {
                throw new SystemStartException(new FruitException(e.getMessage()));
            }

        }catch(FruitException fe){
            throw new SystemStartException(fe);
        }

    }


    /**
     * Retrieves a list of available running SUT processes on this machine.
     * @return A list of available SUTs currently running on this machine.
     */
    public static List<SUT> fromAll() {


        List<SUT> suts = new ArrayList<>();


        // Get a list of all running processes on this Unix machine by using a terminal command and parsing the output.
        List<String[]> processInfos =  runProcessCommand(Command_AllProcesses);


        if (processInfos == null) {
            return null;
        }


        for(String[] pi : processInfos) {

            LinuxProcess lp = parseProcess(pi);

            if (lp != null){
                suts.add(lp);
            }

        }


        // The calling code expects null when the list is empty.
        if (suts.isEmpty()) {
            return null;
        }


        return suts;


    }


    /**
     * Determines whether the Linux process is active/ in the foreground.
     * @return True if the process's window is currently active/ in the foreground; False otherwise.
     */
    public boolean isActive() {
        return isActive(this);
    }


    /**
     * Determines whether the Linux process is active/ in the foreground.
     * @param pid The PID of the Linux process which to check the active state for.
     * @return True if the process's window is currently active/ in the foreground; False otherwise.
     */
    public static boolean isActive(long pid) {
       return isActive(fromPid(pid));
    }


    /**
     * Determines whether the Linux process is active/ in the foreground.
     * @param lp The Linux process which to check the active state for.
     * @return True if the process's window is currently active/ in the foreground; False otherwise.
     */
    public static boolean isActive(LinuxProcess lp) {

        if (lp == null) {
            return false;
        } else if (!lp.isRunning()) {
            return false;
        }

        return xdotools.getPIDFromActiveWindow() == lp.get_pid();

    }


    /**
     * Tries to activate/ bring to the foreground the window associated with this Linux process.
     * @return True if the process's window got activated/ moved to the foreground successfully; False otherwise.
     */
    public boolean activate() {
        return activate(this);
    }


    /**
     * Tries to activate/ bring to the foreground the window associated with the Linux process that hold the specified PID.
     * @param pid The PID of the Linux process of which the window needs to be activated/ brought to the foreground.
     * @return True if the process's window got activated/ moved to the foreground successfully; False otherwise.
     */
    public static boolean activate(long pid) {
        return activate(fromPid(pid));
    }


    /**
     * Tries to activate/ bring to the foreground the window associated with the supplied Linux process.
     * @param lp The Linux process of which the window needs to be activated/ brought to the foreground.
     * @return True if the process's window got activated/ moved to the foreground successfully; False otherwise.
     */
    public static boolean activate(LinuxProcess lp) {


        if (lp == null) {
            return false;
        } else if (!lp.isRunning()) {
            return false;
        }


        // Assumptions:
        // - There are window manager on unix systems that don't support Alt+Tab to switch application windows.
        // - Active window is the same as foreground window on Unix systems.
        // - The name of the application is in the LinuxProcess's description which should contain the filepath.


        // No need to activate if it's already the active window.
        if (lp.isActive()) {
            return true;
        }


        // To activate the application through AT-SPI we need to know the application's name.
        String applicationName = lp.get(Tags.Desc).substring(lp.get(Tags.Desc).lastIndexOf("/") + 1);


        // There may be multiple instances of the same application running - therefore a list is returned with
        // application nodes with the same name.
        List<AtSpiAccessible> applicationNodes = TreeWalker.getApplicationNodes(applicationName);


        if (applicationNodes.size() == 0) {
            System.out.println("Could not find any applications with the name '" + applicationName + "'.");
            return false;
        }


        // Activate each application through AT-SPI and find the PID for each active application through xdotool.
        // Once verified that the node activated the instance of the application launched by us - stop.
        for (AtSpiAccessible application : applicationNodes) {


            // Activate application.
            if (TreeWalker.activateApplication(application)) {

                // Short pause to give the application time to activate.
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


                // Retrieve PID through xdotool.
                int pid = xdotools.getPIDFromActiveWindow();


                // Check if the PID matches the PID of the process we're supposed to activate.
                if (pid == lp.get_pid()) {
                    return true;
                }


            } else {
                System.out.println("Cannot activate an application with the same name - continuing loop...");
            }


        }


        // Could not find or activate the application we launched.
        System.out.println("Could not find or activate the application!");
        return false;


    }


    /**
     * Retrieves the memory usage for a given Linux process.
     * @param lp The Linux process to retrieve the memory usage for.
     * @return The memory the Linux process uses; 0 otherwise.
     */
    public static long getMemUsage(LinuxProcess lp){


        if (!lp.isRunning()){
            System.out.println("SUT is not running - cannot retrieve RAM usage!");
            return 0;
        }


        List<String[]> processInfos =  runProcessCommand(String.format(Command_FindProcess, lp.get_pid()));


        if (processInfos == null || processInfos.isEmpty()) {
            System.out.println("Running command to find process info failed - cannot retrieve RAM usage!");
            return 0;
        }


        // Lets assume that it could happen that multiple lines are returned - parse and find the requested PID.
        for(String[] pi : processInfos) {

            LinuxProcess lpe = parseProcess(pi);

            if (lpe != null && lpe.get_pid() == lp.get_pid()){

                // Parse the current processInfo and extract the memory usage.
                if (JavaHelper.tryParseInt(pi[SizeIndex])) {
                    return Integer.parseInt(pi[SizeIndex]);
                } else {
                    System.out.println("Could not parse the process info - cannot retrieve RAM usage!");
                    return 0;
                }


            }

        }

        System.out.println("Could not find the process info - cannot retrieve RAM usage!");
        return 0;


    }


    /**
     * Retrieves the cpu usage for a given Linux process.
     * @param lp The Linux process to retrieve the cpu usage for.
     * @return The cpu the Linux process uses; 0 otherwise.
     */
    public static double getCpuUsage(LinuxProcess lp){


        if (!lp.isRunning()){
            System.out.println("SUT is not running - cannot retrieve CPU usage!");
            return 0;
        }


        List<String[]> processInfos =  runProcessCommand(String.format(Command_FindProcess, lp.get_pid()));


        if (processInfos == null || processInfos.isEmpty()) {
            System.out.println("Running command to find process info failed - cannot retrieve CPU usage!");
            return 0;
        }


        // Lets assume that it could happen that multiple lines are returned - parse and find the requested PID.
        for(String[] pi : processInfos) {

            LinuxProcess lpe = parseProcess(pi);

            if (lpe != null && lpe.get_pid() == lp.get_pid()){

                // Parse the current processInfo and extract the cpu usage.
                if (JavaHelper.tryParseDouble(pi[CpuIndex])) {
                    return Double.parseDouble(pi[CpuIndex]);
                } else {
                    System.out.println("Could not parse the process info - cannot retrieve CPU usage!");
                    return 0;
                }


            }

        }

        System.out.println("Could not find the process info - cannot retrieve CPU usage!");
        return 0;


    }


    /**
     * Creates a Linux process from a PID.
     * @param pid The PID of a Linux process.
     * @return LinuxProcess instance representing the Linux process with the given PID.
     */
    public static LinuxProcess fromPid(long pid) {

        // Create a LinuxProcess instance from PID.
        LinuxProcess lp = new LinuxProcess(pid);

        // Set a different description tag on the instance - process name + PID.
        lp.set(Tags.Desc, getProcessName(pid) + " (PID: " + pid + ")");

        return lp;

    }


    /**
     * Gets a name for the Linux process.
     * @return The name of the Linux process.
     */
    public String getProcessName() {
        return getProcessName(_pid);
    }


    /**
     * Most likely called when garbage collected.
     */
    public void finalize() {
        // Apparently finalize is called even though the object is still referenced.
        //stop();
    }


    //endregion


    //region Helper functions


    /**
     * Retrieve the PID on unix/linux systems
     * @param p the process to retrive the PID from.
     */
    private void retrievePid(Process p) {


        if(p.getClass().getName().equals("java.lang.UNIXProcess")) {

            try {
                Field f = p.getClass().getDeclaredField("pid");
                f.setAccessible(true);
                _pid = f.getInt(p);
            } catch (Throwable e) {
                throw new FruitException(e.getMessage());
            }

        }


    }


    /**
     * Parses a process info String array into a Linux Process.
     * @param processInfo The info of a Linux process as a String array.
     * @return A Linux process instance.
     */
    private static LinuxProcess parseProcess(String[] processInfo) {


        if (processInfo.length != ProcessInfoLength) {
            // Missing process info - can't be certain to parse correctly.
            return null;
        }


        if (!JavaHelper.tryParseInt(processInfo[PidIndex])) {
            // Can't parse the first entry as the PID.
            return null;
        }


        int pid = Integer.parseInt(processInfo[PidIndex]);


        if (pid <= 0) {
            //Invalid PID.
            return null;
        }
        return new LinuxProcess(pid);


    }


    /**
     * Runs a (process) command and returns the received process infos as a String array.
     * @param command the command to run.
     * @return List of String arrays containing info on the processes.
     */
    private static List<String[]> runProcessCommand(String command) {


        ArrayList<String[]> processInfos = new ArrayList<>();

        try {


            // Run the command.
            Process p = Runtime.getRuntime().exec(command);


            // Get the stream to read the command output from.
            BufferedReader commandOutputReader = new BufferedReader(new InputStreamReader(p.getInputStream()));


            // Read the output as lines - omit the first (header) line.
            String processLine = commandOutputReader.readLine();

            if (processLine != null) {
                while ((processLine = commandOutputReader.readLine()) != null) {


                    // Remove duplicate whitespace and remove leading and trailing whitespace.
                    processLine = processLine.replaceAll("\\s+", " ");
                    processLine = processLine.trim();

                    // Split the processes in their info parts.
                    String[] processInfo = processLine.split(" ");

                    processInfos.add(processInfo);


                }
            }
            return processInfos;


        } catch (IOException e) {
            // Can ignore the error - return null.
            return null;
        }

    }


    /**
     * Creates a list of running Linux processes in a different kind of representation.
     * @return A list of Linux process representations of type LinuxProcessHandle.
     */
    private static List<LinuxProcessHandle> runningProcesses(){


        // Retrieve a list of LinuxProcesses.
        List<SUT> linuxProcesses = fromAll();
        List<LinuxProcessHandle> linuxProcessHandles = new ArrayList<>();


        if (linuxProcesses == null) {
            return linuxProcessHandles;
        }


        // Convert the list to a list of LinuxProcessHandles.
        for (SUT lp : linuxProcesses) {
            linuxProcessHandles.add(new LinuxProcessHandle((LinuxProcess)lp));
        }


        return linuxProcessHandles;


    }


    /**
     * Gets a name for the Linux process.
     * @param pid The PID of the Linux process to get the name from.
     * @return The name of the Linux process.
     */
    private static String getProcessName(long pid) {


        ArrayList<String[]> processInfos = new ArrayList<>();


        try {


            // Run the command
            Process p = Runtime.getRuntime().exec(String.format(Command_FindProcessName, pid));


            // Get the stream to read the command output from.
            BufferedReader commandOutputReader = new BufferedReader(new InputStreamReader(p.getInputStream()));


            // Read the output as lines - omit the first (header) line.
            String processLine = commandOutputReader.readLine();


            if (processLine != null) {
                while ((processLine = commandOutputReader.readLine()) != null) {

                    // Remove leading and trailing whitespace.
                    processLine = processLine.trim();

                    // Split the processes in their info parts - should be only .
                    String[] processInfo = processLine.split(" ", ProcessInfoNameLength);

                    processInfos.add(processInfo);


                }
            }


            // Parse the process infos - find the pid and get the name.
            for (String[] pi : processInfos) {


                // Ignore all items that don't have the right length.
                if (pi.length != ProcessInfoNameLength) {
                    continue;
                }

                // Parse the PID and see if it is the same.
                if (!JavaHelper.tryParseInt(pi[PidIndex])) {
                    continue;
                }


                int pidInternal = Integer.parseInt(pi[PidIndex]);


                if (pidInternal == pid) {
                    // The remainder should be the process name.
                    return pi[ProcessNameIndex];
                }

            }


        } catch (IOException e) {
            // Can ignore the error - return empty string.
            return EmptyString;
        }
        return EmptyString;

    }


    //endregion


    //region Object overrides


    @Override
    public String toString() {
        return getStatus();
    }


    //endregion


    //region SUT implementation


    /**
     * Stops the process.
     * @throws SystemStopException could not stop the process.
     */
    @Override
    public void stop() throws SystemStopException {

        if (_process != null) {

            // Kill process with the Process object we created.
            _process.destroy();
            _process = null;

        } else if (_pid != 0) {

            // Kill process given its PID only.
            try {
                Runtime.getRuntime().exec(String.format(Command_KillProcess, _pid));
                _pid = 0;
            } catch (IOException e) {
                System.out.println("Could not run the kill command for the process with PID: " + _pid);
            }

        }

    }


    /**
     * Checks whether the process is still running.
     * @return True if the process is running; False otherwise.
     */
    @Override
    public boolean isRunning() {

        if (_process != null ) {
            return _process.isAlive();
        } else if (_pid != 0) {

            // Check if process is running given its PID only.
            // Get a list of running processes with the given PID.
            List<String[]> processInfos =  runProcessCommand(String.format(Command_FindProcess, _pid));


            if (processInfos == null) {
                return false;
            }


            // Lets assume that it could happen that multiple lines are returned - parse and find the requested PID.
            for(String[] pi : processInfos) {

                LinuxProcess lp = parseProcess(pi);

                if (lp != null && lp.get_pid() == _pid){
                    return true;
                }

            }

        }

        return false;

    }


    /**
     * The status of the process.
     * @return string representation of the process.
     */
    @Override
    public String getStatus() {

        if (_process != null) {
            return "PID: " + _pid + " - CPU: " + getCpuUsage(this) + "% - Memory: " + (getMemUsage(this) / 1024) + " KB" + " - Process: " + _process.toString();
        } else {
            return "PID: " + _pid + " - CPU: " + getCpuUsage(this) + "% - Memory: " + (getMemUsage(this) / 1024) + " KB";
        }

    }


    //endregion


    //region Tag overrides


    /**
     * Most likely implements the retrieval of the Tags specified in the method tagDomain.
     * @param tag The tag to retrieve the value for.
     * @param <T> Type of the value to retrieve.
     * @return The value of the tag to retrieve.
     */
    @SuppressWarnings("unchecked")
    protected <T> T fetch(Tag<T> tag){
        if(tag.equals(Tags.StandardKeyboard))
            return (T)_kbd;
        else if(tag.equals(Tags.StandardMouse))
            return (T)_mouse;
        else if(tag.equals(Tags.PID))
            return (T)(Long)_pid;
        else if(tag.equals(Tags.ProcessHandles))
            return (T)runningProcesses().iterator();
        else if(tag.equals(Tags.SystemActivator))
            return (T) new LinuxProcessActivator(_pid);
        return null;
    }


    /**
     * Most likely adds new tags to instances of this class.
     * @return A new set of tags to be added to instances of this class.
     */
    @SuppressWarnings("Duplicates")
    protected Set<Tag<?>> tagDomain(){
        Set<Tag<?>> ret = Util.newHashSet();
        ret.add(Tags.StandardKeyboard);
        ret.add(Tags.StandardMouse);
        ret.add(Tags.ProcessHandles);
        ret.add(Tags.PID);
        ret.add(Tags.SystemActivator);
        return ret;
    }


    //endregion


}