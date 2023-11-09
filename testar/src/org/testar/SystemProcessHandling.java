/***************************************************************************************************
 *
 * Copyright (c) 2018 - 2022 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018 - 2022 Open Universiteit - www.ou.nl
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

package org.testar;

import org.testar.monkey.Pair;
import org.testar.monkey.Util;
import org.testar.monkey.alayer.SUT;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.devices.ProcessHandle;
import org.testar.monkey.alayer.exceptions.SystemStopException;
import org.testar.plugin.NativeLinker;

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
    public static List<ProcessInfo> getRunningProcesses(String debugTag){
        List<ProcessInfo> runningProcesses = new ArrayList<ProcessInfo>();
        long pid, handle; String desc;
        List<SUT> runningP = NativeLinker.getNativeProcesses();
        System.out.println("[" + debugTag + "] " + "Running processes (" + runningP.size() + "):");

        for (SUT sut : runningP){
            //System.out.println("\t[" + (i++) +  "] " + sut.getStatus());
            pid = sut.get(Tags.PID, Long.MIN_VALUE);
            if (pid != Long.MIN_VALUE){
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
    public static void killTestLaunchedProcesses(List<ProcessInfo> contextRunningProcesses){
        boolean kill;
        for (ProcessInfo pi1 : getRunningProcesses("END")){
            kill = true;
            for (ProcessInfo pi2 : contextRunningProcesses){
                if (pi1.pid == pi2.pid){
                    kill = false;
                    break;
                }
            }
            if (kill)
                killProcess(pi1,MAX_KILL_WINDOW);
        }
    }

    public static List<Pair<Long, String>> getNewLaunchedProcesses(List<ProcessInfo> contextRunningProcesses) {
    	List<Pair<Long, String>> nonContextProcesses = new ArrayList<>();

    	for (ProcessInfo pi1 : getAllRunningProcesses()) {
    		boolean isInContext = false;
    		for (ProcessInfo pi2 : contextRunningProcesses) {
    			if (pi1.pid == pi2.pid) {
    				isInContext = true;
    				break;
    			}
    		}
    		if (!isInContext) {
    			nonContextProcesses.add(new Pair<>(pi1.pid, pi1.Desc));
    		}
    	}

    	return nonContextProcesses;
    }

    private static List<ProcessInfo> getAllRunningProcesses(){
    	List<ProcessInfo> runningProcesses = new ArrayList<ProcessInfo>();
    	long pid, handle; String desc;
    	List<SUT> runningP = NativeLinker.getNativeProcesses();

    	for (SUT sut : runningP){
    		pid = sut.get(Tags.PID, Long.MIN_VALUE);
    		if (pid != Long.MIN_VALUE){
    			handle = sut.get(Tags.HANDLE, Long.MIN_VALUE);
    			desc = sut.get(Tags.Desc, null);
    			ProcessInfo pi = new ProcessInfo(sut,pid,handle,desc);
    			runningProcesses.add(pi);
    		}
    	}
    	return runningProcesses;
    }

    public static boolean moreThanOneProcessRunning(String processName) {
    	List<SUT> runningP = NativeLinker.getNativeProcesses();

    	long explorerCount = runningP.stream()
    			.map(sut -> new ProcessInfo(
    					sut,
    					sut.get(Tags.PID, Long.MIN_VALUE),
    					sut.get(Tags.HANDLE, Long.MIN_VALUE),
    					sut.get(Tags.Desc, null)))
    			.filter(pi -> pi.Desc != null && pi.Desc.equalsIgnoreCase(processName))
    			.count();

    	return explorerCount > 1;
    }

    //TODO native linker is used and that requires platform specific implementation. move to SystemProcessHandling class
    /**
     * Kills the SUT process. Also true if the process is not running anymore (killing might not happen)
     * @param sut
     * @param KILL_WINDOW
     * @return
     */
    public static boolean killRunningProcesses(SUT sut, long KILL_WINDOW){
        boolean allKilled = true;
        for(ProcessHandle ph : Util.makeIterable(sut.get(Tags.ProcessHandles, Collections.<ProcessHandle>emptyList().iterator()))){
            if (ph.name() != null && sut.get(Tags.Desc, "").contains(ph.name())){
                try{
                    System.out.println("\tWill kill <" + ph.name() +"> with PID <" + ph.pid() + ">");
                    ph.kill();
                } catch (SystemStopException e){
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
    private static boolean killProcess(ProcessInfo pi, long KILL_WINDOW){
        if (pi.sut.isRunning()){
            System.out.println("Will kill process: " + pi.toString());
            long now = System.currentTimeMillis(),
                    elapsed;
            do{
                elapsed = System.currentTimeMillis() - now;
                try {
                    NativeLinker.getNativeProcessHandle(pi.pid).kill();
                } catch (SystemStopException e) {
                    System.out.println("\tException trying to kill process: <" + e.getMessage() + "> after <" + elapsed + "> ms");
                    Util.pauseMs(500);
                }
            } while (pi.sut.isRunning() && elapsed < KILL_WINDOW);
            return pi.sut.isRunning();
        } else{
            System.out.println("Did not kill process as it is not running: " + pi.toString());
            return true;
        }
    }
}
