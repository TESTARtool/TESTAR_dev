package nl.ou.testar;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Element;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.gremlin.groovy.Gremlin;
import com.tinkerpop.pipes.Pipe;

import nl.ou.testar.GraphDB.GremlinStart;

import org.fruit.alayer.Action;
import org.fruit.alayer.State;
import org.fruit.alayer.Tag;
import org.fruit.alayer.Tags;
import org.fruit.alayer.Widget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
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

        LOGGER.info("Store Action {} ({}) from {} to {}",
                action.get(Tags.ConcreteID), action.get(Tags.Desc, ""), action.get(Tags.TargetID), toStateID);

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
        LOGGER.info("Store Action {} ({}) from {} to {}",
                action.get(Tags.ConcreteID), action.get(Tags.Desc, ""), stateId, toStateID);
        long tStart= System.currentTimeMillis();

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
        LOGGER.info("Add Widget {} with id {} to state {}", w.get(Tags.Desc, ""), w.get(Tags.ConcreteID), stateID);
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
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public List<Object> getObjectsFromGremlinPipe(String gremlin, GremlinStart start) {
       	try {
       	    Pipe pipe = Gremlin.compile(gremlin);
       	    OrientGraph graph = graphFactory.getTx();
       	    if (start.equals(GremlinStart.VERTICES))
       	        pipe.setStarts(graph.getVertices());
       	    else
       	        pipe.setStarts(graph.getEdges());
       	    List<Object> ret = new ArrayList<>();
       	    for (Object o : pipe)
                ret.add(o);
            graph.shutdown();
       	    return ret;
        }
       	catch (Exception e) {
            LOGGER.error("Gremlin exception: {}", e.getMessage());
            return new ArrayList<Object>();
       	}
    }

    /**
     * Create new State Vertex
     *
     * @param state State which needs to be stored as a vertex
     * @param graph Handle to the graph database.
     */
    private void createStateVertex(final State state, final OrientGraph graph) {
        Vertex vertex = graph.addVertex("class:State");
        for (Tag<?> t : state.tags())
            setProperty(t, state.get(t), vertex);
        vertex.setProperty("visited", 1);
    }

    private void createWidgetVertex(final String widgetId, final Widget w, final OrientGraph graph) {
        Vertex vertex = graph.addVertex("class:Widget");
        for (Tag<?> t : w.tags())
            setProperty(t, w.get(t), vertex);
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
        for (Tag<?> t : action.tags())
            setProperty(t, action.get(t), edge);
    }
    
    private void setProperty(Tag<?> t, Object o, Element el) {
    	String name = t.name().replace(',', '_');
    	// TODO: is there a more sophisticated way to do this?
    	if (o instanceof Boolean)
    		el.setProperty(name, ((Boolean)o).booleanValue());
    	else if (o instanceof Byte)
    		el.setProperty(name, ((Byte)o).byteValue());
    	else if (o instanceof Character)
    		el.setProperty(name, ((Character)o).charValue());
    	else if (o instanceof Double)
    		el.setProperty(name, ((Double)o).doubleValue());
    	else if (o instanceof Float)
    		el.setProperty(name, ((Float)o).floatValue());
    	else if (o instanceof Integer)
    		el.setProperty(name, ((Integer)o).intValue());
    	else if (o instanceof Long)
    		el.setProperty(name, ((Long)o).longValue());
    	else if (o instanceof Short)
    		el.setProperty(name, ((Short)o).shortValue());
    	else
    		el.setProperty(name, o.toString());
    }

}
