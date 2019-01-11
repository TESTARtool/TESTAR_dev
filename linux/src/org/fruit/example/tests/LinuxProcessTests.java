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

package org.fruit.example.tests;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.fruit.alayer.SUT;
import org.fruit.alayer.linux.LinuxProcess;

import java.util.List;

/**
 * Unit tests for working with Linux processes.
 */
public class LinuxProcessTests {

    private static final double CPU_SIZE = 5.0;
    private static final String ApplicationPath_GEdit = "/usr/bin/gedit";
    private static final String ApplicationPath_Galculator = "/usr/bin/galculator";

    @Test
    public void GetRunningProcesses() {

        // Get a list of processes.
        List<SUT> processes = LinuxProcess.fromAll();

        // Make sure it succeeded.
        assertNotNull(processes, "Could not retrieve any processes!");

    }

    @Test
    public void LaunchProcess() {

        // Launch an application.
        LinuxProcess lp = LinuxProcess.fromExecutable(ApplicationPath_GEdit);

        // Make sure it is running.
        assertNotNull(lp,"Failed to launch the requested application.");
        assertEquals(true, lp.isRunning());

        // Create a LinuxProcess instance and check if it also returns running.
        LinuxProcess gedit = LinuxProcess.fromPid(lp.get_pid());
        assertEquals(true, gedit.isRunning());

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Stop the process and check isRunning again for both LinuxProcess instances.
        gedit.stop();

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertEquals(false, lp.isRunning());
        assertEquals(false, gedit.isRunning());

    }

    @Test
    public void ProcessStats() {

        // Launch an application.
        LinuxProcess lp = LinuxProcess.fromExecutable(ApplicationPath_GEdit);

        // Make sure it is running.
        assertNotNull(lp,"Failed to launch the requested application.");
        assertEquals(true, lp.isRunning());

        // Check the usage stats.
        long mem = LinuxProcess.getMemUsage(lp);
        double cpu = LinuxProcess.getCpuUsage(lp);

        // If nothing failed then memory will not be zero but cpu will be zero or very small.
        assertEquals(true, mem > 0);
        assertEquals(true, cpu >= 0.0 && cpu < CPU_SIZE, "CPU: " + cpu);

        // Check the process name.
        String processName = lp.getProcessName();
        assertEquals(ApplicationPath_GEdit, processName);

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Stop the process and check isRunning again for both LinuxProcess instances.
        lp.stop();

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertEquals(false, lp.isRunning());

    }

    @Test
    public void ActivateProcess() {

        LinuxProcess galc = LinuxProcess.fromExecutable(ApplicationPath_Galculator);
        assertNotNull(galc);

        // Short pause to give the applications time to start.
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        boolean isActive = galc.isActive();
        assertTrue(isActive);

        LinuxProcess gedit = LinuxProcess.fromExecutable(ApplicationPath_GEdit);
        assertNotNull(gedit);

        // Short pause to give the applications time to start.
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        isActive = galc.isActive();
        assertFalse(isActive);

        boolean activation = galc.activate();
        assertTrue(activation);

        isActive = galc.isActive();
        assertTrue(isActive);

        galc.stop();
        gedit.stop();

    }

}
