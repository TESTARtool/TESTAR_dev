package android_digioffice.oracles;

import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.visualizers.RegionsVisualizer;
import org.testar.monkey.alayer.android.enums.AndroidTags;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AndroidDigiOfficeHeaderIsNotEmptyNotNA extends AbstractAndroidDigiOfficeOracle {

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

    public AndroidDigiOfficeHeaderIsNotEmptyNotNA() {
        super("AndroidDigiOfficeHeaderIsNotEmptyNotNA");
    }

    private boolean isHeaderWidget(Widget widget) {
        String resourceId = widget.get(AndroidTags.AndroidResourceId, "");
        return HEADER_WIDGET_ID_PATTERN.matcher(resourceId).matches();
    }

    @Override
    protected boolean isApplicable(State state) {
        for (Widget w : state) {
            if (isHeaderWidget(w)) {
                return true;
            }
        }

        return false;
    }

    @Override
    protected List<Verdict> check(State state) {
        List<Verdict> verdicts = new ArrayList<>();

        for (Widget w : state) {
            if (!isHeaderWidget(w)) {
                continue;
            }

            String resourceId = w.get(AndroidTags.AndroidResourceId, "");
            String value = w.get(AndroidTags.AndroidText, "");

            if (isInvalidValue(value)) {
                String verdictMsg = String.format(
                        "Detected Header with invalid content (resId=%s, value='%s') %s",
                        resourceId, value, w.get(AndroidTags.AndroidXpath));

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
