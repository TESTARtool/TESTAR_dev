package android_digioffice.oracles;

import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.visualizers.RegionsVisualizer;
import org.testar.monkey.alayer.android.enums.AndroidTags;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AndroidDigiOfficeDocumentActionButtonsAreSiblings extends AbstractAndroidDigiOfficeOracle {

    private static final String DOCUMENT_ATTACHMENTS_MENU_RESOURCE_ID = "document-attachments-menu";
    private static final String DOCUMENT_TABS_OVERFLOW_BUTTON_RESOURCE_ID = "document-tabs-overflow-button";

    public AndroidDigiOfficeDocumentActionButtonsAreSiblings() {
        super("AndroidDigiOfficeDocumentActionButtonsAreSiblings");
    }

    private boolean isDocumentAttachmentsMenuWidget(Widget widget) {
        String resourceId = widget.get(AndroidTags.AndroidResourceId, "");
        return DOCUMENT_ATTACHMENTS_MENU_RESOURCE_ID.equals(resourceId);
    }

    @Override
    protected boolean isApplicable(State state) {
        for (Widget widget : state) {
            if (isDocumentAttachmentsMenuWidget(widget)) {
                return true;
            }
        }

        return false;
    }

    @Override
    protected List<Verdict> check(State state) {
        List<Verdict> verdicts = new ArrayList<>();

        for (Widget widget : state) {
            if (!isDocumentAttachmentsMenuWidget(widget)) {
                continue;
            }

            String resourceId = widget.get(AndroidTags.AndroidResourceId, "");
            if (!hasSiblingWithResourceId(widget, DOCUMENT_TABS_OVERFLOW_BUTTON_RESOURCE_ID)) {
                String verdictMsg = String.format(
                        "Detected document attachments menu without required sibling buttons (resId=%s) %s",
                        resourceId,
                        widget.get(AndroidTags.AndroidXpath, ""));

                Visualizer visualizer = new RegionsVisualizer(
                        getRedPen(),
                        getWidgetRegions(Collections.singletonList(widget)),
                        "Invariant Fault",
                        0.5, 0.5);

                Verdict documentActionSiblingsVerdict = new Verdict(
                        Verdict.Severity.WARNING_UI_ITEM_WRONG_VALUE_FAULT,
                        verdictMsg,
                        visualizer);
                verdicts.add(documentActionSiblingsVerdict);
            }
        }

        if (verdicts.isEmpty()) {
            return Collections.singletonList(Verdict.OK);
        }

        return verdicts;
    }

    private boolean hasSiblingWithResourceId(Widget widget, String expectedSiblingResourceId) {
        if (widget.parent() == null) {
            return false;
        }

        Widget parent = widget.parent();
        for (int i = 0; i < parent.childCount(); i++) {
            Widget sibling = parent.child(i);

            if (sibling == null || sibling == widget) {
                continue;
            }

            String siblingResourceId = sibling.get(AndroidTags.AndroidResourceId, "");
            if (expectedSiblingResourceId.equals(siblingResourceId)) {
                return true;
            }
        }

        return false;
    }
}
