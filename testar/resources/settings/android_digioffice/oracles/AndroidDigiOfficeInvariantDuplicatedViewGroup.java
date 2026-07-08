package android_digioffice.oracles;

import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.visualizers.RegionsVisualizer;
import org.testar.monkey.alayer.android.enums.AndroidRoles;
import org.testar.monkey.alayer.android.enums.AndroidTags;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AndroidDigiOfficeInvariantDuplicatedViewGroup extends AbstractAndroidDigiOfficeOracle {

    public AndroidDigiOfficeInvariantDuplicatedViewGroup() {
        super("AndroidDigiOfficeInvariantDuplicatedViewGroup");
    }

    private boolean isCandidateViewGroup(Widget widget) {
        return widget.get(Tags.Role, Roles.Widget).equals(AndroidRoles.AndroidViewGroup)
                && !widget.get(AndroidTags.AndroidAccessibilityId, "").isEmpty()
                && widget.get(AndroidTags.AndroidClickable, false)
                && widget.parent() != null;
    }

    @Override
    protected boolean isApplicable(State state) {
        for (Widget w : state) {
            if (isCandidateViewGroup(w)) {
                return true;
            }
        }

        return false;
    }

    @Override
    protected List<Verdict> check(State state) {
        List<Verdict> verdicts = new ArrayList<>();

        // 1) Join clickable AndroidViewGroup with non-empty accessibility id and a
        // parent
        List<Widget> viewGroupList = new ArrayList<>();
        for (Widget w : state) {
            if (isCandidateViewGroup(w)) {
                viewGroupList.add(w);
            }
        }

        // 2) Group by parent and detect duplicates of AndroidAccessibilityId
        Map<Widget, Map<String, List<Widget>>> parentToWidgets = new HashMap<>();

        for (Widget w : viewGroupList) {
            Widget parent = w.parent();
            String accessId = w.get(AndroidTags.AndroidAccessibilityId, "");

            Map<String, List<Widget>> idToWidgets = parentToWidgets.computeIfAbsent(parent, p -> new HashMap<>());

            List<Widget> widgetsForId = idToWidgets.computeIfAbsent(accessId, id -> new ArrayList<>());

            widgetsForId.add(w);
        }

        // Now collect those with size > 1
        List<Widget> duplicatedViewGroupWidgets = new ArrayList<>();
        Set<String> duplicatedAccessIds = new HashSet<>();

        for (Map<String, List<Widget>> idToWidgets : parentToWidgets.values()) {
            for (Map.Entry<String, List<Widget>> entry : idToWidgets.entrySet()) {
                if (entry.getValue().size() > 1) {
                    duplicatedAccessIds.add(entry.getKey());
                    duplicatedViewGroupWidgets.addAll(entry.getValue());
                }
            }
        }

        // If the list of duplicated accessId list is not empty
        if (!duplicatedAccessIds.isEmpty()) {
            String verdictMsg = String.format(
                    "Detected duplicate accessibility IDs in ViewGroup(s) for widgets: %s ",
                    duplicatedAccessIds);

            Visualizer visualizer = new RegionsVisualizer(
                    getRedPen(),
                    getWidgetRegions(duplicatedViewGroupWidgets),
                    "Invariant Fault",
                    0.5, 0.5);

            Verdict duplicatedVerdict = new Verdict(
                    Verdict.Severity.WARNING_DUPLICATED_RESOURCE_ISSUE,
                    verdictMsg, visualizer);
            verdicts.add(duplicatedVerdict);
        }

        if (verdicts.isEmpty()) {
            return Collections.singletonList(Verdict.OK);
        }

        return verdicts;
    }

}
