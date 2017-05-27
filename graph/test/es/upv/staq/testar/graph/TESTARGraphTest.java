package es.upv.staq.testar.graph;



import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by florendegier on 27/05/2017.
 */

public class TESTARGraphTest {


   @Test
   public void testGraphBuilder() {

      TESTARGraph graph = TESTARGraph.buildEmptyGraph();
      assertNotNull("Graph object should be created",graph);

   }


}
