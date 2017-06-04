package edu.ou.testar;

import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import org.fruit.alayer.State;
import org.fruit.alayer.Tag;

/**
 * Implementation of the interaction with the Graph database. This class is implemented as a Singleton.
 * Created by florendegier on 03/06/2017.
 */
public class OrientDBRepository {

   private final OrientGraphFactory graphFactory;


   public OrientDBRepository(final OrientGraphFactory factory) {
      graphFactory =  factory;
   }

   /**
    * Store State in Graph database.
    *
    * @param state State of the SUT for this step.
    */
   public void addState(final State state) {
      OrientGraph graph = graphFactory.getTx();
      try {
         Vertex v = graph.addVertex("class:State");
         for (Tag t : state.tags()) {
            v.setProperty(t.name(), t);
         }
         graph.commit();
      } finally {
         graph.shutdown();
      }
   }



}
