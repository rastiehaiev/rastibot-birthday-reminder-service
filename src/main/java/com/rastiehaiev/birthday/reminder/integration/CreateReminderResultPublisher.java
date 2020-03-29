package com.rastiehaiev.birthday.reminder.integration;

import com.rastiehaiev.birthday.reminder.model.CreateReminderResult;
import com.sbrati.spring.boot.starter.gcp.pubsub.GcpPubSubPublisher;

public class CreateReminderResultPublisher extends GcpPubSubPublisher<CreateReminderResult> {

    public CreateReminderResultPublisher() {
        super("create-birthday-reminder-result");
    }
}
