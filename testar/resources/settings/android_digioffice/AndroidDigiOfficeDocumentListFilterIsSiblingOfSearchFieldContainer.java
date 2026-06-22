import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.visualizers.RegionsVisualizer;
import org.testar.oracles.Oracle;
import org.testar.monkey.alayer.android.enums.AndroidTags;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AndroidDigiOfficeDocumentListFilterIsSiblingOfSearchFieldContainer implements Oracle {

    private static final String DOCUMENT_LIST_FILTER_RESOURCE_ID = "dms-document-list-list-filter";
    private static final String DOCUMENT_SEARCH_INPUT_RESOURCE_ID = "dms-document-list-list-search-input";
    private static final String VIEW_GROUP_CLASS_NAME = "ViewGroup";

    @Override
    public void initialize() {
    }

    @Override
    public List<Verdict> getVerdicts(State state) {
        List<Verdict> verdicts = new ArrayList<>();

        for (Widget widget : state) {
            String resourceId = widget.get(AndroidTags.AndroidResourceId, "");

            if (!DOCUMENT_LIST_FILTER_RESOURCE_ID.equals(resourceId)) {
                continue;
            }

            if (!hasSearchFieldContainerSibling(widget)) {
                String verdictMsg = String.format(
                        "Detected document list filter without search field container sibling (resId=%s) %s",
                        resourceId,
                        widget.get(AndroidTags.AndroidXpath, ""));

                Visualizer visualizer = new RegionsVisualizer(
                        getRedPen(),
                        getWidgetRegions(Collections.singletonList(widget)),
                        "Invariant Fault",
                        0.5, 0.5);

                Verdict documentFilterSearchSiblingVerdict = new Verdict(
                        Verdict.Severity.WARNING_UI_ITEM_WRONG_VALUE_FAULT,
                        verdictMsg,
                        visualizer);
                verdicts.add(documentFilterSearchSiblingVerdict);
            }
        }

        if (verdicts.isEmpty()) {
            return Collections.singletonList(Verdict.OK);
        }

        return verdicts;
    }

    private boolean hasSearchFieldContainerSibling(Widget widget) {
        if (widget.parent() == null) {
            return false;
        }

        Widget parent = widget.parent();
        for (int i = 0; i < parent.childCount(); i++) {
            Widget sibling = parent.child(i);

            if (sibling == null || sibling == widget) {
                continue;
            }

            if (!isViewGroup(sibling)) {
                continue;
            }

            if (containsResourceIdRecursive(sibling, DOCUMENT_SEARCH_INPUT_RESOURCE_ID)) {
                return true;
            }
        }

        return false;
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
