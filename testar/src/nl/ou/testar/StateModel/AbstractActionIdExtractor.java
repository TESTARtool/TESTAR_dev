package nl.ou.testar.StateModel;

import nl.ou.testar.StateModel.Util.HydrationHelper;
import org.fruit.alayer.Action;
import org.fruit.alayer.Tags;
import javax.annotation.Nonnull;

/**
 * This class calculates the identifier for the abstract actions
 */
public class AbstractActionIdExtractor {

    /**
     * This method returns the abstract action identifier to use in the state model.
     * @param abstractState
     * @param action
     * @return
     */
    @Nonnull
    public static String extract(@Nonnull AbstractState abstractState, @Nonnull Action action, boolean useStateId) {
        // we concatenate the abstract state identifier to the calculated abstract action identifier
        return HydrationHelper.lowCollisionID((useStateId ? abstractState.getStateId() : "") + action.get(Tags.AbstractIDCustom));
    }

}
