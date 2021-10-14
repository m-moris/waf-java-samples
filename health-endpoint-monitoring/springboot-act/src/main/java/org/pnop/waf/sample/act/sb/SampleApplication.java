package org.pnop.waf.sample.act.sb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(SampleApplication.class, args);
    }

       // https://stackoverflow.com/questions/56117631/how-to-secure-actuator-endpoints-with-role-in-spring-boot-2
}
