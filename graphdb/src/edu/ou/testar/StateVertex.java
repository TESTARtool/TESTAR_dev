package edu.ou.testar;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.VertexQuery;

import java.util.Set;

/**
 * Created by florendegier on 03/06/2017.
 */
public class StateVertex implements Vertex{
   /**
    * Return the edges incident to the vertex according to the provided direction and edge labels.
    *
    * @param direction the direction of the edges to retrieve
    * @param labels    the labels of the edges to retrieve
    * @return an iterable of incident edges
    */
   @Override
   public Iterable<Edge> getEdges(Direction direction, String... labels) {
      return null;
   }

   /**
    * Return the vertices adjacent to the vertex according to the provided direction and edge labels.  This
    * method does not remove duplicate vertices (i.e. those vertices that are connected by more than one edge).
    *
    * @param direction the direction of the edges of the adjacent vertices
    * @param labels    the labels of the edges of the adjacent vertices
    * @return an iterable of adjacent vertices
    */
   @Override
   public Iterable<Vertex> getVertices(Direction direction, String... labels) {
      return null;
   }

   /**
    * Generate a query object that can be used to fine tune which edges/vertices are retrieved that are incident/adjacent to this vertex.
    *
    * @return a vertex query object with methods for constraining which data is pulled from the underlying graph
    */
   @Override
   public VertexQuery query() {
      return null;
   }

   /**
    * Add a new outgoing edge from this vertex to the parameter vertex with provided edge label.
    *
    * @param label    the label of the edge
    * @param inVertex the vertex to connect to with an incoming edge
    * @return the newly created edge
    */
   @Override
   public Edge addEdge(String label, Vertex inVertex) {
      return null;
   }

   /**
    * Return the object value associated with the provided string key.
    * If no value exists for that key, return null.
    *
    * @param key the key of the key/value property
    * @return the object value related to the string key
    */
   @Override
   public <T> T getProperty(String key) {
      return null;
   }

   /**
    * Return all the keys associated with the element.
    *
    * @return the set of all string keys associated with the element
    */
   @Override
   public Set<String> getPropertyKeys() {
      return null;
   }

   /**
    * Assign a key/value property to the element.
    * If a value already exists for this key, then the previous key/value is overwritten.
    *
    * @param key   the string key of the property
    * @param value the object value o the property
    */
   @Override
   public void setProperty(String key, Object value) {

   }

   /**
    * Un-assigns a key/value property from the element.
    * The object value of the removed property is returned.
    *
    * @param key the key of the property to remove from the element
    * @return the object value associated with that key prior to removal
    */
   @Override
   public <T> T removeProperty(String key) {
      return null;
   }

   /**
    * Remove the element from the graph.
    */
   @Override
   public void remove() {

   }

   /**
    * An identifier that is unique to its inheriting class.
    * All vertices of a graph must have unique identifiers.
    * All edges of a graph must have unique identifiers.
    *
    * @return the identifier of the element
    */
   @Override
   public Object getId() {
      return null;
   }
}
