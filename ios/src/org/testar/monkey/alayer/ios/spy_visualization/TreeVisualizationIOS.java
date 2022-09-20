package org.testar.monkey.alayer.ios.spy_visualization;

import org.testar.monkey.Pair;
import org.testar.monkey.alayer.Rect;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.Widget;
import org.testar.monkey.alayer.ios.enums.IOSTags;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.util.LinkedList;

public class TreeVisualizationIOS extends JPanel implements TreeSelectionListener {
    private final JPanel infoPaneLeft = new JPanel();
    private final JPanel infoPaneRight = new JPanel();
    public JTree tree;
    private DefaultMutableTreeNode tempTreeNode;
    private final MobileVisualizationIOS mobileVisualizationIOS;
    private boolean triggeredByOverlayClick = false;
    private final JScrollPane treeView;

    private String selectedNodePath;

    private LinkedList<Pair<DefaultMutableTreeNode, Integer>> toBeReplacedA = null;
    private LinkedList<Pair<DefaultMutableTreeNode, Integer>> toBeReplacedWithB = null;


    /** Initializer for the tree component of the spy mode visualization (right hand side screen). */
    public TreeVisualizationIOS(MobileVisualizationIOS mobileVisualizationIOS, Widget widget) {
        super(new GridLayout(1,0));

        this.mobileVisualizationIOS = mobileVisualizationIOS;

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

        //Create the viewing pane for obtaining more detailed information on a widget.
        JSplitPane infoSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        infoSplitPane.setDividerSize(0);
        infoSplitPane.setDividerLocation(150);

        infoPaneLeft.setLayout(new BoxLayout(infoPaneLeft, BoxLayout.Y_AXIS));
        infoPaneRight.setLayout(new BoxLayout(infoPaneRight, BoxLayout.Y_AXIS));

        infoSplitPane.setLeftComponent(infoPaneLeft);
        infoSplitPane.setRightComponent(infoPaneRight);
        JScrollPane infoView = new JScrollPane(infoSplitPane);
        infoView.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        infoView.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        //Add the scroll panes to a split pane.
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setTopComponent(treeView);
        splitPane.setBottomComponent(infoView);

        Dimension minimumSize = new Dimension(400, 600);

        infoView.setMinimumSize(minimumSize);
        treeView.setMinimumSize(minimumSize);
        splitPane.setDividerLocation(550);
        splitPane.setDividerSize(0);
        splitPane.setPreferredSize(new Dimension(500, 850));

        //Add the split pane to this panel.
        add(splitPane);
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
            mobileVisualizationIOS.treeToOverlay(node);
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
                if (!(aChild.get(IOSTags.iosXpath).equals(bChild.get(IOSTags.iosXpath))) ||
                        !(aChild.get(Tags.Title).equals(bChild.get(Tags.Title))) ||
                        !(aChild.get(IOSTags.iosBounds).equals(bChild.get(IOSTags.iosBounds)))) {

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
            String classWidget = nodeWidget.get(IOSTags.iosClassName);
            String textWidget = nodeWidget.get(IOSTags.iosText);
            String accessibilityIdWidget = nodeWidget.get(IOSTags.iosAccessibilityId);
            int indexWidget = nodeWidget.get(IOSTags.iosNodeIndex);
            Rect boundsWidget = nodeWidget.get(IOSTags.iosBounds);
            boolean enabledWidget = nodeWidget.get(IOSTags.iosEnabled);
            String xpathWidget = nodeWidget.get(IOSTags.iosXpath);
            String labelWidget = nodeWidget.get(IOSTags.iosLabel);
            Boolean visibilityWidget = nodeWidget.get(IOSTags.iosVisible);
            int xWidget = nodeWidget.get(IOSTags.iosX);
            int yWidget = nodeWidget.get(IOSTags.iosY);
            int widthWidget = nodeWidget.get(IOSTags.iosWidth);
            int heightWidget = nodeWidget.get(IOSTags.iosHeight);

            //Setting the Jlabels in the gridlayout.
            int fontSize = 12;
            infoPaneLeft.add(new JLabel("Type/Class: ")).setFont(new Font("SansSerif", Font.BOLD, fontSize));
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


            infoPaneLeft.add(new JLabel("Label: ")).setFont(new Font("SansSerif", Font.BOLD, fontSize));
            if (labelWidget.equals("")) {
                infoPaneRight.add(new JLabel(" ")).setFont(new Font("SansSerif", Font.PLAIN, fontSize));
            } else {
                infoPaneRight.add(new JLabel(labelWidget)).setFont(new Font("SansSerif", Font.PLAIN, fontSize));
            }

            infoPaneLeft.add(new JLabel("Visibility: ")).setFont(new Font("SansSerif", Font.BOLD, fontSize));
            if (visibilityWidget.equals("")) {
                infoPaneRight.add(new JLabel(" ")).setFont(new Font("SansSerif", Font.PLAIN, fontSize));
            } else {
                infoPaneRight.add(new JLabel(String.valueOf(visibilityWidget))).setFont(new Font("SansSerif", Font.PLAIN, fontSize));
            }

            infoPaneLeft.add(new JLabel("X: ")).setFont(new Font("SansSerif", Font.BOLD, fontSize));
            if (visibilityWidget.equals("")) {
                infoPaneRight.add(new JLabel(" ")).setFont(new Font("SansSerif", Font.PLAIN, fontSize));
            } else {
                infoPaneRight.add(new JLabel(String.valueOf(xWidget))).setFont(new Font("SansSerif", Font.PLAIN, fontSize));
            }

            infoPaneLeft.add(new JLabel("Y: ")).setFont(new Font("SansSerif", Font.BOLD, fontSize));
            if (visibilityWidget.equals("")) {
                infoPaneRight.add(new JLabel(" ")).setFont(new Font("SansSerif", Font.PLAIN, fontSize));
            } else {
                infoPaneRight.add(new JLabel(String.valueOf(yWidget))).setFont(new Font("SansSerif", Font.PLAIN, fontSize));
            }

            infoPaneLeft.add(new JLabel("Width: ")).setFont(new Font("SansSerif", Font.BOLD, fontSize));
            if (visibilityWidget.equals("")) {
                infoPaneRight.add(new JLabel(" ")).setFont(new Font("SansSerif", Font.PLAIN, fontSize));
            } else {
                infoPaneRight.add(new JLabel(String.valueOf(widthWidget))).setFont(new Font("SansSerif", Font.PLAIN, fontSize));
            }

            infoPaneLeft.add(new JLabel("Height: ")).setFont(new Font("SansSerif", Font.BOLD, fontSize));
            if (visibilityWidget.equals("")) {
                infoPaneRight.add(new JLabel(" ")).setFont(new Font("SansSerif", Font.PLAIN, fontSize));
            } else {
                infoPaneRight.add(new JLabel(String.valueOf(heightWidget))).setFont(new Font("SansSerif", Font.PLAIN, fontSize));
            }

            infoPaneLeft.add(new JLabel("Bounds: ")).setFont(new Font("SansSerif", Font.BOLD, fontSize));
            infoPaneRight.add(new JLabel(String.valueOf(boundsWidget))).setFont(new Font("SansSerif", Font.PLAIN, fontSize));

            infoPaneLeft.add(new JLabel("Index: ")).setFont(new Font("SansSerif", Font.BOLD, fontSize));
            infoPaneRight.add(new JLabel(String.valueOf(indexWidget))).setFont(new Font("SansSerif", Font.PLAIN, fontSize));

            infoPaneLeft.add(new JLabel("Enabled: ")).setFont(new Font("SansSerif", Font.BOLD, fontSize));
            infoPaneRight.add(new JLabel(String.valueOf(enabledWidget))).setFont(new Font("SansSerif", Font.PLAIN, fontSize));


        } else { //null node
            infoPaneLeft.add(new JLabel("nodeinfo:"));
            infoPaneRight.add(new JLabel("NULL"));
        }

        infoPaneLeft.revalidate();
        infoPaneLeft.repaint();
        infoPaneRight.revalidate();
        infoPaneRight.repaint();
    }

    /** Creates the tree of ios widgets */
    private void createNodes(DefaultMutableTreeNode top, Widget widget) {
        DefaultMutableTreeNode subTree = null;

        for (int i = 0; i < widget.childCount(); i++) {
            Widget childWidget = widget.child(i);

            // iOS specific check to not add the widgets which are not visible on the screenshot.
            double heightLocation = childWidget.get(IOSTags.iosBounds).y();
            if (heightLocation < (880*1.0) && heightLocation >= 0.0) {
                subTree = new DefaultMutableTreeNode(childWidget);
                top.add(subTree);
                createNodes(subTree, childWidget);
            }
        }

    }

    /** Method which makes sure that when a widget selected in the overlay also becomes selected in the tree */
    public void triggerTreeClick(DefaultMutableTreeNode node){
        this.triggeredByOverlayClick = true;

        // Sets the selected node path into the variable which tracks the last selected node
        Widget nodeWidget = (Widget)node.getUserObject();
        selectedNodePath = nodeWidget.get(IOSTags.iosXpath);
        // System.out.println("Selected components xpath: " + selectedNodePath);

        tree.setSelectionPath(new TreePath(node.getPath()));
        tree.scrollPathToVisible(new TreePath(node.getPath()));
    }
}
