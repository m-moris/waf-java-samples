package com.example.retry.springboot;

import java.io.IOException;

import com.example.retry.springboot.services.HelloService;
import com.example.retry.springboot.services.HelloServiceWithRecover;
import com.example.retry.springboot.services.HelloServiceWithRetryTemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    private static Logger logger = LoggerFactory.getLogger(HelloController.class);
    
    private HelloService _service;
    private HelloServiceWithRecover _serviceWithRecover;
    private HelloServiceWithRetryTemplate _serviceWithRetryTemplate;

    public HelloController(HelloService service,
        HelloServiceWithRecover serviceWithRecover,
        HelloServiceWithRetryTemplate serviceWithRetryTemplate) {
        _service = service;
        _serviceWithRecover = serviceWithRecover;
        _serviceWithRetryTemplate = serviceWithRetryTemplate;
    }

    @GetMapping("hello1")
    public String hello1(@RequestParam String name) throws IOException {
        logger.info("hello1");
        return _service.sayHello(name);
    }

    @GetMapping("hello2")
    public String hello2(@RequestParam String name) throws IOException {
        logger.info("hello2");
        return _serviceWithRecover.sayHello(name);
    }

    @GetMapping("hello3")
    public String hello3(@RequestParam String name) throws IOException {
        logger.info("hello3");
        return _serviceWithRetryTemplate.sayHello(name);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<String> handleException(IOException exception) {
        return new ResponseEntity<String>("error", HttpStatus.CONFLICT);
    }
}
