package android_digioffice.oracles;

import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Verdict;
import org.testar.monkey.alayer.Visualizer;
import org.testar.monkey.alayer.Widget;
import org.testar.monkey.alayer.visualizers.RegionsVisualizer;
import org.testar.monkey.alayer.android.enums.AndroidTags;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class AndroidDigiOfficeTaskTabsDrawerContainsExpectedActions extends AbstractAndroidDigiOfficeOracle {

    private static final String DRAWER_RESOURCE_ID = "task-tabs-drawer-content";
    private static final String REQUIRED_SUBMIT_FOR_REVIEW_TEXT = "submit for review";
    private static final String REQUIRED_START_SIDESTEP_WORKFLOW_TEXT = "start sidestep workflow";
    private static final String REQUIRED_SHARE_PDF_TEXT = "share pdf";

    public AndroidDigiOfficeTaskTabsDrawerContainsExpectedActions() {
        super("AndroidDigiOfficeTaskTabsDrawerContainsExpectedActions");
    }

    private boolean isTaskTabsDrawerWidget(Widget widget) {
        String resourceId = widget.get(AndroidTags.AndroidResourceId, "");
        return DRAWER_RESOURCE_ID.equals(resourceId);
    }

    @Override
    protected boolean isApplicable(State state) {
        for (Widget widget : state) {
            if (isTaskTabsDrawerWidget(widget)) {
                return true;
            }
        }

        return false;
    }

    @Override
    protected List<Verdict> check(State state) {
        List<Verdict> verdicts = new ArrayList<>();

        for (Widget widget : state) {
            if (!isTaskTabsDrawerWidget(widget)) {
                continue;
            }

            boolean hasSubmitForReview = containsTextRecursive(widget, REQUIRED_SUBMIT_FOR_REVIEW_TEXT);
            boolean hasStartSidestepWorkflow = containsTextRecursive(widget, REQUIRED_START_SIDESTEP_WORKFLOW_TEXT);
            boolean hasSharePdf = containsTextRecursive(widget, REQUIRED_SHARE_PDF_TEXT);

            if (hasSubmitForReview && hasStartSidestepWorkflow && hasSharePdf) {
                continue;
            }

            String verdictMsg = String.format(
                    "Detected task tabs drawer without required subtree texts 'submit for review', 'start sidestep workflow', and 'Share PDF' (resId=%s) %s",
                    widget.get(AndroidTags.AndroidResourceId, ""),
                    widget.get(AndroidTags.AndroidXpath, ""));

            Visualizer visualizer = new RegionsVisualizer(
                    getRedPen(),
                    getWidgetRegions(Collections.singletonList(widget)),
                    "Invariant Fault",
                    0.5, 0.5);

            verdicts.add(new Verdict(
                    Verdict.Severity.WARNING_UI_ITEM_WRONG_VALUE_FAULT,
                    verdictMsg,
                    visualizer));
        }

        if (verdicts.isEmpty()) {
            return Collections.singletonList(Verdict.OK);
        }

        return verdicts;
    }

    private boolean containsTextRecursive(Widget widget, String expectedTextPart) {
        String widgetText = widget.get(AndroidTags.AndroidText, "").toLowerCase(Locale.ROOT);
        if (widgetText.contains(expectedTextPart)) {
            return true;
        }

        for (int i = 0; i < widget.childCount(); i++) {
            if (containsTextRecursive(widget.child(i), expectedTextPart)) {
                return true;
            }
        }

        return false;
    }
}
