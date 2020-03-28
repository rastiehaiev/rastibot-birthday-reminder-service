package com.rastiehaiev.birthday.reminder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class RastibotBirthDayReminderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RastibotBirthDayReminderServiceApplication.class, args);
    }
}
