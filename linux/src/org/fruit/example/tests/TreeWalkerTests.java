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

import org.fruit.alayer.linux.LinuxProcess;
import org.fruit.alayer.linux.atspi.AtSpiAccessible;
import org.fruit.alayer.linux.atspi.TreeWalker;
import org.fruit.alayer.linux.atspi.enums.AtSpiRoles;
import org.fruit.alayer.linux.util.xdotools;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Unit Test to test the TreeWalker functionality.
 */
public class TreeWalkerTests {


    private static final String ApplicationPath_GEdit = "/usr/bin/gedit";
    private static final String ApplicationPath_Galculator = "/usr/bin/galculator";


    @Test
    public void ApplicationMethods() {



        // Launch two instances of the calculator.
        LinuxProcess calc1 = LinuxProcess.fromExecutable(ApplicationPath_Galculator);
        LinuxProcess calc2 = LinuxProcess.fromExecutable(ApplicationPath_Galculator);


        // Short pause to give the applications time to start.
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        List<AtSpiAccessible> applicationNodes =  TreeWalker.getApplicationNodes(determineApplicationName(ApplicationPath_Galculator), false);
        assertEquals(true, applicationNodes.size() > 1);


        System.out.println("[" + getClass().getSimpleName() + "] Galculator apps running: " + applicationNodes.size());


        // Give user time to setup windows.
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        // Lets see if we can find calculator number 2.
        // Determine if active application is one of the calculators.
        AtSpiAccessible activeCalculator = TreeWalker.getActiveApplicationNode(applicationNodes);
        int pid;


        if (activeCalculator == null) {
            System.out.println("[" + getClass().getSimpleName() + "] No calculator is the active application.");
        } else {


            // Retrieve name and PID through xdotools.
            pid = xdotools.getPIDFromActiveWindow();
            assertTrue(pid > 0);

            String name = xdotools.getNameFromActiveWindow();


            System.out.println("[" + getClass().getSimpleName() + "] Active application is: '" + name + "' with PID: " + pid);


            // Check if the PID matches calculator 2.
            if (pid == calc2.get_pid()) {
                System.out.println("[" + getClass().getSimpleName() + "] Calculator 2 already active - done!");
                calc1.stop();
                calc2.stop();
                return;
            } else if (pid == calc1.get_pid()) {
                System.out.println("[" + getClass().getSimpleName() + "] Calculator 1 is active - continuing search for calculator 2...");
            }


        }


        boolean foundCalc = false;


        // Did not find calculator 2 yet from active applications.
        // Activate each application through AT-SPI and find the PID for each active application through xprop.
        for (AtSpiAccessible application : applicationNodes) {


            // Activate application.
            if (TreeWalker.activateApplication(application)) {

                // Short pause to give the application time to activate.
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


                // Retrieve name and PID through xdotools.
                pid = xdotools.getPIDFromActiveWindow();
                assertTrue(pid > 0);

                String name = xdotools.getNameFromActiveWindow();


                System.out.println("[" + getClass().getSimpleName() + "] Active application is: '" + name + "' with PID: " + pid);


                // Check if the PID matches calculator 2.
                if (pid == calc2.get_pid()) {
                    System.out.println("[" + getClass().getSimpleName() + "] Found calculator 2!");
                    foundCalc = true;
                    break;
                } else if (pid == calc1.get_pid()) {
                    System.out.println("[" + getClass().getSimpleName() + "] Found calculator 1 - continuing search for calculator 2...");
                }


            } else {
                System.out.println("[" + getClass().getSimpleName() + "] Cannot activate the application!");
            }


        }


        if (!foundCalc) {
            System.out.println("[" + getClass().getSimpleName() + "] Could not find calculator 2!");
        }


        calc1.stop();
        calc2.stop();


        if (!foundCalc) {
            fail("Could not find calculator 2!");
        }


    }


    @Test
    public void ApplicationStats() {


        // Launch two instances of the calculator.
        LinuxProcess calc = LinuxProcess.fromExecutable(ApplicationPath_Galculator);


        // Short pause to give the applications time to start.
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        List<AtSpiAccessible> applicationNodes =  TreeWalker.getApplicationNodes(determineApplicationName(ApplicationPath_Galculator), false);
        assertEquals(true, applicationNodes.size() > 0);


        for (AtSpiAccessible a : applicationNodes) {
            a.retrieveAccessibleInfoTree();
        }

    }


