package com.rastiehaiev.birthday.reminder.configuration;

import com.rastiehaiev.birthday.reminder.integration.*;
import com.rastiehaiev.birthday.reminder.service.BirthDayReminderService;
import com.rastiehaiev.birthday.reminder.service.EventCreationService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(value = "spring.cloud.gcp.pubsub.enabled", matchIfMissing = true)
public class RastibotBirthdayReminderServiceGcpConfiguration {

    private final BirthDayReminderService reminderService;
    private final EventCreationService eventCreationService;

    @Bean
    public BirthdayReminderNotificationPublisher birthdayReminderNotificationPublisher() {
        return new BirthdayReminderNotificationPublisher();
    }

    @Bean
    public CheckReminderExistsResultPublisher checkReminderExistsResultPublisher() {
        return new CheckReminderExistsResultPublisher();
    }

    @Bean
    public CreateReminderResultPublisher createReminderResultPublisher() {
        return new CreateReminderResultPublisher();
    }

    @Bean
    public NotificationActionResultPublisher notificationActionResultPublisher() {
        return new NotificationActionResultPublisher();
    }

    @Bean
    public CheckReminderExistsSubscriber checkReminderExistsSubscriber() {
        return new CheckReminderExistsSubscriber(reminderService, eventCreationService, checkReminderExistsResultPublisher());
    }

    @Bean
    public CreateReminderSubscriber createReminderSubscriber() {
        return new CreateReminderSubscriber(reminderService, eventCreationService, createReminderResultPublisher());
    }

    @Bean
    public NotificationActionSubscriber notificationActionSubscriber() {
        return new NotificationActionSubscriber(reminderService, eventCreationService, notificationActionResultPublisher());
    }
}
