package org.testar.monkey.alayer.android.spy_visualization;

import org.junit.Before;
import org.junit.Test;
import org.testar.monkey.alayer.Widget;
import org.testar.monkey.alayer.Rect;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.android.enums.AndroidTags;
import org.testar.stub.StateStub;
import org.testar.stub.WidgetStub;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import static org.junit.Assert.*;

public class TreeVisualizationAndroidTest {

    private TreeVisualizationAndroid treeVisualization;

    private StateStub createState(String xpath, String title, Rect bounds) {
        StateStub state = new StateStub();
        state.set(AndroidTags.AndroidXpath, xpath);
        state.set(Tags.Title, title);
        state.set(Tags.Desc, title);
        state.set(AndroidTags.AndroidBounds, bounds);
        state.set(AndroidTags.AndroidChecked, false);
        state.set(AndroidTags.AndroidSelected, false);
        return state;
    }

    private WidgetStub createWidget(String xpath, String title, Rect bounds, boolean checked, boolean selected) {
        WidgetStub widget = new WidgetStub();
        widget.set(AndroidTags.AndroidXpath, xpath);
        widget.set(Tags.Title, title);
        widget.set(Tags.Desc, title);
        widget.set(AndroidTags.AndroidBounds, bounds);
        widget.set(AndroidTags.AndroidChecked, checked);
        widget.set(AndroidTags.AndroidSelected, selected);
        return widget;
    }

    private State simpleTreeStateWithCheckbox(boolean checkboxChecked) {
        StateStub root = createState("//root", "Root", Rect.from(1, 1, 1, 1));
        WidgetStub child = createWidget("//root/checkbox[1]", "Checkbox", Rect.from(2, 2, 2, 2), checkboxChecked, false);

        root.addChild(child);
        child.setParent(root);

        return root;
    }

    @Before
    public void setUp() {
        MobileVisualizationAndroid mobileSpy = null;

        State initialRoot = simpleTreeStateWithCheckbox(false);
        treeVisualization = new TreeVisualizationAndroid(mobileSpy, initialRoot);

        assertNotNull("Tree should be initialized in constructor", treeVisualization.tree);
    }

    @Test
    public void compareTreeIsIdentical() {
        State sameRoot = simpleTreeStateWithCheckbox(false);

        boolean update = treeVisualization.compareUpdateTree(sameRoot);

        assertFalse("Not update, identical trees when state is the same", update);
    }

    @Test
    public void compareTreeIsDifferent_nodeChanges() {
        State checkedRoot = simpleTreeStateWithCheckbox(true);

        JTree treeBefore = treeVisualization.tree;
        DefaultTreeModel modelBefore = (DefaultTreeModel) treeBefore.getModel();
        DefaultMutableTreeNode rootBefore = (DefaultMutableTreeNode) modelBefore.getRoot();
        int childCountBefore = rootBefore.getChildCount();

        boolean update = treeVisualization.compareUpdateTree(checkedRoot);

        assertTrue("Checkbox toggle should be reported as non-identical", update);

        // Tree & root reused (no full rebuild) if only state changed:
        JTree treeAfter = treeVisualization.tree;
        DefaultTreeModel modelAfter = (DefaultTreeModel) treeAfter.getModel();
        DefaultMutableTreeNode rootAfter = (DefaultMutableTreeNode) modelAfter.getRoot();

        assertSame("JTree instance should be reused", treeBefore, treeAfter);
        assertSame("Root node should be reused for state-only changes", rootBefore, rootAfter);
        assertEquals("Child count should be unchanged", childCountBefore, rootAfter.getChildCount());

        // Verify that the child widget now reports checked = true
        DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) rootAfter.getChildAt(0);
        Widget childWidget = (Widget) childNode.getUserObject();
        Boolean checked = childWidget.get(AndroidTags.AndroidChecked);

        assertTrue("Checkbox widget should now be checked", checked);
    }

    @Test
    public void compareTreeIsDifferent_widgetTreeChanges() {
        // Create a new root with no children
        StateStub newRoot = createState("//root2", "Root2", Rect.from(5, 5, 5, 5));

        DefaultTreeModel modelBefore = (DefaultTreeModel) treeVisualization.tree.getModel();
        DefaultMutableTreeNode rootBefore = (DefaultMutableTreeNode) modelBefore.getRoot();

        boolean update = treeVisualization.compareUpdateTree(newRoot);

        assertTrue("Structural change should be reported as non-identical", update);

        DefaultTreeModel modelAfter = (DefaultTreeModel) treeVisualization.tree.getModel();
        DefaultMutableTreeNode rootAfter = (DefaultMutableTreeNode) modelAfter.getRoot();

        assertNotSame("Root node should be rebuilt when structure changed", rootBefore, rootAfter);
        assertEquals("New root should have no children as in newRoot state", 0, rootAfter.getChildCount());
    }

}