    @Test
    public void MultipleWindows() {


        // Launch two instances of the calculator.
        LinuxProcess ged1 = LinuxProcess.fromExecutable(ApplicationPath_GEdit);
        LinuxProcess ged2 = LinuxProcess.fromExecutable(ApplicationPath_GEdit);


        // Short pause to give the applications time to start - and user to mess around.
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        List<AtSpiAccessible> applicationNodes =  TreeWalker.getApplicationNodes("soffice", false);
        //List<AtSpiAccessible> applicationNodes =  TreeWalker.getApplicationNodes(determineApplicationName(ApplicationPath_GEdit), false);
        assertEquals(true, applicationNodes.size() > 0);


        for (AtSpiAccessible a : applicationNodes) {
            a.retrieveAccessibleInfoTree();
        }


        List<AtSpiAccessible> scrollbars = TreeWalker.findNodesWithRole(applicationNodes.get(0), AtSpiRoles.ScrollBar);

        for (AtSpiAccessible s : scrollbars) {
            s.retrieveAccessibleInfoTree();
        }


        List<AtSpiAccessible> relationNodes = getRelationNodes(applicationNodes.get(0));


        for (AtSpiAccessible s : relationNodes) {
            s.retrieveAccessibleInfoTree();
        }


        // Stop the applications.
        ged1.stop();
        ged2.stop();


    }


    @Test
    public void Modality() {


        // Launch LibreOffice manually before running this so you can create a modal dialog or not.
        List<AtSpiAccessible> applicationNodes =  TreeWalker.getApplicationNodes("soffice", true);
        assertTrue(applicationNodes.size() > 0);


        boolean hasModals = TreeWalker.hasApplicationModalDialogs(applicationNodes.get(0));
        System.out.println("[" + getClass().getSimpleName() + "] Has modals: " + hasModals);


        List<AtSpiAccessible> nonModals = TreeWalker.getNonModalApplicationChildNodes(applicationNodes.get(0));


        for (AtSpiAccessible a : nonModals) {
            a.retrieveAccessibleInfo();
        }
        System.out.println("[" + getClass().getSimpleName() + "] NonModals: " + nonModals.size());


    }


    @Test
    public void Modality2() {



        // Short pause to give the applications time to start.
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        // Launch LibreOffice manually before running this so you can create a modal dialog or not.
        List<AtSpiAccessible> applicationNodes =  TreeWalker.getApplicationNodes("eclipse", true);
        assertTrue(applicationNodes.size() > 0);


        boolean hasModals = TreeWalker.hasApplicationModalDialogs(applicationNodes.get(0));
        System.out.println("[" + getClass().getSimpleName() + "] Has modals: " + hasModals);


        List<AtSpiAccessible> nonModals = TreeWalker.getNonModalApplicationChildNodes(applicationNodes.get(0));


        for (AtSpiAccessible a : nonModals) {
            a.retrieveAccessibleInfo();
        }
        System.out.println("[" + getClass().getSimpleName() + "] NonModals: " + nonModals.size());


        List<AtSpiAccessible> modals = TreeWalker.getModalApplicationChildNodes(applicationNodes.get(0));


        for (AtSpiAccessible a : modals) {
            a.retrieveAccessibleInfo();
        }
        System.out.println("[" + getClass().getSimpleName() + "] Modals: " + modals.size());


    }



    /**
     * Retrieves the name of an application from its file path.
     * @param path The file path of the executable that creates the application.
     * @return The name of the application.
     */
    private String determineApplicationName(String path) {

        return  path.substring(path.lastIndexOf("/") + 1);

    }


    /**
     * Creates a list of nodes that own relations.
     * @param node The node to start looking from.
     * @return A List of nodes that have relations.
     */
    private List<AtSpiAccessible> getRelationNodes(AtSpiAccessible node) {


        ArrayList<AtSpiAccessible> relationNodes = new ArrayList<>();


        if (node.relations().size() > 0) {
            relationNodes.add(node);
        }

        for (AtSpiAccessible a : node.children()) {
            for (AtSpiAccessible as : getRelationNodes(a)) {
                relationNodes.add(as);
            }
        }

        return relationNodes;

    }


}
