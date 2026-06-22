import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.visualizers.RegionsVisualizer;
import org.testar.oracles.Oracle;
import org.testar.monkey.alayer.android.enums.AndroidTags;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AndroidDigiOfficePhoneTextIsNotValid implements Oracle {

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

    @Override
    public void initialize() {
    }

    @Override
    public List<Verdict> getVerdicts(State state) {
        List<Verdict> verdicts = new ArrayList<>();

        for (Widget w : state) {
            String resId = w.get(AndroidTags.AndroidResourceId, "");

            if (!PHONE_WIDGET_ID_PATTERN.matcher(resId).matches()) {
                continue;
            }

            String value = w.get(AndroidTags.AndroidAccessibilityId, "");

            if (isInvalidValue(value)) {
                String verdictMsg = String.format(
                        "Detected Phone text with invalid content (resId=%s, value='%s') %s",
                        resId, value, w.get(AndroidTags.AndroidXpath));

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
