package org.fruit.example.tests;


import org.bridj.Pointer;
import org.fruit.alayer.linux.atspi.LibAtSpi;
import org.fruit.alayer.linux.LinuxProcess;
import org.fruit.alayer.linux.atspi.AtSpiAccessible;
import org.fruit.alayer.linux.atspi.TreeWalker;
import org.fruit.alayer.linux.atspi.enums.AtSpiRoles;
import org.fruit.alayer.linux.atspi.enums.AtSpiStateTypes;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Unit tests for working with the AT-SPI API.
 */
public class LibAtSpiTests {


    private static final String ApplicationPath_GEdit = "/usr/bin/gedit";


    @Test
    public void DesktopMethods() {


        // Get the desktop count.
        int desktopCount = LibAtSpi.atspi_get_desktop_count();
        assertEquals(1, desktopCount);


        // Get a pointer to the desktop accessible object.
        long desktopPointer = LibAtSpi.atspi_get_desktop(0);
        assertEquals(true, desktopPointer > 0);
        System.out.println("Desktop pointer: " + desktopPointer);


        // Get the name of the accessible object - for now the pointer to pointer for an error object can be null.
        Pointer<Byte> name = LibAtSpi.atspi_accessible_get_name(desktopPointer, 0);
        assertEquals("main", name.getCString());


        Pointer<Byte> desc = LibAtSpi.atspi_accessible_get_description(desktopPointer, 0);
        assertEquals("", desc.getCString());


        // Test the child count - get a reference for the current state.
        int childCount = LibAtSpi.atspi_accessible_get_child_count(desktopPointer, 0);


        // Launch a new application.
        LinuxProcess gedit =  LinuxProcess.fromExecutable(ApplicationPath_GEdit);


        // Short pause to give the application time to start.
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        // Take a new snapshot and compare them.
        int childCountAfterLaunch = LibAtSpi.atspi_accessible_get_child_count(desktopPointer, 0);
        assertEquals(childCount + 1, childCountAfterLaunch);


        // Get the UI role name of the accessible object.
        Pointer<Byte> roleName = LibAtSpi.atspi_accessible_get_role_name(desktopPointer, 0);
        assertEquals("desktop frame", roleName.getCString());


        // Get the role - method returns an int. To cast it to the corresponding enum value use the returned value as the index
        // for the enum's values array.
        AtSpiRoles role = AtSpiRoles.values()[LibAtSpi.atspi_accessible_get_role(desktopPointer, 0)];
        assertEquals(AtSpiRoles.DesktopFrame, role);



        // Test the children pointers.
        List<Long> children = AtSpiAccessible.getAccessibleChildrenPtrs(desktopPointer);
        assertEquals(childCount + 1, children.size());


        // Test the children as AtSpiAccessible instances.
        List<AtSpiAccessible> childrenExt = AtSpiAccessible.getAccessibleChildren(desktopPointer);
        assertEquals(childCount + 1, childrenExt.size());


        List<AtSpiAccessible> geditChildren;
        long geditActionPtr = 0;


        // Test gedit children's state.
        for (AtSpiAccessible a : childrenExt) {
            if (a.name().equals("gedit")){

                geditChildren = AtSpiAccessible.getAccessibleChildren(a);
                assertEquals("frame", geditChildren.get(0).roleName());
                assertEquals(true, geditChildren.get(0).states().isActive());

                geditActionPtr = LibAtSpi.atspi_accessible_get_action_iface(geditChildren.get(0).accessiblePtr());

            }
        }


        // Test if all children can be found for an AtSpiAccessible object.
        AtSpiAccessible desktopTree = TreeWalker.createAccessibleTree(desktopPointer, true);
        assertNotNull(desktopTree);

        List<AtSpiAccessible> ch = desktopTree.children();
        for (AtSpiAccessible a : ch) {
            a.retrieveAccessibleInfo();
        }


        // The current implementation is too heavy to get the entire tree with all information for the entire machine - apparently...
        desktopTree.retrieveAccessibleInfoTree();


        // Stop the application.
        gedit.stop();


        // Short pause to give the application time to exit.
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }


