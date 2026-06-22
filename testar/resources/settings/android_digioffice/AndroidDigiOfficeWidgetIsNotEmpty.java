import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.visualizers.RegionsVisualizer;
import org.testar.oracles.Oracle;
import org.testar.monkey.alayer.android.enums.AndroidTags;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AndroidDigiOfficeWidgetIsNotEmpty implements Oracle {

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

    @Override
    public void initialize() {
    }

    @Override
    public List<Verdict> getVerdicts(State state) {
        List<Verdict> verdicts = new ArrayList<>();

        for (Widget w : state) {
            String resId = w.get(AndroidTags.AndroidResourceId, "");

            if (!WIDGET_ID_PATTERN.matcher(resId).matches()) {
                continue;
            }

            String value = w.get(AndroidTags.AndroidAccessibilityId, "");

            if (isInvalidValue(value)) {
                String verdictMsg = String.format(
                        "Detected widget with invalid content (resId=%s, value='%s') %s",
                        resId, value, w.get(AndroidTags.AndroidXpath));

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
