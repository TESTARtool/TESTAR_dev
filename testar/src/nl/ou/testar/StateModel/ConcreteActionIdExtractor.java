package nl.ou.testar.StateModel;

import nl.ou.testar.StateModel.Util.HydrationHelper;
import org.fruit.alayer.Action;
import org.fruit.alayer.Tags;

public class ConcreteActionIdExtractor {

    private static ExtractionMode extractionMode;

    /**
     * This method returns the concrete action id to use in the model.
     * @param concreteState
     * @param action
     * @return
     */
    public static String extract(ConcreteState concreteState, Action action) {
        if (extractionMode == null) {
            throw new RuntimeException("Extraction mode was not set in abstract action id extractor.");
        }
        switch(extractionMode) {
            case SINGLE_STATE:
                return extract(concreteState, action, false);

            case PREVIOUS_STATE:
            case INCOMING_ACTION:
                return extract(concreteState, action, true);

            default:
                throw new RuntimeException("Extraction mode for abstract action id extractor is not supported");
        }
    }

    private static String extract(ConcreteState concreteState, Action action, boolean useStateId) {
        return HydrationHelper.lowCollisionID((useStateId ? concreteState.getId() : "") + action.get(Tags.ConcreteIDCustom));
    }

    public static void setExtractionMode(ExtractionMode extractionMode) {
        ConcreteActionIdExtractor.extractionMode = extractionMode;
    }
}
