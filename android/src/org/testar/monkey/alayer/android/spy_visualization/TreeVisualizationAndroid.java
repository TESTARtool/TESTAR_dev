/***************************************************************************************************
 *
 * Copyright (c) 2020 - 2022 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2020 - 2022 Open Universiteit - www.ou.nl
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

package org.testar.monkey.alayer.android.spy_visualization;

import org.testar.monkey.Pair;
import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.android.enums.AndroidTags;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.util.LinkedList;

public class TreeVisualizationAndroid extends JPanel implements TreeSelectionListener {
    private final JPanel infoPaneLeft = new JPanel();
    private final JPanel infoPaneRight = new JPanel();
    public JTree tree;
    private DefaultMutableTreeNode tempTreeNode;
    private final MobileVisualizationAndroid mobileVisualizationAndroid;
    private boolean triggeredByOverlayClick = false;
    private final JScrollPane treeView;

    private String selectedNodePath;

    private LinkedList<Pair<DefaultMutableTreeNode, Integer>> toBeReplacedA = null;
    private LinkedList<Pair<DefaultMutableTreeNode, Integer>> toBeReplacedWithB = null;


    /** Initializer for the tree component of the spy mode visualization (right hand side screen). */
    public TreeVisualizationAndroid(MobileVisualizationAndroid mobileVisualizationAndroid, Widget widget) {
        super(new GridLayout(1,0));

        this.mobileVisualizationAndroid = mobileVisualizationAndroid;

        //Create the nodes.
        DefaultMutableTreeNode top =
                new DefaultMutableTreeNode(widget.get(Tags.Desc));
        createNodes(top, widget);

        //Create a tree that allows one selection at a time.
        tree = new JTree(top);
        tree.getSelectionModel().setSelectionMode
                (TreeSelectionModel.SINGLE_TREE_SELECTION);

        //Listen for when the selection changes.
        tree.addTreeSelectionListener(this);

        //Create the scroll pane and add the tree to it.
        treeView = new JScrollPane(tree);

        // Create the viewing pane for obtaining more detailed information on a widget.
        infoPaneLeft.setLayout(new BoxLayout(infoPaneLeft, BoxLayout.Y_AXIS));
        infoPaneRight.setLayout(new BoxLayout(infoPaneRight, BoxLayout.Y_AXIS));

        // Make each side scrollable
        JScrollPane infoLeftView  = new JScrollPane(infoPaneLeft);
        JScrollPane infoRightView = new JScrollPane(infoPaneRight);
        infoLeftView.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        infoRightView.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        infoLeftView.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        infoRightView.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        // Split pane between left/right scroll panes
        JSplitPane infoSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, infoLeftView, infoRightView);
        infoSplitPane.setDividerSize(0);
        infoSplitPane.setDividerLocation(150);

        // Add the scroll panes to a split pane (tree on top, info at bottom).
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setTopComponent(treeView);
        splitPane.setBottomComponent(infoSplitPane);

        Dimension minimumSize = new Dimension(400, 600);
        infoSplitPane.setMinimumSize(minimumSize);
        treeView.setMinimumSize(minimumSize);
        splitPane.setDividerLocation(550);
        splitPane.setDividerSize(0);
        splitPane.setPreferredSize(new Dimension(500, 850));

        //Add the split pane to this panel.
        add(splitPane);

        infoPaneLeft.revalidate();
        infoPaneLeft.repaint();
        infoPaneRight.revalidate();
        infoPaneRight.repaint();

        splitPane.revalidate();
        splitPane.repaint();
    }


    /** Required by TreeSelectionListener interface.
     * When an object in the tree gets clicked this method is called such that the object can be unfolded and show the
     * detailed information of the clicked tree object.
     */
    public void valueChanged(TreeSelectionEvent e) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                tree.getLastSelectedPathComponent();

        if (tempTreeNode == null) {
            tempTreeNode = node;
        }

        if (node == null) return;

        // If statement making sure when the root gets clicked it does not try to obtain detailed information.
        if (!(node.equals(tree.getModel().getRoot()))) {
            Widget nodeWidget = (Widget)node.getUserObject();

            // Sets the selected node path into the variable which tracks the last selected node
            selectedNodePath = (new TreePath(node.getPath())).toString();

            //Call which makes sure the information of the clicked node (AndroidWidget) is displayed on the screen.
            displayWidgetInfo(nodeWidget);
        }

        // If else clause makes sure not to send back an event to overlay when this method is triggered by the overlay.
        if (!triggeredByOverlayClick) {
            mobileVisualizationAndroid.treeToOverlay(node);
        } else {
            triggeredByOverlayClick = false;
        }
    }


    /** Creates a new tree based on the newly retrieved state (root node).
     * Additionally, calls method which determines which nodes need to be replaced (have changed in the new state)
     * and updates these parts of the tree.
     */
    public boolean createCompareTree(Widget root) {
        // create tree corresponding to the new state
        DefaultMutableTreeNode newTop =
                new DefaultMutableTreeNode(root.get(Tags.Desc));
        createNodes(newTop, root);

        toBeReplacedA = new LinkedList<Pair<DefaultMutableTreeNode, Integer>>();
        toBeReplacedWithB = new LinkedList<Pair<DefaultMutableTreeNode, Integer>>();

        boolean identical = identicalTrees((DefaultMutableTreeNode)(tree.getModel().getRoot()),newTop, 0);

        if (!identical) {
            DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
            for (int i = 0; i < toBeReplacedA.size(); i++) {
                model.insertNodeInto(toBeReplacedWithB.get(i).left(), (DefaultMutableTreeNode) toBeReplacedA.get(i).left().getParent(),toBeReplacedA.get(i).right());
                model.removeNodeFromParent(toBeReplacedA.get(i).left());
            }

            return true;
        }
        return false;
    }

    public TreePath stringToTreePath(String stringPath) {
        //Need to use the xPath of the last clicked component to find the representation in the Tree.
        return null;
    }


    /** Given two trees, return true if they are structurally identical, false otherwise and track at which point in
     * the tree the sub trees are no longer identical.
     */
    boolean identicalTrees(DefaultMutableTreeNode a, DefaultMutableTreeNode b, int childNumber) {
        //1. both subtrees have no childeren
        if (a.getChildCount() == 0 && b.getChildCount() == 0)
            return true;

        //2. both non-empty -> compare them, return false if not equal
        if (a.getChildCount() == b.getChildCount()){

            boolean equality = true;
            for (int i = 0; i < a.getChildCount(); i++) {
                Widget aChild = (Widget) ((DefaultMutableTreeNode)a.getChildAt(i)).getUserObject();
                Widget bChild = (Widget) ((DefaultMutableTreeNode)b.getChildAt(i)).getUserObject();
                if (!(aChild.get(AndroidTags.AndroidXpath).equals(bChild.get(AndroidTags.AndroidXpath))) ||
                        !(aChild.get(Tags.Title).equals(bChild.get(Tags.Title))) ||
                        !(aChild.get(AndroidTags.AndroidBounds).equals(bChild.get(AndroidTags.AndroidBounds))) ||
                        !(aChild.get(AndroidTags.AndroidChecked).equals(bChild.get(AndroidTags.AndroidChecked))) ||
                        !(aChild.get(AndroidTags.AndroidSelected).equals(bChild.get(AndroidTags.AndroidSelected)))) {

                    toBeReplacedA.add(new Pair<>((DefaultMutableTreeNode)a.getChildAt(i),i));
                    toBeReplacedWithB.add(new Pair<>((DefaultMutableTreeNode)b.getChildAt(i),i));
                    equality = false;
                } else {
                    equality = equality && identicalTrees((DefaultMutableTreeNode)a.getChildAt(i), (DefaultMutableTreeNode)b.getChildAt(i), i);
                    if (!equality) {
                        if (toBeReplacedA.size() == 0) {
                            toBeReplacedA.add(new Pair<>((DefaultMutableTreeNode)a.getChildAt(i),childNumber));
                            toBeReplacedWithB.add(new Pair<>((DefaultMutableTreeNode)b.getChildAt(i),childNumber));
                            break;
                        }
                    }
                }
            }
            return equality;
        }

        // When childcount of trees are not equal return false.
        toBeReplacedA.add(new Pair<>(a,childNumber));
        toBeReplacedWithB.add(new Pair<>(b,childNumber));
        return false;
    }


    /** Displays the additional info when a widget is clicked in the tree. */
    private void displayWidgetInfo(Widget nodeWidget) {
        infoPaneLeft.removeAll();
        infoPaneRight.removeAll();
        if (nodeWidget != null) {
            String classWidget = nodeWidget.get(AndroidTags.AndroidClassName);
            String textWidget = nodeWidget.get(AndroidTags.AndroidText);
            String hintWidget = nodeWidget.get(AndroidTags.AndroidHint);
            String accessibilityIdWidget = nodeWidget.get(AndroidTags.AndroidAccessibilityId);
            boolean clickableWidget = nodeWidget.get(AndroidTags.AndroidClickable);
            int indexWidget = nodeWidget.get(AndroidTags.AndroidNodeIndex);
            String packageWidget = nodeWidget.get(AndroidTags.AndroidPackageName);
            String resourceIdWidget = nodeWidget.get(AndroidTags.AndroidResourceId);
            Rect boundsWidget = nodeWidget.get(AndroidTags.AndroidBounds);
            boolean checkableWidget = nodeWidget.get(AndroidTags.AndroidCheckable);
            boolean checkedWidget = nodeWidget.get(AndroidTags.AndroidChecked);
            boolean enabledWidget = nodeWidget.get(AndroidTags.AndroidEnabled);
            boolean focusableWidget = nodeWidget.get(AndroidTags.AndroidFocusable);
            boolean focusedWidget = nodeWidget.get(AndroidTags.AndroidFocused);
            boolean longClickableWidget = nodeWidget.get(AndroidTags.AndroidLongClickable);
            boolean scrollableWidget = nodeWidget.get(AndroidTags.AndroidScrollable);
            boolean selectedWidget = nodeWidget.get(AndroidTags.AndroidSelected);
            String xpathWidget = nodeWidget.get(AndroidTags.AndroidXpath);
            String activityWidget = nodeWidget.get(AndroidTags.AndroidActivity);

            //Setting the Jlabels in the gridlayout.
            int fontSize = 12;
            infoPaneLeft.add(new JLabel("Class: ")).setFont(new Font("SansSerif", Font.BOLD, fontSize));
            if (classWidget.equals("")) {
                infoPaneRight.add(new JLabel(" ")).setFont(new Font("SansSerif", Font.PLAIN, fontSize));
            } else {
                infoPaneRight.add(new JLabel(classWidget)).setFont(new Font("SansSerif", Font.PLAIN, fontSize));
            }

            infoPaneLeft.add(new JLabel("Text content: ")).setFont(new Font("SansSerif", Font.BOLD, fontSize));
            if (textWidget.equals("")) {
                infoPaneRight.add(new JLabel(" ")).setFont(new Font("SansSerif", Font.PLAIN, fontSize));
            } else {
                infoPaneRight.add(new JLabel(textWidget)).setFont(new Font("SansSerif", Font.PLAIN, fontSize));
            }

            infoPaneLeft.add(new JLabel("Hint content: ")).setFont(new Font("SansSerif", Font.BOLD, fontSize));
            if (hintWidget.equals("")) {
            	infoPaneRight.add(new JLabel(" ")).setFont(new Font("SansSerif", Font.PLAIN, fontSize));
            } else {
            	infoPaneRight.add(new JLabel(hintWidget)).setFont(new Font("SansSerif", Font.PLAIN, fontSize));
            }

            infoPaneLeft.add(new JLabel("Access ID: ")).setFont(new Font("SansSerif", Font.BOLD, fontSize));
            if (accessibilityIdWidget.equals("")) {
                infoPaneRight.add(new JLabel(" ")).setFont(new Font("SansSerif", Font.PLAIN, fontSize));
            } else {
                infoPaneRight.add(new JLabel(accessibilityIdWidget)).setFont(new Font("SansSerif", Font.PLAIN, fontSize));
            }


            //TODO: THE XPATH AT STARTUP DOESNT FIT IN THE SCREEN. HOWEVER IF ONE UPDATE IN THE EMULATOR HAS OCCURED
            // SCROLLING IS ADDED. FIGURE OUT WHY SCROLLING IS NOT ENABLED FROM THE START.
            infoPaneLeft.add(new JLabel("XPath: ")).setFont(new Font("SansSerif", Font.BOLD, fontSize));
            if (xpathWidget.equals("")) {
                infoPaneRight.add(new JLabel(" ")).setFont(new Font("SansSerif", Font.PLAIN, fontSize));
            } else {
                infoPaneRight.add(new JLabel(String.format("<html><body style=\"text-align: justify;  text-justify: inter-word;\">%s</body></html>",
                        xpathWidget))).setFont(new Font("SansSerif", Font.PLAIN, fontSize));
            }

            infoPaneLeft.add(new JLabel("Package: ")).setFont(new Font("SansSerif", Font.BOLD, fontSize));
            if (packageWidget.equals("")) {
                infoPaneRight.add(new JLabel(" ")).setFont(new Font("SansSerif", Font.PLAIN, fontSize));
            } else {
                infoPaneRight.add(new JLabel(packageWidget)).setFont(new Font("SansSerif", Font.PLAIN, fontSize));
            }

            infoPaneLeft.add(new JLabel("Resource ID: ")).setFont(new Font("SansSerif", Font.BOLD, fontSize));
            if (resourceIdWidget.equals("")) {
                infoPaneRight.add(new JLabel(" ")).setFont(new Font("SansSerif", Font.PLAIN, fontSize));
            } else {
                infoPaneRight.add(new JLabel(resourceIdWidget)).setFont(new Font("SansSerif", Font.PLAIN, fontSize));
            }



            infoPaneLeft.add(new JLabel("Clickable: ")).setFont(new Font("SansSerif", Font.BOLD, fontSize));
            infoPaneRight.add(new JLabel(String.valueOf(clickableWidget))).setFont(new Font("SansSerif", Font.PLAIN, fontSize));

            infoPaneLeft.add(new JLabel("Index: ")).setFont(new Font("SansSerif", Font.BOLD, fontSize));
            infoPaneRight.add(new JLabel(String.valueOf(indexWidget))).setFont(new Font("SansSerif", Font.PLAIN, fontSize));


            infoPaneLeft.add(new JLabel("Bounds: ")).setFont(new Font("SansSerif", Font.BOLD, fontSize));
            infoPaneRight.add(new JLabel(String.valueOf(boundsWidget))).setFont(new Font("SansSerif", Font.PLAIN, fontSize));

            infoPaneLeft.add(new JLabel("Checkable: ")).setFont(new Font("SansSerif", Font.BOLD, fontSize));
            infoPaneRight.add(new JLabel(String.valueOf(checkableWidget))).setFont(new Font("SansSerif", Font.PLAIN, fontSize));

            infoPaneLeft.add(new JLabel("Checked: ")).setFont(new Font("SansSerif", Font.BOLD, fontSize));
            infoPaneRight.add(new JLabel(String.valueOf(checkedWidget))).setFont(new Font("SansSerif", Font.PLAIN, fontSize));

            infoPaneLeft.add(new JLabel("Enabled: ")).setFont(new Font("SansSerif", Font.BOLD, fontSize));
            infoPaneRight.add(new JLabel(String.valueOf(enabledWidget))).setFont(new Font("SansSerif", Font.PLAIN, fontSize));

            infoPaneLeft.add(new JLabel("Focusable: ")).setFont(new Font("SansSerif", Font.BOLD, fontSize));
            infoPaneRight.add(new JLabel(String.valueOf(focusableWidget))).setFont(new Font("SansSerif", Font.PLAIN, fontSize));

            infoPaneLeft.add(new JLabel("Focussed: ")).setFont(new Font("SansSerif", Font.BOLD, fontSize));
            infoPaneRight.add(new JLabel(String.valueOf(focusedWidget))).setFont(new Font("SansSerif", Font.PLAIN, fontSize));

            infoPaneLeft.add(new JLabel("Long clickable: ")).setFont(new Font("SansSerif", Font.BOLD, fontSize));
            infoPaneRight.add(new JLabel(String.valueOf(longClickableWidget))).setFont(new Font("SansSerif", Font.PLAIN, fontSize));

            infoPaneLeft.add(new JLabel("Scrollable: ")).setFont(new Font("SansSerif", Font.BOLD, fontSize));
            infoPaneRight.add(new JLabel(String.valueOf(scrollableWidget))).setFont(new Font("SansSerif", Font.PLAIN, fontSize));

            infoPaneLeft.add(new JLabel("Selected: ")).setFont(new Font("SansSerif", Font.BOLD, fontSize));
            infoPaneRight.add(new JLabel(String.valueOf(selectedWidget))).setFont(new Font("SansSerif", Font.PLAIN, fontSize));

            infoPaneLeft.add(new JLabel("Current Activity: ")).setFont(new Font("SansSerif", Font.BOLD, fontSize));
            infoPaneRight.add(new JLabel(String.valueOf(activityWidget))).setFont(new Font("SansSerif", Font.PLAIN, fontSize));


        } else { //null node
            infoPaneLeft.add(new JLabel("nodeinfo:"));
            infoPaneRight.add(new JLabel("NULL"));
        }

        infoPaneLeft.revalidate();
        infoPaneLeft.repaint();
        infoPaneRight.revalidate();
        infoPaneRight.repaint();
    }

    /** Creates the tree of android widgets */
    private void createNodes(DefaultMutableTreeNode top, Widget widget) {
        DefaultMutableTreeNode subTree = null;

        for (int i = 0; i < widget.childCount(); i++) {
            Widget childWidget = widget.child(i);
            subTree = new DefaultMutableTreeNode(childWidget);
            top.add(subTree);
            createNodes(subTree, childWidget);
        }

    }

    /** Method which makes sure that when a widget selected in the overlay also becomes selected in the tree */
    public void triggerTreeClick(DefaultMutableTreeNode node){
        this.triggeredByOverlayClick = true;

        // Sets the selected node path into the variable which tracks the last selected node
        Widget nodeWidget = (Widget)node.getUserObject();
        selectedNodePath = nodeWidget.get(AndroidTags.AndroidXpath);
        System.out.println("Selected components xpath: " + selectedNodePath);

        tree.setSelectionPath(new TreePath(node.getPath()));
        tree.scrollPathToVisible(new TreePath(node.getPath()));
    }
}
