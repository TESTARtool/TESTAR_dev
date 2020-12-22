package nl.ou.testar.ReinforcementLearning.RewardFunctions;

import org.apache.commons.collections.map.MultiKeyMap;
import org.apache.commons.lang.StringUtils;
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

    void treeDist(final Widget keyRoot1, final Widget keyRoot2, final MultiKeyMap forestDist, final MultiKeyMap treeDist) {
        forestDist.put(null, null, 0);

        final Deque<Widget> keyRootPathTree1 = getLeftMostArray(keyRoot1);
        final Deque<Widget> keyRootPathTree2 = getLeftMostArray(keyRoot2);

        for (final Widget node : keyRootPathTree1) {
            // TODO: Should earlierNode of node be used instead of node?
            //   final Widget earlierNode = getEarlierNode(node, keyRootPathTree1);
            //   forestDist.put(node, null, getForestDist(forestDist, earlierNode, null) + DELETE);
            forestDist.put(node, null, getForestDist(forestDist, node, null) + DELETE);
        }

        for (final Widget node : keyRootPathTree2) {
            // TODO: Should earlierNode of node be used instead of node?
            //   final Widget earlierNode = getEarlierNode(node, keyRootPathTree2);
            //   forestDist.put(node, null, getForestDist(forestDist, earlierNode, null) + DELETE);
            forestDist.put(null, node, getForestDist(forestDist, null, node) + INSERT);
        }

        for (final Widget nodeTree1 : keyRootPathTree1) {
            for (final Widget nodeTree2: keyRootPathTree2) {
            	
                System.out.println("**** Node Tree1 Widget Title : " + nodeTree1.get(Tags.Title, "NOtitleOne"));
                System.out.println("**** Node Tree1 Widget Role : " + nodeTree1.get(Tags.Role, Roles.Widget));
                System.out.println("**** Node Tree2 Widget Title : " + nodeTree2.get(Tags.Title, "NOtitleTwo"));
                System.out.println("**** Node Tree2 Widget Role : " + nodeTree2.get(Tags.Role, Roles.Widget));
                
                System.out.println("--------------------------------------------------------------------------------------------------");
                System.out.println("getLeftMostArray(nodeTree1).getFirst() TITLE: " + getLeftMostArray(nodeTree1).getFirst().get(Tags.Title, "NO TITLE"));
                System.out.println("getLeftMostArray(nodeTree1).getFirst() ROLE: " + getLeftMostArray(nodeTree1).getFirst().get(Tags.Role, Roles.Widget));
                System.out.println("getLeftMostArray(nodeTree1).getFirst() AbstractIDCustom: " + getLeftMostArray(nodeTree1).getFirst().get(Tags.AbstractIDCustom, "NO AbstractIDCustom"));
                System.out.println("keyRootPathTree1.getFirst() TITLE: " + keyRootPathTree1.getFirst().get(Tags.Title, "NO TITLE"));
                System.out.println("keyRootPathTree1.getFirst() ROLE: " + keyRootPathTree1.getFirst().get(Tags.Role, Roles.Widget));
                System.out.println("keyRootPathTree1.getFirst() AbstractIDCustom: " + keyRootPathTree1.getFirst().get(Tags.AbstractIDCustom, "NO AbstractIDCustom"));
                
                System.out.println("--------------------------------------------------------------------------------------------------");
                
                System.out.println("getLeftMostArray(nodeTree2).getFirst() TITLE: " + getLeftMostArray(nodeTree2).getFirst().get(Tags.Title, "NO TITLE"));
                System.out.println("getLeftMostArray(nodeTree2).getFirst() ROLE: " + getLeftMostArray(nodeTree2).getFirst().get(Tags.Role, Roles.Widget));
                System.out.println("getLeftMostArray(nodeTree2).getFirst() AbstractIDCustom: " + getLeftMostArray(nodeTree2).getFirst().get(Tags.AbstractIDCustom, "NO AbstractIDCustom"));	
                System.out.println("keyRootPathTree2.getFirst() TITLE: " + keyRootPathTree2.getFirst().get(Tags.Title, "NO TITLE"));
                System.out.println("keyRootPathTree2.getFirst() ROLE: " + keyRootPathTree2.getFirst().get(Tags.Role, Roles.Widget));
                System.out.println("keyRootPathTree2.getFirst() AbstractIDCustom: " + keyRootPathTree2.getFirst().get(Tags.AbstractIDCustom, "NO AbstractIDCustom"));
                
                // This equals is comparing AbstractIDCustom widget property
                // See windows -> org.fruit.alayer.windows -> UIAWidget.java -> equals
                if (getLeftMostArray(nodeTree1).getFirst().equals(keyRootPathTree1.getFirst())
                        && getLeftMostArray(nodeTree2).getFirst().equals(keyRootPathTree2.getFirst())) {
                	
                    System.out.println(" INSIDEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE :D");
                    
                    /**
                     * Debug: windows -> org.fruit.alayer.windows -> UIAWidget.java -> getAbstractRepresentation()
                     * This is just a debugging to check that this method works
                     * FINAL implementation should be done with getEarlierNodes
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
                    // TODO: Change to keyRootPathTree2, not keyRootPathTree1
                    final Widget earlierNode2 = getEarlierNode(nodeTree2, keyRootPathTree1);

                    // TODO: is it always nodeTree1 and nodeTree2?. Maybe it is:
                    // first:  int i = getForestDist(forestDist, earlierNode1, nodeTree2) + DELETE
                    // second: int j = getForestDist(forestDist, nodeTree1, earlierNode2) + INSERT;
                    // and not sure if the difference with with early nodes or with the nodes, like:
                    // final boolean nodesAreEqual = areNodesEqual(nodeTree1, nodeTree2);
                    final int i = getForestDist(forestDist, nodeTree1, nodeTree2) + DELETE;
                    final int j = getForestDist(forestDist, nodeTree1, nodeTree2) + INSERT;
                    final boolean nodesAreEqual = areNodesEqual(earlierNode1, earlierNode2);
                    final int k = getForestDist(forestDist, earlierNode1, earlierNode2) + (nodesAreEqual ? 0 : RELABLE);

                    final int min = NumberUtils.min(i, j, k);
                    forestDist.put(nodeTree1, nodeTree2, min);
                    treeDist.put(nodeTree1, nodeTree2, min);
                } else {
                    final Widget earlierNode1 = getEarlierNode(nodeTree1, keyRootPathTree1);
                    final Widget earlierNode2 = getEarlierNode(nodeTree2, keyRootPathTree1);

                    // TODO: same as before... is it always nodeTree1 and nodeTree2?. Maybe it is:
                    // first getForestDist(forestDist, earlierNode1, nodeTree2) + DELETE
                    // second getForestDist(forestDist, nodeTree1, earlierNode2) + INSERT;
                    final int i = getForestDist(forestDist, nodeTree1, nodeTree2) + DELETE;
                    final int j = getForestDist(forestDist, nodeTree1, nodeTree2) + INSERT;

                    // And then we might need the early node of the left most child... does this make sense?
                    // As in the formula from the algorithm: forestdist(T_1[l(i)..l(i_1) - 1 ], T_2[l(j)..l(j_1) - 1]) + treedist(i_1,j_1)
                    // T_1[l(i)..l(i_1) - 1 ] the forest from the left most descendant of i to the node "l(i_1) - 1"
                    // l(i_1) - 1 : I understand from this that l(i_1) is the left most descendant of nodeTree1 and -1 implies the early node
                    // I would assume is something like this, but I have doubts:
                    // final Widget earlyLeftMost1 = getEarlierNode(getLeftMostArray(nodeTree1).getFirst(), keyRootPathTree1);
                    // final Widget earlyLeftMost2 = getEarlierNode(getLeftMostArray(nodeTree2).getFirst(), keyRootPathTree2);
                    // final int k = getForestDist(forestDist, earlyLeftMost1, earlyLeftMost2) + getTreeDist(treeDist,nodeTree1, nodeTree2);
                    final int k = getForestDist(forestDist, earlierNode1, earlierNode2) + getTreeDist(treeDist,nodeTree1, nodeTree2);

                    forestDist.put(nodeTree1, nodeTree2, NumberUtils.min(i,j,k));
                }
            }
        }

    }

    private boolean areNodesEqual(final Widget node1, final Widget node2) {
        // TODO: What should happen if both are null?
        if (node1 == null || node2 == null) {
            return false;
        }
        
        // FIXME: At the moment this seems not to work
        System.out.println("!!!! areNodesEqual NOT NULL !!!! We are IN");
        
        //logger.debug("Comparing nodes: '{}' and '{}'", node1.toString(), node2.toString());
        
        logger.debug("Comparing AbstractIDCustom properties of nodes: '{}' and '{}'", node1.toString(), node2.toString());

        // TODO: Note that getAbstractRepresentation of two "different" menu items will be the same if Widget Title is not included in the abstraction level
        return StringUtils.equals(node1.getAbstractRepresentation(), node2.getAbstractRepresentation());
    }

    private Widget getEarlierNode(final Widget node, final Deque<Widget> deque) {
        final List<Widget> dequeList = new ArrayList<>(deque);
        
        // TODO: test if this works
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
        // TODO: I am not sure at this point, but I think this path should be a post order traversal, not just all left descendants
        final Deque<Widget> result = new ArrayDeque<>();
        result.add(widget);

        addLeftNode(widget, result);

        return result;
    }

    private void addLeftNode(final Widget widget, final Deque<Widget> result) {
        final List<Widget> sortedChildNodes = LRKeyrootsHelper.getSortedChildList(widget);
        if (sortedChildNodes.isEmpty()) {
            return;
        }

        result.add(sortedChildNodes.get(0));
        // call recursively
        addLeftNode(sortedChildNodes.get(0), result);
    }

    private Integer getForestDist(final MultiKeyMap forestDist, final Widget node1, final Widget node2) {
        if (node1 == null && node2 == null) {
            // Why? I thought getForestDist(null, null) = 1
            return 0;
        }

        if (!forestDist.containsKey(node1, node2)) {
            // When is this possible?
            return 0;
        }

        return (Integer) forestDist.get(node1, node2);
    }

    private Integer getTreeDist(final MultiKeyMap treeDist, final Widget node1, final Widget node2) {
        if (node1 == null && node2 == null) {
            return 0;
        }

        if (!treeDist.containsKey(node1, node2)) {
            // When is this possible?
            return 0;
        }

        return (Integer) treeDist.get(node1, node2);
    }
}
