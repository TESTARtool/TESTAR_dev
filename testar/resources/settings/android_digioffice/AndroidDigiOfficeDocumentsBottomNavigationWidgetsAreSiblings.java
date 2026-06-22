import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.visualizers.RegionsVisualizer;
import org.testar.oracles.Oracle;
import org.testar.monkey.alayer.android.enums.AndroidTags;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AndroidDigiOfficeDocumentsBottomNavigationWidgetsAreSiblings implements Oracle {

    private static final String VIEW_CLASS_NAME = "View";
    private static final List<String> REQUIRED_ACCESSIBILITY_IDS = Arrays.asList(
            "Preview",
            "Comments",
            "Info");

    @Override
    public void initialize() {
    }

    @Override
    public List<Verdict> getVerdicts(State state) {
        List<Verdict> verdicts = new ArrayList<>();

        for (Widget widget : state) {
            if (!isExactViewAccessibilityWidget(widget, "Preview")) {
                continue;
            }

            if (!hasAllSiblingAccessibilityIds(widget, REQUIRED_ACCESSIBILITY_IDS)) {
                String verdictMsg = String.format(
                        "Detected documents bottom navigation without required sibling widgets %s",
                        widget.get(AndroidTags.AndroidXpath, ""));

                Visualizer visualizer = new RegionsVisualizer(
                        getRedPen(),
                        getWidgetRegions(Collections.singletonList(widget)),
                        "Invariant Fault",
                        0.5, 0.5);

                Verdict documentsBottomNavigationVerdict = new Verdict(
                        Verdict.Severity.WARNING_UI_ITEM_WRONG_VALUE_FAULT,
                        verdictMsg,
                        visualizer);
                verdicts.add(documentsBottomNavigationVerdict);
            }
        }

        if (verdicts.isEmpty()) {
            return Collections.singletonList(Verdict.OK);
        }

        return verdicts;
    }

    private boolean hasAllSiblingAccessibilityIds(Widget widget, List<String> accessibilityIds) {
        if (widget.parent() == null) {
            return false;
        }

        for (String accessibilityId : accessibilityIds) {
            if (!hasSiblingWithAccessibilityId(widget, accessibilityId)) {
                return false;
            }
        }

        return true;
    }

    private boolean hasSiblingWithAccessibilityId(Widget widget, String expectedAccessibilityId) {
        Widget parent = widget.parent();
        for (int i = 0; i < parent.childCount(); i++) {
            Widget sibling = parent.child(i);

            if (sibling == null) {
                continue;
            }

            if (!isViewClass(sibling)) {
                continue;
            }

            String accessibilityId = sibling.get(AndroidTags.AndroidAccessibilityId, "");
            if ("Comments".equals(expectedAccessibilityId)) {
                if (accessibilityId.contains(expectedAccessibilityId)) {
                    return true;
                }
            } else if (expectedAccessibilityId.equals(accessibilityId)) {
                return true;
            }
        }

        return false;
    }

    private boolean isExactViewAccessibilityWidget(Widget widget, String expectedAccessibilityId) {
        if (!isViewClass(widget)) {
            return false;
        }

        String accessibilityId = widget.get(AndroidTags.AndroidAccessibilityId, "");
        return expectedAccessibilityId.equals(accessibilityId);
    }

    private boolean isViewClass(Widget widget) {
        String className = widget.get(AndroidTags.AndroidClassName, "");
        return VIEW_CLASS_NAME.equals(className);
    }
}
