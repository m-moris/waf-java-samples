package org.pnop.sample.waf.cb.general;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hello world!
 *
 */
public class App {

    private static Logger logger = LoggerFactory.getLogger(CircuitBreaker.class);

    public static void main(String[] args) {
        System.out.println("Hello World!");

        var cb = new CircuitBreaker("test", 10);

        try {
            cb.invoke(() -> {
                logger.info("callback");
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info(cb.toString());

        try {
            cb.invoke(() -> {
                logger.info("callback");
                throw new IOException();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info(cb.toString());
    }
}
