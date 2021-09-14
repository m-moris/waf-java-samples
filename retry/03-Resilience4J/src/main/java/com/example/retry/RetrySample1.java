package com.example.retry;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.resilience4j.core.IntervalFunction;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;

/**
 * 
 */
public class RetrySample1 {

    private static Logger logger = LoggerFactory.getLogger(RetrySample1.class);

    private static int MAX_ATTEMPT_COUNT = 4;
    private static int RETRY_INTERVAL = 3;

    public boolean run(URI uri) {

        IntervalFunction backoff = IntervalFunction
            .ofExponentialBackoff(Duration.ofSeconds(RETRY_INTERVAL), 2d);

        RetryConfig config = RetryConfig.custom()
            .maxAttempts(MAX_ATTEMPT_COUNT)
            .retryOnResult(response -> ((HttpResponse<?>) response).statusCode() == 500)
            .retryExceptions(IOException.class, TimeoutException.class)
            .failAfterMaxAttempts(true)
            .intervalFunction(
                IntervalFunction
                    .ofExponentialBackoff(Duration.ofSeconds(RETRY_INTERVAL), 2d))
            .build();

        var client = HttpClient.newHttpClient();
        var request = HttpRequest.newBuilder()
            .uri(uri)
            .timeout(Duration.ofSeconds(5))
            .GET()
            .build();

        RetryRegistry registry = RetryRegistry.of(config);
        Retry retry = registry.retry("retry");

        try {
            HttpResponse<String> response = retry.executeCallable(new Callable<HttpResponse<String>>() {
                public java.net.http.HttpResponse<String> call() throws Exception {
                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    return response;
                }
            });
            System.out.println(response.body());
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}