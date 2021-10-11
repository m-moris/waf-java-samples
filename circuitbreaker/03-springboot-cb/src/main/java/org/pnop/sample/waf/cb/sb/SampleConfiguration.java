package org.pnop.sample.waf.cb.sb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.circuitbreaker.springretry.SpringRetryCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.springretry.SpringRetryConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.policy.CircuitBreakerRetryPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;

@Configuration
public class SampleConfiguration {

    private static Logger logger = LoggerFactory.getLogger(SampleConfiguration.class);

    @Bean
    public Customizer<SpringRetryCircuitBreakerFactory> defaultCustomizer() {
        logger.info("defaultCustomizer");
        var p = new CircuitBreakerRetryPolicy(new SimpleRetryPolicy(2));
        p.setOpenTimeout(5000);
        p.setResetTimeout(10000);

        return factory -> factory
            .configure(builder -> builder.retryPolicy(p).build(), "custom");

        // .addRetryTemplateCustomizers(retryTemplate ->
        // retryTemplate.registerListener(new RetryListener() {
        //
        // @Override
        // public <T, E extends Throwable> boolean open(RetryContext context,
        // RetryCallback<T, E> callback) {
        // logger.info("open");
        // return false;
        // }
        //
        // @Override
        // public <T, E extends Throwable> void close(RetryContext context,
        // RetryCallback<T, E> callback,
        // Throwable throwable) {
        // logger.info("close");
        // }
        //
        // @Override
        // public <T, E extends Throwable> void onError(RetryContext context,
        // RetryCallback<T, E> callback,
        // Throwable throwable) {
        // logger.info("on error");
        // }
        // }));
    }
}
