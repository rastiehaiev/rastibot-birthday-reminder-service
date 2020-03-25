package com.rastiehaiev.birthday.reminder.integration;

import com.rastiehaiev.birthday.reminder.model.notification.NotificationActionResult;
import com.sbrati.spring.boot.starter.gcp.pubsub.GcpPubSubPublisher;
import org.springframework.stereotype.Component;

@Component
public class NotificationActionResultPublisher extends GcpPubSubPublisher<NotificationActionResult> {

    public NotificationActionResultPublisher() {
        super("birthday-reminder-notification-action-result");
    }
}
