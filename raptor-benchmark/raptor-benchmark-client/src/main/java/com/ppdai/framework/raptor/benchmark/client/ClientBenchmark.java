package com.ppdai.framework.raptor.benchmark.client;

import com.ppdai.framework.raptor.proto.Helloworld;
import com.ppdai.framework.raptor.proto.Simple;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

@Fork(1)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@State(Scope.Benchmark)
@BenchmarkMode({Mode.Throughput, Mode.AverageTime, Mode.SampleTime})
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class ClientBenchmark {


    private Simple jsonProxy;
    private Simple binProxy;

    private byte[] jsonData;
    private byte[] binData;

    @Setup
    public void setup() {
    }

    @Benchmark
    public void testJson(Blackhole bh) {
        Helloworld.HelloRequest helloRequest = Helloworld.HelloRequest.newBuilder().setName("ppdai").build();
        Helloworld.HelloReply helloReply = jsonProxy.sayHello(helloRequest);
        bh.consume(helloReply);
    }


    @Benchmark
    public void testBin(Blackhole bh) {
        Helloworld.HelloRequest helloRequest = Helloworld.HelloRequest.newBuilder().setName("ppdai").build();
        Helloworld.HelloReply helloReply = binProxy.sayHello(helloRequest);
        bh.consume(helloReply);
    }

    @Benchmark
    public void jsonSerialization(Blackhole bh) {
    }

    @Benchmark
    public void binSerialization(Blackhole bh) {
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(ClientBenchmark.class.getSimpleName())
                .build();

        new Runner(opt).run();
    }
}
