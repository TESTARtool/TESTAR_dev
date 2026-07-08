package android_digioffice.oracles;

import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.visualizers.RegionsVisualizer;
import org.testar.monkey.alayer.android.enums.AndroidTags;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AndroidDigiOfficeMainBottomNavigationWidgetsAreSiblings extends AbstractAndroidDigiOfficeOracle {

    private static final String VIEW_CLASS_NAME = "android.view.View";
    private static final List<String> REQUIRED_ACCESSIBILITY_IDS = Arrays.asList(
            "Documents",
            "Tasks",
            "Contacts",
            "Settings");

    public AndroidDigiOfficeMainBottomNavigationWidgetsAreSiblings() {
        super("AndroidDigiOfficeMainBottomNavigationWidgetsAreSiblings");
    }

    @Override
    protected boolean isApplicable(State state) {
        for (Widget widget : state) {
            if (isExactViewAccessibilityWidget(widget, "Documents")) {
                return true;
            }
        }

        return false;
    }

    @Override
    protected List<Verdict> check(State state) {
        List<Verdict> verdicts = new ArrayList<>();

        for (Widget widget : state) {
            if (!isExactViewAccessibilityWidget(widget, "Documents")) {
                continue;
            }

            if (!hasAllSiblingAccessibilityIds(widget, REQUIRED_ACCESSIBILITY_IDS)) {
                String verdictMsg = String.format(
                        "Detected main bottom navigation without required sibling widgets %s",
                        widget.get(AndroidTags.AndroidXpath, ""));

                Visualizer visualizer = new RegionsVisualizer(
                        getRedPen(),
                        getWidgetRegions(Collections.singletonList(widget)),
                        "Invariant Fault",
                        0.5, 0.5);

                Verdict mainBottomNavigationVerdict = new Verdict(
                        Verdict.Severity.WARNING_UI_ITEM_WRONG_VALUE_FAULT,
                        verdictMsg,
                        visualizer);
                verdicts.add(mainBottomNavigationVerdict);
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
            if (expectedAccessibilityId.equals(accessibilityId)) {
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
