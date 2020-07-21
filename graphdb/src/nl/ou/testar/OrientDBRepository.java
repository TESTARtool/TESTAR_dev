/***************************************************************************************************
*
* Copyright (c) 2017 Open Universiteit - www.ou.nl
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
*
* 1. Redistributions of source code must retain the above copyright notice,
* this list of conditions and the following disclaimer.
* 2. Redistributions in binary form must reproduce the above copyright
* notice, this list of conditions and the following disclaimer in the
* documentation and/or other materials provided with the distribution.
* 3. Neither the name of the copyright holder nor the names of its
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
*******************************************************************************************************/


package nl.ou.testar;


import com.tinkerpop.blueprints.*;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
//import com.tinkerpop.gremlin.groovy.Gremlin;
//import com.tinkerpop.pipes.Pipe;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fruit.alayer.*;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Implementation of the interaction with the Graph database. This class is implemented as a Singleton.
 * Created by florendegier on 03/06/2017.
 */
class OrientDBRepository implements GraphDBRepository {

   private static final Logger LOGGER = LogManager.getLogger();

   private final OrientGraphFactory graphFactory;

   OrientDBRepository(final String url, final String userName, final String password) {

      graphFactory = new OrientGraphFactory(url, userName, password);
   }

   public void dropDatabase() {
      graphFactory.drop();
   }


