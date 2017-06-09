package nl.ou.testar;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import org.fruit.alayer.Action;
import org.fruit.alayer.State;
import org.fruit.alayer.Tags;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.NoSuchElementException;

/**
 * Implementation of the interaction with the Graph database. This class is implemented as a Singleton.
 * Created by florendegier on 03/06/2017.
 */
class OrientDBRepository implements GraphDBRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrientDBRepository.class);

    private final OrientGraphFactory graphFactory;


    OrientDBRepository(final String url, final String userName, final String password) {
        graphFactory = new OrientGraphFactory(url, userName, password);

    }


    @Override
    public void addState(final State state) {
        LOGGER.info("Store state {}", state.get(Tags.ConcreteID));
        OrientGraph graph = graphFactory.getTx();
        try {
            Vertex v = getStateVertex(state.get(Tags.ConcreteID), graph);
            if (v == null) {
                createStateVertex(state, graph);
            } else {
                int i = v.getProperty("visited");
                v.setProperty("visited", i + 1);
            }
            graph.commit();
        } finally {
            graph.shutdown();
        }
        LOGGER.info("state {} stored", state.get(Tags.ConcreteID));
    }

    @Override
    public void addAction(final String fromStateID, final Action action, final String toStateID) {

        LOGGER.info("Store Action {} from {} to {}",
                action.get(Tags.ConcreteID), fromStateID, toStateID);

        OrientGraph graph = graphFactory.getTx();
        try {
            Vertex vertexFrom = getStateVertex(fromStateID, graph);
            if (vertexFrom == null) {
                throw new GraphDBException("fromState not found in database");
            }
            Vertex vertexTo = getStateVertex(toStateID, graph);
            if (vertexTo == null) {
                throw new GraphDBException("toState not found in database");
            }
            createActionEdge(vertexFrom, action, vertexTo, graph);
        } finally {
            graph.shutdown();
        }

        LOGGER.info("Action {} stored", action.get(Tags.ConcreteID));
    }

    /**
     * Create new State Vertex
     *
     * @param state State which needs to be stored as a vertex
     * @param graph Handle to the graph database.
     */
    private void createStateVertex(final State state, final OrientGraph graph) {
        Vertex vertex = graph.addVertex("class:State");
        state.tags().forEach(t -> vertex.setProperty(
                t.name().replace(',', '_'),
                state.get(t).toString()));
        vertex.setProperty("visited", 1);
    }

    /**
     * Lookup state vertex in the database
     *
     * @param concrteID unique identification of the state
     * @param graph     handle to the graph database
     * @return the vertex of for the State object or null if the state is not found.
     */
    private Vertex getStateVertex(String concrteID, OrientGraph graph) {
        try {
            Iterable<Vertex> vertices = graph.getVertices("State." + Tags.ConcreteID, concrteID);
            Vertex vertex = vertices.iterator().next();
            LOGGER.debug("Vertex {} found", vertex.getId());
            return vertex;
        } catch (IllegalArgumentException | NoSuchElementException ex) {
            LOGGER.debug("There is no vertex inserted yet for the given State ConcreteID {}", concrteID);
            return null;
        }
    }

    /**
     * Add new Action Edge to Graph Database.
     *
     * @param vFrom  source State
     * @param action the action performed
     * @param vTo    target State
     * @param graph  Reference to the Graph database.
     */
    private void createActionEdge(final Vertex vFrom, final Action action, final Vertex vTo, final Graph graph) {
        Edge edge = graph.addEdge(null, vFrom, vTo, action.get(Tags.ConcreteID));
        action.tags().forEach(t -> edge.setProperty(
                t.name().replace(',', '_'),
                action.get(t).toString()));
    }

}
