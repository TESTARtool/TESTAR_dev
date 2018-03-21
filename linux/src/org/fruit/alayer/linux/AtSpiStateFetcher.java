package org.fruit.alayer.linux;


import org.fruit.Util;
import org.fruit.alayer.*;
import org.fruit.alayer.linux.atspi.*;
import org.fruit.alayer.linux.atspi.enums.AtSpiRoles;
import org.fruit.alayer.linux.enums.AtSpiElementOrientations;
import org.fruit.alayer.linux.util.GdkHelper;

import java.util.List;
import java.util.concurrent.Callable;


/**
 * Represents an object that builds up a tree around AT-SPI elements and then converts it to a different State/ widget tree that Testar uses to determine an application's state.
 */
public class AtSpiStateFetcher implements Callable<AtSpiState> {


    //region Global variables


    private final SUT _system;
    private static final int _retryCountFindSut = 5;


    //endregion


    //region Constructors


    /**
     * Creates a new instance of an object that retrieves the state of a supplied SUT.
     * @param system The SUT to fetch the current state for.
     */
    AtSpiStateFetcher(SUT system){
        this._system = system;
    }


    //endregion


    //region Callable implementation


    /**
     * Creates the AtSpiState object for a SUT.
     * This method will at some point (through concurrency service) be called by the AtSpiStateBuilder class (in apply method).
     * @return The AtSpiState object for a SUT.
     * @throws Exception A lot can go wrong...
     */
    @Override
    public AtSpiState call() throws Exception {


        // Create an AT-SPI tree.
        AtSpiRootElement rootOfAtSpiTree = buildAtSpiTree();


        // Convert the AT-SPI tree into a State (widget tree) that Testar wants to use.
        AtSpiState widgetTree = createWidgetTree(rootOfAtSpiTree);


        // Add some tags to the state tree - it describes a process and it is responding (most likely since
        // we only queried AT-SPI and processed some data we didn't actually check any responsiveness).
        widgetTree.set(Tags.Role, Roles.Process);
        widgetTree.set(Tags.NotResponding, false);


        // Give each widget in the tree a tag describing the path to find it.
        for (Widget w : widgetTree)
            w.set(Tags.Path, Util.indexString(w));


        return widgetTree;


    }


    //endregion


    //region Helper methods


    //region Testar's AT-SPI tree


