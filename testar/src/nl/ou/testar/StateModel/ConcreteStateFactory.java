package nl.ou.testar.StateModel;

import es.upv.staq.testar.CodingManager;
import org.fruit.alayer.State;
import org.fruit.alayer.Tag;
import org.fruit.alayer.Tags;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;

import java.util.HashSet;
import java.util.Set;

public abstract class ConcreteStateFactory {

    /**
     * This builder method will create a new concrete state class and populate it with the needed data
     * @param newState the testar State to serve as a base
     * @param tags the tags containing the atributes that were used in the construction of the concrete state id
     * @return the new concrete state
     */
    public static ConcreteState createConcreteState(State newState, Set<Tag<?>> tags) {
        String concreteStateId = newState.get(Tags.ConcreteIDCustom);
        ConcreteState concreteState = new ConcreteState(concreteStateId, tags);

        // next we want to add all the attributes contained in the state, and then do the same thing for the child widgets
        for (Tag<?> t : newState.tags()) {
            concreteState.addAttribute(t, newState.get(t, null));
        }

        return concreteState;
    }

}
