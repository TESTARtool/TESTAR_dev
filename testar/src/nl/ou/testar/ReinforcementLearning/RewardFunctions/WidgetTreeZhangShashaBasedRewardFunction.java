package nl.ou.testar.ReinforcementLearning.RewardFunctions;

import nl.ou.testar.StateModel.AbstractAction;
import nl.ou.testar.StateModel.AbstractState;
import nl.ou.testar.StateModel.ConcreteState;
import org.apache.commons.collections.map.MultiKeyMap;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.fruit.alayer.Action;
import org.fruit.alayer.State;
import org.fruit.alayer.Widget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


public class WidgetTreeZhangShashaBasedRewardFunction implements RewardFunction {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final static int DELETE = 1;
    private final static int INSERT = 1;
    private final static int RELABLE = 1;

    private final LRKeyrootsHelper lrKeyrootsHelper;

    State previousState = null;

    final MultiKeyMap forestDist = new MultiKeyMap();
    final MultiKeyMap treeDist = new MultiKeyMap();

    public WidgetTreeZhangShashaBasedRewardFunction(final LRKeyrootsHelper lrKeyrootsHelper) {
        this.lrKeyrootsHelper = lrKeyrootsHelper;
    }

    @Override
    public float getReward(final State state, final ConcreteState currentConcreteState, final AbstractState currentAbstractState, final AbstractAction executedAction, Set<Action> actions) {
        if (state == null) {
            return 0f;
        }

        if (previousState == null) {
            previousState = state;
            return 0f;
        }

        final Deque<Widget> lrKeyroots1 = lrKeyrootsHelper.getLRKeyroots(previousState);
        final Deque<Widget> lrKeyroots2 = lrKeyrootsHelper.getLRKeyroots(state);

        for (final Widget keyRoot1 : lrKeyroots1) {
            for (final Widget keyRoot2 : lrKeyroots2) {
                treeDist(keyRoot1, keyRoot2);
            }
        }

        return treeDist.values().stream()
                .mapToInt(object -> (Integer) object)
                .sum();
    }

    private void treeDist(final Widget keyRoot1, final Widget keyRoot2) {
        forestDist.put(null, null, 0);

        final Deque<Widget> keyRootPathTree1 = getLeftMostArray(keyRoot1);
        final Deque<Widget> keyRootPathTree2 = getLeftMostArray(keyRoot2);

        for (final Widget node : keyRootPathTree1) {
            forestDist.put(node, null, getForestDist(node, null) + DELETE);
        }

        for (final Widget node : keyRootPathTree2) {
            forestDist.put(null, node, getForestDist(null, node) + INSERT);
        }

        for (final Widget nodeTree1 : keyRootPathTree1) {
            for (final Widget nodeTree2: keyRootPathTree2) {
                if (getLeftMostArray(nodeTree1).getFirst().equals(keyRootPathTree1.getFirst())
                        && getLeftMostArray(nodeTree2).getFirst().equals(keyRootPathTree2.getFirst())) {

                    final Widget earlierNode1 = getEarlierNode(nodeTree1, keyRootPathTree1);
                    final int i = getForestDist(nodeTree1, nodeTree2) + DELETE;

                    final Widget earlierNode2 = getEarlierNode(nodeTree2, keyRootPathTree1);
                    final int j = getForestDist(nodeTree1, nodeTree2) + INSERT;

                    final boolean nodesAreEqual = areNodesEqual(earlierNode1, earlierNode2);
                    final int k = getForestDist(nodeTree1, nodeTree2) + (nodesAreEqual ? 0 : RELABLE);

                    forestDist.put(nodeTree1, nodeTree2, NumberUtils.min(i,j,k));
                    treeDist.put(nodeTree1, nodeTree2, NumberUtils.min(i,j,k));
                } else {
                    final Widget earlierNode1 = getEarlierNode(nodeTree1, keyRootPathTree1);
                    final int i = getForestDist(nodeTree1, nodeTree2) + DELETE;

                    final Widget earlierNode2 = getEarlierNode(nodeTree2, keyRootPathTree1);
                    final int j = getForestDist(nodeTree1, nodeTree2) + INSERT;

                    final int k = getForestDist(nodeTree1, nodeTree2) + getTreeDist(nodeTree1, nodeTree2);

                    forestDist.put(nodeTree1, nodeTree2, NumberUtils.min(i,j,k));
                }
            }
        }

    }

    private boolean areNodesEqual(final Widget node1, final Widget node2) {
        if (node1 == null || node2 == null) {
            return false;
        }
        logger.debug("Comparing nodes: '{}' and '{}'", node1.toString(), node2.toString());

        return StringUtils.equals(node1.toString(), node2.toString());
    }

    private Widget getEarlierNode(final Widget node, final Deque<Widget> deque) {
        final List<Widget> dequeList = new ArrayList<>(deque);
        final int position = dequeList.indexOf(node); // test if this works

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
        result.add(widget);

        addLeftNode(widget, result);

        return result;
    }

    private void addLeftNode(final Widget widget, final Deque<Widget> result) {
        final List<Widget> sortedChildNodes = lrKeyrootsHelper.getSortedChildList(widget);
        if (sortedChildNodes.isEmpty()) {
            return;
        }

        result.add(sortedChildNodes.get(0));
        // call recursively
        addLeftNode(sortedChildNodes.get(0), result);
    }

    private Integer getForestDist(final Widget node1, final Widget node2) {
        if (node1 == null && node2 == null) {
            return 0;
        }

        if (!forestDist.containsKey(node1, node2)) {
            return 0;
        }

        return (Integer) forestDist.get(node1, node2);
    }

    private Integer getTreeDist(final Widget node1, final Widget node2) {
        if (node1 == null && node2 == null) {
            return 0;
        }

        return (Integer) treeDist.get(node1, node2);
    }
}
