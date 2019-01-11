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

package org.fruit.alayer.linux.atspi;

import java.util.ArrayList;
import java.util.List;
import org.fruit.alayer.linux.atspi.enums.AtSpiRoles;
import org.fruit.alayer.linux.atspi.enums.AtSpiStateTypes;
import org.fruit.alayer.linux.util.xdotools;
import java.util.Objects;

/**
 * Class to help with creating and traversing AtSpi trees/ nodes.
 */
public class TreeWalker {

    /**
     * Gets AtSpiAccessible nodes representing an application with the supplied name.
     * @param applicationName The name of the application to get the AtSpiAccessible nodes for.
     * @return A list of AtSpiAccessible nodes representing applications with the supplied name.
     */
    public static List<AtSpiAccessible> getApplicationNodes(String applicationName) {
        return getApplicationNodes(applicationName, false);
    }

    /**
     * Gets AtSpiAccessible nodes representing an application with the supplied name.
     * @param applicationName The name of the application to get the AtSpiAccessible nodes for.
     * @param createTree If True, the node will be completely filled with information.
     * @return A list of AtSpiAccessible nodes representing applications with the supplied name.
     */
    public static List<AtSpiAccessible> getApplicationNodes(String applicationName, boolean createTree) {

        // The list that will hold the requested application nodes.
        ArrayList<AtSpiAccessible> applicationNodes = new ArrayList<>();

        // Get a pointer to the desktop accessible object.
        long desktopPointer = LibAtSpi.atspi_get_desktop(0);

        // Create an AtSpiAccessible object from the pointer.
        AtSpiAccessible desktopNode = AtSpiAccessible.CreateInstance(desktopPointer);

        if (applicationName.endsWith(".jar")) {
            applicationName = applicationName.replace(".jar", "");
        }

        if (desktopNode != null) {

            // Each child of the desktop should be an application - still check to be sure.
            // All nodes with the name corresponding to the requested name will be returned.
            for (AtSpiAccessible child: desktopNode.children()) {

                if (child.role() == AtSpiRoles.Application && Objects.equals(child.name().toLowerCase(), applicationName.toLowerCase())) {
                    applicationNodes.add(child);
                }

            }

        }

        // Get all children for each application node - making it the root of a tree.
        if (createTree) {
            for (AtSpiAccessible appplicationNode: applicationNodes) {
                appplicationNode.retrieveAccessibleInfoTree();
            }
        }

        return applicationNodes;

    }

    /**
     * Finds the application node belonging to the application with the supplied application name and PID.
     * @param applicationName The name of the application to look for.
     * @param pid The PID of the application to look for.
     * @return The application node belonging to the application with the supplied applicaiton name and PID.
     */
    public static AtSpiAccessible findApplicationNode(String applicationName, long pid) {
        return findApplicationNode(getApplicationNodes(applicationName), pid);
    }

    /**
     * Finds the application node belonging to the application with the supplied PID.
     * @param applicationNodes A list of application nodes (most likely from likely named application) that should
     *                         contain the application to which the supplied PID belongs.
     * @param pid The PID of the application to look for.
     * @return The application node belonging to the application with the supplied PID.
     */
    public static AtSpiAccessible findApplicationNode(List<AtSpiAccessible> applicationNodes, long pid) {

        if (applicationNodes == null) {
            throw new IllegalArgumentException("The application nodes list cannot be null.");
        }

        if (pid <= 0) {
            throw new IllegalArgumentException("The PID of an applicaiton cannot be equal to or less than zero.");
        }

        if (applicationNodes.size() == 0) {
            System.out.println("Could not find a single running application by the supplied name.");
        }

        // Activate each application through AT-SPI and find the PID for each active application through xdotool.
        // Once verified that the node activated the instance of the application launched by us - stop.
        for (AtSpiAccessible application: applicationNodes) {

            // Activate application.
            if (TreeWalker.activateApplication(application)) {

                // Short pause to give the application time to activate.
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // Retrieve PID through xdotool.
                int activePID = xdotools.getPIDFromActiveWindow();

                // Check if the PID matches the PID of the process we're supposed to activate.
                if (pid == activePID) {
                    return application;
                }

            } else {
                System.out.println("Cannot activate an application with the same name - continuing loop...");
            }

        }

        System.out.println("Cannot find an application node in the list belonging to an application with PID '" + pid + "'.");
        return null;

    }

    /**
     * Retrieves the application that is currently active from the supplied list of application nodes.
     * @param applicationNodes A list of AtSpiAccessible nodes representing an application (most likely with the same name).
     * @return The application that is currently active.
     */
    public static AtSpiAccessible getActiveApplicationNode(List<AtSpiAccessible> applicationNodes) {

        if (applicationNodes == null) {
            throw new IllegalArgumentException("The application nodes list argument cannot be null.");
        }

        for (AtSpiAccessible application: applicationNodes) {
            if (isApplicationActive(application)) {
                // There can only be one active application at the same time...
                return application;
            }
        }

        // Active application is not one of the supplied applications.
        return null;

    }

