package org.pnop.waf.sample.cb.r4j;

import java.util.function.Function;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.SlidingWindowType;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;

/**
 * Hello world!
 *
 */
public class App {
    public static void main(String[] args) {

        CircuitBreakerConfig config = CircuitBreakerConfig
           .custom()
           .failureRateThreshold(20)
           .slidingWindowType(SlidingWindowType.COUNT_BASED).slidingWindowSize(5).build();

        var registry = CircuitBreakerRegistry.of(config);
        CircuitBreaker circuitBreaker = registry.circuitBreaker("my-cb");

        Function<Integer, Integer> decorated = CircuitBreaker.decorateFunction(circuitBreaker, App::hoge);

        decorated.apply(1);
        decorated.apply(1);

        

    }

    public static int hoge(int x) {
        return 0;
    }

}
