package android_digioffice.oracles;

import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.visualizers.RegionsVisualizer;
import org.testar.monkey.alayer.android.enums.AndroidTags;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AndroidDigiOfficeWidgetIsNotEmpty extends AbstractAndroidDigiOfficeOracle {

    // Matches any resId that ends with:
    // person-detail-contact-person-0-secondary-text
    // person-detail-contact-person-0-tertiary-text
    // contact-person-detail-function-text
    private static final java.util.regex.Pattern WIDGET_ID_PATTERN = java.util.regex.Pattern
            .compile(".*(detail-contact-person-\\d+-(secondary|tertiary)-text|person-detail-function-text).*");

    private boolean isInvalidValue(String value) {
        if (value.trim().isEmpty())
            return true;
        return value.trim().equals("-");
    }

    public AndroidDigiOfficeWidgetIsNotEmpty() {
        super("AndroidDigiOfficeWidgetIsNotEmpty");
    }

    private boolean isNonEmptyWidgetCandidate(Widget widget) {
        String resourceId = widget.get(AndroidTags.AndroidResourceId, "");
        return WIDGET_ID_PATTERN.matcher(resourceId).matches();
    }

    @Override
    protected boolean isApplicable(State state) {
        for (Widget w : state) {
            if (isNonEmptyWidgetCandidate(w)) {
                return true;
            }
        }

        return false;
    }

    @Override
    protected List<Verdict> check(State state) {
        List<Verdict> verdicts = new ArrayList<>();

        for (Widget w : state) {
            if (!isNonEmptyWidgetCandidate(w)) {
                continue;
            }

            String resourceId = w.get(AndroidTags.AndroidResourceId, "");
            // The value can exist in the accessibility id or in the text content
            String accessibilityValue = w.get(AndroidTags.AndroidAccessibilityId, "");
            String textValue = w.get(AndroidTags.AndroidText, "");

            String value = accessibilityValue.isEmpty() ? textValue : accessibilityValue;

            if (isInvalidValue(value)) {
                String verdictMsg = String.format(
                        "Detected widget with invalid content (resId=%s, value='%s') %s",
                        resourceId, value, w.get(AndroidTags.AndroidXpath));

                Visualizer visualizer = new RegionsVisualizer(
                        getRedPen(),
                        getWidgetRegions(Collections.singletonList(w)),
                        "Invariant Fault",
                        0.5, 0.5);

                Verdict widgetEmptyVerdict = new Verdict(
                        Verdict.Severity.WARNING_UI_ITEM_WRONG_VALUE_FAULT,
                        verdictMsg,
                        visualizer);
                verdicts.add(widgetEmptyVerdict);
            }
        }

        if (verdicts.isEmpty()) {
            return Collections.singletonList(Verdict.OK);
        }

        return verdicts;
    }
}
