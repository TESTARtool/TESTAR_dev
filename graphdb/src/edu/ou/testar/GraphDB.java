package edu.ou.testar;

import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import org.fruit.alayer.Action;
import org.fruit.alayer.State;
import org.fruit.alayer.Widget;

import javax.naming.OperationNotSupportedException;

/**
 * Wrapper for interaction with the Graph Database
 * Created by floren on 5-6-2017.
 */
public class GraphDB {


    private boolean enabled;
    private OrientDBRepository repository;

    public GraphDB(final boolean enabled, final String url, final String userName, final String password) {
        this.enabled = enabled;
        OrientGraphFactory factory = new OrientGraphFactory(url,userName,"orientdb");
        repository = new OrientDBRepository(factory);
    }


    public void addState(final State state) {
        if(enabled) {
            repository.addState(state);
        }
    }

    public void addWidget(final Widget widget) throws OperationNotSupportedException {
        throw new OperationNotSupportedException("Function is not implemented yet");
    }

    public void addAction(final Action action) throws OperationNotSupportedException {
        throw new OperationNotSupportedException("Function is not implemented yet");
    }

}