    /**
     * Creates an AT-SPI tree by wrapping the wanted data in AtSpiElements.
     * @return The root (AtSpiRootElement) of the AT-SPI tree.
     */
    private AtSpiRootElement buildAtSpiTree() {


        // Create the root of the tree and fill it with default values.
        AtSpiRootElement atSpiRootElement = new AtSpiRootElement();
        atSpiRootElement.isRunning = _system.isRunning();
        atSpiRootElement.timeStamp = System.currentTimeMillis();
        atSpiRootElement.hasStandardKeyboard = _system.get(Tags.StandardKeyboard, null) != null;
        atSpiRootElement.hasStandardMouse = _system.get(Tags.StandardMouse, null) != null;


        // Instead of the bounding box of the application use the screen bounding box - no clue why though.
        atSpiRootElement.boundingBoxOnScreen = GdkHelper.getScreenBoundingBox();


        if (!atSpiRootElement.isRunning) {
            return atSpiRootElement;
        }


        // The application is running - get detailed information about it...
        atSpiRootElement.pid = _system.get(Tags.PID);
        atSpiRootElement.isActive = LinuxProcess.isActive(atSpiRootElement.pid);


        // Get the AT-SPI application node.
        String applicationName = _system.get(Tags.Desc).substring(_system.get(Tags.Desc).lastIndexOf("/") + 1);
        AtSpiAccessible applicationNode = TreeWalker.findApplicationNode(applicationName, atSpiRootElement.pid);
        int currentTry = 0;


        if (applicationNode == null) {

            do {


                currentTry += 1;
                System.out.println("AT-SPI did not find the application with name: " + applicationName + "! Retrying, try " + currentTry +  "/ " + _retryCountFindSut + "...");


                // Short pause to give the AT-SPI time to update.
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


                // Actual retry of the operation.
                applicationNode = TreeWalker.findApplicationNode(applicationName, atSpiRootElement.pid);


            } while (applicationNode == null && currentTry < _retryCountFindSut);


        }


        // If it's still null exit since no information can be queried from AT-SPI.
        if (applicationNode == null) {
            return atSpiRootElement;
        }


        // This time instead of screen use the actual application bounding box.
        AtSpiRect appExtents = TreeWalker.getApplicationExtentsOnScreen(applicationNode);
        atSpiRootElement.boundingBoxOnScreen = Rect.from(appExtents.x, appExtents.y, appExtents.width, appExtents.height);


        // We need to create a tree of AT-SPI nodes (AtSpiAccessible) wrapped in the Testar specific AtSpiElement object.
        // The Windows version queries Windows for every window and then uses the window handles to search for the
        // corresponding element in the UIA tree - Linux systems cannot do this, therefore we only use the AT-SPI tree.
        // Note: the Windows version determines multiple windows (for z-order) by querying windows for top level windows
        // - the UIA tree will have those as separate nodes. Furthermore, messagebox windows (which are modal dialogs)
        // show as child nodes of a window in the UIA tree. AT-SPI has multiple windows and modal windows as children of
        // the application node.


        // Create parent - child relations for the application node.
        TreeWalker.createAccessibleTree(applicationNode, true);


        // Retrieve the information Testar wants and wrap it in AtSpiElements.
        wrapAtSpiNodes(applicationNode, atSpiRootElement);


        // Modal dialog handling - needed to know which elements are blocked for interaction...
        // If an application has modal windows then the other non-modal windows are blocked.
        if (TreeWalker.hasApplicationModalDialogs(applicationNode)) {


            // Find the non-modal dialog nodes.
            List<AtSpiAccessible> nonModalNodes = TreeWalker.getNonModalApplicationChildNodes(applicationNode);


            // Find the AtSpiElement representing the node and mark it and its children blocked.
            for (AtSpiAccessible a : nonModalNodes) {

                boolean markingSuccess = findAndMarkBlocked(a, atSpiRootElement);

                if (!markingSuccess) {
                    System.out.println("Could not find non-modal element '" + a.name() + "' and mark it and its children blocked.");
                }

            }


            // Find the modal dialog nodes.
            List<AtSpiAccessible> modalNodes = TreeWalker.getModalApplicationChildNodes(applicationNode);


            // TODO: Because this stuff is undocumented, verify: the last modal element in an application's children is the modal node that blocks all other nodes (modal and non-modal).
            modalNodes.remove(modalNodes.size() - 1);


            // Find the AtSpiElement representing the node and mark it and its children blocked.
            for (AtSpiAccessible a : modalNodes) {

                boolean markingSuccess = findAndMarkBlocked(a, atSpiRootElement);

                if (!markingSuccess) {
                    System.out.println("Could not find modal element '" + a.name() + " and mark it and its children blocked.");
                }

            }


        }


        // z-index handling - needed to determine obscured elements - every window and its children
        // should have a different z-index.
        List<AtSpiAccessible> windows = TreeWalker.getApplicationWindowNodes(applicationNode);

        for (int i = 0; i < windows.size(); i++) {
            findAndSetZIndices(windows.get(i), atSpiRootElement, i + 1);
        }


        // Build a TopLevelContainer map for the root element.
        createTopLevelContainerMap(atSpiRootElement, atSpiRootElement);


        return atSpiRootElement;


    }


    /**
     * Wraps an AtSpiAccessible node and its children.
     * @param nodeToWrap The AtSpiAccessible node that will be wrapped.
     * @param parentOfWrappingElement The parent of the AtSpiElement that will wrap the node.
     */
    private void wrapAtSpiNodes(AtSpiAccessible nodeToWrap, AtSpiElement parentOfWrappingElement) {


        // Process the current node and element.
        AtSpiElement createdParent = createAndFillAtSpiElement(nodeToWrap, parentOfWrappingElement);


        // Process childs of the node to wrap.
        for (AtSpiAccessible a : nodeToWrap.children()) {
            wrapAtSpiNodes(a, createdParent);
        }


    }


