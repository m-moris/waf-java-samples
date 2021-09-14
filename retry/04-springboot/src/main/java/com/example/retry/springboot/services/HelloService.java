package com.example.retry.springboot.services;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
public class HelloService {

    private Logger logger = LoggerFactory.getLogger(HelloService.class);

    public HelloService() {
    }

    // 最大試行回数4回
    // 段階的間隔 3秒,6秒,12秒
    @Retryable(
        value = { IOException.class }, 
        maxAttempts = 4, 
        backoff = @Backoff(delay = 3000, multiplier = 2))
    public String sayHello(String name) throws IOException {
        logger.info("sayHello");
        someFunction();
        return String.format("Hello %s !!", name);
    }
    
    @Recover
    public String recover(IOException e, String name) {
        logger.info("recover : " + name);
        return "recovered";
    }

    private static void someFunction() throws IOException {
        double r = Math.random();
        if (r < 0.7) {
            throw new IOException("IO Error");
        }
    }
}