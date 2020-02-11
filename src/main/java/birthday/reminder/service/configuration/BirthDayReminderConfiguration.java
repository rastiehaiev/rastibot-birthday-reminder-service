package birthday.reminder.service.configuration;

import birthday.reminder.service.schedule.Scheduler;
import birthday.reminder.service.service.BirthDayReminderProcessingService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class BirthDayReminderConfiguration {

    @Bean
    @ConditionalOnProperty("${birthday-reminder-service.schedule.enabled}")
    public Scheduler scheduler(BirthDayReminderProcessingService birthDayReminderProcessingService) {
        return new Scheduler(birthDayReminderProcessingService);
    }

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }
}
