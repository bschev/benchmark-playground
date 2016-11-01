package com.bschev.benchmark;

import org.openjdk.jmh.annotations.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

@Warmup(iterations = 3, time = 2, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3, time = 2, timeUnit = TimeUnit.SECONDS)
@Fork(2)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
@Threads(1)
public class AverageBenchmark {

    @Param({"100", "1000", "10000", "100000"})
    public int numberOfValues;

    private ArrayList<Long> valueArrayList;

    private LinkedList<Long> valueLinkedList;

    @Setup
    public void prepareBenchmark() {
        valueArrayList = new ArrayList<>(numberOfValues);
        valueLinkedList = new LinkedList<>();
        LongStream.range(0, numberOfValues).forEach(v -> {
            long value = 100L + v;
            valueArrayList.add(value);
            valueLinkedList.add(value);
        });
    }

    @Benchmark
    public double measureAvgArrayListStream() {
        return calcAvgListStream(valueArrayList);
    }

    @Benchmark
    public double measureAvgLinkedListStream() {
        return calcAvgListStream(valueLinkedList);
    }

    private double calcAvgListStream(List<Long> valueList) {
        return valueList.stream().collect(Collectors.averagingLong(n -> n));
    }

    @Benchmark
    public double measureAvgArrayListLongLoop() {
        return calcAvgListLongLoop(valueArrayList);
    }

    @Benchmark
    public double measureAvgLinkedListLongLoop() {
        return calcAvgListLongLoop(valueLinkedList);
    }

    private double calcAvgListLongLoop(List<Long> valueList) {
        long sum = 0;
        for (Long value : valueList) {
            sum += value;
        }
        return (double) sum / valueList.size();
    }

    @Benchmark
    public BigDecimal measureAvgArrayListBigDecimalLoop() {
        return calcAvgListBigDecimalLoop(valueArrayList);
    }

    @Benchmark
    public BigDecimal measureAvgLinkedListBigDecimalLoop() {
        return calcAvgListBigDecimalLoop(valueArrayList);
    }

    private BigDecimal calcAvgListBigDecimalLoop(List<Long> valueList) {
        BigDecimal sum = BigDecimal.ZERO;
        for (Long value : valueList) {
            sum = sum.add(new BigDecimal(value));
        }
        return sum.divide(new BigDecimal(valueList.size()));
    }

}
