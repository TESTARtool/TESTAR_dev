package nl.ou.testar;

import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;


@State(Scope.Benchmark)
public class GraphDBBenchmark {

    OrientGraphFactory graphFactory;

    @Setup
    public void setupDatabase() {
        graphFactory = new OrientGraphFactory("plocal:/temp/testPool");
        graphFactory.setupPool(1,5);
    }

    @TearDown



    @Benchmark()
    @Warmup(iterations = 5)
    @Measurement(iterations = 2, time=1, timeUnit = TimeUnit.SECONDS)
    @BenchmarkMode(Mode.All)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public void testGetTx() {
        OrientGraph graph = graphFactory.getTx();

        graph.isAutoStartTx();

        graph.shutdown();
    }
}
