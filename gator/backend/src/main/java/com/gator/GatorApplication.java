package com.gator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GatorApplication {
    public static void main(String[] args) {
        SpringApplication.run(GatorApplication.class, args);
    }
}
