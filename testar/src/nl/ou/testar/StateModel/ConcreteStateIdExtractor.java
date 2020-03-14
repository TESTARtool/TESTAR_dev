package nl.ou.testar.StateModel;

import nl.ou.testar.StateModel.Util.HydrationHelper;
import org.fruit.alayer.State;
import org.fruit.alayer.Tags;

import java.util.ArrayList;
import java.util.List;

import static nl.ou.testar.StateModel.ExtractionMode.ALL_STATES;
import static nl.ou.testar.StateModel.ExtractionMode.PREVIOUS_STATE;

public class ConcreteStateIdExtractor {

    // in what mode is the extractor to be run?
    private ExtractionMode extractionMode;

    private String previousStateId;

    // a list of state id strings that have been extracted
    private List<String> extractedStateIds;

    public ConcreteStateIdExtractor(ExtractionMode extractionMode) {
        this.extractionMode = extractionMode;
        if (extractionMode == ALL_STATES) {
            extractedStateIds = new ArrayList<>();
        }
        if (extractionMode == PREVIOUS_STATE) {
            previousStateId = "";
        }
        System.out.println("Starting concrete state id extractor in mode: " + extractionMode);
    }

    private String extractSingleState(State state) {
        return state.get(Tags.ConcreteIDCustom);
    }

    private String extractPreviousState(State state) {
        String concreteStateId = HydrationHelper.lowCollisionID(previousStateId + extractSingleState(state));
        previousStateId = extractSingleState(state);
        return concreteStateId;
    }

    private String extractAllStates(State state) {
        // this needs to be implemented
        // implementation is non-trivial, as loops need to be eliminated.
        // if we do not, every model will be of infinite size
        return "";
    }

    // todo this method needs verification
    private String extractIncomingAction(State state, ConcreteAction concreteAction) {
        String actionId = concreteAction.getActionId() == null ? "" : concreteAction.getActionId();
        return HydrationHelper.lowCollisionID(actionId + extractSingleState(state));
    }

    public String extractConcreteStateId(State state, ConcreteAction concreteAction) {
        switch (extractionMode) {
            case SINGLE_STATE:
                return extractSingleState(state);

            case PREVIOUS_STATE:
                return extractPreviousState(state);

            case ALL_STATES:
                return extractAllStates(state);

            case INCOMING_ACTION:
                return extractIncomingAction(state, concreteAction);

            default:
                // this means the extraction mode is null
                throw new RuntimeException("The concrete state id extractor was not initialized correctly");
        }
    }
}
