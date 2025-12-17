package com.tourflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TourFlowApplication {

    public static void main(String[] args) {
        SpringApplication.run(TourFlowApplication.class, args);
    }

}
