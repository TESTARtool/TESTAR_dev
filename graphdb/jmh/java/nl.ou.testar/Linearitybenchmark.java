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
public class Linearitybenchmark {

    OrientDBRepository graphFactory;

    @Setup(Level.Invocation)
    public void setupDatabase() {
        graphFactory = new OrientDBRepository("plocal:/tmp/benchmark" +
                "benchmark","admin","admin");
       graphFactory.addState(Util.createState("tagI"+0));
    }

    @TearDown(Level.Invocation)
    public void dropDatabase() {
        graphFactory.dropDatabase();
    }

    @Param({"1","2","4","8","16","32","64","128","256","512","1024"})
    private int interations;

    //@Benchmark()
    @Warmup(iterations = 30)
    @Fork(5)
    @Measurement(iterations = 10, time=1, timeUnit = TimeUnit.MILLISECONDS)
    @BenchmarkMode({Mode.AverageTime})
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void testAddStateLiniairity() {
        for(int i = 1; i<interations; i++)
        graphFactory.addState(Util.createState("tagI"+i));
    }
}
