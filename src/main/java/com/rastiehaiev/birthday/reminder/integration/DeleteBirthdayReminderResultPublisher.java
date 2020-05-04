package com.rastiehaiev.birthday.reminder.integration;

import com.rastiehaiev.birthday.reminder.model.DeleteBirthdayReminderResult;
import com.sbrati.spring.boot.starter.gcp.pubsub.GcpPubSubPublisher;

public class DeleteBirthdayReminderResultPublisher extends GcpPubSubPublisher<DeleteBirthdayReminderResult> {

    public DeleteBirthdayReminderResultPublisher() {
        super("delete-birthday-reminder-result");
    }
}
