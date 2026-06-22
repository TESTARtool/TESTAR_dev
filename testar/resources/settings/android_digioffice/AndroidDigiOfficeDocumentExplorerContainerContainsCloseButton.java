import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.visualizers.RegionsVisualizer;
import org.testar.oracles.Oracle;
import org.testar.monkey.alayer.android.enums.AndroidTags;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AndroidDigiOfficeDocumentExplorerContainerContainsCloseButton implements Oracle {

    private static final String DOCUMENT_EXPLORER_CONTAINER_RESOURCE_ID = "dms-document-list-list-explorer-container";
    private static final String DOCUMENT_EXPLORER_CLOSE_RESOURCE_ID = "dms-document-list-list-explorer-header-close";

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

            if (!containsResourceIdRecursive(widget, DOCUMENT_EXPLORER_CLOSE_RESOURCE_ID)) {
                String verdictMsg = String.format(
                        "Detected document explorer container without close button (resId=%s) %s",
                        resourceId,
                        widget.get(AndroidTags.AndroidXpath, ""));

                Visualizer visualizer = new RegionsVisualizer(
                        getRedPen(),
                        getWidgetRegions(Collections.singletonList(widget)),
                        "Invariant Fault",
                        0.5, 0.5);

                Verdict documentExplorerCloseVerdict = new Verdict(
                        Verdict.Severity.WARNING_UI_ITEM_WRONG_VALUE_FAULT,
                        verdictMsg,
                        visualizer);
                verdicts.add(documentExplorerCloseVerdict);
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
}
