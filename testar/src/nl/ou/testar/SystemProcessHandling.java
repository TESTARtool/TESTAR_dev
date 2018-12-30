package nl.ou.testar;

import es.upv.staq.testar.NativeLinker;
import org.fruit.Util;
import org.fruit.alayer.SUT;
import org.fruit.alayer.Tags;
import org.fruit.alayer.devices.ProcessHandle;
import org.fruit.alayer.exceptions.SystemStopException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SystemProcessHandling {

    //TODO native linker is used and that requires platform specific implementation. move to SystemProcessHandling class
    /**
     * Retrieve a list of Running processes
     * @param debugTag Tag used in debug output
     * @return a list of running processes
     */
    public static List<ProcessInfo> getRunningProcesses(String debugTag) {
        List<ProcessInfo> runningProcesses = new ArrayList<ProcessInfo>();
        long pid, handle; String desc;
        List<SUT> runningP = NativeLinker.getNativeProcesses();
        System.out.println("[" + debugTag + "] " + "Running processes (" + runningP.size() + "):");
        int i = 1;
        for (SUT sut: runningP) {
            //System.out.println("\t[" + (i++) +  "] " + sut.getStatus());
            pid = sut.get(Tags.PID, Long.MIN_VALUE);
            if (pid != Long.MIN_VALUE) {
                handle = sut.get(Tags.HANDLE, Long.MIN_VALUE);
                desc = sut.get(Tags.Desc, null);
                ProcessInfo pi = new ProcessInfo(sut,pid,handle,desc);
                runningProcesses.add(pi);
            }
        }
        return runningProcesses;
    }

    final static long MAX_KILL_WINDOW = 10000; // 10 seconds

    //TODO native linker is used and that requires platform specific implementation. move to SystemProcessHandling class
    public static void killTestLaunchedProcesses(List<ProcessInfo> contextRunningProcesses) {
        boolean kill;
        for (ProcessInfo pi1: getRunningProcesses("END")) {
            kill = true;
            for (ProcessInfo pi2: contextRunningProcesses) {
                if (pi1.pid == pi2.pid) {
                    kill = false;
                    break;
                }
            }
            if (kill)
                killProcess(pi1,MAX_KILL_WINDOW);
        }
    }

    //TODO native linker is used and that requires platform specific implementation. move to SystemProcessHandling class
    /**
     * Kills the SUT process. Also true if the process is not running anymore (killing might not happen)
     * @param sut
     * @param KILL_WINDOW
     * @return
     */
    public static boolean killRunningProcesses(SUT sut, long KILL_WINDOW) {
        boolean allKilled = true;
        for (ProcessHandle ph: Util.makeIterable(sut.get(Tags.ProcessHandles, Collections.<ProcessHandle>emptyList().iterator()))) {
            if (ph.name() != null && sut.get(Tags.Desc, "").contains(ph.name())) {
                try {
                    System.out.println("\tWill kill <" + ph.name() +"> with PID <" + ph.pid() + ">");
                    ph.kill();
                } catch (SystemStopException e) {
                    System.out.println("Exception killing SUT running processes: " + e.getMessage());
                    allKilled = false;
                }
            }
        }
        return allKilled;
    }

    //TODO native linker is used and that requires platform specific implementation. move to SystemProcessHandling class
    /**
     * Kill process with info pi
     * @param pi
     * @param KILL_WINDOW indicates a time frame
     * @return
     */
    private static boolean killProcess(ProcessInfo pi, long KILL_WINDOW) {
        if (pi.sut.isRunning()) {
            System.out.println("Will kill process: " + pi.toString());
            long now = System.currentTimeMillis(),
                    elapsed;
            do {
                elapsed = System.currentTimeMillis() - now;
                try {
                    NativeLinker.getNativeProcessHandle(pi.pid).kill();
                } catch (Exception e) {
                    System.out.println("\tException trying to kill process: <" + e.getMessage() + "> after <" + elapsed + "> ms");
                    Util.pauseMs(500);
                }
            } while (pi.sut.isRunning() && elapsed < KILL_WINDOW);
            return pi.sut.isRunning();
        } else {
            System.out.println("Did not kill process as it is not running: " + pi.toString());
            return true;
        }
    }
}