   @Override
   public void addState(final State state, final boolean isInitial) {
      long tStart = System.currentTimeMillis();
      OrientGraph graph = graphFactory.getTx();

      Vertex v = getStateVertex(state.get(Tags.ConcreteID), graph);
      if (v == null) {
         createStateVertex(state, graph, isInitial);
      }
      graph.commit();

      graph.shutdown();
      long tEnd = System.currentTimeMillis();
      LOGGER.info("[S<] # {} # stored in #{} # ms", state.get(Tags.ConcreteID), tEnd - tStart);
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
         createActionVertex(vertexFrom, action, vertexTo, graph);
         graph.commit();
      } finally {
         graph.shutdown();
      }
      long tEnd = System.currentTimeMillis();
      LOGGER.info("[A<] # {} # stored in # {} # ms", action.get(Tags.ConcreteID), tEnd - tStart);
   }

   @Override
   public void addActionOnState(String stateId, Action action, String toStateID) {
      LOGGER.info("Store Action {} ({}) from {} to {}",
         action.get(Tags.ConcreteID), action.get(Tags.Desc, ""), stateId, toStateID);
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
         createActionVertex(vertexFrom, action, vertexTo, graph);
         graph.commit();
      } finally {
         graph.shutdown();
      }
      long tEnd = System.currentTimeMillis();
      LOGGER.info("[A<] # {} stored in # {} # ms", action.get(Tags.ConcreteID), tEnd - tStart);
   }

   @Override
   public void addWidget(String stateID, Widget w) {
      LOGGER.info("Add Widget {} with id {} to state {}", w.get(Tags.Desc, ""), w.get(Tags.ConcreteID), stateID);
      long tStart = System.currentTimeMillis();
      OrientGraph graph = graphFactory.getTx();
      try {
         if (getStateVertex(stateID, graph) == null) {
            throw new GraphDBException("state not found in database");
         }
         Vertex widgetVertex = getWidgetVertex(w.get(Tags.ConcreteID), graph);
         if (widgetVertex == null) {
            widgetVertex = createWidgetVertex(stateID, w, graph);

            bindToAbstractRole(widgetVertex, w, graph);
         }
         graph.commit();
      } finally {
         graph.shutdown();
      }
      long tEnd = System.currentTimeMillis();
      LOGGER.info("[W<] # {} # stored in # {} # ms", w.get(Tags.ConcreteID), tEnd - tStart);

   }

   @Override
   public void addCustomType(Action action, String relation, CustomType instance) {
      addCustomType("Action",action.get(Tags.ConcreteID),relation,instance);
   }

   @Override
   public void addCustomType(State state, String relation, CustomType instance) {
      addCustomType("State",state.get(Tags.ConcreteID),relation,instance);
   }

   @Override
   public void addCustomType(Widget widget, String relation, CustomType instance) {
      addCustomType("Widget",widget.get(Tags.ConcreteID),relation,instance);
   }


   /**
    * Create the relation between an action, state or widget and the customType.
    * @param sourceType type of the source. This must be an Action, State or Widget.
    * @param sourceId id of the source.
    * @param relation relation between the source and the customType.
    * @param instance the instance of the customType.
    */
   private void addCustomType(String sourceType, String sourceId, String relation, CustomType instance) {
      OrientGraph graph = graphFactory.getTx();
      try {
         Vertex source = getVertexByTypeAndId(sourceType, Tags.ConcreteID.toString(), sourceId, graph);
         if(source == null) {
            throw new IllegalArgumentException("Source action not found in database");
         }
         Vertex target = createVertex(instance.getType(),Tags.ConcreteID.toString(),instance.getId(),graph);
         instance.tags().forEach(tag -> setProperty(tag, instance.get(tag), target));

         if( !source.getEdges(Direction.OUT,relation).iterator().hasNext() ) {
            Edge edge = graph.addEdge(null, source, target, relation);
         }

         graph.commit();

      } finally {
         graph.shutdown();
      }
   }



   @SuppressWarnings({"rawtypes", "unchecked"})
   @Override
   public List<Object> getObjectsFromGremlinPipe(String gremlin, GremlinStart start) {
      /*try {
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
      } catch (Exception e) {
         LOGGER.error("Gremlin exception: {}", e.getMessage());
         return new ArrayList<Object>();
      }*/
	   return new ArrayList<Object>();
   }

   /**
    * Create new State Vertex
    *
    * @param state State which needs to be stored as a vertex
    * @param graph Handle to the graph database.
    */
   private void createStateVertex(final State state, final OrientGraph graph,final boolean isInitial) {
      Vertex vertex = graph.addVertex("class:State");
      for (Tag<?> t : state.tags())
         setProperty(t, state.get(t), vertex);
      if(isInitial) {
         vertex.setProperty("IsInitial", true);
      } else {
         vertex.setProperty("IsInitial",false);
      }

      Vertex abstractState = createVertex("AbsState","AbsRoleID",state.get(Tags.Abstract_R_ID),graph);

      Edge isA = graph.addEdge(null,vertex,abstractState,"isA");
   }

   private Vertex createWidgetVertex(final String stateId, final Widget w, final OrientGraph graph) {
      Vertex widgetVertex = graph.addVertex("class:Widget");
      for (Tag<?> t : w.tags())
         setProperty(t, w.get(t), widgetVertex);
      Vertex stateVertex = getStateVertex(stateId, graph);
      Edge edge = graph.addEdge(null, stateVertex, widgetVertex, "has");
      graph.commit();
      LOGGER.debug("Widget {} Vertex created and connected to state via Edge {} ", widgetVertex.getId(), edge.getId());
      return widgetVertex;
   }

   /**
    * Lookup state vertex in the database
    *
    * @param concreteID unique identification of the state
    * @param graph      handle to the graph database
    * @return the vertex of for the State object or null if the state is not found.
    */
   private Vertex getStateVertex(String concreteID, OrientGraph graph) {
     return getVertexByTypeAndId("State", Tags.ConcreteID.toString(), concreteID,graph);
   }

   /**
    * Lookup widget vertex in the database
    *
    * @param concreteID unique identification of the state
    * @param graph     handle to the graph database
    * @return the vertex of for the Widget object or null if the state is not found.
    */
   private Vertex getWidgetVertex(String concreteID, OrientGraph graph) {
      return getVertexByTypeAndId("Widget",Tags.ConcreteID.toString(),concreteID,graph);
   }

   /**
    * Lookup state vertex in the database.
    * @param type       type of the Vertex
    * @param idField    field used for identification.    
    * @param concreteID unique identification of the state
    * @param graph      handle to the graph database
    * @return the vertex of for the State object or null if the state is not found.
    */
   private Vertex getVertexByTypeAndId(String type, String idField, String concreteID, OrientGraph graph) {
      try {
         Iterable<Vertex> vertices = graph.getVertices(type+"." + idField, concreteID);
         Vertex vertex = vertices.iterator().next();
         LOGGER.debug("Vertex {} found", vertex.getId());
         return vertex;
      } catch (IllegalArgumentException | NoSuchElementException ex) {
         LOGGER.debug("There is no vertex inserted yet for the given {} ConcreteID {}",type, concreteID);
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
   private void createActionVertex(final Vertex vFrom, final Action action, final Vertex vTo, final Graph graph) {

      Vertex a = createVertex("Action","ConcreteID", action.get(Tags.ConcreteID),graph);

      Edge targets = graph.addEdge(null, vFrom, a, "targetedBy");
      Edge resultsin = graph.addEdge(null,a, vTo, "resultsIn");

      Vertex abstractAction = createVertex("AbstractAction","AbstractID",action.get(Tags.AbstractID),graph);

      Edge isA = graph.addEdge(null, a, abstractAction,"isA");

      for (Tag<?> t : action.tags()) {
         setProperty(t, action.get(t), a);
      }
   }

   /**
    * Get or create a vertex of a given type with a given identification (key/id)
    * @param type type of the vertex
    * @param key key for the required vertex
    * @param id id (value)
    * @param graph the graph
    * @return a vertex of the given type identified by key/value.
    */
   private Vertex createVertex(String type, String key, String id, Graph graph) {
      Vertex v;
      try {
         Iterable<Vertex> vertices = graph.getVertices(key,id);
         v = vertices.iterator().next();
         LOGGER.debug("Vertex {} found", v.getId());
      } catch (IllegalArgumentException | NoSuchElementException ex) {
         LOGGER.debug("There is no vertex inserted yet for the given Widget ConcreteID {}", id);
         v = graph.addVertex("class:"+type);
         v.setProperty(key,id);
      }
      return v;
   }

   /**
    * Method to cast the properties to there proper type.
    *
    * @param t  the processed tag
    * @param o  type of the data
    * @param el the element to add
    */
   private void setProperty(Tag<?> t, Object o, Element el) {
      String name = t.name().replace(',', '_');
      name = name.replace('(','_');
      name = name.replace(')','_');
      // TODO: is there a more sophisticated way to do this?
      if (o instanceof Boolean)
         el.setProperty(name, ((Boolean) o).booleanValue());
      else if (o instanceof Byte)
         el.setProperty(name, ((Byte) o).byteValue());
      else if (o instanceof Character)
         el.setProperty(name, ((Character) o).charValue());
      else if (o instanceof Double)
         el.setProperty(name, ((Double) o).doubleValue());
      else if (o instanceof Float)
         el.setProperty(name, ((Float) o).floatValue());
      else if (o instanceof Integer)
         el.setProperty(name, ((Integer) o).intValue());
      else if (o instanceof Long)
         el.setProperty(name, ((Long) o).longValue());
      else if (o instanceof Short)
         el.setProperty(name, ((Short) o).shortValue());
      else if (o instanceof Visualizer) {
         //skip Don't put visualizer in the graph since it has no meaning for graph.
         //It will get a meaning when we want to use the data for reply.
      } else
         el.setProperty(name, o.toString());
   }

   /**
    * Bind the Widget to it's abstract counter parts
    *
    * @param wv    the vertex of the widget to bind.
    * @param w     the widget to bind.
    * @param graph reference to the graph database.
    */
   private void bindToAbstractRole(Vertex wv, Widget w, Graph graph) {
      Vertex abs = getAbstractRoleWidget(graph, w);
      Edge role = graph.addEdge(null, wv, abs, "role");

      abs = getAbstractRoleTitleWidget(graph, w);
      role = graph.addEdge(null, wv, abs, "roleTitle");

      abs = getAbstractRoleTitlePathWidget(graph, w);
      role = graph.addEdge(null, wv, abs, "roleTitlePath");
   }


   /**
    * Get or create an abstract widget based on the role, title and path.
    *
    * @param g      The graph the should contain the vertex
    * @param widget the widget which will be bind to the abstract widget.
    * @return Vertext for the abstract Role/Title/Path combination.
    */
   private Vertex getAbstractRoleWidget(Graph g, Widget widget) {
      String abstractId = widget.get(Tags.Abstract_R_ID, "");
      Vertex abstractRole = createVertex("AbsRole",
         "Abstract_R_ID",
         widget.get(Tags.Abstract_R_ID),g);

      abstractRole.setProperty("absid", abstractId);
      abstractRole.setProperty("Role", widget.get(Tags.Role, Role.from("UNKNOWN")).toString());
      return abstractRole;
   }

   /**
    * Get or create an abstract widget based on the role, title and path.
    *
    * @param g      The graph the should contain the vertex
    * @param widget the widget which will be bind to the abstract widget.
    * @return Vertext for the abstract Role/Title/Path combination.
    */
   private Vertex getAbstractRoleTitleWidget(Graph g, Widget widget) {
      String absID = widget.get(Tags.Abstract_R_T_ID, "");
      Vertex abstractRole;
      try {
         Iterable<Vertex> vertices = g.getVertices("AbsRoleTitle.absid", absID);
         abstractRole = vertices.iterator().next();
      } catch (NoSuchElementException | IllegalArgumentException nse) {
         abstractRole = g.addVertex("class:AbsRoleTitle");

      }
      abstractRole.setProperty("absid", absID);
      abstractRole.setProperty("Role", widget.get(Tags.Role, Role.from("UNKNOWN")).toString());
      abstractRole.setProperty("Title", widget.get(Tags.Title, "UNKNOWN"));
      return abstractRole;
   }

   /**
    * Get or create an abstract widget based on the role, title and path.
    *
    * @param g      The graph the should contain the vertex
    * @param widget the widget which will be bind to the abstract widget.
    * @return Vertext for the abstract Role/Title/Path combination.
    */
   private Vertex getAbstractRoleTitlePathWidget(Graph g, Widget widget) {
      String absID = widget.get(Tags.Abstract_R_T_P_ID, "");
      Vertex abstractRole;
      try {
         Iterable<Vertex> vertices = g.getVertices("AbsRoleTitlePath.absid", absID);
         abstractRole = vertices.iterator().next();
      } catch (NoSuchElementException | IllegalArgumentException nse) {
         abstractRole = g.addVertex("class:AbsRoleTitlePath");
      }
      abstractRole.setProperty("absid", absID);
      abstractRole.setProperty("Role", widget.get(Tags.Role, Role.from("UNKNOWN")).toString());
      abstractRole.setProperty("Title", widget.get(Tags.Title, "UNKNOWN"));
      abstractRole.setProperty("Path", widget.get(Tags.Path, "[]"));
      return abstractRole;
   }


}
