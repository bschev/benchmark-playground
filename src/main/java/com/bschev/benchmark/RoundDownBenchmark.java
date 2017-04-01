package com.bschev.benchmark;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

@Warmup(iterations = 3, time = 2, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3, time = 2, timeUnit = TimeUnit.SECONDS)
@Fork(2)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
@Threads(1)
public class RoundDownBenchmark {

    private static double VALUE_TO_ROUND = 0.435786362876438721;

    private DecimalFormat decimalFormat;

    @Setup
    public void prepareBenchmark() {
        decimalFormat = new DecimalFormat("#.#####");
        decimalFormat.setRoundingMode(RoundingMode.DOWN);
    }

    @Benchmark
    public double measureRoundDownSimple() {
        return (long) (VALUE_TO_ROUND * 1e5) / 1e5;
    }

    @Benchmark
    public double measureRoundDownMath() {
        return Math.floor(VALUE_TO_ROUND * 1e5) / 1e5;
    }

    @Benchmark
    public double measureRoundDownBigDecimalScale() {
        BigDecimal bd = new BigDecimal(VALUE_TO_ROUND);
        BigDecimal rounded = bd.setScale(5, RoundingMode.DOWN);
        return rounded.doubleValue();
    }

    @Benchmark
    public double measureRoundDownDecimalFormatDouble() {
        return Double.valueOf(decimalFormat.format(VALUE_TO_ROUND));
    }

    @Benchmark
    public String measureRoundDownDecimalFormatString() {
        return decimalFormat.format(VALUE_TO_ROUND);
    }

}