    @Test
    public void GrabFocus() {


        // Get a pointer to the desktop accessible object.
        long desktopPointer = LibAtSpi.atspi_get_desktop(0);
        assertEquals(true, desktopPointer > 0);
        System.out.println("Desktop pointer: " + desktopPointer);


        // Launch gedit.
        LinuxProcess gedit =  LinuxProcess.fromExecutable(ApplicationPath_GEdit);


        // Short pause to give the application time to start.
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        // Get a list of children.
        List<AtSpiAccessible> childrenExt = AtSpiAccessible.getAccessibleChildren(desktopPointer);
        assertEquals(true, childrenExt.size() > 0);


        // Find the gedit AtSpiAccessible.
        AtSpiAccessible geditAcc = null;


        for (AtSpiAccessible a : childrenExt) {
            if (a.name().equals("gedit")){
                geditAcc = a;
            }
        }
        assertNotNull(geditAcc);

        assert geditAcc != null;
        assertEquals(true, geditAcc.accessiblePtr() > 0);


        // Get the tree for gedit.
        AtSpiAccessible geditTree = TreeWalker.createAccessibleTree(geditAcc.accessiblePtr(), false);
        assertNotNull(geditTree);


        // Find something focusable.
        AtSpiAccessible focusable = TreeWalker.findFocusableApplicationElementNode(geditTree);
        assertNotNull(focusable);


        // Find a button.
        AtSpiAccessible button = findButton(geditTree, AtSpiRoles.PushButton);
        assertNotNull(button);
        assert button != null;
        button.retrieveAccessibleInfo();


        // Short pause to be able to hide gedit.
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        // Try to give gedit focus.
        assert focusable != null;
        assertNotNull(focusable.component());

        List<AtSpiStateTypes> stateList = focusable.states().getStates();
        focusable.retrieveAccessibleInfo();


        System.out.println("BoundingBox Screen:" + focusable.component().extentsOnScreen().toString());
        System.out.println("BoundingBox Window:" + focusable.component().extentsOnWindow().toString());
        System.out.println("Position Screen:" + focusable.component().positionOnScreen().toString());
        System.out.println("Position Window:" + focusable.component().positionOnWindow().toString());
        System.out.println("Size:" + focusable.component().size().toString());


        boolean successfullFocus = focusable.component().grabFocus();
        assertEquals(true, successfullFocus);


        // Wait a bit to let the user verify.
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        // Stop the application.
        gedit.stop();


        // Short pause to give the application time to exit.
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }


    /**
     * Tries to find a button element.
     * @param root Element to start from.
     * @param buttonType Type of button as a role.
     * @return An element that can is a button.
     */
    private AtSpiAccessible findButton(AtSpiAccessible root, AtSpiRoles buttonType) {

        if (root.role() == buttonType) {
            return root;
        }


        for (AtSpiAccessible a : root.children()) {

            if (a.role() == buttonType) {
                return a;
            }

            if (a.children().size() > 0) {

                AtSpiAccessible ret =  findButton(a, buttonType);

                if (ret != null) {
                    return ret;
                }
            }

        }

        return null;

    }


    public void GetStates() {


        // Get a pointer to the desktop accessible object.
        long desktopPointer = LibAtSpi.atspi_get_desktop(0);
        assertEquals(true, desktopPointer > 0);
        System.out.println("Desktop pointer: " + desktopPointer);


        // Test the child count - get a reference for the current state.
        // For some reason this throws a lot of console messages of:
        // g_object_unref: assertion 'G_IS_OBJECT (object)' failed
        int childCount = LibAtSpi.atspi_accessible_get_child_count(desktopPointer, 0);


        // Test the children as AtSpiAccessible instances.
        List<AtSpiAccessible> childrenExt = AtSpiAccessible.getAccessibleChildren(desktopPointer);
        assertEquals(childCount, childrenExt.size());


        // Test the get state set method.
        long stateSetPtr = LibAtSpi.atspi_accessible_get_state_set(desktopPointer);
        assertEquals(true, stateSetPtr > 0);


    }


}