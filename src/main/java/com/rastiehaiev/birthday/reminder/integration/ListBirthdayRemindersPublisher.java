package com.rastiehaiev.birthday.reminder.integration;

import com.rastiehaiev.birthday.reminder.model.ListBirthdayRemindersResult;
import com.sbrati.spring.boot.starter.gcp.pubsub.GcpPubSubPublisher;

public class ListBirthdayRemindersPublisher extends GcpPubSubPublisher<ListBirthdayRemindersResult> {

    public ListBirthdayRemindersPublisher() {
        super("list-birthday-reminders-result");
    }
}