    /**
     * Creates and fills an AtSpiElement from an AtSpiAccessible node - also defines the parent AtSpiElement.
     * @param node The node that will provide the information for the AtSpiElement.
     * @param parent The parent AtSpiElement of the newly created AtSpiElement in Testar´ś own implementation of
     *               the AtSpi tree.
     * @return Returns the newly created AtSpiElement.
     */
    private AtSpiElement createAndFillAtSpiElement(AtSpiAccessible node, AtSpiElement parent) {


        if (node == null || node.accessiblePtr() == 0) {
            return null;
        }


        // Create new element and create the parent - child relations.
        AtSpiElement nElement = new AtSpiElement(parent);
        parent.children.add(nElement);
        parent.root.pointerMap.put(nElement.accessiblePtr, nElement);


        // Generic information.
        // Notes:
        //      - HelpTest: not defined in AT-SPI - also used as ToolTipText in Testar, perhaps AT-SPI
        //                  does have such property somewhere?
        //      - AutomationId: AT-SPI doesn´t use IDs to find elements - it uses pointers?
        //      - FrameworkId: called toolkit name in AT-SPI.
        //      - CtrlId: equal to the name of the Role enumeration in AT-SPI.
        //      - ClassName: perhaps under AtSpiAccessible's AtSpi-Relation?? - since it's not being
        //                   used don't implement for now.
        nElement.accessiblePtr = node.accessiblePtr();
        nElement.name = node.name();
        nElement.description = node.description();
        nElement.toolkitName = node.toolkitName();
        nElement.role = node.role();

        if (node.component() != null) {

            // Use the location on the screen since Testar mimics the mouse to move to screen locations.
            AtSpiRect bb = node.component().extentsOnScreen();


            // AT-SPI elements often contain negative width and height values for some reason. Testar cannot handle these
            // values so make sure they're always non-negative.
            nElement.boundingBoxOnScreen = Rect.from(bb.x, bb.y, (bb.width >= 0) ? bb.width : 0, (bb.height >= 0) ? bb.height : 0);


        } else {

            // The element does not have visual component - mark it as non-interactable by setting the ignore property.
            nElement.boundingBoxOnScreen = null;
            nElement.ignore = true;

        }


        // State information.
        // Notes:
        //      - IsTopMost: AT-SPI does not support this and Linux only by sending X-events not as a property.
        //      - WindowVisualState: no clue what this is - never used on Windows so removed.
        //      - WindowInteractionState: not available in AT-SPI - only used to determine whether or not a
        //      - window (and it's children) is blocked or not - do a different block check.
        AtSpiStateSet nodeStates = node.states();

        if(nodeStates != null) {

            nElement.isEnabled = nodeStates.isEnabled();
            nElement.hasFocus = nodeStates.isFocused();
            nElement.isFocusable = nodeStates.isFocusable();
            nElement.isModal = nodeStates.isModal();

            boolean h = nodeStates.isHorizontal();
            boolean v = nodeStates.isVertical();


            if (h && v) {
                nElement.orientation = AtSpiElementOrientations.HorizontalAndVertical;
            } else if (h) {
                nElement.orientation = AtSpiElementOrientations.Horizontal;
            } else if (v) {
                nElement.orientation = AtSpiElementOrientations.Vertical;
            } else {
                nElement.orientation = AtSpiElementOrientations.Undefined;
            }


        } else {
            // If the node doesn't have state information, then the node is most likely invalid.
            nElement.ignore = true;
        }


        // Inferred information.
        // Filter on ScrollBar nodes - they will have a (ControllerFor-) relationship with a ScrollPane or Viewport.
        // However, relationships are hardly ever defined and no occurrences have been found of a ScrollBar, ScrollPane
        // or Viewport having a relationship with whatever.
        if (nElement.role == AtSpiRoles.ScrollBar) {

            nElement.canScroll = true;


            // Get the values for the scrollbar to determine the percentage scrolled.
            // Note: the viewPortSize should be percentage visible but since we don't know which element this
            // scrollbar controls we can't get anything - use min and max of scrollbar itself.
            AtSpiValue scrollBarValues = node.value();
            double current = 0;
            double min = 0;
            double max = 0;
            double scrollPercentage = -1;
            double viewPortSize = -1;


            if (scrollBarValues != null) {
                current = scrollBarValues.currentValue();
                min = scrollBarValues.minimumValue();
                max = scrollBarValues.maximumValue();
            }

            if (max != 0) {
                scrollPercentage = current / (max - min);
                viewPortSize = max - min;
            }


            if (nElement.orientation == AtSpiElementOrientations.Horizontal) {

                nElement.canScrollHorizontally = true;
                nElement.hScrollPercentage = scrollPercentage;
                nElement.vScrollPercentage = -1;
                nElement.hScrollViewSizePercentage = viewPortSize;
                nElement.vScrollViewSizePercentage = -1;

            } else if (nElement.orientation == AtSpiElementOrientations.Vertical) {

                nElement.canScrollVertically = true;
                nElement.vScrollPercentage = scrollPercentage;
                nElement.hScrollPercentage = -1;
                nElement.vScrollViewSizePercentage = viewPortSize;
                nElement.hScrollViewSizePercentage = -1;

            }

        }


        // Infer from Role if the element is a toplevel container.
        if (nElement.role == AtSpiRoles.Frame || nElement.role == AtSpiRoles.Menu || nElement.role == AtSpiRoles.MenuBar ||
                nElement.role == AtSpiRoles.Window || nElement.role == AtSpiRoles.Application ||
                nElement.role == AtSpiRoles.DocumentFrame) {
            nElement.isTopLevelContainer = true;
        }


        return nElement;


    }


    /**
     * Finds the AtSpiElement representing a certain node and marks it and its children as blocked.
     * @param node The node for which to find the AtSpiElement.
     * @param element An element of Testar's representation of the AT-SPI tree.
     * @return True if found and marked; False otherwise.
     */
    private boolean findAndMarkBlocked(AtSpiAccessible node, AtSpiElement element) {


        // Check the current node.
        if (node.accessiblePtr() == element.accessiblePtr) {

            // Mark blocked - exit after blocking since it will have marked all children as well.
            markBlocked(element);
            return true;

        }


        // Current node is not the node we're looking for - check the child elements.
        for (AtSpiElement e : element.children) {

            boolean result = findAndMarkBlocked(node, e);

            if (result) {
                // Marked as blocked - we can stop searching.
                return true;
            }


        }


        return false;


    }


