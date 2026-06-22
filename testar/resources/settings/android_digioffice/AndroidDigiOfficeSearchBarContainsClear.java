import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.visualizers.RegionsVisualizer;
import org.testar.oracles.Oracle;
import org.testar.monkey.alayer.android.enums.AndroidTags;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AndroidDigiOfficeSearchBarContainsClear implements Oracle {

    private static final java.util.regex.Pattern SEARCH_WIDGET_ID_PATTERN = java.util.regex.Pattern
            .compile(".*list-search$");

    private static final java.util.regex.Pattern CLEAR_WIDGET_ID_PATTERN = java.util.regex.Pattern
            .compile(".*list-clear.*");

    @Override
    public void initialize() {
    }

    @Override
    public List<Verdict> getVerdicts(State state) {
        List<Verdict> verdicts = new ArrayList<>();

        for (Widget w : state) {
            String resId = w.get(AndroidTags.AndroidResourceId, "");

            if (!SEARCH_WIDGET_ID_PATTERN.matcher(resId).matches()) {
                continue;
            }

            if (!containsClearRecursive(w, CLEAR_WIDGET_ID_PATTERN)) {
                String verdictMsg = String.format(
                        "Detected Search element without clear option (resId=%s) %s",
                        resId,
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
