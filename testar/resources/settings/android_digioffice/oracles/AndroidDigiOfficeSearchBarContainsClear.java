package android_digioffice.oracles;

import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.visualizers.RegionsVisualizer;
import org.testar.monkey.alayer.android.enums.AndroidTags;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AndroidDigiOfficeSearchBarContainsClear extends AbstractAndroidDigiOfficeOracle {

    private static final java.util.regex.Pattern SEARCH_WIDGET_ID_PATTERN = java.util.regex.Pattern
            .compile(".*list-search$");

    private static final java.util.regex.Pattern CLEAR_WIDGET_ID_PATTERN = java.util.regex.Pattern
            .compile(".*list-clear.*");

    public AndroidDigiOfficeSearchBarContainsClear() {
        super("AndroidDigiOfficeSearchBarContainsClear");
    }

    private boolean isSearchWidget(Widget widget) {
        String resourceId = widget.get(AndroidTags.AndroidResourceId, "");
        return SEARCH_WIDGET_ID_PATTERN.matcher(resourceId).matches();
    }

    @Override
    protected boolean isApplicable(State state) {
        for (Widget w : state) {
            if (isSearchWidget(w)) {
                return true;
            }
        }

        return false;
    }

    @Override
    protected List<Verdict> check(State state) {
        List<Verdict> verdicts = new ArrayList<>();

        for (Widget w : state) {
            if (!isSearchWidget(w)) {
                continue;
            }

            String resourceId = w.get(AndroidTags.AndroidResourceId, "");
            if (!containsClearRecursive(w, CLEAR_WIDGET_ID_PATTERN)) {
                String verdictMsg = String.format(
                        "Detected Search element without clear option (resId=%s) %s",
                        resourceId,
                        w.get(AndroidTags.AndroidXpath, ""));

                Visualizer visualizer = new RegionsVisualizer(
                        getRedPen(),
                        getWidgetRegions(java.util.Collections.singletonList(w)),
                        "Invariant Fault",
                        0.5, 0.5);

                Verdict searchBarVerdict = new Verdict(
                        Verdict.Severity.WARNING_UI_ITEM_WRONG_VALUE_FAULT,
                        verdictMsg,
                        visualizer);
                verdicts.add(searchBarVerdict);
            }
        }

        if (verdicts.isEmpty()) {
            return Collections.singletonList(Verdict.OK);
        }

        return verdicts;
    }

    private boolean containsClearRecursive(Widget w, java.util.regex.Pattern clearPattern) {
        String id = w.get(AndroidTags.AndroidResourceId, "");
        if (clearPattern.matcher(id).matches())
            return true;

        for (int i = 0; i < w.childCount(); i++) {
            if (containsClearRecursive(w.child(i), clearPattern)) {
                return true;
            }
        }
        return false;
    }
}
