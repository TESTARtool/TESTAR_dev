import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.visualizers.RegionsVisualizer;
import org.testar.oracles.Oracle;
import org.testar.monkey.alayer.android.enums.AndroidTags;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AndroidDigiOfficeDocumentSearchFieldContainsUniqueClearWidget implements Oracle {

    private static final String DOCUMENT_SEARCH_INPUT_RESOURCE_ID = "dms-document-list-list-search-input";
    private static final String DOCUMENT_CLEAR_RESOURCE_ID = "dms-document-list-list-clear";
    private static final String VIEW_GROUP_CLASS_NAME = "ViewGroup";

    @Override
    public void initialize() {
    }

    @Override
    public List<Verdict> getVerdicts(State state) {
        List<Verdict> verdicts = new ArrayList<>();

        for (Widget widget : state) {
            String resourceId = widget.get(AndroidTags.AndroidResourceId, "");

            if (!DOCUMENT_SEARCH_INPUT_RESOURCE_ID.equals(resourceId)) {
                continue;
            }

            if (!hasUniqueClearWidgetContainerSibling(widget)) {
                String verdictMsg = String.format(
                        "Detected document search input without unique clear widget container sibling (resId=%s) %s",
                        resourceId,
                        widget.get(AndroidTags.AndroidXpath, ""));

                Visualizer visualizer = new RegionsVisualizer(
                        getRedPen(),
                        getWidgetRegions(Collections.singletonList(widget)),
                        "Invariant Fault",
                        0.5, 0.5);

                Verdict documentSearchClearContainerVerdict = new Verdict(
                        Verdict.Severity.WARNING_UI_ITEM_WRONG_VALUE_FAULT,
                        verdictMsg,
                        visualizer);
                verdicts.add(documentSearchClearContainerVerdict);
            }
        }

        if (verdicts.isEmpty()) {
            return Collections.singletonList(Verdict.OK);
        }

        return verdicts;
    }

    private boolean hasUniqueClearWidgetContainerSibling(Widget widget) {
        if (widget.parent() == null) {
            return false;
        }

        int matchingSiblings = 0;
        Widget parent = widget.parent();
        for (int i = 0; i < parent.childCount(); i++) {
            Widget sibling = parent.child(i);

            if (sibling == null || sibling == widget) {
                continue;
            }

            if (!isViewGroup(sibling)) {
                continue;
            }

            if (containsResourceIdRecursive(sibling, DOCUMENT_CLEAR_RESOURCE_ID)) {
                matchingSiblings++;
            }
        }

        return matchingSiblings == 1;
    }

    private boolean isViewGroup(Widget widget) {
        String className = widget.get(AndroidTags.AndroidClassName, "");
        return className.endsWith(VIEW_GROUP_CLASS_NAME);
    }

    private boolean containsResourceIdRecursive(Widget widget, String expectedResourceId) {
        String resourceId = widget.get(AndroidTags.AndroidResourceId, "");
        if (expectedResourceId.equals(resourceId)) {
            return true;
        }

        for (int i = 0; i < widget.childCount(); i++) {
            if (containsResourceIdRecursive(widget.child(i), expectedResourceId)) {
                return true;
            }
        }

        return false;
    }
}
