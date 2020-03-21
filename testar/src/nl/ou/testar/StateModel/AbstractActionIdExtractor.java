package nl.ou.testar.StateModel;

import nl.ou.testar.StateModel.Util.HydrationHelper;
import org.fruit.alayer.Action;
import org.fruit.alayer.Tags;
import javax.annotation.Nonnull;

/**
 * This class calculates the identifier for the abstract actions
 */
public class AbstractActionIdExtractor {

    private static ExtractionMode extractionMode;

    /**
     * This method returns the abstract action identifier to use in the state model.
     * @param abstractState
     * @param action
     * @return
     */
    public static String extract(@Nonnull AbstractState abstractState, @Nonnull Action action) {
        if (extractionMode == null) {
            throw new RuntimeException("Extraction mode was not set in abstract action id extractor.");
        }
        switch(extractionMode) {
            case SINGLE_STATE:
                return extract(abstractState, action, false);

            case PREVIOUS_STATE:
            case INCOMING_ACTION:
                return extract(abstractState, action, true);

            default:
                throw new RuntimeException("Extraction mode for abstract action id extractor is not supported");
        }
    }

    @Nonnull
    private static String extract(@Nonnull AbstractState abstractState, @Nonnull Action action, boolean useStateId) {
        // we concatenate the abstract state identifier to the calculated abstract action identifier
        return HydrationHelper.lowCollisionID((useStateId ? abstractState.getStateId() : "") + action.get(Tags.AbstractIDCustom));
    }

    /**
     * Sets the current extraction mode.
     * @param extractionMode
     */
    public static void setExtractionMode(ExtractionMode extractionMode) {
        AbstractActionIdExtractor.extractionMode = extractionMode;
    }
}
