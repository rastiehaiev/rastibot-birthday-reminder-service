package birthday.reminder.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class BirthDayReminderApplication {

    public static void main(final String[] args) {
        SpringApplication.run(BirthDayReminderApplication.class, args);
    }
}
