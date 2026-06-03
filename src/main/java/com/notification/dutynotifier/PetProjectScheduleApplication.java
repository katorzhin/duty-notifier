package com.notification.dutynotifier;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class PetProjectScheduleApplication {

    public static void main(String[] args) {
        SpringApplication.run(
                PetProjectScheduleApplication.class,
                args
        );
    }
}
