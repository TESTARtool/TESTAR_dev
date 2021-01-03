package nl.ou.testar.ReinforcementLearning.RewardFunctions;

import nl.ou.testar.ReinforcementLearning.Utils.WidgetUtil;
import org.apache.commons.collections.map.MultiKeyMap;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fruit.alayer.Roles;
import org.fruit.alayer.Tags;
import org.fruit.alayer.Widget;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class TreeDistHelper {

    private static final Logger logger = LogManager.getLogger(TreeDistHelper.class);

    private final static int DELETE = 1;
    private final static int INSERT = 1;
    private final static int RELABLE = 1;

    // TODO forestDist as parameter or variable
    void treeDist(final Widget keyRoot1, final Widget keyRoot2, final MultiKeyMap forestDist, final MultiKeyMap treeDist) {
//        final MultiKeyMap forestDist = new MultiKeyMap();
        forestDist.put(null, null, 0);

        final Deque<Widget> keyRootPathTree1 = getLeftMostArray(keyRoot1);
        final Deque<Widget> keyRootPathTree2 = getLeftMostArray(keyRoot2);

        for (final Widget node : keyRootPathTree1) {
            final Widget earlierNode = getEarlierNode(node, keyRootPathTree1);
            forestDist.put(node, null, getForestDist(forestDist, earlierNode, null) + DELETE);
        }

        for (final Widget node : keyRootPathTree2) {
            final Widget earlierNode = getEarlierNode(node, keyRootPathTree2);
            forestDist.put(null, node, getForestDist(forestDist, null, earlierNode) + INSERT);
        }

        for (final Widget nodeTree1: keyRootPathTree1) {
            for (final Widget nodeTree2: keyRootPathTree2) {
                // This equals is comparing AbstractIDCustom widget property
                // See windows -> org.fruit.alayer.windows -> UIAWidget.java -> equals
                if (WidgetUtil.equals(getLeftMostArray(nodeTree1).getFirst(), keyRootPathTree1.getFirst())
                        && WidgetUtil.equals(getLeftMostArray(nodeTree2).getFirst(), keyRootPathTree2.getFirst())) {

                    /**
                     * Debug: Widget getAbstractRepresentation()
                     * This is just a debugging to check that this method works
                     */
                    if(nodeTree1 != null) {
                    	System.out.println("nodeTree1 getAbstractRepresentation()");
                    	System.out.println(nodeTree1.getAbstractRepresentation());
                    }
                    if(nodeTree2 != null) {
                    	System.out.println("nodeTree2 getAbstractRepresentation()");
                    	System.out.println(nodeTree2.getAbstractRepresentation());
                    }


                    final Widget earlierNode1 = getEarlierNode(nodeTree1, keyRootPathTree1);
                    final Widget earlierNode2 = getEarlierNode(nodeTree2, keyRootPathTree2);

                    // TODO: is it always nodeTree1 and nodeTree2?. Maybe it is:
                    // earlyLeftMost1:  int i = getForestDist(forestDist, earlierNode1, nodeTree2) + DELETE
                    // second: int j = getForestDist(forestDist, nodeTree1, earlierNode2) + INSERT;
                    // and not sure if the difference with with early nodes or with the nodes, like:
                    // final boolean nodesAreEqual = areNodesEqual(nodeTree1, nodeTree2);
                    final int i = getForestDist(forestDist, earlierNode1, nodeTree2) + DELETE;
                    final int j = getForestDist(forestDist, nodeTree1, earlierNode2) + INSERT;
                    final boolean nodesAreEqual = areNodesEqual(earlierNode1, earlierNode2);
                    final int k = getForestDist(forestDist, earlierNode1, earlierNode2) + (nodesAreEqual ? 0 : RELABLE);

                    final int min = NumberUtils.min(i, j, k);
                    forestDist.put(nodeTree1, nodeTree2, min);
                    treeDist.put(nodeTree1, nodeTree2, min);
                } else {
                    final Widget earlierNode1 = getEarlierNode(nodeTree1, keyRootPathTree1);
                    final Widget earlierNode2 = getEarlierNode(nodeTree2, keyRootPathTree1);

                    // TODO: same as before... is it always nodeTree1 and nodeTree2?. Maybe it is:
                    // earlyLeftMost1 getForestDist(forestDist, earlierNode1, nodeTree2) + DELETE
                    // second getForestDist(forestDist, nodeTree1, earlierNode2) + INSERT;
                    final int i = getForestDist(forestDist, earlierNode1, nodeTree2) + DELETE;
                    final int j = getForestDist(forestDist, nodeTree1, earlierNode2) + INSERT;

                    // And then we might need the early node of the left most child... does this make sense?
                    // As in the formula from the algorithm: forestdist(T_1[l(i)..l(i_1) - 1 ], T_2[l(j)..l(j_1) - 1]) + treedist(i_1,j_1)
                    // T_1[l(i)..l(i_1) - 1 ] the forest from the left most descendant of i to the node "l(i_1) - 1"
                    // l(i_1) - 1 : I understand from this that l(i_1) is the left most descendant of nodeTree1 and -1 implies the early node
//                     I would assume is something like this, but I have doubts:
                     final Widget earlyLeftMost1 = getEarlierNode(getLeftMostArray(nodeTree1).getFirst(), keyRootPathTree1);
                     final Widget earlyLeftMost2 = getEarlierNode(getLeftMostArray(nodeTree2).getFirst(), keyRootPathTree2);
                     final int k = getForestDist(forestDist, earlyLeftMost1, earlyLeftMost2) + getTreeDist(treeDist,nodeTree1, nodeTree2);
//                    final int k = getForestDist(forestDist, keyRootPathTree1.getFirst(), keyRootPathTree2.getFirst()) + getTreeDist(treeDist,nodeTree1, nodeTree2);

                    forestDist.put(nodeTree1, nodeTree2, NumberUtils.min(i,j,k));
                }
            }
        }

    }

    private boolean areNodesEqual(final Widget node1, final Widget node2) {
//        if (node1 == null && node2 == null) {
//            return true;
//        }

        if (node1 == null || node2 == null) {
            return false;
        }
        
        System.out.println("!!!! Cheking areNodesEqual (Not Null) !!!!");
        
        //TODO: Check what is returning toString()
        logger.debug("Comparing nodes: '{}' and '{}'", node1.toString(), node2.toString());

        // TODO: Note that getAbstractRepresentation of two "different" menu items will be the same if Widget Title is not included in the abstraction level
        // TODO: Basically users will need to define a GOOD SUT Abstraction before running this
        return WidgetUtil.equals(node1, node2);
    }

    private Widget getEarlierNode(final Widget node, final Deque<Widget> deque) {
        final List<Widget> dequeList = new ArrayList<>(deque);
        
        // TODO from Mark: test if this works
        final int position = dequeList.indexOf(node);
        
        System.out.println("getEarlierNode -> " + node.get(Tags.Role, Roles.Widget));
        System.out.println("getEarlierNode left position -> " + String.valueOf(position - 1));

        if (position - 1 < 0) {
            return null;
        }

        return dequeList.get(position - 1);
    }

    /** Creates a collection of the path from the most left item to the root element
     * The first element is the most left
     */
    private Deque<Widget> getLeftMostArray(final Widget widget) {
        final Deque<Widget> result = new ArrayDeque<>();
        addLeftNode(widget, result);
        result.add(widget);
        return result;
    }

    private void addLeftNode(final Widget widget, final Deque<Widget> result) {
        final List<Widget> sortedChildNodes = LRKeyrootsHelper.getSortedChildList(widget);
        if (sortedChildNodes.isEmpty()) {
            return;
        }

        result.addLast(sortedChildNodes.get(0));
        // call recursively
        addLeftNode(sortedChildNodes.get(0), result);
    }

    private Integer getForestDist(final MultiKeyMap forestDist, final Widget node1, final Widget node2) {
        if (node1 == null && node2 == null) {
            // TODO: Why? I thought getForestDist(null, null) = 1
            return 0;
        }

        if (!forestDist.containsKey(node1, node2)) {
            // TODO: When is this possible?
            return 0;
        }

        return (Integer) forestDist.get(node1, node2);
    }

    private Integer getTreeDist(final MultiKeyMap treeDist, final Widget node1, final Widget node2) {
        if (node1 == null && node2 == null) {
            return 0;
        }

        if (!treeDist.containsKey(node1, node2)) {
            // TODO: When is this possible?
            return 0;
        }

        return (Integer) treeDist.get(node1, node2);
    }
}
