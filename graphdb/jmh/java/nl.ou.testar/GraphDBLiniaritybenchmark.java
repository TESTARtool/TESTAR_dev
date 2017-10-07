package nl.ou.testar;

import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

/**
 * Benchmark to test the linear behaviour of adding states to the database.
 * A Set of states will be stored in the database. In each iteration of the benchmark the set
 * of states is doubled. If the behaviour is linear, the amount of time required to execute the
 * benchmark will also change linear.
 */
@State(Scope.Benchmark)
public class GraphDBLiniaritybenchmark {

    OrientDBRepository graphFactory;

    @Setup
    public void setupDatabase() {
        graphFactory = new OrientDBRepository("plocal:/temp/benchmark" +
                "benchmark","admin","admin");
    }

    @TearDown
    public void dropDatabase() {
        graphFactory.dropDatabase();
    }

    @Param({"1","2","4","8","16","32","64","128","256","512","1024"})
    private int interations;

    //@Benchmark()
    @Warmup(iterations = 10)
    @Fork(5)
    @Measurement(iterations = 10, time=1, timeUnit = TimeUnit.MILLISECONDS)
    @BenchmarkMode({Mode.SingleShotTime})
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void testAddStateLiniairity() {
        for(int i = 1; i<interations; i++)
        graphFactory.addState(Util.createState("tagI"+i));
    }
}
