package com.rastiehaiev.birthday.reminder.integration;

import com.rastiehaiev.birthday.reminder.model.BirthdayReminderData;
import com.rastiehaiev.birthday.reminder.model.ListBirthdayRemindersRequest;
import com.rastiehaiev.birthday.reminder.model.ListBirthdayRemindersResult;
import com.rastiehaiev.birthday.reminder.service.BirthDayReminderService;
import com.rastiehaiev.birthday.reminder.service.EventCreationService;
import com.sbrati.spring.boot.starter.gcp.pubsub.GcpPubSubSubscriber;
import com.sbrati.telegram.domain.Event;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class ListBirthdayRemindersSubscriber extends GcpPubSubSubscriber<ListBirthdayRemindersRequest> {

    private final BirthDayReminderService reminderService;
    private final ListBirthdayRemindersPublisher publisher;
    private final EventCreationService eventCreationService;

    public ListBirthdayRemindersSubscriber(BirthDayReminderService reminderService,
                                           EventCreationService eventCreationService,
                                           ListBirthdayRemindersPublisher publisher) {

        super(ListBirthdayRemindersRequest.class, "list-birthday-reminders");
        this.reminderService = reminderService;
        this.publisher = publisher;
        this.eventCreationService = eventCreationService;
    }

    @Override
    public void process(Event<ListBirthdayRemindersRequest> event) {
        log.info("Processing event {}", event);
        ListBirthdayRemindersRequest request = event.getPayload();
        List<BirthdayReminderData> results = reminderService.findAllOfKind(request.getChatId(), request.getKind());
        log.debug("List birthday reminders result: {}", results);

        Event<ListBirthdayRemindersResult> outEvent = getResultEvent(event, results);
        publisher.publish(outEvent);
    }

    private Event<ListBirthdayRemindersResult> getResultEvent(Event<ListBirthdayRemindersRequest> event, List<BirthdayReminderData> results) {
        ListBirthdayRemindersResult result = new ListBirthdayRemindersResult();
        result.setResults(results);
        return eventCreationService.create(event.getChatId(), result);
    }
}
