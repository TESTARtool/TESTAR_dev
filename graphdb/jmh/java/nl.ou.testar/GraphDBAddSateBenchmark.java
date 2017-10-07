package nl.ou.testar;

import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;


@State(Scope.Benchmark)
public class GraphDBAddSateBenchmark {

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



    @Benchmark()
    @Warmup(iterations = 10)
    @Fork(5)
    @Measurement(iterations = 10, time=1, timeUnit = TimeUnit.MILLISECONDS)
    @BenchmarkMode({Mode.SingleShotTime,Mode.AverageTime})
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void testAddState() {
        graphFactory.addState(Util.createState("0xCAFE"));
    }


    @Benchmark()
    @Warmup(iterations = 10)
    @Fork(5)
    @Measurement(iterations = 10, time=1, timeUnit = TimeUnit.MILLISECONDS)
    @BenchmarkMode({Mode.SingleShotTime,Mode.AverageTime})
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void testAddStateTwice() {
        graphFactory.addState(Util.createState("0xCAFE"));
        graphFactory.addState(Util.createState("0xCAFE"));
    }


}
