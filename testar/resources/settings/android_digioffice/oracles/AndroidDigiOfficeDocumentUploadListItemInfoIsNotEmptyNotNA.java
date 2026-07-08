package android_digioffice.oracles;

import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.android.enums.AndroidTags;
import org.testar.monkey.alayer.visualizers.RegionsVisualizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AndroidDigiOfficeDocumentUploadListItemInfoIsNotEmptyNotNA extends AbstractAndroidDigiOfficeOracle {

    private static final java.util.regex.Pattern DOCUMENT_UPLOAD_LIST_ITEM_WIDGET_ID_PATTERN = java.util.regex.Pattern
            .compile(".*dms-documentUpload-list-list-item-.+-(title|date|uploaded-by-label|uploaded-by).*");

    private boolean isInvalidValue(String value) {
        if (value.trim().isEmpty()) {
            return true;
        }

        String upperValue = value.trim().toUpperCase(java.util.Locale.ROOT);

        return upperValue.equals("-")
                || upperValue.contains("N/A")
                || upperValue.contains("N\\A");
    }

    public AndroidDigiOfficeDocumentUploadListItemInfoIsNotEmptyNotNA() {
        super("AndroidDigiOfficeDocumentUploadListItemInfoIsNotEmptyNotNA");
    }

    private boolean isDocumentUploadListItemInfoWidget(Widget widget) {
        String resourceId = widget.get(AndroidTags.AndroidResourceId, "");
        return DOCUMENT_UPLOAD_LIST_ITEM_WIDGET_ID_PATTERN.matcher(resourceId).matches();
    }

    @Override
    protected boolean isApplicable(State state) {
        for (Widget w : state) {
            if (isDocumentUploadListItemInfoWidget(w)) {
                return true;
            }
        }

        return false;
    }

    @Override
    protected List<Verdict> check(State state) {
        List<Verdict> verdicts = new ArrayList<>();

        for (Widget w : state) {
            if (!isDocumentUploadListItemInfoWidget(w)) {
                continue;
            }

            String resourceId = w.get(AndroidTags.AndroidResourceId, "");
            // The value can exist in the accessibility id or in the text content.
            String accessibilityValue = w.get(AndroidTags.AndroidAccessibilityId, "");
            String textValue = w.get(AndroidTags.AndroidText, "");

            String value = accessibilityValue.isEmpty() ? textValue : accessibilityValue;

            if (isInvalidValue(value)) {
                String verdictMsg = String.format(
                        "Detected document upload list item info with invalid content (resId=%s, value='%s') %s",
                        resourceId, value, w.get(AndroidTags.AndroidXpath));

                Visualizer visualizer = new RegionsVisualizer(
                        getRedPen(),
                        getWidgetRegions(Collections.singletonList(w)),
                        "Invariant Fault",
                        0.5, 0.5);

                Verdict documentUploadListItemInfoVerdict = new Verdict(
                        Verdict.Severity.WARNING_UI_ITEM_WRONG_VALUE_FAULT,
                        verdictMsg,
                        visualizer);
                verdicts.add(documentUploadListItemInfoVerdict);
            }
        }

        if (verdicts.isEmpty()) {
            return Collections.singletonList(Verdict.OK);
        }

        return verdicts;
    }
}
