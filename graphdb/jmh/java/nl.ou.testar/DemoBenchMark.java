package nl.ou.testar;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;

public class DemoBenchMark {

    //@Benchmark
    //@BenchmarkMode(Mode.AverageTime)
    //@OutputTimeUnit(TimeUnit.MICROSECONDS)
    public void demoBenchMark(Blackhole bh) {
        int a = 10;
        int b = 21;
        int result = a + b;
        bh.consume(result);
    }
}
