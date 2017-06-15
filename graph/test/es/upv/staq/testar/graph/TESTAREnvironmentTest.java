package es.upv.staq.testar.graph;


import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit test to demonstrate the operation of the TestarEnvironment.
 * Created by florendegier on 27/05/2017.
 */
public class TESTAREnvironmentTest {


   private TESTAREnvironment environment;

   @Before
   public void createEnviroment() {
      environment = new TESTAREnvironment("test" );
   }

   @Test
   public void validateEmptyEnvironment() {
      assertEquals("In a clean environment the path length shall be ",
         0,environment.getLongestPathLength());

      assertArrayEquals(new int[] {0,0,-2},environment.getGraphResumingMetrics());

   }

   @Test
   public void testAddPopulateWithFirstAction() {
      environment.populateEnvironment(new GraphState(Grapher.GRAPH_NODE_ENTRY),
         new GraphAction("a1"),
         new GraphState("v2"));

      assertEquals("Longest path shall be ", 2, environment.getLongestPathLength());
      assertTrue("v2 shall be inserted", environment.stateAtGraph(new GraphState("v2")));

   }

   @Test
   public void testPopulateWithTwoActions() {

      environment.populateEnvironment(new GraphState(Grapher.GRAPH_NODE_ENTRY),
         new GraphAction("a1"),
         new GraphState("v2"));
      environment.populateEnvironment(new GraphState("v2"),
         new GraphAction("a2"),
         new GraphState("v3"));
      assertEquals("Longest path shall be ", 3, environment.getLongestPathLength());
      assertTrue("v1 shall be inserted", environment.stateAtGraph(new GraphState("v3")));

   }




}