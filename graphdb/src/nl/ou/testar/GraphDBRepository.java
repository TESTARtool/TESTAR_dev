package nl.ou.testar;

import org.fruit.alayer.Action;
import org.fruit.alayer.State;

/**
 * Repository API
 * Created by floren on 9-6-2017.
 */
public interface GraphDBRepository {

    /**
     * Store State in Graph database.
     *
     * @param state State of the SUT for this step.
     */
    void addState(final State state);

    /**
     * Add Action to the graph database as Edge
     *
     * @param fromStateID ConcreteID of the original State
     * @param action      The performed action
     * @param toStateID   ConcreteID of the new State
     */
    void addAction(final String fromStateID, final Action action, final String toStateID);
}
