package nl.ou.testar.ReinforcementLearning.RewardFunctions;

import nl.ou.testar.ReinforcementLearning.Utils.TreedistUtil;
import org.apache.commons.collections.map.MultiKeyMap;
import org.apache.commons.lang.math.NumberUtils;
import org.fruit.alayer.Widget;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

import static nl.ou.testar.ReinforcementLearning.Utils.TreedistUtil.*;

/**
 * Helper class for the {@link WidgetTreeZhangShashaBasedRewardFunction}
 */
public class TreeDistHelper {

    private final static int DELETE = 1;
    private final static int INSERT = 1;
    private final static int RELABLE = 1;
    private final static int MATCH = 0;

    public void treeDist(final Widget keyRoot1, final Widget keyRoot2, final MultiKeyMap forestDist, final MultiKeyMap treeDist) {
        forestDist.put(null, null, 0);

        final Deque<Widget> keyRootPathTree1 = getPostOrder(keyRoot1, new ArrayDeque<>());
        final Deque<Widget> keyRootPathTree2 = getPostOrder(keyRoot2, new ArrayDeque<>());

        for (final Widget node : keyRootPathTree1) {
            final Widget earlierNode = getEarlierNode(node, keyRootPathTree1);
            forestDist.put(node, null, getDist(forestDist, earlierNode, null) + DELETE);
        }

        for (final Widget node : keyRootPathTree2) {
            final Widget earlierNode = getEarlierNode(node, keyRootPathTree2);
            forestDist.put(null, node, getDist(forestDist, null, earlierNode) + INSERT);
        }

        final Map<Widget, Widget> leftMostWidgetMapNodeTree1 = new HashMap();
        for (final Widget nodeTree1: keyRootPathTree1) {
            leftMostWidgetMapNodeTree1.put(nodeTree1, getMostLeftWidget(nodeTree1));
        }

        final Map<Widget, Widget> leftMostWidgetMapNodeTree2 = new HashMap();
        for (final Widget nodeTree2: keyRootPathTree2) {
            leftMostWidgetMapNodeTree2.put(nodeTree2, getMostLeftWidget(nodeTree2));
        }

        for (final Widget nodeTree1: keyRootPathTree1) {
            for (final Widget nodeTree2: keyRootPathTree2) {
                final Widget earlierNode1 = getEarlierNode(nodeTree1, keyRootPathTree1);
                final Widget earlierNode2 = getEarlierNode(nodeTree2, keyRootPathTree2);

                if (TreedistUtil.equals(leftMostWidgetMapNodeTree1.get(nodeTree1), keyRootPathTree1.getFirst())
                        && TreedistUtil.equals(leftMostWidgetMapNodeTree2.get(nodeTree2), keyRootPathTree2.getFirst())) {

                    final int i = getDist(forestDist, earlierNode1, nodeTree2) + DELETE;
                    final int j = getDist(forestDist, nodeTree1, earlierNode2) + INSERT;
                    final boolean nodesAreEqual = TreedistUtil.equals(nodeTree1, nodeTree2);
                    final int k = getDist(forestDist, earlierNode1, earlierNode2) + (nodesAreEqual ? MATCH : RELABLE);

                    final int min = NumberUtils.min(i, j, k);
                    forestDist.put(nodeTree1, nodeTree2, min);
                    treeDist.put(nodeTree1, nodeTree2, min);
                } else {
                    final int i = getDist(forestDist, earlierNode1, nodeTree2) + DELETE;
                    final int j = getDist(forestDist, nodeTree1, earlierNode2) + INSERT;
                    final Widget earlyLeftMost1 = getEarlierNode(getMostLeftWidget(leftMostWidgetMapNodeTree1.get(nodeTree1)), keyRootPathTree1);
                    final Widget earlyLeftMost2 = getEarlierNode(leftMostWidgetMapNodeTree2.get(nodeTree2), keyRootPathTree2);
                    final int k = getDist(forestDist, earlyLeftMost1, earlyLeftMost2) + getDist(treeDist,nodeTree1, nodeTree2);

                    forestDist.put(nodeTree1, nodeTree2, NumberUtils.min(i,j,k));
                }
            }
        }
    }
}