    /**
     * Marks an element and its children as blocked.
     * @param element The element and its children to mark blocked.
     */
    private void markBlocked(AtSpiElement element) {

        // Process current element.
        element.isBlocked = true;


        // And its children.
        for (AtSpiElement e : element.children) {
            markBlocked(e);
        }

    }


    /**
     * Finds the AtSpiElement representing a certain node and sets it and its children z-index.
     * @param node The node for which to find the AtSpiElement.
     * @param element An element of Testar's representation of the AT-SPI tree.
     * @param zIndex The zIndex for this frame, dialog, window and its children.
     * @return True if found and set; False otherwise.
     */
    private boolean findAndSetZIndices(AtSpiAccessible node, AtSpiElement element, int zIndex) {


        // Check the current node.
        if (node.accessiblePtr() == element.accessiblePtr) {

            // Set z-indices - exit after since it will have set the z-index on all children as well.
            setZIndex(element, zIndex);
            return true;

        }


        // Current node is not the node we're looking for - check the child elements.
        for (AtSpiElement e : element.children) {

            boolean result = findAndSetZIndices(node, e, zIndex);

            if (result) {
                // z-indices set - we can stop searching.
                return true;
            }


        }


        //System.out.println("Could not find frame, window, dialog element and set the z-index on it and its children.");
        return false;


    }


    /**
     * Sets the z-index on an element and its children.
     * @param element The element and its children to set the z-index for.
     * @param zIndex The z-index to use on the current element and its children.
     */
    private void setZIndex(AtSpiElement element, int zIndex) {

        // Process current element.
        element.zIndex = zIndex;


        // And its children.
        for (AtSpiElement e : element.children) {
            setZIndex(e, zIndex);
        }

    }


    /**
     * Creates a list of top level container elements and sorts the list.
     * @param root The root of Testar's AT-SPI tree representation.
     * @param element The current element to process.
     */
    private void createTopLevelContainerMap(AtSpiRootElement root, AtSpiElement element) {


        // Check if the current element is a top level container.
        if (element.isTopLevelContainer) {
            root.topLevelContainerMap.addElement(element);
        }


        // Process the children of the current element.
        for (AtSpiElement e : element.children) {
            createTopLevelContainerMap(root, e);
        }


        if (root.equals(element)) {
            // This is the first method that got called and we're finished building the map - sort the elements.
            root.topLevelContainerMap.sort();
        }


    }


    //endregion


    //region Widget Tree


    /**
     * Creates a widget tree by creating widgets and linking them to AtSpiElements in the AtSpiTree. It also creates
     * the parent - child relations in the widget tree.
     * @param root The AtSpiRootElement to start the linking from.
     * @return A State (widget tree) object.
     */
    private AtSpiState createWidgetTree(AtSpiRootElement root){


        // Create a new AtSpiState (root of widget tree) and link it to the supplied AtSpiRootElement.
        // The link in the AtSpiState is created on instantiation.
        AtSpiState state = new AtSpiState(root);
        root.backRef = state;


        // Process each child of the AtSpiRootElement in the AT-SPI tree.
        for(AtSpiElement childElement : root.children){
            if (!childElement.ignore) {
                createWidgetTree(state, childElement);
            } else {

                // The node should be ignored, but on Linux there will always be an Application node that needs to be
                // ignored as first child - instead process its children.
                if (childElement.role == AtSpiRoles.Application) {
                    for (AtSpiElement appChild : childElement.children) {
                        if (!appChild.ignore) {
                            createWidgetTree(state, appChild);
                        }
                    }
                }

            }

        }


        return state;


    }


    /**
     * Creates a widget around the next AtSpiElement and links it as a widget child to a parent AtSpiWidget and
     * creates the parent - child relation between widgets in the widget tree.
     * @param parent The parent widget.
     * @param element The AtSpiElement that will be the child of the AtSpiWidget parent.
     */
    private void createWidgetTree(AtSpiWidget parent, AtSpiElement element){


        // Add the new AtSpiElement to the widget tree - it creates a new AtSpiWidget that links to the
        // supplied AtSpiElement.
        // The widget tree root is used to add the element because that way it can add a reference to itself to
        // the newly created widget - this could also have been retrieved differently though...
        AtSpiWidget w = parent.addChild(element);
        element.backRef = w;


        // Process each child of the AtSpiElement in the AT-SPI tree.
        for(AtSpiElement child : element.children) {
            createWidgetTree(w, child);
        }


    }


    //endregion


    //endregion

}