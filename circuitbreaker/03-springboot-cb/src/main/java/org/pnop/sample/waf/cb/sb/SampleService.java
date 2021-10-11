package org.pnop.sample.waf.cb.sb;

import java.io.IOException;
import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.retry.annotation.CircuitBreaker;
import org.springframework.retry.annotation.Recover;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpServerErrorException.ServiceUnavailable;
import org.springframework.web.client.RestTemplate;

import io.vavr.control.Try;

@SuppressWarnings("rawtypes")
@Service
public class SampleService {

    private static Logger logger = LoggerFactory.getLogger(SampleService.class);
    private RestTemplate restTemplate;
    private CircuitBreakerFactory cbFactory;
    private org.springframework.cloud.client.circuitbreaker.CircuitBreaker cb;

    public SampleService(RestTemplateBuilder builder, CircuitBreakerFactory cbFactory) {
        this.restTemplate = builder.build();
        this.cbFactory = cbFactory;
        this.cb = cbFactory.create("custom");
    }

    @Recover
    private String fallbackForCall() {
        logger.error("Fallback for call invoked");
        return "fallback";
    }

    public String call1(int code) {
        return cb.run(() -> {
            String url = "http://httpbin.org/status/" + code;
            logger.info("request : {}", url);
            restTemplate.getForObject(url, String.class);
            return "success";
        }, throwable -> {
            logger.error("{} {}", throwable.getClass().getName(), throwable.getMessage());
            return "fallback";
        });
    }

    @CircuitBreaker(maxAttempts = 3, 
        openTimeout = 5000, 
        exclude = HttpClientErrorException.NotFound.class,
        include = {
          HttpServerErrorException.class,
          HttpClientErrorException.TooManyRequests.class
    })
    public String call2(int code) {
        String url = "http://httpbin.org/status/" + code;
        logger.info("request : {}", url);
        restTemplate.getForObject(url, String.class); 
        logger.info("success");
        return "success";
    }

}