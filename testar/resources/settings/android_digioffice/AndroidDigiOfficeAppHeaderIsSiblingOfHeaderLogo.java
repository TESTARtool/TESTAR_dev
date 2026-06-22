import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.visualizers.RegionsVisualizer;
import org.testar.oracles.Oracle;
import org.testar.monkey.alayer.android.enums.AndroidTags;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AndroidDigiOfficeAppHeaderIsSiblingOfHeaderLogo implements Oracle {

    private static final String APP_HEADER_RESOURCE_ID = "app-header";
    private static final String HEADER_LOGO_RESOURCE_ID = "header-logo";

    @Override
    public void initialize() {
    }

    @Override
    public List<Verdict> getVerdicts(State state) {
        List<Verdict> verdicts = new ArrayList<>();

        for (Widget widget : state) {
            String resourceId = widget.get(AndroidTags.AndroidResourceId, "");

            if (!APP_HEADER_RESOURCE_ID.equals(resourceId)) {
                continue;
            }

            if (!hasSiblingWithResourceId(widget, HEADER_LOGO_RESOURCE_ID)) {
                String verdictMsg = String.format(
                        "Detected app header without header logo sibling (resId=%s) %s",
                        resourceId,
                        widget.get(AndroidTags.AndroidXpath, ""));

                Visualizer visualizer = new RegionsVisualizer(
                        getRedPen(),
                        getWidgetRegions(Collections.singletonList(widget)),
                        "Invariant Fault",
                        0.5, 0.5);

                Verdict appHeaderSiblingVerdict = new Verdict(
                        Verdict.Severity.WARNING_UI_ITEM_WRONG_VALUE_FAULT,
                        verdictMsg,
                        visualizer);
                verdicts.add(appHeaderSiblingVerdict);
            }
        }

        if (verdicts.isEmpty()) {
            return Collections.singletonList(Verdict.OK);
        }

        return verdicts;
    }

    private boolean hasSiblingWithResourceId(Widget widget, String expectedSiblingResourceId) {
        if (widget.parent() == null) {
            return false;
        }

        Widget parent = widget.parent();
        for (int i = 0; i < parent.childCount(); i++) {
            Widget sibling = parent.child(i);

            if (sibling == null || sibling == widget) {
                continue;
            }

            String siblingResourceId = sibling.get(AndroidTags.AndroidResourceId, "");
            if (expectedSiblingResourceId.equals(siblingResourceId)) {
                return true;
            }
        }

        return false;
    }
}
