package android_digioffice.oracles;

import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.visualizers.RegionsVisualizer;
import org.testar.monkey.alayer.android.enums.AndroidTags;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AndroidDigiOfficeMobileTextIsNotValid extends AbstractAndroidDigiOfficeOracle {

    // Matches any resId that ends with:
    // person-detail-mobile-text
    // relation-detail-mobile-text
    // contact-person-detail-mobile-text
    // contact-person-detail-relation-mobile-text
    private static final java.util.regex.Pattern MOBILE_WIDGET_ID_PATTERN = java.util.regex.Pattern
            .compile(".*(detail|detail-relation)-mobile-text.*");

    private boolean isInvalidValue(String value) {
        if (value.trim().isEmpty())
            return true; // empty is invalid
        if (value.trim().equals("-"))
            return false; // dash char is allowed

        String upperValue = value.trim().toUpperCase(java.util.Locale.ROOT);

        // No alphabetic characters allowed
        return java.util.regex.Pattern.compile(".*[A-Z].*").matcher(upperValue).matches();
    }

    public AndroidDigiOfficeMobileTextIsNotValid() {
        super("AndroidDigiOfficeMobileTextIsNotValid");
    }

    private boolean isMobileWidget(Widget widget) {
        String resourceId = widget.get(AndroidTags.AndroidResourceId, "");
        return MOBILE_WIDGET_ID_PATTERN.matcher(resourceId).matches();
    }

    @Override
    protected boolean isApplicable(State state) {
        for (Widget w : state) {
            if (isMobileWidget(w)) {
                return true;
            }
        }

        return false;
    }

    @Override
    protected List<Verdict> check(State state) {
        List<Verdict> verdicts = new ArrayList<>();

        for (Widget w : state) {
            if (!isMobileWidget(w)) {
                continue;
            }

            String resourceId = w.get(AndroidTags.AndroidResourceId, "");
            // The value can exist in the accessibility id or in the text content
            String accessibilityValue = w.get(AndroidTags.AndroidAccessibilityId, "");
            String textValue = w.get(AndroidTags.AndroidText, "");

            String value = accessibilityValue.isEmpty() ? textValue : accessibilityValue;

            if (isInvalidValue(value)) {
                String verdictMsg = String.format(
                        "Detected Mobile text with invalid content (resId=%s, value='%s') %s",
                        resourceId, value, w.get(AndroidTags.AndroidXpath));

                Visualizer visualizer = new RegionsVisualizer(
                        getRedPen(),
                        getWidgetRegions(Collections.singletonList(w)),
                        "Invariant Fault",
                        0.5, 0.5);

                Verdict mobileTextVerdict = new Verdict(
                        Verdict.Severity.WARNING_UI_ITEM_WRONG_VALUE_FAULT,
                        verdictMsg,
                        visualizer);
                verdicts.add(mobileTextVerdict);
            }
        }

        if (verdicts.isEmpty()) {
            return Collections.singletonList(Verdict.OK);
        }

        return verdicts;
    }
}
