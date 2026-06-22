import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.visualizers.RegionsVisualizer;
import org.testar.oracles.Oracle;
import org.testar.monkey.alayer.android.enums.AndroidTags;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AndroidDigiOfficeHeaderIsNotEmptyNotNA implements Oracle {

    // Matches any resId that ends with:
    // header-title
    private static final java.util.regex.Pattern HEADER_WIDGET_ID_PATTERN = java.util.regex.Pattern
            .compile(".*header-title.*");

    private boolean isInvalidValue(String value) {
        if (value.trim().isEmpty())
            return true;

        String upperValue = value.trim().toUpperCase(java.util.Locale.ROOT);

        return upperValue.equals("-")
                || upperValue.contains("N/A")
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

            if (!HEADER_WIDGET_ID_PATTERN.matcher(resId).matches()) {
                continue;
            }

            String value = w.get(AndroidTags.AndroidText, "");

            if (isInvalidValue(value)) {
                String verdictMsg = String.format(
                        "Detected Header with invalid content (resId=%s, value='%s') %s",
                        resId, value, w.get(AndroidTags.AndroidXpath));

                Visualizer visualizer = new RegionsVisualizer(
                        getRedPen(),
                        getWidgetRegions(Collections.singletonList(w)),
                        "Invariant Fault",
                        0.5, 0.5);

                Verdict headerVerdict = new Verdict(
                        Verdict.Severity.WARNING_UI_ITEM_WRONG_VALUE_FAULT,
                        verdictMsg,
                        visualizer);
                verdicts.add(headerVerdict);
            }
        }

        if (verdicts.isEmpty()) {
            return Collections.singletonList(Verdict.OK);
        }

        return verdicts;
    }
}
