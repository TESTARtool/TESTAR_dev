import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.visualizers.RegionsVisualizer;
import org.testar.oracles.Oracle;
import org.testar.monkey.alayer.android.enums.AndroidTags;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AndroidDigiOfficeWebsiteTextIsNotValid implements Oracle {

    // Matches any resId that ends with:
    // person-detail-website-text
    // relation-detail-website-text
    // contact-person-detail-website-text
    // contact-person-detail-relation-website-text
    private static final java.util.regex.Pattern WEBSITE_WIDGET_ID_PATTERN = java.util.regex.Pattern
            .compile(".*(detail|detail-relation)-website-text.*");

    private boolean isInvalidValue(String value) {
        if (value.trim().isEmpty())
            return true;

        String upperValue = value.trim().toUpperCase(java.util.Locale.ROOT);

        return upperValue.contains("N/A")
                || upperValue.contains("N\\A");
    }

    @Override
    public void initialize() {
    }

    @Override
    public List<Verdict> getVerdicts(State state) {
        List<Verdict> verdicts = new ArrayList<>();

        for (Widget w : state) {
            String resId = w.get(AndroidTags.AndroidResourceId, "");

            if (!WEBSITE_WIDGET_ID_PATTERN.matcher(resId).matches()) {
                continue;
            }

            String value = w.get(AndroidTags.AndroidAccessibilityId, "");

            if (isInvalidValue(value)) {
                String verdictMsg = String.format(
                        "Detected Website text with invalid content (resId=%s, value='%s') %s",
                        resId, value, w.get(AndroidTags.AndroidXpath));

                Visualizer visualizer = new RegionsVisualizer(
                        getRedPen(),
                        getWidgetRegions(Collections.singletonList(w)),
                        "Invariant Fault",
                        0.5, 0.5);

                Verdict websiteTextVerdict = new Verdict(
                        Verdict.Severity.WARNING_UI_ITEM_WRONG_VALUE_FAULT,
                        verdictMsg,
                        visualizer);
                verdicts.add(websiteTextVerdict);
            }
        }

        if (verdicts.isEmpty()) {
            return Collections.singletonList(Verdict.OK);
        }

        return verdicts;
    }
}
