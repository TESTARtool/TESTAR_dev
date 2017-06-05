package edu.ou.testar;

import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import org.fruit.alayer.State;
import org.fruit.alayer.Tags;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.NoSuchElementException;

/**
 * Implementation of the interaction with the Graph database. This class is implemented as a Singleton.
 * Created by florendegier on 03/06/2017.
 */
public class OrientDBRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrientDBRepository.class);

    private final OrientGraphFactory graphFactory;


    public OrientDBRepository(final OrientGraphFactory factory) {
        graphFactory = factory;
    }

    /**
     * Store State in Graph database.
     * @param state State of the SUT for this step.
     */
    public void addState(final State state) {
        LOGGER.info("Store state {}",state.get(Tags.ConcreteID));
        OrientGraph graph = graphFactory.getTx();
        try {
            Vertex v = getStateVertex(state.get(Tags.ConcreteID), graph);
            if (v == null) {
               createStateVertex(state,graph);
            } else {
                int i = v.getProperty("visited");
                v.setProperty("visited", i + 1);
            }
            graph.commit();
        } finally {
            graph.shutdown();
        }
        LOGGER.info("state {} stored",state.get(Tags.ConcreteID));
    }

    /**
     * Create new State Vertex
     * @param state State which needs to be stored as a vertex
     * @param graph Handle to the graph database.
     */
    private void createStateVertex(final State state, final OrientGraph graph){
        Vertex vertex = graph.addVertex("class:State");
        state.tags().forEach(t -> vertex.setProperty(t.name().replace(',', '_'), state.get(t)));
        vertex.setProperty("visited", 1);
    }

    /**
     * Lookup state vertex in the database
     * @param concrteID unique identification of the state
     * @param graph handle to the graph database
     * @return the vertex of for the State object or null if the state is not found.
     */
    private Vertex getStateVertex(String concrteID, OrientGraph graph) {
        try {
            Iterable<Vertex> vertices = graph.getVertices("State." + Tags.ConcreteID, concrteID);
            Vertex vertex = vertices.iterator().next();
            LOGGER.debug("Vertex {} found",vertex.getId());
            return vertex;
        } catch (IllegalArgumentException | NoSuchElementException ex) {
            LOGGER.debug("There is no vertex inserted yet for the given State ConcreteID {}", concrteID);
            return null;
        }
    }

}
