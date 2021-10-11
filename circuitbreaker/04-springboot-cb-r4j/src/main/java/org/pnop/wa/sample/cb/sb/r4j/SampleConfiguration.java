package org.pnop.wa.sample.cb.sb.r4j;

import java.io.IOException;
import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.SlidingWindowType;

@Configuration
public class SampleConfiguration {

    private static Logger logger = LoggerFactory.getLogger(SampleConfiguration.class);

    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> defaultCustomizer() {

        logger.info("defaultCustomizer");

        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
            .slidingWindowType(SlidingWindowType.COUNT_BASED)
            .slidingWindowSize(10)
            .failureRateThreshold(30)
            .permittedNumberOfCallsInHalfOpenState(5)
            .recordExceptions(IOException.class, RuntimeException.class)
            .automaticTransitionFromOpenToHalfOpenEnabled(true)
            .waitDurationInOpenState(Duration.ofSeconds(5))
            .build();

        return factory -> factory
            .configure(builder -> builder
                .circuitBreakerConfig(config).build(), "myconfig1");
    }
}
