package android_digioffice.oracles;

import org.testar.monkey.alayer.Rect;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.Verdict;
import org.testar.monkey.alayer.Visualizer;
import org.testar.monkey.alayer.Widget;
import org.testar.monkey.alayer.visualizers.RegionsVisualizer;
import org.testar.monkey.alayer.android.enums.AndroidTags;

import java.util.Collections;
import java.util.List;

public class AndroidDigiOfficeDummyButton extends AbstractAndroidDigiOfficeOracle {

    private boolean awaitingButtonStateChange = false;
    private String previousStateConcreteId = "";
    private String clickedWidgetResourceId = "";
    private String clickedWidgetText = "";
    private Rect clickedWidgetRect = null;

    public AndroidDigiOfficeDummyButton() {
        super("AndroidDigiOfficeDummyButton");
    }

    public void activate(String previousStateConcreteId, Widget clickedWidget) {
        this.awaitingButtonStateChange = true;
        this.previousStateConcreteId = previousStateConcreteId == null ? "" : previousStateConcreteId;
        if (clickedWidget == null) {
            this.clickedWidgetResourceId = "";
            this.clickedWidgetText = "";
            this.clickedWidgetRect = null;
            return;
        }

        this.clickedWidgetResourceId = clickedWidget.get(AndroidTags.AndroidResourceId, "");
        this.clickedWidgetText = clickedWidget.get(AndroidTags.AndroidText, "");
        Object clickedWidgetShape = clickedWidget.get(Tags.Shape, null);
        this.clickedWidgetRect = clickedWidgetShape instanceof Rect ? (Rect) clickedWidgetShape : null;
    }

    public void deactivate() {
        this.awaitingButtonStateChange = false;
        this.previousStateConcreteId = "";
        this.clickedWidgetResourceId = "";
        this.clickedWidgetText = "";
        this.clickedWidgetRect = null;
    }

    @Override
    protected boolean isApplicable(State state) {
        return this.awaitingButtonStateChange && !this.previousStateConcreteId.isEmpty();
    }

    @Override
    protected List<Verdict> check(State state) {
        String currentStateConcreteId = state.get(Tags.ConcreteID, "");
        if (this.previousStateConcreteId.equals(currentStateConcreteId)) {
            Visualizer visualizer = null;
            if (this.clickedWidgetRect != null) {
                visualizer = new RegionsVisualizer(
                        getRedPen(),
                        Collections.singletonList(this.clickedWidgetRect),
                        "Invariant Fault",
                        0.5, 0.5);
            }

            return Collections.singletonList(new Verdict(
                    Verdict.Severity.WARNING_UI_FLOW_FAULT,
                    "Detected AndroidButton click without state change. AndroidResourceId: "
                            + this.clickedWidgetResourceId + " , AndroidText: " + this.clickedWidgetText,
                    visualizer));
        }

        return Collections.singletonList(Verdict.OK);
    }
}
