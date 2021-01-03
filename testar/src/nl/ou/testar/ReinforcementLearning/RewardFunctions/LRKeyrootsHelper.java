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
        processChilds(widget, result);
        result.add(widget);

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
                .sorted(Comparator.comparing(Widget::getAbstractRepresentation))
                .collect(Collectors.toList());
    }

    private void processChilds(final Widget childWidget, final Deque<Widget> result) {
        final List<Widget> sortedChildList = getSortedChildList(childWidget);

        if (sortedChildList.isEmpty()) {
            return;
        }

        // TODO: This is printing Java Widget object identifier, not informative for logs debugging
        //logger.info("Sorted list is '{}'", sortedChildList);

        // TODO Fernando: Why? Only One left Widget
        // ANSWER FROM Olivia: I think this is actually accurate, left child nodes are not LRKeyroots,
            // and each right child should be analyzed because they are indeed LRKeyroots
            // Although the recursive call to processChild could be done here directly
        // Then all other to right Widget
        processLeftChild(sortedChildList.get(0), result);

        IntStream.range(1, sortedChildList.size()).forEach(i -> processRightChild(sortedChildList.get(i), result));
    }

    private void processLeftChild(final Widget leftChildWidget, final Deque<Widget> result) {
    	// TODO Fernando: Why not? result.add(leftChildWidget);
    	// ANSWER FROM Fernando: Probably something related with this LRKeyroots
    	
        processChilds(leftChildWidget, result);
    }

    private void processRightChild(final Widget rightChildWidget, final Deque<Widget> result) {
        // TODO: Maybe call processChilds before adding the widget?
        result.add(rightChildWidget);

        processChilds(rightChildWidget, result);
    }

}
