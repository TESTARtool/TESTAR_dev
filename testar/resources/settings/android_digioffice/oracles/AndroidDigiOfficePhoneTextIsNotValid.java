package android_digioffice.oracles;

import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.visualizers.RegionsVisualizer;
import org.testar.monkey.alayer.android.enums.AndroidTags;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AndroidDigiOfficePhoneTextIsNotValid extends AbstractAndroidDigiOfficeOracle {

    // Matches any resId that ends with:
    // person-detail-phone-text
    // relation-detail-phone-text
    // contact-person-detail-phone-text
    // contact-person-detail-relation-phone-text
    private static final java.util.regex.Pattern PHONE_WIDGET_ID_PATTERN = java.util.regex.Pattern
            .compile(".*(detail|detail-relation)-phone-text.*");

    private boolean isInvalidValue(String value) {
        if (value.trim().isEmpty())
            return true; // empty is invalid
        if (value.trim().equals("-"))
            return false; // dash char is allowed

        String upperValue = value.trim().toUpperCase(java.util.Locale.ROOT);

        // No alphabetic characters allowed
        return java.util.regex.Pattern.compile(".*[A-Z].*").matcher(upperValue).matches();
    }

    public AndroidDigiOfficePhoneTextIsNotValid() {
        super("AndroidDigiOfficePhoneTextIsNotValid");
    }

    private boolean isPhoneWidget(Widget widget) {
        String resourceId = widget.get(AndroidTags.AndroidResourceId, "");
        return PHONE_WIDGET_ID_PATTERN.matcher(resourceId).matches();
    }

    @Override
    protected boolean isApplicable(State state) {
        for (Widget w : state) {
            if (isPhoneWidget(w)) {
                return true;
            }
        }

        return false;
    }

    @Override
    protected List<Verdict> check(State state) {
        List<Verdict> verdicts = new ArrayList<>();

        for (Widget w : state) {
            if (!isPhoneWidget(w)) {
                continue;
            }

            String resourceId = w.get(AndroidTags.AndroidResourceId, "");
            // The value can exist in the accessibility id or in the text content
            String accessibilityValue = w.get(AndroidTags.AndroidAccessibilityId, "");
            String textValue = w.get(AndroidTags.AndroidText, "");

            String value = accessibilityValue.isEmpty() ? textValue : accessibilityValue;

            if (isInvalidValue(value)) {
                String verdictMsg = String.format(
                        "Detected Phone text with invalid content (resId=%s, value='%s') %s",
                        resourceId, value, w.get(AndroidTags.AndroidXpath));

                Visualizer visualizer = new RegionsVisualizer(
                        getRedPen(),
                        getWidgetRegions(Collections.singletonList(w)),
                        "Invariant Fault",
                        0.5, 0.5);

                Verdict phoneTextVerdict = new Verdict(
                        Verdict.Severity.WARNING_UI_ITEM_WRONG_VALUE_FAULT,
                        verdictMsg,
                        visualizer);
                verdicts.add(phoneTextVerdict);
            }
        }

        if (verdicts.isEmpty()) {
            return Collections.singletonList(Verdict.OK);
        }

        return verdicts;
    }
}
