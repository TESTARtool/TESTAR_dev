package nl.ou.testar.ReinforcementLearning.Utils;

import nl.ou.testar.ReinforcementLearning.RewardFunctions.LRKeyrootsHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fruit.alayer.Tags;
import org.fruit.alayer.Widget;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class WidgetUtil {

    private static final Logger logger = LogManager.getLogger(WidgetUtil.class);

    public static boolean equals(Widget widget1, Widget widget2) {
        if (widget1 == null && widget2 == null) {
            return true;
        }

        if (widget1 == null || widget2 == null) {
            return false;
        }

//        System.out.println("!!!! Cheking areNodesEqual (Not Null) !!!!");
//
//        //TODO: Check what is returning toString()
//        logger.debug("Comparing nodes: '{}' and '{}'", widget1.toString(), widget2.toString());
//
//        // TODO: Note that getAbstractRepresentation of two "different" menu items will be the same if Widget Title is not included in the abstraction level
//        // TODO: Basically users will need to define a GOOD SUT Abstraction before running this

        return widget1.get(Tags.Title).equals(widget2.get(Tags.Title));

//        return widget1.getAbstractRepresentation().equals(widget2.getAbstractRepresentation());
    }

    /** Creates a collection of the path from the most left item to the root element
     * The first element is the most left
     */
    public static Deque<Widget> getLeftMostArrayPO(final Widget widget) {
        final Deque<Widget> result = new ArrayDeque<>();

        final Widget leftmost = getLeftMostArray(widget).getFirst();

        addSiblings(leftmost, result);
        return result;
    }

    private static Deque<Widget> addSiblings(Widget widget, Deque<Widget> deque) {
        if (widget == null ) {
            return deque;
        }

        final Widget parent = widget.parent();
        if (parent == null) {
            deque.add(widget);
            return deque;
        }

        for (int i = 0; i < parent.childCount(); i++) {
            deque.add(parent.child(i));
        }

        // recursive call
        addSiblings(parent.parent(), deque);
        return deque;
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

    public static Widget getEarlierNode(final Widget node, final Deque<Widget> deque) {
        final List<Widget> dequeList = new ArrayList<>(deque);

        final int position = dequeList.indexOf(node);

        if (position - 1 < 0) {
            return null;
        }

        return dequeList.get(position - 1);
    }
}
