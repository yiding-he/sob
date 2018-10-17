package com.hyd.sob;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(SobConfiguration.class)
public class SobApplication {

    public static void main(String[] args) {
        SpringApplication.run(SobApplication.class, args);
    }
}
