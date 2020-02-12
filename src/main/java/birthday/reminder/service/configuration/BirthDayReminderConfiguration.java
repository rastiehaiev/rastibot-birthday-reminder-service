package birthday.reminder.service.configuration;

import birthday.reminder.service.schedule.Scheduler;
import birthday.reminder.service.service.BirthDayReminderProcessingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Slf4j
@Configuration
public class BirthDayReminderConfiguration {

    @Bean
    @ConditionalOnProperty(name = "birthday-reminder-service.schedule.enabled", havingValue = "true")
    public Scheduler scheduler(BirthDayReminderProcessingService birthDayReminderProcessingService) {
        Scheduler scheduler = new Scheduler(birthDayReminderProcessingService);
        log.info("Created scheduler for processing reminders.");
        return scheduler;
    }

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }
}
