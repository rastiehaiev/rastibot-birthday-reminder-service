package com.rastiehaiev.birthday.reminder.configuration;

import com.rastiehaiev.birthday.reminder.properties.RastibotBirthDayReminderServiceScheduleProperties;
import com.rastiehaiev.birthday.reminder.schedule.Scheduler;
import com.rastiehaiev.birthday.reminder.service.BirthDayReminderProcessingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Slf4j
@Configuration
@EnableConfigurationProperties(RastibotBirthDayReminderServiceScheduleProperties.class)
public class RastibotBirthdayReminderServiceConfiguration {

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
