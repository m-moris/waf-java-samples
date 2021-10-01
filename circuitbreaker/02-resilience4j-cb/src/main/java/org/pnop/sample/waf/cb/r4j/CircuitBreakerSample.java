package org.pnop.sample.waf.cb.r4j;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.SlidingWindowType;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.vavr.control.Try;

public class CircuitBreakerSample {

    private static Logger logger = LoggerFactory.getLogger(CircuitBreakerSample.class);

    public void run() {
        CircuitBreakerConfig config = CircuitBreakerConfig
            .custom()
            //.minimumNumberOfCalls(5)
            .slidingWindowSize(5) // ring windows buffer
            .permittedNumberOfCallsInHalfOpenState(5)
            .slidingWindowType(SlidingWindowType.COUNT_BASED)
            .recordExceptions(IOException.class, RuntimeException.class)
            .automaticTransitionFromOpenToHalfOpenEnabled(true)
            .waitDurationInOpenState(Duration.ofSeconds(10))
            .build();

        CircuitBreakerRegistry registry = CircuitBreakerRegistry.of(config);
        CircuitBreaker cb = registry.circuitBreaker("circuitbreaker");

        // useCallable(cb);
        useFunction(cb);
    }

    private void useCallable(CircuitBreaker cb) {

        Callable<String> callable = cb.decorateCallable(new Callable<String>() {
            @Override
            public String call() throws Exception {
                if (Math.random() < 1.0) {
                    throw new IOException();
                }
                return "OK";
            }
        });

        for (int i = 0; i < 20; i++) {
            logger.info("Befor state : {}", cb.getState());
            System.out.println(Try.of(() -> callable.call()));
            logger.info("After state : {}", cb.getState());
        }
        try {
            Thread.sleep(11000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        cb.transitionToHalfOpenState();
        for (int i = 0; i < 20; i++) {
            logger.info("Befor state : {}", cb.getState());
            System.out.println(Try.of(() -> callable.call()));
            logger.info("After state : {}", cb.getState());
        }
    }

    private void useFunction(CircuitBreaker cb) {

        Function<Boolean, String> decorated = CircuitBreaker
            .decorateFunction(cb, new Function<Boolean, String>() {
                @Override
                public String apply(Boolean t) {
                    if (t) {
                        return "OK";
                    } else {
                        throw new RuntimeException();
                    }
                }
            });

        for (int i = 0; i < 20; i++) {
            var before = cb.getState();
            var res = Try.of(() -> decorated.apply(Boolean.FALSE));
            var after = cb.getState();
            logger.info("{} => {} : {}", before, after, res);
        }
        try {
            Thread.sleep(11000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        logger.info("****************************************************");
        for (int i = 0; i < 20; i++) {
            var before = cb.getState();
            var res = Try.of(() -> decorated.apply(Boolean.TRUE));
            var after = cb.getState();
            logger.info("{} => {} : {}", before, after, res);
        }
    }
}
