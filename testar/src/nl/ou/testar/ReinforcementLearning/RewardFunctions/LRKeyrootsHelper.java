package nl.ou.testar.ReinforcementLearning.RewardFunctions;

import org.fruit.alayer.Widget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class LRKeyrootsHelper {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Splitting a tree in sub trees. The LR Keyroots are the roots of each sub tree.
     * For a given widget(is a tree) this method finds and returns the LR Keyroots.
     * @param widget The widget which is a tree
     * @return A list of LR Keyroots
     */
    public Deque<Widget> getLRKeyroots(final Widget widget) {
        final Deque<Widget> result = new ArrayDeque<>();
        // add root element
        result.add(widget);

        processChilds(widget, result);

        return result;
    }

    /**
     * Sorts the child nodes of a widget
     * @param widget The widget for which to get the child nodes
     * @return A sorted list of {@link Widget}
     */
    public List<Widget> getSortedChildList(final Widget widget) {
        final List<Widget> childList = new ArrayList<>();
        for(int i = 0; i < widget.childCount(); i++) {
            childList.add(widget.child(i));
        }

        return childList.stream()
                .sorted(Comparator.comparing(childWidget -> childWidget.getRepresentation("")))
                .collect(Collectors.toList());
    }

    private void processChilds(final Widget childWidget, final Deque<Widget> result) {
        final List<Widget> sortedChildList = getSortedChildList(childWidget);

        logger.debug("Sorted list is '{}'", sortedChildList);

        if (sortedChildList.isEmpty()) {
            return;
        }

        processLeftChild(sortedChildList.get(0), result);

        IntStream.range(1, sortedChildList.size())
                .forEach(i -> processRightChild(sortedChildList.get(i), result));
    }

    private void processLeftChild(final Widget leftChildWidget, final Deque<Widget> result) {
        processChilds(leftChildWidget, result);
    }

    private void processRightChild(final Widget rightChildWidget, final Deque<Widget> result) {
        result.add(rightChildWidget);

        processChilds(rightChildWidget, result);
    }

}
