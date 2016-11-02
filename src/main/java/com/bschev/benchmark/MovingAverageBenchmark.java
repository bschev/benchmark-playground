package com.bschev.benchmark;

import org.openjdk.jmh.annotations.*;

import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Warmup(iterations = 3, time = 2, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3, time = 2, timeUnit = TimeUnit.SECONDS)
@Fork(2)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
@Threads(1)
public class MovingAverageBenchmark {

    @Param({"100", "1000", "10000", "100000"})
    public int windowSize;

    private ArrayDeque<Long> valueArrayDeque;

    private LinkedList<Long> valueLinkedList;

    private long[] valueArray;

    private int arrayIdx;

    private double valueSum = 0.0;

    private double valueAvg = 0.0;

    private long newValue = 111L;

    @Setup(Level.Iteration)
    public void prepareBenchmark() {
        valueArrayDeque = new ArrayDeque<>(windowSize);
        valueLinkedList = new LinkedList<>();
        valueArray = new long[windowSize];
        valueSum = 0.0;

        IntStream.range(0, windowSize).forEach(v -> {
            long value = 100L + v;
            valueArrayDeque.add(value);
            valueLinkedList.add(value);
            valueArray[v] = value;
            valueSum += value;
        });
        arrayIdx = valueArray.length;
        valueAvg = valueSum / windowSize;
    }

    @Benchmark
    public double measureMovingAvgArrayDequeStream() {
        if (valueArrayDeque.size() >= windowSize) {
            valueArrayDeque.poll();
        }
        valueArrayDeque.offer(newValue);
        return valueArrayDeque.stream().collect(Collectors.averagingLong(n -> n));
    }

    @Benchmark
    public double measureMovingAvgArrayDequeLoop() {
        if (valueArrayDeque.size() >= windowSize) {
            valueArrayDeque.poll();
        }
        valueArrayDeque.offer(newValue);
        long sum = 0;
        for (Long value : valueArrayDeque) {
            sum += value;
        }
        return (double) sum / valueArrayDeque.size();
    }

    @Benchmark
    public double measureMovingAvgLinkedListAvg() {
        return measureMovingAvgQueueAvg(valueLinkedList);
    }

    @Benchmark
    public double measureMovingAvgArrayDequeAvg() {
        return measureMovingAvgQueueAvg(valueArrayDeque);
    }

    private double measureMovingAvgQueueAvg(Queue<Long> valueQueue) {
        double minus = 0.0;
        if (valueQueue.size() >= windowSize) {
            long head = valueQueue.poll();
            minus = (double) head / (valueQueue.size() + 1);
        }
        valueQueue.offer(newValue);
        double add = (double) newValue / valueQueue.size();
        valueAvg = valueAvg + add - minus;
        return valueAvg;
    }

    @Benchmark
    public double measureMovingAvgLinkedListSum() {
        return measureMovingAvgQueueSum(valueLinkedList);
    }

    @Benchmark
    public double measureMovingAvgArrayDequeSum() {
        return measureMovingAvgQueueSum(valueArrayDeque);
    }

    private double measureMovingAvgQueueSum(Queue<Long> valueQueue) {
        if (valueQueue.size() >= windowSize) {
            valueSum -= valueQueue.poll();
        }
        valueQueue.offer(newValue);
        valueSum += newValue;
        return valueSum / windowSize;
    }

    @Benchmark
    public double measureMovingAvgArraySum() {
        if (arrayIdx < windowSize - 1) {
            arrayIdx++;
        } else {
            arrayIdx = 0;
        }
        valueSum -= valueArray[arrayIdx];  //-= old value
        valueArray[arrayIdx] = newValue;
        valueSum += newValue;
        return valueSum / windowSize;
    }

    @Benchmark
    public double measureMovingAvgArrayAvg() {
        if (arrayIdx < windowSize - 1) {
            arrayIdx++;
        } else {
            arrayIdx = 0;
        }
        double minus = (double) valueArray[arrayIdx] / valueArray.length;
        valueArray[arrayIdx] = newValue;
        double add = (double) newValue / valueArray.length;
        valueAvg = valueAvg + add - minus;
        return valueAvg;
    }

}
