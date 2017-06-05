package nl.ou.testar;

import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import org.fruit.alayer.StdState;
import org.fruit.alayer.Tags;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Unit test to validate the storage of State objects
 * A State will be stored ones. Every time the same state is stored a flag visited is updated.
 * Created by florendegier on 03/06/2017.
 */
public class OrientDBRepositoryTest {

    private OrientGraphFactory factory;

    @Before
    public void setup() {
        factory = new OrientGraphFactory("memory:demo");
        factory.setMaxRetries(10);
        factory.setupPool(1, 2);
        factory.setRequireTransaction(false);
    }

    @After
    public void cleanup() {
        factory.close();
    }

    @Test
    public void testAddState() {
        OrientDBRepository sut = new OrientDBRepository(factory);

        StdState state = new StdState();
        state.set(Tags.ConcreteID, "0xCAFE");

        sut.addState(state);

        OrientGraph graph = factory.getTx();
        long data = graph.getRawGraph().countClass("State");
        assertEquals("The database shall contain one element", 1, data);
        graph.shutdown();

        sut.addState(state);

        graph = factory.getTx();
        data = graph.getRawGraph().countClass("State");
        assertEquals("The database shall contain one element", 1, data);

        state.set(Tags.ConcreteID, "0xDEAD");

        sut.addState(state);

        graph = factory.getTx();
        data = graph.getRawGraph().countClass("State");
        assertEquals("The database shall contain two elements", 2, data);

        graph.shutdown();

    }

}