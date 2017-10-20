package nl.ou.testar;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import org.fruit.alayer.Action;
import org.fruit.alayer.State;
import org.fruit.alayer.Tags;
import org.fruit.alayer.Widget;
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

    public void dropDatabase(){
        graphFactory.drop();
    }


    @Override
    public void addState(final State state) {
        long tStart = System.currentTimeMillis();
        OrientGraph graph = graphFactory.getTx();

        Vertex v = getStateVertex(state.get(Tags.ConcreteID), graph);
        if (v == null) {
            createStateVertex(state, graph);
        } else {
            int i = v.getProperty("visited");
            v.setProperty("visited", i + 1);
        }
        graph.commit();

        graph.shutdown();
        long tEnd = System.currentTimeMillis();
        LOGGER.info("[S<] # {} # stored in #{} # ms", state.get(Tags.ConcreteID),tEnd-tStart);
    }

    @Override
    public void addAction(final Action action, final String toStateID) {
        long tStart = System.currentTimeMillis();

        OrientGraph graph = graphFactory.getTx();
        try {
            Vertex vertexFrom = getWidgetVertex(action.get(Tags.TargetID), graph);
            if (vertexFrom == null) {
                throw new GraphDBException("Widget not found " + action.get(Tags.TargetID));
            }
            Vertex vertexTo = getStateVertex(toStateID, graph);
            if (vertexTo == null) {
                throw new GraphDBException("toState not found in database");
            }
            createActionEdge(vertexFrom, action, vertexTo, graph);
            graph.commit();
        } finally {
            graph.shutdown();
        }
        long tEnd = System.currentTimeMillis();
        LOGGER.info("[A<] # {} # stored in # {} # ms", action.get(Tags.ConcreteID),tEnd-tStart);
    }

    @Override
    public void addActionOnState(String stateId, Action action, String toStateID) {
        long tStart = System.currentTimeMillis();

        OrientGraph graph = graphFactory.getTx();
        try {
            Vertex vertexFrom = getStateVertex(stateId, graph);
            if (vertexFrom == null) {
                throw new GraphDBException("fromState not found in database");
            }
            Vertex vertexTo = getStateVertex(toStateID, graph);
            if (vertexTo == null) {
                throw new GraphDBException("toState not found in database");
            }
            createActionEdge(vertexFrom, action, vertexTo, graph);
            graph.commit();
        } finally {
            graph.shutdown();
        }
        long tEnd = System.currentTimeMillis();
        LOGGER.info("[A<] # {} stored in # {} # ms", action.get(Tags.ConcreteID),tEnd-tStart);
    }

    @Override
    public void addWidget(String stateID, Widget w) {
        long tStart = System.currentTimeMillis();

        OrientGraph graph = graphFactory.getTx();
        try {
            Vertex state = getStateVertex(stateID, graph);
            if (state == null) {
                throw new GraphDBException("state not found in database");
            }
            Vertex widget = getWidgetVertex(w.get(Tags.ConcreteID), graph);
            if (widget == null) {
                createWidgetVertex(stateID, w, graph);
            } else {
               //nothing
            }
            graph.commit();
        } finally {
            graph.shutdown();
        }
        long tEnd = System.currentTimeMillis();
        LOGGER.info("[W<] # {} # stored in # {} # ms", w.get(Tags.ConcreteID),tEnd-tStart);

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

    private void createWidgetVertex(final String widgetId, final Widget w, final OrientGraph graph) {
        Vertex vertex = graph.addVertex("class:Widget");
        w.tags().forEach(t -> vertex.setProperty(
                t.name().replace(',', '_'),
                w.get(t).toString()));
        Vertex state = getStateVertex(widgetId, graph);
        Edge edge = graph.addEdge(null, state, vertex, "has");
        LOGGER.debug("Widget {} Vertex created and connected to state via Edge {} ", vertex.getId(), edge.getId());
    }

    /**
     * Lookup state vertex in the database
     *
     * @param concreteID unique identification of the state
     * @param graph     handle to the graph database
     * @return the vertex of for the State object or null if the state is not found.
     */
    private Vertex getStateVertex(String concreteID, OrientGraph graph) {
        try {
            Iterable<Vertex> vertices = graph.getVertices("State." + Tags.ConcreteID, concreteID);
            Vertex vertex = vertices.iterator().next();
            LOGGER.debug("Vertex {} found", vertex.getId());
            return vertex;
        } catch (IllegalArgumentException | NoSuchElementException ex) {
            LOGGER.debug("There is no vertex inserted yet for the given State ConcreteID {}", concreteID);
            return null;
        }
    }

    /**
     * Lookup widget vertex in the database
     *
     * @param concrteID unique identification of the state
     * @param graph     handle to the graph database
     * @return the vertex of for the Widget object or null if the state is not found.
     */
    private Vertex getWidgetVertex(String concrteID, OrientGraph graph) {
        try {
            Iterable<Vertex> vertices = graph.getVertices("Widget." + Tags.ConcreteID, concrteID);
            Vertex vertex = vertices.iterator().next();
            LOGGER.debug("Vertex {} found", vertex.getId());
            return vertex;
        } catch (IllegalArgumentException | NoSuchElementException ex) {
            LOGGER.debug("There is no vertex inserted yet for the given Widget ConcreteID {}", concrteID);
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
        Edge edge = graph.addEdge(null, vFrom, vTo, "execute");
        action.tags().forEach(t -> edge.setProperty(
                t.name().replace(',', '_'),
                action.get(t).toString()));

    }

}
