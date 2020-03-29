package com.rastiehaiev.birthday.reminder.integration;

import com.rastiehaiev.birthday.reminder.model.notification.Notification;
import com.sbrati.spring.boot.starter.gcp.pubsub.GcpPubSubPublisher;

public class BirthdayReminderNotificationPublisher extends GcpPubSubPublisher<Notification> {

    public BirthdayReminderNotificationPublisher() {
        super("birthday-reminder-notification");
    }
}
