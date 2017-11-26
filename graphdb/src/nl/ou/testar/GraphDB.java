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
public class GraphDB implements GraphDBRepository {
	
    public enum GremlinStart {
        VERTICES,
        EDGES;
    }

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
    @Override
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
    @Override
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
    @Override
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
    @Override
    public void addActionOnState(final String fromSateID, final Action action, final String toStateID) {
        if(enabled) {
            repository.addActionOnState(fromSateID, action, toStateID);
        }
    }
    
    @Override
    public List<Object> getObjectsFromGremlinPipe(String gremlin, GremlinStart start) {
        if(enabled) {
            return repository.getObjectsFromGremlinPipe(gremlin, start);
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
