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
public class GraphDBAddWidgetBenchmark1 {

    OrientDBRepository graphFactory;

    Widget widget = createWidget();

    @Setup
    public void setupDatabase() {
        graphFactory = new OrientDBRepository("plocal:/temp/benchmark" +
                "benchmark","admin","admin");

        graphFactory.addState(Util.createState("widget"+1));

    }

    @TearDown
    public void dropDatabase() {
        graphFactory.dropDatabase();
    }


    @Benchmark
    @Warmup(iterations = 10)
    @Fork(5)
    @Measurement(iterations = 10, time=1, timeUnit = TimeUnit.MILLISECONDS)
    @BenchmarkMode({Mode.SingleShotTime})
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void addSingleWidgetToState() {

        graphFactory.addWidget("widget1",widget);
    }


    private Widget createWidget() {
        Widget widget = new StdWidget();
        widget.set(Tags.ConcreteID,"0xDADA");
        widget.set(Tags.Desc,"Demo");
        return widget;
    }
}
