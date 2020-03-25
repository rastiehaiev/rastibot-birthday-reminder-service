package com.rastiehaiev.birthday.reminder.integration;

import com.rastiehaiev.birthday.reminder.model.CheckReminderExistsResult;
import com.sbrati.spring.boot.starter.gcp.pubsub.GcpPubSubPublisher;
import org.springframework.stereotype.Component;

@Component
public class CheckReminderExistsResultPublisher extends GcpPubSubPublisher<CheckReminderExistsResult> {

    public CheckReminderExistsResultPublisher() {
        super("check-birthday-reminder-exists-result");
    }
}
