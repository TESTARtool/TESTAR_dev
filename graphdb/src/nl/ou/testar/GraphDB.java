package nl.ou.testar;


import org.fruit.alayer.Action;
import org.fruit.alayer.State;
import org.fruit.alayer.Widget;

import javax.naming.OperationNotSupportedException;

/**
 * Wrapper for interaction with the Graph Database
 * Created by floren on 5-6-2017.
 */
public class GraphDB {


    private final boolean enabled;
    private GraphDBRepository repository;

    public GraphDB(final boolean enabled, final String url, final String userName, final String password) {
        this.enabled = enabled;
        repository = new OrientDBRepository(url,userName,"orientdb");
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
     * Store an action in the graph database.
     * @param fromStateId state on which the action will be applied
     * @param action the action performed
     * @param toStateId the new state.
     */
    public void addAction(final String fromStateId, final Action action, final String toStateId) {
        if(enabled) {
            repository.addAction(fromStateId, action, toStateId);
        }
    }

    /**
     * Setter only used in test.
     * @param repo Mock repository
     */
    void setRepository(GraphDBRepository repo) {
        repository = repo;
    }

}
