package nl.ou.testar.StateModel.Persistence.OrientDB;

import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import nl.ou.testar.StateModel.Exception.EntityNotFoundException;

import java.util.Collection;

public class EntityManager {

    // factory that will create graphs
    private final OrientGraphFactory graphFactory;

    /**
     * Constructor
     * @param connectionString
     * @param username
     * @param password
     */
    public EntityManager(final String connectionString, final String username, final String password) {
        graphFactory = new OrientGraphFactory(connectionString, username, password);
    }

    /**
     * Return a single Vertex value based on a filter value
     * @param filterField
     * @param filterValue
     * @return
     * @throws EntityNotFoundException
     */
    public Vertex getVertexWithFilter(String filterField, String filterValue) throws EntityNotFoundException {
        OrientGraph graph = graphFactory.getTx();
        Iterable<Vertex> vertices = graph.getVertices(filterField, filterValue);
        if (vertices instanceof Collection) {
            int size = ((Collection<?>) vertices).size();
            if (size == 0 || size > 1) {
                //@todo should split this into different exceptions at some point
                throw new EntityNotFoundException();
            }
        }
        return vertices.iterator().next();
    }



}
