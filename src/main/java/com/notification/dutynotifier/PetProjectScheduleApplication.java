package com.notification.dutynotifier;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class PetProjectScheduleApplication {

    public static void main(String[] args) {

//        java.util.TimeZone.setDefault(
//                java.util.TimeZone.getTimeZone(
//                        "Europe/Kyiv"
//                )
//        );

        SpringApplication.run(
                PetProjectScheduleApplication.class,
                args
        );
    }
}
