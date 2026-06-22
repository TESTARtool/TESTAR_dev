import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.visualizers.RegionsVisualizer;
import org.testar.oracles.Oracle;
import org.testar.monkey.alayer.android.enums.AndroidTags;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AndroidDigiOfficeDocumentExplorerBackRequiresSearchOption implements Oracle {

    private static final String DOCUMENT_EXPLORER_CONTAINER_RESOURCE_ID = "dms-document-list-list-explorer-container";
    private static final String DOCUMENT_EXPLORER_BACK_RESOURCE_ID = "dms-document-list-list-explorer-header-back";
    private static final String DOCUMENT_EXPLORER_SEARCH_RESOURCE_ID = "dms-document-list-list-explorer-content-search";
    private static final String VIEW_GROUP_CLASS_NAME = "ViewGroup";

    @Override
    public void initialize() {
    }

    @Override
    public List<Verdict> getVerdicts(State state) {
        List<Verdict> verdicts = new ArrayList<>();

        for (Widget widget : state) {
            String resourceId = widget.get(AndroidTags.AndroidResourceId, "");

            if (!DOCUMENT_EXPLORER_CONTAINER_RESOURCE_ID.equals(resourceId)) {
                continue;
            }

            if (!containsResourceIdRecursive(widget, DOCUMENT_EXPLORER_BACK_RESOURCE_ID)) {
                continue;
            }

            if (!containsViewGroupWithResourceId(widget, DOCUMENT_EXPLORER_SEARCH_RESOURCE_ID)) {
                String verdictMsg = String.format(
                        "Detected document explorer container with back option but without search option (resId=%s) %s",
                        resourceId,
                        widget.get(AndroidTags.AndroidXpath, ""));

                Visualizer visualizer = new RegionsVisualizer(
                        getRedPen(),
                        getWidgetRegions(Collections.singletonList(widget)),
                        "Invariant Fault",
                        0.5, 0.5);

                Verdict documentExplorerBackSearchVerdict = new Verdict(
                        Verdict.Severity.WARNING_UI_ITEM_WRONG_VALUE_FAULT,
                        verdictMsg,
                        visualizer);
                verdicts.add(documentExplorerBackSearchVerdict);
            }
        }

        if (verdicts.isEmpty()) {
            return Collections.singletonList(Verdict.OK);
        }

        return verdicts;
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

    private boolean containsViewGroupWithResourceId(Widget widget, String expectedResourceId) {
        if (isViewGroup(widget) && containsResourceIdRecursive(widget, expectedResourceId)) {
            return true;
        }

        for (int i = 0; i < widget.childCount(); i++) {
            if (containsViewGroupWithResourceId(widget.child(i), expectedResourceId)) {
                return true;
            }
        }

        return false;
    }

    private boolean isViewGroup(Widget widget) {
        String className = widget.get(AndroidTags.AndroidClassName, "");
        return className.endsWith(VIEW_GROUP_CLASS_NAME);
    }
}
