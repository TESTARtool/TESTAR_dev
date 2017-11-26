package nl.ou.testar;

/************************************************************************************
 *
 * COPYRIGHT (2017):
 *
 * Open Universiteit
 * www.ou.nl<http://www.ou.nl>
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 3. Neither the name of mosquitto nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 ************************************************************************************/

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import org.fruit.alayer.*;
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
                Vertex wv = createWidgetVertex(stateID, w, graph);
                bindToAbstractRole(wv,w,graph);
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

    @Override
    public Iterable<Vertex> getStateVertices() {
    	OrientGraph graph = graphFactory.getTx();
    	return graph.getVerticesOfClass("State");
    	// TODO: when/where to shutdown graph?
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

    private Vertex createWidgetVertex(final String widgetId, final Widget w, final OrientGraph graph) {
        Vertex vertex = graph.addVertex("class:Widget");
        w.tags().forEach(t -> vertex.setProperty(
                t.name().replace(',', '_'),
                w.get(t).toString()));
        Vertex state = getStateVertex(widgetId, graph);
        Edge edge = graph.addEdge(null, state, vertex, "has");
        graph.commit();
        LOGGER.debug("Widget {} Vertex created and connected to state via Edge {} ", vertex.getId(), edge.getId());
        return vertex;
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

    /**
     * Bind the Widget to it's abstract counter parts
     * @param wv the vertex of the widget to bind.
     * @param w the widget to bind.
     * @param graph reference to the graph database.
     */
    private void bindToAbstractRole(Vertex wv, Widget w, Graph graph) {
        Vertex abs = getAbstractRoleWidget(graph,w);
        Edge role = graph.addEdge(null,wv,abs,"role");

        abs = getAbstractRoleTitleWidget(graph,w);
        role = graph.addEdge(null, wv, abs, "role_title");

        abs = getAbstractRoleTitlePathWidget(graph,w);
        role = graph.addEdge(null, wv, abs, "role_title_path");
    }


    /**
     *
     * @param g
     * @param widget
     * @return
     */
    private Vertex getAbstractRoleWidget(Graph g,Widget widget) {
        String absID = widget.get(Tags.Abstract_R_ID,"");
        Vertex abstractRole;
        try {
            Iterable<Vertex> vertices = g.getVertices("AbsRole.absid", absID);
            abstractRole = vertices.iterator().next();
        } catch (NoSuchElementException | IllegalArgumentException nse) {
            abstractRole = g.addVertex("class:AbsRole");
        }
        abstractRole.setProperty("absid",absID);
        abstractRole.setProperty("Role", widget.get(Tags.Role, Role.from("UNKNONW")).toString());
        return abstractRole;
    }

    /**
     * Get or create an abstract widget based on the role, title and path.
     * @param g The graph the should contain the vertex
     * @param widget the widget which will be bind to the abstract widget.
     * @return Vertext for the abstract Role/Title/Path combination.
     */
    private Vertex getAbstractRoleTitleWidget(Graph g,Widget widget) {
        String absID = widget.get(Tags.Abstract_R_T_ID,"");
        Vertex abstractRole;
        try {
            Iterable<Vertex> vertices = g.getVertices("AbsRoleTitle.absid", absID);
            abstractRole = vertices.iterator().next();
        } catch (NoSuchElementException | IllegalArgumentException nse) {
            abstractRole = g.addVertex("class:AbsRoleTitle");

        }
        abstractRole.setProperty("absid",absID);
        abstractRole.setProperty("Role", widget.get(Tags.Role, Role.from("UNKNONW")).toString());
        abstractRole.setProperty("Title",widget.get(Tags.Title,"UNKNOWN"));
        return abstractRole;
    }

    /**
     * Get or create an abstract widget based on the role, title and path.
     * @param g The graph the should contain the vertex
     * @param widget the widget which will be bind to the abstract widget.
     * @return Vertext for the abstract Role/Title/Path combination.
     */
    private Vertex getAbstractRoleTitlePathWidget(Graph g,Widget widget) {
        String absID = widget.get(Tags.Abstract_R_T_P_ID,"");
        Vertex abstractRole;
        try {
            Iterable<Vertex> vertices = g.getVertices("AbsRoleTitlePath.absid", absID);
            abstractRole =  vertices.iterator().next();
        } catch (NoSuchElementException | IllegalArgumentException nse) {
            abstractRole = g.addVertex("class:AbsRoleTitlePath");
        }
        abstractRole.setProperty("absid",absID);
        abstractRole.setProperty("Role", widget.get(Tags.Role, Role.from("UNKNONW")).toString());
        abstractRole.setProperty("Title",widget.get(Tags.Title,"UNKNOWN"));
        abstractRole.setProperty("Path",widget.get(Tags.Path,"[]"));
        return abstractRole;
    }



}
