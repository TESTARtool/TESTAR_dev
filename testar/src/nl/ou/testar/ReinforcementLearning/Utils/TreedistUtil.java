package nl.ou.testar.ReinforcementLearning.Utils;

import nl.ou.testar.ReinforcementLearning.RewardFunctions.LRKeyrootsHelper;
import org.apache.commons.collections.map.MultiKeyMap;
import org.fruit.alayer.Widget;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * Utils for the {@link nl.ou.testar.ReinforcementLearning.RewardFunctions.TreeDistHelper}
 */
public class TreedistUtil {

    /**
     * Asserts if two widgets are equal by comparing the abstractRepresentation
     * Returns true if both widgets are null, returns false if one of the widgets is null
     * @param widget1 widget to compare, may be null
     * @param widget2 widget to compare, may be null
     * @return True if equal
     */
    public static boolean equals(Widget widget1, Widget widget2) {
        if (widget1 == null && widget2 == null) {
            // should not happen
            return true;
        }

        if (widget1 == null || widget2 == null) {
            return false;
        }

//        return widget1.get(Tags.Title).equals(widget2.get(Tags.Title));

        return widget1.getAbstractRepresentation().equals(widget2.getAbstractRepresentation());
    }

    /**
     * Returns the widget and descendants in post order.
     * Child nodes are sorted by their abstract representation.
     * @param widget Widget as root node
     * @param result A deque in which the widget will be stored
     * @return The provided widget and descendants in post order
     */
    public static Deque<Widget> getPostOrder(final Widget widget, final Deque<Widget> result) {
        final List<Widget> sortedChildNodes = LRKeyrootsHelper.getSortedChildList(widget);

        sortedChildNodes.forEach(childWidget -> getPostOrder(childWidget, result));

        result.add(widget);
        return result;
    }

    /** Find iteratively the most the left element
     * @return The most left element
     */
    public static Widget getMostLeftWidget(final Widget widget) {
        return getLeftMostArray(widget).getFirst();
    }

    /** Creates a collection of the path from the most left item to the root element
     * The first element is the most left
     */
    public static Deque<Widget> getLeftMostArray(final Widget widget) {
        final Deque<Widget> result = new ArrayDeque<>();
        addLeftNode(widget, result);
        result.add(widget);
        return result;
    }

    private static void addLeftNode(final Widget widget, final Deque<Widget> result) {
        final List<Widget> sortedChildNodes = LRKeyrootsHelper.getSortedChildList(widget);
        if (sortedChildNodes.isEmpty()) {
            return;
        }

        // call recursively
        addLeftNode(sortedChildNodes.get(0), result);
        result.addLast(sortedChildNodes.get(0));
    }

    /**
     * Gets the previous node from a deque.
     * If no previous node is available a null value is returned
     * @param node A widget to retrieve the previous node
     * @param deque The deque from where to retrieve the previous node
     * @return The previous node
     */
    public static Widget getEarlierNode(final Widget node, final Deque<Widget> deque) {
        final List<Widget> dequeList = new ArrayList<>(deque);

        final int position = dequeList.indexOf(node);

        if (position - 1 < 0) {
            return null;
        }

        return dequeList.get(position - 1);
    }

    /**
     * For a MultiKeyMap<Widget, Widget, Integer> returns for two widgets the value stored
     * If no value is found a 0 is returned. 
     * @param dist The MultiKeyMap<Widget, Widget, Integer>
     * @param node1 First widget which is a key value
     * @param node2 Second widget which is a key value
     * @return The value stored
     */
    public static Integer getDist(final MultiKeyMap dist, final Widget node1, final Widget node2) {
        if (dist.get(node1, node2) == null){
            return 0;
        }

        return (Integer) dist.get(node1, node2);
    }
}
