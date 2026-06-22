import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.visualizers.RegionsVisualizer;
import org.testar.oracles.Oracle;
import org.testar.monkey.alayer.android.enums.AndroidTags;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AndroidDigiOfficeViewHeaderContainsBackOption implements Oracle {

    private static final String HEADER_TITLE_RESOURCE_ID = "header-title";
    private static final String APP_HEADER_RESOURCE_ID = "app-header";
    private static final String HEADER_BACK_RESOURCE_ID = "header-back";
    private static final String GO_BACK_ACCESSIBILITY_ID = "Go back";
    private static final String NAVIGATE_UP_ACCESSIBILITY_ID = "Navigate up";

    @Override
    public void initialize() {
    }

    @Override
    public List<Verdict> getVerdicts(State state) {
        List<Verdict> verdicts = new ArrayList<>();

        for (Widget widget : state) {
            String resourceId = widget.get(AndroidTags.AndroidResourceId, "");

            if (!HEADER_TITLE_RESOURCE_ID.equals(resourceId)) {
                continue;
            }

            // The main view with app-header is ignored
            if (ignoreIfSiblingWithResourceId(widget, APP_HEADER_RESOURCE_ID)) {
                continue;
            }

            // Other views must contain some possible back option
            if (!hasBackOptionSibling(widget)) {
                String verdictMsg = String.format(
                        "Detected view header without back option sibling (resId=%s) %s",
                        resourceId,
                        widget.get(AndroidTags.AndroidXpath, ""));

                Visualizer visualizer = new RegionsVisualizer(
                        getRedPen(),
                        getWidgetRegions(Collections.singletonList(widget)),
                        "Invariant Fault",
                        0.5, 0.5);

                Verdict viewHeaderBackOptionVerdict = new Verdict(
                        Verdict.Severity.WARNING_UI_ITEM_WRONG_VALUE_FAULT,
                        verdictMsg,
                        visualizer);
                verdicts.add(viewHeaderBackOptionVerdict);
            }
        }

        if (verdicts.isEmpty()) {
            return Collections.singletonList(Verdict.OK);
        }

        return verdicts;
    }

    private boolean hasBackOptionSibling(Widget widget) {
        if (widget.parent() == null) {
            return false;
        }

        // First, we check the back option siblings
        Widget parent = widget.parent();
        for (int i = 0; i < parent.childCount(); i++) {
            Widget sibling = parent.child(i);

            if (sibling == null || sibling == widget) {
                continue;
            }

            String siblingResourceId = sibling.get(AndroidTags.AndroidResourceId, "");
            String siblingAccessibilityId = sibling.get(AndroidTags.AndroidAccessibilityId, "");

            if (HEADER_BACK_RESOURCE_ID.equals(siblingResourceId)
                    || GO_BACK_ACCESSIBILITY_ID.equals(siblingAccessibilityId)) {
                return true;
            }
        }

        // But is also possible to find a Navigate up parent-sibling option
        return hasParentSiblingWithAccessibilityId(parent, NAVIGATE_UP_ACCESSIBILITY_ID);
    }

    private boolean ignoreIfSiblingWithResourceId(Widget widget, String expectedSiblingResourceId) {
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

    private boolean hasParentSiblingWithAccessibilityId(Widget parent, String expectedAccessibilityId) {
        if (parent.parent() == null) {
            return false;
        }

        Widget grandParent = parent.parent();
        for (int i = 0; i < grandParent.childCount(); i++) {
            Widget parentSibling = grandParent.child(i);

            if (parentSibling == null || parentSibling == parent) {
                continue;
            }

            String accessibilityId = parentSibling.get(AndroidTags.AndroidAccessibilityId, "");
            if (expectedAccessibilityId.equals(accessibilityId)) {
                return true;
            }
        }

        return false;
    }
}