    /**
     * Activates an application by focusing a focusable element.
     * @param applicationNode The AtSpiAccessible application node of the application to activate.
     * @return True if activating is successful; False otherwise.
     */
    public static boolean activateApplication(AtSpiAccessible applicationNode) {

        if (applicationNode.role() != AtSpiRoles.Application) {
            throw new IllegalArgumentException("The application node should be an AtSpiAccessible with role 'Application'.");
        }

        // Find a child element that can be focused.
        AtSpiAccessible focusableNode = findFocusable(applicationNode);

        if (focusableNode == null) {
            System.out.println("Could not find a focusable application element node for '" + applicationNode.name() + "'.");
            return false;
        }

        if (focusableNode.component() == null) {
            System.out.println("The component of the a focusable application element node is null for '" + applicationNode.name() + "'.");
            return false;
        }

        // Focus the element and with it activate the application.
        return focusableNode.component().grabFocus();

    }

    /**
     * Determines whether an application is active.
     * @param applicationNode The AtSpiAccessible application node of the application to check whether it is active or not.
     * @return True if the application is active; False otherwise.
     */
    public static boolean isApplicationActive(AtSpiAccessible applicationNode) {

        if (applicationNode.role() != AtSpiRoles.Application) {
            throw new IllegalArgumentException("The application node should be an AtSpiAccessible with role 'Application'.");
        }

        // Check the children - Check all for the frame role and active state.
        for (AtSpiAccessible child: applicationNode.children()) {
            if (child.role() == AtSpiRoles.Frame && child.states().isActive()) {
                return true;
            }
        }

        return false;

    }

    /**
     * Determines if an application has modal windows open.
     * @param applicationNode The application node of the application to check modal windows for.
     * @return True if the application has modal windows; False otherwise.
     */
    public static boolean hasApplicationModalDialogs(AtSpiAccessible applicationNode) {

        if (applicationNode.role() != AtSpiRoles.Application) {
            throw new IllegalArgumentException("The application node should be an AtSpiAccessible with role 'Application'.");
        }

        // Check the children - this could be frames, windows or modal dialogs - check if one is of them is modal.
        for (AtSpiAccessible child: applicationNode.children()) {

            AtSpiStateSet childState = child.states();

            if (childState != null && childState.isModal()) {
                return true;
            }

        }

        return false;

    }

    /**
     * Retrieves a list of non-modal application child nodes - it will only process the direct children of the
     * application node, i.e. the frame, window, dialog nodes that can be modal.
     * @param applicationNode The application node of the application to get non-modal windows for.
     * @return A list of non-modal windows for an application.
     */
    public static List<AtSpiAccessible> getNonModalApplicationChildNodes(AtSpiAccessible applicationNode) {

        if (applicationNode.role() != AtSpiRoles.Application) {
            throw new IllegalArgumentException("The application node should be an AtSpiAccessible with role 'Application'.");
        }

        ArrayList<AtSpiAccessible> nonModals = new ArrayList<>();

        // Check the children - this could be frames, windows or modal dialogs - add the non-modals to the list.
        for (AtSpiAccessible child: applicationNode.children()) {

            AtSpiStateSet childState = child.states();

            if (childState != null && !childState.isModal()) {
                nonModals.add(child);
            }

        }

        return nonModals;

    }

    /**
     * Retrieves a list of non-modal application child nodes - it will only process the direct children of the
     * application node, i.e. the frame, window, dialog nodes that can be modal.
     * @param applicationNode The application node of the application to get non-modal windows for.
     * @return A list of non-modal windows for an application.
     */
    public static List<AtSpiAccessible> getModalApplicationChildNodes(AtSpiAccessible applicationNode) {

        if (applicationNode.role() != AtSpiRoles.Application) {
            throw new IllegalArgumentException("The application node should be an AtSpiAccessible with role 'Application'.");
        }

        ArrayList<AtSpiAccessible> modals = new ArrayList<>();

        // Check the children - this could be frames, windows or modal dialogs - add the non-modals to the list.
        for (AtSpiAccessible child: applicationNode.children()) {

            AtSpiStateSet childState = child.states();

            if (childState != null && childState.isModal()) {
                modals.add(child);
            }

        }

        return modals;

    }

