package nl.ou.testar;

import org.fruit.alayer.Action;
import org.fruit.alayer.State;
import org.fruit.alayer.Widget;

import com.tinkerpop.blueprints.Vertex;

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
     * Add Action on a widget to the graph database as Edge
     *
     * @param action      The performed action
     * @param toStateID   ConcreteID of the new State
     */
    void addAction( final Action action, final String toStateID);

    /**
     * Add Action on a State to the graph database as Edge
     *
     * @param stateId id of the state on which the action is performed.
     * @param action the action.
     * @param toStateID the resulting state
     */
    void addActionOnState(final String stateId, final Action action, final String toStateID);


    /**
     * Add a widget to the datamodel.
     * @param stateID State to which the widget belongs
     * @param w The widget object
     */
    void addWidget(final String stateID, Widget w);
    
    Iterable<Vertex> getStateVertices();
}
