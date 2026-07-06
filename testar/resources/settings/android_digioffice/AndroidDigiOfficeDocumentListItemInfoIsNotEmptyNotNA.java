import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.android.enums.AndroidTags;
import org.testar.monkey.alayer.visualizers.RegionsVisualizer;
import org.testar.oracles.Oracle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AndroidDigiOfficeDocumentListItemInfoIsNotEmptyNotNA implements Oracle {

    private static final java.util.regex.Pattern DOCUMENT_LIST_ITEM_WIDGET_ID_PATTERN = java.util.regex.Pattern
            .compile(".*dms-document-list-list-item-.+-(title|date|full-title|category).*");

    private boolean isInvalidValue(String value) {
        if (value.trim().isEmpty()) {
            return true;
        }

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

            if (!DOCUMENT_LIST_ITEM_WIDGET_ID_PATTERN.matcher(resId).matches()) {
                continue;
            }

            // The value can exist in the accessibility id or in the text content.
            String accessibilityValue = w.get(AndroidTags.AndroidAccessibilityId, "");
            String textValue = w.get(AndroidTags.AndroidText, "");

            String value = accessibilityValue.isEmpty() ? textValue : accessibilityValue;

            if (isInvalidValue(value)) {
                String verdictMsg = String.format(
                        "Detected document list item info with invalid content (resId=%s, value='%s') %s",
                        resId, value, w.get(AndroidTags.AndroidXpath));

                Visualizer visualizer = new RegionsVisualizer(
                        getRedPen(),
                        getWidgetRegions(Collections.singletonList(w)),
                        "Invariant Fault",
                        0.5, 0.5);

                Verdict documentListItemInfoVerdict = new Verdict(
                        Verdict.Severity.WARNING_UI_ITEM_WRONG_VALUE_FAULT,
                        verdictMsg,
                        visualizer);
                verdicts.add(documentListItemInfoVerdict);
            }
        }

        if (verdicts.isEmpty()) {
            return Collections.singletonList(Verdict.OK);
        }

        return verdicts;
    }
}
