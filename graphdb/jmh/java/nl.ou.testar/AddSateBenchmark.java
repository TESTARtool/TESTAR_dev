package nl.ou.testar;

import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;


/**
 * Benchmark to measure the time it takes to add a State to the data model.
 * There are 2 scenario's.
 * 1 Add a State once
 * 2 Add a State twice (it should be updated).
 */
@State(Scope.Benchmark)
public class AddSateBenchmark {

    OrientDBRepository graphFactory;

    @Setup(Level.Invocation)
    public void setupDatabase() {
        graphFactory = new OrientDBRepository("plocal:/tmp/benchmark" +
                "benchmark","admin","admin");
       graphFactory.addState(Util.createState("0xDEAD"),true);
    }

    @TearDown(Level.Invocation)
    public void dropDatabase() {
       graphFactory.dropDatabase();
    }



    @Benchmark()
    @Warmup(iterations = 30)
    @Fork(10)
    @Measurement(iterations = 10, time=1, timeUnit = TimeUnit.MILLISECONDS)
    @BenchmarkMode({Mode.SampleTime})
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void testAddState() {
        graphFactory.addState(Util.createState("0xCAFE"),true);
    }


    @Benchmark()
    @Warmup(iterations = 10)
    @Fork(10)
    @Measurement(iterations = 10, time=1, timeUnit = TimeUnit.MILLISECONDS)
    @BenchmarkMode({Mode.SampleTime})
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void testAddStateTwice() {
        graphFactory.addState(Util.createState("0xCAFE"),false);
        graphFactory.addState(Util.createState("0xCAFE"),false);
    }


}
