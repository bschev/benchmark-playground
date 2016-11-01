# benchmark-playground

Measure, don't guess...

In this project, [Java Micro-Benchmarking Harness (JMH)](http://openjdk.java.net/projects/code-tools/jmh/) is used to compare different implementations for diverse tasks.

## Run Benchmarks

```
 mvn clean package
 java -jar target/benchmarks.jar com.bschev.benchmark.AverageBenchmark
```

## References
http://java-performance.info/jmh/