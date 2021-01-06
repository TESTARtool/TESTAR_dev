package nl.ou.testar.ReinforcementLearning.RewardFunctions;

import nl.ou.testar.ReinforcementLearning.Utils.WidgetUtil;
import org.apache.commons.collections.map.MultiKeyMap;
import org.apache.commons.lang.math.NumberUtils;
import org.fruit.alayer.Tags;
import org.fruit.alayer.Widget;

import java.util.Deque;

import static nl.ou.testar.ReinforcementLearning.Utils.WidgetUtil.*;

public class TreeDistHelper {

    private final static int DELETE = 1;
    private final static int INSERT = 1;
    private final static int RELABLE = 1;
    private final static int MATCH = 0;

    public void treeDist(final Widget keyRoot1, final Widget keyRoot2, final MultiKeyMap forestDist, final MultiKeyMap treeDist) {
        forestDist.put(null, null, 0);

        final Deque<Widget> keyRootPathTree1 = getLeftMostArray(keyRoot1);
        final Deque<Widget> keyRootPathTree2 = getLeftMostArray(keyRoot2);

        for (final Widget node : keyRootPathTree1) {
            final Widget earlierNode = getEarlierNode(node, keyRootPathTree1);
            forestDist.put(node, null, getDist(forestDist, earlierNode, null) + DELETE);
        }

        for (final Widget node : keyRootPathTree2) {
            final Widget earlierNode = getEarlierNode(node, keyRootPathTree2);
            forestDist.put(null, node, getDist(forestDist, null, earlierNode) + INSERT);
        }

        for (final Widget nodeTree1: keyRootPathTree1) {
            for (final Widget nodeTree2: keyRootPathTree2) {
                // This equals is comparing AbstractIDCustom widget property
                // See windows -> org.fruit.alayer.windows -> UIAWidget.java -> equals
                final Widget earlierNode1 = getEarlierNode(nodeTree1, keyRootPathTree1);
                final Widget earlierNode2 = getEarlierNode(nodeTree2, keyRootPathTree2);

                // TODO remove
                System.out.println(getLeftMostArray(nodeTree1).getFirst().get(Tags.Title));
                System.out.println(keyRootPathTree1.getFirst().get(Tags.Title));

                // TODO remove
                System.out.println(getLeftMostArray(nodeTree2).getFirst().get(Tags.Title));
                System.out.println(keyRootPathTree2.getFirst().get(Tags.Title));

                if (WidgetUtil.equals(getLeftMostArray(nodeTree1).getFirst(), keyRootPathTree1.getFirst())
                        && WidgetUtil.equals(getLeftMostArray(nodeTree2).getFirst(), keyRootPathTree2.getFirst())) {

                    final int i = getDist(forestDist, earlierNode1, nodeTree2) + DELETE;
                    final int j = getDist(forestDist, nodeTree1, earlierNode2) + INSERT;
                    final boolean nodesAreEqual = WidgetUtil.equals(nodeTree1, nodeTree2);
                    final int k = getDist(forestDist, earlierNode1, earlierNode2) + (nodesAreEqual ? MATCH : RELABLE);

                    final int min = NumberUtils.min(i, j, k);
                    forestDist.put(nodeTree1, nodeTree2, min);
                    treeDist.put(nodeTree1, nodeTree2, min);
                } else {
                    final int i = getDist(forestDist, earlierNode1, nodeTree2) + DELETE;
                    final int j = getDist(forestDist, nodeTree1, earlierNode2) + INSERT;
                    final Widget earlyLeftMost1 = getEarlierNode(getLeftMostArray(nodeTree1).getFirst(), keyRootPathTree1);
                    final Widget earlyLeftMost2 = getEarlierNode(getLeftMostArray(nodeTree2).getFirst(), keyRootPathTree2);
                    final int k = getDist(forestDist, earlyLeftMost1, earlyLeftMost2) + getDist(treeDist,nodeTree1, nodeTree2);

                    forestDist.put(nodeTree1, nodeTree2, NumberUtils.min(i,j,k));
                }
            }
        }
    }

    private Integer getDist(final MultiKeyMap dist, final Widget node1, final Widget node2) {
        if (dist.get(node1, node2) == null){
            return 0;
        }

        return (Integer) dist.get(node1, node2);
    }
}
