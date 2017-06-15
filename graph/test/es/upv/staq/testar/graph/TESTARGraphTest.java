package es.upv.staq.testar.graph;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by florendegier on 27/05/2017.
 */
public class TESTARGraphTest {

   private TESTARGraph graph;

   @Before
   public void createSUT() {
      graph = TESTARGraph.buildEmptyGraph();
   }

   @Test
   public void buildEmptyGraph() {
      assertNotNull("Graph object shall be created",graph);
   }

   @Test
   public void testEmptyLongestPath() {
      assertEquals("Longest path (NULL)",graph.getLongestPath());
   }

   @Test
   public void testAddNewVertex() {
      boolean result = graph.addVertex(new GraphState("v1"));
      assertTrue("The vertex has been added", result);
      assertEquals("There should be one vertext", 1,graph.vertexSet().size());

   }

   @Test
   public void testAddExistingVertex() {
      graph.addVertex(new GraphState("v1"));
      boolean result = graph.addVertex(new GraphState("v1"));
      assertFalse("A vertex can be added once using it's concrete ID", result);
      assertEquals("There should be one vertext", 1,graph.vertexSet().size());
   }

}