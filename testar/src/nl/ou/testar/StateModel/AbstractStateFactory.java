package nl.ou.testar.StateModel;

import es.upv.staq.testar.CodingManager;
import nl.ou.testar.StateModel.Util.ActionHelper;
import org.fruit.alayer.Action;
import org.fruit.alayer.State;
import org.fruit.alayer.Tag;
import org.fruit.alayer.Tags;

import java.util.Set;

public abstract class AbstractStateFactory {

    /**
     * This builder method will create a new abstract state class and populate it with the needed data
     * @param newState the testar State to serve as a base
     * @param actions a set of Testar actions
     * @return the new abstract state
     */
    public static AbstractState createAbstractState(State newState, Set<Action> actions) {
        String abstractStateId = newState.get(Tags.Abstract_R_ID);
        AbstractState newAbstractState = new AbstractState(abstractStateId, ActionHelper.convertActionsToAbstractActions(actions));
        // add the needed tags and their values to the abstract state
//        for (Tag<?> tag : CodingManager.getCustomTagsForAbstractId()) {
//            newAbstractState.addAttribute(tag, newState.get(tag));
//        }
        return newAbstractState;
    }

}
