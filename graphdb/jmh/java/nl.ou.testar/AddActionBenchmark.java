package nl.ou.testar;

import org.fruit.alayer.Action;
import org.fruit.alayer.Tags;
import org.fruit.alayer.actions.NOP;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

/**
 * Benchmark to measure the average execution time of adding one Action to the data model.
 */
@State(Scope.Benchmark)
public class AddActionBenchmark {


    OrientDBRepository graphFactory;

    @Setup
    public void setupDatabase() {
        graphFactory = new OrientDBRepository("plocal:/tmp/benchmark" +
                "benchmark","admin","admin");

        graphFactory.addState(Util.createState("widget"+1));
        graphFactory.addWidget("widget1", Util.createWidget("w1"));

    }

    @TearDown
    public void dropDatabase() {
        graphFactory.dropDatabase();
    }

    //@Benchmark
    @Warmup(iterations = 30)
    @Fork(5)
    @Measurement(iterations = 10, time=1, timeUnit = TimeUnit.MILLISECONDS)
    @BenchmarkMode({Mode.AverageTime})
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void addSingleAction() {

        Action action = new NOP();
        action.set(Tags.ConcreteID,"arghh");
        action.set(Tags.TargetID,"w1");
        graphFactory.addAction(action,"widget1");
    }


}
