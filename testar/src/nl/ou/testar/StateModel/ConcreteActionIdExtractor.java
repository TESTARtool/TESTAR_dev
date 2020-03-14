package nl.ou.testar.StateModel;

import nl.ou.testar.StateModel.Util.HydrationHelper;
import org.fruit.alayer.Action;
import org.fruit.alayer.Tags;

public class ConcreteActionIdExtractor {

    /**
     * This method returns the concrete action id to use in the model.
     * @param concreteState
     * @param action
     * @return
     */
    public static String extract(ConcreteState concreteState, Action action) {
        return HydrationHelper.lowCollisionID(concreteState.getId() + action.get(Tags.ConcreteIDCustom));
    }

}
