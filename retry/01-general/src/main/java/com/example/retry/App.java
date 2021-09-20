package com.example.retry;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {

    private static Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        
        logger.info("main start");
        
        // 成功
        new RetrySample().run(URI.create("https://httpbin.org/status/200"));
        
        // 500 失敗
        new RetrySample().run(URI.create("https://httpbin.org/status/500"));
        
        // 429 失敗
        new RetrySample().run(URI.create("https://httpbin.org/status/429"));
        
        // 強制的にタイムアウトになるように調整
        new RetrySample().run(URI.create("https://httpbin.org/delay/10"));

        logger.info("main end");
    }
}
