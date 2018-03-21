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

    @Setup(Level.Invocation)
    public void setupDatabase() {
        graphFactory = new OrientDBRepository("plocal:benchmark" +
                "benchmark","admin","admin");

        graphFactory.addState(Util.createState("widget"+1),true);
        graphFactory.addWidget("widget1", Util.createWidget("w1"));

        Action action = new NOP();
        action.set(Tags.ConcreteID,"dummy");
        action.set(Tags.TargetID,"w1");
        action.set(Tags.AbstractID,"abstract");
        graphFactory.addAction(action,"widget1");

    }

    @TearDown(Level.Invocation)
    public void dropDatabase() {
        graphFactory.dropDatabase();
    }

    @Benchmark
    @Warmup(iterations = 30)
    @Fork(10)
    @Measurement(iterations = 10, time=1, timeUnit = TimeUnit.MILLISECONDS)
    @BenchmarkMode({Mode.SampleTime})
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void addSingleAction() {

        Action action = new NOP();
        action.set(Tags.ConcreteID,"arghh");
        action.set(Tags.TargetID,"w1");
        action.set(Tags.AbstractID,"appie");
        graphFactory.addAction(action,"widget1");
    }


}