    /**
     * Retrieves a list of application child nodes - it will only process the direct children of the
     * application node, i.e. the frame, window, dialog nodes.
     * @param applicationNode The application node of the application to get windows, frames, dialogs for.
     * @return A list of windows, frames, dialogs for an application.
     */
    public static List<AtSpiAccessible> getApplicationWindowNodes(AtSpiAccessible applicationNode) {

        if (applicationNode.role() != AtSpiRoles.Application) {
            throw new IllegalArgumentException("The application node should be an AtSpiAccessible with role 'Application'.");
        }

        ArrayList<AtSpiAccessible> windows = new ArrayList<>();

        // Check the children - this could be frames, windows or modal dialogs - add the windows, frames,dialogs
        // to the list. This could be optimized by filtering unwanted stuff by role if there is any.
        for (AtSpiAccessible child: applicationNode.children()) {

            AtSpiStateSet childState = child.states();

            if (childState != null && !childState.isModal()) {
                windows.add(child);
            }

        }

        return windows;

    }

    /**
     * Determines the bounding box of the application on the screen.
     * @param applicationNode The AtSpiAccessible application node of the application to get the bounding box for.
     * @return The bounding box of the application or a bounding box with 0 values if the bounding box could not be determined.
     */
    public static AtSpiRect getApplicationExtentsOnScreen(AtSpiAccessible applicationNode) {

        if (applicationNode.role() != AtSpiRoles.Application) {
            throw new IllegalArgumentException("The application node should be an AtSpiAccessible with role 'Application'.");
        }

        // Check the children - should be only one? Check all for the frame role and active state in any case to be sure.
        for (AtSpiAccessible child: applicationNode.children()) {
            if (child.role() == AtSpiRoles.Frame && child.component() != null) {
                return child.component().extentsOnScreen();
            }
        }

        return new AtSpiRect();

    }

    /**
     * Finds nodes with a certain role in the supplied node (which should have been processed by a method that
     * creates parent - child relations.
     * @param node The node to start from to search for a specific role.
     * @param role The role the node should have.
     * @return A list of AtSpiAccessible nodes with a certain role.
     */
    public static List<AtSpiAccessible> findNodesWithRole(AtSpiAccessible node, AtSpiRoles role) {

        ArrayList<AtSpiAccessible> nodesWithRole = new ArrayList<>();

        if (node.role() == role) {
            nodesWithRole.add(node);
        }

        if (node.childCount() > 0) {
            for (AtSpiAccessible a: node.children()) {
                for (AtSpiAccessible ac: findNodesWithRole(a, role)) {
                    nodesWithRole.add(ac);
                }
            }
        }

        return nodesWithRole;

    }

    /**
     *
     * @param name
     * @param roles
     * @param states
     * @return
     */
    public static List<AtSpiAccessible> findNodes(String name, AtSpiRoles[] roles, AtSpiStateTypes[] states) {

        // TODO: implement finding a node with certain properties in a tree.
        return null;

    }

    /**
     * Tries to find a focusable element of an AtSpiAccessible application child node.
     * @param applicationNode Element to start from - which needs to be an AtSpiAccessible application node.
     * @return An element that can be focused.
     */
    public static AtSpiAccessible findFocusableApplicationElementNode(AtSpiAccessible applicationNode) {

        if (applicationNode.role() != AtSpiRoles.Application) {
            throw new IllegalArgumentException("The application node should be an AtSpiAccessible with role 'Application'.");
        }

        return findFocusable(applicationNode);

    }

    /**
     * Tries to find a focusable element.
     * @param root Element to start from.
     * @return An element that can be focused.
     */
    private static AtSpiAccessible findFocusable(AtSpiAccessible root) {

        if (root.states().isFocusable() && root.component() != null) {
            return root;
        }

        for (AtSpiAccessible a: root.children()) {

            if (a.states().isFocusable() && a.component() != null) {
                return a;
            }

            if (a.children().size() > 0) {

                AtSpiAccessible ret =  findFocusable(a);

                if (ret != null) {
                    return ret;
                }
            }

        }

        return null;

    }

    /**
     * Creates an AtSpiAccessible object with all of its descendants available to be able to be browsed as a tree.
     * @param accessiblePtrStart Pointer to an AtSpiAccessible object where the tree will start.
     * @param fillInfo Whether to create a tree with full info or just with parent - child relations.
     * @return An AtSpiAccessible object with all of its descendants available.
     */
    public static AtSpiAccessible createAccessibleTree(long accessiblePtrStart, boolean fillInfo) {

        AtSpiAccessible parent = AtSpiAccessible.CreateInstance(accessiblePtrStart);

        createAccessibleTree(parent, fillInfo);

        return parent;

    }

    /**
     * Creates an AtSpiAccessible object with all of its descendants available to be able to be browsed as a tree.
     * @param root The AtSpiAccessible object that will act as root of the tree.
     * @param fillInfo Whether to create a tree with full info or just with parent - child relations.
     */
    public static void createAccessibleTree(AtSpiAccessible root, boolean fillInfo) {
        if (root != null) {
            root.createTree(fillInfo);
        }
    }

}
