package nl.ou.testar.ReinforcementLearning.RewardFunctions;

import org.fruit.alayer.Widget;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Helper class for the {@link WidgetTreeZhangShashaBasedRewardFunction}
 */
public class LRKeyrootsHelper {

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

        // processLeftChild
        processChilds(sortedChildList.get(0), result);

        IntStream.range(1, sortedChildList.size()).forEach(i -> processRightChild(sortedChildList.get(i), result));
    }

    private void processRightChild(final Widget rightChildWidget, final Deque<Widget> result) {
        result.add(rightChildWidget);

        processChilds(rightChildWidget, result);
    }

}
