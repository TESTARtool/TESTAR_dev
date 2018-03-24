package nl.ou.testar;

import org.fruit.alayer.Action;
import org.fruit.alayer.StdState;
import org.fruit.alayer.Tags;
import org.fruit.alayer.actions.NOP;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
public class AddActionOnStateBenchmark {


    OrientDBRepository graphFactory;

    @Setup(Level.Invocation)
    public void setupDatabase() {
        graphFactory = new OrientDBRepository("plocal:/tmp/benchmark" +
                "benchmark","admin","admin");

        graphFactory.addState(Util.createState("widget"+1),true);
        graphFactory.addState(Util.createState("widget"+2),true);
        graphFactory.addWidget("widget1", Util.createWidget("w1"));
        Action action = new NOP();
        action.set(Tags.AbstractID,"abstract");
        action.set(Tags.ConcreteID,"dummy");

        graphFactory.addActionOnState("widget2",action,"widget1");

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
        action.set(Tags.AbstractID,"abstract");
        graphFactory.addActionOnState("widget2",action,"widget1");
    }

}
