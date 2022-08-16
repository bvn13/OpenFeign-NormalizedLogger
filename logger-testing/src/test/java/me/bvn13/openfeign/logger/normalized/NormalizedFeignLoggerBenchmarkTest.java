package me.bvn13.openfeign.logger.normalized;

import feign.Feign;
import feign.Logger;
import feign.gson.GsonDecoder;
import feign.slf4j.Slf4jLogger;
import org.junit.jupiter.api.Assertions;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;

import static me.bvn13.openfeign.logger.normalized.TestJettyServer.PORT;

public class NormalizedFeignLoggerBenchmarkTest {
    @State(Scope.Benchmark)
    public static class BenchmarkStateWithoutLogging {
        TestJettyServer jettyServer;
        TestFeignClient feignClient;

        @Setup
        public void init() {
            jettyServer = new TestJettyServer();
            jettyServer.start();
            feignClient = Feign.builder()
                    .decoder(new GsonDecoder())
                    .target(TestFeignClient.class, "http://localhost:" + PORT);
        }

        @TearDown
        public void tearDown() {
            jettyServer.stop();
        }
    }

    @State(Scope.Benchmark)
    public static class BenchmarkStateWithSlf4jLogging {
        TestJettyServer jettyServer;
        TestFeignClient feignClient;

        @Setup
        public void init() {
            jettyServer = new TestJettyServer();
            jettyServer.start();
            feignClient = Feign.builder()
                    .logger(new Slf4jLogger())
                    .logLevel(Logger.Level.FULL)
                    .decoder(new GsonDecoder())
                    .target(TestFeignClient.class, "http://localhost:" + PORT);
        }

        @TearDown
        public void tearDown() {
            jettyServer.stop();
        }
    }

    @State(Scope.Benchmark)
    public static class BenchmarkStateWithNormalizedLogging {
        TestJettyServer jettyServer;
        TestFeignClient feignClient;

        @Setup
        public void init() {
            jettyServer = new TestJettyServer();
            jettyServer.start();
            feignClient = Feign.builder()
                    .logger(new NormalizedFeignLogger())
                    .logLevel(Logger.Level.FULL)
                    .decoder(new GsonDecoder())
                    .target(TestFeignClient.class, "http://localhost:" + PORT);
        }

        @TearDown
        public void tearDown() {
            jettyServer.stop();
        }
    }

    @Benchmark
    @Warmup(iterations = 3)
    public void testFeignLogger(BenchmarkStateWithoutLogging benchmarkState) {
        final ResponseDto status = benchmarkState.feignClient.getStatus();
        Assertions.assertEquals("ok", status.getStatus());
    }

    @Benchmark
    @Warmup(iterations = 3)
    public void testSlf4jFeignLogger(BenchmarkStateWithSlf4jLogging benchmarkState) {
        final ResponseDto status = benchmarkState.feignClient.getStatus();
        Assertions.assertEquals("ok", status.getStatus());
    }

    @Benchmark
    @Warmup(iterations = 3)
    public void testNormalizedFeignLogger(BenchmarkStateWithNormalizedLogging benchmarkState) {
        final ResponseDto status = benchmarkState.feignClient.getStatus();
        Assertions.assertEquals("ok", status.getStatus());
    }

    public static void main(String[] args) throws Exception {
        org.openjdk.jmh.Main.main(args);
    }

}
