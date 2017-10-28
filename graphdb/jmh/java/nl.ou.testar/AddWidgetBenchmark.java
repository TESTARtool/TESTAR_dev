package nl.ou.testar;

import org.fruit.alayer.StdWidget;
import org.fruit.alayer.Tags;
import org.fruit.alayer.Widget;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

/**
 * Benchmark to measure the duration of adding a single widget.
 * Add Widget to state in a database that contains only 1 state.
 *
 */
@State(Scope.Benchmark)
public class AddWidgetBenchmark {

    OrientDBRepository graphFactory;

    Widget widget = createWidget("0xDADA");
    Widget dummy  = createWidget("0xADAD");

    @Setup(Level.Invocation)
    public void setupDatabase() {
        graphFactory = new OrientDBRepository("plocal:/tmp/benchmark" +
                "/benchmark","admin","admin");

        graphFactory.addState(Util.createState("widget"+1));
        graphFactory.addWidget("widget1",dummy);

    }

    @TearDown(Level.Invocation)
    public void dropDatabase() {
        graphFactory.dropDatabase();
    }


    //@Benchmark
    @Warmup(iterations = 30)
    @Fork(5)
    @Measurement(iterations = 10, time=1, timeUnit = TimeUnit.MILLISECONDS)
    @BenchmarkMode({Mode.AverageTime})
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void addSingleWidgetToState() {
        graphFactory.addWidget("widget1",widget);
    }


    private Widget createWidget(String id) {
        Widget widget = new StdWidget();
        widget.set(Tags.ConcreteID,id);
        widget.set(Tags.Desc,"Demo");
        return widget;
    }
}
