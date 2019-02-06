package nl.ou.testar.StateModel;

import org.fruit.alayer.Action;
import org.fruit.alayer.Tags;

public class ConcreteActionFactory {

    public static ConcreteAction createConcreteAction(Action action, AbstractAction abstractAction) {
        return new ConcreteAction(action.get(Tags.ConcreteID), abstractAction);
    }

}
