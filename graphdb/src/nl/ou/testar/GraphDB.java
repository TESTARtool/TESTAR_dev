package nl.ou.testar;


import java.util.ArrayList;
import java.util.List;

import org.fruit.alayer.Action;
import org.fruit.alayer.State;
import org.fruit.alayer.Widget;

/**
 * Wrapper for interaction with the Graph Database
 * Created by floren on 5-6-2017.
 */
public class GraphDB {


    private final boolean enabled;
    private GraphDBRepository repository;

    public GraphDB(final boolean enabled, final String url, final String userName, final String password) {
        this.enabled = enabled;
        if(enabled) {
            repository = new OrientDBRepository(url, userName, password);
        }
    }

    /**
     * Store the State in the graph database.
     * @param state state to store.
     */
    public void addState(final State state) {
        if(enabled) {
            repository.addState(state);
        }
    }


    /**
     * Add Widget to the graph database. A widget will be identified by it's concrete id.
     * @param statedID State to which the widget belongs
     * @param widget The widget to add
     */
    public void addWidget(final String statedID, final Widget widget) {
        if(enabled) {
            repository.addWidget(statedID,widget);
        }
    }

    /**
     * Store an action in the graph database.
     * @param action the action performed
     * @param toStateId the new state.
     */
    public void addAction(final Action action, final String toStateId) {
        if(enabled) {
            repository.addAction(action, toStateId);
        }
    }

    /**
     * Store an action without an targetID (widget). It's assumed that the action operates on the State it was
     * fired from.
     * @param fromSateID id of the original state
     * @param action The action performed
     * @param toStateID the resulting stateId
     */
    public void addActionOnState(final String fromSateID, final Action action, final String toStateID) {
        if(enabled) {
            repository.addActionOnState(fromSateID, action, toStateID);
        }
    }
    
    /**
     * Get all objects from a pipe specified by a Gremlin-Groovy expression
     * @param gremlin The Gremlin-Groovy expression.
     * @return A list of all objects in the pipe.
     */
    public List<Object> getObjectsFromGremlinPipe(String gremlin) {
        if(enabled) {
            return repository.getObjectsFromGremlinPipe(gremlin);
        }
        return new ArrayList<Object>();
    }

    /**
     * Setter only used in test.
     * @param repo Mock repository
     */
    void setRepository(GraphDBRepository repo) {
        repository = repo;
    }

}
