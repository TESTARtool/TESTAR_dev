package nl.ou.testar.ReinforcementLearning.RewardFunctions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fruit.alayer.Widget;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class LRKeyrootsHelper {

    private static final Logger logger = LogManager.getLogger(LRKeyrootsHelper.class);

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

        logger.info("Returned deque of widgets is '{}'", result);
        logger.info("Returned deque of widgets is with size '{}'", result.size());

        return result;
    }

    /**
     * Sorts the child nodes of a widget
     * @param widget The widget for which to get the child nodes
     * @return A sorted list of {@link Widget}
     */
    public static List<Widget> getSortedChildList(final Widget widget) {
        final List<Widget> childList = new ArrayList<>();
        for(int i = 0; i < widget.childCount(); i++) {
            childList.add(widget.child(i));
        }

        return childList.stream()
                .sorted(Comparator.comparing(childWidget -> childWidget.getAbstractRepresentation()))
                .collect(Collectors.toList());
    }

    private void processChilds(final Widget childWidget, final Deque<Widget> result) {
        final List<Widget> sortedChildList = getSortedChildList(childWidget);

        if (sortedChildList.isEmpty()) {
            return;
        }

        // TODO: Widget object identifier is not informative for logs debugging
        //logger.info("Sorted list is '{}'", sortedChildList);

        // TODO: Why? Only One left Widget
        // Then all other to right Widget
        processLeftChild(sortedChildList.get(0), result);

        IntStream.range(1, sortedChildList.size()).forEach(i -> processRightChild(sortedChildList.get(i), result));
    }

    private void processLeftChild(final Widget leftChildWidget, final Deque<Widget> result) {
    	// FIXME: Why not?
    	//result.add(leftChildWidget);
    	
        processChilds(leftChildWidget, result);
    }

    private void processRightChild(final Widget rightChildWidget, final Deque<Widget> result) {
        result.add(rightChildWidget);

        processChilds(rightChildWidget, result);
    }

}
