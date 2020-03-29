package com.rastiehaiev.birthday.reminder.integration;

import com.rastiehaiev.birthday.reminder.model.notification.NotificationActionResult;
import com.sbrati.spring.boot.starter.gcp.pubsub.GcpPubSubPublisher;

public class NotificationActionResultPublisher extends GcpPubSubPublisher<NotificationActionResult> {

    public NotificationActionResultPublisher() {
        super("birthday-reminder-notification-action-result");
    }
}
