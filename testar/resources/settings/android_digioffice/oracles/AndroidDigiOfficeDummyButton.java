package android_digioffice.oracles;

import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.Verdict;

import java.util.Collections;
import java.util.List;

public class AndroidDigiOfficeDummyButton extends AbstractAndroidDigiOfficeOracle {

    private boolean awaitingButtonStateChange = false;
    private String previousStateConcreteId = "";

    public AndroidDigiOfficeDummyButton() {
        super("AndroidDigiOfficeDummyButton");
    }

    public void activate(String previousStateConcreteId) {
        this.awaitingButtonStateChange = true;
        this.previousStateConcreteId = previousStateConcreteId == null ? "" : previousStateConcreteId;
    }

    public void deactivate() {
        this.awaitingButtonStateChange = false;
        this.previousStateConcreteId = "";
    }

    @Override
    protected boolean isApplicable(State state) {
        return this.awaitingButtonStateChange && !this.previousStateConcreteId.isEmpty();
    }

    @Override
    protected List<Verdict> check(State state) {
        String currentStateConcreteId = state.get(Tags.ConcreteID, "");
        if (this.previousStateConcreteId.equals(currentStateConcreteId)) {
            return Collections.singletonList(new Verdict(
                    Verdict.Severity.WARNING_UI_FLOW_FAULT,
                    "Detected AndroidButton click without state change. Previous and current state ConcreteID are equal: "
                            + currentStateConcreteId));
        }

        return Collections.singletonList(Verdict.OK);
    }
}
