package com.example.retry;

import java.net.URI;

/**
  * 
  */
public class App {

    /**
     * @param args
     */
    public static void main(String[] args) {
        System.setProperty(org.slf4j.impl.SimpleLogger.SHOW_DATE_TIME_KEY, "true");
        System.setProperty(org.slf4j.impl.SimpleLogger.DATE_TIME_FORMAT_KEY, "yyyy-MM-dd HH:mm:ss:SSS Z"); 
        new RetrySample1().run(URI.create("https://httpbin.org/get"));
        System.out.println("end");
    }
}
